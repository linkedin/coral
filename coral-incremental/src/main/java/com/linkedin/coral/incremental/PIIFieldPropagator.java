/**
 * Copyright 2022-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.core.TableFunctionScan;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.logical.LogicalExchange;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalIntersect;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalMatch;
import org.apache.calcite.rel.logical.LogicalMinus;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalSort;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;


public class PIIFieldPropagator implements RelShuttle {
  private final PIIContext context;

  PIIFieldPropagator(PIIContext context) {
    this.context = context;
  }

  @Override
  public RelNode visit(LogicalProject project) {
    System.out.println("Visiting LogicalProject");
    List<RelNode> inputs = project.getInputs();
    inputs.forEach(input -> input.accept(this));
    System.out.println("Processing LogicalProject");
    propagatePIILineage(project);
    System.out.println("Finished processing LogicalProject");
    System.out.println("Input PII fields: " + context.getInputPIIFields());
    System.out.println("Output PII fields: " + context.getOutputPIIFields());
    return project;
  }

  private void propagatePIILineage(RelNode relNode) {
    if (relNode instanceof LogicalProject) {
      System.out.println("getPIImapping LogicalProject");
      LogicalProject project = (LogicalProject) relNode;
      List<RexNode> projections = project.getProjects();
      System.out.println("Projections: " + projections);
      List<String> outputFields = project.getRowType().getFieldNames();
      System.out.println("Output fields: " + outputFields);
      List<String> inputFields = project.getInput().getRowType().getFieldNames();
      System.out.println("Input fields: " + inputFields);
      for (int i = 0; i < projections.size(); i++) {
        String outputField = outputFields.get(i);
        RexNode rexNode = projections.get(i);
        if (rexNode instanceof RexInputRef) {
          String inputField = inputFields.get(((RexInputRef) rexNode).getIndex());
          String fullyQualifiedField = context.getFieldToFullyQualifiedMap().get(inputField);
          System.out.println("Fully qualified field: " + fullyQualifiedField);
          if (context.getInputPIIFields().contains(fullyQualifiedField.toLowerCase()) && !context.getOutputPIIFields().contains(outputField)) {
            System.out.println("Adding output PII field " + outputField + " for input field " + fullyQualifiedField);
            context.addOutputPIIField(outputField);
          }
        } else if (rexNode instanceof RexFieldAccess) {
          RexFieldAccess fieldAccess = (RexFieldAccess) rexNode;
          String flattenedFieldPath = resolveNestedField(fieldAccess, inputFields);
          String fullyQualifiedField = context.getFieldToFullyQualifiedMap().get(flattenedFieldPath);
          System.out.println("Fully qualified field: " + fullyQualifiedField);
          if (context.getInputPIIFields().contains(fullyQualifiedField.toLowerCase()) && !context.getOutputPIIFields().contains(outputField)) {
            System.out.println("Adding output PII field " + outputField + " for input field " + fullyQualifiedField);
            context.addOutputPIIField(outputField);
          }
        } else if (rexNode instanceof RexCall) {
          RexCall call = (RexCall) rexNode;
          if (call.getOperands().get(0) instanceof RexInputRef) {
            String inputField = inputFields.get(((RexInputRef) call.getOperands().get(0)).getIndex());
            String fullyQualifiedField = context.getFieldToFullyQualifiedMap().get(inputField);
            System.out.println("Fully qualified field: " + fullyQualifiedField);
            if (context.getInputPIIFields().contains(fullyQualifiedField.toLowerCase()) && !context.getOutputPIIFields().contains(outputField)) {
              System.out.println("Adding output PII field " + outputField + " for input field " + fullyQualifiedField);
              context.addOutputPIIField(outputField);
            }
          }
        } else {
          System.out.println("Unhandled RexNode type: " + rexNode.getClass().getName());
        }
      }
    } else {
      System.out.println("getPIImapping for Other " + relNode.getClass().getName());
      relNode.accept(this);
    }
  }


  // Recursive function to resolve nested fields
  private String resolveNestedField(RexFieldAccess fieldAccess, List<String> inputFields) {
    // Step 1: Get the field name of the current access (e.g., 'memberId')
    String fieldName = fieldAccess.getField().getName();
    // Step 2: Get the reference (the parent field, e.g., '$0')
    RexNode referenceNode = fieldAccess.getReferenceExpr();
    // Step 3: If the reference node is a RexInputRef, resolve the parent path recursively
    if (referenceNode instanceof RexInputRef) {
      String parentField = inputFields.get(((RexInputRef) referenceNode).getIndex());
      // If we hit the root, return the full path
      return parentField + "." + fieldName;
    } else if (referenceNode instanceof RexFieldAccess) {
      // If we encounter another RexFieldAccess, recurse to resolve it
      return resolveNestedField((RexFieldAccess) referenceNode, inputFields) + "." + fieldName;
    } else {
      throw new IllegalArgumentException("Unhandled reference node type: " + referenceNode.getClass().getName());
    }
  }

  private String getFullyQualifiedTableName(RelNode relNode) {
    if (relNode instanceof LogicalTableScan) {
      System.out.println("LogicalTableScan");
      LogicalTableScan tableScan = (LogicalTableScan) relNode;
      String[] tableParts = tableScan.getTable().getQualifiedName().toArray(new String[0]);
      System.out.println("Table parts: " + String.join(", ", tableParts));
      if (tableParts.length == 1) {
        return tableParts[0];
      }
      return String.join(".", tableParts[tableParts.length - 2], tableParts[tableParts.length - 1]);
    } else if (relNode instanceof RelSubset) {
      System.out.println("RelSubset");
      return getFullyQualifiedTableName(((RelSubset) relNode).getOriginal());
    } else {
      System.out.println("Unhandled RelNode type: " + relNode.getClass().getSimpleName());
    }
    throw new IllegalArgumentException("Unable to extract fully qualified table name");
  }

  @Override
  public RelNode visit(TableScan tableScan) {
    System.out.println("Visiting TableScan: " + tableScan.getTable());
    System.out.println("getFieldMappings TableScan");
    String tableAlias = getFullyQualifiedTableName(tableScan);
    List<String> flattenedFields = flattenFields(tableScan.getRowType(), "");
    System.out.println("Flattened fields: " + flattenedFields);
    for (String field : flattenedFields) {
      context.addFieldToFullyQualifiedMap(field, tableAlias + "." + field);
    }
    System.out.println("Field mappings: " + context.getFieldToFullyQualifiedMap());
    return tableScan;
  }

  public void visit(LogicalTableScan logicalTableScan) {
    System.out.println("Visiting LogicalTableScan: " + logicalTableScan.getTable());
    System.out.println("getFieldMappings LogicalTableScan");
    String tableAlias = getFullyQualifiedTableName(logicalTableScan);
    List<String> flattenedFields = flattenFields(logicalTableScan.getRowType(), "");
    System.out.println("Flattened fields: " + flattenedFields);
    for (String field : flattenedFields) {
      context.addFieldToFullyQualifiedMap(field, tableAlias + "." + field);
    }
    System.out.println("Field mappings: " + context.getFieldToFullyQualifiedMap());
  }

  @Override
  public RelNode visit(TableFunctionScan scan) {
    return scan;
  }

  @Override
  public RelNode visit(LogicalValues values) {
    return values;
  }

  @Override
  public RelNode visit(LogicalFilter filter) {
    return filter;
  }

  @Override
  public RelNode visit(LogicalJoin join) {
    System.out.println("Visiting LogicalJoin");

    // Check the left input
    RelNode left = join.getLeft();
    if (left instanceof LogicalTableScan) {
      visit((LogicalTableScan) left);
    } else {
      // Optionally handle deeper traversal
      left.accept(this);
    }

    // Check the right input
    RelNode right = join.getRight();
    if (right instanceof LogicalTableScan) {
      visit((LogicalTableScan) right);
    } else {
      // Optionally handle deeper traversal
      right.accept(this);
    }
    return join;
  }

  @Override
  public RelNode visit(LogicalCorrelate correlate) {
    return correlate;
  }

  @Override
  public RelNode visit(LogicalUnion union) {
    return union;
  }

  @Override
  public RelNode visit(LogicalIntersect intersect) {
    return intersect;
  }

  @Override
  public RelNode visit(LogicalMinus minus) {
    return minus;
  }

  @Override
  public RelNode visit(LogicalAggregate aggregate) {
    return aggregate;
  }

  @Override
  public RelNode visit(LogicalMatch match) {
    return match;
  }

  @Override
  public RelNode visit(LogicalSort sort) {
    return sort;
  }

  @Override
  public RelNode visit(LogicalExchange exchange) {
    return exchange;
  }

  @Override
  public RelNode visit(RelNode other) {
    return other;
  }

  private List<String> flattenFields(RelDataType relDataType, String prefix) {
    List<String> flatFields = new ArrayList<>();
    for (RelDataTypeField field : relDataType.getFieldList()) {
      String fieldName = field.getName();
      RelDataType fieldType = field.getType();
      // Build the fully qualified field path
      String fullPath = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;
      if (fieldType.isStruct()) {
        // Recursively flatten nested STRUCT fields
        flatFields.addAll(flattenFields(fieldType, fullPath));
      } else {
        // Add the fully qualified field path
        flatFields.add(fullPath);
      }
    }
    return flatFields;
  }

}

/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nonnull;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.AggregateCall;
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
import org.apache.calcite.rel.logical.LogicalTableFunctionScan;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexDynamicParam;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexLocalRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexOver;
import org.apache.calcite.rex.RexPatternFieldRef;
import org.apache.calcite.rex.RexRangeRef;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.rex.RexSubQuery;
import org.apache.calcite.rex.RexTableInputRef;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.util.Pair;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.rel.HiveUncollect;


/**
 * This class generates an avro schema for calcite IR RelNode
 *
 * It utilizes RelShuttle to traverse the calcite IR RelNode in a bottom-up manner
 * and generate the avro schema layer by layer based on base table avro schema
 *
 * Case Sensitivity:
 * Case Sensitivity is addressed by extracting field name directly from base table avro schema based on index mapping
 * and pass it through AST tree in a bottom-up manner if there is no renaming. If there is renaming, then
 * the user-specified new name is used instead
 *
 * Nullability:
 * 1. If a column is unmodified by Rel operator, it retains nullability property
 * 2. For UDF and SQL operators like arithmetic, logical and unary expressions,
 *    Operator semantics will determine nullable type. By and large, for all operators,
 *    1) If one of the inputs is null then the return value will also be null.
 *    2) If all inputs are non-null, operator will return non null
 *
 * Enum:
 * Enum is handled in a similar way as case sensitivity. Since sql does not have an enum type, enum type can only be
 * preserved through a pass-through logical operator (e.g. LogicalFilter). If any transformation (e.g. UDF) applies
 * to enum field, we use the output type of the transformation to replace enum type.
 *
 * For example
 * LogicalProject(Id_View_Col=[$0], Map_View_Col=[$1], Struct_Count=[$2], EXPR$3=[100])
 *   LogicalAggregate(group=[{0, 1}], Struct_Count=[COUNT()])
 *     LogicalProject(Id_View_Col=[$0], Map_View_Col=[$2], struct_col=[$3])
 *       LogicalFilter(condition=[AND(&gt;($0, 0), IS NOT NULL($2), IS NOT NULL($3))])
 *         LogicalTableScan(table=[[hive, default, basecomplex]])
 *
 *  1) LogicalTableScan reads base table schema from metastore
 *  2) LogicalFilter passes through the output schema from LogicalTableScan
 *  3) LogicalProject(Id_View_Col=[$0], Map_View_Col=[$2], struct_col=[$3]) keeps
 *    [Id_View_Col, Map_View_Col, struct_col] fields from the output schema of LogicalFilter
 *  4) LogicalAggregate keeps [Id_View_Col, Map_View_Col] field and append a new field [Struct_Count]
 *    with type inferred from COUNT() operation
 *  5) LogicalProject(Id_View_Col=[$0], Map_View_Col=[$1], Struct_Count=[$2], EXPR$3=[100]) keeps field
 *     [Id_View_Col, Map_View_Col, Struct_Count] and append a new field EXPR$3 into the avro schema. The output schema
 *     of top level LogicalProject is the Dali view avro schema
 *
 */
public class RelToAvroSchemaConverter {
  private final HiveMetastoreClient hiveMetastoreClient;
  private static final Logger LOG = LoggerFactory.getLogger(RelToAvroSchemaConverter.class);

  public RelToAvroSchemaConverter(HiveMetastoreClient hiveMetastoreClient) {
    this.hiveMetastoreClient = hiveMetastoreClient;
  }

  /**
   * This method generates a corresponding avro schema for calcite IR RelNode
   *
   * @param relNode {@link RelNode} object
   * @param strictMode configure whether to use strict mode
   * @param forceLowercase  configure whether to return view schema in lowercase mode
   * @return avro schema for calcite IR RelNode
   * @throws RuntimeException if cannot find table in Hive metastore
   * @throws RuntimeException if cannot determine avro schema for tableScan
   */
  public Schema convert(@Nonnull RelNode relNode, boolean strictMode, boolean forceLowercase) {
    Preconditions.checkNotNull(relNode, "RelNode to convert cannot be null");

    Map<RelNode, Schema> schemaMap = new HashMap<>();
    relNode.accept(new SchemaRelShuttle(hiveMetastoreClient, schemaMap, strictMode, forceLowercase));
    Schema viewSchema = schemaMap.get(relNode);

    return viewSchema;
  }

  /**
   * This method generates a corresponding avro schema for calcite IR RelNode
   *
   * @param relNode {@link RelNode} object
   * @param strictMode configure whether to use strict mode
   * @return avro schema for calcite IR RelNode
   * @throws RuntimeException if cannot find table in Hive metastore
   * @throws RuntimeException if cannot determine avro schema for tableScan
   */
  public Schema convert(@Nonnull RelNode relNode, boolean strictMode) {
    return convert(relNode, strictMode, false);
  }

  /**
   * This class extends RelShuttleImp. It's used to generate avro schema while traversing
   * the calcite IR RelNode in a bottom-up manner
   *
   * RelShuttle is used to traverse the IR RelNode without modifying it.
   * For each logical operator type, it's implemented in the following manner:
   *
   *     @Override
   *     public RelNode visit(LogicalOperator logicalOperator) {
   *       RelNode relNode = super.visit(logicalOperator);
   *
   *       // begin of building avro schema
   *       code to build avro schema in this level
   *       // end of building avro schema
   *
   *       return relNode;
   *     }
   *
   * relNode returned is the same instance of input logicalOperator. The reason of being implemented in
   * this way is that we want to build avro schema in a bottom-up manner
   *
   */
  private static class SchemaRelShuttle extends RelShuttleImpl {
    private final Map<RelNode, Schema> schemaMap;
    private final boolean strictMode;
    private final boolean forceLowercase;

    private final HiveMetastoreClient hiveMetastoreClient;

    public SchemaRelShuttle(HiveMetastoreClient hiveMetastoreClient, Map<RelNode, Schema> schemaMap, boolean strictMode,
        boolean forceLowercase) {
      this.hiveMetastoreClient = hiveMetastoreClient;
      this.schemaMap = schemaMap;
      this.strictMode = strictMode;
      this.forceLowercase = forceLowercase;
    }

    @Override
    public RelNode visit(TableScan tableScan) {
      RelNode relNode = super.visit(tableScan);
      Schema tableScanSchema = getTableScanSchema(tableScan);
      schemaMap.put(tableScan, tableScanSchema);

      return relNode;
    }

    @Override
    public RelNode visit(TableFunctionScan tableFunctionScan) {
      // TODO: implement this method
      return super.visit(tableFunctionScan);
    }

    @Override
    public RelNode visit(LogicalValues logicalValues) {
      // TODO: implement this method
      return super.visit(logicalValues);
    }

    @Override
    public RelNode visit(LogicalFilter logicalFilter) {
      RelNode relNode = super.visit(logicalFilter);
      Schema inputSchema = schemaMap.get(logicalFilter.getInput());
      schemaMap.put(logicalFilter, inputSchema);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalProject logicalProject) {
      RelNode relNode = super.visit(logicalProject);
      Schema inputSchema = schemaMap.get(logicalProject.getInput());

      Queue<String> suggestedFieldNames = new LinkedList<>();
      for (RelDataTypeField field : logicalProject.getRowType().getFieldList()) {
        suggestedFieldNames.offer(field.getName());
      }

      SchemaBuilder.FieldAssembler<Schema> logicalProjectFieldAssembler =
          SchemaBuilder.record(inputSchema.getName()).namespace(inputSchema.getNamespace()).fields();
      logicalProject.accept(new SchemaRexShuttle(inputSchema, logicalProject.getInput(), suggestedFieldNames,
          logicalProjectFieldAssembler));

      schemaMap.put(logicalProject, logicalProjectFieldAssembler.endRecord());

      return relNode;
    }

    @Override
    public RelNode visit(LogicalJoin logicalJoin) {
      // TODO: Modify this method to avoid that 2 schema fields share the same name in Avro 1.10, and enable the tests for join
      RelNode relNode = super.visit(logicalJoin);

      Schema leftInputSchema = schemaMap.get(logicalJoin.getLeft());
      Schema rightInputSchema = schemaMap.get(logicalJoin.getRight());

      List<Schema.Field> leftInputSchemaFields = leftInputSchema.getFields();
      List<Schema.Field> rightInputSchemaFields = rightInputSchema.getFields();

      SchemaBuilder.FieldAssembler<Schema> logicalJoinFieldAssembler =
          SchemaBuilder.record(leftInputSchema.getName()).namespace(leftInputSchema.getNamespace()).fields();

      for (Schema.Field leftInputSchemaField : leftInputSchemaFields) {
        SchemaUtilities.appendField(leftInputSchemaField, logicalJoinFieldAssembler);
      }
      for (Schema.Field rightInputSchemaField : rightInputSchemaFields) {
        SchemaUtilities.appendField(rightInputSchemaField, logicalJoinFieldAssembler);
      }

      schemaMap.put(logicalJoin, logicalJoinFieldAssembler.endRecord());

      return relNode;
    }

    @Override
    public RelNode visit(LogicalCorrelate logicalCorrelate) {
      RelNode relNode = super.visit(logicalCorrelate);

      Schema leftSchema = schemaMap.get(logicalCorrelate.getLeft());
      Schema rightSchema = schemaMap.get(logicalCorrelate.getRight());
      Schema logicalCorrelateSchema = SchemaUtilities.joinSchemas(leftSchema, rightSchema);
      schemaMap.put(logicalCorrelate, logicalCorrelateSchema);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalUnion logicalUnion) {
      RelNode relNode = super.visit(logicalUnion);
      Schema inputSchema1 = schemaMap.get(logicalUnion.getInput(0));
      Schema inputSchema2 = schemaMap.get(logicalUnion.getInput(1));

      Schema mergedSchema =
          SchemaUtilities.mergeUnionRecordSchema(inputSchema1, inputSchema2, strictMode, forceLowercase);

      schemaMap.put(logicalUnion, mergedSchema);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalIntersect logicalIntersect) {
      // TODO: implement this method
      return super.visit(logicalIntersect);
    }

    @Override
    public RelNode visit(LogicalMinus logicalMinus) {
      // TODO: implement this method
      return super.visit(logicalMinus);
    }

    @Override
    public RelNode visit(LogicalAggregate logicalAggregate) {
      // TODO: Potential need RexShuttle
      RelNode relNode = super.visit(logicalAggregate);
      Schema inputSchema = schemaMap.get(logicalAggregate.getInput());
      SchemaBuilder.FieldAssembler<Schema> logicalAggregateFieldAssembler =
          SchemaBuilder.record(inputSchema.getName()).namespace(inputSchema.getNamespace()).fields();

      List<Schema.Field> inputSchemaFields = inputSchema.getFields();
      for (int i = 0; i < inputSchemaFields.size(); i++) {
        if (logicalAggregate.getGroupSet().get(i)) {
          SchemaUtilities.appendField(inputSchemaFields.get(i), logicalAggregateFieldAssembler);
        }
      }

      // Handle aggCalls
      for (Pair<AggregateCall, String> aggCall : logicalAggregate.getNamedAggCalls()) {
        String fieldName = SchemaUtilities.toAvroQualifiedName(aggCall.right);
        RelDataType fieldType = aggCall.left.getType();
        SchemaUtilities.appendField(fieldName, fieldType,
            SchemaUtilities.generateDocumentationForAggregate(aggCall.left), logicalAggregateFieldAssembler, true);
      }

      schemaMap.put(logicalAggregate, logicalAggregateFieldAssembler.endRecord());

      return relNode;
    }

    @Override
    public RelNode visit(LogicalMatch logicalMatch) {
      // TODO: implement this method
      return super.visit(logicalMatch);
    }

    @Override
    public RelNode visit(LogicalSort logicalSort) {
      // TODO: implement this method
      return super.visit(logicalSort);
    }

    @Override
    public RelNode visit(LogicalExchange logicalExchange) {
      // TODO: implement this method
      return super.visit(logicalExchange);
    }

    @Override
    public RelNode visit(RelNode relNode) {
      // Handles lateral views here
      if (relNode instanceof HiveUncollect || relNode instanceof LogicalTableFunctionScan) {
        SchemaBuilder.FieldAssembler<Schema> hiveUncollectFieldAssembler =
            SchemaBuilder.record("LateralViews").namespace("LateralViews").fields();

        for (RelDataTypeField field : relNode.getRowType().getFieldList()) {
          SchemaUtilities.appendField(field.getName(), field.getType(), null, hiveUncollectFieldAssembler, true);
        }

        schemaMap.put(relNode, hiveUncollectFieldAssembler.endRecord());

        return relNode;
      } else {
        return super.visit(relNode);
      }
    }

    /**
     * This method retrieves avro schema for tableScan
     *
     * @param tableScan
     * @return avro schema for tableScan. The schema includes partition columns if table is partitioned
     * @throws RuntimeException if cannot find table in Hive metastore
     * @throws RuntimeException if cannot determine avro schema for tableScan
     */
    private Schema getTableScanSchema(TableScan tableScan) {
      List<String> qualifiedName = tableScan.getTable().getQualifiedName();
      String dbName = qualifiedName.get(1);
      String tableName = qualifiedName.get(2);
      Table baseTable = hiveMetastoreClient.getTable(dbName, tableName);
      if (baseTable == null) {
        throw new RuntimeException("Cannot find table " + dbName + "." + tableName + " in Hive metastore");
      }

      Schema tableSchema = SchemaUtilities.getAvroSchemaForTable(baseTable, strictMode);

      return tableSchema;
    }
  }

  /**
   * This class extends RexShuttle. It's used to generate avro schema while traversing RexNode.
   */
  private static class SchemaRexShuttle extends RexShuttle {
    private final Schema inputSchema;
    private final Queue<String> suggestedFieldNames;
    private final SchemaBuilder.FieldAssembler<Schema> fieldAssembler;
    private RelNode inputNode;

    public SchemaRexShuttle(Schema inputSchema, Queue<String> suggestedFieldNames,
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
      this.inputSchema = inputSchema;
      this.suggestedFieldNames = suggestedFieldNames;
      this.fieldAssembler = fieldAssembler;
    }

    public SchemaRexShuttle(Schema inputSchema, RelNode inputNode, Queue<String> suggestedFieldNames,
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
      this(inputSchema, suggestedFieldNames, fieldAssembler);
      this.inputNode = inputNode;
    }

    @Override
    public RexNode visitInputRef(RexInputRef rexInputRef) {
      RexNode rexNode = super.visitInputRef(rexInputRef);

      Schema.Field field = inputSchema.getFields().get(rexInputRef.getIndex());
      String oldFieldName = field.name();
      String suggestNewFieldName = suggestedFieldNames.poll();
      String newFieldName = SchemaUtilities.getFieldName(oldFieldName, suggestNewFieldName);

      SchemaUtilities.appendField(newFieldName, field, fieldAssembler);

      return rexNode;
    }

    @Override
    public RexNode visitLocalRef(RexLocalRef rexLocalRef) {
      // TODO: implement this method
      return super.visitLocalRef(rexLocalRef);
    }

    /*
     * TODO: Populate doc for queries with literal.
     */
    @Override
    public RexNode visitLiteral(RexLiteral rexLiteral) {
      RexNode rexNode = super.visitLiteral(rexLiteral);
      RelDataType fieldType = rexLiteral.getType();
      appendField(fieldType, true, SchemaUtilities.generateDocumentationForLiteral(rexLiteral));

      return rexNode;
    }

    @Override
    public RexNode visitCall(RexCall rexCall) {
      /**
       * For SqlUserDefinedFunction and SqlOperator RexCall, no need to handle it recursively
       * and only return type of udf or sql operator is relevant
       */
      RelDataType fieldType = rexCall.getType();
      boolean isNullable = SchemaUtilities.isFieldNullable(rexCall, inputSchema);

      appendField(fieldType, isNullable,
          SchemaUtilities.generateDocumentationForFunctionCall(rexCall, inputSchema, inputNode));

      return rexCall;
    }

    @Override
    public RexNode visitOver(RexOver rexOver) {
      // TODO: implement this method
      return super.visitOver(rexOver);
    }

    @Override
    public RexNode visitCorrelVariable(RexCorrelVariable rexCorrelVariable) {
      // TODO: implement this method
      return super.visitCorrelVariable(rexCorrelVariable);
    }

    @Override
    public RexNode visitDynamicParam(RexDynamicParam rexDynamicParam) {
      // TODO: implement this method
      return super.visitDynamicParam(rexDynamicParam);
    }

    @Override
    public RexNode visitRangeRef(RexRangeRef rexRangeRef) {
      // TODO: implement this method
      return super.visitRangeRef(rexRangeRef);
    }

    @Override
    public RexNode visitFieldAccess(RexFieldAccess rexFieldAccess) {
      RexNode referenceExpr = rexFieldAccess.getReferenceExpr();

      if (referenceExpr instanceof RexCall
          && ((RexCall) referenceExpr).getOperator() instanceof SqlUserDefinedFunction) {
        String oldFieldName = rexFieldAccess.getField().getName();
        String suggestNewFieldName = suggestedFieldNames.poll();
        String newFieldName = SchemaUtilities.getFieldName(oldFieldName, suggestNewFieldName);

        RelDataType fieldType = rexFieldAccess.getType();
        boolean isNullable = SchemaUtilities.isFieldNullable((RexCall) referenceExpr, inputSchema);
        // TODO: add field documentation
        SchemaUtilities.appendField(newFieldName, fieldType, null, fieldAssembler, isNullable);
      } else {
        Deque<String> innerRecordNames = new LinkedList<>();
        while (!(referenceExpr instanceof RexInputRef)) {
          if (referenceExpr instanceof RexCall
              && ((RexCall) referenceExpr).getOperator().getName().equalsIgnoreCase("ITEM")) {
            // While selecting `int_field` from `array_col:array<struct<int_field:int>>` using `array_col[x].int_field`,
            // `rexFieldAccess` is like `ITEM($1, 1).int_field`, we need to set `referenceExpr` to be the first operand (`$1`) of `ITEM` function
            referenceExpr = ((RexCall) referenceExpr).getOperands().get(0);
          } else if (referenceExpr instanceof RexFieldAccess) {
            // While selecting `int_field` from `struct_col:struct<inner_struct_col:struct<int_field:int>>` using `struct_col.inner_struct_col.int_field`,
            // `rexFieldAccess` is like `$3.inner_struct_col.int_field`, we need to set `referenceExpr` to be the expr (`$3`) of itself.
            // Besides, we need to store the field name (`inner_struct_col`) in `fieldNames` so that we can retrieve the correct inner struct from `topSchema` afterwards
            innerRecordNames.push(((RexFieldAccess) referenceExpr).getField().getName());
            referenceExpr = ((RexFieldAccess) referenceExpr).getReferenceExpr();
          } else {
            return super.visitFieldAccess(rexFieldAccess);
          }
        }

        String oldFieldName = rexFieldAccess.getField().getName();
        String suggestNewFieldName = suggestedFieldNames.poll();
        String newFieldName = SchemaUtilities.getFieldName(oldFieldName, suggestNewFieldName);
        Schema topSchema = inputSchema.getFields().get(((RexInputRef) referenceExpr).getIndex()).schema();

        Schema.Field accessedField = getFieldFromTopSchema(topSchema, oldFieldName, innerRecordNames);
        assert accessedField != null;
        SchemaUtilities.appendField(newFieldName, accessedField, fieldAssembler);
      }

      return rexFieldAccess;
    }

    @Override
    public RexNode visitSubQuery(RexSubQuery rexSubQuery) {
      // TODO: implement this method
      return super.visitSubQuery(rexSubQuery);
    }

    @Override
    public RexNode visitTableInputRef(RexTableInputRef rexTableInputRef) {
      // TODO: implement this method
      return super.visitTableInputRef(rexTableInputRef);
    }

    @Override
    public RexNode visitPatternFieldRef(RexPatternFieldRef rexPatternFieldRef) {
      // TODO: implement this method
      return super.visitPatternFieldRef(rexPatternFieldRef);
    }

    private void appendField(RelDataType fieldType, boolean isNullable, String doc) {
      String fieldName = SchemaUtilities.getFieldName("", suggestedFieldNames.poll());
      SchemaUtilities.appendField(fieldName, fieldType, doc, fieldAssembler, isNullable);
    }

    /**
     * Get the field named `fieldName` in the given `topSchema` which might be nested
     * @param innerRecordNames contains inner record names, if `topSchema` contains inner record, we need to retrieve the inner record which
     *                         is the ancestor of field named `fieldName`
     */
    private Schema.Field getFieldFromTopSchema(@Nonnull Schema topSchema, @Nonnull String fieldName,
        @Nonnull Deque<String> innerRecordNames) {
      topSchema = SchemaUtilities.extractIfOption(topSchema);

      while (topSchema.getType() != Schema.Type.RECORD
          || topSchema.getFields().stream().noneMatch(f -> f.name().equalsIgnoreCase(fieldName))) {
        switch (topSchema.getType()) {
          case MAP:
            topSchema = topSchema.getValueType();
            break;
          case ARRAY:
            topSchema = topSchema.getElementType();
            break;
          case RECORD:
            final String innerRecordName = innerRecordNames.pop();
            for (Schema.Field field : topSchema.getFields()) {
              if (field.name().equalsIgnoreCase(innerRecordName)) {
                topSchema = field.schema();
                break;
              }
            }
            break;
          default:
            throw new IllegalArgumentException("Unsupported topSchema type: " + topSchema.getType());
        }
        topSchema = SchemaUtilities.extractIfOption(topSchema);
      }

      Schema.Field targetField = null;
      for (Schema.Field field : topSchema.getFields()) {
        if (field.name().equalsIgnoreCase(fieldName)) {
          targetField = field;
          break;
        }
      }
      return targetField;
    }
  }
}

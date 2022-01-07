/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.common.functions.GenericProjectFunction;


/**
 * Fuzzy union occur when there is a mismatch in the schemas of the branches of a union. This can occur in a Dali view
 * that is composed of the union of two tables. When a view is initially deployed, the schemas must be exactly the
 * same. However, one of the tables could evolve to introduce a new field in a struct, which leads to a mismatch in the
 * schemas when taking the union of the tables in the view.
 *
 * This shuttle rewrites a SqlNode AST so that every branch of a union operator is projected as a given schema
 * (expectedDataType), which is the subset of columns in the all the branches of that union.
 * The schema of a struct is fixed using the GenericProject UDF.
 * The introduction of the GenericProject only occurs when the branch of the union has the different structure or
 * ordering of columns from the given schema (expectedDataType).
 *
 * If the SqlNode AST does not have union operators, the initial SqlNode AST will be returned.
 * If the SqlNode AST has a union operator, and there exists a semantically incorrect branch, an error will be thrown.
 *
 * For example:
 *
 * Given schemas:
 * a - (f:int, f1:struct(f2:int))
 * b - (f:int, f1:struct(f2:int))
 * Given view as query:
 *   SELECT *
 *   FROM a
 *   UNION ALL
 *   SELECT *
 *   FROM b
 *
 * This view will be initially deployed successfully because both branches of the union are the same.
 * Let's name the view, 'view_c', and assume it was deployed in a database, 'database_d'.
 *
 * Suppose the schema of b is changed such that it is now:
 * b - (f:int, f1:struct(f2:int, f3:int))
 * There is now a mismatch in the schemas between the two branches of the view leading to a fuzzy union.
 * A query for database_d.view_c will fail.
 *
 * To resolve the mismatch in schemas, we use the GenericProject UDF to project view_c's original schema
 * of b.f1 given by (f:int, f1:struct(f2:int)) over the column.
 * The rewritten query will look something like:
 *   SELECT *
 *   FROM a
 *   UNION ALL
 *   SELECT view_c.f, generic_project(view_c.f1, 'view_c.f1') as f1
 *   FROM (
 *     SELECT * from b
 *   ) as view_c
 *
 * The expected schema will be inserted as the return type of the generic_project SQL function.
 * The first parameter will be a reference to the column to be fixed.
 * The second parameter will be a string literal containing the name of the column.
 *
 * It is up to the coral modules for a specific engine (spark/trino) to resolve the schema of b.f1.
 */
public class FuzzyUnionSqlRewriter extends SqlShuttle {

  private final String tableName;
  private final ToRelConverter toRelConverter;

  public FuzzyUnionSqlRewriter(@Nonnull String tableName, @Nonnull ToRelConverter toRelConverter) {
    this.toRelConverter = toRelConverter;
    this.tableName = tableName;
  }

  @Override
  public SqlNode visit(SqlCall call) {
    if (call.getOperator().getKind() == SqlKind.UNION) {
      // Since a union is represented as a binary operator in calcite, unions would have chaining unions in the AST.
      // For example: given A UNION B UNION C, the SqlNode AST would look something like:
      //      U
      //     / \
      //    A   U
      //       / \
      //      B   C
      // We need to compare the schemas of all leaves of the union (A,B,C) and adjust if necessary.
      // expectedDataType is the column subset of the union (A, B, C)
      final RelDataType expectedDataType = getUnionDataType(call);
      addFuzzyUnionToUnionCall(call, expectedDataType);
    }
    return super.visit(call);
  }

  /**
   * Create a SqlNode that calls GenericProject for the given column
   * @param columnName The name of the column that is to be fixed
   * @param expectedType The expected data type
   */
  private SqlNode createGenericProject(String columnName, RelDataType expectedType) {
    SqlNode[] genericProjectOperands = new SqlNode[2];
    genericProjectOperands[0] = new SqlIdentifier(ImmutableList.of(tableName, columnName), SqlParserPos.ZERO);
    genericProjectOperands[1] = SqlLiteral.createCharString(columnName, SqlParserPos.ZERO);
    SqlBasicCall genericProjectCall =
        new SqlBasicCall(new GenericProjectFunction(expectedType), genericProjectOperands, SqlParserPos.ZERO);
    SqlNode[] castAsColumnOperands = new SqlNode[2];
    castAsColumnOperands[0] = genericProjectCall;
    castAsColumnOperands[1] = new SqlIdentifier(columnName, SqlParserPos.ZERO);
    SqlBasicCall castAsCall = new SqlBasicCall(new SqlAsOperator(), castAsColumnOperands, SqlParserPos.ZERO);
    return castAsCall;
  }

  /**
   * Create the SqlNodeList for the operands for a GenericProject.
   * @param fromNodeDataType RelDataType that might need to be projected as expectedDataType
   * @param expectedDataType The expected data type
   * @return a SqlNodeList that contains all the fields in expectedDataType where:
   *         - fields that do not involve structs are identity fields
   *         - fields that involve structs:
   *           - if fromNodeDataType.structField = expectedDataType.structField
   *             - identity projection
   *           - else
   *             - use a generic projection over the struct column fixing the schema to be expectedDataType.structField
   */
  private SqlNodeList createProjectedFieldsNodeList(RelDataType fromNodeDataType, RelDataType expectedDataType) {
    final SqlNodeList projectedFields = new SqlNodeList(SqlParserPos.ZERO);

    for (final RelDataTypeField expectedField : expectedDataType.getFieldList()) {
      final RelDataTypeField inputField = fromNodeDataType.getField(expectedField.getName(), false, false);
      SqlNode projectedField = new SqlIdentifier(ImmutableList.of(tableName, inputField.getName()), SqlParserPos.ZERO);
      if (!equivalentDataTypes(expectedField.getType(), inputField.getType())) {
        projectedField = createGenericProject(inputField.getName(), expectedField.getType());
      }
      projectedFields.add(projectedField);
    }

    return projectedFields;
  }

  /**
   * Create a SqlNode that has a same schema as expectedDataType
   * @param unionBranch SqlNode node that is a branch in a union, which might need to be projected as expectedDataType
   * @param expectedDataType The expected data type for the UNION branch
   * @return SqlNode that has its schema fixed to the schema of the table
   */
  private SqlNode addFuzzyUnionToUnionBranch(SqlNode unionBranch, RelDataType expectedDataType) {
    // Get the base tables' (rather than intermediate base views') RelDataType of unionBranch, so that it can pass the type
    // validation of calcite because calcite uses base tables' RelDataType to do the type validation for union.
    RelDataType fromNodeDataType = toRelConverter.getSqlToRelConverter()
        .convertQuery(unionBranch.accept(new FuzzyUnionSqlRewriter(tableName, toRelConverter)), true, true).rel
            .getRowType();

    // Create a SqlNode that has a string equivalent to the following query:
    // SELECT table_name.col1, generic_project(table_name.col2), ... FROM (unionBranch) as table_name
    SqlNodeList projectedFields = createProjectedFieldsNodeList(fromNodeDataType, expectedDataType);
    SqlNode[] castTableOperands = { unionBranch, new SqlIdentifier(tableName, SqlParserPos.ZERO) };
    SqlBasicCall castTableCall = new SqlBasicCall(new SqlAsOperator(), castTableOperands, SqlParserPos.ZERO);
    SqlSelect selectOperator = new SqlSelect(SqlParserPos.ZERO, new SqlNodeList(SqlParserPos.ZERO), projectedFields,
        castTableCall, null, null, null, null, null, null, null);

    return selectOperator;
  }

  /**
   * Look at the union operator in the call and add a fuzzy union projection over branches that have mismatched
   * schemas.
   * @param unionCall Union operator SqlCall
   * @return a union operator SqlCall with fuzzy union semantics
   */
  private SqlCall addFuzzyUnionToUnionCall(SqlCall unionCall, RelDataType expectedDataType) {
    for (int i = 0; i < unionCall.operandCount(); ++i) {
      SqlNode operand = unionCall.operand(i);
      // In this case, we do not need to apply fuzzy union semantics to the union itself.
      // The fuzzy union semantics only need to be applied to the children of ths union.
      if (!isUnionOperator(operand)) {
        unionCall.setOperand(i, addFuzzyUnionToUnionBranch(operand, expectedDataType));
      } else {
        unionCall.setOperand(i, addFuzzyUnionToUnionCall((SqlCall) operand, expectedDataType));
      }
    }
    return unionCall;
  }

  /**
   * Return data type given by the common subset of columns in the branches of the UNION call.
   */
  private RelDataType getUnionDataType(final SqlCall unionCall) {
    final List<SqlNode> leafNodes = getUnionLeafNodes(unionCall);
    final List<RelDataType> leafNodeDataTypes = new ArrayList<>();
    for (final SqlNode node : leafNodes) {
      RelDataType fromNodeDataType = toRelConverter.getSqlValidator().getValidatedNodeTypeIfKnown(node);
      if (fromNodeDataType == null) {
        toRelConverter.getSqlValidator().validate(node.accept(new FuzzyUnionSqlRewriter(tableName, toRelConverter)));
        fromNodeDataType = toRelConverter.getSqlValidator().getValidatedNodeType(node);
      }
      leafNodeDataTypes.add(fromNodeDataType);
    }

    return getUnionDataType(leafNodeDataTypes);
  }

  /**
   * Return data type given by the common subset of columns in the given dataTypes.
   */
  private RelDataType getUnionDataType(final List<RelDataType> dataTypes) {
    // Use the first dataType as the model/base type.
    // Assume that all dataTypes in the list once shared a common type
    // and only evolved in a backwards compatible fashion.
    final RelDataType baseDataType = dataTypes.get(0);

    if (!(baseDataType.isStruct()) && !(baseDataType.getSqlTypeName() == SqlTypeName.ARRAY)
        && !(baseDataType.getSqlTypeName() == SqlTypeName.MAP)) {
      return toRelConverter.getRelBuilder().getTypeFactory().createTypeWithNullability(baseDataType, true);
    }

    RelDataType expectedCommonType = null;

    if (baseDataType.isStruct()) {
      // Build the common UNION type using the first branch that appears in the query
      final RelDataTypeFactory.Builder builder =
          new RelDataTypeFactory.Builder(toRelConverter.getRelBuilder().getTypeFactory());

      // Build a set of common fields by name in the given dataTypes
      Set<String> commonFieldNames =
          baseDataType.getFieldNames().stream().map(String::toLowerCase).collect(Collectors.toSet());
      for (int i = 1; i < dataTypes.size(); ++i) {
        final Set<String> branchFieldNames =
            dataTypes.get(i).getFieldNames().stream().map(String::toLowerCase).collect(Collectors.toSet());
        commonFieldNames = Sets.intersection(commonFieldNames, branchFieldNames);
      }

      // Build a new struct with the common fields using the baseDataType's struct ordering
      for (final RelDataTypeField field : baseDataType.getFieldList()) {
        if (!commonFieldNames.contains(field.getName().toLowerCase())) {
          continue;
        }

        final List<RelDataType> fieldTypes = dataTypes.stream()
            .map(type -> type.getField(field.getName(), false, false).getType()).collect(Collectors.toList());

        // Recursively derive the common types for all fields of the struct
        final RelDataType columnType = getUnionDataType(fieldTypes);

        builder.add(field.getName(),
            toRelConverter.getRelBuilder().getTypeFactory().createTypeWithNullability(columnType, true));
      }

      expectedCommonType =
          toRelConverter.getRelBuilder().getTypeFactory().createTypeWithNullability(builder.build(), true);
    }

    if (baseDataType.getSqlTypeName() == SqlTypeName.ARRAY) {
      // Recursively derive the common type of the component in the array
      final List<RelDataType> nestedArrayTypes =
          dataTypes.stream().map(RelDataType::getComponentType).collect(Collectors.toList());
      final RelDataType expectedArrayType = getUnionDataType(nestedArrayTypes);
      expectedCommonType = toRelConverter.getRelBuilder().getTypeFactory().createArrayType(expectedArrayType, -1);
    }

    if (baseDataType.getSqlTypeName() == SqlTypeName.MAP) {
      // Recursively derive the common type of the value type in the map
      final List<RelDataType> nestedValueTypes =
          dataTypes.stream().map(RelDataType::getValueType).collect(Collectors.toList());
      final RelDataType expectedValueType = getUnionDataType(nestedValueTypes);
      final RelDataType expectedKeyType =
          toRelConverter.getRelBuilder().getTypeFactory().createTypeWithNullability(baseDataType.getKeyType(), true);
      expectedCommonType =
          toRelConverter.getRelBuilder().getTypeFactory().createMapType(expectedKeyType, expectedValueType);
    }

    return expectedCommonType;
  }

  /**
   * Return a list of SqlNode branches in the UNION call ordered based on the query string.
   * Example:
   *     'A UNION B UNION C' => [SqlNode_A, SqlNode_B, SqlNode_C]
   */
  private List<SqlNode> getUnionLeafNodes(SqlCall unionCall) {
    final List<SqlNode> leafNodes = new ArrayList<>();
    for (int i = 0; i < unionCall.operandCount(); ++i) {
      final SqlNode operand = unionCall.operand(i);
      if (isUnionOperator(operand)) {
        leafNodes.addAll(getUnionLeafNodes((SqlCall) operand));
      } else {
        leafNodes.add(operand);
      }
    }
    return leafNodes;
  }

  /**
   * Determine if the SqlNode is a UNION call
   * @param node a given SqlNode to evaluate
   * @return true if the SqlNode is a UNION call; false otherwise
   */
  private boolean isUnionOperator(SqlNode node) {
    return (node instanceof SqlCall) && ((SqlCall) node).getOperator().getKind() == SqlKind.UNION;
  }

  /**
   * Return true if the RelDataTypes t1 and t2 are equivalent.
   *
   * NOTE:
   * We don't actually check the types of columns.
   * We just want to check that the data types have the same structure and ordering of columns.
   */
  private boolean equivalentDataTypes(RelDataType t1, RelDataType t2) {
    if (t1.isStruct()) {
      if (t1.getFieldCount() != t2.getFieldCount()) {
        return false;
      }

      for (int i = 0; i < t1.getFieldCount(); ++i) {
        final RelDataTypeField f1 = t1.getFieldList().get(i);
        final RelDataTypeField f2 = t2.getFieldList().get(i);

        if (!f1.getName().equalsIgnoreCase(f2.getName()) || !equivalentDataTypes(f1.getType(), f2.getType())) {
          return false;
        }
      }
    }

    if (t1.getSqlTypeName() == SqlTypeName.ARRAY
        && !equivalentDataTypes(t1.getComponentType(), t2.getComponentType())) {
      return false;
    }

    if (t1.getSqlTypeName() == SqlTypeName.MAP && !equivalentDataTypes(t1.getValueType(), t2.getValueType())) {
      return false;
    }

    return true;
  }
}

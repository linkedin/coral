/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlAsOperator;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.hive.hive2rel.functions.GenericProjectFunction;


/**
 * Fuzzy union occur when there is a mismatch in the schemas of the branches of a union. This can occur in a Dali view
 * that is composed of the union of two tables. When a view is initially deployed, the schemas must be exactly the
 * same. However, one of the tables could evolve to introduce a new field in a struct, which leads to a mismatch in the
 * schemas when taking the union of the tables in the view.
 *
 * This shuttle rewrites a SqlNode AST so that every branch of a union operator project exactly a given table schema.
 * The schema of a struct is fixed using the GenericProject UDF.
 * The introduction of the GenericProject only occurs when the branch of the union contains a superset of the table
 * schema fields and is not strictly equivalent.
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
 * It is up to the coral modules for a specific engine (spark/presto) to resolve the schema of b.f1.
 */
class FuzzyUnionSqlRewriter extends SqlShuttle {

  private final String tableName;
  private final RelDataType tableDataType;
  private final RelContextProvider relContextProvider;
  private final List<String> columnNames;

  public FuzzyUnionSqlRewriter(@Nonnull Table table, @Nonnull String tableName,
      @Nonnull RelContextProvider relContextProvider) {
    this.relContextProvider = relContextProvider;
    this.tableName = tableName;
    this.tableDataType = table.getRowType(relContextProvider.getHiveSqlValidator().getTypeFactory());
    this.columnNames = tableDataType.getFieldNames();
  }

  @Override
  public SqlNode visit(SqlCall call) {
    if (call.getOperator().getKind() == SqlKind.UNION) {
      call = addFuzzyUnionToUnionCall(call);
    }
    ArgHandler<SqlNode> argHandler = new CallCopyingArgHandler(call, false);
    call.getOperator().acceptCall(this, call, false, argHandler);
    return argHandler.result();
  }

  /**
   * Create a SqlNode that calls GenericProject for the given column
   * @param columnName The name of the column that is to be fixed
   */
  private SqlNode createGenericProject(String columnName) {
    SqlNode[] genericProjectOperands = new SqlNode[2];
    genericProjectOperands[0] = new SqlIdentifier(ImmutableList.of(tableName, columnName), SqlParserPos.ZERO);
    genericProjectOperands[1] = SqlLiteral.createCharString(columnName, SqlParserPos.ZERO);
    RelDataTypeField columnField = tableDataType.getField(columnName, false, true);
    SqlBasicCall genericProjectCall =
        new SqlBasicCall(new GenericProjectFunction(columnField.getType()), genericProjectOperands, SqlParserPos.ZERO);
    SqlNode[] castAsColumnOperands = new SqlNode[2];
    castAsColumnOperands[0] = genericProjectCall;
    castAsColumnOperands[1] = new SqlIdentifier(columnName, SqlParserPos.ZERO);
    SqlBasicCall castAsCall = new SqlBasicCall(new SqlAsOperator(), castAsColumnOperands, SqlParserPos.ZERO);
    return castAsCall;
  }

  /**
   * Create the SqlNodeList for the operands for a GenericProject.
   * @param fromNodeDataType RelDataType that contains a superset of the fields in tableDataType and is not strictly
   *                         equal to tableDataType.
   * @return a SqlNodeList that contains all the fields in tableDataType where:
   *         - fields that do not involve structs are identity fields
   *         - fields that involve structs:
   *           - if fromNodeDataType.structField = tableDataType.structField
   *             - identity projection
   *           - else
   *             - use a generic projection over the struct column fixing the schema to be tableDataType.structField
   */
  private SqlNodeList createProjectedFieldsNodeList(RelDataType fromNodeDataType) {
    SqlNodeList projectedFields = new SqlNodeList(SqlParserPos.ZERO);
    for (RelDataTypeField field : tableDataType.getFieldList()) {
      SqlNode projectedField;
      RelDataTypeField schemaDataTypeField = tableDataType.getField(field.getName(), false, false);
      RelDataTypeField inputDataTypeField = fromNodeDataType.getField(field.getName(), false, false);
      if (field.getType().getFullTypeString().contains("RecordType")
          && !schemaDataTypeField.equals(inputDataTypeField)) {
        projectedField = createGenericProject(field.getName());
      } else {
        projectedField = new SqlIdentifier(ImmutableList.of(tableName, field.getName()), SqlParserPos.ZERO);
      }
      projectedFields.add(projectedField);
    }

    return projectedFields;
  }

  /**
   * Create a SqlNode that has that has a schema fixed to the provided table if and only if the SqlNode has
   * a RelDataType that is a superset of the fields that exist in the tableDataType and is not strictly the equivalent
   * to tableDataType.
   * @param unionBranch SqlNode node that is a branch in a union
   * @return SqlNode that has its schema fixed to the schema of the table
   */
  private SqlNode addFuzzyUnionToUnionBranch(SqlNode unionBranch) {

    // Retrieve the datatype of the node if known.
    // If it is known, retrieve it from the node.
    // Otherwise, return the passed in SqlNode.
    // The SqlShuttle will derive the view graph bottom up (from base tables to views above it).
    // In this case, the SqlValidator will always fix the schemas from tables to the view and will not fail.
    RelDataType fromNodeDataType = relContextProvider.getHiveSqlValidator().getValidatedNodeTypeIfKnown(unionBranch);
    if (fromNodeDataType == null) {
      relContextProvider.getHiveSqlValidator().validate(unionBranch);
      fromNodeDataType = relContextProvider.getHiveSqlValidator().getValidatedNodeType(unionBranch);
    }
    // tableDataType is always a view RelDataType. View RelDataTypes are inferred from Hive's storage
    // descriptor. Unlike view RelDataTypes, base table RelDataTypes are inferred from Hive SerDe
    // library corresponding to the table. See {@link com.linkedin.coral.hive.hive2rel.HiveTable.getRowType}
    // for more details. This results in the view schema (tableDataType) being always lower-cased, and the
    // (potentially) base table schema (fromNodeDataType) being properly cased. Case difference
    // between view schema and underlying union branch schema should not warrant a fuzzy union,
    // and hence we ignore cases when comparing fromNodeDataType and tableDataType.
    if (tableDataType.getFullTypeString().equalsIgnoreCase(fromNodeDataType.getFullTypeString())
        || !fromNodeDataType.isStruct() || fromNodeDataType.getFieldCount() < tableDataType.getFieldCount()) {
      return unionBranch;
    }

    // The table schema will be projected over a branch if and only if the branch contains a superset of the
    // fields in the provided table schema and does not have the same schema as the table schema.
    Set<String> fromNodeFieldNames =
        fromNodeDataType.getFieldList().stream().map(f -> f.getName()).collect(Collectors.toSet());

    if (!fromNodeFieldNames.containsAll(columnNames)) {
      return unionBranch;
    }

    // Create a SqlNode that has a string equivalent to the following query:
    // SELECT table_name.col1, generic_project(table_name.col2), ... FROM (unionBranch) as table_name
    SqlNodeList projectedFields = createProjectedFieldsNodeList(fromNodeDataType);
    SqlNode[] castTableOperands = { unionBranch, new SqlIdentifier(tableName, SqlParserPos.ZERO) };
    SqlBasicCall castTableCall = new SqlBasicCall(new SqlAsOperator(), castTableOperands, SqlParserPos.ZERO);
    SqlSelect selectOperator = new SqlSelect(SqlParserPos.ZERO, new SqlNodeList(SqlParserPos.ZERO), projectedFields,
        castTableCall, null, null, null, null, null, null, null);

    return selectOperator;
  }

  /**
   * Looks at the union operator in the call and adds a fuzzy union projection over branches that have mismatched
   * schemas.
   * @param unionCall Union operator SqlCall
   * @return a Union operator SqlCall with fuzzy union semantics
   */
  private SqlCall addFuzzyUnionToUnionCall(SqlCall unionCall) {
    for (int i = 0; i < unionCall.operandCount(); ++i) {
      SqlNode operand = unionCall.operand(i);
      // Since a union is represented as a binary operator in calcite, chaining unions would have an AST like:
      // Given A UNION B UNION C, the SqlNode AST would look something like:
      //      U
      //     / \
      //    A   U
      //       / \
      //      B   C
      // From the root node, we can see that one of the operands is another union.
      // In this case, we do not need to apply fuzzy union semantics to the union itself.
      // The fuzzy union semantics only need to be applied to the children of ths union.
      if (!isUnionOperator(operand)) {
        unionCall.setOperand(i, addFuzzyUnionToUnionBranch(operand));
      }
    }
    return unionCall;
  }

  /**
   * Determines if the SqlNode is a UNION call
   * @param node a given SqlNode to evaluate
   * @return true if the SqlNode is a UNION call; false otherwise
   */
  private boolean isUnionOperator(SqlNode node) {
    return (node instanceof SqlCall) && ((SqlCall) node).getOperator().getKind() == SqlKind.UNION;
  }
}

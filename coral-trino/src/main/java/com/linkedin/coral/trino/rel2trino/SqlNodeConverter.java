/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * SqlNodeConverter transforms the sqlNodes
 * in the input SqlNode representation to be compatible with Trino engine.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlNode.
 *
 * NOTE: This is a temporary class which hosts certain transformations which were previously done in RelToTrinoConverter.
 * This class will be refactored once standardized CoralIR is integrated in the CoralRelNode to trino SQL translation path.
 */
public class SqlNodeConverter extends SqlShuttle {
  public SqlNodeConverter() {
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    SqlCall transformedSqlCall = getTransformedSqlCall(call);
    return super.visit(transformedSqlCall);
  }

  private static SqlCall getTransformedSqlCall(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case EQUALS:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case NOT_EQUALS:
        return castOperandsToVarchar(sqlCall);
      case SELECT:
        return getTransformedSqlSelectSqlCall(sqlCall);
      default:
        return sqlCall;
    }
  }

  /**
   * Coral IR generates a SqlNode representation where operands of a relational operator may not be compatible.
   * This transformation appends TRY_CAST operator to both operands and casts each operand's data type to VARCHAR to ensure operand inter-compatibility.
   * @param sqlCall sqlCall input SqlCall
   * @return transformed SqlCall
   */
  private static SqlCall castOperandsToVarchar(SqlCall sqlCall) {
    List<SqlNode> updatedOperands = new ArrayList<>();

    final SqlTypeNameSpec varcharTypeNameSpec = new SqlBasicTypeNameSpec(SqlTypeName.VARCHAR, ZERO);
    SqlDataTypeSpec varcharSqlDataTypeSpec = new SqlDataTypeSpec(varcharTypeNameSpec, ZERO);

    for (SqlNode operand : sqlCall.getOperandList()) {
      SqlNode newOperand = TrinoTryCastFunction.INSTANCE.createCall(POS,
          new ArrayList<>(Arrays.asList(operand, varcharSqlDataTypeSpec)));
      updatedOperands.add(newOperand);
    }

    return sqlCall.getOperator().createCall(POS, updatedOperands);
  }

  /**
   * Input SqlCall to this method will access nested fields as is, such as SELECT a.b, a.c.
   * The below transformation appends an AS operator on nested fields and updates the SqlCall to: SELECT a.b AS b, a.c AS c
   * @param sqlCall input SqlCall
   * @return transformed SqlCall
   */
  private static SqlCall getTransformedSqlSelectSqlCall(SqlCall sqlCall) {
    if (((SqlSelect) sqlCall).getSelectList() != null && ((SqlSelect) sqlCall).getSelectList().size() != 0) {
      final List<SqlNode> modifiedSelectList = new ArrayList<>();

      for (SqlNode selectNode : ((SqlSelect) sqlCall).getSelectList().getList()) {
        final String name = SqlValidatorUtil.getAlias(selectNode, -1);
        final boolean nestedFieldAccess =
            selectNode instanceof SqlIdentifier && ((SqlIdentifier) selectNode).names.size() > 1;

        // Always add "AS" when accessing nested fields.
        // In parent class "AS" clause is skipped for "SELECT a.b AS b". Here we will keep the "a.b AS b"
        if (nestedFieldAccess) {
          selectNode = SqlStdOperatorTable.AS.createCall(POS, selectNode, new SqlIdentifier(name, POS));
        }
        modifiedSelectList.add(selectNode);
      }
      ((SqlSelect) sqlCall).setSelectList(new SqlNodeList(modifiedSelectList, POS));
    }
    return sqlCall;
  }
}

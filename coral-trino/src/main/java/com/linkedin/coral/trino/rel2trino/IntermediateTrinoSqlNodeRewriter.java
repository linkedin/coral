/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * IntermediateTrinoSqlNodeRewriter rewrites the TrinoSqlNode representation. It transforms the sqlCalls
 * in the TrinoSqlNode to be compatible with Trino engine in a backward compatible manner.
 *
 * This is achieved by visiting the TrinoSqlNode AST in a pre-order traversal manner and
 * transforming each SqlNode (SqlCall), wherever required.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlCall.
 *
 * NOTE: This is a temporary class which hosts certain transformations which were previously done in RelToTrinoConverter.
 * This class will be refactored once standardized CoralIR is integrated in the CoralRelNode to trino SQL translation path.
 */
public class IntermediateTrinoSqlNodeRewriter extends SqlShuttle {
  public IntermediateTrinoSqlNodeRewriter() {
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    SqlCall transformedSqlCall = getTransformedSqlCall(call);
    return super.visit(transformedSqlCall);
  }

  public static SqlCall getTransformedSqlCall(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case AS:
        return getTransformedAsSqlCall(sqlCall);
      case EQUALS:
        return getTransformedEqualsOperatorSqlCall(sqlCall);
      case SELECT:
        return getTransformedSqlSelectSqlCall(sqlCall);
      default:
        return sqlCall;
    }
  }

  private static SqlCall getTransformedAsSqlCall(SqlCall sqlCall) {
    // This transformation aims to remove redundant CAST operator on NULL values
    if (sqlCall.operandCount() == 2 && (sqlCall.operand(0) instanceof SqlBasicCall)
        && isNullToNullCastPresent(sqlCall.operand(0))) {
      SqlLiteral nullLiteral = SqlLiteral.createNull(ZERO);
      List<SqlNode> newAsSqlCallOperands = new ArrayList<>();
      newAsSqlCallOperands.add(nullLiteral);
      newAsSqlCallOperands.add(sqlCall.operand(1));
      return SqlStdOperatorTable.AS.createCall(ZERO, newAsSqlCallOperands);
    }
    return sqlCall;
  }

  // Append TryCast operator to both operands to cast each operand's data type to VARCHAR
  private static SqlCall getTransformedEqualsOperatorSqlCall(SqlCall sqlCall) {
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
   * The below transformations appends an AS operator on nested fields and updated the SqlCall to: SELECT a.b AS b, a.c AS c
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

        // always add "AS" when accessing nested fields.
        // In parent class "as" is skipped for "select a.b as b", here we will keep the "a.b as b"
        if (nestedFieldAccess) {
          selectNode = SqlStdOperatorTable.AS.createCall(POS, selectNode, new SqlIdentifier(name, POS));
        } else if (selectNode instanceof SqlBasicCall && isNullToNullCastPresent((SqlBasicCall) selectNode)) {
          // Simplify CAST(NULL AS NULL) type selectNode to NULL
          selectNode = SqlLiteral.createNull(POS);
        }
        modifiedSelectList.add(selectNode);
      }
      ((SqlSelect) sqlCall).setSelectList(new SqlNodeList(modifiedSelectList, POS));
    }
    return sqlCall;
  }

  private static boolean isNullToNullCastPresent(SqlBasicCall sqlBasicCall) {
    if (sqlBasicCall.getOperator().getName().equalsIgnoreCase("CAST") && sqlBasicCall.operandCount() == 2) {
      final SqlNode left = sqlBasicCall.operand(0);
      final SqlNode right = sqlBasicCall.operand(1);
      return SqlUtil.isNullLiteral(left, false) && right.toString().equalsIgnoreCase("NULL");
    }
    return false;
  }

}

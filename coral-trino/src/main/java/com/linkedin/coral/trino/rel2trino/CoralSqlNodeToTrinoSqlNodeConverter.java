/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCharStringLiteral;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.trino.rel2trino.functions.TrinoArrayTransformFunction;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * CoralSqlNodeToTrinoSqlNodeConverter rewrites the Coral SqlNode AST. It replaces Coral IR SqlCalls
 * with Trino compatible SqlCalls to subsequently obtain a Trino compatible SqlNode AST representation.
 * This will enable generating a SQL which can be accurately interpreted by the Trino engine.
 *
 * This is achieved by visiting the Coral SqlNode AST in a pre-order traversal manner and
 * transforming each SqlNode (SqlCall), wherever required.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlCall.
 */
public class CoralSqlNodeToTrinoSqlNodeConverter extends SqlShuttle {

  public CoralSqlNodeToTrinoSqlNodeConverter() {
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    SqlCall transformedSqlCall = getTransformedSqlCall(call);
    return super.visit(transformedSqlCall);
  }

  public static SqlCall getTransformedSqlCall(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case SELECT:
        return getTransformedSqlSelectSqlCall(sqlCall);
      case JOIN:
        return getTransformedJoinSqlCall(sqlCall);
      case AS:
        return getTransformedAsSqlCall(sqlCall);
      case UNNEST:
        return getTransformedUnnestSqlCall(sqlCall);
      case EQUALS:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case NOT_EQUALS:
        return castOperandsToVarchar(sqlCall);
      default:
        return sqlCall;
    }
  }

  // Append TryCast operator to both operands to cast each operand's data type to VARCHAR
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

  // Update unnest operand for trino engine to expand the unnest operand to a single column
  private static SqlCall getTransformedUnnestSqlCall(SqlCall sqlCall) {
    if (!(sqlCall.getOperator() instanceof CoralSqlUnnestOperator)) {
      return sqlCall;
    }

    CoralSqlUnnestOperator operator = (CoralSqlUnnestOperator) sqlCall.getOperator();
    SqlNode unnestOperand = sqlCall.operand(0);

    // Transform UNNEST(fieldName) to UNNEST(TRANSFORM(fieldName, x -> ROW(x)))
    if (operator.getRelDataType() != null) {
      String fieldName = "empty";

      if (unnestOperand instanceof SqlIdentifier) {
        SqlIdentifier operand = (SqlIdentifier) unnestOperand;
        fieldName = operand.toSqlString(TrinoSqlDialect.INSTANCE).getSql();
      } else if (unnestOperand instanceof SqlCall
          && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("if")) {
        // for trino outer unnest, unnest has an inner SqlCall with "if" operator
        fieldName = unnestOperand.toSqlString(TrinoSqlDialect.INSTANCE).getSql();
      }
      SqlCharStringLiteral transformArgsLiteral =
          SqlLiteral.createCharString(String.format("%s, x -> ROW(x)", fieldName), POS);

      // Generate expected recordType required for transformatioin
      RelDataType recordType = operator.getRelDataType();
      RelRecordType transformDataType =
          new RelRecordType(ImmutableList.of(new RelDataTypeFieldImpl("wrapper_field", 0, recordType)));

      unnestOperand = new TrinoArrayTransformFunction(transformDataType).createCall(POS, transformArgsLiteral);
    }

    return operator.createCall(POS, new ArrayList<>(Collections.singletonList(unnestOperand)).toArray(new SqlNode[0]));
  }

  private static SqlCall getTransformedSqlSelectSqlCall(SqlCall sqlCall) {
    if (((SqlSelect) sqlCall).getSelectList() != null && ((SqlSelect) sqlCall).getSelectList().size() != 0) {
      final List<SqlNode> modifiedSelectList = new ArrayList<>();

      for (SqlNode selectNode : ((SqlSelect) sqlCall).getSelectList().getList()) {
        final String name = SqlValidatorUtil.getAlias(selectNode, -1);
        final boolean nestedFieldAccess =
            selectNode instanceof SqlIdentifier && ((SqlIdentifier) selectNode).names.size() > 1;

        // always add "AS" when accessing nested fields.
        if (nestedFieldAccess) {
          selectNode = SqlStdOperatorTable.AS.createCall(POS, selectNode, new SqlIdentifier(name, POS));
        }
        modifiedSelectList.add(selectNode);
      }
      ((SqlSelect) sqlCall).setSelectList(new SqlNodeList(modifiedSelectList, POS));
    }
    return sqlCall;
  }

  private static SqlCall getTransformedJoinSqlCall(SqlCall sqlCall) {
    SqlJoin joinSqlCall = (SqlJoin) sqlCall;

    if (joinSqlCall.getJoinType() != JoinType.COMMA) {
      return sqlCall;
    }

    /**
     * check if there's an unnest SqlCall present in the nested SqlNodes:
     * false -> substitute COMMA JOIN with CROSS JOIN
     * true -> check if unnest operand is an inline independent array (not referring to columns in the SQL)
     *                  true -> return
     *                  false -> substitute COMMA JOIN with CROSS JOIN
     */
    if (isUnnestOperatorPresentInChildNode(joinSqlCall.getRight())) {
      if (shouldSwapForCrossJoin(joinSqlCall.getRight())) {
        return createCrossJoinSqlCall(joinSqlCall);
      } else {
        return sqlCall;
      }
    } else {
      return createCrossJoinSqlCall(joinSqlCall);
    }
  }

  private static SqlCall getTransformedAsSqlCall(SqlCall sqlCall) {
    if (sqlCall.operandCount() <= 2 || !(sqlCall.operand(0) instanceof SqlBasicCall)
        || !(sqlCall.operand(0) instanceof SqlBasicCall && sqlCall.operand(0).getKind() == SqlKind.LATERAL)) {
      return sqlCall;
    }

    List<SqlNode> oldAliasOperands = sqlCall.getOperandList();
    List<SqlNode> newAliasOperands = new ArrayList<>();
    SqlCall lateralSqlCall = sqlCall.operand(0);

    // Drop the LATERAL operator when a lateralSqlCall's child operator is UNNEST
    SqlCall newAliasFirstOperand =
        lateralSqlCall.operand(0).getKind() == SqlKind.UNNEST ? lateralSqlCall.operand(0) : lateralSqlCall;

    newAliasOperands.add(newAliasFirstOperand);
    newAliasOperands.addAll(oldAliasOperands.subList(1, oldAliasOperands.size()));

    return SqlStdOperatorTable.AS.createCall(ZERO, newAliasOperands);
  }

  private static boolean isUnnestOperatorPresentInChildNode(SqlNode sqlNode) {
    if (sqlNode instanceof SqlCall && sqlNode.getKind() == SqlKind.AS
        && ((SqlCall) sqlNode).operand(0) instanceof SqlCall
        && ((SqlCall) sqlNode).operand(0).getKind() == SqlKind.LATERAL
        && ((SqlCall) ((SqlCall) sqlNode).operand(0)).operand(0) instanceof SqlCall
        && ((SqlCall) ((SqlCall) sqlNode).operand(0)).operand(0).getKind() == SqlKind.UNNEST) {
      return true;
    }
    return false;
  }

  private static boolean shouldSwapForCrossJoin(SqlNode sqlNode) {
    SqlNode aliasOperand = ((SqlCall) sqlNode).operand(0); // LATERAL unnest(x)
    SqlNode lateralOperand = ((SqlCall) aliasOperand).operand(0); //  unnest(x)
    SqlNode unnestOperand = ((SqlCall) lateralOperand).operand(0);

    // Field to unnest can be:
    // (1) a SqlIdentifier referring to a column, ex: table1.col1
    // (2) a SqlCall with "if" operator for outer unnest
    // (3) a SqlSelect SqlCall
    // For the above scenarios, return true
    if (unnestOperand.getKind() == SqlKind.IDENTIFIER
        || (unnestOperand instanceof SqlCall
            && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("if"))
        || (lateralOperand.getKind() == SqlKind.SELECT)) { // should go to cross join
      return true;
    }
    // If the unnest operand is an inline defined array, return false
    return false;
  }

  private static SqlCall createCrossJoinSqlCall(SqlCall sqlCall) {
    return new SqlJoin(POS, ((SqlJoin) sqlCall).getLeft(), SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
        JoinType.CROSS.symbol(POS), ((SqlJoin) sqlCall).getRight(), JoinConditionType.NONE.symbol(SqlParserPos.ZERO),
        null);
  }
}

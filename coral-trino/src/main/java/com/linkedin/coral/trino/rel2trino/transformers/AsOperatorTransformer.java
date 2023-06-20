/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.sql.parser.SqlParserPos.*;


public class AsOperatorTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    if (sqlCall.getOperator().getKind() != SqlKind.AS || sqlCall.operandCount() <= 2
        || !(sqlCall.operand(0) instanceof SqlBasicCall)
        || !(sqlCall.operand(0) instanceof SqlBasicCall && sqlCall.operand(0).getKind() == SqlKind.LATERAL)) {
      return false;
    }
    return true;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
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
}

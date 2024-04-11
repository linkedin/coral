/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.com.google.common.collect.ImmutableSet;
import com.linkedin.coral.common.calcite.CalciteUtil;
import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;


/**
 * This class transforms the substr indexing in the input SqlCall to be compatible with Trino engine.
 * Trino uses 1-based indexing for substr, so the lowest possible index is 1. While other engines like Hive
 * allow for 0 as a valid index.
 *
 * This transformer guarantees that starting index will always 1 or greater.
 */
public class SubstrIndexTransformer extends SqlCallTransformer {
  private final static Set<String> SUBSTRING_OPERATORS = ImmutableSet.of("substr", "substring");
  @Override
  protected boolean condition(SqlCall sqlCall) {
    return SUBSTRING_OPERATORS.contains(sqlCall.getOperator().getName().toLowerCase());
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final List<SqlNode> operandList = sqlCall.getOperandList();
    SqlNode start = operandList.get(1);
    if (start instanceof SqlNumericLiteral) {
      int startInt = ((SqlNumericLiteral) operandList.get(1)).getValueAs(Integer.class);

      if (startInt == 0) {
        SqlNumericLiteral newStart = SqlNumericLiteral.createExactNumeric(String.valueOf(1), POS);
        sqlCall.setOperand(1, newStart);
      }

    } else if (start instanceof SqlIdentifier) {
      // If we don't have a literal start index value, we need to use a case statement with the column identifier to ensure the value is always 1 or greater
      // So instead of just "col_name" as the start index, we have "CASE WHEN col_name = 0 THEN 1 ELSE col_name END"
      List<SqlNode> whenClauses = Arrays
          .asList(SqlStdOperatorTable.EQUALS.createCall(POS, start, SqlNumericLiteral.createExactNumeric("0", POS)));
      List<SqlNode> thenClauses = Arrays.asList(SqlNumericLiteral.createExactNumeric("1", POS));

      sqlCall.setOperand(1, CASE.createCall(null, POS, null, CalciteUtil.createSqlNodeList(whenClauses, POS),
          CalciteUtil.createSqlNodeList(thenClauses, POS), start));
    }

    return sqlCall;
  }
}

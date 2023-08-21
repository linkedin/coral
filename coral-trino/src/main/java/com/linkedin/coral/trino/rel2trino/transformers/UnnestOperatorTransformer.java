/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCharStringLiteral;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.TrinoSqlDialect;
import com.linkedin.coral.trino.rel2trino.functions.TrinoArrayTransformFunction;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;


/**
 * This class implements the transformation of SqlCalls with UNNEST operator to their
 * corresponding Trino-compatible versions.
 *
 * When expanding an array of type struct, Coral IR returns a row set of a single column. This transformer
 * wraps the unnest operand with an additional ROW to enable the equivalent operation in Trino.
 *
 * For example:
 *  Given table:
 *      t1(id INTEGER, arr array&lt;struct&lt;sa: int, sb: string&gt;&gt; )
 *  and a Coral IR SqlCall:
 *      UNNEST(arr)
 *
 *  The transformed SqlCall would be:
 *      UNNEST(TRANSFORM(arr, x -&gt; ROW(x)))
 */
public class UnnestOperatorTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator() instanceof CoralSqlUnnestOperator;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    CoralSqlUnnestOperator operator = (CoralSqlUnnestOperator) sqlCall.getOperator();
    SqlNode unnestOperand = sqlCall.operand(0);

    // Transform UNNEST(fieldName) to UNNEST(TRANSFORM(fieldName, x -> ROW(x)))
    if (operator.getRelDataType() != null) {
      String fieldName = unnestOperand.toSqlString(TrinoSqlDialect.INSTANCE).getSql();

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

      // The crucial part in above transformation is call to TRANSFORM with lambda which adds extra layer of
      // ROW wrapping.
      // Generate expected recordType required for transformation
      RelDataType recordType = operator.getRelDataType();
      RelRecordType transformDataType =
          new RelRecordType(ImmutableList.of(new RelDataTypeFieldImpl("wrapper_field", 0, recordType)));

      // wrap unnested field to recordType by calling TRANSFORM with lambda which adds an extra layer of ROW wrapping
      // and generates: transform(field, x -> ROW(x))
      unnestOperand = new TrinoArrayTransformFunction(transformDataType).createCall(POS, transformArgsLiteral);
    }

    return operator.createCall(POS, new ArrayList<>(Collections.singletonList(unnestOperand)).toArray(new SqlNode[0]));
  }
}

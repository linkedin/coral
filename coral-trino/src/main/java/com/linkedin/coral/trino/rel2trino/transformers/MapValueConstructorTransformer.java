/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlMapValueConstructor;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SqlCallTransformer;


/**
 * This class implements the transformation from the {@link SqlMapValueConstructor} operation
 * to separate {@link SqlStdOperatorTable#ARRAY_VALUE_CONSTRUCTOR} operations for keys and values
 * in order to be compatible with Trino.
 *
 * For example, "MAP['key1', 'value1', 'key2', 'value2']" is transformed into
 * "MAP(ARRAY['key1', 'key2'], ARRAY['value1', 'value2'])" for Trino compatibility.
 */
public class MapValueConstructorTransformer extends SqlCallTransformer {

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator() instanceof SqlMapValueConstructor;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final List<SqlNode> sourceOperands = sqlCall.getOperandList();
    final List<SqlNode> updatedOperands = new ArrayList<>();
    // Even numbers are keys
    final List<SqlNode> keys = new ArrayList<>();
    for (int i = 0; i < sourceOperands.size(); i += 2) {
      keys.add(sourceOperands.get(i));
    }
    updatedOperands.add(SqlStdOperatorTable.ARRAY_VALUE_CONSTRUCTOR.createCall(SqlParserPos.ZERO, keys));
    // Odd numbers are values
    final List<SqlNode> values = new ArrayList<>();
    for (int i = 1; i < sourceOperands.size(); i += 2) {
      values.add(sourceOperands.get(i));
    }
    updatedOperands.add(SqlStdOperatorTable.ARRAY_VALUE_CONSTRUCTOR.createCall(SqlParserPos.ZERO, values));
    return sqlCall.getOperator().createCall(SqlParserPos.ZERO, updatedOperands);
  }
}

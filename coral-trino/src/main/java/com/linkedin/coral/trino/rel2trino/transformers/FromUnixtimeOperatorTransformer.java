/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.sql.type.ReturnTypes.*;
import static org.apache.calcite.sql.type.SqlTypeName.*;


/**
 * This transformer operates on SqlCalls with 'FROM_UNIXTIME(x)' Coral IR function
 * and transforms it to trino engine compatible function - FORMAT_DATETIME(FROM_UNIXTIME(x)).
 * For Example:
 * A SqlCall of the form: "FROM_UNIXTIME(10000)" is transformed to
 * "FORMAT_DATETIME(FROM_UNIXTIME(10000), 'yyyy-MM-dd HH:mm:ss')"
 */
public class FromUnixtimeOperatorTransformer extends SqlCallTransformer {

  private static final String FORMAT_DATETIME = "format_datetime";
  private static final String FROM_UNIXTIME = "from_unixtime";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(FROM_UNIXTIME);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlOperator formatDatetimeOperator = createSqlOperator(FORMAT_DATETIME, FunctionReturnTypes.STRING);
    SqlOperator fromUnixtimeOperator = createSqlOperator(FROM_UNIXTIME, explicit(TIMESTAMP));

    List<SqlNode> operands = sqlCall.getOperandList();
    if (operands.size() == 1) {
      return formatDatetimeOperator.createCall(SqlParserPos.ZERO,
          fromUnixtimeOperator.createCall(SqlParserPos.ZERO, operands.get(0)),
          SqlLiteral.createCharString("yyyy-MM-dd HH:mm:ss", SqlParserPos.ZERO));
    } else if (operands.size() == 2) {
      return formatDatetimeOperator.createCall(SqlParserPos.ZERO,
          fromUnixtimeOperator.createCall(SqlParserPos.ZERO, operands.get(0)), operands.get(1));
    }
    return sqlCall;
  }
}

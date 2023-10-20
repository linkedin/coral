/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.sql.type.ReturnTypes.explicit;
import static org.apache.calcite.sql.type.SqlTypeName.TIMESTAMP;


/**
 * SQL call transformer having the sole purpose of avoiding unintentional transformation
 * through {@link FromUnixtimeOperatorTransformer} of Trino's `from_unixtime` SQL calls.
 */
public class TimestampFromUnixtimeTransformer extends SqlCallTransformer {

  private static final String FROM_UNIXTIME = "from_unixtime";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return "timestamp_from_unixtime".equalsIgnoreCase(sqlCall.getOperator().getName());
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlOperator fromUnixtimeOperator = createSqlOperator(FROM_UNIXTIME, explicit(TIMESTAMP));
    return fromUnixtimeOperator.createCall(SqlParserPos.ZERO, sqlCall.getOperandList());
  }
}

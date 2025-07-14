/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import com.linkedin.relocated.org.apache.calcite.sql.SqlBasicTypeNameSpec;
import com.linkedin.relocated.org.apache.calcite.sql.SqlCall;
import com.linkedin.relocated.org.apache.calcite.sql.SqlDataTypeSpec;
import com.linkedin.relocated.org.apache.calcite.sql.fun.SqlStdOperatorTable;
import com.linkedin.relocated.org.apache.calcite.sql.parser.SqlParserPos;
import com.linkedin.relocated.org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.transformers.SqlCallTransformer;


/**
 * This class implements the transformation for the CURRENT_TIMESTAMP function.
 * For example, "SELECT CURRENT_TIMESTAMP" is transformed into "SELECT CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3))".
 * This transformation ensures compatibility with Trino.
 */
public class CurrentTimestampTransformer extends SqlCallTransformer {

  private static final String CURRENT_TIMESTAMP_FUNCTION_NAME = "CURRENT_TIMESTAMP";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(CURRENT_TIMESTAMP_FUNCTION_NAME);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlDataTypeSpec timestampType =
        new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.TIMESTAMP, 3, SqlParserPos.ZERO), SqlParserPos.ZERO);
    return SqlStdOperatorTable.CAST.createCall(SqlParserPos.ZERO, sqlCall, timestampType);
  }
}

/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.transformers.SqlCallTransformer;


/**
 * This class implements the transformation for the CURRENT_TIMESTAMP function.
 * For example, "SELECT CURRENT_TIMESTAMP" is transformed into "SELECT CAST(CURRENT_TIMESTAMP AT TIME ZONE 'UTC' AS TIMESTAMP(3))".
 * This transformation ensures compatibility with Trino due to the following reasons:
 * (1) In Hive, CURRENT_TIMESTAMP returns a UTC timestamp, whereas in Trino, it returns a timestamp based on the session's timezone.
 * (2) In Hive, the return type of CURRENT_TIMESTAMP is timestamp, whereas in Trino, the return type is timestamp with time zone,
 * so we need to add an explicit CAST.
 */
public class CurrentTimestampTransformer extends SqlCallTransformer {

  private static final String CURRENT_TIMESTAMP_FUNCTION_NAME = "CURRENT_TIMESTAMP";
  private static final String CURRENT_TIMESTAMP_AT_TIMEZONE_UTC = "CURRENT_TIMESTAMP AT TIME ZONE 'UTC'";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(CURRENT_TIMESTAMP_FUNCTION_NAME);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlDataTypeSpec timestampType =
        new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.TIMESTAMP, 3, SqlParserPos.ZERO), SqlParserPos.ZERO);
    final SqlOperator currentTimestampOperator = sqlCall.getOperator();
    final SqlFunction currentTimestampAtTimeZoneUTCOperator =
        new SqlFunction(CURRENT_TIMESTAMP_AT_TIMEZONE_UTC, currentTimestampOperator.kind,
            currentTimestampOperator.getReturnTypeInference(), currentTimestampOperator.getOperandTypeInference(),
            currentTimestampOperator.getOperandTypeChecker(), SqlFunctionCategory.TIMEDATE) {
          @Override
          public SqlSyntax getSyntax() {
            return SqlSyntax.FUNCTION_ID;
          }
        };
    return SqlStdOperatorTable.CAST.createCall(SqlParserPos.ZERO,
        currentTimestampAtTimeZoneUTCOperator.createCall(SqlParserPos.ZERO, sqlCall.getOperandList()), timestampType);
  }
}

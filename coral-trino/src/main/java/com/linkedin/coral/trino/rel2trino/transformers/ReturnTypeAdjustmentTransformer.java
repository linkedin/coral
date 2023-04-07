/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


/**
 * This transformer casts the result of some Trino functions to the same return type as in Hive.
 *
 * Example:
 *  "DATEDIFF" function in Hive returns int type, but the corresponding function "DATE_DIFF" in Trino
 *  returns bigint type. To ensure compatibility, a "CAST" is added to convert the result to the int type.
 */
public class ReturnTypeAdjustmentTransformer extends SqlCallTransformer {

  private static final Map<String, SqlTypeName> OPERATORS_TO_ADJUST = ImmutableMap.<String, SqlTypeName> builder()
      .put("date_diff", SqlTypeName.INTEGER).put("cardinality", SqlTypeName.INTEGER).put("ceil", SqlTypeName.BIGINT)
      .put("ceiling", SqlTypeName.BIGINT).put("floor", SqlTypeName.BIGINT).put("date_add", SqlTypeName.VARCHAR).build();
  private final Map<String, Boolean> configs;

  public ReturnTypeAdjustmentTransformer(Map<String, Boolean> configs) {
    this.configs = configs;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    String lowercaseOperatorName = sqlCall.getOperator().getName().toLowerCase(Locale.ROOT);
    if ("date_add".equals(lowercaseOperatorName) && !configs.getOrDefault(CAST_DATEADD_TO_STRING, false)) {
      return false;
    }
    return OPERATORS_TO_ADJUST.containsKey(lowercaseOperatorName);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    String lowercaseOperatorName = sqlCall.getOperator().getName().toLowerCase(Locale.ROOT);
    SqlTypeName targetType = OPERATORS_TO_ADJUST.get(lowercaseOperatorName);
    if (targetType != null) {
      return createCast(sqlCall, targetType);
    }
    return sqlCall;
  }

  private SqlCall createCast(SqlNode node, SqlTypeName typeName) {
    SqlDataTypeSpec targetTypeNode =
        new SqlDataTypeSpec(new SqlBasicTypeNameSpec(typeName, SqlParserPos.ZERO), SqlParserPos.ZERO);
    return SqlStdOperatorTable.CAST.createCall(SqlParserPos.ZERO, node, targetTypeNode);
  }
}

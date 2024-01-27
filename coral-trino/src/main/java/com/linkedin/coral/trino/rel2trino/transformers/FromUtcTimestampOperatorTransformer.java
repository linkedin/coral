/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.BasicSqlType;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.HiveTypeSystem;
import com.linkedin.coral.common.calcite.CalciteUtil;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;

import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;
import static org.apache.calcite.sql.type.ReturnTypes.*;
import static org.apache.calcite.sql.type.SqlTypeName.*;
import static org.apache.calcite.sql.type.SqlTypeName.BIGINT;
import static org.apache.calcite.sql.type.SqlTypeName.DATE;
import static org.apache.calcite.sql.type.SqlTypeName.DOUBLE;
import static org.apache.calcite.sql.type.SqlTypeName.INTEGER;


/**
 * This transformer operates on SqlCalls with 'FROM_UTC_TIMESTAMP(x, timezone)' Coral IR function
 * and transforms it into different trino engine compatible functions based on the data type of 'x'.
 * For Example:
 * A SqlCall of the form: "FROM_UTC_TIMESTAMP(10000, 'America/Los_Angeles')" is transformed to
 * "CAST(AT_TIMEZONE(FROM_UNIXTIME_NANOS(CAST(10000 AS BIGINT) * 1000000), $CANONICALIZE_HIVE_TIMEZONE_ID('America/Los_Angeles')) AS TIMESTAMP(3))"
 */
public class FromUtcTimestampOperatorTransformer extends SqlCallTransformer {

  private static final Set<SqlTypeName> INT_SQL_TYPE_NAMES =
      new HashSet<>(Arrays.asList(BIGINT, INTEGER, SMALLINT, TINYINT));
  private static final Set<SqlTypeName> DECIMAL_SQL_TYPE_NAMES = new HashSet<>(Arrays.asList(DOUBLE, FLOAT, DECIMAL));
  private static final Set<SqlTypeName> TIMESTAMP_SQL_TYPE_NAMES = new HashSet<>(Arrays.asList(TIMESTAMP, DATE));
  private static final String FROM_UTC_TIMESTAMP = "from_utc_timestamp";
  private static final String AT_TIMEZONE = "at_timezone";
  private static final String WITH_TIMEZONE = "with_timezone";
  private static final String TO_UNIXTIME = "to_unixtime";
  private static final String FROM_UNIXTIME_NANOS = "from_unixtime_nanos";
  private static final String TIMESTAMP_FROM_UNIXTIME = "timestamp_from_unixtime";
  private static final String CANONICALIZE_HIVE_TIMEZONE_ID = "$canonicalize_hive_timezone_id";

  public FromUtcTimestampOperatorTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(FROM_UTC_TIMESTAMP);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> operands = sqlCall.getOperandList();
    SqlNode sourceValue = operands.get(0);
    SqlNode timezone = operands.get(1);

    RelDataType targetType = new BasicSqlType(new HiveTypeSystem(), TIMESTAMP, 3);
    RelDataType inputType = deriveRelDatatype(sqlCall.getOperandList().get(0));
    SqlTypeName inputSqlTypeName = inputType.getSqlTypeName();

    // In below definitions we should use `TIMESTAMP WITH TIME ZONE`. As calcite is lacking
    // this type we use `TIMESTAMP` instead. It does not have any practical implications as result syntax tree
    // is not type-checked, and only used for generating output SQL for a view query.
    SqlOperator trinoAtTimeZone = createSqlOperator(AT_TIMEZONE, explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoWithTimeZone =
        createSqlOperator(WITH_TIMEZONE, explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoToUnixTime = createSqlOperator(TO_UNIXTIME, explicit(DOUBLE));
    SqlOperator trinoFromUnixtimeNanos =
        createSqlOperator(FROM_UNIXTIME_NANOS, explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoFromUnixTime =
        createSqlOperator(TIMESTAMP_FROM_UNIXTIME, explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoCanonicalizeHiveTimezoneId = createSqlOperator(CANONICALIZE_HIVE_TIMEZONE_ID, explicit(VARCHAR));

    SqlCall canonicalizeHiveTimezoneIdSqlCall = trinoCanonicalizeHiveTimezoneId.createCall(ZERO, timezone);

    if (INT_SQL_TYPE_NAMES.contains(inputSqlTypeName)) {

      SqlCall castedOperandCall = castOperand(sourceValue, new BasicSqlType(new HiveTypeSystem(), BIGINT));
      SqlCall multipliedValueCall =
          MULTIPLY.createCall(ZERO, castedOperandCall, CalciteUtil.createLiteralNumber(1000000, ZERO));
      SqlCall fromUnixtimeNanosCall = trinoFromUnixtimeNanos.createCall(ZERO, multipliedValueCall);
      SqlCall atTimeZoneCall =
          trinoAtTimeZone.createCall(ZERO, fromUnixtimeNanosCall, canonicalizeHiveTimezoneIdSqlCall);

      return castOperand(atTimeZoneCall, targetType);
    } else if (DECIMAL_SQL_TYPE_NAMES.contains(inputSqlTypeName)) {

      SqlCall castedOperandCall = castOperand(sourceValue, new BasicSqlType(new HiveTypeSystem(), DOUBLE));
      SqlCall fromUnixTimeCall = trinoFromUnixTime.createCall(ZERO, castedOperandCall);
      SqlCall atTimeZoneCall = trinoAtTimeZone.createCall(ZERO, fromUnixTimeCall, canonicalizeHiveTimezoneIdSqlCall);

      return castOperand(atTimeZoneCall, targetType);
    } else if (TIMESTAMP_SQL_TYPE_NAMES.contains(inputSqlTypeName)) {

      SqlCall withTimeZoneCall =
          trinoWithTimeZone.createCall(ZERO, sourceValue, CalciteUtil.createStringLiteral("UTC", ZERO));
      SqlCall toUnixTimeCall = trinoToUnixTime.createCall(ZERO, withTimeZoneCall);
      SqlCall fromUnixTimeCall = trinoFromUnixTime.createCall(ZERO, toUnixTimeCall);
      SqlCall atTimeZoneCall = trinoAtTimeZone.createCall(ZERO, fromUnixTimeCall, canonicalizeHiveTimezoneIdSqlCall);

      return castOperand(atTimeZoneCall, targetType);
    }
    return sqlCall;
  }

  private SqlCall castOperand(SqlNode operand, RelDataType relDataType) {
    return SqlStdOperatorTable.CAST.createCall(ZERO, operand, getSqlDataTypeSpecForCasting(relDataType));
  }

  private SqlDataTypeSpec getSqlDataTypeSpecForCasting(RelDataType relDataType) {
    final SqlTypeNameSpec typeNameSpec = new SqlBasicTypeNameSpec(relDataType.getSqlTypeName(),
        relDataType.getPrecision(), relDataType.getScale(), null, ZERO);
    return new SqlDataTypeSpec(typeNameSpec, ZERO);
  }
}

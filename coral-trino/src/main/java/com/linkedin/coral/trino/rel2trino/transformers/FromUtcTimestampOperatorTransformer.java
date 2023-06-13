/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.BasicSqlType;

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


public class FromUtcTimestampOperatorTransformer extends SqlCallTransformer {

  public FromUtcTimestampOperatorTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  private static final String FROM_UTC_TIMESTAMP = "from_utc_timestamp";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(FROM_UTC_TIMESTAMP);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> operands = sqlCall.getOperandList();
    SqlNode sourceValue = operands.get(0);
    SqlNode timezone = operands.get(1);

    RelDataType inputType = deriveRelDatatype(sqlCall.getOperandList().get(0));
    RelDataType targetType = new BasicSqlType(new HiveTypeSystem(), TIMESTAMP, 3);

    // In below definitions we should use `TIMESTATMP WITH TIME ZONE`. As calcite is lacking
    // this type we use `TIMESTAMP` instead. It does not have any practical implications as result syntax tree
    // is not type-checked, and only used for generating output SQL for a view query.
    SqlOperator trinoAtTimeZone = createSqlOperator("at_timezone", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoWithTimeZone =
        createSqlOperator("with_timezone", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoToUnixTime = createSqlOperator("to_unixtime", explicit(DOUBLE));
    SqlOperator trinoFromUnixtimeNanos =
        createSqlOperator("from_unixtime_nanos", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoFromUnixTime =
        createSqlOperator("from_unixtime", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
    SqlOperator trinoCanonicalizeHiveTimezoneId =
        createSqlOperator("$canonicalize_hive_timezone_id", explicit(VARCHAR));

    RelDataType bigintType = new BasicSqlType(new HiveTypeSystem(), BIGINT);
    RelDataType doubleType = new BasicSqlType(new HiveTypeSystem(), DOUBLE);
    SqlNode bigintConstant = CalciteUtil.createLiteralNumber(1000000, ZERO);

    if (inputType.getSqlTypeName() == BIGINT || inputType.getSqlTypeName() == INTEGER
        || inputType.getSqlTypeName() == SMALLINT || inputType.getSqlTypeName() == TINYINT) {

      SqlCall i0 = SqlStdOperatorTable.CAST.createCall(ZERO, sourceValue, getSqlDataTypeSpecForCasting(bigintType));
      SqlCall i1 = MULTIPLY.createCall(ZERO, i0, bigintConstant);
      SqlCall i2 = trinoFromUnixtimeNanos.createCall(ZERO, i1);

      SqlCall inner = trinoCanonicalizeHiveTimezoneId.createCall(ZERO, timezone);
      SqlCall i3 = trinoAtTimeZone.createCall(ZERO, i2, inner);

      return castOperand(i3, targetType);
    }

    if (inputType.getSqlTypeName() == DOUBLE || inputType.getSqlTypeName() == FLOAT
        || inputType.getSqlTypeName() == DECIMAL) {

      SqlCall i0 = SqlStdOperatorTable.CAST.createCall(ZERO, sourceValue, getSqlDataTypeSpecForCasting(doubleType));

      SqlCall i11 = trinoFromUnixTime.createCall(ZERO, i0);
      SqlCall i12 = trinoCanonicalizeHiveTimezoneId.createCall(ZERO, timezone);

      SqlCall i2 = trinoAtTimeZone.createCall(ZERO, i11, i12);

      return castOperand(i2, targetType);
    }

    if (inputType.getSqlTypeName() == TIMESTAMP || inputType.getSqlTypeName() == DATE) {
      SqlNode stringConstant = CalciteUtil.createStringLiteral("UTC", ZERO);

      SqlCall i00 = trinoCanonicalizeHiveTimezoneId.createCall(ZERO, timezone);

      SqlCall i0 = trinoWithTimeZone.createCall(ZERO, sourceValue, stringConstant);
      SqlCall i1 = trinoToUnixTime.createCall(ZERO, i0);
      SqlCall i2 = trinoFromUnixTime.createCall(ZERO, i1);
      SqlCall i3 = trinoAtTimeZone.createCall(ZERO, i2, i00);

      return castOperand(i3, targetType);
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

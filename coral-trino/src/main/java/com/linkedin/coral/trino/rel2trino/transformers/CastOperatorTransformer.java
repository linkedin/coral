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
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.calcite.CalciteUtil;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;

import static org.apache.calcite.sql.parser.SqlParserPos.*;
import static org.apache.calcite.sql.type.ReturnTypes.*;
import static org.apache.calcite.sql.type.SqlTypeName.*;
import static org.apache.calcite.sql.type.SqlTypeName.DOUBLE;


/**
 * The CAST operator in Trino has some differences compared to Hive, which are handled by this transformer:
 *
 * 1) Hive allows for casting of TIMESTAMP to DECIMAL, by converts it to unix time if the decimal format is valid
 *    Trino does not allow for such conversion, but we can achieve the same behavior by first calling "to_unixtime"
 *    (with the timezone) on the TIMESTAMP and then casting it to DECIMAL after.
 *    Hive:  SELECT CAST(timestamp AS DECIMAL(10,0))
 *    Trino: CAST(to_unixtime(with_timezone(timestamp, 'UTC')) AS DECIMAL(10, 0))
 *
 * 2) Hive allows for casting varbinary/binary to varchar/char, while in Trino we need to use the
 *    built-in function `from_utf8` instead to replace the cast.
 *    Hive:  CAST(binary AS VARCHAR)
 *    Trino: from_utf8(binary)
 *
 * Since this transformer introduces an extra iteration of Calcite validation during RelNode to SqlNode transformation (RHS)
 * for queries with CAST operators, there is an added (but expected) side effect of implicit casting by Calcite's type coercion rules.
 *
 */
public class CastOperatorTransformer extends SqlCallTransformer {
  private static final String WITH_TIMEZONE = "with_timezone";
  private static final String TO_UNIXTIME = "to_unixtime";
  private static final Set<SqlTypeName> BINARY_SQL_TYPE_NAMES =
      new HashSet<>(Arrays.asList(SqlTypeName.VARBINARY, SqlTypeName.BINARY));
  private static final Set<SqlTypeName> CHAR_SQL_TYPE_NAMES =
      new HashSet<>(Arrays.asList(SqlTypeName.VARCHAR, SqlTypeName.CHAR));

  public CastOperatorTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getKind() == SqlKind.CAST;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> operands = sqlCall.getOperandList();

    SqlNode leftOperand = operands.get(0);
    RelDataType sourceType = deriveRelDatatype(leftOperand);
    SqlDataTypeSpec targetSqlDataTypeSpec = (SqlDataTypeSpec) operands.get(1);
    SqlTypeName targetType = SqlTypeName.get(targetSqlDataTypeSpec.getTypeNameSpec().getTypeName().toString());

    if (sourceType.getSqlTypeName() == TIMESTAMP && targetType == DECIMAL) {
      SqlOperator trinoWithTimeZone =
          createSqlOperator(WITH_TIMEZONE, explicit(TIMESTAMP /* should be WITH TIME ZONE */));
      SqlOperator trinoToUnixTime = createSqlOperator(TO_UNIXTIME, explicit(DOUBLE));

      SqlCall withTimeZoneCall =
          trinoWithTimeZone.createCall(ZERO, leftOperand, CalciteUtil.createStringLiteral("UTC", ZERO));
      SqlCall toUnixTimeCall = trinoToUnixTime.createCall(ZERO, withTimeZoneCall);

      return castOperand(toUnixTimeCall, targetSqlDataTypeSpec);
    }

    if (BINARY_SQL_TYPE_NAMES.contains(sourceType.getSqlTypeName()) && CHAR_SQL_TYPE_NAMES.contains(targetType)) {
      SqlOperator fromUTF8 = createSqlOperator("from_utf8", explicit(VARCHAR));

      return fromUTF8.createCall(ZERO, leftOperand);
    }

    return sqlCall;
  }

  private SqlCall castOperand(SqlNode operand, SqlDataTypeSpec targetSqlDataTypeSpec) {
    return SqlStdOperatorTable.CAST.createCall(ZERO, operand, targetSqlDataTypeSpec);
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

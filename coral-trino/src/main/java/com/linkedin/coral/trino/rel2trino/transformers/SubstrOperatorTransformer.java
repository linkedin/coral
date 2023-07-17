/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.HiveTypeSystem;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;

import static org.apache.calcite.sql.parser.SqlParserPos.*;
import static org.apache.calcite.sql.type.SqlTypeName.*;


/**
 * This class implements the transformation of SqlCalls with Coral IR function `SUBSTR`
 * to their corresponding Trino-compatible versions.
 *
 * For example:
 *  Given table:
 *      t1(int_col INTEGER, time_col timestamp)
 *  and a Coral IR SqlCall:
 *      `SUBSTR(time_col, 12, 8)`
 *
 *  The transformed SqlCall would be:
 *      `SUBSTR(CAST(time_col AS VARCHAR(65535)), 12, 8)`
 */
public class SubstrOperatorTransformer extends SqlCallTransformer {

  private static final int DEFAULT_VARCHAR_PRECISION = new HiveTypeSystem().getDefaultPrecision(SqlTypeName.VARCHAR);
  private static final String SUBSTR_OPERATOR_NAME = "substr";
  private static final Set<SqlTypeName> OPERAND_SQL_TYPE_NAMES =
      new HashSet<>(Arrays.asList(SqlTypeName.VARCHAR, SqlTypeName.CHAR));
  private static final SqlDataTypeSpec VARCHAR_SQL_DATA_TYPE_SPEC =
      new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.VARCHAR, DEFAULT_VARCHAR_PRECISION, ZERO), ZERO);

  public SubstrOperatorTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(SUBSTR_OPERATOR_NAME);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> operands = sqlCall.getOperandList();
    RelDataType relDataTypeOfOperand = deriveRelDatatype(operands.get(0));

    // Coral IR accepts a byte array or String as an input for the `substr` operator.
    // This behavior is emulated by casting non-String input to String in this transformer
    // https://cwiki.apache.org/confluence/display/hive/languagemanual+udf
    if (!OPERAND_SQL_TYPE_NAMES.contains(relDataTypeOfOperand.getSqlTypeName())) {
      List<SqlNode> modifiedOperands = new ArrayList<>();

      modifiedOperands.add(SqlStdOperatorTable.CAST.createCall(ZERO, operands.get(0), VARCHAR_SQL_DATA_TYPE_SPEC));
      modifiedOperands.addAll(operands.subList(1, operands.size()));

      return sqlCall.getOperator().createCall(SqlParserPos.ZERO, modifiedOperands);
    }
    return sqlCall;
  }
}

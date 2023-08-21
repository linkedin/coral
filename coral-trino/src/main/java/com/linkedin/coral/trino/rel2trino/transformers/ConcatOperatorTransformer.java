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
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.HiveTypeSystem;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * This transformer is designed for SqlCalls that use the CONCAT operator.
 * Its purpose is to convert the data types of the operands to be compatible with Trino.
 * Trino only allows VARCHAR type operands for the CONCAT operator. Therefore, if there are any other data type operands present,
 * an extra CAST operator is added around the operand to cast it to VARCHAR.
 */
public class ConcatOperatorTransformer extends SqlCallTransformer {
  private static final int DEFAULT_VARCHAR_PRECISION = new HiveTypeSystem().getDefaultPrecision(SqlTypeName.VARCHAR);
  private static final String OPERATOR_NAME = "concat";
  private static final Set<SqlTypeName> OPERAND_SQL_TYPE_NAMES =
      new HashSet<>(Arrays.asList(SqlTypeName.VARCHAR, SqlTypeName.CHAR));
  private static final SqlDataTypeSpec VARCHAR_SQL_DATA_TYPE_SPEC =
      new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.VARCHAR, DEFAULT_VARCHAR_PRECISION, ZERO), ZERO);

  public ConcatOperatorTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(OPERATOR_NAME);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> updatedOperands = new ArrayList<>();

    for (SqlNode operand : sqlCall.getOperandList()) {
      RelDataType type = deriveRelDatatype(operand);
      if (!OPERAND_SQL_TYPE_NAMES.contains(type.getSqlTypeName())) {
        SqlNode castOperand = SqlStdOperatorTable.CAST.createCall(POS,
            new ArrayList<>(Arrays.asList(operand, VARCHAR_SQL_DATA_TYPE_SPEC)));
        updatedOperands.add(castOperand);
      } else {
        updatedOperands.add(operand);
      }
    }
    return sqlCall.getOperator().createCall(POS, updatedOperands);
  }
}

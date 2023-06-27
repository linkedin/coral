/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlRowTypeSpec;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeUtil;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.functions.HiveNamedStructFunction;

import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


/**
 * Converts Coral's named_struct function to CAST AS ROW(types) function.
 */
public class NamedStructOperandTransformer extends SqlCallTransformer {

  public NamedStructOperandTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().equals(HiveNamedStructFunction.NAMED_STRUCT);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> inputOperands = sqlCall.getOperandList();

    List<SqlDataTypeSpec> rowTypes = new ArrayList<>();
    List<String> fieldNames = new ArrayList<>();
    for (int i = 0; i < inputOperands.size(); i += 2) {
      assert inputOperands.get(i) instanceof SqlLiteral;
      fieldNames.add(((SqlLiteral) inputOperands.get(i)).getStringValue());
    }

    List<SqlNode> rowCallOperands = new ArrayList<>();
    for (int i = 1; i < inputOperands.size(); i += 2) {
      rowCallOperands.add(inputOperands.get(i));
      RelDataType type = deriveRelDatatype(inputOperands.get(i));
      rowTypes.add(SqlTypeUtil.convertTypeToSpec(type));
    }
    SqlNode rowCall = SqlStdOperatorTable.ROW.createCall(ZERO, rowCallOperands);
    return SqlStdOperatorTable.CAST.createCall(ZERO, rowCall, new SqlRowTypeSpec(fieldNames, rowTypes, ZERO));
  }
}

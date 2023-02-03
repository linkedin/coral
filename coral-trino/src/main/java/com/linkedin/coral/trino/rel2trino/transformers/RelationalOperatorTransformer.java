/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.validate.SqlValidator;

import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.com.google.common.collect.Multimap;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.TrinoTryCastFunction;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * Coral IR allows implicit casting to VARCHAR from other data types such as INTs, etc. However, Trino requires explicit casting.
 * This transformation casts the operands' data type to ensure operand inter-compatibility for Trino.
 */
public class RelationalOperatorTransformer extends SqlCallTransformer {
  // In the CAST_REFERENCE_MAP, each (Key, Value) pair represents a data type pairing where
  // the Key data type must be cast to the Value data type in order to ensure compatibility for Trino operations.
  private static final Multimap<SqlTypeFamily, SqlTypeFamily> CAST_REFERENCE_MAP =
      ImmutableMultimap.<SqlTypeFamily, SqlTypeFamily> builder()
          .putAll(SqlTypeFamily.CHARACTER, SqlTypeFamily.BOOLEAN, SqlTypeFamily.NUMERIC, SqlTypeFamily.DATE,
              SqlTypeFamily.TIME, SqlTypeFamily.TIMESTAMP)
          .putAll(SqlTypeFamily.NUMERIC, SqlTypeFamily.DATE, SqlTypeFamily.TIME, SqlTypeFamily.TIMESTAMP)
          .putAll(SqlTypeFamily.BOOLEAN, SqlTypeFamily.NUMERIC)
          .putAll(SqlTypeFamily.BINARY, SqlTypeFamily.CHARACTER, SqlTypeFamily.NUMERIC).build();

  public RelationalOperatorTransformer(SqlValidator sqlValidator) {
    super(sqlValidator);
  }

  @Override
  protected boolean predicate(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case EQUALS:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case NOT_EQUALS:
        return true;
      default:
        return false;
    }
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> updatedOperands = new ArrayList<>();

    SqlNode leftOperand = sqlCall.operand(0);
    SqlNode rightOperand = sqlCall.operand(1);

    if (leftOperand.getKind() == SqlKind.CAST || rightOperand.getKind() == SqlKind.CAST) {
      return sqlCall;
    }

    RelDataType leftType = getRelDataType(leftOperand);
    RelDataType rightType = getRelDataType(rightOperand);

    // Determine if the left operand requires casting to the data type of the right operand.
    if (CAST_REFERENCE_MAP.containsEntry(leftType.getSqlTypeName().getFamily(),
        rightType.getSqlTypeName().getFamily())) {
      SqlNode newLeftOperand = castOperand(leftOperand, rightType);
      updatedOperands.addAll(Arrays.asList(newLeftOperand, rightOperand));
    }
    //Determine if the right operand requires casting to the data type of the left operand.
    else if (CAST_REFERENCE_MAP.containsEntry(rightType.getSqlTypeName().getFamily(),
        leftType.getSqlTypeName().getFamily())) {
      SqlNode newRightOperand = castOperand(rightOperand, leftType);
      updatedOperands.addAll(Arrays.asList(leftOperand, newRightOperand));
    } else {
      updatedOperands.addAll(Arrays.asList(leftOperand, rightOperand));
    }

    return sqlCall.getOperator().createCall(POS, updatedOperands);
  }

  private SqlNode castOperand(SqlNode operand, RelDataType relDataType) {
    return TrinoTryCastFunction.INSTANCE.createCall(POS,
        new ArrayList<>(Arrays.asList(operand, getSqlDataTypeSpecForCasting(relDataType))));
  }

  private SqlDataTypeSpec getSqlDataTypeSpecForCasting(RelDataType relDataType) {
    final SqlTypeNameSpec typeNameSpec = new SqlBasicTypeNameSpec(relDataType.getSqlTypeName(), ZERO);
    return new SqlDataTypeSpec(typeNameSpec, ZERO);
  }
}

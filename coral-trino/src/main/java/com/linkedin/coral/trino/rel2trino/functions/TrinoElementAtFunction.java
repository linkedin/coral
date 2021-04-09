/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.Arrays;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlSingleOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * TrinoElementAtFunction represents a SQL operator that maps to the subscript [] operator in SQL standard. In the
 * case of arrays it access the nth element of an array, and in case of maps it looks up the value corresponding to the
 * given key. The implementation is identical to the Calcite ITEM operator (from {@link org.apache.calcite.sql.SqlSpecialOperator})
 * but uses the "element_at" name, and unparses to the element_at(map, key) syntax.
 */
public class TrinoElementAtFunction extends SqlSpecialOperator {

  public static final TrinoElementAtFunction INSTANCE = new TrinoElementAtFunction();

  private static final SqlSingleOperandTypeChecker ARRAY_OR_MAP =
      OperandTypes.or(OperandTypes.family(SqlTypeFamily.ARRAY), OperandTypes.family(SqlTypeFamily.MAP),
          OperandTypes.family(SqlTypeFamily.ANY));

  private TrinoElementAtFunction() {
    super("element_at", SqlKind.OTHER_FUNCTION, 100, true, null, null, null);
  }

  @Override
  public ReduceResult reduceExpr(int ordinal, TokenSequence list) {
    SqlNode left = list.node(ordinal - 1);
    SqlNode right = list.node(ordinal + 1);
    return new ReduceResult(ordinal - 1, ordinal + 2,
        createCall(
            SqlParserPos.sum(Arrays.asList(left.getParserPosition(), right.getParserPosition(), list.pos(ordinal))),
            left, right));
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    SqlUtil.unparseFunctionSyntax(this, writer, call);
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.of(2);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    final SqlNode left = callBinding.operand(0);
    final SqlNode right = callBinding.operand(1);
    if (!ARRAY_OR_MAP.checkSingleOperandType(callBinding, left, 0, throwOnFailure)) {
      return false;
    }
    final SqlSingleOperandTypeChecker checker = getChecker(callBinding);
    return checker.checkSingleOperandType(callBinding, right, 0, throwOnFailure);
  }

  private SqlSingleOperandTypeChecker getChecker(SqlCallBinding callBinding) {
    final RelDataType operandType = callBinding.getOperandType(0);
    switch (operandType.getSqlTypeName()) {
      case ARRAY:
        return OperandTypes.family(SqlTypeFamily.INTEGER);
      case MAP:
        return OperandTypes.family(operandType.getKeyType().getSqlTypeName().getFamily());
      case ANY:
      case DYNAMIC_STAR:
        return OperandTypes.or(OperandTypes.family(SqlTypeFamily.INTEGER),
            OperandTypes.family(SqlTypeFamily.CHARACTER));
      default:
        throw callBinding.newValidationSignatureError();
    }
  }

  @Override
  public String getAllowedSignatures(String name) {
    return "<ARRAY>[<INTEGER>]\n" + "<MAP>[<VALUE>]";
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    final RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
    final RelDataType operandType = opBinding.getOperandType(0);
    switch (operandType.getSqlTypeName()) {
      case ARRAY:
        return typeFactory.createTypeWithNullability(operandType.getComponentType(), true);
      case MAP:
        return typeFactory.createTypeWithNullability(operandType.getValueType(), true);
      case ANY:
      case DYNAMIC_STAR:
        return typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.ANY), true);
      default:
        throw new AssertionError();
    }
  }
}

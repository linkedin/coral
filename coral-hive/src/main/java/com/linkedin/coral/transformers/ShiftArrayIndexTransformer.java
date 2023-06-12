/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import java.math.BigDecimal;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;


/**
 * Transformer to convert SqlCall from array[i] to array[i+1] to ensure array indexes start at 1.
 */
public class ShiftArrayIndexTransformer extends SqlCallTransformer {

  private static final String ITEM_OPERATOR = "ITEM";

  public ShiftArrayIndexTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  public boolean condition(SqlCall sqlCall) {
    if (ITEM_OPERATOR.equalsIgnoreCase(sqlCall.getOperator().getName())) {
      final SqlNode columnNode = sqlCall.getOperandList().get(0);
      return deriveRelDatatype(columnNode) instanceof ArraySqlType;
    }
    return false;
  }

  @Override
  public SqlCall transform(SqlCall sqlCall) {
    final SqlNode itemNode = sqlCall.getOperandList().get(1);
    if (itemNode instanceof SqlNumericLiteral
        && deriveRelDatatype(itemNode).getSqlTypeName().equals(SqlTypeName.INTEGER)) {
      final Integer value = ((SqlNumericLiteral) itemNode).getValueAs(Integer.class);
      sqlCall.setOperand(1,
          SqlNumericLiteral.createExactNumeric(new BigDecimal(value + 1).toString(), itemNode.getParserPosition()));
    } else {
      final SqlCall oneBasedIndex = SqlStdOperatorTable.PLUS.createCall(itemNode.getParserPosition(), itemNode,
          SqlNumericLiteral.createExactNumeric("1", SqlParserPos.ZERO));
      sqlCall.setOperand(1, oneBasedIndex);
    }
    return sqlCall;
  }
}

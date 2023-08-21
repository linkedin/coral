/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;

import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;


/**
 * This transformer focuses on SqlCalls that involve a FunctionFieldReferenceOperator with the following characteristics:
 * (1) The first operand is a SqlBasicCall with a non-struct RelDataType, and the second operand is tag_0.
 * This indicates that the first operand represents a Union data type with a single data type inside.
 * (2) Examples of such SqlCalls include extract_union(product.value).tag_0 or (extract_union(product.value).id).tag_0.
 * (3) The transformation for such SqlCalls is to return the first operand.
 */
public class SingleUnionFieldReferenceTransformer extends SqlCallTransformer {
  private static final String TAG_0_OPERAND = "tag_0";

  public SingleUnionFieldReferenceTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    if (FunctionFieldReferenceOperator.DOT.getName().equalsIgnoreCase(sqlCall.getOperator().getName())) {
      if (sqlCall.operand(0) instanceof SqlBasicCall) {
        SqlBasicCall outerSqlBasicCall = sqlCall.operand(0);
        boolean isOperandStruct = deriveRelDatatype(outerSqlBasicCall).isStruct();

        return !isOperandStruct
            && FunctionFieldReferenceOperator.fieldNameStripQuotes(sqlCall.operand(1)).equalsIgnoreCase(TAG_0_OPERAND);
      }
    }
    return false;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    // convert x.tag_0 -> x where x is a sqlBasicCall with non-struct RelDataType
    return sqlCall.operand(0);
  }
}

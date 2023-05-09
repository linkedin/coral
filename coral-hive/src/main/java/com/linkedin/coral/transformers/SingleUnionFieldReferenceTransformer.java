/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.validate.SqlValidator;

import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;
import com.linkedin.coral.common.transformers.SqlCallTransformer;


public class SingleUnionFieldReferenceTransformer extends SqlCallTransformer {

  public SingleUnionFieldReferenceTransformer(SqlValidator sqlValidator) {
    super(sqlValidator);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    // weed out (`extract_union`(`product`.`value`).`productcategoryurns`).`tag_0`
    // when productcategoryurns is a single type union
    // weed out `extract_union`(`product`.`value`).`tag_0`
    if (FunctionFieldReferenceOperator.DOT.getName().equalsIgnoreCase(sqlCall.getOperator().getName())) {
      SqlNode firstOperand = sqlCall.operand(0);
      if (firstOperand instanceof SqlBasicCall) {

        // `extract_union`(`product`.`value`).`productcategoryurns` or (`extract_union`(`product`.`value`).`productcategoryurns`).`tag_0`
        SqlBasicCall outerSqlBasicCall = (SqlBasicCall) firstOperand;
        boolean isOperandStruct = getRelDataType(outerSqlBasicCall).isStruct();

        if (!isOperandStruct
            && FunctionFieldReferenceOperator.fieldNameStripQuotes(sqlCall.operand(1)).equalsIgnoreCase("tag_0")) {
          return true;
        }

        // `extract_union`(`product`.`value`) = innerSqlBasicCall
        //        if (outerSqlBasicCall.operand(0) instanceof SqlBasicCall) {
        //          SqlBasicCall innerSqlBasicCall = outerSqlBasicCall.operand(0);
        //          if (innerSqlBasicCall.getOperator().getName().equalsIgnoreCase("extract_union")
        //              && FunctionFieldReferenceOperator.fieldNameStripQuotes(sqlCall.operand(1)).equalsIgnoreCase("tag_0")
        //              && !getRelDataType(outerSqlBasicCall).isStruct() ) {
        //            return true;
        //          }
        //        }
      }
    }
    return false;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    // convert (`extract_union`(`product`.`value`).`productcategoryurns`).`tag_0`
    // to `extract_union`(`product`.`value`).`productcategoryurns`

    // convert x.y -> x where x is a sqlBasicCall

    return sqlCall.operand(0);
  }
}

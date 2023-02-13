/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlRowTypeNameSpec;
import org.apache.calcite.sql.SqlRowTypeSpec;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.HiveNamedStructFunction;


/**
 * This transformer transforms Coral IR function `CAST(ROW: RECORD_TYPE)` to Spark compatible function `named_struct`.
 * For example, the SqlCall `CAST(ROW(123, 'xyz') AS ROW(`abc` INTEGER, `def` CHAR(3) CHARACTER SET `ISO-8859-1`))`
 * will be transformed to `named_struct('abc', 123, 'def', 'xyz')`
 */
public class CastToNamedStructTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    if (sqlCall.getOperator().getKind() == SqlKind.CAST) {
      final SqlNode firstOperand = sqlCall.getOperandList().get(0);
      final SqlNode secondOperand = sqlCall.getOperandList().get(1);
      return firstOperand instanceof SqlCall && ((SqlCall) firstOperand).getOperator().getKind() == SqlKind.ROW
          && secondOperand instanceof SqlRowTypeSpec;
    }
    return false;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> newOperands = new ArrayList<>();
    final SqlCall rowCall = (SqlCall) sqlCall.getOperandList().get(0); // like `ROW(123, 'xyz')` in above example
    final SqlRowTypeSpec sqlRowTypeSpec = (SqlRowTypeSpec) sqlCall.getOperandList().get(1); // like `ROW(`abc` INTEGER, `def` CHAR(3) CHARACTER SET `ISO-8859-1`))` in above example
    for (int i = 0; i < rowCall.getOperandList().size(); ++i) {
      final String fieldName =
          ((SqlRowTypeNameSpec) sqlRowTypeSpec.getTypeNameSpec()).getFieldNames().get(i).names.get(0);
      newOperands.add(new SqlIdentifier("'" + fieldName + "'", SqlParserPos.ZERO)); // need to single-quote the field name
      newOperands.add(rowCall.getOperandList().get(i));
    }
    return HiveNamedStructFunction.NAMED_STRUCT.createCall(sqlCall.getParserPosition(), newOperands);
  }
}

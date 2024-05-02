/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.functions;

import org.apache.calcite.sql.SqlAsOperator;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;


/**
 * This class represents a custom NOP (No Operation) function in SQL.
 * The NOP function takes only one operand and does not perform any operation on it.
 * When unparsed, it simply returns the operand as is. We extend the AS operator to hold the
 * operand as it doesn't wrap the operand with parentheses when unparsed.
 */
public class NOP extends SqlAsOperator {
  public NOP() {
    super();
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    SqlNode operand = call.operand(0);
    operand.unparse(writer, leftPrec, rightPrec);
  }
}

/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.com.google.common.base.Preconditions;


public class TrinoTryCastFunction extends SqlFunction {
  public static final TrinoTryCastFunction INSTANCE = new TrinoTryCastFunction();

  public TrinoTryCastFunction() {
    super("try_cast", SqlKind.CAST, SqlStdOperatorTable.CAST::inferReturnType, null, null,
        SqlFunctionCategory.USER_DEFINED_FUNCTION);
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    Preconditions.checkState(call.operandCount() == 2);
    final SqlWriter.Frame frame = writer.startFunCall(getName());
    call.operand(0).unparse(writer, 0, 0);
    writer.sep("AS");
    if (call.operand(1) instanceof SqlIntervalQualifier) {
      writer.sep("INTERVAL");
    }
    call.operand(1).unparse(writer, 0, 0);
    writer.endFunCall(frame);
  }
}

/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import com.google.common.base.Preconditions;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.common.functions.FunctionReturnTypes;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * This class is a subclass of {@link SqlCallTransformer} which transforms a function operator on SqlNode layer
 * if the signature of the operator to be transformed, including both the name and the number of operands,
 * matches the target values in the condition function.
 */
public abstract class SourceOperatorMatchSqlCallTransformer extends SqlCallTransformer {
  public static final SqlOperator TIMESTAMP_OPERATOR =
      new SqlUserDefinedFunction(new SqlIdentifier("timestamp", SqlParserPos.ZERO), FunctionReturnTypes.TIMESTAMP, null,
          OperandTypes.STRING, null, null) {
        @Override
        public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
          // for timestamp operator, we need to construct `CAST(x AS TIMESTAMP)`
          Preconditions.checkState(call.operandCount() == 1);
          final SqlWriter.Frame frame = writer.startFunCall("CAST");
          call.operand(0).unparse(writer, 0, 0);
          writer.sep("AS");
          writer.literal("TIMESTAMP");
          writer.endFunCall(frame);
        }
      };

  protected final SqlOperator DATE_OPERATOR = new SqlUserDefinedFunction(new SqlIdentifier("date", SqlParserPos.ZERO),
      ReturnTypes.DATE, null, OperandTypes.STRING, null, null);
  protected final String sourceOpName;
  protected final int numOperands;

  public SourceOperatorMatchSqlCallTransformer(String sourceOpName, int numOperands) {
    this.sourceOpName = sourceOpName;
    this.numOperands = numOperands;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sourceOpName.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == numOperands;
  }
}

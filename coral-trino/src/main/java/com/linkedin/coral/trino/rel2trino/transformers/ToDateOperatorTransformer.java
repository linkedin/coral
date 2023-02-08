/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;
import static com.linkedin.coral.trino.rel2trino.CoralToTrinoSqlCallConverter.*;


/**
 *  This class implements the transformation from the operation of "to_date"
 *  for example, "to_date('2023-01-01')" is transformed into "date(CAST('2023-01-01' AS TIMESTAMP))"
 */
public class ToDateOperatorTransformer extends SqlCallTransformer {
  private static final StaticHiveFunctionRegistry HIVE_FUNCTION_REGISTRY = new StaticHiveFunctionRegistry();
  private static final String FROM_OPERATOR_NAME = "to_date";

  private static final String TO_OPERATOR_NAME = "date";
  private static final int NUM_OPERANDS = 1;
  private static final SqlOperator TIMESTAMP_OPERATOR =
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

  private final boolean avoidTransformToDateUDF;

  public ToDateOperatorTransformer(boolean avoidTransformToDateUDF) {
    this.avoidTransformToDateUDF = avoidTransformToDateUDF;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return !avoidTransformToDateUDF && FROM_OPERATOR_NAME.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == NUM_OPERANDS;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = new ArrayList<>();
    SqlNode timestampSqlCall = createCall(TIMESTAMP_OPERATOR, sourceOperands, SqlParserPos.ZERO);
    newOperands.add(timestampSqlCall);
    return createCall(createSqlOperator(TO_OPERATOR_NAME,
        HIVE_FUNCTION_REGISTRY.lookup(FROM_OPERATOR_NAME).iterator().next().getSqlOperator().getReturnTypeInference()),
        newOperands, SqlParserPos.ZERO);
  }
}

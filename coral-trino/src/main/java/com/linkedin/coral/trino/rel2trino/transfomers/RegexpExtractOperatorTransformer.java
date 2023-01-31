/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transfomers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.transformers.OperatorBasedSqlCallTransformer;

import static com.linkedin.coral.trino.rel2trino.utils.CoralToTrinoSqlCallTransformersUtil.*;


/**
 * This class transforms a Coral SqlCall of "regexp_extract" operator with 3 operands into a Trino SqlCall of an operator
 * named "regexp_extract"
 */
public class RegexpExtractOperatorTransformer extends OperatorBasedSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "regexp_extract";
  private static final int OPERAND_NUM = 3;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlUDF("regexp_extract", hiveToCoralSqlOperator("regexp_extract").getReturnTypeInference());

  private static final SqlOperator HIVE_PATTERN_TO_TRINO_OPERATOR =
      new SqlUserDefinedFunction(new SqlIdentifier("hive_pattern_to_trino", SqlParserPos.ZERO),
          FunctionReturnTypes.STRING, null, OperandTypes.STRING, null, null);

  public RegexpExtractOperatorTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM, TARGET_OPERATOR);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = new ArrayList<>();
    newOperands.add(sourceOperands.get(0));

    List<SqlNode> hivePatternToTrinoOperands = new ArrayList<>();
    hivePatternToTrinoOperands.add(sourceOperands.get(1));
    newOperands
        .add(HIVE_PATTERN_TO_TRINO_OPERATOR.createCall(new SqlNodeList(hivePatternToTrinoOperands, SqlParserPos.ZERO)));

    newOperands.add(sourceOperands.get(2));
    return TARGET_OPERATOR.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
  }
}

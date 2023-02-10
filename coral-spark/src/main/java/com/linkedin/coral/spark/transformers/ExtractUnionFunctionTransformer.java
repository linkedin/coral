/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility;
import com.linkedin.coral.spark.containers.SparkUDFInfo;


/**
 * This transformer transforms `extract_union` to `coalesce_struct`.
 * Instead of leaving `extract_union` visible to Spark, since we adopted the new exploded struct schema (a.k.a struct_tr)
 * that is different from extract_union's output (a.k.a struct_ex) to interpret union in Coral IR,
 * we need to swap the reference of `extract_union` to a new UDF that is coalescing the difference between
 * struct_tr and struct_ex.
 * See {@link CoalesceStructUtility#COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY} and its comments for more details.
 *
 * Check `CoralSparkTest#testUnionExtractUDF` for examples.
 */
public class ExtractUnionFunctionTransformer extends SqlCallTransformer {
  private static final String EXTRACT_UNION = "extract_union";
  private static final String COALESCE_STRUCT = "coalesce_struct";

  private final Set<SparkUDFInfo> sparkUDFInfos;

  public ExtractUnionFunctionTransformer(Set<SparkUDFInfo> sparkUDFInfos) {
    this.sparkUDFInfos = sparkUDFInfos;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return EXTRACT_UNION.equalsIgnoreCase(sqlCall.getOperator().getName());
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    sparkUDFInfos.add(new SparkUDFInfo("com.linkedin.coalescestruct.GenericUDFCoalesceStruct", "coalesce_struct",
        ImmutableList.of(URI.create("ivy://com.linkedin.coalesce-struct:coalesce-struct-impl:+")),
        SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));
    final List<SqlNode> operandList = sqlCall.getOperandList();
    final SqlOperator coalesceStructFunction =
        createSqlOperator(COALESCE_STRUCT, CoalesceStructUtility.COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY);
    if (operandList.size() == 1) {
      // one arg case: extract_union(field_name)
      return coalesceStructFunction.createCall(sqlCall.getParserPosition(), operandList);
    } else if (operandList.size() == 2) {
      // two arg case: extract_union(field_name, ordinal)
      final int newOrdinal = ((SqlNumericLiteral) operandList.get(1)).getValueAs(Integer.class) + 1;
      return coalesceStructFunction.createCall(sqlCall.getParserPosition(), ImmutableList.of(operandList.get(0),
          SqlNumericLiteral.createExactNumeric(String.valueOf(newOrdinal), SqlParserPos.ZERO)));
    } else {
      return sqlCall;
    }
  }
}

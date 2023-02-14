/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.net.URI;
import java.util.Set;

import org.apache.calcite.sql.SqlCall;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.spark.containers.SparkUDFInfo;


/**
 * This transformer transforms the SqlCall `generic_project(col, col_name_string, hive_type_string)`
 * (check {@link com.linkedin.coral.common.FuzzyUnionSqlRewriter}) to `generic_project(col, hive_type_string)`
 * to meet Spark's expectation, and registers the `GenericProject` UDF.
 */
public class FuzzyUnionGenericProjectTransformer extends SqlCallTransformer {

  private final Set<SparkUDFInfo> sparkUDFInfos;

  public FuzzyUnionGenericProjectTransformer(Set<SparkUDFInfo> sparkUDFInfos) {
    this.sparkUDFInfos = sparkUDFInfos;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator() instanceof GenericProjectFunction && sqlCall.getOperandList().size() == 3;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    sparkUDFInfos.add(new SparkUDFInfo("com.linkedin.genericprojectudf.GenericProject", "generic_project",
        ImmutableList.of(URI.create("ivy://com.linkedin.GenericProject:GenericProject-impl:+")),
        SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));
    return sqlCall.getOperator().createCall(sqlCall.getParserPosition(), sqlCall.getOperandList().get(0),
        sqlCall.getOperandList().get(2));
  }
}

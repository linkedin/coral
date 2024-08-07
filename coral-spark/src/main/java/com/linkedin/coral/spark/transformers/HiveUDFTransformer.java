/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.collect.ImmutableSet;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.exceptions.UnsupportedUDFException;


/**
 * This transformer converts the Hive UDF SqlCall name from the UDF class name (e.g., `com.linkedin.HiveUDF`)
 * to the corresponding view-dependent UDF name in the view text. It also adds the UDF information to `sparkUDFInfos`.
 * Refer to `CoralSparkTest#testHiveUDFTransformer()` for an example.
 */
public class HiveUDFTransformer extends SqlCallTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(HiveUDFTransformer.class);

  /**
   * Some LinkedIn UDFs get registered correctly in a SparkSession, and hence a DataFrame is successfully
   * created for the views containing those UDFs, but those UDFs fail going forward during the execution phase.
   * We cannot use a fallback mechanism for such cases because a DataFrame can be created successfully.
   * Because of this, we need to proactively fail during the CoralSpark view analysis phase when we encounter such UDFs,
   * so that Spark can fall back to its stable execution.
   */
  private static final Set<String> UNSUPPORTED_HIVE_UDFS =
      ImmutableSet.of("com.linkedin.dali.udf.userinterfacelookup.hive.UserInterfaceLookup",
          "com.linkedin.dali.udf.portallookup.hive.PortalLookup",
          // for unit test
          "com.linkedin.coral.hive.hive2rel.CoralTestUnsupportedUDF");
  private final Set<SparkUDFInfo> sparkUDFInfos;

  public HiveUDFTransformer(Set<SparkUDFInfo> sparkUDFInfos) {
    this.sparkUDFInfos = sparkUDFInfos;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    final SqlOperator operator = sqlCall.getOperator();
    final String operatorName = operator.getName();
    return operator instanceof VersionedSqlUserDefinedFunction && operatorName.contains(".")
        && !operatorName.equals(".");
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final VersionedSqlUserDefinedFunction operator = (VersionedSqlUserDefinedFunction) sqlCall.getOperator();
    final String operatorName = operator.getName();
    if (UNSUPPORTED_HIVE_UDFS.contains(operatorName)) {
      throw new UnsupportedUDFException(operatorName);
    }
    final String viewDependentFunctionName = operator.getViewDependentFunctionName();
    final List<String> dependencies = operator.getIvyDependencies();
    List<URI> listOfUris = dependencies.stream().map(URI::create).collect(Collectors.toList());
    LOG.info("Function: {} is not a Builtin UDF or Transport UDF. We fall back to its Hive "
        + "function with ivy dependency: {}", operatorName, String.join(",", dependencies));
    final SparkUDFInfo sparkUDFInfo =
        new SparkUDFInfo(operatorName, viewDependentFunctionName, listOfUris, SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF);
    sparkUDFInfos.add(sparkUDFInfo);
    final SqlOperator convertedFunction =
        createSqlOperator(viewDependentFunctionName, operator.getReturnTypeInference());
    return convertedFunction.createCall(sqlCall.getParserPosition(), sqlCall.getOperandList());
  }
}

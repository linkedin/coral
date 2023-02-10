/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.exceptions.UnsupportedUDFException;


/**
 * This transformer transforms legacy Hive UDFs to an equivalent registered Transport UDF.
 * Check `CoralSparkTest#testTransportUDFTransformer()` for example.
 */
public class TransportUDFTransformer extends SqlCallTransformer {
  private final String hiveUDFClassName;
  private final String sparkUDFClassName;
  private final String artifactoryUrlSpark211;
  private final String artifactoryUrlSpark212;
  private final Set<SparkUDFInfo> sparkUDFInfos;
  private ScalaVersion scalaVersion;

  public TransportUDFTransformer(String hiveUDFClassName, String sparkUDFClassName, String artifactoryUrlSpark211,
      String artifactoryUrlSpark212, Set<SparkUDFInfo> sparkUDFInfos) {
    this.hiveUDFClassName = hiveUDFClassName;
    this.sparkUDFClassName = sparkUDFClassName;
    this.artifactoryUrlSpark211 = artifactoryUrlSpark211;
    this.artifactoryUrlSpark212 = artifactoryUrlSpark212;
    this.sparkUDFInfos = sparkUDFInfos;
  }

  private static final Logger LOG = LoggerFactory.getLogger(TransportUDFTransformer.class);
  public static final String DALI_UDFS_IVY_URL_SPARK_2_11 =
      "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:2.0.3?classifier=spark_2.11";
  public static final String DALI_UDFS_IVY_URL_SPARK_2_12 =
      "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:2.0.3?classifier=spark_2.12";

  public enum ScalaVersion {
    SCALA_2_11,
    SCALA_2_12
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    scalaVersion = getScalaVersionOfSpark();
    if (!(sqlCall.getOperator() instanceof VersionedSqlUserDefinedFunction)
        || !hiveUDFClassName.equalsIgnoreCase(sqlCall.getOperator().getName())) {
      return false;
    }
    if (scalaVersion == ScalaVersion.SCALA_2_11 && artifactoryUrlSpark211 != null
        || scalaVersion == ScalaVersion.SCALA_2_12 && artifactoryUrlSpark212 != null) {
      return true;
    } else {
      throw new UnsupportedUDFException(String.format(
          "Transport UDF for class '%s' is not supported for scala %s, please contact the UDF owner for upgrade",
          hiveUDFClassName, scalaVersion.toString()));
    }
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final VersionedSqlUserDefinedFunction operator = (VersionedSqlUserDefinedFunction) sqlCall.getOperator();
    final String viewDependentFunctionName = operator.getViewDependentFunctionName();
    sparkUDFInfos.add(new SparkUDFInfo(sparkUDFClassName, viewDependentFunctionName,
        Collections.singletonList(
            URI.create(scalaVersion == ScalaVersion.SCALA_2_11 ? artifactoryUrlSpark211 : artifactoryUrlSpark212)),
        SparkUDFInfo.UDFTYPE.TRANSPORTABLE_UDF));
    final SqlOperator convertedFunction =
        createSqlOperator(viewDependentFunctionName, operator.getReturnTypeInference());
    return convertedFunction.createCall(sqlCall.getParserPosition(), sqlCall.getOperandList());
  }

  public ScalaVersion getScalaVersionOfSpark() {
    try {
      String sparkVersion = SparkSession.active().version();
      if (sparkVersion.matches("2\\.[\\d\\.]*")) {
        return ScalaVersion.SCALA_2_11;
      } else if (sparkVersion.matches("3\\.[\\d\\.]*")) {
        return ScalaVersion.SCALA_2_12;
      } else {
        throw new IllegalStateException(String.format("Unsupported Spark Version %s", sparkVersion));
      }
    } catch (IllegalStateException | NoClassDefFoundError ex) {
      LOG.warn("Couldn't determine Spark version, falling back to scala_2.11: {}", ex.getMessage());
      return ScalaVersion.SCALA_2_11;
    }
  }
}

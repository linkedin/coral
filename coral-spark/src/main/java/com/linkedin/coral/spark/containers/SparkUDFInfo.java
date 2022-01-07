/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.containers;

import java.net.URI;
import java.util.List;
import java.util.Objects;


/**
 * This class aggregates all information required for registering a single UDF.
 *
 */
public class SparkUDFInfo {

  // need to distinguish different UDF types because we use different mechanisms
  // to register different UDF types.
  public enum UDFTYPE {
    HIVE_BUILTIN_FUNCTION,
    HIVE_CUSTOM_UDF,
    TRANSPORTABLE_UDF
  }

  private final String className;
  private final String functionName;
  private final List<URI> artifactoryUrls;
  private final UDFTYPE udfType;

  /**
   * @param className Class name of a Spark UDF
   *                  Example: com.linkedin.dali.udf.date.hive.DateFormatToEpoch
   * @param functionName  Function name of the Spark UDF to be registered. Usually it is the function name defined
   *                      in the function parameter in TBLPROPERTIES of a CREATE VIEW SQL statement.
   *                      Example: epochToDateFormat
   * @param artifactoryUrls list of Artifactory Urls to download all dependencies for this UDF.  Example:
   *            ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:1.0.2?classifier=spark
   * @param udfType  the type of the UDF to be registered.
   *                      Example: HIVE_CUSTOM_UDF
   *
   */
  public SparkUDFInfo(String className, String functionName, List<URI> artifactoryUrls, UDFTYPE udfType) {
    this.className = className;
    this.functionName = functionName;
    this.artifactoryUrls = artifactoryUrls;
    this.udfType = udfType;
  }

  /**
   * Get class name for the UDF implementation.
   *
   * @return  String  Class name of a Spark UDF
   *                  Example: com.linkedin.stdudfs.spark.daliudfs.EpochToDateFormatFunctionWrapper
   */
  public String getClassName() {
    return className;
  }

  /**
   * Get function name of the UDF, which will be needed while registering UDF.
   * Note: SQL retrieved from CoralSpark.getExpandedSQL() will contain this name.
   *
   * @return  String  Function name of the Spark UDF
   *                  Example: epochToDateFormat
   */
  public String getFunctionName() {
    return functionName;
  }

  /**
   * Artifactory Url to download all dependencies for this UDF.
   *
   * @return  URI  Artifactory Url to download all dependencies for this UDF
   *                  Example: ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs-spark:0.0.8
   */
  public List<URI> getArtifactoryUrls() {
    return artifactoryUrls;
  }

  /**
   * Get type of the UDF, which will be needed while registering UDF.
   * Note: Different UDF type will require different mechnism to register a UDF.
   *
   * @return  UDF_TYPE  the UDF type of the function
   *                  Example: HIVE_CUSTOM_UDF
   */
  public UDFTYPE getUdfType() {
    return udfType;
  }

  @Override
  public String toString() {
    return "SparkUDFInfo{" + "className='" + className + '\'' + ", functionName='" + functionName + '\''
        + ", artifactoryUrls='" + artifactoryUrls + '\'' + ", udfType='" + udfType + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SparkUDFInfo that = (SparkUDFInfo) o;
    return Objects.equals(className, that.className) && Objects.equals(functionName, that.functionName)
        && Objects.equals(artifactoryUrls, that.artifactoryUrls) && udfType == that.udfType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(className, functionName, artifactoryUrls, udfType);
  }
}

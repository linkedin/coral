package com.linkedin.coral.spark.containers;

import java.net.URI;


/**
 * This class aggregates all information required for registering a single UDF.
 *
 */
public class SparkUDFInfo {

  // need to distinguish different UDF types because we use different mechanisms
  // to register different UDF types.
  public enum UDFTYPE {
    HIVE_BUILTIN_FUNCTION, HIVE_CUSTOM_UDF, TRANSPORTABLE_UDF
  }

  private String className;
  private String functionName;
  private URI artifactoryUrl;
  private UDFTYPE udfType;

  /**
   * @param className Class name of a Spark UDF
   *                  Example: com.linkedin.dali.udf.date.hive.DateFormatToEpoch
   * @param functionName  Function name of the Spark UDF to be registered. Usually it is the function name defined
   *                      in the function parameter in TBLPROPERTIES of a CREATE VIEW SQL statement.
   *                      Example: epochToDateFormat
   * @param artifactoryUrl Artifactory Url to download all dependencies for this UDF.  Example:
   *            ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:1.0.2?classifier=spark
   * @param udfType  the type of the UDF to be registered.
   *                      Example: HIVE_CUSTOM_UDF
   *
   */
  public SparkUDFInfo(String className, String functionName, URI artifactoryUrl, UDFTYPE udfType) {
    this.className = className;
    this.functionName = functionName;
    this.artifactoryUrl = artifactoryUrl;
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
  public URI getArtifactoryUrl() {
    return artifactoryUrl;
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
        + ", artifactoryUrl='" + artifactoryUrl + '\'' + ", udfType='" + udfType + '\'' + '}';
  }
}

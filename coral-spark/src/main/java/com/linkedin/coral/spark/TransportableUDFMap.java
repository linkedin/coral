package com.linkedin.coral.spark;

import com.linkedin.coral.spark.containers.SparkUDFInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class contains static mapping from HiveUDF to an equivalent TransportableUDF
 *
 * Add new mappings here
 */
class TransportableUDFMap {

  private TransportableUDFMap() {
  }

  private static final Map<String, SparkUDFInfo> UDF_MAP = new HashMap();

  static {
    add("com.linkedin.coral.hive.hive2rel.CoralTestUDF",
        "coralTestUDF",
        "com.linkedin.coral.spark.CoralTestUDF",
        "ivy://com.linkedin.coral.spark.CoralTestUDF");

    add("com.linkedin.dali.udf.date.hive.EpochToDateFormat",
        "epochToDateFormat",
        "com.linkedin.stdudfs.spark.daliudfs.EpochToDateFormatFunctionWrapper",
        "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs-spark:0.0.8");

    add("com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds",
        "epochToEpochMilliseconds",
        "com.linkedin.stdudfs.spark.daliudfs.EpochToEpochMillisecondsFunctionWrapper",
        "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs-spark:0.0.8");

    add("com.linkedin.dali.udf.date.hive.DateFormatToEpoch",
        "dateFormatToEpochFunctionWrapper",
        "com.linkedin.stdudfs.spark.daliudfs.DateFormatToEpochFunctionWrapper",
        "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs-spark:0.0.8");
  }

  /**
   * Returns Optional of SparkUDFInfo for a given Hive UDF classname, if it is present in the static mapping, UDF_MAP.
   * Otherwise returns Optional<Null>.
   *
   * @return Optional<SparkUDFInfo>
   */
  static Optional<SparkUDFInfo> lookup(String className) {
    return Optional.ofNullable(UDF_MAP.get(className));
  }

  private static void add(String className, String sparkFunctionName, String sparkClassName, String artifcatoryUrl) {
    try {
      URI url = new URI(artifcatoryUrl);
      UDF_MAP.put(className, new SparkUDFInfo(sparkClassName, sparkFunctionName, url));
    } catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Artifactory URL is malformed %s", artifcatoryUrl), e);
    }
  }

}

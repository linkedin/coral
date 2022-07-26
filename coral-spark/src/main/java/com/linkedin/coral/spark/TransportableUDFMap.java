/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.exceptions.UnsupportedUDFException;


/**
 * This class contains static mapping from legacy Dali Hive UDFs to an equivalent Transportable UDF.
 * This class also contains those UDFs that are already defined using Transport UDF.
 * sparkClassName points to a Spark native class in the corresponding spark jar file.
 *
 * Add new mappings here
 */
class TransportableUDFMap {

  private TransportableUDFMap() {
  }

  private static final Logger LOG = LoggerFactory.getLogger(TransportableUDFMap.class);
  private static final Map<String, Map<ScalaVersion, SparkUDFInfo>> UDF_MAP = new HashMap<>();
  public static final String DALI_UDFS_IVY_URL_SPARK_2_11 =
      "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:2.0.3?classifier=spark_2.11";
  public static final String DALI_UDFS_IVY_URL_SPARK_2_12 =
      "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:2.0.3?classifier=spark_2.12";

  enum ScalaVersion {
    SCALA_2_11,
    SCALA_2_12
  }

  static {

    // The following UDFs are the legacy Hive UDF. Since they have been converted to
    // Transport UDF, we point their class files to the corresponding Spark jar.
    add("com.linkedin.dali.udf.date.hive.DateFormatToEpoch", "com.linkedin.stdudfs.daliudfs.spark.DateFormatToEpoch",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.dali.udf.date.hive.EpochToDateFormat", "com.linkedin.stdudfs.daliudfs.spark.EpochToDateFormat",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds",
        "com.linkedin.stdudfs.daliudfs.spark.EpochToEpochMilliseconds", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.dali.udf.isguestmemberid.hive.IsGuestMemberId",
        "com.linkedin.stdudfs.daliudfs.spark.IsGuestMemberId", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    // add the transportudf spark version for lookup UDF
    add("com.linkedin.dali.udf.istestmemberid.hive.IsTestMemberId",
        "com.linkedin.stdudfs.daliudfs.spark.IsTestMemberId", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.dali.udf.maplookup.hive.MapLookup", "com.linkedin.stdudfs.daliudfs.spark.MapLookup",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.dali.udf.sanitize.hive.Sanitize", "com.linkedin.stdudfs.daliudfs.spark.Sanitize",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    // add the transportudf spark version for lookup UDF
    add("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup",
        "com.linkedin.stdudfs.daliudfs.spark.WatBotCrawlerLookup", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    // The following UDFs are already defined using Transport UDF.
    // The class name is the corresponding Hive UDF.
    // We point their class files to the corresponding Spark jar file.
    add("com.linkedin.stdudfs.daliudfs.hive.DateFormatToEpoch", "com.linkedin.stdudfs.daliudfs.spark.DateFormatToEpoch",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.EpochToDateFormat", "com.linkedin.stdudfs.daliudfs.spark.EpochToDateFormat",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.EpochToEpochMilliseconds",
        "com.linkedin.stdudfs.daliudfs.spark.EpochToEpochMilliseconds", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.GetProfileSections",
        "com.linkedin.stdudfs.daliudfs.spark.GetProfileSections", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.stringudfs.hive.InitCap", "com.linkedin.stdudfs.stringudfs.spark.InitCap",
        "ivy://com.linkedin.standard-udfs-common-sql-udfs:standard-udfs-string-udfs:1.0.1?classifier=spark_2.11",
        "ivy://com.linkedin.standard-udfs-common-sql-udfs:standard-udfs-string-udfs:1.0.1?classifier=spark_2.12");

    add("com.linkedin.stdudfs.daliudfs.hive.IsGuestMemberId", "com.linkedin.stdudfs.daliudfs.spark.IsGuestMemberId",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.IsTestMemberId", "com.linkedin.stdudfs.daliudfs.spark.IsTestMemberId",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.MapLookup", "com.linkedin.stdudfs.daliudfs.spark.MapLookup",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.PortalLookup", "com.linkedin.stdudfs.daliudfs.spark.PortalLookup",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.daliudfs.hive.Sanitize", "com.linkedin.stdudfs.daliudfs.spark.Sanitize",
        DALI_UDFS_IVY_URL_SPARK_2_11, DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.stdudfs.userinterfacelookup.hive.UserInterfaceLookup",
        "com.linkedin.stdudfs.userinterfacelookup.spark.UserInterfaceLookup",
        "ivy://com.linkedin.standard-udf-userinterfacelookup:userinterfacelookup-std-udf:0.0.27?classifier=spark_2.11",
        "ivy://com.linkedin.standard-udf-userinterfacelookup:userinterfacelookup-std-udf:0.0.27?classifier=spark_2.12");

    add("com.linkedin.stdudfs.daliudfs.hive.WatBotCrawlerLookup",
        "com.linkedin.stdudfs.daliudfs.spark.WatBotCrawlerLookup", DALI_UDFS_IVY_URL_SPARK_2_11,
        DALI_UDFS_IVY_URL_SPARK_2_12);

    add("com.linkedin.jemslookup.udf.hive.JemsLookup", "com.linkedin.jemslookup.udf.spark.JemsLookup",
        "ivy://com.linkedin.jobs-udf:jems-udfs:2.1.7?classifier=spark_2.11",
        "ivy://com.linkedin.jobs-udf:jems-udfs:2.1.7?classifier=spark_2.12");

    add("com.linkedin.stdudfs.parsing.hive.UserAgentParser", "com.linkedin.stdudfs.parsing.spark.UserAgentParser",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:3.0.3?classifier=spark_2.11",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:3.0.3?classifier=spark_2.12");

    add("com.linkedin.stdudfs.parsing.hive.Ip2Str", "com.linkedin.stdudfs.parsing.spark.Ip2Str",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:3.0.3?classifier=spark_2.11",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:3.0.3?classifier=spark_2.12");

    add("com.linkedin.stdudfs.lookup.hive.BrowserLookup", "com.linkedin.stdudfs.lookup.spark.BrowserLookup",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:3.0.3?classifier=spark_2.11",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:3.0.3?classifier=spark_2.12");

    add("com.linkedin.jobs.udf.hive.ConvertIndustryCode", "com.linkedin.jobs.udf.spark.ConvertIndustryCode",
        "ivy://com.linkedin.jobs-udf:jobs-udfs:2.1.6?classifier=spark_2.11",
        "ivy://com.linkedin.jobs-udf:jobs-udfs:2.1.6?classifier=spark_2.12");
  }

  /**
   * Returns Optional of SparkUDFInfo for a given Hive UDF classname, if it is present in the static mapping, UDF_MAP.
   * Otherwise returns Optional<Null>.
   *
   * @return Optional<SparkUDFInfo>
   */
  static Optional<SparkUDFInfo> lookup(String className) {
    ScalaVersion scalaVersion = getScalaVersion();
    return Optional.ofNullable(UDF_MAP.get(className)).map(scalaMap -> Optional.ofNullable(scalaMap.get(scalaVersion))
        .<UnsupportedUDFException> orElseThrow(() -> new UnsupportedUDFException(String.format(
            "Transport UDF for class '%s' is not supported for scala %s, please contact " + "the UDF owner for upgrade",
            className, scalaVersion.toString()))));
  }

  public static void add(String className, String sparkClassName, String artifactoryUrlSpark211,
      String artifactoryUrlSpark212) {
    Map scalaToTransportUdfMap = new HashMap<ScalaVersion, SparkUDFInfo>() {
      {
        put(ScalaVersion.SCALA_2_11, artifactoryUrlSpark211 == null ? null : new SparkUDFInfo(sparkClassName, null,
            Collections.singletonList(URI.create(artifactoryUrlSpark211)), SparkUDFInfo.UDFTYPE.TRANSPORTABLE_UDF));
        put(ScalaVersion.SCALA_2_12, artifactoryUrlSpark212 == null ? null : new SparkUDFInfo(sparkClassName, null,
            Collections.singletonList(URI.create(artifactoryUrlSpark212)), SparkUDFInfo.UDFTYPE.TRANSPORTABLE_UDF));
      }
    };
    UDF_MAP.put(className, scalaToTransportUdfMap);
  }

  static ScalaVersion getScalaVersion() {
    try {
      String sparkVersion = SparkSession.active().version();
      if (sparkVersion.matches("2\\.[\\d\\.]*"))
        return ScalaVersion.SCALA_2_11;
      if (sparkVersion.matches("3\\.[\\d\\.]*"))
        return ScalaVersion.SCALA_2_12;
      throw new IllegalStateException(String.format("Unsupported Spark Version %s", sparkVersion));
    } catch (IllegalStateException | NoClassDefFoundError ex) {
      LOG.warn("Couldn't determine Spark version, falling back to scala_2.11: {}", ex.getMessage());
      return ScalaVersion.SCALA_2_11;
    }
  }

}

/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import com.linkedin.coral.spark.containers.SparkUDFInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

  private static final Map<String, SparkUDFInfo> UDF_MAP = new HashMap();
  public static final String STANDARD_UDFS_DALI_UDFS_URL =
      "ivy://com.linkedin.standard-udfs-dali-udfs:standard-udfs-dali-udfs:1.0.4?classifier=spark";

  static {

    // LIHADOOP-48502: The following UDFs are the legacy Hive UDF. Since they have been converted to
    // Transport UDF, we point their class files to the corresponding Spark jar.
    add("com.linkedin.dali.udf.date.hive.DateFormatToEpoch",
        "dateFormatToEpoch",
        "com.linkedin.stdudfs.daliudfs.spark.DateFormatToEpoch",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.dali.udf.date.hive.EpochToDateFormat",
        "epochToDateFormat",
        "com.linkedin.stdudfs.daliudfs.spark.EpochToDateFormat",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds",
        "epochToEpochMilliseconds",
        "com.linkedin.stdudfs.daliudfs.spark.EpochToEpochMilliseconds",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.dali.udf.isguestmemberid.hive.IsGuestMemberId",
        "isGuestMemberId",
        "com.linkedin.stdudfs.daliudfs.spark.IsGuestMemberId",
        STANDARD_UDFS_DALI_UDFS_URL);

    // LIHADOOP-49851 add the transportudf spark version for lookup UDF
    add("com.linkedin.dali.udf.istestmemberid.hive.IsTestMemberId",
        "isTestMemberId",
        "com.linkedin.stdudfs.daliudfs.spark.IsTestMemberId",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.dali.udf.maplookup.hive.MapLookup",
        "mapLookup",
        "com.linkedin.stdudfs.daliudfs.spark.MapLookup",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.dali.udf.sanitize.hive.Sanitize",
        "sanitize",
        "com.linkedin.stdudfs.daliudfs.spark.Sanitize",
        STANDARD_UDFS_DALI_UDFS_URL);

    // LIHADOOP-49851 add the transportudf spark version for lookup UDF
    add("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup",
        "watBotCrawlerLookup",
        "com.linkedin.stdudfs.daliudfs.spark.WatBotCrawlerLookup",
        STANDARD_UDFS_DALI_UDFS_URL);

    // LIHADOOP-48502: The following UDFs are already defined using Transport UDF.
    // The class name is the corresponding Hive UDF.
    // We point their class files to the corresponding Spark jar file.
    add("com.linkedin.stdudfs.daliudfs.hive.DateFormatToEpoch",
        "dateFormatToEpoch",
        "com.linkedin.stdudfs.daliudfs.spark.DateFormatToEpoch",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.EpochToDateFormat",
        "epochToDateFormat",
        "com.linkedin.stdudfs.daliudfs.spark.EpochToDateFormat",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.EpochToEpochMilliseconds",
        "epochToEpochMilliseconds",
        "com.linkedin.stdudfs.daliudfs.spark.EpochToEpochMilliseconds",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.GetProfileSections",
        "getProfileSections",
        "com.linkedin.stdudfs.daliudfs.spark.GetProfileSections",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.stringudfs.hive.InitCap",
        "initCap",
        "com.linkedin.stdudfs.stringudfs.spark.InitCap",
        "ivy://com.linkedin.standard-udfs-common-sql-udfs:standard-udfs-string-udfs:0.0.7?classifier=spark");

    add("com.linkedin.stdudfs.daliudfs.hive.IsGuestMemberId",
        "isGuestMemberId",
        "com.linkedin.stdudfs.daliudfs.spark.IsGuestMemberId",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.IsTestMemberId",
        "isTestMemberId",
        "com.linkedin.stdudfs.daliudfs.spark.IsTestMemberId",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.MapLookup",
        "mapLookup",
        "com.linkedin.stdudfs.daliudfs.spark.MapLookup",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.PortalLookup",
        "portalLookup",
        "com.linkedin.stdudfs.daliudfs.spark.PortalLookup",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.daliudfs.hive.Sanitize",
        "sanitize",
        "com.linkedin.stdudfs.daliudfs.spark.Sanitize",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.stdudfs.userinterfacelookup.hive.UserInterfaceLookup",
        "userInterfaceLookup",
        "com.linkedin.stdudfs.userinterfacelookup.spark.UserInterfaceLookup",
        "ivy://com.linkedin.standard-udf-userinterfacelookup:userinterfacelookup-std-udf:0.0.9?classifier=spark");

    add("com.linkedin.stdudfs.daliudfs.hive.WatBotCrawlerLookup",
        "watBotCrawlerLookup",
        "com.linkedin.stdudfs.daliudfs.spark.WatBotCrawlerLookup",
        STANDARD_UDFS_DALI_UDFS_URL);

    add("com.linkedin.jemslookup.udf.hive.JemsLookup",
        "jemsLookup",
        "com.linkedin.jemslookup.udf.spark.JemsLookup",
        "ivy://com.linkedin.jobs-udf:jems-udfs:0.0.1?classifier=spark");

    add("com.linkedin.stdudfs.parsing.hive.UserAgentParser",
        "userAgentParser",
        "com.linkedin.stdudfs.parsing.spark.UserAgentParser",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:2.0.1?classifier=spark");

    add("com.linkedin.stdudfs.parsing.hive.Ip2Str",
        "ip2Str",
        "com.linkedin.stdudfs.parsing.spark.Ip2Str",
        "ivy://com.linkedin.standard-udfs-parsing:parsing-stdudfs:2.0.1?classifier=spark");
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

  public static void add(String className, String sparkFunctionName, String sparkClassName, String artifcatoryUrl) {
    try {
      URI url = new URI(artifcatoryUrl);
      List<URI> listOfUris = new LinkedList<>();
      listOfUris.add(url);

      UDF_MAP.put(className, new SparkUDFInfo(sparkClassName, sparkFunctionName, listOfUris,
          SparkUDFInfo.UDFTYPE.TRANSPORTABLE_UDF));
    } catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Artifactory URL is malformed %s", artifcatoryUrl), e);
    }
  }

}

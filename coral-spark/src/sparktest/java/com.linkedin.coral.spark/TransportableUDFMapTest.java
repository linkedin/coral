/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import org.apache.spark.sql.SparkSession;
import org.testng.Assert;
import org.testng.annotations.Test;


public class TransportableUDFMapTest {

  @Test
  public void testScalaVersionWithSparkSession() {
    SparkSession ss = SparkSession.builder().appName(TransportableUDFMapTest.class.getSimpleName()).master("local[1]")
        .enableHiveSupport().getOrCreate();
    Assert.assertEquals(TransportableUDFMap.getScalaVersion(), TransportableUDFMap.ScalaVersion.SCALA_2_11);
    ss.close();
  }

  @Test
  public void testDefaultScalaVersion() {
    // If SparkSession is not active, getScalaVersion should return Scala2.11
    Assert.assertEquals(TransportableUDFMap.getScalaVersion(), TransportableUDFMap.ScalaVersion.SCALA_2_11);
  }

  @Test
  public void testLookupWithSparkSession() {
    SparkSession ss = SparkSession.builder().appName(TransportableUDFMapTest.class.getSimpleName()).master("local[1]")
        .enableHiveSupport().getOrCreate();
    String stdDaliUdf = "com.linkedin.dali.udf.date.hive.EpochToDateFormat";
    Assert.assertTrue(TransportableUDFMap.lookup(stdDaliUdf).get().getArtifactoryUrls().get(0).toString()
        .contains("classifier=spark_2.11"));
    ss.close();
  }

  @Test
  public void testDefaultLookup() {
    // If SparkSession is not active, lookup should return Scala2.11
    String stdDaliUdf = "com.linkedin.dali.udf.date.hive.EpochToDateFormat";
    Assert.assertTrue(TransportableUDFMap.lookup(stdDaliUdf).get().getArtifactoryUrls().get(0).toString()
        .contains("classifier=spark_2.11"));
  }

  @Test
  public void testLookupDoesNotExist() {
    String stdClassName = "com.linkedin.dali.udf.date.hive.UdfDoesNotExist";
    Assert.assertFalse(TransportableUDFMap.lookup(stdClassName).isPresent());
  }

  @Test
  public void testLookupExistButNotReadyForSpark3() {
    SparkSession ss = SparkSession.builder().appName(TransportableUDFMapTest.class.getSimpleName()).master("local[1]")
        .enableHiveSupport().getOrCreate();
    String stdClassName = "com.transport.test.hive.Spark3Fail";
    TransportableUDFMap.add(stdClassName, "com.transport.test.spark.Spark3Fail",
        "com.transport:spark-udf:0.0.0?classifier=spark_2.11", null);
    assert TransportableUDFMap.lookup(stdClassName).get().getArtifactoryUrls().get(0).toString()
        .contains("classifier=spark_2.11");
    ss.close();
  }
}

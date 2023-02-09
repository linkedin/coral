/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.HashSet;

import org.apache.spark.sql.SparkSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.linkedin.coral.spark.transformers.TransportUDFTransformer;


public class TransportUDFTransformerTest {
  final TransportUDFTransformer _transportUDFTransformer = new TransportUDFTransformer(
      "com.linkedin.coral.hive.hive2rel.CoralTestUDF", "com.linkedin.coral.spark.CoralTestUDF",
      "ivy://com.linkedin.coral.spark.CoralTestUDF", null, new HashSet<>());

  @Test
  public void testScalaVersionWithSparkSession() {
    SparkSession ss = SparkSession.builder().appName(TransportUDFTransformerTest.class.getSimpleName())
        .master("local[1]").enableHiveSupport().getOrCreate();
    Assert.assertEquals(_transportUDFTransformer.getScalaVersionOfSpark(),
        TransportUDFTransformer.ScalaVersion.SCALA_2_12);
    ss.close();
  }

  @Test
  public void testDefaultScalaVersion() {
    // If SparkSession is not active, getScalaVersion should return Scala2.11
    Assert.assertEquals(_transportUDFTransformer.getScalaVersionOfSpark(),
        TransportUDFTransformer.ScalaVersion.SCALA_2_11);
  }
}

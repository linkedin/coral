/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;


public class TestAggregate {

  @Test
  public void testDistinct() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("DistinctTestApplication", "distinct_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("testStream"));
    String script = ""
        + "inputData = load 'dummy' as (intCol:int, doubleCol:double, stringCol:chararray);\n"
        + "aggregate = distinct inputData;\n"
        + "store aggregate into 'output';";
    testBeamCodeGen(config, script);
  }

  @Test
  public void testAggregate() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("AggregateTestApplication", "aggregate_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("testStream"));
    String script = ""
        + "inputData = load 'dummy' as (strGroupCol:chararray, longGroupCol:long, intCol:int, longCol:long, doubleCol:double, stringCol:chararray);\n"
        + "group1 = group inputData by (strGroupCol, longGroupCol);\n"
        + "aggregate1 = foreach group1 generate\n"
        + "                group.strGroupCol,\n"
        + "                group.longGroupCol,\n"
        + "                MAX(inputData.longCol) as maxLong,\n"
        + "                MAX(inputData.intCol) as maxInt,\n"
        + "                MAX(inputData.doubleCol) as maxDouble,\n"
        + "                MAX(inputData.stringCol) as maxString,\n"
        + "                MIN(inputData.longCol) as minLong,\n"
        + "                MIN(inputData.intCol) as minInt,\n"
        + "                MIN(inputData.doubleCol) as minDouble,\n"
        + "                MIN(inputData.stringCol) as minString,\n"
        + "                SUM(inputData.longCol) as sumLong,\n"
        + "                SUM(inputData.intCol) as sumInt,\n"
        + "                SUM(inputData.doubleCol) as sumDouble,\n"
        + "                COUNT(inputData.longCol) as countLong,\n"
        + "                COUNT(inputData) as countStar,\n"
        + "                inputData.longCol as longMultiset;\n"
        + "store aggregate1 into 'output';";
    testBeamCodeGen(config, script);
  }
}

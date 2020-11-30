/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;


public class TestJoin {

  @Test
  public void testStreamStreamJoin() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("JoinTestApplication", "ss_join_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("joinStream"));
    config.tableToInputStreams.put("dummy2", new CalciteBeamConfig.StreamConfig("joinStream2"));
    String script = ""
        + "inputData1 = load 'dummy' as (intCol:int, doubleCol:double, stringCol:chararray);\n"
        + "inputData1 = foreach inputData1 generate intCol, stringCol, doubleCol;\n"
        + "inputData2 = load 'dummy2' as (intCol:int, longCol:long, stringCol:chararray);\n"
        + "inputData2 = foreach inputData2 generate intCol, stringCol, longCol;\n"
        + "final = JOIN inputData1 by (intCol, stringCol) LEFT OUTER, inputData2 by (intCol, stringCol);\n"
        + "store final into 'output';";
    testBeamCodeGen(config, script);
  }
}

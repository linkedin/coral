/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;


public class TestUnion {

  @Test
  public void testUnion() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("UnionTestApplication", "union_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("unionStream"));
    config.tableToInputStreams.put("dummy2", new CalciteBeamConfig.StreamConfig("unionStream2"));
    config.tableToInputStreams.put("dummy3", new CalciteBeamConfig.StreamConfig("unionStream3"));
    String script = ""
        + "inputData = load 'dummy' as (intCol:int, doubleCol:double, stringCol:chararray);\n"
        + "child1 = foreach inputData generate\n"
        + "             intCol as intCol,\n"
        + "             stringCol as stringCol;\n"
        + "inputData2 = load 'dummy2' as (intCol:int, longCol:long, stringCol:chararray);\n"
        + "child2 = foreach inputData2 generate\n"
        + "             intCol + 1 as intCol,\n"
        + "             stringCol as stringCol;\n"
        + "inputData3 = load 'dummy3' as (intCol:int, booleanCol:boolean, stringCol:chararray);\n"
        + "child3 = foreach inputData3 generate\n"
        + "             intCol + 2 as intCol,\n"
        + "             stringCol as stringCol;\n"
        + "final = UNION child1, child2, child3;\n"
        + "store final into 'output';";
    testBeamCodeGen(config, script);
  }
}

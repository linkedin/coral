/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;


public class TestArrayFlatten {

  @Test
  public void testFlattenOneColumn() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("ArrayFlatten1CTestApplication", "array_flatten1_output");
    config.tableToInputStreams.put("dummy_array", new CalciteBeamConfig.StreamConfig("arrayFlattenStream"));
    String script = ""
        + "inputData = load 'dummy_array' as (key:chararray, intCol:int, longArray:{(elem:long)});\n"
        + "flatten1 = foreach inputData generate\n"
        + "             key,\n"
        + "             flatten(longArray) as longCol;\n"
        + "store flatten1 into 'output';";
    testBeamCodeGen(config, script);
  }

  @Test
  public void testFlattenTwoColumns() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("ArrayFlatten2CTestApplication", "array_flatten1_output");
    config.tableToInputStreams.put("dummy_array", new CalciteBeamConfig.StreamConfig("arrayFlattenStream"));
    String script = ""
        + "inputData = load 'dummy_array' as (key:chararray, intCol:int, longArray:{(lElem:long)}, doubleArray:{(dElem:double)});\n"
        + "flatten2 = foreach inputData generate\n"
        + "             key,\n"
        + "             flatten(longArray) as longCol,\n"
        + "             flatten(doubleArray) as doubleCol;\n"
        + "store flatten2 into 'output';";
    testBeamCodeGen(config, script);
  }
}

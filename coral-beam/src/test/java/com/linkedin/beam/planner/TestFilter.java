/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;


public class TestFilter {

  @Test
  public void testFilter() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("FilterTestApplication", "filter_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("testStream"));
    String script = ""
        + "inputData = load 'dummy' as (intCol:int, doubleCol:double, stringCol:chararray);\n"
        + "filterData = FILTER inputData BY intCol > 2 AND (doubleCol * doubleCol < 1.8 OR stringCol == 'test');\n"
        + "store filterData into 'output';";
    testBeamCodeGen(config, script);
  }
}

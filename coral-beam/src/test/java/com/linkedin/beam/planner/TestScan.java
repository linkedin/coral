/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;
import static org.testng.Assert.*;


public class TestScan {
  private final String SCRIPT = ""
      + "inputData = load 'dummy' as (a:int, longCol:long, stringCol:chararray);\n"
      + "store inputData into 'output';";

  @Test
  public void testScan() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("ScanTestApplication", "scan_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("testStream", ImmutableList.of("longCol")));
    testBeamCodeGen(config, SCRIPT);
  }

  @Test
  public void testInvalidTimeSchema() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("ScanTestApplication", "scan_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("testStream",
        ImmutableList.of("invalidTimeColumn")));
    try {
      testBeamCodeGen(config, SCRIPT);
      fail("Codegen should fail on invalid time column");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Invalid schema for time field"));
    }
  }

  @Test
  public void testInvalidTimeType() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("ScanTestApplication", "scan_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("testStream",
        ImmutableList.of("stringCol")));
    try {
      testBeamCodeGen(config, SCRIPT);
      fail("Codegen should fail on invalid time column");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Invalid type for time column"));
    }
  }
}

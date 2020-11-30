/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.linkedin.beam.planner.CodegenTestBase.*;


public class TestProject {

  @Test
  public void testProject() throws Exception {
    CalciteBeamConfig config = makeBeamConfig("ProjectTestApplication", "project_output");
    config.tableToInputStreams.put("dummy", new CalciteBeamConfig.StreamConfig("projectStream",
        ImmutableList.of("longCol")));
    String SCRIPT =
        "" + "inputData = load 'dummy' as (intCol:int, longCol:long, doubleCol:double, stringCol:chararray);\n"
            + "project = foreach inputData generate\n"
            + "             (chararray) intCol as intStringCol,\n"
            + "             (chararray) longCol as longStringCol,\n"
            + "             (chararray) doubleCol as doubleStringCol,\n"
            + "             (chararray) stringCol as stringStringCol,\n"
            + "             (chararray) 'abc' as constStringString,\n"
            + "             (chararray) 1L as constLongString,\n"
            + "             (long) intCol as intLongCol,\n"
            + "             (long) (longCol + 2) as longLongCol,\n"
            + "             (long) doubleCol as doubleLongCol,\n"
            + "             (long) stringCol as stringLongCol,\n"
            + "             (long) 3L as constLongLong,\n"
            + "             (long) '4' as constStringLong,\n"
            + "             (int) (intCol + 5) as intIntCol,\n"
            + "             (int) longCol as longIntCol,\n"
            + "             (int) doubleCol as doubleIntCol,\n"
            + "             (int) stringCol as stringIntCol,\n"
            + "             (int) 6 as constIntInt,\n"
            + "             (int) '7' as constStringInt,\n"
            + "             (double) intCol as intDoubleCol,\n"
            + "             (double) longCol as longDoubleCol,\n"
            + "             (double) doubleCol as doubleDoubleCol,\n"
            + "             (double) stringCol as stringDoubleCol,\n"
            + "             (double) 9 as intConstDoubleCol,\n"
            + "             (double) '10.5' as stringContsDoubleCol,\n"
            + "             (1, 'a') as recordCol,\n"
            + "             {(1, 'a'), (2, 'b')} as bagCol,"
            + "             ENDSWITH(stringCol, 'foo') as udfCol;\n"
            + "store project into 'output';";

    testBeamCodeGen(config, SCRIPT);
  }
}

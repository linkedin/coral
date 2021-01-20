/**
 * Copyright 2018-2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.hadoop.hive.ql.exec.UDF;


// This is used in TestUtils to set up as dali function
//
// It is necessary to have this class in the classpath during test runtime
// for Hive to successfully register this UDF, ie. to run CREATE FUNCTION
// query in TestUtils.
public class CoralTestUnsupportedUDF extends UDF {
  public boolean evaluate(int input) {
    return input < 100;
  }
}

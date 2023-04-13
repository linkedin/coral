/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.catalog;

import org.apache.hadoop.hive.ql.exec.UDF;


// This is used in TestUtils to set up as dali function
// This needs in a separate file for Hive to correctly load for setup
public class CoralTestUDF extends UDF {
  public boolean evaluate(int input) {
    return input < 100;
  }
}

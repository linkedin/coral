/**
 * Copyright 2018-2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.hadoop.hive.ql.exec.UDF;


// This is used in TestUtils to set up as dali function
// This needs in a separate file for Hive to correctly load for setup
public class CoralTestUDF3 extends UDF {
  public int evaluate(int input) {
    return (input * input);
  }
}

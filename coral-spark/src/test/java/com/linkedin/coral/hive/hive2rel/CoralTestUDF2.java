package com.linkedin.coral.hive.hive2rel;

import org.apache.hadoop.hive.ql.exec.UDF;

// This is used in TestUtils to set up as dali function
// This needs in a separate file for Hive to correctly load for setup
public class CoralTestUDF2 extends UDF {
  public boolean evaluate(int input) {
    return input > 100;
  }
}

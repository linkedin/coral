package com.linkedin.coral.hive.hive2rel;

import org.apache.hadoop.hive.ql.exec.UDF;

// This is used in TestUtils to set up as dali function
// This needs in a separate file for Hive to correctly load for setup
public class CoralTestUdfSquare extends UDF {
  public int evaluate(int input) {
    return (input * input);
  }
}

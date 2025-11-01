package com.linkedin.coral.spark;

import org.apache.hadoop.hive.ql.exec.UDF;


// This is used in CoralSparkViewCatalogTest to set up as dali function
// This needs in a separate file for Hive to correctly load for setup
public class CoralTestUDF extends UDF {
  public boolean evaluate(int input) {
    return input < 100;
  }
}
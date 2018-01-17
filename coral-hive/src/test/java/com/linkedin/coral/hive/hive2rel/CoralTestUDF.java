package com.linkedin.coral.hive.hive2rel;

import org.apache.hadoop.hive.ql.exec.UDF;


public class CoralTestUDF extends UDF {
  public boolean evaluate(int input) {
    return input < 100;
  }
}

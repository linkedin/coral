/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

// This is used in TestUtils to set up as dali function
// This needs in a separate file for Hive to correctly load for setup


/**
 * CoralTestUDTF outputs the number of rows seen, twice. It's output twice
 * to test outputting of rows on close with lateral view.
 */
public class CoralTestUDTF extends GenericUDTF {

  Integer count = 0;
  Object[] forwardObj = new Object[1];

  @Override
  public void close() throws HiveException {
    forwardObj[0] = count;
    forward(forwardObj);
    forward(forwardObj);
  }

  @Override
  public StructObjectInspector initialize(StructObjectInspector argOIs) {
    ArrayList<String> fieldNames = new ArrayList<>();
    ArrayList<ObjectInspector> fieldOIs = new ArrayList<>();
    fieldNames.add("col1");
    fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
    return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
  }

  @Override
  public void process(Object[] args) {
    count++;
  }
}

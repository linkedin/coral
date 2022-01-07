/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;


// This is used in TestUtils to set up as dali function
// This needs in a separate file for Hive to correctly load for setup
public class CoralTestUDFReturnStruct extends GenericUDF {
  private IntObjectInspector inputInspector;

  @Override
  public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
    if (!(arguments[0] instanceof IntObjectInspector)) {
      throw new UDFArgumentException();
    }

    inputInspector = (IntObjectInspector) arguments[0];

    List<String> fieldNames = new ArrayList<String>(2);
    fieldNames.add("isEven");
    fieldNames.add("number");

    List<ObjectInspector> fieldTypes = new ArrayList<>(2);
    fieldTypes.add(PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);
    fieldTypes.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);

    return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldTypes);
  }

  @Override
  public Object evaluate(DeferredObject[] arguments) throws HiveException {
    int input = inputInspector.get(arguments[0].get());
    Object[] result = new Object[2];
    result[0] = (input % 2 == 0);
    result[1] = input;

    return result;
  }

  @Override
  public String getDisplayString(String[] children) {
    return "CoralTestUDFReturnStruct";
  }
}

/**
 * Copyright 2021-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.*;


@Description(name = "return_second_arg_struct_udf",
    value = "_FUNC_(string, struct) - Returns the second argument (struct) as-is")
public class CoralTestUDFReturnSecondArg extends GenericUDF {

  private transient ObjectInspector stringOI;
  private transient StructObjectInspector structOI;

  @Override
  public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
    // Check the number of arguments
    if (arguments.length != 2) {
      throw new UDFArgumentLengthException(
          "return_struct_udf() requires exactly two arguments: a string and a struct.");
    }

    // Validate the first argument (string)
    if (arguments[0].getCategory() != ObjectInspector.Category.PRIMITIVE || ((PrimitiveObjectInspector) arguments[0])
        .getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
      throw new UDFArgumentException("The first argument must be a string.");
    }

    // Validate the second argument (struct)
    if (arguments[1].getCategory() != ObjectInspector.Category.STRUCT) {
      throw new UDFArgumentException("The second argument must be a struct.");
    }

    // Initialize ObjectInspectors
    stringOI = arguments[0];
    structOI = (StructObjectInspector) arguments[1];

    // Return the ObjectInspector for the struct (second argument)
    return structOI;
  }

  @Override
  public Object evaluate(DeferredObject[] arguments) throws HiveException {
    // Simply return the second argument as-is
    Object structObj = arguments[1].get();
    return structObj;
  }

  @Override
  public String getDisplayString(String[] children) {
    return "return_struct_udf(" + children[0] + ", " + children[1] + ")";
  }
}

/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

import java.lang.reflect.Method;
import java.util.Objects;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.piglet.PigRelSqlUdfs;


public class Methods {
  private Methods() {
  }

  public static final Method P_COLLECTION_SET_CODER = Types.lookupMethod(PCollection.class, "setCoder", Coder.class);
  public static final Method P_COLLECTION_APPLY = Types.lookupMethod(PCollection.class, "apply", PTransform.class);
  public static final Method AVRO_PUT = Types.lookupMethod(GenericRecord.class, "put", String.class, Object.class);
  public static final Method OBJECTS_TO_STRING = Types.lookupMethod(Objects.class, "toString", Object.class, String.class);
  public static final Method BUILD_TUPLE = Types.lookupMethod(PigRelSqlUdfs.class, "buildTuple", Object.class);
  public static final Method BUILD_BAG = Types.lookupMethod(PigRelSqlUdfs.class, "buildBag", Object.class);
  public static final Method AVRO_ARRAY_ADD = Types.lookupMethod(GenericArray.class, "add", Object.class);

}
/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

public class MethodNames {
  private MethodNames() {
  }

  // Beam methods
  public static final String APPLY = "apply";
  public static final String INTO = "into";
  public static final String VIA = "via";
  public static final String BY = "by";
  public static final String AND = "and";
  public static final String WITH = "with";
  public static final String WITH_IN = "within";
  public static final String WITH_NAME = "withName";
  public static final String WITH_TOPIC = "withTopic";
  public static final String WITH_KEY_TYPE = "withKeyType";
  public static final String WITH_TIME_FUNCTION = "withTimeFunction";
  public static final String WITH_TIMESTAMP_FN = "withTimestampFn";
  public static final String WITH_STORE_TYPE = "withStoreType";
  public static final String WITHOUT_REPARTITION = "withoutRepartition";
  public static final String WITHOUT_METADATA = "withoutMetadata";
  public static final String PER_KEY = "perKey";
  public static final String STRINGS = "strings";
  public static final String CREATE = "create";
  public static final String RUN = "run";
  public static final String P_COLLECTIONS = "pCollections";

  // Time methods
  public static final String STANDARD_DAYS = "standardDays";
  public static final String STANDARD_MINUTES = "standardMinutes";

  // BeamExecUtil
  public static final String RAND = "rand";
  public static final String READ = "read";
  public static final String WRITE = "write";
  public static final String TO_AVRO_ARRAY = "toAvroArray";
  public static final String TO_AVRO_RECORD = "toAvroRecord";
  public static final String STRING_GENERIC_RECORD_WRITE = "stringGenericRecordWrite";
  public static final String TO_BYTE_BUFFER = "toByteBuffer";
  public static final String BUILD_STRING_KEY = "buildStringKey";
  public static final String ROUND_UP_TIME = "roundUpTime";
  public static final String BUILD_DISTINCT_STRING_KEY_FROM_RECORD = "buildDistinctStringKeyFromRecord";

  // GenericRecord
  public static final String AVRO_PUT = "put";
  public static final String AVRO_GET = "get";
  public static final String GET_KEY = "getKey";
  public static final String GET_VALUE = "getValue";

  // PigUDF
  public static final String EXEC = "exec";
  public static final String BUILD_TUPLE = "buildTuple";

  // String
  public static final String EQUALS = "equals";
  public static final String TO_LOWER = "toLowerCase";
  public static final String TO_UPPER = "toUpperCase";
  public static final String COMPARE = "compare";

  // Map
  public static final String MAP_GET = "get";


  // Iterator.class
  public static final String NEXT = "next";
  public static final String HAS_NEXT = "hasNext";

  // Other classes
  public static final String OF = "of"; // ImmutableList.class
  public static final String MAIN = "main"; // main method
  public static final String FLATTEN = "flatten"; // ArrayFlatten.class
  public static final String ITERATOR = "iterator"; // Collection.class

  public static final String PARSE = "parse"; // Schema.Parser.class
  public static final String VALUE_OF = "valueOf";
  public static final String MATCHES = "matches"; // Pattern
  public static final String ARRAY = "array"; // DataByteArray
  public static final String PRINT_STACK_TRACE = "printStackTrace"; //Exception
}

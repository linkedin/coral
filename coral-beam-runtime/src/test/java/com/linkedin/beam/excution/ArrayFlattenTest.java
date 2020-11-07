/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ArrayFlattenTest {
  private static final Schema INPUT_SCHEMA = new Schema.Parser().parse(
      "{\"type\":\"record\","
          + "\"name\":\"record0\","
          + "\"namespace\":\"rel_avro\","
          + "\"fields\":["
            + "{\"name\":\"col0\",\"type\":[\"string\",\"null\"]},"
            + "{\"name\":\"col1\",\"type\":{\"type\":\"array\",\"items\":\"int\"}},"
            + "{\"name\":\"col2\","
              + "\"type\":{\"type\":\"array\","
                + "\"items\":{\"type\":\"record\",\"name\":\"nestedRec\","
                  + "\"fields\":["
                    + "{\"name\":\"field1\",\"type\":[\"string\",\"null\"]},"
                    + "{\"name\":\"field2\",\"type\":[\"int\",\"null\"]}"
                  + "]"
                + "}"
              + "}"
            + "}"
          + "]}");

  private static final Schema NESTED_SCHEMA = new Schema.Parser().parse(
      "{\"type\":\"record\",\"name\":\"nestedRec\","
          + "\"fields\":["
            + "{\"name\":\"field1\",\"type\":[\"string\",\"null\"]},"
            + "{\"name\":\"field2\",\"type\":[\"int\",\"null\"]}"
          + "] }");

  private static final Schema OUTPUT_SCHEMA = new Schema.Parser().parse(
      "{\"type\":\"record\","
          + "\"name\":\"record1\","
          + "\"namespace\":\"rel_avro\","
          + "\"fields\":["
            + "{\"name\":\"col0\",\"type\":[\"string\",\"null\"]},"
            + "{\"name\":\"col1\",\"type\":[\"int\",\"null\"]},"
            + "{\"name\":\"field1\",\"type\":[\"string\",\"null\"]},"
            + "{\"name\":\"field2\",\"type\":[\"int\",\"null\"]}"
          + "]}");

  private static final List<String> FLATTEN_COLS = Arrays.asList("col1", "col2");

  private static final String RESULT1 = "["
      + "{\"col0\": \"rec1\", \"col1\": 1, \"field1\": null, \"field2\": null}, "
      + "{\"col0\": \"rec1\", \"col1\": 2, \"field1\": null, \"field2\": null}, "
      + "{\"col0\": \"rec1\", \"col1\": 3, \"field1\": null, \"field2\": null}]";
  private static final String RESULT2 = "["
      + "{\"col0\": null, \"col1\": 4, \"field1\": \"nested1\", \"field2\": 1}, "
      + "{\"col0\": null, \"col1\": 4, \"field1\": \"nested2\", \"field2\": 2}]";
  private static final String RESULT3 = "["
      + "{\"col0\": \"rec3\", \"col1\": 6, \"field1\": \"nested1\", \"field2\": 1}, "
      + "{\"col0\": \"rec3\", \"col1\": 6, \"field1\": \"nested2\", \"field2\": 2}, "
      + "{\"col0\": \"rec3\", \"col1\": 7, \"field1\": \"nested1\", \"field2\": 1}, "
      + "{\"col0\": \"rec3\", \"col1\": 7, \"field1\": \"nested2\", \"field2\": 2}, "
      + "{\"col0\": \"rec3\", \"col1\": 8, \"field1\": \"nested1\", \"field2\": 1}, "
      + "{\"col0\": \"rec3\", \"col1\": 8, \"field1\": \"nested2\", \"field2\": 2}]";

  private GenericRecord record1;
  private GenericRecord record2;
  private GenericRecord record3;

  @BeforeMethod
  public void setUp() {
    record1 = new GenericData.Record(INPUT_SCHEMA);
    record1.put("col0", "rec1");
    record1.put("col1", new Integer[] { 1, 2, 3});

    record2 = new GenericData.Record(INPUT_SCHEMA);
    record2.put("col1", Sets.newHashSet(4));
    GenericRecord nestedRec1 = new GenericData.Record(NESTED_SCHEMA);
    nestedRec1.put("field1", "nested1");
    nestedRec1.put("field2", 1);
    GenericRecord nestedRec2 = new GenericData.Record(NESTED_SCHEMA);
    nestedRec2.put("field1", "nested2");
    nestedRec2.put("field2", 2);
    List<GenericRecord> nestedValues = Arrays.asList(nestedRec1, nestedRec2);
    record2.put("col2", nestedValues);

    record3 = new GenericData.Record(INPUT_SCHEMA);
    record3.put("col0", "rec3");
    record3.put("col1", new Integer[] { 6, 7, 8});
    record3.put("col2", nestedValues);


  }

  private void testFlattenHelper(GenericRecord inputRec, int expectedSize, String expectedResult) {
    Collection<GenericRecord> results = BeamArrayFlatten.flatten(inputRec, FLATTEN_COLS, OUTPUT_SCHEMA);
    Assert.assertEquals(results.size(), expectedSize);
    Assert.assertEquals(results.toString(), expectedResult);
  }

  @Test
  public void testFlatten() {
    testFlattenHelper(record1, 3, RESULT1);
    testFlattenHelper(record2, 2, RESULT2);
    testFlattenHelper(record3, 6, RESULT3);
  }
}

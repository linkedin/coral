/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import org.codehaus.jackson.node.NullNode;
import org.testng.Assert;
import org.testng.annotations.Test;


public class TestExecUtil {

  private Calendar getCalendar(String timezone) {
    return Calendar.getInstance(TimeZone.getTimeZone(timezone));
  }
  private Calendar getCalendar() {
    return getCalendar("UTC");
  }


  private static class Pair<K, V> {
    K key;
    V value;

    public Pair(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  static Schema createSchema(List<Pair<String, Schema.Type>> fieldTypes) {
    List<Schema.Field> fields =
        fieldTypes.stream()
            .map(ntp -> new Schema.Field(ntp.key, Schema.create(ntp.value), "",
                NullNode.getInstance()))
            .collect(Collectors.toList());
    return Schema.createRecord(fields);
  }

  @Test
  public void testTimeRoundUp() {
    Calendar cal = getCalendar();
    cal.set(2018, 7, 25, 14, 48, 30); // 2018-08-25 14:40:30
    long inputTime = cal.getTimeInMillis();
    testTimeRoundUpHelper(inputTime, Calendar.MINUTE, 15,  45);
    testTimeRoundUpHelper(inputTime, Calendar.MINUTE, 10,  40);
    testTimeRoundUpHelper(inputTime, Calendar.MINUTE, 8,  48);
    testTimeRoundUpHelper(inputTime, Calendar.HOUR_OF_DAY, 3,  12);

    Assert.assertEquals(BeamExecUtil.roundUpTime(inputTime, 1, Calendar.DATE), 1535155200000L /* 2018-08-25 00:00:00 */);
    Assert.assertEquals(BeamExecUtil.roundUpTime(inputTime, 2, Calendar.DATE), 1535068800000L /* 2018-08-24 00:00:00 */);
  }

  private void testTimeRoundUpHelper(long inputTime, int timeUnit, int roundTime, int expected) {
    Calendar out = getCalendar();
    out.setTimeInMillis(BeamExecUtil.roundUpTime(inputTime, roundTime, timeUnit));
    Assert.assertEquals(out.get(timeUnit), expected);
    for (int unit = Calendar.MILLISECOND; unit > timeUnit; --unit) {
      Assert.assertEquals(out.get(unit), 0);
    }
  }

  @Test
  public void testExpiredTime() {
    Calendar cal1 = getCalendar();
    Calendar cal2 = getCalendar();

    cal1.set(2019, 3, 20, 23, 59, 59);
    cal2.set(2019, 3, 21, 0, 0, 0);
    Assert.assertTrue(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.DATE, 1));
    Assert.assertFalse(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.DATE, 2));
    cal2.set(Calendar.DATE, 20);
    Assert.assertFalse(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.HOUR_OF_DAY, 1));

    cal1.set(2019, 3, 20, 0, 59, 59);
    cal2.set(2019, 3, 20, 2, 0, 0);
    Assert.assertTrue(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.HOUR_OF_DAY, 2));
    Assert.assertFalse(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.HOUR_OF_DAY, 3));
    cal2.set(Calendar.HOUR_OF_DAY, 0);
    Assert.assertFalse(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.HOUR_OF_DAY, 1));

    cal1.set(2019, 3, 21, 0, 0, 0);
    cal2.set(2019, 3, 21, 0, 0, 0);
    cal1.set(Calendar.MILLISECOND, 0);

    cal2.set(Calendar.MILLISECOND, 99);
    Assert.assertFalse(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.MILLISECOND, 100));
    cal2.set(Calendar.MILLISECOND, 100);
    Assert.assertTrue(BeamExecUtil.isExpired(cal1.getTimeInMillis(), cal2.getTimeInMillis(), Calendar.MILLISECOND, 100));
  }

  @Test
  public void testToDateFormat() {
    // Test pacific time
    Calendar cal1 = getCalendar("America/Los_Angeles");
    cal1.set(2019, 3, 21, 0, 0, 0);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal1.getTimeInMillis()), 20190421);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal1.getTimeInMillis(), "UTC"), 20190421);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal1.getTimeInMillis() / 1000), 20190421);
    cal1.set(2019, 3, 21, 23, 59, 59);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal1.getTimeInMillis()), 20190421);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal1.getTimeInMillis(), "UTC"), 20190422);

    // Test UTC time
    Calendar cal2 = getCalendar();
    cal2.set(2019, 3, 21, 0, 0, 0);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal2.getTimeInMillis()), 20190420);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal2.getTimeInMillis(), "UTC"), 20190421);
    cal2.set(2019, 3, 21, 23, 59, 59);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal2.getTimeInMillis()), 20190421);
    Assert.assertEquals((long) BeamExecUtil.toDateFormat(cal2.getTimeInMillis(), "UTC"), 20190421);
  }

  private GenericRecord createRecord(String stringCol, Integer intCol, Long longCol, Double doubleCol, Boolean boolCol, Long timestamp) {
    List<Pair<String, Schema.Type>> fields = new ArrayList<>();
    fields.add(new Pair<>("stringCol", Schema.Type.STRING));
    fields.add(new Pair<>("intCol", Schema.Type.INT));
    fields.add(new Pair<>("longCol", Schema.Type.LONG));
    fields.add(new Pair<>("doubleCol", Schema.Type.DOUBLE));
    fields.add(new Pair<>("boolCol", Schema.Type.BOOLEAN));
    fields.add(new Pair<>("timestamp", Schema.Type.LONG));
    GenericRecord record = new GenericData.Record(createSchema(fields));
    record.put("stringCol", stringCol);
    record.put("intCol", intCol);
    record.put("longCol", longCol);
    record.put("doubleCol", doubleCol);
    record.put("boolCol", boolCol);
    record.put("timestamp", timestamp);
    return record;
  }

  private void testBuildKeyHelper(String stringCol, Integer intCol, Long longCol, Double doubleCol, Boolean boolCol, Long timestamp, String expected) {
    GenericRecord record = createRecord(stringCol, intCol, longCol, doubleCol, boolCol, timestamp);
    Assert.assertEquals(BeamExecUtil.buildStringKeyFromRecord(record), expected);
  }

  @Test
  public void testBuildKeyFromRecord() {
    testBuildKeyHelper("test", 1, 2L, 3.01, true, 123L, "test_1_2_3.01_true_123_");
    testBuildKeyHelper("test", null, 2L, 3.01, false, 123L, "test_null_2_3.01_false_123_");
    testBuildKeyHelper("test", null, null, null, null, null, "test_null_null_null_null_null_");
  }

  private void testBuildDistinctKeyHelper(String stringCol, Integer intCol, Long longCol, Double doubleCol,
      Boolean boolCol, Long timestamp, String expected) {
    testBuildDistinctKeyHelper("", stringCol, intCol, longCol, doubleCol, boolCol, timestamp, expected);
  }

  private void testBuildDistinctKeyHelper(String prefix, String stringCol, Integer intCol, Long longCol,
      Double doubleCol, Boolean boolCol, Long timestamp, String expected) {
    GenericRecord record = createRecord(stringCol, intCol, longCol, doubleCol, boolCol, timestamp);
    Assert.assertEquals(BeamExecUtil.buildDistinctStringKeyFromRecord(prefix, record, "timestamp"), expected);
  }

  @Test
  public void testBuildDistinctKeyFromRecord() {
    testBuildDistinctKeyHelper("test", 1, 2L, 3.01, true, 123L, "test_1_2_3.01_true_");
    testBuildDistinctKeyHelper("prefix_", "test", 1, 2L, 3.01, true, 123L, "prefix_test_1_2_3.01_true_");
    testBuildDistinctKeyHelper("test", null, 2L, 3.01, false, Instant.now().toEpochMilli(), "test_null_2_3.01_false_");
    testBuildDistinctKeyHelper("test", null, null, null, null, null, "test_null_null_null_null_");
  }
}

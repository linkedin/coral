/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.builtin.Nondeterministic;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.util.avro.AvroStorageDataConversionUtilities;


public class BeamExecUtil {
  public static final String RECORD_NAME_PREFIXED = "record0";
  public static final String DYNAMIC_RECORD_NAME_PREFIXED = "dynamic_record";

  private BeamExecUtil() {
  }

  private static GenericRecord dynamicTuple2Avro(final Tuple t, final Schema s) throws ExecException {
    final GenericData.Record record = new GenericData.Record(s);
    for (Schema.Field f : s.getFields()) {
      final Object o = t.get(f.pos());
      record.put(f.pos(), o);
    }
    return record;
  }

  /**
   * Converts an object, which can be already ByteBuffer or GenericFixed,
   * into a ByteBuffer. Returns null if object is null.
   */
  public static ByteBuffer toByteBuffer(Object object) {
    if (object == null) {
      return null;
    }

    if (object instanceof ByteBuffer) {
      return (ByteBuffer) object;
    }
    if (object instanceof GenericFixed) {
      return ByteBuffer.wrap(((GenericFixed) object).bytes());
    }
    throw new RuntimeException("Cannot convert " + object.getClass().getName() + " object into ByteBuffer");
  }

  /**
   * Converts an object, which can be already an Avro GenericRecord or a Pig Tuple,
   * into an Avro GenericRecord. Returns null if object is null.
   */
  public static GenericRecord toAvroRecord(Object obj, Schema schema) {
    if (obj == null) {
      return null;
    }

    if (obj instanceof GenericRecord) {
      return (GenericRecord) obj;
    }

    if (obj instanceof Tuple) {
      final Tuple tuple = (Tuple) obj;
      try {
        if (isDynamicRecord(schema)) {
          return dynamicTuple2Avro(tuple, schema);
        } else {
          return AvroStorageDataConversionUtilities.packIntoAvro(tuple, schema);
        }
      } catch (Exception e) {
        throw new RuntimeException("Cannot convert " + obj + " into GenericRecord", e);
      }
    }
    throw new RuntimeException("Cannot convert " + obj.getClass().getName() + " object into GenericRecord");
  }

  /**
   * Converts an object, which can be already an Avro GenericArray or a Pig DataBag,
   * into an Avro GenericArray. Returns null if object is null.
   */
  public static GenericArray toAvroArray(Object obj, Schema schema) {
    if (obj == null) {
      return null;
    }

    if (obj instanceof GenericArray) {
      return (GenericArray) obj;
    }

    if (obj instanceof DataBag) {
      final DataBag bag = (DataBag) obj;
      final GenericData.Array<Object> array = new GenericData.Array<>(new Long(bag.size()).intValue(), schema);
      for (Tuple tuple : bag) {
        try {
          if (schema.getElementType() != null && schema.getElementType().getType() == Schema.Type.RECORD) {
            array.add(toAvroRecord(tuple, schema.getElementType()));
          } else if (tuple.size() == 1) {
            array.add(tuple.get(0));
          } else {
            throw new RuntimeException("Can't pack " + tuple + " into schema " + schema);
          }
        } catch (ExecException e) {
          throw new RuntimeException("Can't pack " + tuple + " into schema " + schema, e);
        }
      }
      return array;
    }

    throw new RuntimeException("Cannot convert " + obj.getClass().getName() + " object into GenericArray");
  }

  private static boolean isDynamicRecord(Schema schema) {
    return schema.getName() != null && schema.getName().startsWith(DYNAMIC_RECORD_NAME_PREFIXED);
  }

  /**
   * Wrapper for Math.random to indicate that it's nondeterministic
   * @return
   */
  @Nondeterministic
  public static double rand() {
    return Math.random();
  }

  public static String buildStringKey(String... stringList) {
    StringBuilder builder = new StringBuilder();
    for (String item : stringList) {
      if (item != null) {
        builder.append(item);
      } else {
        builder.append("null");
      }
      builder.append("_");
    }
    return builder.toString();
  }

  public static String buildStringKey(GenericRecord record, String... keyColNames) {
    if (keyColNames == null) {
      return "null";
    }
    String[] keys = new String[keyColNames.length];
    for (int i = 0; i < keyColNames.length; i++) {
      keys[i] = Objects.toString(record.get(keyColNames[i]));
    }
    return buildStringKey(keys);
  }

  public static String buildDistinctStringKeyFromRecord(GenericRecord avroRecord, String timestampCol) {
    return buildDistinctStringKeyFromRecord("", avroRecord, timestampCol);
  }

  public static String buildDistinctStringKeyFromRecord(String prefix, GenericRecord avroRecord, String timestampCol) {
    final StringBuilder builder = new StringBuilder();
    builder.append(prefix);
    for (Schema.Field field : avroRecord.getSchema().getFields()) {
      // Ignore timestamp column
      if (field.name().equalsIgnoreCase(timestampCol)) {
        continue;
      }
      builder.append(Objects.toString(avroRecord.get(field.name()), "null"));
      builder.append("_");
    }
    return builder.toString();
  }

  public static String buildStringKeyFromRecord(GenericRecord avroRecord) {
    final StringBuilder builder = new StringBuilder();
    for (Schema.Field field : avroRecord.getSchema().getFields()) {
      builder.append(Objects.toString(avroRecord.get(field.name()), "null"));
      builder.append("_");
    }
    return builder.toString();
  }

  public static String getOpId(String opName, int codeVersion) {
    return opName + "_v" + codeVersion;
  }

  /**
   * Rounds up time to a given range. Example roundTime = 15, timeUnit = MINUTE, epochTime will be rounded
   * up to every 15 minutes, like 10:00, 10:15, 10:30...
   *
   * @param epochTime Original epoch time
   * @param roundTime Amount of time to round up (see above example)
   * @param timeUnit Time unit in {{@link Calendar}}, from HOUR_OF_DAY to MILLISECOND
   * @return Time in epoch after rounding up
   */
  public static long roundUpTime(long epochTime, int roundTime, int timeUnit) {
    return roundUpTimeInCalendar(epochTime, roundTime, timeUnit).getTimeInMillis();
  }

  private static Calendar roundUpTimeInCalendar(long epochTime, int roundTime, int timeUnit) {
    if (epochTime <= 0) {
      throw new IllegalArgumentException("Invalid time to convert: " + epochTime);
    }

    if (roundTime <= 0) {
      throw new IllegalArgumentException("Invalid round up time: " + roundTime);
    }

    if (timeUnit > Calendar.MILLISECOND || (timeUnit < Calendar.HOUR_OF_DAY && timeUnit != Calendar.DATE)) {
      throw new IllegalArgumentException("Invalid time unit: " + timeUnit);
    }

    final Calendar cal = getCalendarFromEpoch(epochTime);
    cal.set(timeUnit, cal.get(timeUnit) - (cal.get(timeUnit) % roundTime));
    for (int unit = Calendar.MILLISECOND; unit > Math.max(timeUnit, Calendar.HOUR); --unit) {
      cal.set(unit, 0);
    }
    return cal;
  }

  /**
   * Checks if end time is expired from the begin time after a duration.
   *
   * Example:
   * - 2019/03/20 23:59, 2019/03/21 00:00, DATE, 1 - true
   * - 2019/03/20 23:59, 2019/03/21 00:00, MINUTE, 2 - false
   * - 2019/03/21 00:00, 2019/03/21 23:59, DAY, 1 - false
   * - 2019/03/21 00:59, 2019/03/21 01:00, HOUR_OF_DAY, 2 - false
   *
   * @param begin begin time
   * @param end end time
   * @param timeGranularity Time granularity unit (in {{@link Calendar}}, from HOUR_OF_DAY to MILLISECOND)
   *                        for rounding up and comparison
   * @param duration Duration (within time granularity) for expiration
   * @return true iff end time is expired
   */
  public static boolean isExpired(long begin, long end, int timeGranularity, int duration) {
    final Calendar beginCal = roundUpTimeInCalendar(begin, 1, timeGranularity);
    final Calendar endCal = roundUpTimeInCalendar(end, 1, timeGranularity);
    beginCal.add(timeGranularity, duration - 1);
    return beginCal.before(endCal);
  }

  private static Calendar getCalendarFromEpoch(long epochTime) {
    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(epochTime);
    return calendar;
  }

  /**
   * Converts epoch time to yyyyMMdd format in Pacific timezone
   *
   * @param epochTime epoch time
   */
  public static Long toDateFormat(long epochTime) {
    return toDateFormat(epochTime, "America/Los_Angeles");
  }

  /**
   * Converts epoch time to yyyyMMdd format in a given timezone
   *
   * @param epochTime epoch time
   * @param timeZone time zone
   */
  public static Long toDateFormat(long epochTime, String timeZone) {
    final SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd");
    dateParser.setTimeZone(TimeZone.getTimeZone(timeZone));
    if (epochTime < 0) {
      return null;
    }

    if (epochTime < 10000000000L) {
      return Long.parseLong(dateParser.format(epochTime * 1000));
    }

    return Long.parseLong(dateParser.format(epochTime));
  }
}

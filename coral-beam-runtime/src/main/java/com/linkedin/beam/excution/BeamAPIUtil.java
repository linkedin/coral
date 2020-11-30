/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import java.util.List;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.log4j.Logger;
import org.joda.time.Instant;


public class BeamAPIUtil {
  private static final Logger LOG = Logger.getLogger(BeamAPIUtil.class.getName());

  private BeamAPIUtil() {
  }

  public static SerializableFunction<KV<String, GenericRecord>, Instant> withTimeFunction(List<String> timeField) {
    return (SerializableFunction<KV<String, GenericRecord>, Instant>) input -> {
      final Instant now = Instant.now();
      if (timeField == null || timeField.isEmpty()) {
        // No time field specified return current processing time
        return now;
      }
      GenericRecord record = input.getValue();
      for (int i = 0; i < timeField.size(); i++) {
        Object value = record.get(timeField.get(i));
        if (value == null) {
          LOG.warn("Cannot get event time for input record, use current processing time instead. Time field: "
              + timeField + ". Record: " + input.getValue());
          return now;
        }
        if (i < timeField.size() - 1) {
          if (!(value instanceof GenericRecord)) {
            throw new RuntimeException(
                "Invalid schema for time field. Time field: " + timeField + ". Record schema: " + input.getValue().getSchema());
          }
          record = (GenericRecord) value;
          continue;
        }
        return new Instant(value);
      }

      // Never reach here
      return null;
    };
  }
}

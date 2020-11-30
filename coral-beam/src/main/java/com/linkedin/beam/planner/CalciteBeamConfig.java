/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Configuration for Beam physical operators, obtained from dataset.conf
 */
public class CalciteBeamConfig {
  public static final long DEFAULT_AGGREGATE_WINDOW_MINUTES = 5L;
  public static final long DEFAULT_JOIN_WINDOW_MINUTES = 5L;
  public static final int DEFAULT_DEDUP_TIME_GRANULARITY = Calendar.DATE;
  public static final int DEFAULT_DEDUP_TIME_VALUE = 1;

  public static class StreamConfig {
    public final String topic;
    public final List<String> timeField;

    public StreamConfig(String topic) {
      this(topic, new ArrayList<>());
    }

    public StreamConfig(String topic, List<String> timeField) {
      this.topic = topic;
      this.timeField = timeField;
    }
  }

  public final String applicationName;
  public final String timestampField;
  public final String kafkaOutputTopic;
  // Map from table names to Kafka input streams
  public final Map<String, StreamConfig> tableToInputStreams = new HashMap<>();
  public long joinWindowMinutes = DEFAULT_JOIN_WINDOW_MINUTES;
  public long aggregateWindowMinutes = DEFAULT_AGGREGATE_WINDOW_MINUTES;

  public int dedupTimeGranularity = DEFAULT_DEDUP_TIME_GRANULARITY;
  public int dedupTimeValue = DEFAULT_DEDUP_TIME_VALUE;

  public boolean planOptimized = false;

  public CalciteBeamConfig(String applicationName, String timestampField, String kafkaOutputTopic) {
    this.applicationName = applicationName;
    this.timestampField = timestampField;
    this.kafkaOutputTopic = kafkaOutputTopic;
  }

  public StreamConfig getInputStream(List<String> tableFullNames) {
    final String tableName = tableFullNames.get(tableFullNames.size() - 1);
    final StreamConfig inputStream = tableToInputStreams.get(tableName);
    if (inputStream != null) {
      return inputStream;
    }

    throw new IllegalArgumentException("No input stream specified for the table: " + tableFullNames);
  }
}

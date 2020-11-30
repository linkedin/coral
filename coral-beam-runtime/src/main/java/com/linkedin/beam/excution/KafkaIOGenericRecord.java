/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.kafka.KafkaIO;


public class KafkaIOGenericRecord {
  public static KafkaIO.Read<String, GenericRecord> read() {
    return KafkaIO.read();
  }

  public static KafkaIO.Write<String, GenericRecord> write() {
    return KafkaIO.write();
  }

  private KafkaIOGenericRecord() {
  }
}

/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.Map;

import org.apache.avro.Schema;


/**
 * A unified abstraction representing a dataset (table/view) in Coral.
 * This interface provides a common way to access table metadata regardless
 * of the underlying table format (Hive, Iceberg, etc.).
 *
 * Implementations of this interface hide the details of specific table formats
 * and provide a consistent API for accessing schema, properties, and metadata.
 */
public interface Dataset {

  /**
   * Returns the fully qualified table name in the format "database.table".
   *
   * @return Fully qualified table name
   */
  String name();

  /**
   * Returns the Avro schema representation of this dataset.
   * The schema includes all columns, their types, and nullability information.
   *
   * @return Avro Schema object representing the dataset structure
   */
  Schema avroSchema();

  /**
   * Returns the properties/parameters associated with this dataset.
   * Properties may include table format specific metadata, statistics,
   * partitioning information, etc.
   *
   * @return Map of property key-value pairs
   */
  Map<String, String> properties();

  /**
   * Returns the type of this dataset (TABLE or VIEW).
   *
   * @return TableType enum value
   */
  TableType tableType();
}


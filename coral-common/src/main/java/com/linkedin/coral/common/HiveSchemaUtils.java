/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;


/**
 * Utility class providing convenience methods to access schema objects
 */
public class HiveSchemaUtils {

  private HiveSchemaUtils() {

  }

  /**
   * Returns Calcite schema corresponding to hive db name
   * @param schema root level Hive Schema
   * @param db database name
   * @return Optional Calcite schema representation of Hive table. Returned value is empty
   * if the schema is not found
   */
  public static Optional<Schema> getDb(@Nonnull HiveSchema schema, @Nonnull String db) {
    Preconditions.checkNotNull(db);
    Preconditions.checkNotNull(schema);
    return Optional.ofNullable(schema.getSubSchema(db));
  }

  /**
   * Returns a calcite representation of Hive table
   * @param schema Root calcite schema representation of hive catalog
   * @param db database name
   * @param table table name
   * @return Returned object is empty if the input db or table does not exist.
   */
  public static Optional<Table> getTable(@Nonnull HiveSchema schema, @Nonnull String db, @Nonnull String table) {
    Preconditions.checkNotNull(table);
    Optional<Schema> dbSchema = getDb(schema, db);
    return dbSchema.map(value -> value.getTable(table));
  }
}

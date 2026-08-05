/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.List;

import org.apache.calcite.schema.Table;


/**
 * SPI (Service Provider Interface) for creating Calcite table adapters from {@link CoralTable} instances.
 *
 * <p>Implementations of this interface are discovered at runtime via {@link java.util.ServiceLoader},
 * allowing format-specific adapters (Hive, Iceberg, etc.) to be loaded without requiring their
 * classes on the compile-time classpath of the consuming module.
 *
 * <p>Each implementation should handle a specific {@link CoralTable} subtype and create the appropriate
 * Calcite {@link Table} adapter for it.
 *
 * @see CoralCalciteTableAdapterRegistry Registry that discovers and dispatches to factories
 */
public interface CoralCalciteTableAdapterFactory {

  /**
   * Returns whether this factory can create an adapter for the given CoralTable.
   *
   * @param coralTable The table to check
   * @return true if this factory supports the given table type
   */
  boolean supports(CoralTable coralTable);

  /**
   * Creates a Calcite {@link Table} adapter for the given CoralTable.
   *
   * @param coralTable The table to create an adapter for
   * @param schemaPath The Calcite schema path for the table
   * @return A Calcite Table adapter
   */
  Table createAdapter(CoralTable coralTable, List<String> schemaPath);

  /**
   * Returns the priority of this factory. Lower values indicate higher priority.
   * When multiple factories support the same CoralTable type, the one with the
   * lowest priority value is used.
   *
   * @return Priority value (default 100)
   */
  default int priority() {
    return 100;
  }
}

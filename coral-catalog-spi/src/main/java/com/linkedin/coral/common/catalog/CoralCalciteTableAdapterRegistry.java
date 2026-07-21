/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.calcite.schema.Table;


/**
 * Registry that discovers {@link CoralCalciteTableAdapterFactory} implementations via
 * {@link ServiceLoader} and dispatches table adapter creation to the appropriate factory.
 *
 * <p>Factories are ordered by {@link CoralCalciteTableAdapterFactory#priority()} (lowest first).
 * The first factory that {@link CoralCalciteTableAdapterFactory#supports(CoralTable) supports}
 * a given CoralTable is used to create the adapter.
 *
 * <p>In environments with complex classloader hierarchies (e.g., Spark), factories can also
 * be registered explicitly via {@link #register(CoralCalciteTableAdapterFactory)}.
 */
public final class CoralCalciteTableAdapterRegistry {

  private static final CopyOnWriteArrayList<CoralCalciteTableAdapterFactory> EXPLICIT_FACTORIES =
      new CopyOnWriteArrayList<>();

  private CoralCalciteTableAdapterRegistry() {
  }

  /**
   * Explicitly registers a factory. Use this in environments where ServiceLoader
   * discovery may not work (e.g., complex classloader hierarchies in Spark).
   *
   * @param factory The factory to register
   */
  public static void register(CoralCalciteTableAdapterFactory factory) {
    EXPLICIT_FACTORIES.addIfAbsent(factory);
  }

  /**
   * Creates a Calcite {@link Table} adapter for the given CoralTable by finding an appropriate factory.
   *
   * @param coralTable The table to create an adapter for
   * @param schemaPath The Calcite schema path
   * @return A Calcite Table adapter
   * @throws UnsupportedOperationException if no factory supports the given table type
   */
  public static Table createAdapter(CoralTable coralTable, List<String> schemaPath) {
    List<CoralCalciteTableAdapterFactory> allFactories = getAllFactories();
    for (CoralCalciteTableAdapterFactory factory : allFactories) {
      if (factory.supports(coralTable)) {
        return factory.createAdapter(coralTable, schemaPath);
      }
    }
    throw new UnsupportedOperationException(
        "No CoralCalciteTableAdapterFactory found for table type: " + coralTable.getClass().getName());
  }

  private static List<CoralCalciteTableAdapterFactory> getAllFactories() {
    List<CoralCalciteTableAdapterFactory> factories = new ArrayList<>(EXPLICIT_FACTORIES);

    ServiceLoader<CoralCalciteTableAdapterFactory> loader = ServiceLoader.load(CoralCalciteTableAdapterFactory.class);
    for (CoralCalciteTableAdapterFactory factory : loader) {
      factories.add(factory);
    }

    return factories.stream().sorted(Comparator.comparingInt(CoralCalciteTableAdapterFactory::priority))
        .collect(Collectors.toList());
  }
}

/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.spi;

import com.linkedin.coral.common.catalog.CoralCatalog;


/**
 * SPI for discovering and constructing {@link DialectPlugin} instances.
 *
 * <p>Provider implementations are the registration entry point picked up by
 * {@link java.util.ServiceLoader}, so they must declare a public no-arg constructor and a
 * {@code META-INF/services/com.linkedin.coral.benchmark.spi.DialectPluginProvider}
 * file listing the implementing class. The provider is a thin factory: its only job is
 * to produce a fully-constructed {@link DialectPlugin} bound to the supplied catalog,
 * which lets the plugin keep its catalog reference {@code final} and avoids the
 * two-phase-{@code init} pattern.
 *
 * <p>Typical implementation:
 * <pre>{@code
 * public final class HiveDialectPluginProvider implements DialectPluginProvider {
 *     public Dialect dialect() { return Dialect.HIVE; }
 *     public DialectPlugin create(CoralCatalog catalog) {
 *         return new HiveDialectPlugin(catalog);
 *     }
 * }
 * }</pre>
 */
public interface DialectPluginProvider {

  /**
   * Returns the dialect that this provider produces plugins for.
   *
   * @return the dialect identifier
   */
  Dialect dialect();

  /**
   * Constructs a {@link DialectPlugin} bound to the given catalog.
   *
   * @param catalog the catalog providing table metadata for query resolution and emission
   * @return a new, fully-constructed plugin instance
   */
  DialectPlugin create(CoralCatalog catalog);
}

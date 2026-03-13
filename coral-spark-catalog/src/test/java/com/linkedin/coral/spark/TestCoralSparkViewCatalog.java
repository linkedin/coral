/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import com.linked.coral.spark.CoralSparkViewCatalog;

import org.apache.spark.sql.connector.catalog.SupportsNamespaces;
import org.apache.spark.sql.connector.catalog.TableCatalog;


/**
 * Concrete implementation of CoralSparkViewCatalog for testing.
 */
public class TestCoralSparkViewCatalog extends CoralSparkViewCatalog<TestCoralSparkViewCatalog.SessionCatalogType> {

  public interface SessionCatalogType extends TableCatalog, SupportsNamespaces {
  }
}

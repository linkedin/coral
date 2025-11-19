/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.List;


/**
 * Top-level catalog interface for accessing tables in Coral.
 * This interface provides a unified API for querying table metadata
 * across different table formats (Hive, Iceberg, etc.).
 *
 * CoralCatalog abstracts away the differences between various table formats
 * and provides a consistent way to access table information through
 * the {@link CoralTable} interface.
 *
 * Implementations of this interface handle the details of connecting to
 * metadata stores and converting format-specific table representations
 * into the unified CoralTable abstraction.
 */
public interface CoralCatalog {

  /**
   * Retrieves a table by namespace and table name.
   * This method returns a unified CoralTable abstraction that works
   * across different table formats (Hive, Iceberg, etc.).
   *
   * @param namespaceName Namespace (database) name
   * @param tableName Table name
   * @return CoralTable object representing the table, or null if not found
   */
  CoralTable getTable(String namespaceName, String tableName);

  /**
   * Checks if a namespace (database) exists in the catalog.
   * This provides a lightweight way to verify namespace existence
   * without retrieving full metadata.
   *
   * @param namespaceName Namespace (database) name
   * @return true if the namespace exists, false otherwise
   */
  boolean namespaceExists(String namespaceName);

  /**
   * Retrieves all table (table/view) names in a namespace.
   *
   * @param namespaceName Namespace (database) name
   * @return List of table names in the namespace, empty list if namespace doesn't exist
   */
  List<String> getAllTables(String namespaceName);

  /**
   * Retrieves all namespace (database) names accessible through this catalog.
   *
   * @return List of namespace names, empty list if no namespaces are accessible
   */
  List<String> getAllNamespaces();
}

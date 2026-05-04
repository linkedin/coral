/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


/**
 * Thread-local tracker that records view to [immediate dependencies] mappings
 * during Calcite view expansion.
 */
public class ViewDependencyTracker {
  public static final String HIVE_CATALOG = "hive";
  public static final String OPENHOUSE_CATALOG = "openhouse";

  private static final ThreadLocal<ViewDependencyTracker> INSTANCE =
      ThreadLocal.withInitial(ViewDependencyTracker::new);

  private final Map<String, List<String>> viewDeps = new LinkedHashMap<>();

  private final Deque<String> expansionStack = new ArrayDeque<>();

  public static ViewDependencyTracker get() {
    return INSTANCE.get();
  }

  public static void reset() {
    INSTANCE.remove();
  }

  /**
   * Tracks a view expansion, recording the view as a dependency of any parent view
   * currently being expanded, then executing the supplied work within the scope of
   * this view. The expansion stack is managed automatically.
   */
  public <T> T withViewExpansion(String catalog, String dbName, String tableName, Supplier<T> work) {
    String qualifiedName = catalog + "." + dbName + "." + tableName;
    if (!expansionStack.isEmpty()) {
      String parent = expansionStack.peek();
      viewDeps.computeIfAbsent(parent, k -> new ArrayList<>()).add(qualifiedName);
    }
    expansionStack.push(qualifiedName);
    try {
      return work.get();
    } finally {
      expansionStack.pop();
    }
  }

  /**
   * Records a base table (non-view) as a dependency of the view currently being expanded.
   */
  public void recordBaseTableDependency(String catalog, String dbName, String tableName) {
    String qualifiedName = catalog + "." + dbName + "." + tableName;
    if (!expansionStack.isEmpty()) {
      String parent = expansionStack.peek();
      // Skip self-dependency: this occurs when convertView is called on a base table,
      // which pushes the table name onto the stack and then resolves itself.
      if (!qualifiedName.equals(parent)) {
        viewDeps.computeIfAbsent(parent, k -> new ArrayList<>()).add(qualifiedName);
      }
    }
  }

  /**
   * Returns the collected view dependency chain.
   * Each entry represents a view and its immediate dependencies (both views and base tables).
   */
  public List<ViewDependency> getViewDependencies() {
    List<ViewDependency> result = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : viewDeps.entrySet()) {
      if (!entry.getValue().isEmpty()) {
        result.add(new ViewDependency(entry.getKey(), entry.getValue()));
      }
    }
    return result;
  }
}

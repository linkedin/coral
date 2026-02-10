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


/**
 * Thread-local tracker that records view to [immediate dependencies] mappings
 * during Calcite view expansion.
 */
public class ViewDependencyTracker {
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
   * Called at the START of expanding a view.
   */
  public void enterView(String dbName, String tableName) {
    String qualifiedName = dbName + "." + tableName;
    // Record this view as a dependency of the current parent
    if (!expansionStack.isEmpty()) {
      String parent = expansionStack.peek();
      viewDeps.computeIfAbsent(parent, k -> new ArrayList<>()).add(qualifiedName);
    }
    expansionStack.push(qualifiedName);
  }

  /**
   * Called when a base table (non-view) is encountered during view expansion.
   */
  public void recordBaseDependency(String dbName, String tableName) {
    String qualifiedName = dbName + "." + tableName;
    if (!expansionStack.isEmpty()) {
      String parent = expansionStack.peek();
      viewDeps.computeIfAbsent(parent, k -> new ArrayList<>()).add(qualifiedName);
    }
  }

  /**
   * Called at the END of expanding a view.
   */
  public void exitView() {
    if (!expansionStack.isEmpty()) {
      expansionStack.pop();
    }
  }

  /**
   * Returns the collected view dependency chain.
   * Each entry represents a view and its immediate dependencies (both views and base tables).
   */
  public List<ViewDependency> getViewDependencies() {
    List<ViewDependency> result = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : viewDeps.entrySet()) {
      result.add(new ViewDependency(entry.getKey(), entry.getValue()));
    }
    return result;
  }
}

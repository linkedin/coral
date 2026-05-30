/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;
import java.util.Objects;


/**
 * Represents a single node in the view dependency chain.
 * Names are in "catalog.db.table" format (e.g. "hive.db.v1", "openhouse.db.t1").
 * For a view "hive.db.v1" that depends on "hive.db.v2" and "openhouse.db.t1",
 * this would be: ViewDependency("hive.db.v1", ["hive.db.v2", "openhouse.db.t1"])
 */
public class ViewDependency {
  private final String viewName;
  private final List<String> dependencies;

  public ViewDependency(String viewName, List<String> dependencies) {
    this.viewName = viewName;
    this.dependencies = dependencies;
  }

  public String getViewName() {
    return viewName;
  }

  public List<String> getDependencies() {
    return dependencies;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewDependency that = (ViewDependency) o;
    return Objects.equals(viewName, that.viewName) && Objects.equals(dependencies, that.dependencies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(viewName, dependencies);
  }

  @Override
  public String toString() {
    return "ViewDependency{view=" + viewName + ", deps=" + dependencies + "}";
  }
}

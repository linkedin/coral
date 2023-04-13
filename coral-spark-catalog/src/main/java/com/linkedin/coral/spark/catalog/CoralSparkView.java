/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.catalog;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.spark.sql.connector.catalog.View;
import org.apache.spark.sql.types.StructType;


public class CoralSparkView implements View {

  private final String name;
  private final String sql;
  private final StructType viewSchema;
  private final String catalogName;
  private final String[] namespace;

  public CoralSparkView(String name, String sql, StructType viewSchema, String catalogName, String[] namespace) {
    this.name = name;
    this.sql = sql;
    this.viewSchema = viewSchema;
    this.catalogName = catalogName;
    this.namespace = namespace;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public StructType schema() {
    return viewSchema;
  }

  @Override
  public String[] columnAliases() {
    return new String[0];
  }

  @Override
  public String[] columnComments() {
    return new String[0];
  }

  @Override
  public String currentCatalog() {
    return catalogName;
  }

  @Override
  public String[] currentNamespace() {
    return namespace;
  }

  @Override
  public Map<String, String> properties() {
    return ImmutableMap.of();
  }
}

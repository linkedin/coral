/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.iceberg;

import java.util.HashMap;
import java.util.Map;

import org.apache.iceberg.Table;

import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.TableType;
import com.linkedin.coral.common.types.CoralDataType;

import static com.google.common.base.Preconditions.*;


/**
 * Implementation of {@link CoralTable} interface for Apache Iceberg tables.
 * This class wraps an Iceberg Table object and provides a unified
 * CoralTable API for accessing table metadata.
 */
public class IcebergTable implements CoralTable {

  private final Table table;

  /**
   * Creates a new IcebergTable wrapping the given Iceberg table.
   *
   * @param table Iceberg Table object (must not be null)
   */
  public IcebergTable(Table table) {
    this.table = checkNotNull(table, "Iceberg table cannot be null");
  }

  @Override
  public String name() {
    return table.name();
  }

  @Override
  public Map<String, String> properties() {
    if (table.properties() != null) {
      return new HashMap<>(table.properties());
    }
    return new HashMap<>();
  }

  @Override
  public TableType tableType() {
    return TableType.TABLE;
  }

  /**
   * INTERNAL API
   * @deprecated This method is for internal use only and will be removed in a future release.
   * Do not depend on this API.
   *
   * @return Iceberg Table object
   */
  public org.apache.iceberg.Table getIcebergTable() {
    return table;
  }

  @Override
  public CoralDataType getSchema() {
    return IcebergToCoralTypeConverter.convert(table.schema());
  }
}

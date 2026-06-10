/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.validate.SqlConformance;


/**
 * @deprecated Use {@link CoralSqlValidator} instead.
 */
@Deprecated
public class HiveSqlValidator extends CoralSqlValidator {

  public HiveSqlValidator(SqlOperatorTable opTab, CalciteCatalogReader catalogReader, JavaTypeFactory typeFactory,
      SqlConformance conformance) {
    super(opTab, catalogReader, typeFactory, conformance);
  }
}

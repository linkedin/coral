/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;


/**
 * @deprecated Use {@link CoralSqlToRelConverter} instead.
 */
@Deprecated
class HiveSqlToRelConverter extends CoralSqlToRelConverter {

  HiveSqlToRelConverter(RelOptTable.ViewExpander viewExpander, SqlValidator validator,
      Prepare.CatalogReader catalogReader, RelOptCluster cluster, SqlRexConvertletTable convertletTable,
      Config config) {
    super(viewExpander, validator, catalogReader, cluster, convertletTable, config);
  }
}

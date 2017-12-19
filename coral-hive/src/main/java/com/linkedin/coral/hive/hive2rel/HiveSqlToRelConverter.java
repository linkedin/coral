package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;


/**
 * Class to convert Hive SQL to Calcite RelNode. This class
 * specializes the functionality provided by {@link SqlToRelConverter}.
 */
class HiveSqlToRelConverter extends SqlToRelConverter {

  HiveSqlToRelConverter(RelOptTable.ViewExpander viewExpander, SqlValidator validator,
      Prepare.CatalogReader catalogReader, RelOptCluster cluster, SqlRexConvertletTable convertletTable,
      Config config) {
    super(viewExpander, validator, catalogReader, cluster, convertletTable, config);
  }
}

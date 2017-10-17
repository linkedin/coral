package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;


public class HiveSqlToRelConverter extends SqlToRelConverter {

  public HiveSqlToRelConverter(RelOptTable.ViewExpander viewExpander, SqlValidator validator,
      Prepare.CatalogReader catalogReader, RelOptCluster cluster, SqlRexConvertletTable convertletTable,
      Config config) {
    super(viewExpander, validator, catalogReader, cluster, convertletTable, config);
  }

  @Override
  public RelNode toRel(RelOptTable table) {
    return LogicalTableScan.create(cluster, table);
  }
}

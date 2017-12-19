package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.hadoop.hive.metastore.api.Table;


/**
 * A TranslatableTable (ViewTable) version of HiveTable that supports
 * recursive expansion of view definitions
 */
public class HiveViewTable extends HiveTable implements TranslatableTable {
  private final List<String> schemaPath;

  /**
   * Constructor to create bridge from hive table to calcite table

   * @param hiveTable Hive table
   */
  public HiveViewTable(Table hiveTable, List<String> schemaPath) {
    super(hiveTable);
    this.schemaPath = schemaPath;
  }

  @Override
  public RelNode toRel(RelOptTable.ToRelContext relContext, RelOptTable relOptTable) {
    try {
      RelRoot root = relContext.expandView(relOptTable.getRowType(), hiveTable.getViewExpandedText(), schemaPath, null);
      root = root.withRel(RelOptUtil.createCastRel(root.rel, relOptTable.getRowType(), true));
      return root.rel;
    } catch (Exception e) {
      throw new RuntimeException("Error while parsing view definition", e);
    }
  }
}

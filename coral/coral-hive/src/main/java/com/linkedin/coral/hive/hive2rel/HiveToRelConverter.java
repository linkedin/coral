package com.linkedin.coral.hive.hive2rel;

import com.linkedin.coral.hive.hive2rel.parsetree.ParseTreeBuilder;
import java.io.File;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.sql.SqlNode;


/**
 * Public class to convert Hive SQL to Calcite relational algebra.
 * This class should serve as the main entry point for clients to convert
 * Hive queries.
 */
/*
 * We provide this class as a public interface by providing a thin wrapper
 * around HiveSqlToRelConverter. Directly using HiveSqlToRelConverter will
 * expose public methods from SqlToRelConverter. Use of SqlToRelConverter
 * is likely to change in the future if we want more control over the
 * conversion process. This class abstracts that out.
 */
public class HiveToRelConverter {

  private final RelContextProvider relContextProvider;
  private final HiveContext hiveContext;

  /**
   * Initializes converter with hive configuration at provided path
   * @param hiveConfFile hive configuration file
   */
  public static HiveToRelConverter create(File hiveConfFile) {
    try {
      HiveContext hiveContext = HiveContext.create(hiveConfFile.getPath());
      HiveSchema schema = HiveSchema.create(hiveContext.getConf());
      RelContextProvider relContextProvider = new RelContextProvider(schema);
      return new HiveToRelConverter(hiveContext, relContextProvider);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private HiveToRelConverter(HiveContext hiveContext, RelContextProvider relContextProvider) {
    this.hiveContext = hiveContext;
    this.relContextProvider = relContextProvider;
  }

  /**
   * Converts input Hive SQL query to Calcite {@link RelNode}.
   *
   * This method resolves all the database, table and field names using the catalog
   * information provided by hive configuration during initialization
   *
   * @param sql Hive sql string to convert to Calcite RelNode
   * @return Calcite RelNode representation of input hive sql
   */
  public RelNode convert(String sql) {
    ParseTreeBuilder treeBuilder = new ParseTreeBuilder();
    SqlNode sqlNode = treeBuilder.process(sql);
    RelRoot relRoot = relContextProvider.getSqlToRelConverter().convertQuery(sqlNode, true, true);
    return relRoot.rel;
  }
}

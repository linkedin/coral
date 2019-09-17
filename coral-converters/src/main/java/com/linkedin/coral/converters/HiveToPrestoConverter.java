package com.linkedin.coral.converters;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.presto.rel2presto.RelToPrestoConverter;
import org.apache.calcite.rel.RelNode;

import static com.google.common.base.Preconditions.*;


public class HiveToPrestoConverter {

  private final HiveToRelConverter hiveToRelConverter;
  private final RelToPrestoConverter relToPrestoConverter;

  public static HiveToPrestoConverter create(HiveMetastoreClient mscClient) {
    checkNotNull(mscClient);
    HiveToRelConverter hiveToRelConverter = HiveToRelConverter.create(mscClient);
    RelToPrestoConverter relToPrestoConverter = new RelToPrestoConverter();
    return new HiveToPrestoConverter(hiveToRelConverter, relToPrestoConverter);
  }

  private HiveToPrestoConverter(HiveToRelConverter hiveToRelConverter,
      RelToPrestoConverter relToPrestoConverter) {
    this.hiveToRelConverter = hiveToRelConverter;
    this.relToPrestoConverter = relToPrestoConverter;
  }

  /**
   * Converts input HiveQL to Presto SQL
   *
   * @param hiveSql hive sql query string
   * @return presto sql string representing input hiveSql
   */
  public String toPrestoSql(String hiveSql) {
    RelNode rel = hiveToRelConverter.convertSql(hiveSql);
    return relToPrestoConverter.convert(rel);
  }

  /**
   * Converts input view definition to Presto SQL
   * @param dbName hive DB name
   * @param viewName hive view base name
   * @return Presto SQL matching input view definition
   */
  public String toPrestoSql(String dbName, String viewName) {
    RelNode rel = hiveToRelConverter.convertView(dbName, viewName);
    return relToPrestoConverter.convert(rel);
  }
}

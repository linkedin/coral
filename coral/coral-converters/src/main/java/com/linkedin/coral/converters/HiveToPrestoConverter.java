package com.linkedin.coral.converters;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.presto.rel2presto.RelToPrestoConverter;
import java.io.File;
import org.apache.calcite.rel.RelNode;


public class HiveToPrestoConverter {

  private final HiveToRelConverter hiveToRelConverter;
  private final RelToPrestoConverter rel2ToPrestoConverter;

  public HiveToPrestoConverter create(File hiveConfPath) {
    HiveToRelConverter hiveToRelConverter = HiveToRelConverter.create(hiveConfPath);
    RelToPrestoConverter relToPrestoConverter = new RelToPrestoConverter();
    return new HiveToPrestoConverter(hiveToRelConverter, relToPrestoConverter);
  }

  private HiveToPrestoConverter(HiveToRelConverter hiveToRelConverter,
      RelToPrestoConverter relToPrestoConverter) {
    this.hiveToRelConverter = hiveToRelConverter;
    this.rel2ToPrestoConverter = relToPrestoConverter;
  }

  /**
   * Converts input HiveQL to Presto SQL
   *
   * @param hiveSql hive sql query string
   * @return presto sql string representing input hiveSql
   */
  public String toPrestoSql(String hiveSql) {
    RelNode rel = hiveToRelConverter.convert(hiveSql);
    return rel2ToPrestoConverter.convert(rel);
  }
}

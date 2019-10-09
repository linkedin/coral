package com.linkedin.coral.tools;

import com.facebook.presto.sql.parser.ParsingOptions;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import com.linkedin.coral.presto.rel2presto.HiveToPrestoConverter;
import com.linkedin.coral.tests.MetastoreProvider;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.hadoop.hive.metastore.api.Table;

import static com.google.common.base.Preconditions.*;


public class ViewTranslationUtils {
  private ViewTranslationUtils() {
  }

  public static void convertToPrestoAndValidate(String db, String table, HiveToPrestoConverter converter,
      PrintWriter outputWriter) {
    final String prestoSql = toPrestoSql(db, table, converter);
    validatePrestoSql(prestoSql);
    if (outputWriter != null) {
      outputWriter.println(db + "." + table + ":");
      outputWriter.println(prestoSql);
      outputWriter.flush();
    }
  }

  public static String toViewString(Table table) {
    return table.getDbName() + "." + table.getTableName();
  }

  private static String toPrestoSql(String db, String table, HiveToPrestoConverter converter) {
    checkNotNull(table);
    return converter.toPrestoSql(db, table);
  }

  private static void validatePrestoSql(String sql) {
    final SqlParser parser = new SqlParser();
    final Statement statement = parser.createStatement(sql, new ParsingOptions());
    if (statement == null) {
      throw new RuntimeException("Failed to parse presto sql: " + sql);
    }
  }

  public static HiveMetastoreClient getMetastoreClient() throws Exception {
    final InputStream hiveConfStream =
        ViewTranslationUtils.class.getClassLoader().getResourceAsStream("hive.properties");
    final Properties props = new Properties();
    props.load(hiveConfStream);
    return new HiveMscAdapter(MetastoreProvider.getGridMetastoreClient(props));
  }

  public static Table getHiveTable(String dbTable, HiveMetastoreClient metaStoreClient) {
    final String[] dbTableParts = dbTable.split("\\.");
    Preconditions.checkState(dbTableParts.length == 2,
        String.format("<db>.<table> format is required. Provided %s", dbTable));
    return metaStoreClient.getTable(dbTableParts[0], dbTableParts[1]);
  }
}

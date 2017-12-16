package com.linkedin.coral.tools;

import com.facebook.presto.sql.parser.ParsingOptions;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import com.google.common.collect.ImmutableList;
import com.linkedin.coral.converters.HiveToPrestoConverter;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.parsetree.UnknownSqlFunctionException;
import com.linkedin.coral.tests.MetastoreProvider;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;

import javax.naming.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Tool cum integration test to connect to the hive metastore on grid
 * and verify translation of all hive view definitions.
 * This tool:
 *   1. Reads all databases and all tables within the database
 *   2. If the table is a 'virtual_view', this will translate the
 *      view definition to Presto SQL dialect
 *   3. Verifies that Presto SQL parser can successfully parse translated SQL.
 *   4. Tracks stats about failures and some of the common causes.
 *   5. Prints stats and failures
 * This is expected to continue through all kinds of translation failures.
 *
 * We have intentionally not integrated this as part of automated testing because:
 *   1. Hive metastore may have a large number of view definitions
 *   2. Running each time, as part of automated suite, can put load on metastore
 *   3. Build machine setup may not have credentials to connect to metastore
 *   4. Parallelizing translation to speed up can put more load on metastore in turn
 *
 * See {@link MetastoreProvider} for the required configuration properties
 */
@SuppressWarnings("unused")
public class ViewReader {

  public static void main(String[] args) throws IOException, MetaException, ConfigurationException {
    InputStream hiveConfStream = ViewReader.class.getClassLoader().getResourceAsStream("hive.properties");
    Properties props = new Properties();
    props.load(hiveConfStream);

    IMetaStoreClient metastoreClient = MetastoreProvider.getGridMetastoreClient(props);
    HiveMetastoreClient msc = new HiveMetaStoreClientAdapter(metastoreClient);
    HiveToPrestoConverter converter = HiveToPrestoConverter.create(msc);
    OutputStream ostr = System.out;
    translateAllViews(msc, converter, ostr);
  }

  // for debugging
  private void printTableInfo(Table table) {
    for (FieldSchema fieldSchema : table.getSd().getCols()) {
      System.out.println(fieldSchema.getName() + " : " + fieldSchema.getType());
    }
    System.out.println("Partitioning Columns");
    for (FieldSchema fieldSchema : table.getPartitionKeys()) {
      System.out.println(fieldSchema.getName() + " : " + fieldSchema.getType());
    }
  }

  private static class Stats {
    int datasets;

    int views;
    int failures;

    int daliviews;
    int daliviewfailures;
    int sqlFnErrors;

    @Override
    public String toString() {
      return String.format("datasets = %d, views = %d, failures = %d, successes = %d, daliviews = %d, daliFailures = %d, sqlFnErrors = %d",
          datasets, views, failures, (views - failures), daliviews, daliviewfailures, sqlFnErrors);
    }
  }

  private static void translateAllViews(HiveMetastoreClient metaStoreClient, HiveToPrestoConverter converter,
      OutputStream ostr) {
    List<String> allDatabases = metaStoreClient.getAllDatabases();
    PrintWriter writer = new PrintWriter(ostr);
    Stats stats = new Stats();
    List<String> failures = new ArrayList<>();
    Map<String, Integer> errorCategories = new HashMap<>();

    for (String db : allDatabases) {
      List<String> tables = metaStoreClient.getAllTables(db);
      ++stats.datasets;
      for (String tableName : tables) {
        try {
          Table table = metaStoreClient.getTable(db, tableName);
          if (!table.getTableType().equalsIgnoreCase("virtual_view")) {
            continue;
          }
          ++stats.views;
          boolean isDaliView = table.getOwner().equalsIgnoreCase("daliview");
          stats.daliviews += isDaliView ? 1 : 0;
          try {
            convertToPrestoAndValidate(table, converter);
          } catch (Exception e) {
            ++stats.failures;
            failures.add(db + "." + tableName);
            stats.daliviewfailures += isDaliView ? 1 : 0;
            if (e instanceof UnknownSqlFunctionException) {
              ++stats.sqlFnErrors;
            }
            errorCategories.merge(e.getClass().getName(), 1, (i, j) -> i + j);
          } catch (Throwable t) {
            writer.println(String.format("Unexpected error translating %s.%s, text: %s", db, tableName, table.getViewOriginalText()));
            ++stats.failures;
            failures.add(db + "." + tableName);
          }
        } catch (Throwable t) {
          writer.println(String.format("Unexpected error: %s for %s.%s", t.getMessage(), db, tableName));
          failures.add(db + "." + tableName);
          t.printStackTrace();
        }
      }

      if (stats.datasets % 10 == 0) {
        writer.println(stats);
        writer.flush();
      }
    }

    writer.println(stats);
    failures.forEach(writer::println);
    errorCategories.forEach((x, y) -> writer.println(x + " : " + y));
    writer.flush();
  }

  public static void translateTable(String db, String tableName, HiveMetastoreClient msc,
      HiveToPrestoConverter converter) {
    Table table = msc.getTable(db, tableName);
    convertToPrestoAndValidate(table, converter);
  }

  private static void convertToPrestoAndValidate(Table table, HiveToPrestoConverter converter) {
    validatePrestoSql(toPrestoSql(table, converter));
  }

  private static String toPrestoSql(Table table, HiveToPrestoConverter converter) {
    checkNotNull(table);
    checkArgument(table.getTableType().equalsIgnoreCase("virtual_view"));
    String viewText = table.getViewExpandedText();
    return converter.toPrestoSql(viewText);
  }

  private static void validatePrestoSql(String sql) {
    SqlParser parser = new SqlParser();
    Statement statement = parser.createStatement(sql, new ParsingOptions());
    if (statement == null) {
      throw new RuntimeException("Failed to parse presto sql: " + sql);
    }
  }

  static class HiveMetaStoreClientAdapter implements HiveMetastoreClient {

    final IMetaStoreClient client;

    HiveMetaStoreClientAdapter(IMetaStoreClient client) {
      this.client = client;
    }

    @Override
    public List<String> getAllDatabases() {
      try {
        return client.getAllDatabases();
      } catch (TException e) {
        return ImmutableList.of();
      }
    }

    @Override
    public Database getDatabase(String dbName) {
      try {
        return client.getDatabase(dbName);
      } catch (TException e) {
        return null;
      }
    }

    @Override
    public List<String> getAllTables(String dbName) {
      try {
        return client.getAllTables(dbName);
      } catch (TException e) {
        return ImmutableList.of();
      }
    }

    @Override
    public Table getTable(String dbName, String tableName) {
      try {
        return client.getTable(dbName, tableName);
      } catch (TException e) {
        return null;
      }
    }
  }
}

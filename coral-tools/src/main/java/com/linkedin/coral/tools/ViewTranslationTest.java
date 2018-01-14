package com.linkedin.coral.tools;

import com.facebook.presto.sql.parser.ParsingOptions;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import com.google.common.collect.ImmutableList;
import com.linkedin.coral.converters.HiveToPrestoConverter;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.functions.UnknownSqlFunctionException;
import com.linkedin.coral.tests.MetastoreProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.naming.ConfigurationException;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;

import static com.google.common.base.Preconditions.*;


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
public class ViewTranslationTest {

  public static final Pattern VIEW_NAME_PATTERN = Pattern.compile("([a-zA-Z0-9_]*)_(\\d+)_(\\d+)_(\\d+)$");

  public static void main(String[] args) throws IOException, MetaException, ConfigurationException {
    InputStream hiveConfStream = ViewTranslationTest.class.getClassLoader().getResourceAsStream("hive.properties");
    Properties props = new Properties();
    props.load(hiveConfStream);

    IMetaStoreClient metastoreClient = MetastoreProvider.getGridMetastoreClient(props);
    HiveMetastoreClient msc = new HiveMetaStoreClientAdapter(metastoreClient);
    HiveToPrestoConverter converter = HiveToPrestoConverter.create(msc);
    OutputStream ostr = System.out;
    // translateTable("l2m_mp_versioned", "message_received_event_zephyr_0_1_8", msc, converter);
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
      return String.format(
          "datasets = %d, views = %d, failures = %d, successes = %d, daliviews = %d, daliFailures = %d, sqlFnErrors = %d",
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
    Map<String, Integer> sqlFunctions = new HashMap<>();
    Set<String> translated = new HashSet<>();

    for (String db : allDatabases) {
      List<String> tables = metaStoreClient.getAllTables(db);
      // dali datasets have names ending with mp or mp_versioned
      // We focus only on dali datasets for the time-being because
      // processing all datasets is slow process
      if (!db.endsWith("_mp") && !db.endsWith("_mp_versioned")) {
        continue;
      }

      Set<String> maxVersionTables = latestViewVersions(tables);
      ++stats.datasets;
      boolean isDaliView = false;
      Table table = null;
      for (String tableName : maxVersionTables) {
        try {
          table = metaStoreClient.getTable(db, tableName);
          if (!table.getTableType().equalsIgnoreCase("virtual_view")) {
            continue;
          }
          ++stats.views;
          isDaliView = table.getOwner().equalsIgnoreCase("daliview");
          stats.daliviews += isDaliView ? 1 : 0;
          convertToPrestoAndValidate(table, converter);

        } catch (Exception e) {
          ++stats.failures;
          failures.add(db + "." + tableName);
          stats.daliviewfailures += isDaliView ? 1 : 0;
          if (e instanceof UnknownSqlFunctionException) {
            ++stats.sqlFnErrors;
            sqlFunctions.merge(((UnknownSqlFunctionException) e).getFunctionName(), 1, (i, j) -> i + j);
          }
          errorCategories.merge(e.getClass().getName(), 1, (i, j) -> i + j);
        } catch (Throwable t) {
          writer.println(String.format("Unexpected error translating %s.%s, text: %s", db, tableName,
              (table == null ? "null" : table.getViewOriginalText()))
          );
          ++stats.failures;
          failures.add(db + "." + tableName);
        }
        // set this to not carry over state for next iteration or when exiting
        table = null;
      }

      if (stats.datasets % 10 == 0) {
        writer.println(stats);
        writer.flush();
      }
    }

    writer.println("Failed datasets");
    failures.forEach(writer::println);
    writer.println("Error categories");
    errorCategories.forEach((x, y) -> writer.println(x + " : " + y));
    writer.println("Unknown functions");
    sqlFunctions.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .forEach(e -> writer.println(String.format("%s:%d", e.getKey(), e.getValue())));
    writer.println(stats);
    writer.flush();
  }

  private static class ViewName {
    final String basename;
    final Version version;
    static final Version VERSION_ZERO = new Version(0, 0, 0);

    static ViewName create(String fullViewName) {
      Matcher m = VIEW_NAME_PATTERN.matcher(fullViewName);
      if (!m.matches()) {
        return new ViewName(fullViewName, new Version(0, 0, 0));
      }

      return new ViewName(m.group(1),
          new Version(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
    }

    ViewName(String name, Version v) {
      this.basename = name;
      this.version = v;
    }

    String getBasename() {
      return basename;
    }

    Version getVersion() {
      return version;
    }

    boolean isSameView(ViewName rhs) {
      return basename.equals(rhs.basename);
    }

    @Override
    public String toString() {
      return version.equals(VERSION_ZERO) ? basename : String.join("_", basename, version.toString());
    }
  }

  static class Version implements Comparable<Version> {
    int major;
    int minor;
    int patch;

    Version(int major, int minor, int patch) {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
    }

    @Override
    public int compareTo(Version rhs) {
      if (major > rhs.major) {
        return 1;
      }
      if (major < rhs.major) {
        return -1;
      }
      // major = rhs.major
      if (minor > rhs.minor) {
        return 1;
      }
      if (minor < rhs.minor) {
        return -1;
      }
      return Integer.compare(patch, rhs.patch);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Version)) {
        return false;
      }

      Version version = (Version) o;

      if (major != version.major) {
        return false;
      }
      if (minor != version.minor) {
        return false;
      }
      return patch == version.patch;
    }

    @Override
    public int hashCode() {
      int result = major;
      result = 31 * result + minor;
      result = 31 * result + patch;
      return result;
    }

    @Override
    public String toString() {
      return String.join("_", String.valueOf(major), String.valueOf(minor), String.valueOf(patch));
    }
  }

  public static Set<String> latestViewVersions(List<String> allViews) {
    allViews.sort(String::compareTo);
    ViewName selected = new ViewName("", new Version(0, 0, 0));
    //Map<String, List<ViewName>> viewGroups =
    Map<String, Version> maxViews = allViews.stream()
        .map(ViewName::create)
        .collect(Collectors.groupingBy(ViewName::getBasename,
            Collectors.reducing(new Version(0, 0, 0), ViewName::getVersion, BinaryOperator.maxBy(Version::compareTo))));

    Set<String> views = maxViews.entrySet()
        .stream()
        .map(k -> new ViewName(k.getKey(), k.getValue()).toString())
        .collect(Collectors.toSet());
    return views;
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

/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools;

import com.linkedin.coral.hive.hive2rel.functions.UnknownSqlFunctionException;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.metastore.api.Table;

import static com.linkedin.coral.tools.ViewTranslationUtils.*;


/**
 * Gradle task to convert HQL of all Dali views to a supported target language (PrestoSQL, PigLatin, etc.)
 * and report the results.
 * If no target language is specified, the view translation task defaults to PrestoSQL validation.
 * Hive properties must be correctly configured before using this command. See {@link MetastoreProvider} for the
 * required configuration properties
 *
 * Run from console:
 *   ligradle translateAll -PresultDir=<result dir> [-Pinclude=<File for included dataset>] \
 *                     [-Pexclude=<File for excluded dataset>] [-Planguage=<Target language for validation>]
 *     - PresultDir is the directory that contains all reports about success datasets (successes.txt),
 *     failure datasets (failures.txt), all translated PrestoSQL (prestoSql.txt), and the summary (summary.txt)
 *     - Pinclude process only datasets included in this file. If not specified, process all datasets
 *     available in metastore.
 *     - Pexclude is the file that contains all datasets (one for each line, normally reusing failures.txt)
 *     that we want to exclude in the translation process.
 *     - Planguage is the query language that the DaliViews will be translated to and subsequently validated. If no
 *     option is specified, the target validation language defaults to PrestoSQL.
 *   Currently, we support local runs for the following languages:
 *     - PrestoSQL (-Planguage="presto")
 *
 * Run from grid gateway:
 *   To run on the gateway, we need the following JAR(s) on a gateway machine:
 *     - coral-tools-[version]-all.jar
 *   Then run:
 *     java -DresultDir=<result dir> [-Dinclude=<File for included dataset>]
 *                       [-Dexclude=<File for excluded dataset>] [-Dlanguage=<Target language for validation>] \
 *                       -cp coral-tools-[version]-all.jar com.linkedin.coral.tools.AllViewTranslation
 *       - DresultDir is the same as PresultDir above
 *       - Dinclude is same as Pinclude above
 *       - Dexclude is same as Pexclude above
 *       - Dlanguage is same as Planguage above
 *   Currently, we support runs on a gateway for the following languages:
 *     -Pig Latin (-Dlanguage="pig")
 *     -PrestoSQL (-Dlanguage="presto")
 *
 * This command:
 *   1. Reads all databases and all tables within the database
 *   2. If the table is a 'virtual_view', this will translate the
 *      view definition to the target language dialect
 *   3. Verifies that the target language parser can successfully parse translated SQL.
 *   4. Prints results into 4 reporting files in resultDir after processing every 10 datasets or 20 views
 * This is expected to continue through all kinds of translation failures.
 *
 * For details on coral regression test, check out: go/coral-regression.
 *
 */
public class AllViewTranslation {
  private final HiveMetastoreClient metaStoreClient;
  private final List<String> includededDatasets;
  private final List<String> excludedDatasets;
  private final PrintWriter sqlWriter;
  private final PrintWriter successWriter;
  private final PrintWriter failureWriter;
  private final PrintWriter summaryWriter;
  private final Stats stats;
  private final Map<String, Integer> errorCategories;
  private final Map<String, Integer> sqlFunctions;
  private final LanguageValidator validator;

  public AllViewTranslation(String resultDir, String includedFile, String excludedFile, LanguageValidator validator) throws Exception {
    metaStoreClient = getMetastoreClient();
    sqlWriter = makeWriter(resultDir, validator.getCamelName() + ".txt");
    successWriter = makeWriter(resultDir, "successes.txt");
    failureWriter = makeWriter(resultDir, "failures.txt");
    summaryWriter = makeWriter(resultDir, "summary.txt");
    includededDatasets = loadDatasets(includedFile);
    excludedDatasets = loadDatasets(excludedFile);
    errorCategories = new HashMap<>();
    sqlFunctions = new HashMap<>();
    stats = new Stats();
    this.validator = validator;
  }

  public static void main(String[] args) throws Exception {
    final String resultDir = System.getProperty("resultDir") != null ? System.getProperty("resultDir") : "results";
    FileUtils.forceMkdir(new File(resultDir));
    final String queryLanguage = System.getProperty("language") != null ? System.getProperty("language") : "presto";
    final AllViewTranslation viewTranslater =
        new AllViewTranslation(resultDir, System.getProperty("include"), System.getProperty("exclude"),
            ViewTranslationUtils.getValidator(queryLanguage));

    viewTranslater.translateAllViews();
  }

  private static List<String> loadDatasets(String fileName) throws FileNotFoundException {
    final List<String> datasets = new ArrayList<>();
    if (fileName != null) {
      final Scanner scanner = new Scanner(new File(fileName));
      while (scanner.hasNext()) {
        datasets.add(scanner.next());
      }
      scanner.close();
    }
    return datasets;
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

  private static PrintWriter makeWriter(String resultDir, String fileName) throws FileNotFoundException {
    return  new PrintWriter(new FileOutputStream(new File(String.join(File.separator, resultDir, fileName))));
  }

  private void translateAllViews() {
    if (!includededDatasets.isEmpty()) {
      for (String dataset : includededDatasets) {
        final Table table = getHiveTable(dataset, metaStoreClient);
        if (table != null) {
          convertView(table);
          if (stats.views % 20 == 0) {
            reportProgress();
          }
        }
      }
    } else {
      final List<String> allDatabases = metaStoreClient.getAllDatabases();
      for (String db : allDatabases) {
        if (!db.endsWith("_mp") && !db.endsWith("_mp_versioned")) {
          continue;
        }
        ++stats.datasets;
        final Set<String> maxVersionTables = latestViewVersions(metaStoreClient.getAllTables(db));
        for (String tableName : maxVersionTables) {
          final Table table = metaStoreClient.getTable(db, tableName);
          if (table != null) {
            convertView(table);
          }
        }
        if (stats.datasets % 10 == 0) {
          reportProgress();
        }
      }
    }

    summaryWriter.println("Error categories");
    errorCategories.forEach((x, y) -> summaryWriter.println(x + " : " + y));
    summaryWriter.println("Unknown functions");
    sqlFunctions.entrySet()
        .stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .forEach(e -> summaryWriter.println(String.format("%s:%d", e.getKey(), e.getValue())));
    summaryWriter.println(stats);
    reportProgress();
  }

  private void reportProgress() {
    System.out.println(stats);
    successWriter.flush();
    failureWriter.flush();
    sqlWriter.flush();
    summaryWriter.flush();
  }

  private void convertView(Table table) {
    if (excludedDatasets.contains(toViewString(table))) {
      return;
    }
    boolean isDaliView = false;
    try {
      if (!table.getTableType().equalsIgnoreCase("virtual_view")) {
        return;
      }
      ++stats.views;
      isDaliView = table.getOwner().equalsIgnoreCase("daliview");
      stats.daliviews += isDaliView ? 1 : 0;
      validator.convertAndValidate(table.getDbName(), table.getTableName(), metaStoreClient, sqlWriter);
      successWriter.println(toViewString(table));
    } catch (Exception e) {
      failureWriter.println(toViewString(table));
      ++stats.failures;
      stats.daliviewfailures += isDaliView ? 1 : 0;
      if (e instanceof UnknownSqlFunctionException) {
        ++stats.sqlFnErrors;
        sqlFunctions.merge(((UnknownSqlFunctionException) e).getFunctionName(), 1, (i, j) -> i + j);
      }
      if (e instanceof RuntimeException && e.getCause() != null) {
        errorCategories.merge(e.getCause().getClass().getName(), 1, (i, j) -> i + j);
      } else {
        errorCategories.merge(e.getClass().getName(), 1, (i, j) -> i + j);
      }
    } catch (Throwable t) {
      failureWriter.println(toViewString(table));
      ++stats.failures;
      System.out.println(String.format("Unexpected error translating %s.%s, text: %s",
          table.getDbName(), table.getTableName(), table.getViewOriginalText()));
    }
  }

  public static Set<String> latestViewVersions(List<String> allViews) {
    allViews.sort(String::compareTo);
    Map<String, Version> maxViews = allViews.stream()
        .map(ViewName::create)
        .collect(Collectors.groupingBy(ViewName::getBasename,
            Collectors.reducing(new Version(0, 0, 0), ViewName::getVersion, BinaryOperator
                .maxBy(Version::compareTo))));

    Set<String> views = maxViews.entrySet()
        .stream()
        .map(k -> new ViewName(k.getKey(), k.getValue()).toString())
        .collect(Collectors.toSet());
    return views;
  }
}

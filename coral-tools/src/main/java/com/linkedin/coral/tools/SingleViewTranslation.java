package com.linkedin.coral.tools;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import java.io.PrintWriter;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import static com.linkedin.coral.tools.ViewTranslationUtils.*;


/**
 * Gradle task to debug and convert to a supported target language (PrestoSQL, PigLatin, etc.) for a single Dali view.
 * Hive properties must be correctly configured before using this command. See {@link MetastoreProvider} for the
 * required configuration properties.
 *
 * Run locally from console:
 *   ligradle translate -Pview=<view name> [-Planguage=<Target language for validation>]
 *     - Pview is the name of the view in the form of '[dbName].[tableName]'
 *     - Planguage is the query language that the DaliViews will be translated to and subsequently validated. If no
 *     option is specified, we validate translations to PrestoSQL by default.
 *   Currently, we support local runs for the following languages:
 *     - PrestoSQL (-Planguage="presto")
 *
 * Run from grid gateway:
 *   To run on the gateway, we need the following JAR(s) on a gateway machine:
 *     - coral-tools-[version]-all.jar
 *   Then run:
 *     java -Dview=<view name> [-Dlanguage=<Target language for validation>] \
 *                       -cp coral-tools-[version]-all.jar com.linkedin.coral.tools.SingleViewTranslation
 *       - Dview is the same as Pview above
 *       - Dlanguage is the same as Planguage above
 *   Currently, we support runs on a gateway for the following languages:
 *     -Pig Latin (-Dlanguage="pig")
 *     -PrestoSQL (-Dlanguage="presto")
 *
 */
public class SingleViewTranslation {
  private SingleViewTranslation() {
  }

  public static void main(String[] args) throws Exception {
    final String view = System.getProperty("view");
    if (view == null) {
      System.out.println("View name is required. Please use -Pview=<view_name>");
      return;
    }
    final HiveMetastoreClient metastoreClient = getMetastoreClient();

    final String queryLanguage = System.getProperty("language") != null ? System.getProperty("language") : "presto";

    translateTable(view, metastoreClient, ViewTranslationUtils.getValidator(queryLanguage));
  }

  public static void translateTable(String dbTable, HiveMetastoreClient metaStoreClient, LanguageValidator validator) {
    final Table table = getHiveTable(dbTable, metaStoreClient);
    printTableInfo(table);

    System.out.println(validator.getStandardName() + ":");
    validator.convertAndValidate(table.getDbName(), table.getTableName(), metaStoreClient,
        new PrintWriter(System.out));
  }

  private static void printTableInfo(Table table) {
    System.out.println("View schema:");
    for (FieldSchema fieldSchema : table.getSd().getCols()) {
      System.out.println(fieldSchema.getName() + " : " + fieldSchema.getType());
    }

    System.out.println("Partitioning columns:");
    for (FieldSchema fieldSchema : table.getPartitionKeys()) {
      System.out.println(fieldSchema.getName() + " : " + fieldSchema.getType());
    }

    if (table.getTableType().equalsIgnoreCase("virtual_view")) {
      System.out.println("HQL query:");
      System.out.println(table.getViewExpandedText());
    }
  }
}

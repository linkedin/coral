package com.linkedin.coral.tools;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.presto.rel2presto.HiveToPrestoConverter;
import java.io.PrintWriter;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import static com.linkedin.coral.tools.ViewTranslationUtils.*;


/**
 * Gralde task to debug and convert to PrestoSQL for a single Dali view. Hive properties
 * must be correctly configured before using this command. See {@link MetastoreProvider} for the
 * required configuration properties.
 *
 * Run from console:
 * ligradle generate -Pview=<view name>
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

    translateTable(view, metastoreClient);
  }

  public static void translateTable(String dbTable, HiveMetastoreClient metaStoreClient) {
    final Table table = getHiveTable(dbTable, metaStoreClient);
    printTableInfo(table);

    System.out.println("PrestoSQL:");
    convertToPrestoAndValidate(table.getDbName(), table.getTableName(),
        HiveToPrestoConverter.create(metaStoreClient), new PrintWriter(System.out));
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

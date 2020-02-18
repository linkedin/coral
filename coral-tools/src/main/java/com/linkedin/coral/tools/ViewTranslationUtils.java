/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import com.linkedin.coral.tests.MetastoreProvider;
import java.io.InputStream;
import java.util.Properties;
import org.apache.hadoop.hive.metastore.api.Table;


public class ViewTranslationUtils {
  private ViewTranslationUtils() {
  }

  public static LanguageValidator getValidator(String queryLanguage) throws IllegalArgumentException {
    switch (queryLanguage.trim().toLowerCase()) {
      case "presto":
        return new PrestoValidator();
      case "pig":
        return new PigValidator();
      default:
        throw new IllegalArgumentException("Validator for %s is not supported.");
    }
  }

  public static String toViewString(Table table) {
    return table.getDbName() + "." + table.getTableName();
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

/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;
import com.linkedin.coral.trino.trino2rel.TrinoToRelConverter;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class TranslationUtils {

  public static String translateTrinoToSpark(String query) {
    RelNode relNode = new TrinoToRelConverter(hiveMetastoreClient).convertSql(query);
    CoralSpark coralSpark = CoralSpark.create(relNode, hiveMetastoreClient);
    return coralSpark.getSparkSql();
  }

  public static String translateHiveToTrino(String query) {
    RelNode relNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    return new RelToTrinoConverter(hiveMetastoreClient).convert(relNode);
  }

  public static String translateHiveToSpark(String query) {
    RelNode relNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    CoralSpark coralSpark = CoralSpark.create(relNode, hiveMetastoreClient);
    return coralSpark.getSparkSql();
  }

  public static String translateQuery(String query, String sourceLanguage, String targetLanguage) {
    String translatedSql = null;

    // TODO: add more translations once n-to-one-to-n is completed
    // From Trino
    if (sourceLanguage.equalsIgnoreCase("trino")) {
      // To Spark
      if (targetLanguage.equalsIgnoreCase("spark")) {
        translatedSql = translateTrinoToSpark(query);
      }
    }
    // From Hive or Spark
    else if (sourceLanguage.equalsIgnoreCase("hive") || sourceLanguage.equalsIgnoreCase("spark")) {
      // To Spark
      if (targetLanguage.equalsIgnoreCase("spark")) {
        translatedSql = translateHiveToSpark(query);
      }
      // To Trino
      else if (targetLanguage.equalsIgnoreCase("trino")) {
        translatedSql = translateHiveToTrino(query);
      }
    }

    return translatedSql;
  }
}

/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.spark.CoralSpark;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class TranslationUtils {

  public static String translateTrinoToSpark(String query) {
    RelNode relNode = trinoToRelConverter.convertSql(query);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    return coralSpark.getSparkSql();
  }

  public static String translateHiveToTrino(String query) {
    RelNode relNode = hiveToRelConverter.convertSql(query);
    return relToTrinoConverter.convert(relNode);
  }

  public static String translateHiveToSpark(String query) {
    RelNode relNode = hiveToRelConverter.convertSql(query);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    return coralSpark.getSparkSql();
  }
}

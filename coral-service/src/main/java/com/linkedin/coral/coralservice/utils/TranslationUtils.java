package com.linkedin.coral.coralservice.utils;

import com.linkedin.coral.spark.CoralSpark;
import org.apache.calcite.rel.RelNode;

import static com.linkedin.coral.coralservice.utils.CoralUtils.*;


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

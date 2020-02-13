package com.linkedin.coral.tools;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.spark.CoralSpark;
import java.io.PrintWriter;
import org.apache.calcite.rel.RelNode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.parser.ParseException;


public class SparkValidator implements LanguageValidator {
  @Override
  public String getCamelName() {
    return "SparkSQL";
  }

  @Override
  public String getStandardName() {
    return "Spark SQL";
  }

  @Override
  public void convertAndValidate(String db, String table, HiveMetastoreClient hiveMetastoreClient,
      PrintWriter outputWriter) {
    SparkSession spark = SparkSession
        .builder()
        .appName("CoralTools")
        .config("spark.master", "local")
        .getOrCreate();
    spark.sparkContext().setLogLevel("ERROR");
    CoralSpark coralSparkTranslation = getCoralSparkTranslation(db, table, hiveMetastoreClient);
    validateSparkSql(spark, coralSparkTranslation);
    if (outputWriter != null) {
      outputWriter.println(db + "." + table + ":");
      outputWriter.println(coralSparkTranslation.getSparkSql());
      outputWriter.flush();
    }
  }

  private void validateSparkSql(SparkSession spark, CoralSpark coralSparkTranslation) {
    String sql = coralSparkTranslation.getSparkSql();
    try {
      spark.sessionState().sqlParser().parsePlan(sql);
    } catch (ParseException e) {
      throw new RuntimeException("Failed to parse spark sql: " + sql, e);
    }
  }

  CoralSpark getCoralSparkTranslation(String db, String table, HiveMetastoreClient hiveMetastoreClient) {
    final HiveToRelConverter hiveToRelConverter = HiveToRelConverter.create(hiveMetastoreClient);
    final RelNode rel = hiveToRelConverter.convertView(db, table);
    return CoralSpark.create(rel);
  }
}

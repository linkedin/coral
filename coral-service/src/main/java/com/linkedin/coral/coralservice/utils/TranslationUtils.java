/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.common.calcite.sql.SqlCommand;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;
import com.linkedin.coral.trino.trino2rel.TrinoToRelConverter;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class TranslationUtils {

  public static String translateTrinoToSpark(String query) {
    RelNode relNode = new TrinoToRelConverter(hiveMetastoreClient).convertSql(query);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    return coralSpark.getSparkSql();
  }

  public static String translateHiveToTrino(String query) {
    RelNode relNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    return new RelToTrinoConverter().convert(relNode);
  }

  public static String translateHiveToSpark(String query) {
    HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
    SqlNode sqlNode = hiveToRelConverter.toSqlNode(query);
    if (sqlNode instanceof SqlCommand) {
      SqlNode selectNode = ((SqlCommand) sqlNode).getSelectQuery();
      SqlNode selectSparkNode = convertHiveSqlNodeToCoralNode(hiveToRelConverter, selectNode);
      ((SqlCommand) sqlNode).setSelectQuery(selectSparkNode);
    } else {
      sqlNode = convertHiveSqlNodeToCoralNode(hiveToRelConverter, sqlNode);
    }
    return CoralSpark.constructSparkSQL(sqlNode);
  }

  private static SqlNode convertHiveSqlNodeToCoralNode(HiveToRelConverter hiveToRelConverter, SqlNode sqlNode) {
    RelNode relNode = hiveToRelConverter.toRel(sqlNode);
    SqlNode coralSqlNode = CoralSpark.getCoralSqlNode(relNode);
    return coralSqlNode;
  }
}

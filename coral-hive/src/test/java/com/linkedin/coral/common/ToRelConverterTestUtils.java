/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.io.IOException;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.hive.hive2rel.TestUtils;

import static org.testng.Assert.*;


public class ToRelConverterTestUtils {

  private static TestUtils.TestHive hive;
  private static IMetaStoreClient msc;
  public static HiveToRelConverter converter;

  public static void setup(HiveConf conf) throws IOException, HiveException, MetaException {
    hive = TestUtils.setupDefaultHive(conf);
    msc = hive.getMetastoreClient();
    HiveMscAdapter mscAdapter = new HiveMscAdapter(msc);
    converter = new HiveToRelConverter(mscAdapter);
  }

  public static IMetaStoreClient getMsc() {
    return msc;
  }

  public static RelNode toRel(String sql) {
    return converter.convertSql(sql);
  }

  public static String relToStr(RelNode rel) {
    return RelOptUtil.toString(rel);
  }

  public static String sqlToRelStr(String sql) {
    return relToStr(toRel(sql));
  }

  public static SqlNode viewToSqlNode(String database, String table) {
    return converter.processView(database, table);
  }

  public static String nodeToStr(SqlNode sqlNode) {
    RelNode relNode = converter.toRel(sqlNode);
    return relToSql(relNode);
  }

  public static RelBuilder createRelBuilder() {
    return converter.getRelBuilder();
  }

  public static void verifyRel(RelNode input, RelNode expected) {
    assertEquals(input.getInputs().size(), expected.getInputs().size());
    for (int i = 0; i < input.getInputs().size(); i++) {
      verifyRel(input.getInput(i), expected.getInput(i));
    }
    RelOptUtil.areRowTypesEqual(input.getRowType(), expected.getRowType(), true);
  }

  public static String relToSql(RelNode rel) {
    RelToSqlConverter rel2sql = new RelToSqlConverter(SqlDialect.DatabaseProduct.POSTGRESQL.getDialect());
    return rel2sql.visitChild(0, rel).asStatement().toSqlString(SqlDialect.DatabaseProduct.POSTGRESQL.getDialect())
        .getSql();
  }

  public static String relToHql(RelNode rel) {
    RelToSqlConverter rel2sql = new RelToSqlConverter(SqlDialect.DatabaseProduct.HIVE.getDialect());
    return rel2sql.visitChild(0, rel).asStatement().toSqlString(SqlDialect.DatabaseProduct.HIVE.getDialect()).getSql();
  }
}

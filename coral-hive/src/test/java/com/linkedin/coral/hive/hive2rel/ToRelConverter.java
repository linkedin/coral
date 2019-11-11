package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;

import static org.testng.Assert.*;


class ToRelConverter {

  private static TestUtils.TestHive hive;
  private static IMetaStoreClient msc;
  static HiveToRelConverter converter;
  static RelContextProvider relContextProvider;

  static void setup() throws IOException, HiveException, MetaException {
    hive = TestUtils.setupDefaultHive();
    msc = hive.getMetastoreClient();
    HiveMscAdapter mscAdapter = new HiveMscAdapter(msc);
    converter = HiveToRelConverter.create(mscAdapter);
    // for validation
    HiveSchema schema = new HiveSchema(mscAdapter);
    relContextProvider = new RelContextProvider(schema);
  }

  public static IMetaStoreClient getMsc() {
    return msc;
  }

  public static RelContextProvider getRelContextProvider() { return relContextProvider; }

  static RelNode toRel(String sql) {
    return converter.convertSql(sql);
  }

  static String relToStr(RelNode rel) {
    return RelOptUtil.toString(rel);
  }

  static String sqlToRelStr(String sql) {
    return relToStr(toRel(sql));
  }

  static SqlNode viewToSqlNode(String database, String table) {
    return converter.getTreeBuilder().processView(database, table);
  }

  static String nodeToStr(SqlNode sqlNode) {
    RelNode relNode = converter.toRel(sqlNode);
    return relToSql(relNode);
  }

  static String viewToRelStr(String database, String table) {
    RelNode rel = converter.convertView(database, table);
    return relToStr(rel);
  }

  static RelBuilder createRelBuilder() {
    return RelBuilder.create(relContextProvider.getConfig());
  }

  static void verifyRel(RelNode input, RelNode expected) {
    assertEquals(input.getInputs().size(), expected.getInputs().size());
    for (int i = 0; i < input.getInputs().size(); i++) {
      verifyRel(input.getInput(i), expected.getInput(i));
    }
    RelOptUtil.areRowTypesEqual(input.getRowType(), expected.getRowType(), true);
  }

  static String relToSql(RelNode rel) {
    RelToSqlConverter rel2sql = new RelToSqlConverter(SqlDialect.DatabaseProduct.POSTGRESQL.getDialect());
    return rel2sql.visitChild(0, rel)
        .asStatement()
        .toSqlString(SqlDialect.DatabaseProduct.POSTGRESQL.getDialect()).getSql();
  }

  static String relToHql(RelNode rel) {
    RelToSqlConverter rel2sql = new RelToSqlConverter(SqlDialect.DatabaseProduct.HIVE.getDialect());
    return rel2sql.visitChild(0, rel)
        .asStatement()
        .toSqlString(SqlDialect.DatabaseProduct.HIVE.getDialect()).getSql();
  }
}

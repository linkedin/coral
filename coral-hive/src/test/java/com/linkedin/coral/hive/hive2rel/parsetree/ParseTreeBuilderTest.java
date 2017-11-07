package com.linkedin.coral.hive.hive2rel.parsetree;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClientProvider;
import com.linkedin.coral.hive.hive2rel.HiveSchema;
import com.linkedin.coral.hive.hive2rel.RelContextProvider;
import com.linkedin.coral.hive.hive2rel.TestUtils;
import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.TestUtils.*;


public class ParseTreeBuilderTest {

  private static TestHive hive;
  private static RelContextProvider relProvider;
  private static SqlToRelConverter converter;

  @BeforeClass
  public static void beforeClass() throws HiveException, IOException {
    hive = TestUtils.setupDefaultHive();
    HiveMetastoreClientProvider mscProvider = new HiveMetastoreClientProvider(hive.getConf());
    HiveSchema schema = new HiveSchema(mscProvider.getMetastoreClient());
    relProvider = new RelContextProvider(schema);
    converter = relProvider.getSqlToRelConverter();
  }

  /*driver.run("CREATE TABLE IF NOT EXISTS test.tableOne(a int, b varchar(30), c double, d timestamp)");
  driver.run("CREATE TABLE IF NOT EXISTS test.tableTwo(x int, y double)");
  driver.run("CREATE TABLE IF NOT EXISTS foo(a int, b varchar(30), c double)");
  driver.run("CREATE TABLE IF NOT EXISTS bar(x int, y double)");
  */
  @Test
  public void testBasicSql() {
    {
      String sql = "SELECT a, -c, a+c, (c>10), (c > 10 AND a < 25) from test.tableOne where c > 10 AND a < 15 OR b = 'abc'";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
      System.out.println(toRelStr(converted));
    }
    {
      String sql = "SELECT * from test.tableTwo order by x desc, y asc";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
      System.out.println(toRelStr(converted));
    }
    {
      String sql = "SELECT a, sum(c), count(*) from foo group by b, a";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
      System.out.println(toRelStr(converted));
    }
    {
      String sql = "SELECT c[0], s.name from complex";
      System.out.println(toAST(sql).dump());
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
      //System.out.println(toRelStr(converted));
    }
    {
      String sql = "SELECT a, b from (select x as a, y as b from bar) f";
      System.out.println(toAST(sql).dump());
      SqlNode n = convert(sql);
      System.out.println(n);
      System.out.println(toRelStr(n));
    }
    {
      String sql = "SELECT a, c from foo union all select x, y from bar";
      SqlNode n = convert(sql);
      RelNode r = toRel(n);
      System.out.println(RelOptUtil.toString(r));
      System.out.println(n);
    }
    {
      String sql = "SELECT a, b from foo where a in  (select x from bar where y < 10)";
      SqlNode n = convert(sql);
      System.out.println(n);
      System.out.println(RelOptUtil.toString(toRel(n)));
    }
  }

  private static SqlNode convert(String sql) {
    ParseTreeBuilder builder = new ParseTreeBuilder();
    return builder.process(sql);
  }

  static String toRelStr(SqlNode node) {
    return RelOptUtil.toString(toRel(node));
  }

  static RelNode toRel(SqlNode node) {
    try {
      return converter.convertQuery(node, true, true).rel;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

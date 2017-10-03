package com.linkedin.coral.hive.hive2rel.tree;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.Context;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.apache.hadoop.hive.ql.parse.HiveParser.*;
import static org.testng.Assert.*;


public class QueryTest {
  private static Context context;

  @BeforeClass
  public static void beforeClass() throws IOException {
//    context = getContext();
  }

  private static Context getContext() throws IOException {
    Configuration conf = new Configuration();
    conf.set("_hive.hdfs.session.path", ".");
    conf.set("_hive.local.session.path", ".");
    conf.addResource(new Path("hive-site.xml"));
    InputStream hiveConfigStream = QueryTest.class.getClassLoader().getResourceAsStream("hive.xml");
    conf.addResource(hiveConfigStream);
    conf.set("_hive.hdfs.session.path", ".");
    conf.set("_hive.local.session.path", ".");
    return new Context(conf);

  }

  @Test
  public void testCreate() throws Exception {
    String sql = "SELECT * FROM myTable where a > 10 group by b having sum(c) > 100 order by a desc limit 10";
    Query query = parse(sql);
    verifyFrom(query.getFrom(), "myTable");
    assertEquals(query.getGroupBy().getType(), TOK_GROUPBY);
    assertEquals(query.getHaving().getType(), TOK_HAVING);
    assertEquals(query.getOrderBy().getType(), TOK_ORDERBY);
    assertEquals(query.getLimit().getType(), TOK_LIMIT);
    assertEquals(query.getWhere().getType(), TOK_WHERE);
    assertEquals(query.getSelects().size(), 1);
    assertEquals(query.getSelects().get(0).getType(), TOK_SELEXPR);
  }

  private static void verifyFrom(ASTNode from, String table) {
    assertNotNull(from);
    assertEquals(from.getType(), TOK_FROM);
    assertEquals(getNode(from, 1).getType(), TOK_TABREF);
    assertEquals(getNode(from, 2).getType(), TOK_TABNAME, getNode(from, 2).dump());
    assertEquals(getNode(from, 3).getText(), table);
  }

  private static ASTNode getNode(ASTNode node, int depth) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(depth > 0);
    ASTNode child = node;

    while (depth > 0) {
      child = (ASTNode) child.getChildren().get(0);
      --depth;
    }
    return child;
  }

  private Query parse(String sql) throws ParseException {
    ParseDriver driver = new ParseDriver();
    ASTNode parseTree = driver.parse(sql);
    Preconditions.checkNotNull(parseTree);
    ArrayList<Node> children = parseTree.getChildren();
    Preconditions.checkState(children.size() == 2, "Atleast two children are expected");
    Preconditions.checkState(((ASTNode) children.get(0)).getType() == HiveParser.TOK_QUERY);
    return Query.create((ASTNode) children.get(0));
  }
}

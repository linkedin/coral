package com.linkedin.coral.hive.hive2rel.parsetree;

import com.linkedin.coral.hive.hive2rel.TestUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class ASTUtilsTest {

  @Test
  public void testGetChildAtDepth() throws Exception {
    String sql = "SELECT a, b from myTable";
    ASTNode root = (ASTNode) TestUtils.toAST(sql).getChildren().get(0);
    Query query = Query.create(root);
    ASTNode child =
        ASTUtils.getChildAtPath(query.getFrom(), new int[]{HiveParser.TOK_TABREF, HiveParser.TOK_TABNAME});
    assertEquals(child.getText(), "myTable");
  }
}

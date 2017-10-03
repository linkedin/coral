package com.linkedin.coral.hive.hive2rel.tree;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.*;


public class Query extends AbstractASTVisitor<Query> implements ASTVisitorContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(Query.class);
  private ASTNode from;
  private ASTNode where;
  private ASTNode having;
  private ASTNode orderBy;
  private ASTNode groupBy;
  private List<ASTNode> selects = new ArrayList<>();
  private ASTNode limit;

  public static Query create(ASTNode queryNode) {
    checkNotNull(queryNode);
    Preconditions.checkArgument(queryNode.getType() == HiveParser.TOK_QUERY,
        "Expected root level query node for parsing, received: %s", queryNode.getText());

    Query query = new Query();
    query.visit(queryNode, query);
    return query;
  }

  public ASTNode getFrom() {
    return from;
  }

  public ASTNode getWhere() {
    return where;
  }

  public ASTNode getHaving() {
    return having;
  }

  public ASTNode getOrderBy() {
    return orderBy;
  }

  public ASTNode getGroupBy() {
    return groupBy;
  }

  public List<ASTNode> getSelects() {
    return selects;
  }

  public ASTNode getLimit() {
    return limit;
  }

  protected void visitLimit(ASTNode node, Query query) {
    query.limit = node;
  }

  protected void visitSelects(ASTNode node, Query query) {
    ArrayList<Node> children = node.getChildren();
    checkNotNull(children);

    node.getChildren().forEach(c -> query.selects.add((ASTNode) c));
  }

  protected void visitOrderBy(ASTNode node, Query query) {
    query.orderBy = node;
  }

  protected void visitGroupBy(ASTNode node, Query query) {
    query.groupBy = node;
  }

  protected void visitHaving(ASTNode node, Query query) {
    query.having = node;
  }

  protected void visitFrom(ASTNode node, Query query) {
    query.from = node;
  }

  protected void visitWhere(ASTNode node, Query query) {
    query.where = node;
  }
}

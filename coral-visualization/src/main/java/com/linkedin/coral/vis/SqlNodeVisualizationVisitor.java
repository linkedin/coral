/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlVisitor;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.LinkTarget;
import guru.nidi.graphviz.model.Node;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;
import static guru.nidi.graphviz.model.Factory.*;


/**
 * A visitor that converts a SqlNode tree to a graphviz node.
 */
public class SqlNodeVisualizationVisitor implements SqlVisitor<Node> {
  @Override
  public Node visit(SqlLiteral literal) {
    return node(literal.toString());
  }

  @Override
  public Node visit(SqlCall call) {
    if (call instanceof SqlSelect) {
      SqlSelect sqlSelect = (SqlSelect) call;
      List<LinkTarget> edges = new ArrayList<>();
      if (sqlSelect.getFrom() != null) {
        edges.add(edge(sqlSelect.getFrom(), "from"));
      }
      if (sqlSelect.getWhere() != null) {
        edges.add(edge(sqlSelect.getWhere(), "where"));
      }
      if (sqlSelect.getGroup() != null) {
        edges.add(edge(sqlSelect.getGroup(), "group_by"));
      }
      if (sqlSelect.getHaving() != null) {
        edges.add(edge(sqlSelect.getHaving(), "having"));
      }
      if (sqlSelect.getSelectList() != null) {
        edges.add(edge(sqlSelect.getSelectList(), "select_list"));
      } else {
        edges.add(edge(createSelectStarSqlNodeList(), "select_list"));
      }
      if (sqlSelect.getOrderList() != null) {
        edges.add(edge(sqlSelect.getOrderList(), "order_by_list"));
      }
      Node node = node("SELECT").link(edges.toArray(new LinkTarget[0]));
      return node;
    } else if (call instanceof SqlJoin) {
      SqlJoin sqlJoin = (SqlJoin) call;
      List<LinkTarget> edges = new ArrayList<>();
      edges.add(edge(sqlJoin.getLeft(), "left"));
      edges.add(edge(sqlJoin.getRight(), "right"));
      if (sqlJoin.getCondition() != null) {
        edges.add(edge(sqlJoin.getCondition(), "condition"));
      }
      return node("JOIN").link(edges.toArray(new LinkTarget[0]));
    } else if (call instanceof SqlBasicCall) {
      SqlBasicCall sqlBasicCall = (SqlBasicCall) call;
      List<LinkTarget> edges = new ArrayList<>();
      for (SqlNode operand : sqlBasicCall.getOperandList()) {
        edges.add(to(operand.accept(this)));
      }
      return node(sqlBasicCall.getOperator().getName()).link(edges.toArray(new LinkTarget[0]));
    }
    return node("TBD");
  }

  @Override
  public Node visit(SqlNodeList nodeList) {
    List<LinkTarget> edges = new ArrayList<>();
    int i = 0;
    for (SqlNode node : nodeList.getList()) {
      edges.add(edge(node, "list_item_" + i++));
    }
    return node("LIST").link(edges.toArray(new LinkTarget[0]));
  }

  @Override
  public Node visit(SqlIdentifier id) {
    return node(id.toString());
  }

  @Override
  public Node visit(SqlDataTypeSpec type) {
    return node("TBD");
  }

  @Override
  public Node visit(SqlDynamicParam param) {
    return node("TBD");
  }

  @Override
  public Node visit(SqlIntervalQualifier intervalQualifier) {
    return node("TBD");
  }

  private Link edge(SqlNode target, String label) {
    return to(target.accept(this)).with(Label.of(label));
  }

  private static Node node(String label) {
    return Factory.node(UUID.randomUUID().toString()).with(Label.of(label));
  }

  private static SqlNodeList createSelectStarSqlNodeList() {
    Collection<SqlNode> sqlNodeList = new ArrayList();
    sqlNodeList.add(createStarIdentifier(SqlParserPos.ZERO));
    SqlNodeList selectStar = createSqlNodeList(sqlNodeList);
    return selectStar;
  }
}

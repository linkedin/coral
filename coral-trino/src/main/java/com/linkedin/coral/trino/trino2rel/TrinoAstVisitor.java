/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.hadoop.hive.metastore.api.Table;

import io.trino.sql.tree.*;

import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


public class TrinoAstVisitor extends AstVisitor<SqlNode, TrinoAstVisitor.ParseContext> {
  @Override
  protected SqlNode visitQuerySpecification(QuerySpecification node, ParseContext context) {
    process(node.getSelect(), context);
    if (node.getFrom().isPresent()) {
      process(node.getFrom().get(), context);
    }
    SqlSelect select = new SqlSelect(ZERO, context.keywords, context.selects, context.from, context.where,
        context.grpBy, context.having, null, context.orderBy, null, context.fetch);
    return select;
  }

  @Override
  protected SqlNode visitQuery(Query node, ParseContext context) {
    ParseContext parseContext = new ParseContext(null);
    return process(node.getQueryBody(), parseContext);
  }

  @Override
  protected SqlNode visitSingleColumn(SingleColumn node, ParseContext context) {
    return process(node.getExpression(), context);
  }

  @Override
  protected SqlNode visitSelect(Select node, ParseContext context) {
    List<SqlNode> sqlNodes = new ArrayList<>();
    for (Node item : node.getSelectItems()) {
      sqlNodes.add(process(item, context));
    }
    context.selects = new SqlNodeList(sqlNodes, ZERO);
    return context.selects;
  }

  @Override
  protected SqlNode visitDecimalLiteral(DecimalLiteral node, ParseContext context) {
    return SqlLiteral.createExactNumeric(node.getValue(), ZERO);
  }

  @Override
  protected SqlNode visitLongLiteral(LongLiteral node, ParseContext context) {
    return SqlLiteral.createExactNumeric(String.valueOf(node.getValue()), ZERO);
  }

  @Override
  protected SqlNode visitLiteral(Literal node, ParseContext context) {
    return SqlLiteral.createExactNumeric(node.toString(), ZERO);
  }

  @Override
  protected SqlNode visitGenericLiteral(GenericLiteral node, ParseContext context) {
    return SqlLiteral.createExactNumeric(String.valueOf(node.getValue()), ZERO);
  }

  class ParseContext {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Table> hiveTable;
    SqlNodeList keywords;
    SqlNode from;
    SqlNodeList selects;
    SqlNode where;
    SqlNodeList grpBy;
    SqlNode having;
    SqlNode fetch;
    SqlNodeList orderBy;

    ParseContext(@Nullable Table hiveTable) {
      this.hiveTable = Optional.ofNullable(hiveTable);
    }

    Optional<Table> getHiveTable() {
      return hiveTable;
    }
  }
}

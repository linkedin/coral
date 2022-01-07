/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.linkedin.coral.hive.hive2rel.parsetree.parser.ASTNode;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.HiveParser;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.Node;


/**
 * Abstract visitor (actually, a walker) to hive AST.
 * This class implements a walker that calls specific named methods
 * passing corresponding {@link ASTNode}.
 *
 * By default, this visits all children of the node
 *
 * @param <C> Visitor context that is passed to all the visitor methods
 */
public abstract class AbstractASTVisitor<R, C> {

  /**
   * Provides a AST visitor functionality by calling
   * appropriately named visitor method based on node type.
   *
   * We need to provide this because Hive AST object model
   * does not provide properly typed nodes accepting visitors.
   *
   * @param node hive parse tree node to visit
   * @param ctx abstract context passed to visitor methods
   * @return return the value from visitor node
   */
  protected R visit(ASTNode node, C ctx) {

    if (node == null) {
      return null;
    }
    switch (node.getType()) {
      case 0:
        return visitNil(node, ctx);

      case HiveParser.TOK_SUBQUERY:
        return visitSubquery(node, ctx);

      case HiveParser.TOK_SUBQUERY_EXPR:
        return visitSubqueryExpr(node, ctx);

      case HiveParser.TOK_SUBQUERY_OP:
        return visitSubqueryOp(node, ctx);

      case HiveParser.TOK_FROM:
        return visitFrom(node, ctx);

      case HiveParser.TOK_UNIONTYPE:
      case HiveParser.TOK_UNIONALL:
        return visitUnion(node, ctx);

      case HiveParser.TOK_QUERY:
        return visitQueryNode(node, ctx);

      case HiveParser.TOK_TABREF:
        return visitTabRefNode(node, ctx);

      case HiveParser.TOK_TABNAME:
        return visitTabnameNode(node, ctx);

      case HiveParser.KW_CURRENT_DATE:
      case HiveParser.KW_CURRENT_TIMESTAMP:
      case HiveParser.Identifier:
        return visitIdentifier(node, ctx);

      case HiveParser.StringLiteral:
        return visitStringLiteral(node, ctx);

      case HiveParser.BigintLiteral:
        return visitBigintLiteral(node, ctx);

      case HiveParser.TOK_INSERT:
        return visitInsert(node, ctx);

      case HiveParser.TOK_SELECTDI:
        return visitSelectDistinct(node, ctx);

      case HiveParser.TOK_LIMIT:
        return visitLimit(node, ctx);

      case HiveParser.TOK_SELECT:
        return visitSelect(node, ctx);

      case HiveParser.TOK_SELEXPR:
        return visitSelectExpr(node, ctx);

      case HiveParser.TOK_ALLCOLREF:
        return visitAllColRef(node, ctx);

      case HiveParser.TOK_HAVING:
        return visitHaving(node, ctx);

      case HiveParser.TOK_WHERE:
        return visitWhere(node, ctx);

      case HiveParser.TOK_GROUPBY:
        return visitGroupBy(node, ctx);

      case HiveParser.TOK_ORDERBY:
        return visitOrderBy(node, ctx);

      case HiveParser.TOK_TABSORTCOLNAMEASC:
        return visitSortColNameAsc(node, ctx);

      case HiveParser.TOK_TABSORTCOLNAMEDESC:
        return visitSortColNameDesc(node, ctx);

      case HiveParser.TOK_FUNCTION:
        return visitFunction(node, ctx);

      case HiveParser.TOK_FUNCTIONDI:
        return visitFunctionDistinct(node, ctx);

      case HiveParser.TOK_FUNCTIONSTAR:
        return visitFunctionStar(node, ctx);

      case HiveParser.DOT:
        return visitDotOperator(node, ctx);

      case HiveParser.PLUS:
      case HiveParser.MINUS:
      case HiveParser.DIVIDE:
      case HiveParser.STAR:
      case HiveParser.MOD:
      case HiveParser.AMPERSAND:
      case HiveParser.TILDE:
      case HiveParser.BITWISEOR:
      case HiveParser.BITWISEXOR:
      case HiveParser.KW_OR:
      case HiveParser.KW_AND:
      case HiveParser.LESSTHAN:
      case HiveParser.LESSTHANOREQUALTO:
      case HiveParser.GREATERTHAN:
      case HiveParser.GREATERTHANOREQUALTO:
      case HiveParser.EQUAL:
      case HiveParser.NOTEQUAL:
      case HiveParser.EQUAL_NS:
      case HiveParser.KW_NOT:
      case HiveParser.KW_LIKE:
      case HiveParser.KW_RLIKE:
      case HiveParser.KW_REGEXP:
        return visitOperator(node, ctx);

      case HiveParser.LSQUARE:
        return visitLParen(node, ctx);

      case HiveParser.KW_TRUE:
        return visitTrue(node, ctx);

      case HiveParser.KW_FALSE:
        return visitFalse(node, ctx);

      case HiveParser.TOK_NULL:
        return visitNullToken(node, ctx);

      case HiveParser.Number:
        return visitNumber(node, ctx);

      case HiveParser.TOK_TABLE_OR_COL:
        return visitTableTokOrCol(node, ctx);

      case HiveParser.TOK_DESTINATION:
      case HiveParser.EOF:
        return null;

      // add function names here
      case HiveParser.TOK_ISNOTNULL:
      case HiveParser.TOK_ISNULL:
      case HiveParser.KW_CASE:
      case HiveParser.KW_CAST:
      case HiveParser.KW_WHEN:
      case HiveParser.KW_IN:
      case HiveParser.KW_EXISTS:
      case HiveParser.KW_IF:
      case HiveParser.KW_ARRAY:
      case HiveParser.KW_MAP:
      case HiveParser.KW_STRUCT:
      case HiveParser.KW_UNBOUNDED:
        return visitKeywordLiteral(node, ctx);

      case HiveParser.TOK_BOOLEAN:
        return visitBoolean(node, ctx);

      case HiveParser.TOK_INT:
        return visitInt(node, ctx);

      case HiveParser.TOK_STRING:
        return visitString(node, ctx);

      case HiveParser.TOK_BINARY:
        return visitBinary(node, ctx);

      case HiveParser.TOK_DOUBLE:
        return visitDouble(node, ctx);

      case HiveParser.TOK_FLOAT:
        return visitFloat(node, ctx);

      case HiveParser.TOK_BIGINT:
        return visitBigInt(node, ctx);

      case HiveParser.TOK_TINYINT:
        return visitTinyInt(node, ctx);

      case HiveParser.TOK_SMALLINT:
        return visitSmallInt(node, ctx);

      case HiveParser.TOK_CHAR:
        return visitChar(node, ctx);

      case HiveParser.TOK_DECIMAL:
        return visitDecimal(node, ctx);

      case HiveParser.TOK_VARCHAR:
        return visitVarchar(node, ctx);

      case HiveParser.TOK_DATE:
        return visitDate(node, ctx);

      case HiveParser.TOK_DATELITERAL:
        return visitDateLiteral(node, ctx);

      case HiveParser.TOK_TIMESTAMP:
        return visitTimestamp(node, ctx);

      // joins
      case HiveParser.TOK_JOIN:
        return visitJoin(node, ctx);

      case HiveParser.TOK_LEFTOUTERJOIN:
        return visitLeftOuterJoin(node, ctx);

      case HiveParser.TOK_RIGHTOUTERJOIN:
        return visitRightOuterJoin(node, ctx);

      case HiveParser.TOK_FULLOUTERJOIN:
        return visitFullOuterJoin(node, ctx);

      case HiveParser.TOK_CROSSJOIN:
        return visitCrossJoin(node, ctx);

      case HiveParser.TOK_LEFTSEMIJOIN:
        return visitLeftSemiJoin(node, ctx);

      case HiveParser.TOK_LATERAL_VIEW:
        return visitLateralView(node, ctx);

      case HiveParser.TOK_LATERAL_VIEW_OUTER:
        return visitLateralViewOuter(node, ctx);

      case HiveParser.TOK_TABALIAS:
        return visitTabAlias(node, ctx);

      case HiveParser.TOK_CTE:
        return visitCTE(node, ctx);

      case HiveParser.TOK_WINDOWSPEC:
        return visitWindowSpec(node, ctx);

      case HiveParser.TOK_PARTITIONINGSPEC:
        return visitPartitioningSpec(node, ctx);

      case HiveParser.TOK_DISTRIBUTEBY:
        return visitDistributeBy(node, ctx);

      case HiveParser.TOK_WINDOWRANGE:
        return visitWindowRange(node, ctx);

      case HiveParser.TOK_WINDOWVALUES:
        return visitWindowValues(node, ctx);

      // See IdentifiersParser.g:
      case HiveParser.TOK_INTERVAL_DAY_LITERAL:
      case HiveParser.TOK_INTERVAL_DAY_TIME_LITERAL:
      case HiveParser.TOK_INTERVAL_HOUR_LITERAL:
      case HiveParser.TOK_INTERVAL_MINUTE_LITERAL:
      case HiveParser.TOK_INTERVAL_MONTH_LITERAL:
      case HiveParser.TOK_INTERVAL_SECOND_LITERAL:
      case HiveParser.TOK_INTERVAL_YEAR_LITERAL:
      case HiveParser.TOK_INTERVAL_YEAR_MONTH_LITERAL:
        return visitIntervalLiteral(node, ctx);

      case HiveParser.KW_PRECEDING:
        return visitPreceding(node, ctx);

      case HiveParser.KW_FOLLOWING:
        return visitFollowing(node, ctx);

      case HiveParser.KW_CURRENT:
        return visitCurrentRow(node, ctx);

      default:
        // return visitChildren(node, ctx);
        throw new UnhandledASTTokenException(node);
    }
  }

  protected R visitKeywordLiteral(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected List<R> visitChildren(ASTNode node, C ctx) {
    Preconditions.checkNotNull(node, ctx);
    Preconditions.checkNotNull(ctx);
    if (node.getChildren() == null) {
      return null;
    }
    return visitChildren(node.getChildren(), ctx);
  }

  protected List<R> visitChildren(List<Node> nodes, C ctx) {
    return nodes.stream().map(n -> visit((ASTNode) n, ctx)).collect(Collectors.toList());
  }

  protected R visitOptionalChildByType(ASTNode node, C ctx, int nodeType) {
    List<R> results = visitChildrenByType(node, ctx, nodeType);
    if (results == null || results.isEmpty()) {
      return null;
    }
    if (results.size() > 1) {
      throw new UnexpectedASTChildCountException(node, nodeType, 1, results.size());
    }
    return results.get(0);
  }

  protected List<R> visitChildrenByType(ASTNode node, C ctx, int nodeType) {
    Preconditions.checkNotNull(node, ctx);
    Preconditions.checkNotNull(ctx);
    if (node.getChildren() == null) {
      return null;
    }
    return visitChildrenByType(node.getChildren(), ctx, nodeType);
  }

  protected List<R> visitChildrenByType(List<Node> nodes, C ctx, int nodeType) {
    return nodes.stream().filter(node -> ((ASTNode) node).getType() == nodeType).map(n -> visit((ASTNode) n, ctx))
        .collect(Collectors.toList());
  }

  protected R visitTabAlias(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitLateralView(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitLateralViewOuter(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitLeftSemiJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitCrossJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFullOuterJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitRightOuterJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitLeftOuterJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFalse(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitTrue(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitNullToken(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitLimit(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitUnion(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitNumber(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitAllColRef(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitHaving(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitWhere(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSortColNameDesc(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSortColNameAsc(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitOrderBy(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitGroupBy(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitOperator(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitDotOperator(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitLParen(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFunctionStar(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFunctionDistinct(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFunction(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSelectExpr(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSelectDistinct(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSelect(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitTabRefNode(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitTabnameNode(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSubqueryOp(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSubqueryExpr(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSubquery(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFrom(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitIdentifier(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitStringLiteral(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitBigintLiteral(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitQueryNode(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitNil(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitBoolean(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitSmallInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitBigInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitTinyInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFloat(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitDouble(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitVarchar(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitChar(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitString(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitBinary(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitDecimal(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitDate(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitDateLiteral(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitTimestamp(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitTableTokOrCol(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitInsert(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitIsNull(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitIsNotNull(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitCTE(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitWindowSpec(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitPartitioningSpec(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitDistributeBy(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitWindowRange(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitWindowValues(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitPreceding(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitFollowing(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitCurrentRow(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected R visitIntervalLiteral(ASTNode node, C ctx) {
    return visitChildren(node, ctx).get(0);
  }
}

package com.linkedin.coral.hive.hive2rel.parsetree;

import com.google.common.base.Preconditions;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;


/**
 * Abstract visitor (actually, a walker) to hive AST.
 * This class implements a walker that calls specific named methods
 * passing corresponding {@link ASTNode}.
 *
 * By default, this visits all children of the node
 *
 * @param <C> abstract visitor context ({@link ASTVisitorContext}) that is passed to all
 *           the visitor methods
 */
public abstract class AbstractASTVisitor<R, C> {

  public AbstractASTVisitor() {
  }

  public R visit(ASTNode node, C ctx) {
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
        return visitUnion(node, ctx);

      case HiveParser.TOK_QUERY:
        return visitQueryNode(node, ctx);

      case HiveParser.TOK_TABREF:
        return visitTabRefNode(node, ctx);

      case HiveParser.TOK_TABNAME:
        return visitTabnameNode(node, ctx);

      case HiveParser.Identifier:
      case HiveParser.StringLiteral:
        return visitStringLiteral(node, ctx);

      case HiveParser.TOK_INSERT:
        return visitChildren(node, ctx);

      case HiveParser.TOK_DESTINATION:
        return null;

      case HiveParser.TOK_SELECTDI:
        return visitSelectDistinct(node, ctx);

      case HiveParser.TOK_LIMIT:
        return visitLimit(node, ctx);

      case HiveParser.TOK_SELECT:
        return visitSelects(node, ctx);

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
        return visitOperator(node, ctx);

      case HiveParser.LSQUARE:
        return visitLParen(node, ctx);

      case HiveParser.KW_FALSE:
        return visitFalse(node, ctx);

      case HiveParser.TOK_NULL:
        return visitNullToken(node, ctx);

      case HiveParser.Number:
        return visitNumber(node, ctx);

      case HiveParser.TOK_TABLE_OR_COL:
        return visitChildren(node, ctx);

      case HiveParser.EOF:
              case HiveParser.KW_IN:
      case HiveParser.KW_EXISTS:
        return visitStringLiteral(node, ctx);

      case HiveParser.TOK_BOOLEAN:
        return visitBoolean(node, ctx);

      case HiveParser.TOK_INT:
        return visitInt(node, ctx);

      case HiveParser.TOK_STRING:
        return visitString(node, ctx);

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

      case HiveParser.TOK_TABALIAS:
        return visitTabAlias(node, ctx);

      default:
        // return visitChildren(node, ctx);
        throw new UnhandledASTTokenException(node);
    }
  }

  protected R visitChildren(ASTNode node, C ctx) {
    Preconditions.checkNotNull(node, ctx);
    Preconditions.checkNotNull(ctx);
    if (node.getChildren() == null) {
      return null;
    }
    node.getChildren().forEach(c -> visit((ASTNode) c, ctx));
    return null;
  }

  protected R visitTabAlias(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitLateralView(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitLeftSemiJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitCrossJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFullOuterJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitRightOuterJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitLeftOuterJoin(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFalse(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitNullToken(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitLimit(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitUnion(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitNumber(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitAllColRef(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitHaving(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitWhere(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSortColNameDesc(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSortColNameAsc(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitOrderBy(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitGroupBy(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitOperator(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitDotOperator(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitLParen(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFunctionStar(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFunctionDistinct(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFunction(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSelectExpr(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSelectDistinct(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSelects(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitTabRefNode(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitTabnameNode(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSubqueryOp(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSubqueryExpr(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSubquery(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFrom(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitStringLiteral(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitQueryNode(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitNil(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitBoolean(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitSmallInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitBigInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitTinyInt(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitFloat(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitDouble(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitVarchar(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitChar(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitString(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitDecimal(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitDate(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }

  protected R visitTimestamp(ASTNode node, C ctx) {
    return visitChildren(node, ctx);
  }
}

package com.linkedin.coral.hive.hive2rel.tree;

import com.google.common.base.Preconditions;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;


/**
 * Abstract visitor (actually, a walker) to hive AST.
 * This class implements a walker that calls specific named methods
 * passing corresponding ASTNode.
 *
 * By default, this visits all children of the node
 *
 * @param <C> abstract visitor context that is passed to all the visitor methods
 */
public abstract class AbstractASTVisitor<C extends ASTVisitorContext> {

  public AbstractASTVisitor() {
  }

  public void visit(ASTNode node, C ctx) {
    if (node == null) {
      return;
    }
    switch (node.getType()) {
      case 0:
        visitNil(node, ctx);
        break;

      case HiveParser.TOK_SUBQUERY:
        visitSubquery(node, ctx);
        break;

      case HiveParser.TOK_SUBQUERY_EXPR:
        visitSubqueryExpr(node, ctx);
        break;

      case HiveParser.TOK_SUBQUERY_OP:
        visitSubqueryOp(node, ctx);
        break;

      case HiveParser.TOK_FROM:
        visitFrom(node, ctx);
        break;

      case HiveParser.TOK_UNIONTYPE:
        visitUnion(node, ctx);
        break;

      case HiveParser.TOK_QUERY:
        visitQueryNode(node, ctx);
        break;

      case HiveParser.TOK_TABREF:
        visitTabRefNode(node, ctx);
        break;

      case HiveParser.TOK_TABNAME:
        visitTabnameNode(node, ctx);
        break;

      case HiveParser.Identifier:
      case HiveParser.StringLiteral:
        visitStringLiteral(node, ctx);
        break;

      case HiveParser.TOK_INSERT:
        visitChildren(node, ctx);
        break;

      case HiveParser.TOK_DESTINATION:
        // do nothing
        break;

      case HiveParser.TOK_SELECTDI:
        visitSelectDistinct(node, ctx);
        break;

      case HiveParser.TOK_LIMIT:
        visitLimit(node, ctx);
        break;

      case HiveParser.TOK_SELECT:
        visitSelects(node, ctx);
        break;

      case HiveParser.TOK_SELEXPR:
        visitSelectExpr(node, ctx);
        break;

      case HiveParser.TOK_ALLCOLREF:
        visitAllColRef(node, ctx);
        break;

      case HiveParser.TOK_HAVING:
        visitHaving(node, ctx);
        break;

      case HiveParser.TOK_WHERE:
        visitWhere(node, ctx);
        break;

      case HiveParser.TOK_GROUPBY:
        visitGroupBy(node, ctx);
        break;

      case HiveParser.TOK_ORDERBY:
        visitOrderBy(node, ctx);
        break;

      case HiveParser.TOK_TABSORTCOLNAMEASC:
        visitSortColNameAsc(node, ctx);
        break;

      case HiveParser.TOK_TABSORTCOLNAMEDESC:
        visitSortColNameDesc(node, ctx);
        break;

      case HiveParser.TOK_FUNCTION:
        visitFunction(node, ctx);
        break;

      case HiveParser.TOK_FUNCTIONDI:
        visitFunctionDistinct(node, ctx);
        break;

      case HiveParser.TOK_FUNCTIONSTAR:
        visitFunctionStar(node, ctx);
        break;

      case HiveParser.DOT:
        visitDotOperator(node, ctx);
        break;

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
        visitOperator(node, ctx);
        break;

      case HiveParser.LSQUARE:
        visitLParen(node, ctx);
        break;

      case HiveParser.KW_FALSE:
        visitFalse(node, ctx);
        break;

      case HiveParser.TOK_NULL:
        visitNullToken(node, ctx);
        break;

      case HiveParser.Number:
        visitNumber(node, ctx);
        break;

      case HiveParser.TOK_TABLE_OR_COL:
        visitChildren(node, ctx);
        break;

      case HiveParser.EOF:
        break;
      case HiveParser.KW_IN:
      case HiveParser.KW_EXISTS:
        visitStringLiteral(node, ctx);
        break;

      case HiveParser.TOK_BOOLEAN:
        visitBoolean(node, ctx);
        break;

      case HiveParser.TOK_INT:
        visitInt(node, ctx);
        break;

      case HiveParser.TOK_STRING:
        visitString(node, ctx);
        break;

      case HiveParser.TOK_DOUBLE:
        visitDouble(node, ctx);
        break;

      case HiveParser.TOK_FLOAT:
        visitFloat(node, ctx);
        break;

      case HiveParser.TOK_BIGINT:
        visitBigInt(node, ctx);
        break;

      case HiveParser.TOK_TINYINT:
        visitTinyInt(node, ctx);
        break;

      case HiveParser.TOK_SMALLINT:
        visitSmallInt(node, ctx);
        break;

      case HiveParser.TOK_CHAR:
        visitChar(node, ctx);
        break;

      case HiveParser.TOK_DECIMAL:
        visitDecimal(node, ctx);
        break;

      case HiveParser.TOK_VARCHAR:
        visitVarchar(node, ctx);
        break;

      case HiveParser.TOK_DATE:
        visitDate(node, ctx);
        break;

      case HiveParser.TOK_TIMESTAMP:
        visitTimestamp(node, ctx);
        break;

        // joins
      case HiveParser.TOK_JOIN:
        visitJoin(node, ctx);
        break;

      case HiveParser.TOK_LEFTOUTERJOIN:
        visitLeftOuterJoin(node, ctx);
        break;

      case HiveParser.TOK_RIGHTOUTERJOIN:
        visitRightOuterJoin(node, ctx);
        break;

      case HiveParser.TOK_FULLOUTERJOIN:
        visitFullOuterJoin(node, ctx);
        break;

      case HiveParser.TOK_CROSSJOIN:
        visitCrossJoin(node, ctx);
        break;

      case HiveParser.TOK_LEFTSEMIJOIN:
        visitLeftSemiJoin(node, ctx);
        break;

      case HiveParser.TOK_LATERAL_VIEW:
        visitLateralView(node, ctx);
        break;

      case HiveParser.TOK_TABALIAS:
        visitTabAlias(node, ctx);
        break;

      default:
        // visitChildren(node, ctx);
        throw new UnhandledASTToken(node);
    }
  }

  protected void visitChildren(ASTNode node, C ctx) {
    Preconditions.checkNotNull(node, ctx);
    Preconditions.checkNotNull(ctx);
    if (node.getChildren() == null) {
      return;
    }
    node.getChildren().forEach(c -> visit((ASTNode) c, ctx));
  }

  protected void visitTabAlias(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitLateralView(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitLeftSemiJoin(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitCrossJoin(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFullOuterJoin(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitRightOuterJoin(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitJoin(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitLeftOuterJoin(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFalse(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitNullToken(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitLimit(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitUnion(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitNumber(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitAllColRef(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitHaving(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitWhere(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSortColNameDesc(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSortColNameAsc(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitOrderBy(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitGroupBy(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitOperator(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitDotOperator(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitLParen(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFunctionStar(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFunctionDistinct(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFunction(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSelectExpr(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSelectDistinct(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSelects(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitTabRefNode(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitTabnameNode(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSubqueryOp(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSubqueryExpr(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSubquery(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFrom(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitStringLiteral(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitQueryNode(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitNil(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitBoolean(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitInt(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitSmallInt(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitBigInt(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitTinyInt(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitFloat(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitDouble(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitVarchar(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitChar(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitString(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitDecimal(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitDate(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }

  protected void visitTimestamp(ASTNode node, C ctx) {
    visitChildren(node, ctx);
  }
}

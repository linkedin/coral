/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel.parsetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlUnresolvedFunction;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;
import org.apache.spark.sql.catalyst.parser.SqlBaseBaseVisitor;
import org.apache.spark.sql.catalyst.parser.SqlBaseParser;

import com.linkedin.coral.common.HiveTypeSystem;
import com.linkedin.coral.common.calcite.CalciteUtil;

import static com.linkedin.coral.common.calcite.CalciteUtil.createCall;
import static com.linkedin.coral.common.calcite.CalciteUtil.createSqlIdentifier;
import static com.linkedin.coral.common.calcite.CalciteUtil.createStarIdentifier;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.AS;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.BETWEEN;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.CAST;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.CONCAT;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.DIVIDE;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.DIVIDE_INTEGER;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.GREATER_THAN;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.GREATER_THAN_OR_EQUAL;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.IN;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.IS_DISTINCT_FROM;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.IS_FALSE;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.IS_NULL;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.IS_TRUE;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.IS_UNKNOWN;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.LESS_THAN;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.LESS_THAN_OR_EQUAL;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.LIKE;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.MINUS;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.MULTIPLY;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.NOT;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.NOT_EQUALS;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.PERCENT_REMAINDER;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.PLUS;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


public class SparkSqlAstVisitor extends SqlBaseBaseVisitor<SqlNode> {
  private static final SqlTypeFactoryImpl SQL_TYPE_FACTORY = new SqlTypeFactoryImpl(new HiveTypeSystem());
  private static final String UNSUPPORTED_EXCEPTION_MSG = "%s at line %d column %d is not supported in the visit.";

  @Override
  public SqlNode visitChildren(RuleNode node) {
    if (node.getChildCount() == 2 && node.getChild(1) instanceof TerminalNode) {
      return node.getChild(0).accept(this);
    }
    return super.visitChildren(node);
  }

  @Override
  public SqlNode visitQuery(SqlBaseParser.QueryContext ctx) {
    SqlSelect select = (SqlSelect) ((SqlNodeList) visit(ctx.queryTerm())).get(0);
    SqlNodeList orderBy = ctx.queryOrganization().order.isEmpty() ? null : new SqlNodeList(
        ctx.queryOrganization().order.stream().map(this::visit).collect(Collectors.toList()), getPos(ctx));
    SqlNode limit = ctx.queryOrganization().limit == null ? null : visit(ctx.queryOrganization().limit);
    if (orderBy != null || limit != null) {
      return new SqlOrderBy(getPos(ctx), select, orderBy, null, limit);
    }
    return select;
  }

  @Override
  public SqlNode visitRegularQuerySpecification(SqlBaseParser.RegularQuerySpecificationContext ctx) {
    SqlNodeList selectList = visitSelectClause(ctx.selectClause());
    SqlNode from = ctx.fromClause() == null ? null : visitFromClause(ctx.fromClause());
    SqlNode where = ctx.whereClause() == null ? null : visitWhereClause(ctx.whereClause());
    SqlNodeList groupBy =
        ctx.aggregationClause() == null ? null : visitGroupByClause(ctx.aggregationClause().groupByClause);
    SqlNode having = ctx.havingClause() == null ? null : visitHavingClause(ctx.havingClause());
    return new SqlSelect(getPos(ctx), null, selectList, from, where, groupBy, having, null, null, null, null);
  }

  @Override
  public SqlNode visitWhereClause(SqlBaseParser.WhereClauseContext ctx) {
    return super.visitWhereClause(ctx);
  }

  @Override
  public SqlNode visitCtes(SqlBaseParser.CtesContext ctx) {
    throw new UnhandledASTNodeException(ctx, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  public SqlNode visitJoinRelation(SqlBaseParser.JoinRelationContext ctx) {
    throw new UnhandledASTNodeException(ctx, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  public SqlNode visitJoinType(SqlBaseParser.JoinTypeContext ctx) {
    throw new UnhandledASTNodeException(ctx, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  public SqlNode visitJoinCriteria(SqlBaseParser.JoinCriteriaContext ctx) {
    throw new UnhandledASTNodeException(ctx, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  public SqlNode visitPredicated(SqlBaseParser.PredicatedContext ctx) {
    SqlNode expression = visit(ctx.valueExpression());
    if (ctx.predicate() != null) {
      return withPredicate(expression, ctx.predicate());
    }
    return expression;
  }

  private SqlNode withPredicate(SqlNode expression, SqlBaseParser.PredicateContext ctx) {
    SqlNode predicate = toPredicate(expression, ctx);
    if (ctx.NOT() == null) {
      return predicate;
    }
    return NOT.createCall(getPos(ctx), predicate);
  }

  private SqlNode toPredicate(SqlNode expression, SqlBaseParser.PredicateContext ctx) {
    SqlParserPos position = getPos(ctx);
    int type = ctx.kind.getType();
    switch (type) {
      case SqlBaseParser.BETWEEN:
        return BETWEEN.createCall(position, expression, visit(ctx.right));
      case SqlBaseParser.IN:
        return IN.createCall(position, expression, visit(ctx.right));
      case SqlBaseParser.LIKE:
        return LIKE.createCall(position, expression, visit(ctx.right));
      case SqlBaseParser.RLIKE:
        throw new UnsupportedOperationException("Unsupported predicate type: RLIKE");
      case SqlBaseParser.NULL:
        return IS_NULL.createCall(position, expression);
      case SqlBaseParser.TRUE:
        return IS_TRUE.createCall(position, expression);
      case SqlBaseParser.FALSE:
        return IS_FALSE.createCall(position, expression);
      case SqlBaseParser.UNKNOWN:
        return IS_UNKNOWN.createCall(position, expression);
      case SqlBaseParser.DISTINCT:
        return IS_DISTINCT_FROM.createCall(position, expression, visit(ctx.right));
      default:
        throw new UnsupportedOperationException("Unsupported predicate type:" + type);
    }
  }

  @Override
  public SqlNode visitComparison(SqlBaseParser.ComparisonContext ctx) {
    SqlParserPos position = getPos(ctx);
    SqlNode left = visit(ctx.left);
    SqlNode right = visit(ctx.right);
    TerminalNode operator = (TerminalNode) ctx.comparisonOperator().getChild(0);
    switch (operator.getSymbol().getType()) {
      case SqlBaseParser.EQ:
        return EQUALS.createCall(position, left, right);
      case SqlBaseParser.NSEQ:
        throw new UnsupportedOperationException("Unsupported operator: NSEQ (NullSafeEqual)");
      case SqlBaseParser.NEQ:
      case SqlBaseParser.NEQJ:
        return NOT_EQUALS.createCall(position, left, right);
      case SqlBaseParser.LT:
        return LESS_THAN.createCall(position, left, right);
      case SqlBaseParser.LTE:
        return LESS_THAN_OR_EQUAL.createCall(position, left, right);
      case SqlBaseParser.GT:
        return GREATER_THAN.createCall(position, left, right);
      case SqlBaseParser.GTE:
        return GREATER_THAN_OR_EQUAL.createCall(position, left, right);
    }
    throw new UnsupportedOperationException("visitComparison");
  }

  @Override
  public SqlNodeList visitSelectClause(SqlBaseParser.SelectClauseContext ctx) {
    return getChildSqlNodeList(ctx.namedExpressionSeq());
  }

  @Override
  public SqlNodeList visitGroupByClause(SqlBaseParser.GroupByClauseContext ctx) {
    return getChildSqlNodeList(ctx.children);
  }

  @Override
  public SqlNode visitTableIdentifier(SqlBaseParser.TableIdentifierContext ctx) {
    return createSqlIdentifier(getPos(ctx), ctx.db.getText(), ctx.table.getText());
  }

  @Override
  public SqlNode visitTableName(SqlBaseParser.TableNameContext ctx) {
    if (ctx.tableAlias().children != null) {
      List<SqlNode> operands = new ArrayList<>();
      operands.add(visit(ctx.getChild(0)));
      operands.addAll(visitTableAlias(ctx.tableAlias()).getList());
      return AS.createCall(getPos(ctx), operands);
    }
    return visitMultipartIdentifier(ctx.multipartIdentifier());
  }

  @Override
  public SqlNodeList visitTableAlias(SqlBaseParser.TableAliasContext ctx) {
    List<SqlNode> operands = new ArrayList<>();
    operands.add(visit(ctx.getChild(0)));
    if (ctx.children.size() > 1) {
      operands.addAll(((SqlNodeList) visit(ctx.getChild(1))).getList());
    }
    return new SqlNodeList(operands, getPos(ctx));
  }

  @Override
  public SqlNode visitQuotedIdentifierAlternative(SqlBaseParser.QuotedIdentifierAlternativeContext ctx) {
    return createSqlIdentifier(getPos(ctx), ctx.getText());
  }

  @Override
  public SqlNode visitUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext ctx) {
    return createSqlIdentifier(getPos(ctx), ctx.getText());
  }

  @Override
  public SqlNode visitMultipartIdentifier(SqlBaseParser.MultipartIdentifierContext ctx) {
    return createSqlIdentifier(getPos(ctx),
        ctx.parts.stream().map(part -> part.identifier().getText()).toArray(String[]::new));
  }

  @Override
  public SqlNode visitCast(SqlBaseParser.CastContext ctx) {
    SqlDataTypeSpec spec = toSqlDataTypeSpec(ctx);
    return CAST.createCall(getPos(ctx), visit(ctx.expression()), spec);
  }

  private SqlDataTypeSpec toSqlDataTypeSpec(SqlBaseParser.CastContext ctx) {
    return SqlTypeUtil.convertTypeToSpec(
        SQL_TYPE_FACTORY.createSqlType(SqlTypeName.valueOf((ctx.dataType().getText().toUpperCase()))));
  }

  @Override
  public SqlNode visitFunctionCall(SqlBaseParser.FunctionCallContext ctx) {
    SqlIdentifier functionName = createSqlIdentifier(getPos(ctx), ctx.functionName().getText());
    SqlUnresolvedFunction unresolvedFunction =
        new SqlUnresolvedFunction(functionName, null, null, null, null, SqlFunctionCategory.USER_DEFINED_FUNCTION);
    List<SqlNode> operands = ctx.argument.stream().map(this::visit).collect(Collectors.toList());
    return createCall(unresolvedFunction, operands, getPos(ctx));
  }

  @Override
  public SqlNode visitStatementDefault(SqlBaseParser.StatementDefaultContext ctx) {
    return ctx.query().accept(this);
  }

  @Override
  public SqlNode visitQueryTermDefault(SqlBaseParser.QueryTermDefaultContext ctx) {
    return getChildSqlNodeList(ctx);
  }

  @Override
  public SqlNode visitSubscript(SqlBaseParser.SubscriptContext ctx) {
    throw new UnhandledASTNodeException(ctx, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  public SqlNode visitNamedExpression(SqlBaseParser.NamedExpressionContext ctx) {
    if (ctx.name != null) {
      return AS.createCall(getPos(ctx), visit(ctx.getChild(0)),
          createSqlIdentifier(getPos(ctx), ctx.name.identifier().getText()));
    }
    String text = ctx.getText();
    if (text.equals("*")) {
      return createStarIdentifier(getPos(ctx));
    }
    return super.visitNamedExpression(ctx);
  }

  @Override
  public SqlNode visitIdentifierList(SqlBaseParser.IdentifierListContext ctx) {
    List<SqlIdentifier> identifiers = ctx.identifierSeq().ident.stream()
        .map(identifier -> createSqlIdentifier(getPos(identifier), identifier.getText())).collect(Collectors.toList());
    return new SqlNodeList(identifiers, getPos(ctx));
  }

  @Override
  public SqlNode visitColumnReference(SqlBaseParser.ColumnReferenceContext ctx) {
    return createSqlIdentifier(getPos(ctx), ctx.getText());
  }

  @Override
  public SqlNode visitArithmeticBinary(SqlBaseParser.ArithmeticBinaryContext ctx) {
    SqlNode left = visit(ctx.left);
    SqlNode right = visit(ctx.right);
    SqlParserPos position = getPos(ctx);
    switch (ctx.operator.getType()) {
      case SqlBaseParser.ASTERISK:
        return MULTIPLY.createCall(position, left, right);
      case SqlBaseParser.SLASH:
        return DIVIDE.createCall(position, left, right);
      case SqlBaseParser.PERCENT:
        return PERCENT_REMAINDER.createCall(position, left, right);
      case SqlBaseParser.DIV:
        return DIVIDE_INTEGER.createCall(position, left, right);
      case SqlBaseParser.PLUS:
        return PLUS.createCall(position, left, right);
      case SqlBaseParser.MINUS:
        return MINUS.createCall(position, left, right);
      case SqlBaseParser.CONCAT_PIPE:
        return CONCAT.createCall(position, left, right);
      case SqlBaseParser.AMPERSAND:
        throw new UnsupportedOperationException("Unsupported arithmetic binary: &");
      case SqlBaseParser.HAT:
        throw new UnsupportedOperationException("Unsupported arithmetic binary: ^");
      case SqlBaseParser.PIPE:
        throw new UnsupportedOperationException("Unsupported arithmetic binary: |");
    }
    throw new UnsupportedOperationException("Unsupported arithmetic binary: " + ctx.operator);
  }

  @Override
  public SqlNode visitBooleanLiteral(SqlBaseParser.BooleanLiteralContext ctx) {
    boolean value = ctx.getText().equalsIgnoreCase("true");
    return CalciteUtil.createLiteralBoolean(value, getPos(ctx));
  }

  @Override
  public SqlNode visitNumericLiteral(SqlBaseParser.NumericLiteralContext ctx) {
    return SqlLiteral.createExactNumeric(ctx.getText(), getPos(ctx));
  }

  @Override
  public SqlNode visitStringLiteral(SqlBaseParser.StringLiteralContext ctx) {
    String text = ctx.getText().substring(1, ctx.getText().length() - 1);
    return CalciteUtil.createStringLiteral(text, getPos(ctx));
  }

  private SqlNodeList getChildSqlNodeList(ParserRuleContext ctx) {
    return new SqlNodeList(getChildren(ctx), getPos(ctx));
  }

  private SqlNodeList getChildSqlNodeList(List<ParseTree> nodes) {
    return new SqlNodeList(toListOfSqlNode(nodes), ZERO);
  }

  private List<SqlNode> getChildren(ParserRuleContext node) {
    return toListOfSqlNode(node.children);
  }

  private List<SqlNode> toListOfSqlNode(List<? extends ParseTree> nodes) {
    if (nodes == null) {
      return Collections.emptyList();
    }
    return nodes.stream().map(this::visit).filter(Objects::nonNull).collect(Collectors.toList());
  }

  private SqlParserPos getPos(ParserRuleContext ctx) {
    if (ctx.start != null) {
      return new SqlParserPos(ctx.start.getLine(), ctx.start.getStartIndex());
    }
    return ZERO;
  }
}

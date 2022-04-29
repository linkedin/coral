/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel.parsetree;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.avatica.util.TimeUnit;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;
import org.apache.calcite.util.TimeString;
import org.apache.calcite.util.TimestampString;
import org.apache.commons.lang3.ObjectUtils;

import com.linkedin.coral.common.HiveTypeSystem;
import com.linkedin.coral.common.calcite.CalciteUtil;

import coral.shading.io.trino.sql.tree.*;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;
import static java.lang.String.format;
import static org.apache.calcite.sql.SqlFunctionCategory.USER_DEFINED_FUNCTION;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


public class ParseTreeBuilder extends AstVisitor<SqlNode, ParserVisitorContext> {
  private static final String UNSUPPORTED_EXCEPTION_MSG = "%s at line %d column %d is not supported in the visit.";
  private static final String DDL_NOT_SUPPORT_MSG = "DDL %s at line %d column %d is not supported.";
  private static final String LAMBDA_NOT_SUPPORT_MSG = "Lambda expression %s at line %d column %d is not supported.";
  private static final ImmutableMap<Join.Type, JoinType> JOIN_TYPE_MAP =
      ImmutableMap.of(Join.Type.LEFT, JoinType.LEFT, Join.Type.RIGHT, JoinType.RIGHT, Join.Type.FULL, JoinType.FULL,
          Join.Type.INNER, JoinType.INNER, Join.Type.CROSS, JoinType.CROSS);
  // TODO there are a couple of fields don't have corresponding TimeUnit
  private static final ImmutableMap<String, TimeUnit> TIME_UNIT_MAP =
      ImmutableMap.<String, TimeUnit> builder().put(Extract.Field.YEAR.name(), TimeUnit.YEAR)
          .put(Extract.Field.QUARTER.name(), TimeUnit.QUARTER).put(Extract.Field.MONTH.name(), TimeUnit.MONTH)
          .put(Extract.Field.WEEK.name(), TimeUnit.WEEK).put(Extract.Field.DAY.name(), TimeUnit.DAY)
          .put(Extract.Field.DAY_OF_MONTH.name(), TimeUnit.DAY).put(Extract.Field.DAY_OF_WEEK.name(), TimeUnit.DOW)
          .put(Extract.Field.DOW.name(), TimeUnit.DOW).put(Extract.Field.DAY_OF_YEAR.name(), TimeUnit.DOY)
          .put(Extract.Field.DOY.name(), TimeUnit.DOY).put(Extract.Field.HOUR.name(), TimeUnit.HOUR)
          .put(Extract.Field.MINUTE.name(), TimeUnit.MINUTE).put(Extract.Field.SECOND.name(), TimeUnit.SECOND).build();
  private static final ImmutableMap<String, SqlOperator> OPERATOR_MAP =
      ImmutableMap.<String, SqlOperator> builder().put(ArithmeticBinaryExpression.Operator.ADD.getValue(), PLUS)
          .put(ArithmeticBinaryExpression.Operator.DIVIDE.getValue(), DIVIDE)
          .put(ArithmeticBinaryExpression.Operator.SUBTRACT.getValue(), MINUS)
          .put(ArithmeticBinaryExpression.Operator.MULTIPLY.getValue(), MULTIPLY)
          .put(ArithmeticBinaryExpression.Operator.MODULUS.getValue(), PERCENT_REMAINDER)
          .put(ComparisonExpression.Operator.EQUAL.getValue(), EQUALS)
          .put(ComparisonExpression.Operator.LESS_THAN.getValue(), LESS_THAN)
          .put(ComparisonExpression.Operator.LESS_THAN_OR_EQUAL.getValue(), LESS_THAN_OR_EQUAL)
          .put(ComparisonExpression.Operator.GREATER_THAN.getValue(), GREATER_THAN)
          .put(ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL.getValue(), GREATER_THAN_OR_EQUAL)
          .put(ComparisonExpression.Operator.NOT_EQUAL.getValue(), NOT_EQUALS)
          .put(ComparisonExpression.Operator.IS_DISTINCT_FROM.getValue(), IS_DISTINCT_FROM).build();
  private static final ImmutableMap<ComparisonExpression.Operator, SqlOperator> ALL_COMPARISON_OPERATOR =
      ImmutableMap.<ComparisonExpression.Operator, SqlOperator> builder()
          .put(ComparisonExpression.Operator.EQUAL, ALL_EQ).put(ComparisonExpression.Operator.NOT_EQUAL, ALL_NE)
          .put(ComparisonExpression.Operator.LESS_THAN, ALL_LT)
          .put(ComparisonExpression.Operator.LESS_THAN_OR_EQUAL, ALL_LE)
          .put(ComparisonExpression.Operator.GREATER_THAN, ALL_GT)
          .put(ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL, ALL_GE).build();
  private static final ImmutableMap<ComparisonExpression.Operator, SqlOperator> SOME_COMPARISON_OPERATOR =
      ImmutableMap.<ComparisonExpression.Operator, SqlOperator> builder()
          .put(ComparisonExpression.Operator.EQUAL, SOME_EQ).put(ComparisonExpression.Operator.NOT_EQUAL, SOME_NE)
          .put(ComparisonExpression.Operator.LESS_THAN, SOME_LT)
          .put(ComparisonExpression.Operator.LESS_THAN_OR_EQUAL, SOME_LE)
          .put(ComparisonExpression.Operator.GREATER_THAN, SOME_GT)
          .put(ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL, SOME_GE).build();
  private static final ImmutableSet<SqlKind> NULL_CARED_OPERATOR =
      ImmutableSet.of(SqlKind.FIRST_VALUE, SqlKind.LAST_VALUE, SqlKind.LEAD, SqlKind.LAG);

  private final SqlTypeFactoryImpl sqlTypeFactory = new SqlTypeFactoryImpl(new HiveTypeSystem());

  // convert the Presto node parse location to the Calcite SqlParserPos
  private SqlParserPos getPos(Node node) {
    if (node.getLocation().isPresent()) {
      return new SqlParserPos(node.getLocation().get().getLineNumber(), node.getLocation().get().getColumnNumber());
    }
    return ZERO;
  }

  private SqlNode processOptional(Optional<? extends Node> node, ParserVisitorContext context) {
    return node.map(value -> process(value, context)).orElse(null);
  }

  private List<SqlNode> getChildren(Node node, ParserVisitorContext context) {
    return toListOfSqlNode(node.getChildren(), context);
  }

  private SqlNodeList getChildSqlNodeList(Node node, ParserVisitorContext context) {
    return new SqlNodeList(getChildren(node, context), getPos(node));
  }

  private List<SqlNode> toListOfSqlNode(List<? extends Node> nodes, ParserVisitorContext context) {
    if (nodes == null) {
      return Collections.emptyList();
    }
    return nodes.stream().map(n -> process(n, context)).collect(Collectors.toList());
  }

  private SqlNodeList toSqlNodeList(List<? extends Node> nodes, ParserVisitorContext context, SqlParserPos pos) {
    return createSqlNodeList(toListOfSqlNode(nodes, context), pos);
  }

  private SqlNode parseExpression(String expression) {
    try {
      return CalciteUtil.parseStatement(expression);
    } catch (SqlParseException e) {
      throw new RuntimeException(format("Failed to parse the expression %s.", expression), e);
    }
  }

  private SqlIdentifier convertQualifiedName(QualifiedName name, SqlParserPos pos) {
    return CalciteUtil.createSqlIdentifier(pos,
        new ArrayList<>(name.getOriginalParts().stream().map(Identifier::getValue).collect(Collectors.toList()))
            .toArray(new String[0]));
  }

  /**
   * It's used for most of the functions in the presto query. The unresolved call will be resolved in validation flow.
   */
  private SqlCall getUnresolvedFunction(String functionName, List<SqlNode> operands) {
    SqlIdentifier functionIdentifier = CalciteUtil.createSqlIdentifier(SqlParserPos.ZERO, functionName);
    return createCall(new SqlUnresolvedFunction(functionIdentifier, null, null, null, null, USER_DEFINED_FUNCTION),
        operands);
  }

  private SqlCall getCall(SqlOperator operator, Node node, ParserVisitorContext context) {
    if (operator == null) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    return operator.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitCurrentTime(CurrentTime node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    switch (node.getFunction()) {
      case TIME:
        return createCall(CURRENT_TIME, Collections.emptyList(), pos);
      case DATE:
        return createCall(CURRENT_DATE, Collections.emptyList(), pos);
      case LOCALTIME:
        return createCall(LOCALTIME, Collections.emptyList(), pos);
      case TIMESTAMP:
        return createCall(CURRENT_TIMESTAMP, Collections.emptyList(), pos);
      case LOCALTIMESTAMP:
        return createCall(LOCALTIMESTAMP, Collections.emptyList(), pos);
      default:
        return getUnresolvedFunction(node.getFunction().getName(), Collections.emptyList());
    }
  }

  @Override
  protected SqlNode visitExtract(Extract node, ParserVisitorContext context) {
    TimeUnit unit = TIME_UNIT_MAP.get(node.getField().name());
    if (unit == null) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    return createCall(EXTRACT,
        Arrays.asList(new SqlIntervalQualifier(unit, null, ZERO), process(node.getExpression(), context)));
  }

  @Override
  protected SqlNode visitArithmeticBinary(ArithmeticBinaryExpression node, ParserVisitorContext context) {
    return getCall(OPERATOR_MAP.get(node.getOperator().getValue()), node, context);
  }

  @Override
  protected SqlNode visitBetweenPredicate(BetweenPredicate node, ParserVisitorContext context) {
    return BETWEEN.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitCoalesceExpression(CoalesceExpression node, ParserVisitorContext context) {
    return COALESCE.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitComparisonExpression(ComparisonExpression node, ParserVisitorContext context) {
    return getCall(OPERATOR_MAP.get(node.getOperator().getValue()), node, context);
  }

  @Override
  protected SqlNode visitDoubleLiteral(DoubleLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createLiteralNumber(node.getValue(), getPos(node));
  }

  @Override
  protected SqlNode visitDecimalLiteral(DecimalLiteral node, ParserVisitorContext context) {
    return SqlNumericLiteral.createExactNumeric(node.getValue(), getPos(node));
  }

  private SqlNode getFinalQuery(SqlParserPos pos, SqlNode query, SqlNodeList orderByList, SqlNode fetch,
      SqlNode offset) {
    if (!orderByList.getList().isEmpty() || fetch != null || offset != null) {
      return new SqlOrderBy(pos, query, orderByList, offset, fetch);
    }
    return query;
  }

  @Override
  protected SqlNode visitQuery(Query node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    SqlNode query = node.getQueryBody().accept(this, context);
    SqlNodeList orderByList = ObjectUtils.defaultIfNull((SqlNodeList) processOptional(node.getOrderBy(), context),
        createSqlNodeList(Collections.emptyList(), ZERO));
    SqlNode fetch = processOptional(node.getLimit(), context);
    SqlNode offset = processOptional(node.getOffset(), context);
    if (node.getWith().isPresent()) {
      SqlNodeList withItems = visitWith(node.getWith().get(), context);
      return getFinalQuery(pos, new SqlWith(pos, withItems, query), orderByList, fetch, offset);
    } else {
      return getFinalQuery(pos, query, orderByList, fetch, offset);
    }
  }

  @Override
  protected SqlNode visitGenericLiteral(GenericLiteral node, ParserVisitorContext context) {
    return parseExpression(node.toString());
  }

  private String formatTime(String time) {
    if (time.split(":").length == 2) {
      return time + ":00";
    } else if (time.split(":").length == 1) {
      return time + ":00:00";
    }
    return time;
  }

  @Override
  protected SqlNode visitTimeLiteral(TimeLiteral node, ParserVisitorContext context) {
    return SqlLiteral.createTime(new TimeString(formatTime(node.getValue())), 1, getPos(node));
  }

  @Override
  protected SqlNodeList visitWith(With node, ParserVisitorContext context) {
    if (node.isRecursive()) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlNode visitWithQuery(WithQuery node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    SqlIdentifier alias = visitIdentifier(node.getName(), context);
    SqlNode query = visitQuery(node.getQuery(), context);

    SqlNodeList columns = null;
    if (node.getColumnNames().isPresent()) {
      columns = toSqlNodeList(node.getColumnNames().get(), context, pos);
    }
    return new SqlWithItem(pos, alias, columns, query);
  }

  /**
   * Distinct is already handled in the visitQuerySpecification.
   */
  @Override
  protected SqlNodeList visitSelect(Select node, ParserVisitorContext context) {
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlNodeList visitOrderBy(OrderBy node, ParserVisitorContext context) {
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlNode visitOffset(Offset node, ParserVisitorContext context) {
    return process(node.getRowCount(), context);
  }

  @Override
  protected SqlNode visitQuerySpecification(QuerySpecification node, ParserVisitorContext context) {
    SqlNodeList selectList = visitSelect(node.getSelect(), context);
    SqlNodeList keywords = node.getSelect().isDistinct()
        ? new SqlNodeList(ImmutableList.of(SqlSelectKeyword.DISTINCT.symbol(ZERO)), ZERO) : null;
    SqlNode from = processOptional(node.getFrom(), context);
    SqlNode where = processOptional(node.getWhere(), context);
    SqlNodeList groupBy = (SqlNodeList) processOptional(node.getGroupBy(), context);
    SqlNode having = processOptional(node.getHaving(), context);
    SqlNodeList orderBy = ObjectUtils.defaultIfNull((SqlNodeList) processOptional(node.getOrderBy(), context),
        createSqlNodeList(Collections.emptyList(), ZERO));
    SqlNode limit = processOptional(node.getLimit(), context);
    SqlNode offset = processOptional(node.getOffset(), context);
    SqlSelect select =
        new SqlSelect(getPos(node), keywords, selectList, from, where, groupBy, having, null, null, null, null);
    if (!orderBy.getList().isEmpty() || offset != null || limit != null) {
      return new SqlOrderBy(getPos(node), select, orderBy, offset, limit);
    }
    return select;
  }

  @Override
  protected SqlNode visitUnion(Union node, ParserVisitorContext context) {
    if (node.isDistinct()) {
      return UNION.createCall(getChildSqlNodeList(node, context));
    } else {
      return UNION_ALL.createCall(getChildSqlNodeList(node, context));
    }
  }

  @Override
  protected SqlNode visitIntersect(Intersect node, ParserVisitorContext context) {
    return INTERSECT.createCall(getChildSqlNodeList(node, context));
  }

  @Override
  protected SqlNode visitExcept(Except node, ParserVisitorContext context) {
    return EXCEPT.createCall(getChildSqlNodeList(node, context));
  }

  @Override
  protected SqlNode visitTimestampLiteral(TimestampLiteral node, ParserVisitorContext context) {

    return SqlLiteral.createTimestamp(new TimestampString(formatTime(node.getValue())), 1, getPos(node));
  }

  @Override
  protected SqlNode visitWhenClause(WhenClause node, ParserVisitorContext context) {
    context.whenClauses.add(process(node.getOperand(), context));
    context.thenClauses.add(process(node.getResult(), context));
    return null;
  }

  @Override
  protected SqlNode visitIntervalLiteral(IntervalLiteral node, ParserVisitorContext context) {
    TimeUnit startUnit = TimeUnit.valueOf(node.getStartField().toString());
    TimeUnit endUnit = node.getEndField().isPresent() ? TimeUnit.valueOf(node.getEndField().toString()) : null;
    SqlIntervalQualifier qualifier = new SqlIntervalQualifier(startUnit, endUnit, ZERO);
    return SqlLiteral.createInterval(node.getSign().multiplier(), node.getValue(), qualifier, getPos(node));
  }

  @Override
  protected SqlNode visitInPredicate(InPredicate node, ParserVisitorContext context) {
    return IN.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  /**
   * TODO generalize the HiveFunctionResolver to lookup the function for presto here.
   */
  @Override
  protected SqlNode visitFunctionCall(FunctionCall node, ParserVisitorContext context) {
    if (node.getFilter().isPresent()) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    SqlParserPos pos = getPos(node);
    List<SqlNode> operands =
        node.getArguments().stream().map(arg -> process(arg, context)).collect(Collectors.toList());
    SqlIdentifier functionName = convertQualifiedName(node.getName(), getPos(node));
    if (node.getName().toString().equalsIgnoreCase("COUNT") && operands.isEmpty()) {
      operands.add(createStarIdentifier(ZERO));
    }
    SqlUnresolvedFunction unresolvedFunction =
        new SqlUnresolvedFunction(functionName, null, null, null, null, SqlFunctionCategory.USER_DEFINED_FUNCTION);
    SqlLiteral functionQualifier = node.isDistinct() ? SqlLiteral.createSymbol(SqlSelectKeyword.DISTINCT, ZERO) : null;
    SqlCall call = createCall(unresolvedFunction, operands, functionQualifier);
    if (NULL_CARED_OPERATOR.contains(call.getKind())) {
      if (node.getNullTreatment().isPresent()) {
        if (node.getNullTreatment().get().equals(FunctionCall.NullTreatment.IGNORE)) {
          call = IGNORE_NULLS.createCall(pos, call);
        } else {
          call = RESPECT_NULLS.createCall(pos, call);
        }
      }
    }
    if (node.getWindow().isPresent()) {
      return OVER.createCall(pos, call, visitWindow(node.getWindow().get(), context));
    }
    return call;
  }

  private SqlNode visitWindow(Window window, ParserVisitorContext context) {
    if (window instanceof WindowSpecification) {
      return visitWindowSpecification((WindowSpecification) window, context);
    }
    if (window instanceof WindowReference) {
      return visitWindowReference((WindowReference) window, context);
    }
    return null;
  }

  @Override
  protected SqlNode visitLambdaExpression(LambdaExpression node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, LAMBDA_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSimpleCaseExpression(SimpleCaseExpression node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    SqlNode operand = process(node.getOperand(), context);
    ParserVisitorContext caseWhenContext = new ParserVisitorContext();
    node.getWhenClauses().forEach(cw -> process(cw, caseWhenContext));
    SqlNode defaultValue = node.getDefaultValue().isPresent() ? process(node.getDefaultValue().get(), context)
        : CalciteUtil.createLiteralNull(ZERO);
    return caseWhenContext.createCaseCall(pos, operand, defaultValue);
  }

  @Override
  protected SqlNode visitStringLiteral(StringLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createStringLiteral(node.getValue(), getPos(node));
  }

  @Override
  protected SqlNode visitCharLiteral(CharLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createStringLiteral(node.getValue(), getPos(node));
  }

  @Override
  protected SqlNode visitBinaryLiteral(BinaryLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createBinaryLiteral(node.getValue().getBytes(), getPos(node));
  }

  @Override
  protected SqlNode visitBooleanLiteral(BooleanLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createLiteralBoolean(node.getValue(), getPos(node));
  }

  @Override
  protected SqlNode visitInListExpression(InListExpression node, ParserVisitorContext context) {
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlIdentifier visitIdentifier(Identifier node, ParserVisitorContext context) {
    return CalciteUtil.createSqlIdentifier(getPos(node), node.getValue());
  }

  /**
   * If the base is identifier, then it just adds the reference at the end of the identifier names. Otherwise, it's creating the DOT call.
   */
  @Override
  protected SqlNode visitDereferenceExpression(DereferenceExpression node, ParserVisitorContext context) {
    SqlNode base = process(node.getBase(), context);
    if (base.getKind().equals(SqlKind.IDENTIFIER)) {
      List<String> names = new ArrayList<>(((SqlIdentifier) base).names);
      names.add(node.getField().getValue());
      return CalciteUtil.createSqlIdentifier(getPos(node), names.toArray(new String[0]));
    } else {
      return DOT.createCall(getPos(node), base, new SqlIdentifier(node.getField().getValue(), ZERO));
    }
  }

  @Override
  protected SqlNode visitNullIfExpression(NullIfExpression node, ParserVisitorContext context) {
    return NULLIF.createCall(getPos(node), getChildSqlNodeList(node, context));
  }

  /**
   * Convert if(condition, value1, value2) to case when condition then value1 else value2 since not all engine support if().
   */
  @Override
  protected SqlNode visitIfExpression(IfExpression node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    SqlNode defaultValue = node.getFalseValue().isPresent() ? process(node.getFalseValue().get(), context)
        : CalciteUtil.createLiteralNull(ZERO);
    SqlNodeList whenList = toSqlNodeList(Collections.singletonList(node.getCondition()), context, pos);
    SqlNodeList thenList = toSqlNodeList(Collections.singletonList(node.getTrueValue()), context, pos);
    return CASE.createCall(null, pos, null, whenList, thenList, defaultValue);
  }

  @Override
  protected SqlNode visitNullLiteral(NullLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createLiteralNull(getPos(node));
  }

  @Override
  protected SqlNode visitArithmeticUnary(ArithmeticUnaryExpression node, ParserVisitorContext context) {
    SqlNode operand = process(node.getValue(), context);
    if (node.getSign().equals(ArithmeticUnaryExpression.Sign.MINUS)) {
      if (operand instanceof SqlNumericLiteral) {
        return SqlLiteral.createNegative((SqlNumericLiteral) operand, getPos(node));
      }
      return UNARY_MINUS.createCall(getPos(node), operand);
    }
    return operand;
  }

  @Override
  protected SqlNode visitNotExpression(NotExpression node, ParserVisitorContext context) {
    SqlNode operand = process(node.getValue(), context);
    SqlParserPos pos = getPos(node);
    if (operand.getKind().equals(SqlKind.BETWEEN)) {
      return NOT_BETWEEN.createCall(pos, ((SqlCall) operand).getOperandList());
    } else if (operand.getKind().equals(SqlKind.IN)) {
      return NOT_IN.createCall(pos, ((SqlCall) operand).getOperandList());
    } else {
      return NOT.createCall(pos, operand);
    }
  }

  @Override
  protected SqlNode visitSingleColumn(SingleColumn node, ParserVisitorContext context) {
    if (node.getAlias().isPresent()) {
      return AS.createCall(getPos(node), process(node.getExpression(), context),
          process(node.getAlias().get(), context));
    }
    return process(node.getExpression(), context);
  }

  @Override
  protected SqlNode visitAllColumns(AllColumns node, ParserVisitorContext context) {
    if (!node.getAliases().isEmpty()) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    if (node.getTarget().isPresent()) {
      // AllColumns target is prefix of the .*
      SqlNode target = process(node.getTarget().get(), context);
      if (target.getKind().equals(SqlKind.IDENTIFIER)) {
        List<String> names = new ArrayList<>(((SqlIdentifier) target).names);
        names.add("");
        return createSqlIdentifier(getPos(node), names.toArray(new String[0]));
      } else {
        return DOT.createCall(getPos(node), target, createStarIdentifier(ZERO));
      }
    } else {
      return CalciteUtil.createStarIdentifier(getPos(node));
    }
  }

  @Override
  protected SqlNode visitSearchedCaseExpression(SearchedCaseExpression node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    List<SqlNode> whenClauses = new ArrayList<>();
    List<SqlNode> thenClauses = new ArrayList<>();
    node.getWhenClauses().forEach(caseWhen -> {
      whenClauses.add(process(caseWhen.getOperand(), context));
      thenClauses.add(process(caseWhen.getResult(), context));
    });
    SqlNode defaultValue = node.getDefaultValue().isPresent() ? process(node.getDefaultValue().get(), context)
        : CalciteUtil.createLiteralNull(ZERO);
    return CASE.createCall(null, pos, null, CalciteUtil.createSqlNodeList(whenClauses, pos),
        CalciteUtil.createSqlNodeList(thenClauses, pos), defaultValue);
  }

  @Override
  protected SqlNode visitLikePredicate(LikePredicate node, ParserVisitorContext context) {
    return LIKE.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitIsNotNullPredicate(IsNotNullPredicate node, ParserVisitorContext context) {
    return IS_NOT_NULL.createCall(getPos(node), process(node.getValue(), context));
  }

  @Override
  protected SqlNode visitIsNullPredicate(IsNullPredicate node, ParserVisitorContext context) {
    return IS_NULL.createCall(getPos(node), process(node.getValue(), context));
  }

  @Override
  protected SqlNode visitArrayConstructor(ArrayConstructor node, ParserVisitorContext context) {
    return ARRAY_VALUE_CONSTRUCTOR.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitSubscriptExpression(SubscriptExpression node, ParserVisitorContext context) {
    return ITEM.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitLongLiteral(LongLiteral node, ParserVisitorContext context) {
    return CalciteUtil.createLiteralNumber(node.getValue(), getPos(node));
  }

  @Override
  protected SqlNode visitParameter(Parameter node, ParserVisitorContext context) {
    return CalciteUtil.createLiteralNumber(node.getPosition(), getPos(node));
  }

  @Override
  protected SqlNode visitLogicalBinaryExpression(LogicalBinaryExpression node, ParserVisitorContext context) {
    if (node.getOperator().equals(LogicalBinaryExpression.Operator.AND)) {
      return AND.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
    } else {
      return OR.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
    }
  }

  @Override
  protected SqlNode visitSubqueryExpression(SubqueryExpression node, ParserVisitorContext context) {
    return process(node.getQuery(), context);
  }

  @Override
  protected SqlNode visitSortItem(SortItem node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    SqlNode operand = process(node.getSortKey(), context);
    SqlNode ordering =
        node.getOrdering().equals(SortItem.Ordering.DESCENDING) ? DESC.createCall(pos, operand) : operand;
    if (node.getNullOrdering().equals(SortItem.NullOrdering.FIRST)) {
      return NULLS_FIRST.createCall(pos, ordering);
    } else if (node.getNullOrdering().equals(SortItem.NullOrdering.LAST)) {
      return NULLS_LAST.createCall(pos, ordering);
    }
    return ordering;
  }

  @Override
  protected SqlNode visitTable(Table node, ParserVisitorContext context) {
    return convertQualifiedName(node.getName(), getPos(node));
  }

  @Override
  protected SqlNode visitUnnest(Unnest node, ParserVisitorContext context) {
    if (node.isWithOrdinality()) {
      return UNNEST_WITH_ORDINALITY.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
    }
    return UNNEST.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitLateral(Lateral node, ParserVisitorContext context) {
    return process(node.getQuery(), context);
  }

  @Override
  protected SqlNode visitValues(Values node, ParserVisitorContext context) {
    return VALUES.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitRow(Row node, ParserVisitorContext context) {
    return ROW.createCall(getPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitTableSubquery(TableSubquery node, ParserVisitorContext context) {
    return process(node.getQuery(), context);
  }

  @Override
  protected SqlNode visitAliasedRelation(AliasedRelation node, ParserVisitorContext context) {
    List<SqlNode> operands = new ArrayList<>();
    operands.add(process(node.getRelation(), context));
    operands.add(visitIdentifier(node.getAlias(), context));
    operands.addAll(toListOfSqlNode(node.getColumnNames(), context));
    return AS.createCall(getPos(node), operands);
  }

  @Override
  protected SqlNode visitSampledRelation(SampledRelation node, ParserVisitorContext context) {
    List<SqlNode> operands = new ArrayList<>();
    operands.add(process(node.getRelation(), context));
    // percentage of presto is from 1 to 100, while calcite keeps (0, 1)
    SqlNode percentageNode = process(node.getSamplePercentage(), context);
    float percentage = 0;
    if (percentageNode instanceof SqlNumericLiteral) {
      // round to 2 decimal points
      percentage = (float) (((SqlNumericLiteral) percentageNode).longValue(true) / 100.0);
    } else {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    operands.add(SqlLiteral.createSample(
        SqlSampleSpec.createTableSample(node.getType().equals(SampledRelation.Type.BERNOULLI), percentage),
        getPos(node)));
    return TABLESAMPLE.createCall(getPos(node), operands);
  }

  @Override
  protected SqlNode visitJoin(Join node, ParserVisitorContext context) {
    SqlNode left = process(node.getLeft(), context);
    SqlNode right = process(node.getRight(), context);
    SqlLiteral natural = CalciteUtil.createLiteralBoolean(false, ZERO);
    JoinConditionType conditionType = JoinConditionType.NONE;
    SqlNode condition = null;
    if (node.getCriteria().isPresent()) {
      JoinCriteria joinCriteria = node.getCriteria().get();
      natural = CalciteUtil.createLiteralBoolean(joinCriteria instanceof NaturalJoin, ZERO);
      if (joinCriteria instanceof JoinOn) {
        conditionType = JoinConditionType.ON;
        condition = process(((JoinOn) joinCriteria).getExpression(), context);
      } else if (joinCriteria instanceof JoinUsing) {
        conditionType = JoinConditionType.USING;
        condition = toSqlNodeList(((JoinUsing) joinCriteria).getColumns(), context, getPos(node));
      }
    }
    JoinType joinType = JOIN_TYPE_MAP.get(node.getType());
    if (joinType == null) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    return new SqlJoin(getPos(node), left, natural, joinType.symbol(ZERO), right, conditionType.symbol(ZERO),
        condition);
  }

  @Override
  protected SqlNode visitExists(ExistsPredicate node, ParserVisitorContext context) {
    return EXISTS.createCall(getPos(node), process(node.getSubquery(), context));
  }

  @Override
  protected SqlNode visitCast(Cast node, ParserVisitorContext context) {
    SqlDataTypeSpec spec = (SqlDataTypeSpec) process(node.getType(), context);
    return CAST.createCall(getPos(node), process(node.getExpression(), context), spec);
  }

  @Override
  protected SqlNode visitFieldReference(FieldReference node, ParserVisitorContext context) {
    return CalciteUtil.createLiteralNumber(node.getFieldIndex(), getPos(node));
  }

  @Override
  protected SqlNode visitWindowSpecification(WindowSpecification node, ParserVisitorContext context) {
    SqlParserPos pos = getPos(node);
    SqlNodeList partitionList = toSqlNodeList(node.getPartitionBy(), context, pos);
    SqlNodeList orderList = ObjectUtils.defaultIfNull((SqlNodeList) processOptional(node.getOrderBy(), context),
        createSqlNodeList(Collections.emptyList(), ZERO));
    ParserVisitorContext frameContext = new ParserVisitorContext();
    if (node.getFrame().isPresent()) {
      process(node.getFrame().get(), frameContext);
    }
    return frameContext.createWindow(pos, partitionList, orderList);
  }

  @Override
  protected SqlNode visitWindowFrame(WindowFrame node, ParserVisitorContext context) {
    context.isRows = node.getType().equals(WindowFrame.Type.ROWS);
    context.lowerBound = visitFrameBound(node.getStart(), context);
    context.upperBound = node.getEnd().isPresent() ? visitFrameBound(node.getEnd().get(), context) : null;
    return null;
  }

  @Override
  protected SqlNode visitFrameBound(FrameBound node, ParserVisitorContext context) {
    if (node.getValue().isPresent()) {
      return getUnresolvedFunction(node.getType().name(),
          Collections.singletonList(process(node.getValue().get(), context)));
    }
    return SqlLiteral.createSymbol(node.getType(), ZERO);
  }

  @Override
  protected SqlNode visitCallArgument(CallArgument node, ParserVisitorContext context) {
    return process(node.getValue(), context);
  }

  @Override
  protected SqlNode visitLikeClause(LikeClause node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCreateSchema(CreateSchema node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDropSchema(DropSchema node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRenameSchema(RenameSchema node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCreateTable(CreateTable node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCreateTableAsSelect(CreateTableAsSelect node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitProperty(Property node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDropTable(DropTable node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRenameTable(RenameTable node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRenameColumn(RenameColumn node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDropColumn(DropColumn node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitAddColumn(AddColumn node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitAnalyze(Analyze node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCreateView(CreateView node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDropView(DropView node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCreateMaterializedView(CreateMaterializedView node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDropMaterializedView(DropMaterializedView node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRefreshMaterializedView(RefreshMaterializedView node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitInsert(Insert node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCall(Call node, ParserVisitorContext context) {
    return PROCEDURE_CALL.createCall(getPos(node),
        getUnresolvedFunction(node.getName().toString(), toListOfSqlNode(node.getArguments(), context)));
  }

  @Override
  protected SqlNode visitDelete(Delete node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitStartTransaction(StartTransaction node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCreateRole(CreateRole node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDropRole(DropRole node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitGrantRoles(GrantRoles node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRevokeRoles(RevokeRoles node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSetRole(SetRole node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitGrant(Grant node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRevoke(Revoke node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowGrants(ShowGrants node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowRoles(ShowRoles node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowRoleGrants(ShowRoleGrants node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitTransactionMode(TransactionMode node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitIsolationLevel(Isolation node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitTransactionAccessMode(TransactionAccessMode node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCommit(Commit node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRollback(Rollback node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitAtTimeZone(AtTimeZone node, ParserVisitorContext context) {
    return getUnresolvedFunction("AT_TIMEZONE", getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNodeList visitGroupBy(GroupBy node, ParserVisitorContext context) {
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlNode visitCube(Cube node, ParserVisitorContext context) {
    return CUBE.createCall(getPos(node), toListOfSqlNode(node.getExpressions(), context));
  }

  @Override
  protected SqlNode visitGroupingSets(GroupingSets node, ParserVisitorContext context) {
    List<List<Expression>> sets = node.getSets();
    List<SqlNode> operands = new ArrayList<>();
    for (List<Expression> expressions : sets) {
      if (expressions.isEmpty()) {
        operands.add(CalciteUtil.createSqlNodeList(Collections.emptyList()));
      } else if (expressions.size() > 1) {
        operands.add(ROW.createCall(ZERO, toSqlNodeList(expressions, context, getPos(node))));
      } else {
        operands.add(process(expressions.get(0), context));
      }
    }
    return GROUPING_SETS.createCall(getPos(node), operands);
  }

  @Override
  protected SqlNode visitRollup(Rollup node, ParserVisitorContext context) {
    return ROLLUP.createCall(getPos(node), toListOfSqlNode(node.getExpressions(), context));
  }

  @Override
  protected SqlNode visitSimpleGroupBy(SimpleGroupBy node, ParserVisitorContext context) {
    Preconditions.checkArgument(node.getExpressions().size() == 1,
        "SimpleGroupBy should have only one element in the expression.");
    return process(node.getExpressions().get(0), context);
  }

  @Override
  protected SqlNode visitQuantifiedComparisonExpression(QuantifiedComparisonExpression node,
      ParserVisitorContext context) {
    SqlNode left = process(node.getValue(), context);
    SqlNode right = process(node.getSubquery(), context);
    SqlParserPos pos = getPos(node);
    if (node.getQuantifier().equals(QuantifiedComparisonExpression.Quantifier.ALL)) {
      return ALL_COMPARISON_OPERATOR.get(node.getOperator()).createCall(pos, left, right);
    } else {
      return SOME_COMPARISON_OPERATOR.get(node.getOperator()).createCall(pos, left, right);
    }
  }

  @Override
  protected SqlNode visitLambdaArgumentDeclaration(LambdaArgumentDeclaration node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, LAMBDA_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitBindExpression(BindExpression node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, LAMBDA_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitGroupingOperation(GroupingOperation node, ParserVisitorContext context) {
    return GROUPING.createCall(getPos(node), toListOfSqlNode(node.getGroupingColumns(), context));
  }

  @Override
  protected SqlNode visitCurrentUser(CurrentUser node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitPrepare(Prepare node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDeallocate(Deallocate node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitExecute(Execute node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDescribeOutput(DescribeOutput node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitDescribeInput(DescribeInput node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitExplain(Explain node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowTables(ShowTables node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowSchemas(ShowSchemas node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowCatalogs(ShowCatalogs node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowColumns(ShowColumns node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowStats(ShowStats node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowCreate(ShowCreate node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowFunctions(ShowFunctions node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitUse(Use node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitShowSession(ShowSession node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSetSession(SetSession node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitResetSession(ResetSession node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitExplainOption(ExplainOption node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitTryExpression(TryExpression node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  protected SqlNode visitColumnDefinition(ColumnDefinition node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSymbolReference(SymbolReference node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  protected SqlNode visitFetchFirst(FetchFirst node, ParserVisitorContext context) {
    if (node.isWithTies()) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    if (!node.getRowCount().isPresent()) {
      return null;
    }
    return process(node.getRowCount().get(), context);
  }

  @Override
  protected SqlNode visitLimit(Limit node, ParserVisitorContext context) {
    return process(node.getRowCount(), context);
  }

  /**
   * Limit All is null in Calcite
   */
  @Override
  protected SqlNode visitAllRows(AllRows node, ParserVisitorContext context) {
    return null;
  }

  @Override
  protected SqlNode visitWindowReference(WindowReference node, ParserVisitorContext context) {
    return visitIdentifier(node.getName(), context);
  }

  @Override
  protected SqlNode visitWindowDefinition(WindowDefinition node, ParserVisitorContext context) {
    SqlWindow window = (SqlWindow) visitWindowSpecification(node.getWindow(), context);
    return SqlWindow.create(visitIdentifier(node.getName(), context), window.getRefName(), window.getPartitionList(),
        window.getOrderList(), createLiteralBoolean(window.isRows(), ZERO), window.getLowerBound(),
        window.getUpperBound(), createLiteralBoolean(window.isAllowPartial(), ZERO), window.getParserPosition());
  }

  @Override
  protected SqlNode visitMergeInsert(MergeInsert node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitMergeUpdate(MergeUpdate node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitMergeDelete(MergeDelete node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSetSchemaAuthorization(SetSchemaAuthorization node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRenameView(RenameView node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSetViewAuthorization(SetViewAuthorization node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitComment(Comment node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSetTableAuthorization(SetTableAuthorization node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitUpdate(Update node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitUpdateAssignment(UpdateAssignment node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitSetPath(SetPath node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitPathSpecification(PathSpecification node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitPathElement(PathElement node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitCurrentPath(CurrentPath node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  // TODO Requires the special SqlOperator to handle format function in trino
  @Override
  protected SqlNode visitFormat(Format node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  protected SqlNode visitMerge(Merge node, ParserVisitorContext context) {
    throw new UnhandledASTNodeException(node, DDL_NOT_SUPPORT_MSG);
  }

  @Override
  protected SqlNode visitRowDataType(RowDataType node, ParserVisitorContext context) {
    ParserVisitorContext rowContext = new ParserVisitorContext();
    node.getFields().forEach(f -> process(f, rowContext));
    return rowContext.createRowType(getPos(node));
  }

  @Override
  protected SqlNode visitGenericDataType(GenericDataType node, ParserVisitorContext context) {
    if (node.getArguments().isEmpty()) {
      return SqlTypeUtil.convertTypeToSpec(
          sqlTypeFactory.createSqlType(SqlTypeName.valueOf((node.getName().getValue().toUpperCase()))));
    }
    switch (node.getName().getValue().toUpperCase()) {
      case "DECIMAL":
        int precision = getNumberFromNumericParameter(node.getArguments().get(0));
        int scale = node.getArguments().size() > 1 ? getNumberFromNumericParameter(node.getArguments().get(0)) : 0;
        return SqlTypeUtil.convertTypeToSpec(sqlTypeFactory.createSqlType(SqlTypeName.DECIMAL, precision, scale));
      case "CHAR":
      case "VARCHAR":
      case "BINARY":
        int len = getNumberFromNumericParameter(node.getArguments().get(0));
        return SqlTypeUtil.convertTypeToSpec(
            sqlTypeFactory.createSqlType(SqlTypeName.valueOf(node.getName().getValue().toUpperCase()), len));
      case "ARRAY":
        return new SqlArrayTypeSpec((SqlDataTypeSpec) process(node.getArguments().get(0)), ZERO);
      case "MAP":
        return new SqlMapTypeSpec((SqlDataTypeSpec) process(node.getArguments().get(0)),
            (SqlDataTypeSpec) process(node.getArguments().get(1)), ZERO);
    }
    throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
  }

  @Override
  protected SqlNode visitRowField(RowDataType.Field node, ParserVisitorContext context) {
    if (!node.getName().isPresent()) {
      throw new UnhandledASTNodeException(node, UNSUPPORTED_EXCEPTION_MSG);
    }
    context.fieldTypes.add((SqlDataTypeSpec) process(node.getType(), context));
    context.fieldNames.add((SqlIdentifier) process(node.getName().get(), context));
    return null;
  }

  @Override
  protected SqlNode visitIntervalDataType(IntervalDayTimeDataType node, ParserVisitorContext context) {
    return new SqlIntervalQualifier(TIME_UNIT_MAP.get(node.getFrom().name()), TIME_UNIT_MAP.get(node.getTo().name()),
        getPos(node));
  }

  int getNumberFromNumericParameter(Node numericParameter) {
    return Integer.parseInt(((NumericParameter) numericParameter).getValue());
  }

  @Override
  protected SqlNode visitDateTimeType(DateTimeDataType node, ParserVisitorContext context) {
    int precision = node.getPrecision().isPresent() ? getNumberFromNumericParameter(node.getPrecision().get()) : -1;
    SqlParserPos pos = getPos(node);
    if (node.isWithTimeZone()) {
      if (node.getType().equals(DateTimeDataType.Type.TIME)) {
        return new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.TIME_WITH_LOCAL_TIME_ZONE, precision, pos),
            pos);
      }
      return new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.TIMESTAMP_WITH_LOCAL_TIME_ZONE, precision, pos),
          pos);
    }
    return new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.valueOf(node.getType().name()), precision, pos),
        pos);
  }

  @Override
  protected SqlNode visitNumericTypeParameter(NumericParameter node, ParserVisitorContext context) {
    return createLiteralNumber(getNumberFromNumericParameter(node), getPos(node));
  }

  @Override
  protected SqlNode visitTypeParameter(TypeParameter node, ParserVisitorContext context) {
    return process(node.getValue(), context);
  }
}

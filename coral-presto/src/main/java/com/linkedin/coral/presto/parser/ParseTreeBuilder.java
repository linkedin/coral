/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.facebook.presto.sql.tree.*;
import com.facebook.presto.sql.tree.Extract.Field;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.google.common.collect.ImmutableSet;
import org.apache.calcite.avatica.util.TimeUnit;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;
import org.apache.calcite.util.TimeString;
import org.apache.calcite.util.TimestampString;

import com.linkedin.coral.hive.hive2rel.HiveTypeSystem;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static com.linkedin.coral.presto.parser.calcite.CalciteUtil.*;
import static java.lang.String.format;
import static org.apache.calcite.sql.SqlFunctionCategory.USER_DEFINED_FUNCTION;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.SOME_GE;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


public class ParseTreeBuilder extends AstVisitor<SqlNode, ParserVisitorContext> {
  private static final String UNSUPPORTED_EXCEPTION_MSG = "%s is not supported in the visit.";
  private static final String DDL_NOT_SUPPORT_MSG = "DDL %s is not supported.";
  private static final String LAMBDA_NOT_SUPPORT_MSG = "Lambda expression %s is not supported.";
  private static final ImmutableMap<Join.Type, JoinType> JOIN_TYPE_MAP =
      ImmutableMap.of(Join.Type.LEFT, JoinType.LEFT, Join.Type.RIGHT, JoinType.RIGHT, Join.Type.FULL, JoinType.FULL,
          Join.Type.INNER, JoinType.INNER, Join.Type.CROSS, JoinType.CROSS);
  // TODO there are a couple of fields don't have corresponding TimeUnit
  private static final ImmutableMap<Field, TimeUnit> TIME_UNIT_MAP =
      ImmutableMap.<Field, TimeUnit> builder().put(Field.YEAR, TimeUnit.YEAR).put(Field.QUARTER, TimeUnit.QUARTER)
          .put(Field.MONTH, TimeUnit.MONTH).put(Field.WEEK, TimeUnit.WEEK).put(Field.DAY, TimeUnit.DAY)
          .put(Field.DAY_OF_MONTH, TimeUnit.DAY).put(Field.DAY_OF_WEEK, TimeUnit.DOW).put(Field.DOW, TimeUnit.DOW)
          .put(Field.DAY_OF_YEAR, TimeUnit.DOY).put(Field.DOY, TimeUnit.DOY).put(Field.HOUR, TimeUnit.HOUR)
          .put(Field.MINUTE, TimeUnit.MINUTE).put(Field.SECOND, TimeUnit.SECOND).build();
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
  private static final ImmutableSet<SqlKind> NULL_CARED_OPERATOR = ImmutableSet.of(SqlKind.FIRST_VALUE, SqlKind.LAST_VALUE, SqlKind.LEAD, SqlKind.LAG);

  private final SqlTypeFactoryImpl sqlTypeFactory = new SqlTypeFactoryImpl(new HiveTypeSystem());
  private final HiveFunctionResolver functionResolver =
      new HiveFunctionResolver(new StaticHiveFunctionRegistry(), new ConcurrentHashMap<>());

  // convert the Presto node parse location to the Calcite SqlParserPos
  private SqlParserPos getParserPos(Node node) {
    if (node.getLocation().isPresent()) {
      return new SqlParserPos(node.getLocation().get().getLineNumber(), node.getLocation().get().getColumnNumber());
    }
    return ZERO;
  }

  private UnsupportedOperationException getUnsupportedException(Node node) {
    return new UnsupportedOperationException(format(UNSUPPORTED_EXCEPTION_MSG, node.toString()));
  }

  private UnsupportedOperationException getDDLException(Node node) {
    return new UnsupportedOperationException(format(DDL_NOT_SUPPORT_MSG, node.toString()));
  }

  private UnsupportedOperationException getLambdaException(Node node) {
    return new UnsupportedOperationException(format(LAMBDA_NOT_SUPPORT_MSG, node.toString()));
  }

  private List<SqlNode> getChildren(Node node, ParserVisitorContext context) {
    return getListSqlNode(node.getChildren(), context);
  }

  private SqlNodeList getChildSqlNodeList(Node node, ParserVisitorContext context) {
    return new SqlNodeList(getChildren(node, context), getParserPos(node));
  }

  private List<SqlNode> getListSqlNode(List<? extends Node> nodes, ParserVisitorContext context) {
    if (nodes == null) {
      return Collections.emptyList();
    }
    return nodes.stream().map(n -> visitNode(n, context)).collect(Collectors.toList());
  }

  private SqlNode parseExpression(String expression) {
    try {
      return parseStatement(expression);
    } catch (SqlParseException e) {
      throw new RuntimeException(format("Failed to parse the expression %s.", expression), e);
    }
  }

  private SqlIdentifier convertQualifiedName(QualifiedName name, SqlParserPos pos) {
    return createSqlIdentifier(pos, name.getOriginalParts().toArray(new String[0]));
  }

  /**
   * Because the accept method of node is protected, we cannot leverage the accept method to route the visit to right type.
   * We switch based on the class name of the object to route it to right visit function.
   */
  @Override
  protected SqlNode visitNode(Node node, ParserVisitorContext context) {
    if (node == null) {
      return null;
    }
    String[] path = splitIdentifierString(node.getClass().getName());
    String classBaseName = path[path.length - 1];
    switch (classBaseName) {
      case "CurrentTime":
        return visitCurrentTime((CurrentTime) node, context);
      case "Extract":
        return visitExtract((Extract) node, context);
      case "ArithmeticBinaryExpression":
        return visitArithmeticBinary((ArithmeticBinaryExpression) node, context);
      case "BetweenPredicate":
        return visitBetweenPredicate((BetweenPredicate) node, context);
      case "CoalesceExpression":
        return visitCoalesceExpression((CoalesceExpression) node, context);
      case "ComparisonExpression":
        return visitComparisonExpression((ComparisonExpression) node, context);
      case "DoubleLiteral":
        return visitDoubleLiteral((DoubleLiteral) node, context);
      case "DecimalLiteral":
        return visitDecimalLiteral((DecimalLiteral) node, context);
      case "Query":
        return visitQuery((Query) node, context);
      case "GenericLiteral":
        return visitGenericLiteral((GenericLiteral) node, context);
      case "TimeLiteral":
        return visitTimeLiteral((TimeLiteral) node, context);
      case "With":
        return visitWith((With) node, context);
      case "WithQuery":
        return visitWithQuery((WithQuery) node, context);
      case "Select":
        return visitSelect((Select) node, context);
      case "OrderBy":
        return visitOrderBy((OrderBy) node, context);
      case "Offset":
        return visitOffset((Offset) node, context);
      case "QuerySpecification":
        return visitQuerySpecification((QuerySpecification) node, context);
      case "Union":
        return visitUnion((Union) node, context);
      case "Intersect":
        return visitIntersect((Intersect) node, context);
      case "Except":
        return visitExcept((Except) node, context);
      case "TimestampLiteral":
        return visitTimestampLiteral((TimestampLiteral) node, context);
      case "WhenClause":
        return visitWhenClause((WhenClause) node, context);
      case "IntervalLiteral":
        return visitIntervalLiteral((IntervalLiteral) node, context);
      case "InPredicate":
        return visitInPredicate((InPredicate) node, context);
      case "FunctionCall":
        return visitFunctionCall((FunctionCall) node, context);
      case "LambdaExpression":
        return visitLambdaExpression((LambdaExpression) node, context);
      case "SimpleCaseExpression":
        return visitSimpleCaseExpression((SimpleCaseExpression) node, context);
      case "StringLiteral":
        return visitStringLiteral((StringLiteral) node, context);
      case "CharLiteral":
        return visitCharLiteral((CharLiteral) node, context);
      case "BinaryLiteral":
        return visitBinaryLiteral((BinaryLiteral) node, context);
      case "BooleanLiteral":
        return visitBooleanLiteral((BooleanLiteral) node, context);
      case "EnumLiteral":
        return visitEnumLiteral((EnumLiteral) node, context);
      case "InListExpression":
        return visitInListExpression((InListExpression) node, context);
      case "Identifier":
        return visitIdentifier((Identifier) node, context);
      case "DereferenceExpression":
        return visitDereferenceExpression((DereferenceExpression) node, context);
      case "NullIfExpression":
        return visitNullIfExpression((NullIfExpression) node, context);
      case "IfExpression":
        return visitIfExpression((IfExpression) node, context);
      case "NullLiteral":
        return visitNullLiteral((NullLiteral) node, context);
      case "ArithmeticUnaryExpression":
        return visitArithmeticUnary((ArithmeticUnaryExpression) node, context);
      case "NotExpression":
        return visitNotExpression((NotExpression) node, context);
      case "SingleColumn":
        return visitSingleColumn((SingleColumn) node, context);
      case "AllColumns":
        return visitAllColumns((AllColumns) node, context);
      case "SearchedCaseExpression":
        return visitSearchedCaseExpression((SearchedCaseExpression) node, context);
      case "LikePredicate":
        return visitLikePredicate((LikePredicate) node, context);
      case "IsNotNullPredicate":
        return visitIsNotNullPredicate((IsNotNullPredicate) node, context);
      case "IsNullPredicate":
        return visitIsNullPredicate((IsNullPredicate) node, context);
      case "ArrayConstructor":
        return visitArrayConstructor((ArrayConstructor) node, context);
      case "SubscriptExpression":
        return visitSubscriptExpression((SubscriptExpression) node, context);
      case "LongLiteral":
        return visitLongLiteral((LongLiteral) node, context);
      case "Parameter":
        return visitParameter((Parameter) node, context);
      case "LogicalBinaryExpression":
        return visitLogicalBinaryExpression((LogicalBinaryExpression) node, context);
      case "SubqueryExpression":
        return visitSubqueryExpression((SubqueryExpression) node, context);
      case "SortItem":
        return visitSortItem((SortItem) node, context);
      case "Table":
        return visitTable((Table) node, context);
      case "Unnest":
        return visitUnnest((Unnest) node, context);
      case "Lateral":
        return visitLateral((Lateral) node, context);
      case "Values":
        return visitValues((Values) node, context);
      case "Row":
        return visitRow((Row) node, context);
      case "TableSubquery":
        return visitTableSubquery((TableSubquery) node, context);
      case "AliasedRelation":
        return visitAliasedRelation((AliasedRelation) node, context);
      case "SampledRelation":
        return visitSampledRelation((SampledRelation) node, context);
      case "Join":
        return visitJoin((Join) node, context);
      case "ExistsPredicate":
        return visitExists((ExistsPredicate) node, context);
      case "TryExpression":
        return visitTryExpression((TryExpression) node, context);
      case "Cast":
        return visitCast((Cast) node, context);
      case "FieldReference":
        return visitFieldReference((FieldReference) node, context);
      case "Window":
        return visitWindow((Window) node, context);
      case "WindowFrame":
        return visitWindowFrame((WindowFrame) node, context);
      case "FrameBound":
        return visitFrameBound((FrameBound) node, context);
      case "CallArgument":
        return visitCallArgument((CallArgument) node, context);
      case "ColumnDefinition":
        return visitColumnDefinition((ColumnDefinition) node, context);
      case "LikeClause":
        return visitLikeClause((LikeClause) node, context);
      case "CreateSchema":
        return visitCreateSchema((CreateSchema) node, context);
      case "DropSchema":
        return visitDropSchema((DropSchema) node, context);
      case "RenameSchema":
        return visitRenameSchema((RenameSchema) node, context);
      case "CreateTable":
        return visitCreateTable((CreateTable) node, context);
      case "CreateType":
        return visitCreateType((CreateType) node, context);
      case "CreateTableAsSelect":
        return visitCreateTableAsSelect((CreateTableAsSelect) node, context);
      case "Property":
        return visitProperty((Property) node, context);
      case "DropTable":
        return visitDropTable((DropTable) node, context);
      case "RenameTable":
        return visitRenameTable((RenameTable) node, context);
      case "RenameColumn":
        return visitRenameColumn((RenameColumn) node, context);
      case "DropColumn":
        return visitDropColumn((DropColumn) node, context);
      case "AddColumn":
        return visitAddColumn((AddColumn) node, context);
      case "Analyze":
        return visitAnalyze((Analyze) node, context);
      case "CreateView":
        return visitCreateView((CreateView) node, context);
      case "DropView":
        return visitDropView((DropView) node, context);
      case "CreateMaterializedView":
        return visitCreateMaterializedView((CreateMaterializedView) node, context);
      case "DropMaterializedView":
        return visitDropMaterializedView((DropMaterializedView) node, context);
      case "RefreshMaterializedView":
        return visitRefreshMaterializedView((RefreshMaterializedView) node, context);
      case "CreateFunction":
        // There is visitDropFunction with CreateFunction instance, but it's not used in the presto repo.
        return visitCreateFunction((CreateFunction) node, context);
      case "AlterFunction":
        return visitAlterFunction((AlterFunction) node, context);
      case "DropFunction":
        return visitDropFunction((DropFunction) node, context);
      case "Insert":
        return visitInsert((Insert) node, context);
      case "Call":
        return visitCall((Call) node, context);
      case "Delete":
        return visitDelete((Delete) node, context);
      case "StartTransaction":
        return visitStartTransaction((StartTransaction) node, context);
      case "CreateRole":
        return visitCreateRole((CreateRole) node, context);
      case "DropRole":
        return visitDropRole((DropRole) node, context);
      case "GrantRoles":
        return visitGrantRoles((GrantRoles) node, context);
      case "RevokeRoles":
        return visitRevokeRoles((RevokeRoles) node, context);
      case "SetRole":
        return visitSetRole((SetRole) node, context);
      case "Grant":
        return visitGrant((Grant) node, context);
      case "Revoke":
        return visitRevoke((Revoke) node, context);
      case "ShowGrants":
        return visitShowGrants((ShowGrants) node, context);
      case "ShowRoles":
        return visitShowRoles((ShowRoles) node, context);
      case "ShowRoleGrants":
        return visitShowRoleGrants((ShowRoleGrants) node, context);
      case "TransactionMode":
        return visitTransactionMode((TransactionMode) node, context);
      case "Isolation":
        return visitIsolationLevel((Isolation) node, context);
      case "TransactionAccessMode":
        return visitTransactionAccessMode((TransactionAccessMode) node, context);
      case "Commit":
        return visitCommit((Commit) node, context);
      case "Rollback":
        return visitRollback((Rollback) node, context);
      case "AtTimeZone":
        return visitAtTimeZone((AtTimeZone) node, context);
      case "GroupBy":
        return visitGroupBy((GroupBy) node, context);
      case "Cube":
        return visitCube((Cube) node, context);
      case "GroupingSets":
        return visitGroupingSets((GroupingSets) node, context);
      case "Rollup":
        return visitRollup((Rollup) node, context);
      case "SimpleGroupBy":
        return visitSimpleGroupBy((SimpleGroupBy) node, context);
      case "SymbolReference":
        return visitSymbolReference((SymbolReference) node, context);
      case "QuantifiedComparisonExpression":
        return visitQuantifiedComparisonExpression((QuantifiedComparisonExpression) node, context);
      case "LambdaArgumentDeclaration":
        return visitLambdaArgumentDeclaration((LambdaArgumentDeclaration) node, context);
      case "BindExpression":
        return visitBindExpression((BindExpression) node, context);
      case "GroupingOperation":
        return visitGroupingOperation((GroupingOperation) node, context);
      case "CurrentUser":
        return visitCurrentUser((CurrentUser) node, context);
      case "Return":
        return visitReturn((Return) node, context);
      case "ExternalBodyReference":
        return visitExternalBodyReference((ExternalBodyReference) node, context);
      case "ExplainOption":
        return visitExplainOption((ExplainOption) node, context);
      case "ResetSession":
        return visitResetSession((ResetSession) node, context);
      case "SetSession":
        return visitSetSession((SetSession) node, context);
      case "ShowSession":
        return visitShowSession((ShowSession) node, context);
      case "Use":
        return visitUse((Use) node, context);
      case "ShowFunctions":
        return visitShowFunctions((ShowFunctions) node, context);
      case "ShowCreateFunction":
        return visitShowCreateFunction((ShowCreateFunction) node, context);
      case "ShowCreate":
        return visitShowCreate((ShowCreate) node, context);
      case "ShowStats":
        return visitShowStats((ShowStats) node, context);
      case "ShowColumns":
        return visitShowColumns((ShowColumns) node, context);
      case "ShowCatalogs":
        return visitShowCatalogs((ShowCatalogs) node, context);
      case "ShowSchemas ":
        return visitShowSchemas((ShowSchemas) node, context);
      case "ShowTables":
        return visitShowTables((ShowTables) node, context);
      case "Explain":
        return visitExplain((Explain) node, context);
      case "DescribeInput":
        return visitDescribeInput((DescribeInput) node, context);
      case "DescribeOutput":
        return visitDescribeOutput((DescribeOutput) node, context);
      case "Execute":
        return visitExecute((Execute) node, context);
      case "Deallocate":
        return visitDeallocate((Deallocate) node, context);
      case "Prepare":
        return visitPrepare((Prepare) node, context);
    }
    throw getUnsupportedException(node);
  }

  private SqlNumericLiteral getNumericalLiteral(String literal) {
    return SqlNumericLiteral.createExactNumeric(literal, ZERO);
  }

  /**
   * It's used for most of the functions in the presto query. The unresolved call will be resolved in validation flow.
   */
  private SqlCall getUnresolvedFunction(String functionName, List<SqlNode> operands) {
    SqlIdentifier functionIdentifier = createSqlIdentifier(SqlParserPos.ZERO, functionName);
    return createCall(new SqlUnresolvedFunction(functionIdentifier, null, null, null, null, USER_DEFINED_FUNCTION),
        operands);
  }

  private SqlCall getCall(SqlOperator operator, Node node, ParserVisitorContext context) {
    if (operator == null) {
      throw getUnsupportedException(node);
    }
    return operator.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitCurrentTime(CurrentTime node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
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
    TimeUnit unit = TIME_UNIT_MAP.get(node.getField());
    if (unit == null) {
      throw getUnsupportedException(node);
    }
    return createCall(EXTRACT,
        Arrays.asList(new SqlIntervalQualifier(unit, null, ZERO), visitNode(node.getExpression(), context)));
  }

  @Override
  protected SqlNode visitArithmeticBinary(ArithmeticBinaryExpression node, ParserVisitorContext context) {
    return getCall(OPERATOR_MAP.get(node.getOperator().getValue()), node, context);
  }

  @Override
  protected SqlNode visitBetweenPredicate(BetweenPredicate node, ParserVisitorContext context) {
    return BETWEEN.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitCoalesceExpression(CoalesceExpression node, ParserVisitorContext context) {
    return COALESCE.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitComparisonExpression(ComparisonExpression node, ParserVisitorContext context) {
    return getCall(OPERATOR_MAP.get(node.getOperator().getValue()), node, context);
  }

  @Override
  protected SqlNode visitDoubleLiteral(DoubleLiteral node, ParserVisitorContext context) {
    return createLiteralNumber(node.getValue(), getParserPos(node));
  }

  @Override
  protected SqlNode visitDecimalLiteral(DecimalLiteral node, ParserVisitorContext context) {
    return SqlNumericLiteral.createExactNumeric(node.getValue(), getParserPos(node));
  }

  @Override
  protected SqlNode visitQuery(Query node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    SqlNode query = node.getQueryBody().accept(this, context);
    SqlNodeList orderBy = node.getOrderBy().isPresent() ? visitOrderBy(node.getOrderBy().get(), context)
        : createSqlNodeList(Collections.emptyList());
    SqlOrderBy calciteOrderBy = null;
    SqlNode fetch = node.getLimit().isPresent() ? getNumericalLiteral(node.getLimit().get()) : null;
    SqlNode offset = node.getOffset().isPresent() ? visitOffset(node.getOffset().get(), context) : null;
    if (!orderBy.getList().isEmpty() || fetch != null || offset != null) {
      calciteOrderBy = new SqlOrderBy(pos, query, orderBy, offset, fetch);
    }

    SqlNode finalQuery = calciteOrderBy == null ? query : calciteOrderBy;

    if (node.getWith().isPresent()) {
      SqlNodeList withItems = visitWith(node.getWith().get(), context);
      if (!finalQuery.getKind().equals(SqlKind.ORDER_BY)) {
        return new SqlWith(pos, withItems, finalQuery);
      } else {
        SqlOrderBy finalOrderBy = (SqlOrderBy) finalQuery;
        return new SqlOrderBy(pos, new SqlWith(pos, withItems, finalOrderBy.query), finalOrderBy.orderList,
            finalOrderBy.offset, finalOrderBy.fetch);
      }
    } else {
      return finalQuery;
    }
  }

  @Override
  protected SqlNode visitGenericLiteral(GenericLiteral node, ParserVisitorContext context) {
    return parseExpression(node.toString());
  }

  private String updateTime(String time) {
    String value = time;
    if (value.split(":").length == 2) {
      value = value + ":00";
    } else if (value.split(":").length == 1) {
      value = value + ":00:00";
    }
    return value;
  }

  @Override
  protected SqlNode visitTimeLiteral(TimeLiteral node, ParserVisitorContext context) {
    return SqlLiteral.createTime(new TimeString(updateTime(node.getValue())), 1, getParserPos(node));
  }

  @Override
  protected SqlNodeList visitWith(With node, ParserVisitorContext context) {
    if (node.isRecursive()) {
      throw new UnsupportedOperationException("With clause doesn't support recursive.");
    }
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlNode visitWithQuery(WithQuery node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    SqlIdentifier alias = visitIdentifier(node.getName(), context);
    SqlNode query = visitQuery(node.getQuery(), context);

    SqlNodeList columns = null;
    if (node.getColumnNames().isPresent()) {
      columns = createSqlNodeList(
          node.getColumnNames().get().stream().map(n -> visitNode(n, context)).collect(Collectors.toList()), pos);
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
    return getNumericalLiteral(node.getRowCount());
  }

  @Override
  protected SqlNode visitQuerySpecification(QuerySpecification node, ParserVisitorContext context) {
    SqlNodeList selectList = visitSelect(node.getSelect(), context);
    SqlNodeList keywords = node.getSelect().isDistinct()
        ? new SqlNodeList(ImmutableList.of(SqlSelectKeyword.DISTINCT.symbol(ZERO)), ZERO) : null;
    SqlNode from = node.getFrom().isPresent() ? node.getFrom().get().accept(this, context) : null;
    SqlNode where = node.getWhere().isPresent() ? visitNode(node.getWhere().get(), context) : null;
    SqlNodeList groupBy = node.getGroupBy().isPresent() ? visitGroupBy(node.getGroupBy().get(), context) : null;
    SqlNode having = node.getHaving().isPresent() ? visitNode(node.getHaving().get(), context) : null;
    SqlNodeList orderBy = node.getOrderBy().isPresent() ? visitOrderBy(node.getOrderBy().get(), context)
        : createSqlNodeList(Collections.emptyList());
    SqlNumericLiteral limit = null;
    if (node.getLimit().isPresent() && !node.getLimit().get().toUpperCase().equals("ALL")) {
      limit = getNumericalLiteral(node.getLimit().get());
    }
    SqlNode offset = node.getOffset().isPresent() ? visitOffset(node.getOffset().get(), context) : null;
    SqlSelect select =
        new SqlSelect(getParserPos(node), keywords, selectList, from, where, groupBy, having, null, null, null, null);
    if (!orderBy.getList().isEmpty() || offset != null || limit != null) {
      return new SqlOrderBy(getParserPos(node), select, orderBy, offset, limit);
    }
    return select;
  }

  @Override
  protected SqlNode visitUnion(Union node, ParserVisitorContext context) {
    if (!node.isDistinct().isPresent() || node.isDistinct().get()) {
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

    return SqlLiteral.createTimestamp(new TimestampString(updateTime(node.getValue())), 1, getParserPos(node));
  }

  @Override
  protected SqlNode visitWhenClause(WhenClause node, ParserVisitorContext context) {
    context.getWhenList().add(visitNode(node.getOperand(), context));
    context.getThenList().add(visitNode(node.getResult(), context));
    return null;
  }

  @Override
  protected SqlNode visitIntervalLiteral(IntervalLiteral node, ParserVisitorContext context) {
    TimeUnit startUnit = TimeUnit.valueOf(node.getStartField().toString());
    TimeUnit endUnit = node.getEndField().isPresent() ? TimeUnit.valueOf(node.getEndField().toString()) : null;
    SqlIntervalQualifier qualifier = new SqlIntervalQualifier(startUnit, endUnit, ZERO);
    return SqlLiteral.createInterval(node.getSign().multiplier(), node.getValue(), qualifier, getParserPos(node));
  }

  @Override
  protected SqlNode visitInPredicate(InPredicate node, ParserVisitorContext context) {
    return IN.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  /**
   * TODO generalize the HiveFunctionResolver to lookup the function for presto here.
   */
  @Override
  protected SqlNode visitFunctionCall(FunctionCall node, ParserVisitorContext context) {
    if (node.getFilter().isPresent()) {
      throw getUnsupportedException(node);
    }
    SqlParserPos pos = getParserPos(node);
    List<SqlNode> operands =
        node.getArguments().stream().map(arg -> visitNode(arg, context)).collect(Collectors.toList());
    SqlIdentifier functionName = convertQualifiedName(node.getName(), getParserPos(node));
    SqlUnresolvedFunction unresolvedFunction =
        new SqlUnresolvedFunction(functionName, null, null, null, null, SqlFunctionCategory.USER_DEFINED_FUNCTION);
    SqlLiteral functionQualifier = node.isDistinct() ? SqlLiteral.createSymbol(SqlSelectKeyword.DISTINCT, ZERO) : null;
    SqlCall call = createCall(unresolvedFunction, operands, functionQualifier);
    if (NULL_CARED_OPERATOR.contains(call.getKind())) {
        if (node.isIgnoreNulls()) {
            call = IGNORE_NULLS.createCall(pos, call);
        } else {
            call = RESPECT_NULLS.createCall(pos, call);
        }
    }
    if (node.getWindow().isPresent()) {
      return OVER.createCall(pos, call, visitWindow(node.getWindow().get(), context));
    }
    return call;
  }

  @Override
  protected SqlNode visitLambdaExpression(LambdaExpression node, ParserVisitorContext context) {
    throw getLambdaException(node);
  }

  @Override
  protected SqlNode visitSimpleCaseExpression(SimpleCaseExpression node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    SqlNode operand = visitNode(node.getOperand(), context);
    context.resetCaseWhen();
    node.getWhenClauses().forEach(caseWhen -> visitWhenClause(caseWhen, context));
    SqlNode defaultValue =
        node.getDefaultValue().isPresent() ? visitNode(node.getDefaultValue().get(), context) : createLiteralNull(ZERO);
    return CASE.createCall(null, pos, operand, createSqlNodeList(context.getWhenList(), pos),
        createSqlNodeList(context.getThenList(), pos), defaultValue);
  }

  @Override
  protected SqlNode visitStringLiteral(StringLiteral node, ParserVisitorContext context) {
    return createStringLiteral(node.getValue(), getParserPos(node));
  }

  @Override
  protected SqlNode visitCharLiteral(CharLiteral node, ParserVisitorContext context) {
    return createStringLiteral(node.getValue(), getParserPos(node));
  }

  @Override
  protected SqlNode visitBinaryLiteral(BinaryLiteral node, ParserVisitorContext context) {
    return createBinaryLiteral(node.getValue().getBytes(), getParserPos(node));
  }

  @Override
  protected SqlNode visitBooleanLiteral(BooleanLiteral node, ParserVisitorContext context) {
    return createLiteralBoolean(node.getValue(), getParserPos(node));
  }

  @Override
  protected SqlNode visitInListExpression(InListExpression node, ParserVisitorContext context) {
    return getChildSqlNodeList(node, context);
  }

  @Override
  protected SqlIdentifier visitIdentifier(Identifier node, ParserVisitorContext context) {
    return createSqlIdentifier(getParserPos(node), node.getValue());
  }

  @Override
  protected SqlNode visitDereferenceExpression(DereferenceExpression node, ParserVisitorContext context) {
    SqlNode base = visitNode(node.getBase(), context);
    if (base.getKind().equals(SqlKind.IDENTIFIER)) {
      List<String> names = new ArrayList<>(((SqlIdentifier) base).names);
      names.add(node.getField().getValue());
      return createSqlIdentifier(getParserPos(node), names.toArray(new String[0]));
    } else {
      return DOT.createCall(getParserPos(node), base, new SqlIdentifier(node.getField().getValue(), ZERO));
    }
  }

  @Override
  protected SqlNode visitNullIfExpression(NullIfExpression node, ParserVisitorContext context) {
    return NULLIF.createCall(getParserPos(node), getChildSqlNodeList(node, context));
  }

  /**
   * Convert if(condition, value1, value2) to case when condition then value1 else value2 since not all engine support if().
   */
  @Override
  protected SqlNode visitIfExpression(IfExpression node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    SqlNode defaultValue =
        node.getFalseValue().isPresent() ? visitNode(node.getFalseValue().get(), context) : createLiteralNull(ZERO);
    SqlNodeList whenList = createSqlNodeList(Collections.singletonList(visitNode(node.getCondition(), context)), pos);
    SqlNodeList thenList = createSqlNodeList(Collections.singletonList(visitNode(node.getTrueValue(), context)), pos);
    return CASE.createCall(null, pos, null, whenList, thenList, defaultValue);
  }

  @Override
  protected SqlNode visitNullLiteral(NullLiteral node, ParserVisitorContext context) {
    return createLiteralNull(getParserPos(node));
  }

  @Override
  protected SqlNode visitArithmeticUnary(ArithmeticUnaryExpression node, ParserVisitorContext context) {
    SqlNode operand = visitNode(node.getValue(), context);
    if (node.getSign().equals(ArithmeticUnaryExpression.Sign.MINUS)) {
      if (operand instanceof SqlNumericLiteral) {
        return SqlLiteral.createNegative((SqlNumericLiteral) operand, getParserPos(node));
      }
      return UNARY_MINUS.createCall(getParserPos(node), operand);
    }
    return operand;
  }

  @Override
  protected SqlNode visitNotExpression(NotExpression node, ParserVisitorContext context) {
    SqlNode operand = visitNode(node.getValue(), context);
    SqlParserPos pos = getParserPos(node);
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
      return AS.createCall(getParserPos(node), visitNode(node.getExpression(), context),
          visitNode(node.getAlias().get(), context));
    }
    return visitNode(node.getExpression(), context);
  }

  @Override
  protected SqlNode visitAllColumns(AllColumns node, ParserVisitorContext context) {
    if (node.getPrefix().isPresent()) {
      List<String> names = new ArrayList<>(node.getPrefix().get().getOriginalParts());
      names.add("");
      return createSqlIdentifier(getParserPos(node), names.toArray(new String[0]));
    } else {
      return createStarIdentifier(getParserPos(node));
    }
  }

  @Override
  protected SqlNode visitSearchedCaseExpression(SearchedCaseExpression node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    context.resetCaseWhen();
    node.getWhenClauses().forEach(caseWhen -> visitWhenClause(caseWhen, context));
    SqlNode defaultValue =
        node.getDefaultValue().isPresent() ? visitNode(node.getDefaultValue().get(), context) : createLiteralNull(ZERO);
    return CASE.createCall(null, pos, null, createSqlNodeList(context.getWhenList(), pos),
        createSqlNodeList(context.getThenList(), pos), defaultValue);
  }

  @Override
  protected SqlNode visitLikePredicate(LikePredicate node, ParserVisitorContext context) {
    return LIKE.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitIsNotNullPredicate(IsNotNullPredicate node, ParserVisitorContext context) {
    return IS_NOT_NULL.createCall(getParserPos(node), visitNode(node.getValue(), context));
  }

  @Override
  protected SqlNode visitIsNullPredicate(IsNullPredicate node, ParserVisitorContext context) {
    return IS_NULL.createCall(getParserPos(node), visitNode(node.getValue(), context));
  }

  @Override
  protected SqlNode visitArrayConstructor(ArrayConstructor node, ParserVisitorContext context) {
    return ARRAY_VALUE_CONSTRUCTOR.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitSubscriptExpression(SubscriptExpression node, ParserVisitorContext context) {
    return ITEM.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitLongLiteral(LongLiteral node, ParserVisitorContext context) {
    return createLiteralNumber(node.getValue(), getParserPos(node));
  }

  @Override
  protected SqlNode visitParameter(Parameter node, ParserVisitorContext context) {
    return createLiteralNumber(node.getPosition(), getParserPos(node));
  }

  @Override
  protected SqlNode visitLogicalBinaryExpression(LogicalBinaryExpression node, ParserVisitorContext context) {
    if (node.getOperator().equals(LogicalBinaryExpression.Operator.AND)) {
      return AND.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
    } else {
      return OR.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
    }
  }

  @Override
  protected SqlNode visitSubqueryExpression(SubqueryExpression node, ParserVisitorContext context) {
    return visitNode(node.getQuery(), context);
  }

  @Override
  protected SqlNode visitSortItem(SortItem node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    SqlNode operand = visitNode(node.getSortKey(), context);
    SqlNode ordering = null;
    if (node.getOrdering().equals(SortItem.Ordering.DESCENDING)) {
      ordering = DESC.createCall(pos, operand);
    } else {
      ordering = operand;
    }
    if (node.getNullOrdering().equals(SortItem.NullOrdering.FIRST)) {
      return NULLS_FIRST.createCall(pos, ordering);
    } else if (node.getNullOrdering().equals(SortItem.NullOrdering.LAST)) {
      return NULLS_LAST.createCall(pos, ordering);
    }
    return ordering;
  }

  @Override
  protected SqlNode visitTable(Table node, ParserVisitorContext context) {
    return convertQualifiedName(node.getName(), getParserPos(node));
  }

  @Override
  protected SqlNode visitUnnest(Unnest node, ParserVisitorContext context) {
    if (node.isWithOrdinality()) {
      return UNNEST_WITH_ORDINALITY.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
    }
    return UNNEST.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitLateral(Lateral node, ParserVisitorContext context) {
    return visitNode(node.getQuery(), context);
  }

  @Override
  protected SqlNode visitValues(Values node, ParserVisitorContext context) {
    return VALUES.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitRow(Row node, ParserVisitorContext context) {
    return ROW.createCall(getParserPos(node), getChildSqlNodeList(node, context).getList());
  }

  @Override
  protected SqlNode visitTableSubquery(TableSubquery node, ParserVisitorContext context) {
    return visitNode(node.getQuery(), context);
  }

  @Override
  protected SqlNode visitAliasedRelation(AliasedRelation node, ParserVisitorContext context) {
    List<SqlNode> operands = new ArrayList<>();
    operands.add(visitNode(node.getRelation(), context));
    operands.add(visitIdentifier(node.getAlias(), context));
    operands.addAll(getListSqlNode(node.getColumnNames(), context));
    return AS.createCall(getParserPos(node), operands);
  }

  @Override
  protected SqlNode visitSampledRelation(SampledRelation node, ParserVisitorContext context) {
    List<SqlNode> operands = new ArrayList<>();
    operands.add(visitNode(node.getRelation(), context));
    // percentage of presto is from 1 to 100, while calcite keeps (0, 1)
    SqlNode percentageNode = visitNode(node.getSamplePercentage(), context);
    float percentage = 0;
    if (percentageNode instanceof SqlNumericLiteral) {
      percentage = (float) (((SqlNumericLiteral) percentageNode).longValue(true) / 100.0);
    } else {
      throw new UnsupportedOperationException(
          format("TableSample percentage should be a numeric constant but as %s.", percentageNode.toString()));
    }
    operands.add(SqlLiteral.createSample(
        SqlSampleSpec.createTableSample(node.getType().equals(SampledRelation.Type.BERNOULLI), percentage),
        getParserPos(node)));
    return TABLESAMPLE.createCall(getParserPos(node), operands);
  }

  @Override
  protected SqlNode visitJoin(Join node, ParserVisitorContext context) {
    SqlNode left = visitNode(node.getLeft(), context);
    SqlNode right = visitNode(node.getRight(), context);
    SqlLiteral natural = createLiteralBoolean(false, ZERO);
    JoinConditionType conditionType = JoinConditionType.NONE;
    SqlNode condition = null;
    if (node.getCriteria().isPresent()) {
      JoinCriteria joinCriteria = node.getCriteria().get();
      natural = createLiteralBoolean(joinCriteria instanceof NaturalJoin, ZERO);
      if (joinCriteria instanceof JoinOn) {
        conditionType = JoinConditionType.ON;
        condition = visitNode(((JoinOn) joinCriteria).getExpression(), context);
      } else if (joinCriteria instanceof JoinUsing) {
        conditionType = JoinConditionType.USING;
        condition =
            createSqlNodeList(getListSqlNode(((JoinUsing) joinCriteria).getColumns(), context), getParserPos(node));
      }
    }
    JoinType joinType = JOIN_TYPE_MAP.get(node.getType());
    if (joinType == null) {
      throw getUnsupportedException(node);
    }
    return new SqlJoin(getParserPos(node), left, natural, joinType.symbol(ZERO), right, conditionType.symbol(ZERO),
        condition);
  }

  @Override
  protected SqlNode visitExists(ExistsPredicate node, ParserVisitorContext context) {
    return EXISTS.createCall(getParserPos(node), visitNode(node.getSubquery(), context));
  }

  private RelDataType getDecimalType(String type) {
    String value = type.substring(8, type.length() - 1);
    String[] nums = value.split(",");
    int precision = Integer.parseInt(nums[0]);
    int scale = nums.length > 1 ? Integer.parseInt(nums[1]) : 0;
    return sqlTypeFactory.createSqlType(SqlTypeName.DECIMAL, precision, scale);
  }

  private RelDataType getVarcharType(String type) {
    String precision = type.substring(8, type.length() - 1);
    return sqlTypeFactory.createSqlType(SqlTypeName.VARCHAR, Integer.parseInt(precision));
  }

  private RelDataType getCharType(String type) {
    String precision = type.substring(5, type.length() - 1);
    return sqlTypeFactory.createSqlType(SqlTypeName.CHAR, Integer.parseInt(precision));
  }

  private RelDataType convertType(String type) {
    if (type.toUpperCase().startsWith("DECIMAL(")) {
      return getDecimalType(type);
    } else if (type.toUpperCase().startsWith("VARCHAR(")) {
      return getVarcharType(type);
    } else if (type.toUpperCase().startsWith("CHAR(")) {
      return getCharType(type);
    }
    return sqlTypeFactory.createSqlType(SqlTypeName.valueOf(type.toUpperCase()));
  }

  @Override
  protected SqlNode visitCast(Cast node, ParserVisitorContext context) {
    SqlDataTypeSpec spec = SqlTypeUtil.convertTypeToSpec(convertType(node.getType()));
    return CAST.createCall(getParserPos(node), visitNode(node.getExpression(), context), spec);
  }

  @Override
  protected SqlNode visitFieldReference(FieldReference node, ParserVisitorContext context) {
    return createLiteralNumber(node.getFieldIndex(), getParserPos(node));
  }

  @Override
  protected SqlNode visitWindow(Window node, ParserVisitorContext context) {
    SqlParserPos pos = getParserPos(node);
    SqlNodeList partitionList = createSqlNodeList(getListSqlNode(node.getPartitionBy(), context), pos);
    SqlNodeList orderList = createSqlNodeList(getListSqlNode(
        node.getOrderBy().isPresent() ? node.getOrderBy().get().getSortItems() : Collections.emptyList(), context));
    boolean isRows = false;
    SqlNode lowerBound = null;
    SqlNode upperBound = null;
    if (node.getFrame().isPresent()) {
      WindowFrame frame = node.getFrame().get();
      isRows = frame.getType().equals(WindowFrame.Type.ROWS);
      lowerBound = visitFrameBound(frame.getStart(), context);
      upperBound = frame.getEnd().isPresent() ? visitFrameBound(frame.getEnd().get(), context) : null;
    }
    return SqlWindow.create(null, null, partitionList, orderList, createLiteralBoolean(isRows, ZERO), lowerBound,
        upperBound, null, pos);
  }

  @Override
  protected SqlNode visitWindowFrame(WindowFrame node, ParserVisitorContext context) {
    throw new UnsupportedOperationException(
        format("Window frame %s should be processed in the window visitor.", node.toString()));
  }

  @Override
  protected SqlNode visitFrameBound(FrameBound node, ParserVisitorContext context) {
    if (node.getValue().isPresent()) {
      return getUnresolvedFunction(node.getType().name(),
          Collections.singletonList(visitNode(node.getValue().get(), context)));
    }
    return SqlLiteral.createSymbol(node.getType(), ZERO);
  }

  @Override
  protected SqlNode visitCallArgument(CallArgument node, ParserVisitorContext context) {
    return visitNode(node.getValue(), context);
  }

  @Override
  protected SqlNode visitLikeClause(LikeClause node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateSchema(CreateSchema node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropSchema(DropSchema node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRenameSchema(RenameSchema node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateTable(CreateTable node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateType(CreateType node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateTableAsSelect(CreateTableAsSelect node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitProperty(Property node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropTable(DropTable node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRenameTable(RenameTable node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRenameColumn(RenameColumn node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropColumn(DropColumn node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitAddColumn(AddColumn node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitAnalyze(Analyze node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateView(CreateView node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropView(DropView node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateMaterializedView(CreateMaterializedView node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropMaterializedView(DropMaterializedView node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRefreshMaterializedView(RefreshMaterializedView node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateFunction(CreateFunction node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitAlterFunction(AlterFunction node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropFunction(CreateFunction node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropFunction(DropFunction node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitInsert(Insert node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCall(Call node, ParserVisitorContext context) {
    return PROCEDURE_CALL.createCall(getParserPos(node),
        getUnresolvedFunction(node.getName().toString(), getListSqlNode(node.getArguments(), context)));
  }

  @Override
  protected SqlNode visitDelete(Delete node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitStartTransaction(StartTransaction node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCreateRole(CreateRole node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDropRole(DropRole node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitGrantRoles(GrantRoles node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRevokeRoles(RevokeRoles node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitSetRole(SetRole node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitGrant(Grant node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRevoke(Revoke node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowGrants(ShowGrants node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowRoles(ShowRoles node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowRoleGrants(ShowRoleGrants node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitTransactionMode(TransactionMode node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitIsolationLevel(Isolation node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitTransactionAccessMode(TransactionAccessMode node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitCommit(Commit node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitRollback(Rollback node, ParserVisitorContext context) {
    throw getDDLException(node);
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
    return CUBE.createCall(getParserPos(node), getListSqlNode(node.getExpressions(), context));
  }

  @Override
  protected SqlNode visitGroupingSets(GroupingSets node, ParserVisitorContext context) {
    List<List<Expression>> sets = node.getSets();
    List<SqlNode> operands = new ArrayList<>();
    for (List<Expression> expressions : sets) {
      if (expressions.isEmpty()) {
        operands.add(createSqlNodeList(Collections.emptyList()));
      } else if (expressions.size() > 1) {
        operands.add(ROW.createCall(ZERO, createSqlNodeList(getListSqlNode(expressions, context))));
      } else {
        operands.add(visitNode(expressions.get(0), context));
      }
    }
    return GROUPING_SETS.createCall(getParserPos(node), operands);
  }

  @Override
  protected SqlNode visitRollup(Rollup node, ParserVisitorContext context) {
    return ROLLUP.createCall(getParserPos(node), getListSqlNode(node.getExpressions(), context));
  }

  @Override
  protected SqlNode visitSimpleGroupBy(SimpleGroupBy node, ParserVisitorContext context) {
    Preconditions.checkArgument(node.getExpressions().size() == 1,
        "SimpleGroupBy should have only one element in the expression.");
    return visitNode(node.getExpressions().get(0), context);
  }

  @Override
  protected SqlNode visitQuantifiedComparisonExpression(QuantifiedComparisonExpression node,
      ParserVisitorContext context) {
    SqlNode left = visitNode(node.getValue(), context);
    SqlNode right = visitNode(node.getSubquery(), context);
    SqlParserPos pos = getParserPos(node);
    if (node.getQuantifier().equals(QuantifiedComparisonExpression.Quantifier.ALL)) {
      return ALL_COMPARISON_OPERATOR.get(node.getOperator()).createCall(pos, left, right);
    } else {
      return SOME_COMPARISON_OPERATOR.get(node.getOperator()).createCall(pos, left, right);
    }
  }

  @Override
  protected SqlNode visitLambdaArgumentDeclaration(LambdaArgumentDeclaration node, ParserVisitorContext context) {
    throw getLambdaException(node);
  }

  @Override
  protected SqlNode visitBindExpression(BindExpression node, ParserVisitorContext context) {
    throw getLambdaException(node);
  }

  @Override
  protected SqlNode visitGroupingOperation(GroupingOperation node, ParserVisitorContext context) {
    return GROUPING.createCall(getParserPos(node), getListSqlNode(node.getGroupingColumns(), context));
  }

  @Override
  protected SqlNode visitCurrentUser(CurrentUser node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitReturn(Return node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitExternalBodyReference(ExternalBodyReference node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitPrepare(Prepare node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDeallocate(Deallocate node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitExecute(Execute node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDescribeOutput(DescribeOutput node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitDescribeInput(DescribeInput node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitExplain(Explain node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowTables(ShowTables node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowSchemas(ShowSchemas node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowCatalogs(ShowCatalogs node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowColumns(ShowColumns node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowStats(ShowStats node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowCreate(ShowCreate node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowCreateFunction(ShowCreateFunction node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowFunctions(ShowFunctions node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitUse(Use node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitShowSession(ShowSession node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitSetSession(SetSession node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitResetSession(ResetSession node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitExplainOption(ExplainOption node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitEnumLiteral(EnumLiteral node, ParserVisitorContext context) {
    throw new UnsupportedOperationException(format("Enum literal %s is not supported.", node.toString()));
  }

  @Override
  protected SqlNode visitTryExpression(TryExpression node, ParserVisitorContext context) {
    throw new UnsupportedOperationException(format("Try expression %s is not supported.", node.toString()));
  }

  @Override
  protected SqlNode visitColumnDefinition(ColumnDefinition node, ParserVisitorContext context) {
    throw getDDLException(node);
  }

  @Override
  protected SqlNode visitSymbolReference(SymbolReference node, ParserVisitorContext context) {
    throw new UnsupportedOperationException(format("Symbol reference %s is not supported.", node.toString()));
  }
}

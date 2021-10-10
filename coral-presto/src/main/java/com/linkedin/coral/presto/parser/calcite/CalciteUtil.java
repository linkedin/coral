/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.parser.calcite;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlAbstractParserImpl;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;
import static org.apache.calcite.sql.type.SqlTypeName.*;
import static org.apache.calcite.sql.type.SqlTypeName.TIMESTAMP;
import static org.apache.calcite.util.Litmus.IGNORE;


public class CalciteUtil {
  private final static Logger LOG = LoggerFactory.getLogger(CalciteUtil.class);

  public static SqlParser.Config SQL_PARSER_CONFIG =
      SqlParser.configBuilder().setCaseSensitive(false).setUnquotedCasing(Casing.UNCHANGED)
          .setQuotedCasing(Casing.UNCHANGED).setConformance(SqlConformanceEnum.ORACLE_10).build();
  public static final SqlDataTypeSpec TYPE_SPEC_BIGINT =
      new SqlDataTypeSpec(new SqlBasicTypeNameSpec(BIGINT, -1, -1, ZERO), ZERO);
  public static final SqlDataTypeSpec TYPE_SPEC_VARCHAR =
      new SqlDataTypeSpec(new SqlBasicTypeNameSpec(VARCHAR, -1, -1, ZERO), ZERO);
  public static final SqlDataTypeSpec TYPE_SPEC_DATE =
      new SqlDataTypeSpec(new SqlBasicTypeNameSpec(DATE, -1, -1, ZERO), ZERO);
  public static final SqlDataTypeSpec TYPE_SPEC_TIMESTAMP =
      new SqlDataTypeSpec(new SqlBasicTypeNameSpec(TIMESTAMP, -1, -1, ZERO), ZERO);
  public static final Set<String> CALCITE_LOWER_CASE_RESERVED_WORDS =
      SqlAbstractParserImpl.getSql92ReservedWords().stream().map(String::toLowerCase).collect(Collectors.toSet());

  public static SqlNode parseStatement(String query) throws SqlParseException {
    String quotedQuery = quoteReservedWords(query);
    try {
      String expression = "(" + quotedQuery + ")";
      SqlParser sqlParser = SqlParser.create(expression, SQL_PARSER_CONFIG);
      return sqlParser.parseExpression();
    } catch (SqlParseException e) {
      return parseQuery(quotedQuery);
    }
  }

  private static SqlNode parseQuery(String query) throws SqlParseException {
    try {
      SqlParser sqlParser = SqlParser.create(query);
      return sqlParser.parseQuery();
    } catch (SqlParseException e) {
      LOG.error("Failed to parse query: {}", query);
      throw e;
    }
  }

  public static boolean isCaseWhenOverAgg(SqlCall sqlCall) {
    return sqlCall.getKind().equals(SqlKind.CASE);
  }

  public static SqlNode getRoundedNumber(SqlNode value, SqlNode interval) {
    return createMultiply(createDiv(value, interval), interval);
  }

  public static SqlOrderBy addLimit(SqlSelect sqlSelect, int limit) {
    return new SqlOrderBy(ZERO, sqlSelect, SqlNodeList.EMPTY, null, createLiteralNumber(limit, SqlParserPos.ZERO));
  }

  public static SqlCall getCastToString(SqlNode sqlNode) {
    return getCast(sqlNode, TYPE_SPEC_VARCHAR);
  }

  public static SqlCall getCastToDate(SqlNode sqlNode) {
    return getCast(sqlNode, TYPE_SPEC_DATE);
  }

  public static SqlCall getCastToTimestamp(SqlNode sqlNode) {
    return getCast(sqlNode, TYPE_SPEC_TIMESTAMP);
  }

  public static SqlCall getCastToBigInt(SqlNode sqlNode) {
    return getCast(sqlNode, TYPE_SPEC_BIGINT);
  }

  public static SqlCall getCast(SqlNode sqlNode, SqlDataTypeSpec sqlDataTypeSpec) {
    return SqlStdOperatorTable.CAST.createCall(ZERO, sqlNode, sqlDataTypeSpec);
  }

  public static void unparseSqlJoin(SqlWriter writer, SqlJoin call, int leftPrec, int rightPrec) {
    SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.JOIN);
    call.getOperator().unparse(writer, call, leftPrec, rightPrec);
    writer.endList(frame);
  }

  public static void unparseSqlOrderBy(SqlWriter writer, SqlOrderBy call, int leftPrec, int rightPrec,
      String limitKeyword) {
    SqlOrderBy orderBy = call;

    // Only support ORDER BY [columns] LIMIT a_number
    Preconditions.checkState(orderBy.offset == null);
    Preconditions.checkState(orderBy.fetch == null || orderBy.fetch.getKind() == SqlKind.LITERAL);

    SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.ORDER_BY);
    orderBy.query.unparse(writer, leftPrec, rightPrec);
    if (orderBy.orderList != SqlNodeList.EMPTY) {
      writer.sep("ORDER BY");
      SqlWriter.Frame orderByFrame = writer.startList(SqlWriter.FrameTypeEnum.ORDER_BY_LIST);
      orderBy.orderList.unparse(writer, leftPrec, rightPrec);
      writer.endList(orderByFrame);
    }

    if (orderBy.fetch != null) {
      SqlWriter.Frame limitFrame = writer.startList(SqlWriter.FrameTypeEnum.FETCH);
      writer.newlineAndIndent();
      writer.keyword(limitKeyword);
      orderBy.fetch.unparse(writer, -1, -1);
      writer.endList(limitFrame);
    }

    writer.endList(frame);
  }

  @Nullable
  public static SqlNode removeAlias(SqlNode node) {
    if (node == null) {
      return null;
    }
    return node.getKind().equals(SqlKind.AS) ? ((SqlCall) node).operand(0) : node;
  }

  public static SqlNode extractAlias(SqlNode node) {
    return node.getKind().equals(SqlKind.AS) ? ((SqlCall) node).operand(1) : node;
  }

  public static SqlNode createAs(SqlNode sqlNode, SqlNode alias) {
    return createCall(SqlStdOperatorTable.AS, ImmutableList.of(sqlNode, alias));
  }

  public static SqlNode createDiv(SqlNode numerator, SqlNode denominator) {
    return createCall(SqlStdOperatorTable.DIVIDE, ImmutableList.of(numerator, denominator));
  }

  public static SqlNode createAdd(SqlNode op, SqlNode op1) {
    return createCall(SqlStdOperatorTable.PLUS, ImmutableList.of(op, op1));
  }

  public static SqlNode createMultiply(SqlNode leftNumber, SqlNode rightNumber) {
    return createCall(SqlStdOperatorTable.MULTIPLY, ImmutableList.of(leftNumber, rightNumber));
  }

  public static SqlNode createLiteralInterval(int sign, long value, SqlIntervalQualifier sqlIntervalQualifier,
      SqlParserPos pos) {
    return SqlLiteral.createInterval(sign, String.valueOf(value), sqlIntervalQualifier, pos);
  }

  public static SqlNode createLiteralNumber(long value, SqlParserPos pos) {
    return SqlLiteral.createExactNumeric(String.valueOf(value), pos);
  }

  public static String convertScientificDouble(double value) {
    String valueString = String.valueOf(value);
    if (valueString.toUpperCase().contains("E")) {
      int index = valueString.indexOf("E-") > 0 ? valueString.indexOf("E-") + 2 : valueString.indexOf("E") + 1;
      Integer scale = Integer.valueOf(valueString.substring(index));
      valueString = format("%." + format("%d", scale) + "f", value);
    }
    return valueString;
  }

  public static SqlNode createLiteralNumber(double value, SqlParserPos pos) {
    return SqlLiteral.createExactNumeric(convertScientificDouble(value), pos);
  }

  public static SqlLiteral createLiteralBoolean(boolean value, SqlParserPos pos) {
    return SqlLiteral.createBoolean(value, pos);
  }

  public static SqlNode createStringLiteral(String value, SqlParserPos pos) {
    return SqlLiteral.createCharString(value, pos);
  }

  public static SqlNode createBinaryLiteral(byte[] value, SqlParserPos pos) {
    return SqlLiteral.createBinaryString(value, pos);
  }

  public static SqlNode createLiteralNull(SqlParserPos pos) {
    return SqlLiteral.createNull(pos);
  }

  public static SqlCall createCall(SqlOperator sqlOperator, List<SqlNode> sqlNodeList) {
    return sqlOperator.createCall(createSqlNodeList(sqlNodeList));
  }

  public static SqlCall createCall(SqlOperator sqlOperator, List<SqlNode> sqlNodeList, SqlParserPos pos) {
    return sqlOperator.createCall(new SqlNodeList(sqlNodeList, pos));
  }

  public static SqlCall createCall(SqlOperator sqlOperator, List<SqlNode> sqlNodeList, SqlLiteral functionQuantifier) {
    return sqlOperator.createCall(functionQuantifier, ZERO, sqlNodeList.toArray(new SqlNode[0]));
  }

  public static SqlNode createAndCall(Iterable<SqlNode> sqlNodeList) {
    Iterator<SqlNode> iter = sqlNodeList.iterator();
    SqlNode call = iter.next();
    while (iter.hasNext()) {
      call = createCall(SqlStdOperatorTable.AND, ImmutableList.of(call, iter.next()));
    }
    return call;
  }

  @Nullable
  public static SqlNode createAndCall(@Nullable SqlNode left, @Nullable SqlNode right) {
    if (right == null) {
      return left;
    }

    if (left == null) {
      return right;
    }

    return createCall(SqlStdOperatorTable.AND, ImmutableList.of(left, right));
  }

  @Nullable
  public static SqlNode createOrCall(@Nullable SqlNode left, @Nullable SqlNode right) {
    if (right == null) {
      return left;
    }

    if (left == null) {
      return right;
    }

    return createCall(SqlStdOperatorTable.OR, ImmutableList.of(left, right));
  }

  public static SqlCall createEquals(SqlNode identifier, SqlNode value) {
    return createCall(SqlStdOperatorTable.EQUALS, ImmutableList.of(identifier, value));
  }

  public static SqlCall createGreaterThan(SqlNode identifier, SqlNode value) {
    return createGreaterThan(identifier, value, false);
  }

  public static SqlCall createGreaterThanOrEqual(SqlNode identifier, SqlNode value) {
    return createGreaterThan(identifier, value, true);
  }

  public static SqlCall createGreaterThan(SqlNode identifier, SqlNode value, boolean orEqualTo) {
    SqlBinaryOperator operator =
        orEqualTo ? SqlStdOperatorTable.GREATER_THAN_OR_EQUAL : SqlStdOperatorTable.GREATER_THAN;
    return createCall(operator, ImmutableList.of(identifier, value));
  }

  public static SqlCall createLessThan(SqlNode identifier, SqlNode value) {
    return createLessThan(identifier, value, false);
  }

  public static SqlCall createLessThanOrEqual(SqlNode identifier, SqlNode value) {
    return createLessThan(identifier, value, true);
  }

  public static SqlCall createLessThan(SqlNode identifier, SqlNode value, boolean orEqualTo) {
    SqlBinaryOperator operator = orEqualTo ? SqlStdOperatorTable.LESS_THAN_OR_EQUAL : SqlStdOperatorTable.LESS_THAN;
    return createCall(operator, ImmutableList.of(identifier, value));
  }

  public static boolean sqlNodeEqual(SqlNode node1, SqlNode node2) {
    return Objects.equals(node1, node2) || (node1 != null && node2 != null && node1.equalsDeep(node2, IGNORE));
  }

  public static <T extends SqlNode> Set<T> consolidateNodeSet(Set<T> nodes) {
    Map<String, T> stringToNode = new LinkedHashMap<>();
    nodes.forEach(n -> stringToNode.put(n.toString(), n));
    return new LinkedHashSet<>(stringToNode.values());
  }

  public static String getIdentifierLastName(SqlIdentifier sqlIdentifier) {
    return sqlIdentifier.names.get(sqlIdentifier.names.size() - 1);
  }

  public static String getIdentifierFirstName(SqlIdentifier sqlIdentifier) {
    return sqlIdentifier.names.get(0);
  }

  public static String[] splitIdentifierString(String identifierString) {
    return identifierString.split("\\.");
  }

  public static boolean isPhysicalTable(SqlIdentifier sqlIdentifier, SchemaPlus schemaPlus) {
    if (sqlIdentifier.isSimple()) {
      return schemaPlus.getTable(sqlIdentifier.getSimple()) != null;
    }
    // recursive visit the sub-schema until the identifier as simple
    SchemaPlus subSchema = schemaPlus.getSubSchema(getIdentifierFirstName(sqlIdentifier));
    if (subSchema != null) {
      return isPhysicalTable(createSqlIdentifier(SqlParserPos.ZERO,
          sqlIdentifier.names.subList(1, sqlIdentifier.names.size()).toArray(new String[0])), subSchema);
    }
    return false;
  }

  public static Table registerSchema(SchemaPlus schemaPlus, Table table, List<String> path) {
    for (int i = 0; i < path.size() - 1; i++) {
      String key = path.get(i);
      if (schemaPlus.getSubSchema(key) == null) {
        schemaPlus.add(key, new AbstractSchema());
      }
      schemaPlus = schemaPlus.getSubSchema(key);
    }

    String tableName = path.get(path.size() - 1);
    Table oldTable = schemaPlus.getTable(tableName);
    schemaPlus.add(tableName, table);
    return oldTable;
  }

  public static SqlIdentifier createSqlIdentifier(SqlParserPos pos, String... path) {
    return new SqlIdentifier(Arrays.asList(path), ZERO);
  }

  public static SqlIdentifier createStarIdentifier(SqlParserPos pos) {
    return createSqlIdentifier(pos, "");
  }

  public static SqlNodeList createSqlNodeList(Collection<SqlNode> sqlNodeList) {
    return new SqlNodeList(sqlNodeList, ZERO);
  }

  public static SqlNodeList createSqlNodeList(Collection<SqlNode> sqlNodeList, SqlParserPos pos) {
    return new SqlNodeList(sqlNodeList, pos);
  }

  public static String quoteReservedWords(String s) {
    if (s == null) {
      return s;
    }

    s = s.replaceAll("(^|[^\"]\\b)time(\\b[^']|$)", "$1\"time\"$2");
    s = s.replaceAll("(^|\\W)rank($|\\W)", "$1\"rank\"$2");
    return s;
  }

  public static String getViewName(SqlSelect sqlSelect) {
    if (sqlSelect.getFrom().getKind().equals(SqlKind.JOIN)) {
      SqlJoin join = (SqlJoin) sqlSelect.getFrom();
      while (join.getLeft().getKind().equals(SqlKind.JOIN)) {
        join = (SqlJoin) join.getLeft();
      }
      return join.getLeft().toString();
    }
    return sqlSelect.getFrom().toString();
  }

  public static <I, R> List<R> filterAndCastByType(Collection<I> collection, Class<R> rClass) {
    if (collection == null) {
      return Collections.emptyList();
    }

    return collection.stream().filter(rClass::isInstance).map(rClass::cast).collect(Collectors.toList());
  }

  public static List<String> getOriginalColumnName(List<String> names, Table table) {
    RelDataTypeFactory relDataTypeFactory = new JavaTypeFactoryImpl();
    RelDataType relDataType = table.getRowType(relDataTypeFactory);
    List<String> originalNames = new ArrayList<>(names.size());
    for (String name : names) {
      // Remove quote
      name = name.replaceAll("^\"|\"$", "");

      // Handle star
      if (StringUtils.isEmpty(name)) {
        originalNames.add(name);
        Preconditions.checkState(originalNames.size() == names.size(),
            "name list contains star in the middle: " + names);
        break;
      }

      RelDataTypeField relDataTypeField = relDataType.getField(name, false, true);
      Preconditions.checkState(relDataTypeField != null, "Could not find path " + names);
      originalNames.add(relDataTypeField.getName());
      relDataType = relDataTypeField.getType();
    }
    return originalNames;
  }

  public static String getOriginalColumnName(String columnName, Table table) {
    String[] names = columnName.split("\\.");
    List<String> originalNames = getOriginalColumnName(Arrays.asList(names), table);
    StringJoiner sj = new StringJoiner(".");
    originalNames.forEach(sj::add);
    return sj.toString();
  }

  public static SqlNode createLeftJoinCall(SqlNode from, String namespace, String tableName, SqlNode joinOn) {
    return SqlJoin.OPERATOR.createCall(ZERO, from, SqlLiteral.createBoolean(false, ZERO),
        SqlLiteral.createSymbol(JoinType.LEFT, ZERO), createSqlIdentifier(SqlParserPos.ZERO, namespace, tableName),
        SqlLiteral.createSymbol(JoinConditionType.ON, ZERO), joinOn);
  }

  public static SqlNode createLeftJoinCall(SqlNode from, SqlNode joinSource, SqlNode joinOn) {
    return SqlJoin.OPERATOR.createCall(ZERO, from, SqlLiteral.createBoolean(false, ZERO),
        SqlLiteral.createSymbol(JoinType.LEFT, ZERO), joinSource, SqlLiteral.createSymbol(JoinConditionType.ON, ZERO),
        joinOn);
  }

  /**
   * Create an UNNEST call.
   *
   * @return CROSS JOIN UNNEST(operand1, operand2, ...) AS table(column1, column2, ...)
   */
  public static SqlNode createUnnestCall(SqlNode from, List<SqlNode> unnestOperands, String table,
      List<String> columns) {
    return SqlJoin.OPERATOR.createCall(ZERO, from, SqlLiteral.createBoolean(false, ZERO),
        SqlLiteral.createSymbol(JoinType.CROSS, ZERO),
        createCall(SqlStdOperatorTable.AS,
            Stream
                .concat(
                    Stream.of(createCall(SqlStdOperatorTable.UNNEST, unnestOperands),
                        createSqlIdentifier(SqlParserPos.ZERO, table)),
                    columns.stream().map(path -> createSqlIdentifier(SqlParserPos.ZERO, path)))
                .collect(Collectors.toList())),
        SqlLiteral.createSymbol(JoinConditionType.NONE, ZERO), null);
  }

  public static boolean isSingleNonAsSelectItem(SqlNodeList selectList) {
    return selectList.size() == 1 && selectList.get(0).getKind() != SqlKind.AS;
  }
}

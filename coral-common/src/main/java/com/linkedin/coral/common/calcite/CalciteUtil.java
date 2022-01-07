/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.calcite;

import java.util.*;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


public class CalciteUtil {
  private final static Logger LOG = LoggerFactory.getLogger(CalciteUtil.class);

  public static final SqlParser.Config SQL_PARSER_CONFIG =
      SqlParser.configBuilder().setCaseSensitive(false).setUnquotedCasing(Casing.UNCHANGED)
          .setQuotedCasing(Casing.UNCHANGED).setConformance(SqlConformanceEnum.ORACLE_10).build();

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

  public static String[] splitIdentifierString(String identifierString) {
    return identifierString.split("\\.");
  }

  public static SqlIdentifier createSqlIdentifier(SqlParserPos pos, String... path) {
    return new SqlIdentifier(Arrays.asList(path), pos);
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
}

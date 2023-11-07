/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.dialect;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlUnnestOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlMultisetValueConstructor;
import org.apache.calcite.sql.fun.SqlSubstringFunction;

import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;


/**
 * This class represents the Spark SQL Dialect.
 *
 * It overrides Hive Dialect with following behavior
 *    - Disables character set names
 *    - converts ARRAY[] to ARRAY() or MAP[] to MAP()
 *    - converts UNNEST to EXPLODE
 *
 * This is the Final Step in translation pipeline.
 * There should not be any AST manipulation logic here, just Spark translation logic.
 */
public class SparkSqlDialect extends SqlDialect {

  public static final SqlDialect.Context DEFAULT_CONTEXT = SqlDialect.EMPTY_CONTEXT
      .withDatabaseProduct(DatabaseProduct.SPARK).withLiteralQuoteString("'").withLiteralEscapedQuoteString("\\'")
      .withNullCollation(NullCollation.LOW).withUnquotedCasing(Casing.UNCHANGED).withQuotedCasing(Casing.UNCHANGED)
      .withCaseSensitive(false).withIdentifierQuoteString("`");

  public static final SparkSqlDialect INSTANCE = new SparkSqlDialect(DEFAULT_CONTEXT);

  private static final List<String> RESERVED_KEYWORDS = ImmutableList.copyOf(
      Arrays.asList("ALL", "ALTER", "AND", "ARRAY", "AS", "AUTHORIZATION", "BETWEEN", "BIGINT", "BINARY", "BOOLEAN",
          "BOTH", "BY", "CASE", "CAST", "CHAR", "COLUMN", "CONF", "CREATE", "CROSS", "CUBE", "CURRENT", "CURRENT_DATE",
          "CURRENT_TIMESTAMP", "CURSOR", "DATABASE", "DATE", "DECIMAL", "DELETE", "DESCRIBE", "DISTINCT", "DOUBLE",
          "DROP", "ELSE", "END", "EXCHANGE", "EXISTS", "EXTENDED", "EXTERNAL", "FALSE", "FETCH", "FLOAT", "FOLLOWING",
          "FOR", "FROM", "FULL", "FUNCTION", "GRANT", "GROUP", "GROUPING", "HAVING", "IF", "IMPORT", "IN", "INNER",
          "INSERT", "INT", "INTERSECT", "INTERVAL", "INTO", "IS", "JOIN", "LATERAL", "LEFT", "LESS", "LIKE", "LOCAL",
          "MACRO", "MAP", "MORE", "NONE", "NOT", "NULL", "OF", "ON", "OR", "ORDER", "OUT", "OUTER", "OVER",
          "PARTIALSCAN", "PARTITION", "PERCENT", "PRECEDING", "PRESERVE", "PROCEDURE", "RANGE", "READS", "REDUCE",
          "REVOKE", "RIGHT", "ROLLUP", "ROW", "ROWS", "SELECT", "SET", "SMALLINT", "TABLE", "TABLESAMPLE", "THEN",
          "TIMESTAMP", "TO", "TRANSFORM", "TRIGGER", "TRUE", "TRUNCATE", "UNBOUNDED", "UNION", "UNIQUEJOIN", "UPDATE",
          "USER", "USING", "UTC_TMESTAMP", "VALUES", "VARCHAR", "WHEN", "WHERE", "WINDOW", "WITH"));

  private SparkSqlDialect(Context context) {
    super(context);
  }

  /**
   * Overrides unparse call:
   *      All SqlCall translations will go through here.
   *
   * We specifically target two
   *    - UNNEST
   *    - MAP or ARRAY
   * */
  @Override
  public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    if (call.getOperator() instanceof SqlMultisetValueConstructor) {
      unparseMapOrArray(writer, call);
    } else if (call.getOperator() instanceof SqlUnnestOperator) {
      unparseUnnest(writer, call);
    } else if (call.getOperator() instanceof SqlSubstringFunction) {
      unparseSubstring(writer, call);
    } else {
      super.unparseCall(writer, call, leftPrec, rightPrec);
    }
  }

  /**
   *  Default SQL operator for SUBSTRING(a, 1, 5)
   *    unparses to "SUBSTRING(a FROM 1 FOR 5)"
   *
   *  SparkSQL doesn't support this translation
   *  so the behaviour is overridden here with
   *    "SUBSTRING(a, 1, 5)"
   *
   * */
  private void unparseSubstring(SqlWriter writer, SqlCall call) {
    writer.keyword("SUBSTRING");
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "(", ")");
    for (SqlNode operand : call.getOperandList()) {
      writer.sep(",");
      operand.unparse(writer, 0, 0);
    }
    writer.endList(frame);
  }

  /**
   *  Converts UNNEST(...) to EXPLODE(...)
   *
   *  Code referred from SqlFunctionalOperator.java
   * */
  private void unparseUnnest(SqlWriter writer, SqlCall call) {
    if (call.getOperator() instanceof CoralSqlUnnestOperator
        && ((CoralSqlUnnestOperator) call.getOperator()).withOrdinality) {
      writer.keyword("POSEXPLODE");
    } else {
      writer.keyword("EXPLODE");
    }
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "(", ")");
    for (SqlNode operand : call.getOperandList()) {
      writer.sep(",");
      operand.unparse(writer, 0, 0);
    }
    writer.endList(frame);
  }

  /**
   *  Converts ARRAY[] to ARRAY()
   *  Converts MAP[] to MAP()
   * */
  private void unparseMapOrArray(SqlWriter writer, SqlCall call) {
    writer.keyword(call.getOperator().getName()); // "MULTISET" or "ARRAY"
    final SqlWriter.Frame frame = writer.startList("(", ")");
    for (SqlNode operand : call.getOperandList()) {
      writer.sep(",");
      operand.unparse(writer, 0, 0);
    }
    writer.endList(frame);
  }

  @Override
  protected boolean allowsAs() {
    return false;
  }

  /**
   *  Disables character set.
   *
   *  Otherwise data types are accompanied with character sets
   *
   *  For ex:
   *  VARCHAR(30) CHARACTER SET `ISO-8859-1`
   *
   * */
  @Override
  public boolean supportsCharSet() {
    return false;
  }

  @Override
  protected boolean identifierNeedsQuote(String val) {
    return RESERVED_KEYWORDS.contains(val.toUpperCase(Locale.ROOT));
  }

  @Override
  public void unparseOffsetFetch(SqlWriter writer, SqlNode offset, SqlNode fetch) {
    unparseFetchUsingLimit(writer, offset, fetch);
  }

}

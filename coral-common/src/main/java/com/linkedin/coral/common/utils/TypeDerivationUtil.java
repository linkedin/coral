/**
 * Copyright 2023-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlWith;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidator;


/**
 * This is a utility class which helps derive the RelDataType of a given SqlNode.
 */
public class TypeDerivationUtil {
  private final SqlValidator sqlValidator;
  private final List<SqlSelect> topSelectNodes = new ArrayList<>();
  // For every SELECT collected in topSelectNodes, the WITH lists that are in scope for it, outermost first.
  // A dummy SELECT built from such a SELECT has to be wrapped in the same WITH clauses, otherwise the
  // validator cannot resolve references to CTE names (e.g. `FROM t1` where t1 is defined by `WITH t1 AS (...)`).
  private final Map<SqlSelect, List<SqlNodeList>> enclosingWithLists = new IdentityHashMap<>();

  public TypeDerivationUtil(SqlValidator sqlValidator, SqlNode topSqlNode) {
    this.sqlValidator = sqlValidator;
    topSqlNode.accept(new SqlNodePreprocessorForTypeDerivation());
  }

  /**
   * This method derives the RelDataType for a given SqlNode.
   * It constructs a dummy SqlSelect object with the passed SqlNode as a field and iterates over each of the top-level SELECT statements.
   * If the SqlNode passes validation with the SqlValidator instance, then the method returns the validated node's RelDataType.
   * If the method is unable to derive the RelDataType for the given SqlNode, it throws a RuntimeException.
   *
   * The method uses a list named 'topSelectNodes', to store all the top-level SqlSelects from the original "topSqlNode".
   * It does this to handle data type derivation in SQL statements that use a UNION ALL operator.
   * Since a UNION ALL operator cannot be represented as a unified SQL Select statement, it is necessary to use a list
   * to store both SELECT statements separately. Example SQL:
   *
   * SELECT *
   * FROM `test`.`tablea`
   * UNION ALL
   * SELECT *
   * FROM `test`.`tableb`
   * WHERE `concat`(CURRENT_DATE, `tableb`.`a`) = 'some string'
   *
   * If a top-level SELECT is defined inside a WITH clause (or reads from a CTE), the dummy SqlSelect is wrapped
   * in the WITH clauses that enclose it in the original query, so that CTE names in its FROM clause still resolve.
   * Example SQL:
   *
   * WITH t1 AS (SELECT b AS str FROM test.tableOne)
   * SELECT split(str, ':')[0] FROM t1
   *
   * Here the type of `split(str, ':')` is derived by validating
   * `WITH t1 AS (SELECT b AS str FROM test.tableOne) SELECT split(str, ':') FROM t1`.
   *
   * @param sqlNode The SqlNode for which the RelDataType needs to be derived.
   * @return The RelDataType derived for the given SqlNode.
   * @throws RuntimeException if the RelDataType cannot be derived for the given SqlNode.
   */
  public RelDataType getRelDataType(SqlNode sqlNode) {
    if (sqlValidator == null) {
      throw new RuntimeException("SqlValidator does not exist to derive the RelDataType for SqlNode: " + sqlNode);
    }

    RelDataType cachedDataType = sqlValidator.getValidatedNodeTypeIfKnown(sqlNode);
    if (cachedDataType != null) {
      return cachedDataType;
    }

    // Retained so the terminal RuntimeException can report why derivation failed. Candidate SELECTs are
    // tried in turn and individual failures are expected, so they are not logged.
    Throwable lastFailure = null;

    for (SqlSelect topSqlSelectNode : topSelectNodes) {
      final SqlSelect dummySqlSelect = new SqlSelect(topSqlSelectNode.getParserPosition(), null,
          SqlNodeList.of(sqlNode), topSqlSelectNode.getFrom(), topSqlSelectNode.getWhere(), topSqlSelectNode.getGroup(),
          topSqlSelectNode.getHaving(), topSqlSelectNode.getWindowList(), topSqlSelectNode.getOrderList(),
          topSqlSelectNode.getOffset(), topSqlSelectNode.getFetch());

      // Try in the CTE scope of topSqlSelectNode first, then without any WITH clause (the previous behavior)
      for (SqlNode dummyQuery : candidateQueries(topSqlSelectNode, dummySqlSelect)) {
        try {
          sqlValidator.validate(dummyQuery);
          return sqlValidator.getValidatedNodeType(dummyQuery).getFieldList().get(0).getType();
        } catch (Throwable t) {
          lastFailure = t;
        }
      }
    }

    // Additional attempt to derive RelDataType of the input sqlNode by validating the topSelectNode.
    // This is particularly useful when the input is defined as an alias in topSelectNode's selectList.
    // For example, when the topSelectNode is: `SELECT a AS tmp FROM foo WHERE tmp > 5`.
    // Previous attempts would try validating dummySqlNode: `SELECT tmp FROM foo WHERE tmp > 5`, which would fail RelDataType derivation.
    final SqlSelect aliasDummySqlSelect = new SqlSelect(topSelectNodes.get(0).getParserPosition(), null,
        SqlNodeList.of(sqlNode), topSelectNodes.get(0), null, null, null, null, null, null, null);
    for (SqlNode dummyQuery : candidateQueries(topSelectNodes.get(0), aliasDummySqlSelect)) {
      try {
        sqlValidator.validate(dummyQuery);
        return sqlValidator.getValidatedNodeType(sqlNode);
      } catch (Throwable t) {
        lastFailure = t;
      }
    }

    // Chain the cause: without it this message masks the real failure (e.g. an authorization denial
    // while loading a base table's schema).
    throw new RuntimeException(String.format("Failed to derive the RelDataType for SqlNode: %s with topSqlNode: %s",
        sqlNode, topSelectNodes.get(0)), lastFailure);
  }

  /**
   * Returns the queries to validate for a dummy SELECT built from {@code topSelect}: the dummy wrapped in the WITH
   * clauses enclosing {@code topSelect} (if any), followed by the bare dummy.
   */
  private List<SqlNode> candidateQueries(SqlSelect topSelect, SqlSelect dummySqlSelect) {
    final SqlNode wrapped = wrapInEnclosingWithClauses(topSelect, dummySqlSelect);
    if (wrapped == dummySqlSelect) {
      return Collections.singletonList(dummySqlSelect);
    }
    final List<SqlNode> candidates = new ArrayList<>(2);
    candidates.add(wrapped);
    candidates.add(dummySqlSelect);
    return candidates;
  }

  /**
   * Wraps {@code query} in the WITH clauses that enclose {@code topSelect} in the original query, innermost first,
   * so that the result can be validated in the same CTE scope as {@code topSelect}. Returns {@code query} unchanged
   * when {@code topSelect} is not inside a WITH clause.
   */
  private SqlNode wrapInEnclosingWithClauses(SqlSelect topSelect, SqlNode query) {
    final List<SqlNodeList> withLists = enclosingWithLists.getOrDefault(topSelect, Collections.emptyList());
    SqlNode wrapped = query;
    for (int i = withLists.size() - 1; i >= 0; i--) {
      final SqlNodeList withList = withLists.get(i);
      wrapped = new SqlWith(withList.getParserPosition(), withList, wrapped);
    }
    return wrapped;
  }

  public RelDataType leastRestrictive(List<RelDataType> types) {
    return sqlValidator.getTypeFactory().leastRestrictive(types);
  }

  private class SqlNodePreprocessorForTypeDerivation extends SqlShuttle {
    // WITH lists enclosing the node currently being visited, outermost first
    private final Deque<SqlNodeList> withListStack = new ArrayDeque<>();

    @Override
    public SqlNode visit(SqlCall sqlCall) {
      if (sqlCall instanceof SqlWith) {
        // CTE names are visible both in the WITH body and in later WITH items, so every SELECT nested anywhere
        // under this SqlWith is validated with its WITH list in scope
        withListStack.addLast(((SqlWith) sqlCall).withList);
        try {
          return super.visit(sqlCall);
        } finally {
          withListStack.removeLast();
        }
      }
      if (sqlCall instanceof SqlSelect) {
        // Updates selectList of a SqlSelect node
        // to correctly handle t.* type nodes for accurate data type derivation
        if (((SqlSelect) sqlCall).getSelectList() == null) {
          List<String> names = new ArrayList<>();
          names.add("*");
          List<SqlParserPos> sqlParserPos = Collections.nCopies(names.size(), SqlParserPos.ZERO);
          SqlNode star = SqlIdentifier.star(names, SqlParserPos.ZERO, sqlParserPos);
          ((SqlSelect) sqlCall).setSelectList(SqlNodeList.of(star));
        }
        topSelectNodes.add((SqlSelect) sqlCall);
        if (!withListStack.isEmpty()) {
          enclosingWithLists.put((SqlSelect) sqlCall, new ArrayList<>(withListStack));
        }
      }
      return super.visit(sqlCall);
    }
  }
}

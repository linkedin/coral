/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.validate.SqlValidator;


/**
 * Abstract class for generic transformations on SqlCalls
 */
public abstract class OperatorTransformer {
  private SqlValidator sqlValidator;
  private final List<SqlSelect> topSelectNodes = new ArrayList<>();

  public OperatorTransformer() {

  }

  public OperatorTransformer(SqlValidator sqlValidator) {
    this.sqlValidator = sqlValidator;
  }

  protected abstract boolean condition(SqlCall sqlCall);

  protected abstract SqlCall transform(SqlCall sqlCall);

  public SqlCall apply(SqlCall sqlCall) {
    if (sqlCall instanceof SqlSelect) {
      this.topSelectNodes.add((SqlSelect) sqlCall);
    }
    if (condition(sqlCall)) {
      return transform(sqlCall);
    } else {
      return sqlCall;
    }
  }

  /**
   * To get the RelDatatype of a SqlNode, we iterate through `topSelectNodes` in reverse chronological order,
   * for each `topSelectNode`, we create a minimum dummy SqlSelect: SELECT `sqlNode` FROM `topSelectNode.getFrom()`
   * If the SqlValidator is able to validate the dummy SqlSelect, return the SqlNode's RelDataType directly.
   *
   * We can't just use the latest visited `topSelectNode` to construct the dummy SqlSelect because of
   * the following corner case:
   * SELECT * FROM (
   *   SELECT col1 FROM db.tbl
   * ) LATERAL JOIN EXPLODE(col1) t AS a WHERE t.a = 0
   * If we want to derive the datatype of `t.a`, the latest visited `topSelectNode` will be `SELECT * FROM db.tbl`,
   * however, `t.a` doesn't exist in `db.tbl`, so it would throw exception.
   * Therefore, we need to store all the `topSelectNode` (both inner `SELECT * FROM db.tbl` and the whole SQL)
   * in the `topSelectNodes` list and traverse them in reverse chronological order, return the datatype directly
   * once the datatype can be derived without exception.
   */
  protected RelDataType getRelDataType(SqlNode sqlNode) {
    if (sqlValidator == null) {
      throw new RuntimeException("Please provide sqlValidator to get the RelDataType of a SqlNode!");
    }
    for (int i = topSelectNodes.size() - 1; i >= 0; --i) {
      final SqlSelect topSelectNode = topSelectNodes.get(i);
      final SqlSelect dummySqlSelect = new SqlSelect(topSelectNode.getParserPosition(), null, SqlNodeList.of(sqlNode),
          topSelectNode.getFrom(), null, null, null, null, null, null, null);
      try {
        sqlValidator.validate(dummySqlSelect);
        return sqlValidator.getValidatedNodeType(dummySqlSelect).getFieldList().get(0).getType();
      } catch (Throwable ignored) {
      }
    }
    throw new RuntimeException("Failed to derive the RelDataType for SqlNode " + sqlNode);
  }
}

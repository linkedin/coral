/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.sql.validate.SqlValidator;


/**
 * Abstract class for generic transformations on SqlCalls
 */
public abstract class SqlCallTransformer {
  private SqlValidator sqlValidator;
  private final List<SqlSelect> topSelectNodes = new ArrayList<>();

  public SqlCallTransformer() {

  }

  public SqlCallTransformer(SqlValidator sqlValidator) {
    this.sqlValidator = sqlValidator;
  }

  /**
   * Condition of the transformer, it’s used to determine if the SqlCall should be transformed or not
   */
  protected abstract boolean condition(SqlCall sqlCall);

  /**
   * Implementation of the transformation, returns the transformed SqlCall
   */
  protected abstract SqlCall transform(SqlCall sqlCall);

  /**
   * Public entry of the transformer, it returns the result of transformed SqlCall if `condition(SqlCall)` returns true,
   * otherwise returns the input SqlCall without any transformation
   */
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
   * To get the RelDatatype of a SqlNode, we iterate through `topSelectNodes` from the latest visited to the oldest visited,
   * for each `topSelectNode`, we create a minimum dummy SqlSelect: SELECT `sqlNode` FROM `topSelectNode.getFrom()`
   * If the SqlValidator is able to validate the dummy SqlSelect, return the SqlNode's RelDataType directly.
   *
   * We can't just use the latest visited `topSelectNode` to construct the dummy SqlSelect because of
   * the following corner case:
   *
   * CREATE db.tbl(col1 array(int));
   *
   * SELECT * FROM (
   *   SELECT col1 FROM db.tbl
   * ) LATERAL JOIN EXPLODE(col1) t AS a WHERE t.a = 0
   *
   * If we want to derive the datatype of `t.a`, the latest visited `topSelectNode` will be `SELECT col1 FROM db.tbl`,
   * however, `t.a` doesn't exist in `db.tbl`, so it would throw exception.
   * Therefore, we need to store all the `topSelectNodes` (both inner `SELECT col1 FROM db.tbl` and the whole SQL)
   * in the `topSelectNodes` list and traverse them from the latest visited to the oldest visited, return the datatype
   * directly once it can be derived without exception.
   *
   * Note: This implementation assumes that the parent SqlSelect is visited before determining the datatype of the child
   * SqlNode, which is typically achieved by traversing the SqlNode tree using SqlShuttle.
   * The implementation might be updated to not rely on this assumption for determining the datatype of the child SqlNode.
   */
  protected RelDataType getRelDataType(SqlNode sqlNode) {
    if (sqlValidator == null) {
      throw new RuntimeException("SqlValidator does not exist to derive the RelDataType for SqlNode " + sqlNode);
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

  /**
   * This function creates a {@link SqlOperator} for a function with the function name and return type inference.
   */
  protected static SqlOperator createSqlOperator(String functionName, SqlReturnTypeInference typeInference) {
    SqlIdentifier sqlIdentifier = new SqlIdentifier(ImmutableList.of(functionName), SqlParserPos.ZERO);
    return new SqlUserDefinedFunction(sqlIdentifier, typeInference, null, null, null, null);
  }
}

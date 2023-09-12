/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlWindow;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;


/**
 * This class transforms the ordering in the input SqlCall to be compatible with Trino engine.
 * There is no need to override ASC inputs since the default null orderings of Coral IR, Hive and Trino all match.
 * However, "DESC NULLS LAST" need to be overridden to remove the redundant "NULLS LAST" since
 * Trino defaults to a NULLS LAST ordering for DESC anyways.
 *
 * For example, "SELECT * FROM TABLE_NAME ORDER BY COL_NAME DESC NULLS LAST "
 * is transformed to "SELECT * FROM TABLE_NAME ORDER BY COL_NAME DESC"
 *
 * Also, "SELECT ROW_NUMBER() OVER (PARTITION BY a ORDER BY b DESC NULLS LAST) AS rid FROM foo"
 * is transformed to "SELECT ROW_NUMBER() OVER (PARTITION BY a ORDER BY b DESC) AS rid FROM foo"
 */
public class NullOrderingTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    return (sqlCall.getOperator().kind == SqlKind.SELECT && ((SqlSelect) sqlCall).getOrderList() != null)
        || (sqlCall.getOperator().kind == SqlKind.WINDOW && ((SqlWindow) sqlCall).getOrderList() != null);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlNodeList newOrderList = new SqlNodeList(POS);

    for (SqlNode node : getOrderList(sqlCall)) {
      SqlNode operand = ((SqlBasicCall) node).getOperandList().get(0);

      if (node instanceof SqlBasicCall && ((SqlBasicCall) node).getOperator().kind == SqlKind.NULLS_LAST
          && operand instanceof SqlBasicCall && ((SqlBasicCall) operand).getOperator().kind == SqlKind.DESCENDING) {
        newOrderList.add(operand);
      } else {
        newOrderList.add(node);
      }
    }

    setOrderList(sqlCall, newOrderList);
    return sqlCall;
  }

  private SqlNodeList getOrderList(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case SELECT:
        return ((SqlSelect) sqlCall).getOrderList();
      case WINDOW:
        return ((SqlWindow) sqlCall).getOrderList();
      case DEFAULT:
        return new SqlNodeList(POS);
    }

    return new SqlNodeList(POS);
  }

  private void setOrderList(SqlCall sqlCall, SqlNodeList newOrderList) {
    switch (sqlCall.getOperator().kind) {
      case SELECT:
        ((SqlSelect) sqlCall).setOrderBy(newOrderList);
        break;
      case WINDOW:
        ((SqlWindow) sqlCall).setOrderList(newOrderList);
        break;
    }
  }
}

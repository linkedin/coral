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
 * This class implements the transformation of SqlCalls with NULLS LAST operator preceded by DESC
 *
 * For example, "SELECT * FROM TABLE_NAME ORDER BY COL_NAME DESC NULLS LAST "
 * is transformed to "SELECT * FROM TABLE_NAME ORDER BY COL_NAME DESC"
 *
 * We want this change as the NULLS LAST is redundant as Trino defaults to NULLS LAST ordering,
 * furthermore, this allows us to avoid regression.
 */
public class NullOrderingTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    return (sqlCall.getOperator().kind == SqlKind.SELECT && ((SqlSelect) sqlCall).getOrderList() != null)
        || (sqlCall.getOperator().kind == SqlKind.WINDOW && ((SqlWindow) sqlCall).getOrderList() != null);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlNodeList orderList = new SqlNodeList(POS);

    switch (sqlCall.getOperator().kind) {
      case SELECT:
        orderList = ((SqlSelect) sqlCall).getOrderList();
        break;
      case WINDOW:
        orderList = ((SqlWindow) sqlCall).getOrderList();
        break;
    }

    SqlNodeList newOrderList = new SqlNodeList(POS);

    for (SqlNode node : orderList) {
      SqlNode operand = ((SqlBasicCall) node).getOperandList().get(0);

      if (node instanceof SqlBasicCall && ((SqlBasicCall) node).getOperator().kind == SqlKind.NULLS_LAST
          && operand instanceof SqlBasicCall && ((SqlBasicCall) operand).getOperator().kind == SqlKind.DESCENDING) {
        newOrderList.add(operand);
      } else {
        newOrderList.add(node);
      }
    }

    switch (sqlCall.getOperator().kind) {
      case SELECT:
        ((SqlSelect) sqlCall).setOrderBy(newOrderList);
        break;
      case WINDOW:
        ((SqlWindow) sqlCall).setOrderList(newOrderList);
        break;
    }

    return sqlCall;
  }
}

/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;

import com.linkedin.coral.common.transformers.SqlCallTransformer;


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
    return sqlCall.getOperator().kind == SqlKind.NULLS_LAST && sqlCall.operand(0).getKind() == SqlKind.DESCENDING
        && sqlCall.operandCount() > 0;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return sqlCall.operand(0);
  }
}

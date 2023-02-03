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
 * Identity transformer is a no-op transformer.
 * It enables tracking all SELECT type SqlNodes for data type derivation.
 */
public class IdentityTransformer extends SqlCallTransformer {

  @Override
  protected boolean predicate(SqlCall sqlCall) {
    return sqlCall.getOperator().kind == SqlKind.SELECT;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return sqlCall;
  }
}

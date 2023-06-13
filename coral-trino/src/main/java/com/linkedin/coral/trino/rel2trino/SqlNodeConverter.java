/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidator;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.trino.rel2trino.transformers.FromUtcTimestampOperatorTransformer;


/**
 * SqlNodeConverter transforms the sqlNodes
 * in the input SqlNode representation to be compatible with Trino engine.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlNode. All the transformations performed as part of this shuttle require data type derivation.
 *
 * NOTE: This is a temporary class which hosts certain transformations which were previously done in RelToTrinoConverter.
 * This class may be refactored once standardized CoralIR is integrated in the CoralRelNode to trino SQL translation path.
 */
public class SqlNodeConverter extends SqlShuttle {
  private final SqlCallTransformers operatorTransformerList;

  public SqlNodeConverter(HiveMetastoreClient mscClient, SqlNode topSqlNode) {
    SqlValidator sqlValidator = new HiveToRelConverter(mscClient).getSqlValidator();
    TypeDerivationUtil typeDerivationUtil = new TypeDerivationUtil(sqlValidator, topSqlNode);
    operatorTransformerList = SqlCallTransformers.of(new FromUtcTimestampOperatorTransformer(typeDerivationUtil));
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    return operatorTransformerList.apply((SqlCall) super.visit(call));
  }
}

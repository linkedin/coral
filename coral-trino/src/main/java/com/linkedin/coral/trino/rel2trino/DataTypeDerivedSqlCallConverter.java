/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import com.linkedin.coral.trino.rel2trino.transformers.ConcatOperatorTransformer;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidator;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.trino.rel2trino.transformers.FromUtcTimestampOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.GenericProjectTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.NamedStructToCastTransformer;


/**
 * DataTypeDerivedSqlCallConverter transforms the sqlCalls
 * in the input SqlNode representation to be compatible with Trino engine.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlNode.
 *
 * All the transformations performed as part of this shuttle require RelDataType derivation.
 */
public class DataTypeDerivedSqlCallConverter extends SqlShuttle {
  private final SqlCallTransformers operatorTransformerList;

  public DataTypeDerivedSqlCallConverter(HiveMetastoreClient mscClient, SqlNode topSqlNode) {
    SqlValidator sqlValidator = new HiveToRelConverter(mscClient).getSqlValidator();
    TypeDerivationUtil typeDerivationUtil = new TypeDerivationUtil(sqlValidator, topSqlNode);
    operatorTransformerList = SqlCallTransformers.of(new FromUtcTimestampOperatorTransformer(typeDerivationUtil),
        new GenericProjectTransformer(typeDerivationUtil), new NamedStructToCastTransformer(typeDerivationUtil), new ConcatOperatorTransformer(typeDerivationUtil));
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    return operatorTransformerList.apply((SqlCall) super.visit(call));
  }
}

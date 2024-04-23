/**
 * Copyright 2022-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.spark.transformers.SingleUnionFieldReferenceTransformer;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;


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
  private final HiveToRelConverter toRelConverter;

  public DataTypeDerivedSqlCallConverter(HiveMetastoreClient mscClient, SqlNode topSqlNode) {
    toRelConverter = new HiveToRelConverter(mscClient);

    TypeDerivationUtil typeDerivationUtil = new TypeDerivationUtil(toRelConverter.getSqlValidator(), topSqlNode);
    operatorTransformerList = SqlCallTransformers.of(new SingleUnionFieldReferenceTransformer(typeDerivationUtil));
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    return operatorTransformerList.apply((SqlCall) super.visit(call));
  }
}

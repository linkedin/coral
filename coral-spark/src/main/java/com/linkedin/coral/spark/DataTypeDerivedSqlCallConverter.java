/**
 * Copyright 2022-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.Set;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.catalog.CoralCatalog;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.transformers.ExtractUnionFunctionTransformer;


/**
 * DataTypeDerivedSqlCallConverter transforms the sqlCalls
 * in the input SqlNode representation to be compatible with Spark engine.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlNode.
 *
 * All the transformations performed as part of this shuttle require RelDataType derivation.
 */
public class DataTypeDerivedSqlCallConverter extends SqlShuttle {
  private final SqlCallTransformers operatorTransformerList;
  private final HiveToRelConverter toRelConverter;
  TypeDerivationUtil typeDerivationUtil;

  /**
   * Backward-compatible constructor wired against {@link HiveMetastoreClient}.
   *
   * @deprecated Use {@link #DataTypeDerivedSqlCallConverter(CoralCatalog, SqlNode, Set)} instead.
   */
  @Deprecated
  public DataTypeDerivedSqlCallConverter(HiveMetastoreClient mscClient, SqlNode topSqlNode,
      Set<SparkUDFInfo> sparkUDFInfos) {
    this(new HiveToRelConverter(mscClient), topSqlNode, sparkUDFInfos);
  }

  /**
   * CoralCatalog-backed constructor. The inner {@link HiveToRelConverter} is wired against the
   * CoralCatalog, so base-table resolution during type derivation works uniformly across Hive- and
   * Iceberg-backed tables.
   */
  public DataTypeDerivedSqlCallConverter(CoralCatalog coralCatalog, SqlNode topSqlNode,
      Set<SparkUDFInfo> sparkUDFInfos) {
    this(new HiveToRelConverter(coralCatalog), topSqlNode, sparkUDFInfos);
  }

  private DataTypeDerivedSqlCallConverter(HiveToRelConverter toRelConverter, SqlNode topSqlNode,
      Set<SparkUDFInfo> sparkUDFInfos) {
    this.toRelConverter = toRelConverter;
    typeDerivationUtil = new TypeDerivationUtil(toRelConverter.getSqlValidator(), topSqlNode);
    operatorTransformerList =
        SqlCallTransformers.of(new ExtractUnionFunctionTransformer(typeDerivationUtil, sparkUDFInfos));
  }

  @Override
  public SqlNode visit(SqlCall call) {
    return operatorTransformerList.apply((SqlCall) super.visit(call));
  }
}

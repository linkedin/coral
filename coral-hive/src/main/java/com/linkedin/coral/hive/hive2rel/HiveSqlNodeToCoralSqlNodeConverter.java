/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidator;

import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.transformers.ShiftArrayIndexTransformer;
import com.linkedin.coral.transformers.SingleUnionFieldReferenceTransformer;


/**
 * Converts Hive SqlNode to Coral SqlNode
 */
public class HiveSqlNodeToCoralSqlNodeConverter extends SqlShuttle {
  private final SqlCallTransformers operatorTransformerList;

  public HiveSqlNodeToCoralSqlNodeConverter(SqlValidator sqlValidator, SqlNode topSqlNode) {
    TypeDerivationUtil typeDerivationUtil = new TypeDerivationUtil(sqlValidator, topSqlNode);
    operatorTransformerList = SqlCallTransformers.of(new ShiftArrayIndexTransformer(typeDerivationUtil),
        new SingleUnionFieldReferenceTransformer(typeDerivationUtil));
  }

  @Override
  public SqlNode visit(SqlCall call) {
    return super.visit(operatorTransformerList.apply(call));
  }
}

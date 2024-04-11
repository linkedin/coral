/**
 * Copyright 2022-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.trino.rel2trino.transformers.CastOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.ConcatOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.FromUtcTimestampOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.GenericProjectTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.NamedStructToCastTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.SubstrOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.UnionSqlCallTransformer;


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
    topSqlNode.accept(new RegisterDynamicFunctionsForTypeDerivation());

    TypeDerivationUtil typeDerivationUtil = new TypeDerivationUtil(toRelConverter.getSqlValidator(), topSqlNode);
    operatorTransformerList = SqlCallTransformers.of(new FromUtcTimestampOperatorTransformer(typeDerivationUtil),
        new GenericProjectTransformer(typeDerivationUtil), new NamedStructToCastTransformer(typeDerivationUtil),
        new ConcatOperatorTransformer(typeDerivationUtil), new SubstrOperatorTransformer(typeDerivationUtil),
        new CastOperatorTransformer(typeDerivationUtil), new UnionSqlCallTransformer(typeDerivationUtil));
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    return operatorTransformerList.apply((SqlCall) super.visit(call));
  }

  private class RegisterDynamicFunctionsForTypeDerivation extends SqlShuttle {
    @Override
    public SqlNode visit(SqlCall sqlCall) {
      if (sqlCall instanceof SqlBasicCall && sqlCall.getOperator() instanceof VersionedSqlUserDefinedFunction
          && sqlCall.getOperator().getName().contains(".")) {
        // Register versioned SqlUserDefinedFunctions in RelConverter's dynamicFunctionRegistry.
        // This enables the SqlValidator to derive RelDataType for SqlCalls that involve these operators.
        Function function = new Function(sqlCall.getOperator().getName(), sqlCall.getOperator());
        toRelConverter.getFunctionResolver().addDynamicFunctionToTheRegistry(sqlCall.getOperator().getName(), function);
      }
      return super.visit(sqlCall);
    }
  }
}

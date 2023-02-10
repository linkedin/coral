/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.trino.rel2trino.transformers.IdentityTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.RelationalOperatorTransformer;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;


/**
 * SqlNodeConverter transforms the sqlNodes
 * in the input SqlNode representation to be compatible with Trino engine.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlNode.
 *
 * NOTE: This is a temporary class which hosts certain transformations which were previously done in RelToTrinoConverter.
 * This class will be refactored once standardized CoralIR is integrated in the CoralRelNode to trino SQL translation path.
 */
public class SqlNodeConverter extends SqlShuttle {
  private final SqlCallTransformers operatorTransformerList;

  public SqlNodeConverter(HiveMetastoreClient mscClient) {
    SqlValidator sqlValidator = new HiveToRelConverter(mscClient).getSqlValidator();
    operatorTransformerList =
        SqlCallTransformers.of(new IdentityTransformer(), new RelationalOperatorTransformer(sqlValidator));
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    SqlCall interimSqlCall = operatorTransformerList.apply(call);
    SqlCall transformedSqlCall = getTransformedSqlCall(interimSqlCall);
    return super.visit(transformedSqlCall);
  }

  private SqlCall getTransformedSqlCall(SqlCall sqlCall) {
    if (sqlCall.getOperator().kind == SqlKind.SELECT) {
      return getTransformedSqlSelectSqlCall(sqlCall);
    }
    return sqlCall;
  }

  /**
   * Input SqlCall to this method will access nested fields as is, such as SELECT a.b, a.c.
   * The below transformation appends an AS operator on nested fields and updates the SqlCall to: SELECT a.b AS b, a.c AS c
   * @param sqlCall input SqlCall
   * @return transformed SqlCall
   */
  private SqlCall getTransformedSqlSelectSqlCall(SqlCall sqlCall) {
    if (((SqlSelect) sqlCall).getSelectList() != null && ((SqlSelect) sqlCall).getSelectList().size() != 0) {
      final List<SqlNode> modifiedSelectList = new ArrayList<>();

      for (SqlNode selectNode : ((SqlSelect) sqlCall).getSelectList().getList()) {
        final String name = SqlValidatorUtil.getAlias(selectNode, -1);
        final boolean nestedFieldAccess =
            selectNode instanceof SqlIdentifier && ((SqlIdentifier) selectNode).names.size() > 1;

        // Always add "AS" when accessing nested fields.
        // In parent class "AS" clause is skipped for "SELECT a.b AS b". Here we will keep the "a.b AS b"
        if (nestedFieldAccess) {
          selectNode = SqlStdOperatorTable.AS.createCall(POS, selectNode, new SqlIdentifier(name, POS));
        }
        modifiedSelectList.add(selectNode);
      }
      ((SqlSelect) sqlCall).setSelectList(new SqlNodeList(modifiedSelectList, POS));
    }
    return sqlCall;
  }
}

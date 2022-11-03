/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;


/**
 * IntermediateTrinoSqlNodeRewriter rewrites the TrinoSqlNode representation. It transforms the sqlCalls
 * in the TrinoSqlNode to be compatible with Trino engine in a backward compatible manner.
 *
 * This is achieved by visiting the TrinoSqlNode AST in a pre-order traversal manner and
 * transforming each SqlNode (SqlCall), wherever required.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlCall.
 *
 * NOTE: This is a temporary class which hosts certain transformations which were previously done in RelToTrinoConverter.
 * This class will be refactored once standardized CoralIR is integrated in the CoralRelNode to trino SQL translation path.
 */
public class IntermediateTrinoSqlNodeRewriter extends SqlShuttle {
  public IntermediateTrinoSqlNodeRewriter() {
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    SqlCall transformedSqlCall = getTransformedSqlCall(call);
    return super.visit(transformedSqlCall);
  }

  public static SqlCall getTransformedSqlCall(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case SELECT:
        return getTransformedSqlSelectSqlCall(sqlCall);
      default:
        return sqlCall;
    }
  }

  /**
   * Input SqlCall to this method will access nested fields as is, such as SELECT a.b, a.c.
   * The below transformations appends an AS operator on nested fields and updated the SqlCall to: SELECT a.b AS b, a.c AS c
   * @param sqlCall input SqlCall
   * @return transformed SqlCall
   */
  private static SqlCall getTransformedSqlSelectSqlCall(SqlCall sqlCall) {
    if (((SqlSelect) sqlCall).getSelectList() != null && ((SqlSelect) sqlCall).getSelectList().size() != 0) {
      final List<SqlNode> modifiedSelectList = new ArrayList<>();

      for (SqlNode selectNode : ((SqlSelect) sqlCall).getSelectList().getList()) {
        final String name = SqlValidatorUtil.getAlias(selectNode, -1);
        final boolean nestedFieldAccess =
            selectNode instanceof SqlIdentifier && ((SqlIdentifier) selectNode).names.size() > 1;

        // always add "AS" when accessing nested fields.
        // In parent class "as" is skipped for "select a.b as b", here we will keep the "a.b as b"
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

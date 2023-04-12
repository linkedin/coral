/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;


/**
 * This transformer operates on SqlSelect type sqlCalls. It appends an alias to all the projected fields with fully qualified names.
 * For Example: It modifies a SqlSelect sqlCall statement of the form: "SELECT foo.a FROM foo" to "SELECT foo.a AS a FROM foo".
 */
public class SqlSelectAliasAppenderTransformer extends SqlCallTransformer {

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().kind == SqlKind.SELECT && ((SqlSelect) sqlCall).getSelectList() != null
        && ((SqlSelect) sqlCall).getSelectList().size() != 0;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final List<SqlNode> modifiedSelectList = new ArrayList<>();

    for (SqlNode selectNode : ((SqlSelect) sqlCall).getSelectList().getList()) {
      final String name = SqlValidatorUtil.getAlias(selectNode, -1);
      final boolean nestedFieldAccess =
          selectNode instanceof SqlIdentifier && ((SqlIdentifier) selectNode).names.size() > 1;

      // Always add "AS" when accessing nested fields.
      // CoralSqlNode does not contain "AS" clause for "SELECT a.b AS b". Here we will introduce the "a.b AS b"
      if (nestedFieldAccess) {
        selectNode = SqlStdOperatorTable.AS.createCall(POS, selectNode, new SqlIdentifier(name, POS));
      }
      modifiedSelectList.add(selectNode);
    }
    ((SqlSelect) sqlCall).setSelectList(new SqlNodeList(modifiedSelectList, POS));
    return sqlCall;
  }
}

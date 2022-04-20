/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.POS;


public class AddExplicitAlias extends SqlShuttle {

  private final List<String> aliases;
  // Use a boolean to track if it's the outermost select statement
  private boolean isOutermostLevel;

  public AddExplicitAlias(List<String> aliases) {
    this.aliases = aliases;
    this.isOutermostLevel = true;
  }

  @Override
  public SqlNode visit(SqlCall call) {
    // We only care about the outermost SqlSelect (isOutermostLevel == true),
    // that's the select list we want to explicitly add aliases to,
    // the visitor should enter the following block only once.
    if (call.getKind() == SqlKind.SELECT && isOutermostLevel) {
      isOutermostLevel = false;
      SqlSelect select = (SqlSelect) call;
      // Make sure the select list is the same length as the coral-schema fields
      Preconditions.checkState(aliases.size() == select.getSelectList().size());
      List<SqlNode> aliasedSelectNodes = IntStream.range(0, select.getSelectList().size())
          .mapToObj(i -> updateAlias(select.getSelectList().get(i), aliases.get(i))).collect(Collectors.toList());
      select.setSelectList(new SqlNodeList(aliasedSelectNodes, SqlParserPos.ZERO));
    }
    return super.visit(call);
  }

  private SqlNode updateAlias(SqlNode node, String newAlias) {
    if (node.getKind() == SqlKind.AS) {
      // If alias already exists, replace it with the new one
      SqlNode selectWithoutAlias = ((SqlCall) node).getOperandList().get(0);
      return SqlStdOperatorTable.AS.createCall(POS, selectWithoutAlias, new SqlIdentifier(newAlias, POS));
    } else {
      // If there's no existing alias, just add the new alias
      return SqlStdOperatorTable.AS.createCall(POS, node, new SqlIdentifier(newAlias, POS));
    }
  }
}

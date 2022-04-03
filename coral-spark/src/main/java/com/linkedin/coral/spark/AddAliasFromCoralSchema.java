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

import org.apache.avro.Schema;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.POS;


public class AddAliasFromCoralSchema extends SqlShuttle {

  private final Schema schema;

  public AddAliasFromCoralSchema(Schema schema) {
    this.schema = schema;
  }

  @Override
  public SqlNode visit(SqlCall call) {
    // Spark SQL doesn't support CASTING the named_struct function TO A ROW.
    // Keeping the expression inside CAST operator seems sufficient.
    if (call.getKind() == SqlKind.SELECT) {
      SqlSelect select = (SqlSelect) call;
      // Make sure the select list is the same length as the coral-schema fields
      Preconditions.checkState(schema.getFields().size() == select.getSelectList().size());
      List<SqlNode> aliasedSelectNodes = IntStream
          .range(0, select.getSelectList().size()).mapToObj(i -> SqlStdOperatorTable.AS.createCall(POS,
              select.getSelectList().get(i), new SqlIdentifier(schema.getFields().get(i).name(), POS)))
          .collect(Collectors.toList());
      select.setSelectList(new SqlNodeList(aliasedSelectNodes, SqlParserPos.ZERO));
    }
    return super.visit(call);
  }
}

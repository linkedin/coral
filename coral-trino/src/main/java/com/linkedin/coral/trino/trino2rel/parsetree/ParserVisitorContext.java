/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel.parsetree;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.calcite.CalciteUtil;

import static org.apache.calcite.sql.fun.SqlStdOperatorTable.CASE;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


public class ParserVisitorContext {
  final List<SqlIdentifier> fieldNames = new ArrayList<>();
  final List<SqlDataTypeSpec> fieldTypes = new ArrayList<>();
  final List<SqlNode> whenClauses = new ArrayList<>();
  final List<SqlNode> thenClauses = new ArrayList<>();
  boolean isRows = false;
  SqlNode lowerBound = null;
  SqlNode upperBound = null;

  public SqlDataTypeSpec createRowType(SqlParserPos pos) {
    return new SqlDataTypeSpec(new SqlRowTypeNameSpec(pos, fieldNames, fieldTypes), pos);
  }

  public SqlCall createCaseCall(SqlParserPos pos, SqlNode operand, SqlNode defaultValue) {
    return CASE.createCall(null, pos, operand, CalciteUtil.createSqlNodeList(whenClauses, pos),
        CalciteUtil.createSqlNodeList(thenClauses, pos), defaultValue);
  }

  public SqlWindow createWindow(SqlParserPos pos, SqlNodeList partitionList, SqlNodeList orderList) {
    return SqlWindow.create(null, null, partitionList, orderList, CalciteUtil.createLiteralBoolean(isRows, ZERO),
        lowerBound, upperBound, null, pos);
  }
}

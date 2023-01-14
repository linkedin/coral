/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.calcite.sql.util;

import org.apache.calcite.sql.SqlCharStringLiteral;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.calcite.sql.SqlCreateTable;


public class SqlDdlNodes {

  /** Creates a CREATE TABLE. */
  public static SqlCreateTable createTable(SqlParserPos pos, boolean replace, boolean ifNotExists, SqlIdentifier name,
      SqlNodeList columnList, SqlNode query, SqlNode tableSerializer, SqlNodeList tableFileFormat,
      SqlCharStringLiteral tableRowFormat) {
    return new SqlCreateTable(pos, replace, ifNotExists, name, columnList, query, tableSerializer, tableFileFormat,
        tableRowFormat);
  }
}

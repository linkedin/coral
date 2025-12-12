/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.sql;

import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.parser.SqlParserPos;


/**
 * Defines the keywords that can occur immediately after the "INSERT" keyword.
 *
 * <p>Standard SQL has no such keywords, but extension projects may define them.
 *
 * <p>This class is almost an extension of {@link org.apache.calcite.sql.SqlInsertKeyword}.
 */
public enum RichSqlInsertKeyword {
  OVERWRITE;

  /**
   * Creates a parse-tree node representing an occurrence of this keyword at a particular position
   * in the parsed text.
   */
  public SqlLiteral symbol(SqlParserPos pos) {
    return SqlLiteral.createSymbol(this, pos);
  }
}

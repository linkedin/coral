/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import io.trino.sql.parser.ParsingOptions;
import io.trino.sql.parser.SqlParser;
import io.trino.sql.tree.Statement;

import static io.trino.sql.parser.ParsingOptions.DecimalLiteralTreatment.AS_DECIMAL;


public class PrestoParserDriver {
  private final static ParsingOptions parsingOptions = new ParsingOptions(AS_DECIMAL /* anything */);
  /**
   * Use the Presto SqlParser to parse the command and return the presto statement.
   *
   * @return {@link Statement} as response
   */
  public static Statement parse(String command) {
    SqlParser sqlParser = new SqlParser();
    return sqlParser.createStatement(command, parsingOptions);
  }

}

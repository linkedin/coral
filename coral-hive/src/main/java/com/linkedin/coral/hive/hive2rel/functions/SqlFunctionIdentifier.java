/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.List;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParserPos;


/**
 * Class to encapsulate more information about function name identifiers.
 * The extra information is useful for resolving Dali function names
 */
public class SqlFunctionIdentifier extends SqlIdentifier {

  private final List<String> schemaPath;

  public SqlFunctionIdentifier(String functionName, List<String> schemaPath) {
    super(functionName, SqlParserPos.ZERO);
    this.schemaPath = schemaPath;
  }

  public List<String> getSchemaPath() {
    return schemaPath;
  }
}

/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import java.util.List;

import org.apache.calcite.sql.*;

import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


/**
 * Class to represent builtin or user-defined function. This provides
 * information required to analyze the function call in SQL statement and to
 * convert the function to intermediate representation in Calcite. This does
 * not provide function definition to actually evaluate the function. Right now,
 * this also does not provide implementation to dynamically figure out return type
 * based on input parameters.
 *
 *
 */
public class Function {

  // Function class name specified in TBLPROPERTIES clause.  It contains path leading to the class file.
  // Example: "com.linkedin.dali.udf.date.hive.DateFormatToEpoch"
  private final String functionName;
  private final SqlOperator sqlOperator;

  public Function(String functionName, SqlOperator sqlOperator) {
    this.functionName = functionName;
    this.sqlOperator = sqlOperator;
  }

  public String getFunctionName() {
    return functionName;
  }

  public SqlOperator getSqlOperator() {
    return sqlOperator;
  }

  public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
    return sqlOperator.createCall(qualifier, ZERO, operands.toArray(new SqlNode[0]));
  }
}

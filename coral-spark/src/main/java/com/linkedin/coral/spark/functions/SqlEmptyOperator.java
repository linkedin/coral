/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.functions;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;


/**
 * SqlEmptyOperator is a type of SqlSpecialOperator where the operation name is an empty string.
 * Hence, unparsing a sqlCall where the operator is a SqlEmptyOperator only prints out the operands.
 */
public class SqlEmptyOperator extends SqlSpecialOperator {
  public static final SqlEmptyOperator EMPTY_OPERATOR = new SqlEmptyOperator();

  public SqlEmptyOperator() {
    super("", SqlKind.OTHER, 200, true, ReturnTypes.ARG0, null, OperandTypes.ANY);
  }

  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    writer.keyword(this.getName());
    call.operand(0).unparse(writer, 0, 0);
  }
}

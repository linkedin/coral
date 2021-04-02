/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.rel2presto.functions;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;


/**
 * Represents the CARDINALITY function in Presto
 *
 * Identical the CARDINALITY SQL function, but CARDINALITY in PRESTO returns a BIGINT so we
 * need to cast it to an INTEGER when unparsing
 */
public class PrestoCardinalityFunction extends SqlFunction {
  public static final PrestoCardinalityFunction INSTANCE = new PrestoCardinalityFunction();

  public PrestoCardinalityFunction() {
    super("CARDINALITY", SqlKind.OTHER_FUNCTION, ReturnTypes.INTEGER_NULLABLE, null, OperandTypes.COLLECTION_OR_MAP,
        SqlFunctionCategory.SYSTEM);
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    final SqlWriter.Frame frame = writer.startFunCall(SqlStdOperatorTable.CAST.getName());
    super.unparse(writer, call, leftPrec, rightPrec);
    writer.sep("AS");
    writer.sep("INTEGER");
    writer.endFunCall(frame);
  }
}

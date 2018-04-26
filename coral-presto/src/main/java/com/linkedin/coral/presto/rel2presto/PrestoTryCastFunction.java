package com.linkedin.coral.presto.rel2presto;

import com.linkedin.coral.com.google.common.base.Preconditions;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlReturnTypeInference;


public class PrestoTryCastFunction extends SqlFunction {
  public static final PrestoTryCastFunction INSTANCE = new PrestoTryCastFunction();

  public PrestoTryCastFunction() {
    super("try_cast", SqlKind.CAST, new SqlReturnTypeInference() {
      @Override
      public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
        return SqlStdOperatorTable.CAST.inferReturnType(opBinding);
      }
    }, null, null, SqlFunctionCategory.USER_DEFINED_FUNCTION);
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    Preconditions.checkState(call.operandCount() == 2);
    final SqlWriter.Frame frame = writer.startFunCall(getName());
    call.operand(0).unparse(writer, 0, 0);
    writer.sep("AS");
    if (call.operand(1) instanceof SqlIntervalQualifier) {
      writer.sep("INTERVAL");
    }
    call.operand(1).unparse(writer, 0, 0);
    writer.endFunCall(frame);
  }
}

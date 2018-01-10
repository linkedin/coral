package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.base.Preconditions;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;


public class HiveReturnTypes {

  private HiveReturnTypes() {

  }

  public static final SqlReturnTypeInference ARG1_OR_ARG2 = new SqlReturnTypeInference() {
    @Override
    public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
      Preconditions.checkState(opBinding.getOperandCount() == 3);
      if (!opBinding.isOperandNull(1, false)) {
        return opBinding.getOperandType(1);
      } else {
        return opBinding.getOperandType(2);
      }
    }
  };

  public static final SqlReturnTypeInference STRING = ReturnTypes.explicit(SqlTypeName.VARCHAR);
  public static final SqlReturnTypeInference BINARY = ReturnTypes.explicit(SqlTypeName.BINARY);
}

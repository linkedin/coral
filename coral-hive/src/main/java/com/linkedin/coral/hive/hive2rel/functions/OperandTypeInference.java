/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.base.Preconditions;

import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;


// TODO: move this to calcite code
public class OperandTypeInference {

  private OperandTypeInference() {

  }

  // expects operands to be (boolean, any, same_as_operand2)
  public static final SqlOperandTypeInference BOOLEAN_ANY_SAME =
      new SqlOperandTypeInference() {
        @Override
        public void inferOperandTypes(SqlCallBinding callBinding, RelDataType returnType,
                                      RelDataType[] relDataTypes) {
          final RelDataType unknownType = callBinding.getValidator().getUnknownType();
          List<SqlNode> operands = callBinding.operands();
          Preconditions.checkState(operands.size() == 3 && relDataTypes.length == 3);
          RelDataTypeFactory typeFactory = callBinding.getTypeFactory();
          relDataTypes[0] = typeFactory.createSqlType(SqlTypeName.BOOLEAN);
          relDataTypes[1] = callBinding.getValidator().deriveType(callBinding.getScope(), operands.get(1));
          relDataTypes[2] = callBinding.getValidator().deriveType(callBinding.getScope(), operands.get(2));
        }
      };
}

package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
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

  public static final SqlReturnTypeInference ARRAY_OF_STR_STR_MAP = new SqlReturnTypeInference() {
    @Override
    public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      RelDataType strType = typeFactory.createSqlType(SqlTypeName.VARCHAR);
      return typeFactory.createArrayType(
          typeFactory.createMapType(strType, strType), -1);
    }
  };

  public static SqlReturnTypeInference arrayOfType(final SqlTypeName typeName) {
    return new SqlReturnTypeInference() {
      @Override
      public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
        return opBinding.getTypeFactory().createArrayType(
            opBinding.getTypeFactory().createSqlType(typeName), -1);
      }
    };
  }

  public static SqlReturnTypeInference mapOfType(final SqlTypeName keyType, final SqlTypeName valueType) {
    return new SqlReturnTypeInference() {
      @Override
      public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
        RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
        return typeFactory.createMapType(
            typeFactory.createSqlType(keyType),
            typeFactory.createSqlType(valueType)
        );
      }
    };
  }

  public static SqlReturnTypeInference rowOf(ImmutableList<String> fieldNames, ImmutableList<SqlTypeName> types) {
    return new SqlReturnTypeInference() {
      @Override
      public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
        RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
        List<RelDataType> relTypes = types.stream()
            .map(typeFactory::createSqlType)
            .collect(Collectors.toList());
        return typeFactory.createStructType(relTypes, fieldNames);
      }
    };
  }
}

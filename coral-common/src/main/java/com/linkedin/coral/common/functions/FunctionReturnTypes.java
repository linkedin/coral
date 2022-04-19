/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeTransforms;

import com.linkedin.coral.com.google.common.collect.ImmutableList;


/**
 * This class provides function return types that are not in {@link ReturnTypes}
 */
public final class FunctionReturnTypes {

  private FunctionReturnTypes() {

  }

  public static final SqlReturnTypeInference IF_FUNC_RETURN_TYPE = opBinding -> {
    Preconditions.checkState(opBinding.getOperandCount() == 3);
    final RelDataType type1 = opBinding.getOperandType(1);
    final RelDataType type2 = opBinding.getOperandType(2);
    if (type1 == type2) {
      return type1;
    }
    // If the types mismatch, the non-literal one's type should be picked
    // i.e. the type of `if(..., 0, bigint_type_field)` should be bigint rather than int
    return opBinding.isOperandLiteral(1, false) ? type2 : type1;
  };

  public static final SqlReturnTypeInference STRING = ReturnTypes.explicit(SqlTypeName.VARCHAR);
  public static final SqlReturnTypeInference BINARY = ReturnTypes.explicit(SqlTypeName.BINARY);
  public static final SqlReturnTypeInference SMALLINT = ReturnTypes.explicit(SqlTypeName.SMALLINT);
  public static final SqlReturnTypeInference TIMESTAMP = ReturnTypes.explicit(SqlTypeName.TIMESTAMP);

  public static final SqlReturnTypeInference ARRAY_OF_STR_STR_MAP = opBinding -> {
    RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
    RelDataType strType = typeFactory.createSqlType(SqlTypeName.VARCHAR);
    return typeFactory.createArrayType(typeFactory.createMapType(strType, strType), -1);
  };

  public static final SqlReturnTypeInference ARRAY_OF_ARG0_TYPE =
      opBinding -> opBinding.getTypeFactory().createArrayType(opBinding.getOperandType(0), -1);

  public static SqlReturnTypeInference arrayOfType(final SqlTypeName typeName) {
    return arrayOfType(typeName, false);
  }

  public static SqlReturnTypeInference arrayOfType(final SqlTypeName typeName, boolean elementsNullable) {
    return opBinding -> {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      RelDataType relType;
      if (elementsNullable) {
        relType = ReturnTypes.cascade(ReturnTypes.explicit(typeName), SqlTypeTransforms.TO_NULLABLE)
            .inferReturnType(opBinding);
      } else {
        relType = typeFactory.createSqlType(typeName);
      }
      return typeFactory.createArrayType(relType, -1);
    };
  }

  public static SqlReturnTypeInference mapOfType(final SqlTypeName keyType, final SqlTypeName valueType) {
    return opBinding -> {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      return typeFactory.createMapType(typeFactory.createSqlType(keyType), typeFactory.createSqlType(valueType));
    };
  }

  public static SqlReturnTypeInference rowOf(ImmutableList<String> fieldNames, ImmutableList<SqlTypeName> types) {
    return opBinding -> {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      List<RelDataType> relTypes = types.stream().map(typeFactory::createSqlType).collect(Collectors.toList());
      return typeFactory.createStructType(relTypes, fieldNames);
    };
  }

  /**
   * Creates a row type given the field names and {@link SqlReturnTypeInference} of the fields.
   *
   * This method is useful to create row types whose fields are complex types and hence cannot be represented through
   * {@link SqlTypeName}s to be used in {@link #rowOf(ImmutableList, ImmutableList)}
   *
   * @param fieldNames List of field names
   * @param types List of {@link SqlReturnTypeInference} corresponding to field names
   * @return {@link SqlReturnTypeInference} object inferring struct type of field names and filed types baed on
   * input {@link SqlReturnTypeInference} objects.
   */
  public static SqlReturnTypeInference rowOfInference(ImmutableList<String> fieldNames,
      ImmutableList<SqlReturnTypeInference> types) {
    return opBinding -> {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      List<RelDataType> relTypes = types.stream().map(t -> t.inferReturnType(opBinding)).collect(Collectors.toList());
      return typeFactory.createStructType(relTypes, fieldNames);
    };
  }
}

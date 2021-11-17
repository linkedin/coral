/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.ImmutableList;

import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.coalesce;


/**
 * This class provides function return types that are not in {@link ReturnTypes}
 */
public final class FunctionReturnTypes {

  private FunctionReturnTypes() {

  }

  public static final SqlReturnTypeInference ARG1_OR_ARG2 = opBinding -> {
    Preconditions.checkState(opBinding.getOperandCount() == 3);
    if (!opBinding.isOperandNull(1, false)) {
      return opBinding.getOperandType(1);
    } else {
      return opBinding.getOperandType(2);
    }
  };

  public static final SqlReturnTypeInference STRING = ReturnTypes.explicit(SqlTypeName.VARCHAR);
  public static final SqlReturnTypeInference BINARY = ReturnTypes.explicit(SqlTypeName.BINARY);
  public static final SqlReturnTypeInference BIGINT = ReturnTypes.explicit(SqlTypeName.BIGINT);
  public static final SqlReturnTypeInference SMALLINT = ReturnTypes.explicit(SqlTypeName.SMALLINT);
  public static final SqlReturnTypeInference DATE = ReturnTypes.explicit(SqlTypeName.DATE);
  public static final SqlReturnTypeInference TIMESTAMP = ReturnTypes.explicit(SqlTypeName.TIMESTAMP);

  public static final SqlReturnTypeInference ARRAY_OF_STR_STR_MAP = opBinding -> {
    RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
    RelDataType strType = typeFactory.createSqlType(SqlTypeName.VARCHAR);
    return typeFactory.createArrayType(typeFactory.createMapType(strType, strType), -1);
  };

  /**
   * The semantics for the extract_union is now pass-through: Assuming the engine's reader could deal with
   * union type and explode it into a struct, this extract_union UDF's return type will simply follow exploded struct's
   * schema based on how many arguments passed by users.
   */
  public static final SqlReturnTypeInference EXTRACT_UNION_FUNCTION_RETURN_STRATEGY = opBinding -> {
    int numArgs = opBinding.getOperandCount();
    Preconditions.checkState(numArgs == 1 || numArgs == 2);
    // 1-arg case
    if (numArgs == 1) {
      return opBinding.getOperandType(0);
    }
    // 2-arg case
    else {
      int ordinal = opBinding.getOperandLiteralValue(1, Integer.class);
      return opBinding.getOperandType(0).getFieldList().get(ordinal).getType();
    }
  };

  /**
   * Represents the return type for the coalesce_struct UDF that is built for bridging the schema difference
   * between extract_union UDF's processed schema of union field in Coral IR (let's call it struct_ex) and
   * Trino's schema when deserializing union field from its reader.
   * (Let's call it struct_tr, See https://github.com/trinodb/trino/pull/3483 for details).
   *
   * The main reason we need this briding capability is due to the fact that we have existing users relying on the
   * schema of struct_ex. While the underlying reader(e.g. the trino one referenced above) starts to interpret the union
   * in its own format, Coral tries to maintain backward compatibility on top of that. Notably we also have
   * Iceberg reader does the same, see Linkedin's (temporary) fork on Iceberg:
   * https://github.com/linkedin/iceberg/pull/84 (Avro)
   * https://github.com/linkedin/iceberg/pull/85 (ORC)
   *
   *
   * Further details:
   * struct_tr looks like:
   * struct&lt;tag:int, field0:type0, field1:type1, ... fieldN:typeN&gt;
   *
   * struct_ex looks like:
   * struct&lt;tag_0:type0, tag_1:type1, ... tag_N:typeN&gt;
   *
   * This new UDF could be stated as the following signatures:
   * def coalesce_struct(struct:struct_tr) : struct_ex = {...}
   * def coalesce_struct(struct:struct_tr, ordinal: int): field_at_ordinal = {...}
   *
   */
  public static final SqlReturnTypeInference COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY = opBinding -> {
    int numArgs = opBinding.getOperandCount();
    RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
    Preconditions.checkState(numArgs == 1 || numArgs == 2);
    RelDataType coalescedDataType = coalesce(opBinding.getOperandType(0), typeFactory);
    // 1-arg case
    if (numArgs == 1) {
      return coalescedDataType;
    }
    // 2-arg case
    else {
      int ordinal = opBinding.getOperandLiteralValue(1, Integer.class);
      return coalescedDataType.getFieldList().get(ordinal).getType();
    }
  };

  public static final SqlReturnTypeInference ARRAY_OF_ARG0_TYPE =
      opBinding -> opBinding.getTypeFactory().createArrayType(opBinding.getOperandType(0), -1);

  public static SqlReturnTypeInference arrayOfType(final SqlTypeName typeName) {
    return opBinding -> opBinding.getTypeFactory()
        .createArrayType(opBinding.getTypeFactory().createSqlType(typeName), -1);
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

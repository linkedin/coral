/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlFunctionalOperator;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.TypeConverter;


/**
 * Calcite operator representation for Hive reflect function.
 * reflect(class,method[,arg1[,arg2..]]) calls method with reflection.
 */
public class HiveReflectOperator extends SqlFunctionalOperator {

  public static final HiveReflectOperator REFLECT = new HiveReflectOperator();

  public HiveReflectOperator() {
    super("reflect", SqlKind.OTHER_FUNCTION, 200, true, null, null, null);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    return callBinding.getOperandType(0).getSqlTypeName() == SqlTypeName.VARCHAR
        && callBinding.getOperandType(1).getSqlTypeName() == SqlTypeName.VARCHAR;
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.from(2);
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding sqlOperatorBinding) {
    int operandCount = sqlOperatorBinding.getOperandCount();
    ObjectInspector[] inputObjectInspectors = new ObjectInspector[operandCount];
    // Since `reflect` is implemented as a GenericUDF, we can use `HiveGenericUDFReturnTypeInference` to get its return type
    HiveGenericUDFReturnTypeInference hiveGenericUDFReturnTypeInference = new HiveGenericUDFReturnTypeInference(
        "org.apache.hadoop.hive.ql.udf.generic.GenericUDFReflect", ImmutableList.of("org.apache.hive:hive-exec:1.1.0"));

    // We need to reset the first two object inspectors because they are required to be instances of `StringObjectInspector`
    // Otherwise, the provided will be `JavaHiveCharObjectInspector` and can't pass the validation while calling `initialize` function
    // in `org.apache.hadoop.hive.ql.udf.generic.GenericUDFReflect`
    inputObjectInspectors[0] = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    inputObjectInspectors[1] = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    for (int i = 2; i < operandCount; i++) {
      TypeInfo typeInfo = TypeConverter.convert(sqlOperatorBinding.getOperandType(i));
      inputObjectInspectors[i] = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
    }
    return hiveGenericUDFReturnTypeInference.inferReturnType(inputObjectInspectors,
        sqlOperatorBinding.getTypeFactory());
  }
}

/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.TypeConverter;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.RelDataTypeToHiveTypeStringConverter;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility;
import com.linkedin.coral.spark.containers.SparkUDFInfo;


/**
 * This transformer transforms `extract_union` to `coalesce_struct`.
 * Instead of leaving `extract_union` visible to Spark, since we adopted the new exploded struct schema (a.k.a struct_tr)
 * that is different from extract_union's output (a.k.a struct_ex) to interpret union in Coral IR,
 * we need to swap the reference of `extract_union` to a new UDF that is coalescing the difference between
 * struct_tr and struct_ex.
 * See {@link CoalesceStructUtility#COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY} and its comments for more details.
 *
 * Check `CoralSparkTest#testUnionExtractUDF` for examples.
 *
 * Note that there is a Spark-specific mechanism that unwraps a single uniontype (a uniontype holding only one data type)
 * to simply the single underlying data type. This behavior is specific during the Avro schema to Spark schema conversion
 * in base tables. The problem with this behavior is we expect `coalesce_struct` to coalesce columns that originally contained
 * single uniontypes, yet lose this information after Spark gets rid of the uniontype. To work around this, we retain information
 * about the original schema and pass it to `coalesce_struct` UDF as a schema string.
 * Reference: https://spark.apache.org/docs/latest/sql-data-sources-avro.html#supported-types-for-avro---spark-sql-conversion
 *
 * For example, if we have an input SqlNode like so, where `col` is a uniontype column holding only string type:
 *  "SELECT extract_union(col) FROM table"
 *
 * This transformer would transform the above SqlNode to:
 *  "SELECT coalesce_struct(col, 'uniontype&lt;string&gt;') FROM table"
 *
 * Check `CoralSparkTest#testUnionExtractUDFOnSingleTypeUnions` for more examples including examples where we have single
 * uniontypes nested in a struct.
 *
 */
public class ExtractUnionFunctionTransformer extends SqlCallTransformer {
  private static final String EXTRACT_UNION = "extract_union";
  private static final String COALESCE_STRUCT = "coalesce_struct";

  private final Set<SparkUDFInfo> sparkUDFInfos;
  private static final RelDataTypeToHiveTypeStringConverter hiveTypeStringConverter =
      new RelDataTypeToHiveTypeStringConverter(true);

  public ExtractUnionFunctionTransformer(TypeDerivationUtil typeDerivationUtil, Set<SparkUDFInfo> sparkUDFInfos) {
    super(typeDerivationUtil);
    this.sparkUDFInfos = sparkUDFInfos;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return EXTRACT_UNION.equalsIgnoreCase(sqlCall.getOperator().getName());
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    sparkUDFInfos.add(new SparkUDFInfo("com.linkedin.coalescestruct.GenericUDFCoalesceStruct", "coalesce_struct",
        ImmutableList.of(URI.create("ivy://com.linkedin.coalesce-struct:coalesce-struct-impl:+")),
        SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));
    final List<SqlNode> operandList = sqlCall.getOperandList();
    final SqlOperator coalesceStructFunction =
        createSqlOperator(COALESCE_STRUCT, CoalesceStructUtility.COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY);
    if (operandList.size() == 1) {
      // one arg case: extract_union(field_name)
      RelDataType operandType = deriveRelDatatype(sqlCall.operand(0));

      if (containsSingleUnionType(operandType)) {
        // Pass in schema string to keep track of the original Hive schema containing single uniontypes so coalesce_struct
        // UDF knows which fields are unwrapped single uniontypes. This is needed otherwise coalesce_struct would
        // not coalesce the single uniontype fields as expected.
        String operandSchemaString = hiveTypeStringConverter.convertRelDataType(deriveRelDatatype(sqlCall.operand(0)));
        List<SqlNode> newOperandList = new ArrayList<>(operandList);
        newOperandList.add(SqlLiteral.createCharString(operandSchemaString, SqlParserPos.ZERO));
        return coalesceStructFunction.createCall(sqlCall.getParserPosition(), newOperandList);
      }

      return coalesceStructFunction.createCall(sqlCall.getParserPosition(), operandList);
    } else if (operandList.size() == 2) {
      // two arg case: extract_union(field_name, ordinal)
      final int newOrdinal = ((SqlNumericLiteral) operandList.get(1)).getValueAs(Integer.class) + 1;
      return coalesceStructFunction.createCall(sqlCall.getParserPosition(), ImmutableList.of(operandList.get(0),
          SqlNumericLiteral.createExactNumeric(String.valueOf(newOrdinal), SqlParserPos.ZERO)));
    } else {
      return sqlCall;
    }
  }

  private boolean containsSingleUnionType(RelDataType relDataType) {
    if (isSingleUnionType(relDataType)) {
      return true;
    }

    // Recursive case: if the current type is a struct, map or collection, check its nested types
    if (relDataType.isStruct()) {
      for (RelDataTypeField field : relDataType.getFieldList()) {
        if (containsSingleUnionType(field.getType())) {
          return true;
        }
      }
    } else if (relDataType.getKeyType() != null) {
      // For map type, check both key and value types
      if (containsSingleUnionType(relDataType.getKeyType()) || containsSingleUnionType(relDataType.getValueType())) {
        return true;
      }
    } else if (relDataType.getComponentType() != null) {
      // For collection type, check the component type
      if (containsSingleUnionType(relDataType.getComponentType())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Check if the given RelDataType is a single union type in Coral IR representation, the conversion to which happens in
   * {@link TypeConverter#convert(UnionTypeInfo, RelDataTypeFactory)}
   */
  private boolean isSingleUnionType(RelDataType relDataType) {
    return relDataType.isStruct() && relDataType.getFieldList().size() == 2
        && relDataType.getFieldList().get(0).getKey().equalsIgnoreCase("tag")
        && relDataType.getFieldList().get(1).getKey().equalsIgnoreCase("field0");
  }
}

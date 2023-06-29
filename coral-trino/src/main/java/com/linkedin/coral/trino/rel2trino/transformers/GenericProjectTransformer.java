/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.MapSqlType;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.trino.rel2trino.functions.RelDataTypeToTrinoTypeStringConverter;
import com.linkedin.coral.trino.rel2trino.functions.TrinoArrayTransformFunction;
import com.linkedin.coral.trino.rel2trino.functions.TrinoKeywordsConverter;
import com.linkedin.coral.trino.rel2trino.functions.TrinoMapTransformValuesFunction;
import com.linkedin.coral.trino.rel2trino.functions.TrinoStructCastRowFunction;

import static org.apache.calcite.sql.parser.SqlParserPos.*;


public class GenericProjectTransformer extends SqlCallTransformer {

  public GenericProjectTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator() instanceof GenericProjectFunction;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return convertGenericProject(sqlCall);
  }

  private SqlCall convertGenericProject(SqlCall call) {

    final SqlNode transformColumn = call.getOperandList().get(0);
    ImmutableList<String> transformColumnFieldFullName = ((SqlIdentifier) transformColumn).names;
    String transformColumnFieldName = transformColumnFieldFullName.get(transformColumnFieldFullName.size() - 1);

    RelDataType fromDataType = deriveRelDatatype(transformColumn);
    RelDataType toDataType = call.getOperator().inferReturnType(null);

    switch (toDataType.getSqlTypeName()) {
      case ROW:
        // Create a Trino CAST RexCall
        String structDataTypeArgumentString = structDataTypeArgumentString((RelRecordType) fromDataType,
            (RelRecordType) toDataType, transformColumnFieldName);
        SqlOperator structFunction = new TrinoStructCastRowFunction(toDataType);
        return structFunction.createCall(ZERO, new SqlIdentifier(structDataTypeArgumentString, ZERO));
      case ARRAY:
        // Create a Trino TRANSFORM RexCall
        String arrayDataTypeArgumentString = arrayDataTypeArgumentString((ArraySqlType) fromDataType,
            (ArraySqlType) toDataType, transformColumnFieldName);
        SqlOperator arrayFunction = new TrinoArrayTransformFunction(toDataType);
        return arrayFunction.createCall(ZERO, new SqlIdentifier(arrayDataTypeArgumentString, ZERO));
      case MAP:
        // Create a Trino TRANSFORM_VALUES RexCall
        String mapDataTypeArgumentString =
            mapDataTypeArgumentString((MapSqlType) fromDataType, (MapSqlType) toDataType, transformColumnFieldName);
        SqlOperator mapFunction = new TrinoMapTransformValuesFunction(toDataType);
        return mapFunction.createCall(ZERO, new SqlIdentifier(mapDataTypeArgumentString, ZERO));
      default:
        return call;
    }
  }

  /**
   * Create the transformation string for a map RelDataType to convert fromDataType to toDataType.
   * The transformation string follows the following format:
   *   TRANSFORM_VALUES([selected_map_field_reference], (k, v) -> [transform v as necessary])
   * @param fromDataType given map RelDataType
   * @param toDataType desired map RelDataType
   * @param fieldNameReference name of the field that references the map input
   * @return string denoting the UDF call applied to the map
   */
  private static String mapDataTypeString(MapSqlType fromDataType, MapSqlType toDataType, String fieldNameReference) {
    String mapDataTypeArgumentString = mapDataTypeArgumentString(fromDataType, toDataType, fieldNameReference);
    return String.format("transform_values(%s)", mapDataTypeArgumentString);
  }

  /**
   * Create the argument string to a TRANSFORM_VALUES() Trino UDF call to convert fromDataType to toDataType.
   * The argument string follows the following format:
   *   [selected_map_field_reference], (k, v) -> [transform v as necessary]
   * @param fromDataType given map RelDataType
   * @param toDataType desired map RelDataType
   * @param fieldNameReference name of the field that references the map input
   * @return string denoting the argument to a TRANSFORM_VALUES UDF call
   */
  private static String mapDataTypeArgumentString(MapSqlType fromDataType, MapSqlType toDataType,
      String fieldNameReference) {
    String mapKeyFieldReference = "k";
    String mapValueFieldReference = "v";
    String valueTransformedFieldString =
        relDataTypeFieldAccessString(fromDataType.getValueType(), toDataType.getValueType(), mapValueFieldReference);
    return String.format("%s, (%s, %s) -> %s", fieldNameReference, mapKeyFieldReference, mapValueFieldReference,
        valueTransformedFieldString);
  }

  /**
   * Create the transformation string for an array RelDataType to convert fromDataType to toDataType.
   * The transformation string follows the following format:
   *   TRANSFORM([selected_array_field_reference], x -> [transform x as necessary])
   * @param fromDataType given array RelDataType
   * @param toDataType desired array RelDataType
   * @param fieldNameReference name of the field that references the array input
   * @return string denoting the UDF call applied to the array
   */
  private static String arrayDataTypeString(ArraySqlType fromDataType, ArraySqlType toDataType,
      String fieldNameReference) {
    String arrayDataTypeArgumentString = arrayDataTypeArgumentString(fromDataType, toDataType, fieldNameReference);
    return String.format("transform(%s)", arrayDataTypeArgumentString);
  }

  /**
   * Create the argument string to a TRANSFORM() Trino UDF call to convert fromDataType to toDataType.
   * The argument string follows the following format:
   *   [selected_array_field_reference], x -> [transform x as necessary]
   * @param fromDataType given array RelDataType
   * @param toDataType desired array RelDataType
   * @param fieldNameReference name of the field that references the array input
   * @return string denoting the argument to a TRANSFORM UDF call
   */
  private static String arrayDataTypeArgumentString(ArraySqlType fromDataType, ArraySqlType toDataType,
      String fieldNameReference) {
    String elementFieldReference = "x";
    String elementTransformedFieldString = relDataTypeFieldAccessString(fromDataType.getComponentType(),
        toDataType.getComponentType(), elementFieldReference);
    return String.format("%s, %s -> %s", fieldNameReference, elementFieldReference, elementTransformedFieldString);
  }

  /**
   * Create the transformation string for a struct RelDataType to convert fromDataType to toDataType.
   * The transformation string follows the following format:
   *   CAST(ROW([selected_row_field_references]) AS ROW([selected_row_field_types]))
   * @param fromDataType given struct RelDataType
   * @param toDataType desired struct RelDataType
   * @param fieldNameReference name of the field that references the struct input
   * @return string denoting the UDF call applied to the struct
   */
  private static String structDataTypeString(RelRecordType fromDataType, RelRecordType toDataType,
      String fieldNameReference) {
    String structDataTypeArgumentString = structDataTypeArgumentString(fromDataType, toDataType, fieldNameReference);
    return (String.format("cast(%s)", structDataTypeArgumentString));
  }

  /**
   * Create the argument string to a CAST() Trino UDF call to convert fromDataType to toDataType.
   * The argument string follows the following format:
   *   ROW([selected_row_field_references]) AS ROW([selected_row_field_types])
   * @param fromDataType given array RelDataType
   * @param toDataType desired array RelDataType
   * @param fieldNameReference name of the field that references the struct input
   * @return string denoting the argument to a CAST UDF call
   */
  private static String structDataTypeArgumentString(RelRecordType fromDataType, RelRecordType toDataType,
      String fieldNameReference) {
    String structFieldsAccessString =
        buildStructRelDataTypeFieldAccessString(fromDataType, toDataType, fieldNameReference);
    String castToRowTypeString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(toDataType);
    return String.format("%s as %s", structFieldsAccessString, castToRowTypeString);
  }

  /**
   * Delegates a string builder to transform fromDataType to toDataType.
   * @param fromDataType given RelDataType
   * @param toDataType desired RelDataType
   * @param fieldNameReference name of the field reference
   * @return string denoting the argument to a CAST UDF call
   */
  private static String relDataTypeFieldAccessString(RelDataType fromDataType, RelDataType toDataType,
      String fieldNameReference) {
    if (fromDataType.equals(toDataType)) {
      return fieldNameReference;
    }

    switch (toDataType.getSqlTypeName()) {
      case ROW:
        return structDataTypeString((RelRecordType) fromDataType, (RelRecordType) toDataType, fieldNameReference);
      case ARRAY:
        return arrayDataTypeString((ArraySqlType) fromDataType, (ArraySqlType) toDataType, fieldNameReference);
      case MAP:
        return mapDataTypeString((MapSqlType) fromDataType, (MapSqlType) toDataType, fieldNameReference);
      default:
        return fieldNameReference;
    }
  }

  /**
   * Create a struct field access string in the form of:
   *   ROW([selected_col_field_1], [selected_col_field_2], etc.)
   * @param fromDataType given struct RelDataType
   * @param toDataType desired struct RelDataType
   * @param fieldNameReference name of the struct field reference
   * @return string denoting a row with all desired field accesses in toDataType derived from fromDataType
   */
  private static String buildStructRelDataTypeFieldAccessString(RelRecordType fromDataType, RelRecordType toDataType,
      String fieldNameReference) {
    List<String> structSelectedFieldStrings = new ArrayList<>();
    for (RelDataTypeField toDataTypeField : toDataType.getFieldList()) {
      RelDataTypeField fromDataTypeField = fromDataType.getField(toDataTypeField.getName(), false, false);
      if (fromDataTypeField == null) {
        throw new RuntimeException(
            String.format("Field %s was not found in column %s.", toDataTypeField.getName(), fieldNameReference));
      }
      String fromDataTypeFieldName =
          String.format("%s.%s", TrinoKeywordsConverter.quoteWordIfNotQuoted(fieldNameReference),
              TrinoKeywordsConverter.quoteWordIfNotQuoted(fromDataTypeField.getName()));
      structSelectedFieldStrings.add(
          relDataTypeFieldAccessString(fromDataTypeField.getType(), toDataTypeField.getType(), fromDataTypeFieldName));
    }

    String subFieldsString = String.join(", ", structSelectedFieldStrings);
    return String.format("row(%s)", subFieldsString);
  }
}

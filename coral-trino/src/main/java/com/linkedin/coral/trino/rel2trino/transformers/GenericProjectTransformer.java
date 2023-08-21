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


/**
 * GenericProjectTransformer converts Coral IR function `generic_project` to its Trino compatible representation.
 *
 * Trino does not support the GenericProject UDF which can be done in Hive and Spark because the return type of
 * GenericProject can only be determined at runtime based on the schema. Trino has compile-time validations that will
 * fail because the return type of GenericProject cannot be validated.
 *
 * At the language-level, we can apply a set of transformations over each data type to perform similar functionalities
 * to GenericProject by calling Trino built-in UDFs in conjunction:
 *   - struct/row
 *     - CAST(ROW([selected_row_field_references]) as ROW([selected_row_field_types]))
 *   - array
 *     - TRANSFORM([selected_array_field_reference], x -&gt; [transform x as necessary])
 *   - map
 *     - TRANSFORM_VALUES([selected_map_field_reference], (k, v) -&gt; [transform v as necessary])
 *   - primitives/other
 *     - [selected_field_reference]
 *
 * Some example of transformation strings are as follows:
 * NOTE: Assume the following format for a GenericProject call:
 *   - generic_project([actual_schema], [desired_schema]) from a column (col)
 *
 *   Example 1:
 *     - GenericProject call
 *       - generic_project(
 *           struct(a:int, b:int),
 *           struct(a:int)
 *         )
 *     - Trino rewrite
 *       - cast(row(col.a) as row(a int))
 *
 *   Example 2:
 *     - GenericProject call
 *       - generic_project(
 *           array(struct(a:int, b:int)),
 *           array(struct(a:int))
 *         )
 *     - Trino rewrite
 *       - transform(col, x -&gt; cast(row(x.a) as row(a int)))
 *
 *   Example 3:
 *     - GenericProject call
 *       - generic_project(
 *           map(string, struct(a:int, b:int)),
 *           map(string, struct(a:int))
 *         )
 *     - Trino rewrite
 *       - transform_values(col, (k, v) -&gt; cast(row(v.a) as row(a int)))
 *
 *   Example 4:
 *     - GenericProject call
 *       - generic_project(
 *           struct(a:int, s:struct(b:int, c:int)),
 *           struct(a:int, s:struct(b:int))
 *         )
 *     - Trino rewrite
 *       - cast(row(col.a, cast(row(col.s.b) as row(b int))) as row(a int, s row(b int)))
 *
 *   Example 5:
 *     - GenericProject call
 *       - generic_project(
 *           map(string, array(struct(a:int, s:struct(b:int, c:int)))),
 *           map(string, array(struct(a:int, s:struct(b:int))))
 *         )
 *     - Trino rewrite
 *       - transform_values(col, (k, v) -&gt;
 *           transform(v, x -&gt;
 *             cast(row(x.a, cast(row(x.s.b) as row(b int))) as row(a int, s row(b int)))
 *           )
 *         )
 */
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

  /**
   * Create a SqlCall that performs GenericProject-like operations for Trino depending on the given return type.
   * The Trino UDF for each data type is as follows:
   *   - struct/row
   *     - CAST
   *   - array
   *     - TRANSFORM
   *   - map
   *     - TRANSFORM_VALUES
   *   - other/primitives
   *     - N/A
   * @param call a GenericProject SqlCall
   * @return SqlCall a Trino UDF call equivalent for the given GenericProject call
   */
  private SqlCall convertGenericProject(SqlCall call) {

    // We build a SqlCall to a UDF based on the outermost return type.
    // Although other transformations may be necessary, we do not recursively build SqlCalls.
    // Instead, we represent these nested transformations as an input string to the top level SqlCall.
    //
    // This is necessary because it is hard to represent lambda expressions as input parameters in Calcite.
    // Since these UDF calls are only used in re-writers, we do not have to worry about external use-cases.
    //
    // This function internally does the following:
    // 1. Takes a SqlCall 'call' to 'generic_project(transformColumn, columnNameLiteral)
    //                  [SqlCall:generic_project(`arg_0`, `arg_1`)]
    //                  /                                         \
    // [SqlIdentifier:transformColumn]                [SqlLiteral:columnNameLiteral]
    // where:
    //  - transformColumn:
    //    - contains the fully qualified name of the input column and enables RelDataType derivation of this source column
    //  - columnNameLiteral:
    //    - the string name of the column
    //
    // 2. Transforms the original SqlCall 'call' to a modified call using a Trino UDF
    //               [SqlCall:Trino_UDF_call(`arg_0`)]
    //                                 |
    //            [SqlLiteral:input_transformation_string]
    //
    // A non-trivial example that requires more than one transformation illustrates the UDF call re-write and recursive
    // transformation string building:
    //
    //   Suppose we had an input column 'col1' of type 'transformColumn':
    //     map<string: array<struct<a int, b int>>>
    //   Suppose we wanted an output column of type:
    //     map<string: array<struct<a int>>>
    //
    // 1. The input SqlCall would look something like:
    //                  [SqlCall:generic_project(`arg_0`, `arg_1`)]
    //                  /                                         \
    // [SqlIdentifier:transformColumn]                    [SqlLiteral:'col1']
    // where:
    //  - transformColumn has type map<string: array<struct<a int, b int>>>
    //  - generic_project has type map<string: array<struct<a int>>>
    //
    // 2. The re-written transformed call would look something like:
    //              [SqlCall:transform_values(`arg_0`)]
    //                              |
    //           [SqlLiteral:input_transformation_string]
    // NOTE:
    //  - The transform_values UDF is chosen because the outermost data type is a map type.
    //  - The input_transformation_string is recursively built as follows:
    //        ['`arg_0`, (k, v) -> `arg_1`']
    //        /                            \
    //  ['col1']            [array: 'transform(`arg_0`, x -> `arg_1`)']
    //                     /                                          \
    //                 ['v']                  [struct: 'cast(row(`arg_0`) as row(`arg_1`) )']
    //                                        /                                              \
    //                                   [`arg_0`]                                     [`arg_0` `arg_1`]
    //                                      /                                          /               \
    //                                   ['x.a']                                   ['a']               ['int']
    //    Resolving for the transform_string gives:
    //      'col1, (k, v) -> transform(v , x -> cast(row(x.a) as row(a int)))'
    //
    // The final SqlCall will look like:
    //                             [SqlCall:transform_values(`arg_0`)]
    //                                              |
    //           [SqlLiteral: 'col1, (k, v) -> transform(v , x -> cast(row(x.a) as row(a int)))']
    // The resolved SQL string will look like:
    //   'transform_values(col1, (k, v) -> transform(v , x -> cast(row(x.a) as row(a int))))'
    final SqlNode transformColumn = call.getOperandList().get(0);
    ImmutableList<String> transformColumnFieldFullName = ((SqlIdentifier) transformColumn).names;
    String transformColumnFieldName = transformColumnFieldFullName.get(transformColumnFieldFullName.size() - 1);

    RelDataType fromDataType = deriveRelDatatype(transformColumn);
    RelDataType toDataType = call.getOperator().inferReturnType(null);

    switch (toDataType.getSqlTypeName()) {
      case ROW:
        // Create a Trino CAST SqlCall
        String structDataTypeArgumentString = structDataTypeArgumentString((RelRecordType) fromDataType,
            (RelRecordType) toDataType, transformColumnFieldName);
        SqlOperator structFunction = new TrinoStructCastRowFunction(toDataType);
        return structFunction.createCall(ZERO, new SqlIdentifier(structDataTypeArgumentString, ZERO));
      case ARRAY:
        // Create a Trino TRANSFORM SqlCall
        String arrayDataTypeArgumentString = arrayDataTypeArgumentString((ArraySqlType) fromDataType,
            (ArraySqlType) toDataType, transformColumnFieldName);
        SqlOperator arrayFunction = new TrinoArrayTransformFunction(toDataType);
        return arrayFunction.createCall(ZERO, new SqlIdentifier(arrayDataTypeArgumentString, ZERO));
      case MAP:
        // Create a Trino TRANSFORM_VALUES SqlCall
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
  private String mapDataTypeString(MapSqlType fromDataType, MapSqlType toDataType, String fieldNameReference) {
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
  private String mapDataTypeArgumentString(MapSqlType fromDataType, MapSqlType toDataType, String fieldNameReference) {
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
  private String arrayDataTypeString(ArraySqlType fromDataType, ArraySqlType toDataType, String fieldNameReference) {
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
  private String arrayDataTypeArgumentString(ArraySqlType fromDataType, ArraySqlType toDataType,
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
  private String structDataTypeString(RelRecordType fromDataType, RelRecordType toDataType, String fieldNameReference) {
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
  private String structDataTypeArgumentString(RelRecordType fromDataType, RelRecordType toDataType,
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
  private String relDataTypeFieldAccessString(RelDataType fromDataType, RelDataType toDataType,
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
  private String buildStructRelDataTypeFieldAccessString(RelRecordType fromDataType, RelRecordType toDataType,
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

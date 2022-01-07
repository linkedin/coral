/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.MapSqlType;


/**
 * GenericProjectToTrinoConverter takes a GenericProject RexCall call and rewrites its function call to be suitable
 * to Trino.
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
 *
 */
public class GenericProjectToTrinoConverter {
  private GenericProjectToTrinoConverter() {
  }

  /**
   * Create a RexCall that performs GenericProject-like operations for Trino depending on the given return type.
   * The Trino UDF for each data type is as follows:
   *   - struct/row
   *     - CAST
   *   - array
   *     - TRANSFORM
   *   - map
   *     - TRANSFORM_VALUES
   *   - other/primitives
   *     - N/A
   * @param builder RexBuilder for the call
   * @param call a GenericProject call
   * @param node parent relNode of call
   * @return a Trino UDF call equivalent for the given GenericProject call
   */
  public static RexCall convertGenericProject(RexBuilder builder, RexCall call, RelNode node) {
    // We build a RexCall to a UDF based on the outermost return type.
    // Although other transformations may be necessary, we do not recursively build RexCalls.
    // Instead, we represent these nested transformations as an input string to the top level RexCall.
    //
    // This is necessary because it is hard to represent lambda expressions as input parameters in Calcite.
    // Since these UDF calls are only used in re-writers, we do not have to worry about external use-cases.
    //
    // This function internally does the following:
    // 1. Takes a RexCall 'call' to 'generic_project(transformColumn, columnNameLiteral)
    //                  [RexCall:generic_project(`arg_0`, `arg_1`)]
    //                  /                                         \
    // [RexInputRef:transformColumn]                [RexLiteral:columnNameLiteral]
    // where:
    //  - transformColumn:
    //    - contains the type of the input column
    //  - columnNameLiteral:
    //    - the string name of the column
    //      - this is necessary because the transformColumn is represented as a numbered reference used by the
    //        RexBuilder
    //        - we cannot retrieve the name of the column from the RexBuilder from the numbered reference
    //
    // 2. Transforms the original RexCall 'call' to a transformed call using a Trino UDF
    //               [RexCall:Trino_UDF_call(`arg_0`)]
    //                                 |
    //            [RexLiteral:input_transformation_string]
    //
    // A non-trivial example that requires more than one transformation illustrates the UDF call re-write and recursive
    // transformation string building:
    //
    //   Suppose we had an input column 'col1' of type 'transformColumn':
    //     map<string: array<struct<a int, b int>>>
    //   Suppose we wanted an output column of type:
    //     map<string: array<struct<a int>>>
    //
    // 1. The RexCall 'call' would look something like:
    //                  [RexCall:generic_project(`arg_0`, `arg_1`)]
    //                  /                                         \
    // [RexInputRef:transformColumn]                    [RexLiteral:'col1']
    // where:
    //  - transformColumn has type map<string: array<struct<a int, b int>>>
    //  - generic_project has type map<string: array<struct<a int>>>
    //
    // 2. The re-written transformed call would look something like:
    //              [RexCall:transform_values(`arg_0`)]
    //                              |
    //           [RexLiteral:input_transformation_string]
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
    // The final RexCall will look like:
    //                             [RexCall:transform_values(`arg_0`)]
    //                                              |
    //           [RexLiteral: 'col1, (k, v) -> transform(v , x -> cast(row(x.a) as row(a int)))']
    // The resolved SQL string will look like:
    //   'transform_values(col1, (k, v) -> transform(v , x -> cast(row(x.a) as row(a int))))'

    RexLiteral columnNameLiteral = (RexLiteral) call.getOperands().get(1);
    String transformColumnFieldName = RexLiteral.stringValue(columnNameLiteral);
    final RexNode transformColumn = call.getOperands().get(0);

    // `transformColumnFieldName` could be the column name in an intermediate view, which might be different
    // from the base table's column name because of the alias operation. In this case, the translated SQL couldn't be resolved.
    // Therefore, we add the following `if` block to reset `transformColumnFieldName` if necessary.
    // It traverses the base nodes, and if the column type on the corresponding index equals to the type of `transformColumn`
    // but the column name is different from `transformColumnFieldName`, we reset `transformColumnFieldName`
    // Corresponding unit test is `HiveToTrinoConverterTest#testResetTransformColumnFieldNameForGenericProject`
    if (transformColumn instanceof RexInputRef) {
      int columnIndexInBaseTable = ((RexInputRef) transformColumn).getIndex();
      Deque<RelNode> nodeDeque = new LinkedList<>(node.getInputs());
      while (!nodeDeque.isEmpty()) {
        RelNode currentNode = nodeDeque.pollFirst();
        final List<RelDataTypeField> fieldList = currentNode.getRowType().getFieldList();
        if (columnIndexInBaseTable < fieldList.size()) {
          final RelDataTypeField relDataTypeField = fieldList.get(columnIndexInBaseTable);
          if (relDataTypeField.getType() == transformColumn.getType()
              && !transformColumnFieldName.equalsIgnoreCase(relDataTypeField.getName())) {
            transformColumnFieldName = relDataTypeField.getName();
            break;
          }
        }
        nodeDeque.addAll(currentNode.getInputs());
      }
    }

    RelDataType fromDataType = transformColumn.getType();
    RelDataType toDataType = call.getOperator().inferReturnType(null);
    switch (toDataType.getSqlTypeName()) {
      case ROW:
        // Create a Trino CAST RexCall
        String structDataTypeArgumentString = structDataTypeArgumentString((RelRecordType) fromDataType,
            (RelRecordType) toDataType, transformColumnFieldName);
        SqlOperator structFunction = new TrinoStructCastRowFunction(toDataType);
        return (RexCall) builder.makeCall(structFunction, builder.makeLiteral(structDataTypeArgumentString));
      case ARRAY:
        // Create a Trino TRANSFORM RexCall
        String arrayDataTypeArgumentString = arrayDataTypeArgumentString((ArraySqlType) fromDataType,
            (ArraySqlType) toDataType, transformColumnFieldName);
        SqlOperator arrayFunction = new TrinoArrayTransformFunction(toDataType);
        return (RexCall) builder.makeCall(arrayFunction, builder.makeLiteral(arrayDataTypeArgumentString));
      case MAP:
        // Create a Trino TRANSFORM_VALUES RexCall
        String mapDataTypeArgumentString =
            mapDataTypeArgumentString((MapSqlType) fromDataType, (MapSqlType) toDataType, transformColumnFieldName);
        SqlOperator mapFunction = new TrinoMapTransformValuesFunction(toDataType);
        return (RexCall) builder.makeCall(mapFunction, builder.makeLiteral(mapDataTypeArgumentString));
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

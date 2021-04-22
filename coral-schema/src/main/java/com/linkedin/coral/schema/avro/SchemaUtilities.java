/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.base.Strings;
import com.linkedin.coral.schema.avro.exceptions.SchemaNotFoundException;


class SchemaUtilities {
  private static final Logger LOG = LoggerFactory.getLogger(SchemaUtilities.class);
  private static final String DALI_ROW_SCHEMA = "dali.row.schema";

  // private constructor for utility class
  private SchemaUtilities() {
  }

  /**
   * This method return case preserved avro schema including partition columns for table
   *
   * @param table
   * @return case preserved avro schema for table including partition columns
   */
  static Schema getCasePreservedSchemaForTable(@Nonnull final Table table) {
    Preconditions.checkNotNull(table);
    Schema avroSchema = getCasePreservedSchemaFromTblProperties(table);

    if (avroSchema == null) {
      return null;
    }

    // add partition columns to schema if table is partitioned
    Schema tableSchema = addPartitionColsToSchema(avroSchema, table);

    return tableSchema;
  }

  /**
   * This method return avro schema including partition columns for table
   *
   * If avro schema exists in table properties, retrieve it from table properties
   * Otherwise, avro schema is converted from hive schema
   *
   * @param table
   * @param strictMode if set to true, we do not fall back to Hive schema
   * @return Avro schema for table including partition columns
   */
  static Schema getAvroSchemaForTable(@Nonnull final Table table, boolean strictMode) {
    Preconditions.checkNotNull(table);
    Schema tableSchema = SchemaUtilities.getCasePreservedSchemaForTable(table);
    if (tableSchema == null) {
      if (!strictMode) {
        LOG.warn("Cannot determine Avro schema for table " + table.getDbName() + "." + table.getTableName() + ". "
            + "Deriving Avro schema from Hive schema for that table. "
            + "Please note every field will have lower-cased name and be nullable");

        tableSchema = SchemaUtilities.convertHiveSchemaToAvro(table);
      } else {
        throw new SchemaNotFoundException("strictMode is set to True and fallback to Hive schema is disabled. "
            + "Cannot determine Avro schema for table " + table.getDbName() + "." + table.getTableName() + ".");
      }
    }

    return tableSchema;
  }

  static Schema convertHiveSchemaToAvro(@Nonnull final Table table) {
    Preconditions.checkNotNull(table);

    String recordName = table.getTableName();
    String recordNamespace = table.getDbName() + "." + recordName;

    final List<FieldSchema> cols = new ArrayList<>();

    cols.addAll(table.getSd().getCols());
    if (isPartitioned(table)) {
      cols.addAll(getPartitionCols(table));
    }

    return convertFieldSchemaToAvroSchema(recordName, recordNamespace, true, cols);
  }

  /**
   * Returns case sensitive schema from table properties or null if not present
   *
   * Note: This method is modified based on SchemaUtilities in Dali codebase
   *
   * @param table
   * @return Avro schema stored under 'avro.schema.literal', under 'dali.row.schema',
   * or null if none of the above are present
   */
  static Schema getCasePreservedSchemaFromTblProperties(@Nonnull final Table table) {
    Preconditions.checkNotNull(table);

    // First try avro.schema.literal
    String schemaStr = readSchemaFromSchemaLiteral(table);

    // Then, try dali.row.schema
    if (Strings.isNullOrEmpty(schemaStr)) {
      schemaStr = table.getParameters().get(DALI_ROW_SCHEMA);
    }

    if (!Strings.isNullOrEmpty(schemaStr)) {
      LOG.info("Schema found for table {}", getCompleteName(table));
      LOG.debug("Schema is {}", schemaStr);
      return new Schema.Parser().parse(schemaStr);
    } else {
      LOG.warn("Cannot determine avro schema for table {}", getCompleteName(table));
      return null;
    }
  }

  static void appendField(@Nonnull Schema.Field field, @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(fieldAssembler);

    JsonNode defaultValue = field.defaultValue();

    SchemaBuilder.GenericDefault genericDefault = fieldAssembler.name(field.name()).type(field.schema());
    if (defaultValue != null) {
      genericDefault.withDefault(defaultValue);
    } else {
      genericDefault.noDefault();
    }
  }

  /**
   * This method appends a field to avro schema SchemaBuilder.FieldAssembler<Schema>
   *
   * @param fieldName
   * @param fieldRelDataType
   * @param fieldAssembler
   */
  static void appendField(@Nonnull String fieldName, @Nonnull RelDataType fieldRelDataType,
      @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler, @Nonnull boolean isNullable) {
    Preconditions.checkNotNull(fieldName);
    Preconditions.checkNotNull(fieldRelDataType);
    Preconditions.checkNotNull(fieldAssembler);

    Schema fieldSchema = RelDataTypeToAvroType.relDataTypeToAvroTypeNonNullable(fieldRelDataType, fieldName);

    // TODO: handle default value properly
    if (isNullable && fieldSchema.getType() != Schema.Type.NULL) {
      Schema fieldSchemaNullable = Schema.createUnion(Arrays.asList(fieldSchema, Schema.create(Schema.Type.NULL)));
      fieldAssembler.name(fieldName).type(fieldSchemaNullable).noDefault();
    } else {
      fieldAssembler.name(fieldName).type(fieldSchema).noDefault();
    }
  }

  static boolean isFieldNullable(@Nonnull RexCall rexCall, @Nonnull Schema inputSchema) {
    Preconditions.checkNotNull(rexCall);
    Preconditions.checkNotNull(inputSchema);

    // the field is non-nullable only if all operands are RexInputRef
    // and corresponding field schema type of RexInputRef index is not UNION
    List<RexNode> operands = rexCall.getOperands();
    for (RexNode operand : operands) {
      if (operand instanceof RexInputRef) {
        Schema.Field field = inputSchema.getFields().get(((RexInputRef) operand).getIndex());
        if (Schema.Type.UNION.equals(field.schema().getType())) {
          return true;
        }
      } else if (operand instanceof RexCall) {
        boolean isNullable = isFieldNullable((RexCall) operand, inputSchema);
        if (isNullable) {
          return true;
        }
      } else {
        return true;
      }
    }

    return false;
  }

  static void appendField(@Nonnull String fieldName, @Nonnull Schema.Field field,
      @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
    Preconditions.checkNotNull(fieldName);
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(fieldAssembler);

    JsonNode defaultValue = field.defaultValue();

    SchemaBuilder.GenericDefault genericDefault = fieldAssembler.name(fieldName).type(field.schema());
    if (defaultValue != null) {
      genericDefault.withDefault(defaultValue);
    } else {
      genericDefault.noDefault();
    }
  }

  static String getFieldName(String oldName, String suggestedNewName) {
    Preconditions.checkNotNull(oldName);
    Preconditions.checkNotNull(suggestedNewName);

    String newName = suggestedNewName;
    if (suggestedNewName.equals(oldName.toLowerCase())) {
      // we do not allow renaming the field to all lower-casing compared to original name. Say Id to id
      // since we cannot distinguish the lower-casing behavior introduced by users and engines
      newName = oldName;
    } else if (suggestedNewName.contains("$")) {
      newName = toAvroQualifiedName(suggestedNewName);
    }

    return newName;
  }

  static String toAvroQualifiedName(@Nonnull String name) {
    Preconditions.checkNotNull(name);
    return name.replace("$", "_");
  }

  static boolean isPartitioned(@Nonnull Table tableOrView) {
    Preconditions.checkNotNull(tableOrView);

    List<FieldSchema> partitionColumns = getPartitionCols(tableOrView);

    if (partitionColumns == null) {
      return false;
    }

    return (partitionColumns.size() != 0);
  }

  private static List<Schema.Field> cloneFieldList(List<Schema.Field> fieldList, boolean isPartCol) {
    List<Schema.Field> result = new ArrayList<>();
    for (Schema.Field field : fieldList) {
      String fieldDoc = isPartCol ? "This is the partition column. "
          + "Partition columns, if present in the schema, should also be projected in the data." : field.doc();
      Schema.Field clonedField =
          new Schema.Field(field.name(), field.schema(), fieldDoc, field.defaultValue(), field.order());
      // Copy field level properties, which could be critical for things like logical type.
      for (Map.Entry<String, JsonNode> prop : field.getJsonProps().entrySet()) {
        clonedField.addProp(prop.getKey(), prop.getValue());
      }
      result.add(clonedField);
    }
    return result;
  }

  /**
   * Exposed method for cloning fieldList as `isPartCol=false` is an internal case.
   */
  @VisibleForTesting
  static List<Schema.Field> cloneFieldList(List<Schema.Field> fieldList) {
    return cloneFieldList(fieldList, false);
  }

  static void replicateSchemaProps(Schema srcSchema, Schema targetSchema) {
    for (Map.Entry<String, JsonNode> prop : srcSchema.getJsonProps().entrySet()) {
      if (targetSchema.getProp(prop.getKey()) == null) {
        targetSchema.addProp(prop.getKey(), prop.getValue());
      }
    }
  }

  static Schema addPartitionColsToSchema(@Nonnull Schema schema, @Nonnull Table tableOrView) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(tableOrView);

    if (!isPartitioned(tableOrView)) {
      return schema;
    }

    Schema partitionColumnsSchema =
        convertFieldSchemaToAvroSchema("partitionCols", "partitionCols", false, tableOrView.getPartitionKeys());

    List<Schema.Field> fieldsWithPartitionColumns = cloneFieldList(schema.getFields());
    fieldsWithPartitionColumns.addAll(cloneFieldList(partitionColumnsSchema.getFields(), true));

    Schema schemaWithPartitionColumns =
        Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), schema.isError());
    schemaWithPartitionColumns.setFields(fieldsWithPartitionColumns);

    // Copy schema level properties
    replicateSchemaProps(schema, schemaWithPartitionColumns);

    return schemaWithPartitionColumns;
  }

  static Schema setupNameAndNamespace(@Nonnull Schema schema, @Nonnull String schemaName,
      @Nonnull String schemaNamespace) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(schemaName);
    Preconditions.checkNotNull(schemaNamespace);

    // setup name
    Schema schemaWithProperName = setupTopLevelRecordName(schema, schemaName);

    // setup nested namespace
    Schema schmeWithProperNamespace = setupNestedNamespaceForRecord(schemaWithProperName, schemaNamespace);

    return schmeWithProperNamespace;
  }

  static Schema joinSchemas(@Nonnull Schema leftSchema, @Nonnull Schema rightSchema) {
    Preconditions.checkNotNull(leftSchema);
    Preconditions.checkNotNull(rightSchema);

    List<Schema.Field> combinedSchemaFields = cloneFieldList(leftSchema.getFields());
    combinedSchemaFields.addAll(cloneFieldList(rightSchema.getFields()));

    Schema combinedSchema =
        Schema.createRecord(leftSchema.getName(), leftSchema.getDoc(), leftSchema.getNamespace(), leftSchema.isError());
    combinedSchema.setFields(combinedSchemaFields);
    // In case there are conflicts of property values among leftSchema and rightSchema, the former-applied leftSchema
    // will be the winner as Schema object doesn't support prop-overwrite.
    replicateSchemaProps(leftSchema, combinedSchema);
    replicateSchemaProps(rightSchema, combinedSchema);

    return combinedSchema;
  }

  /**
   * This method merges two input schemas of LogicalUnion operator, or throws exception if they can't be merged.
   *
   * @param leftSchema Left schema to be merged
   * @param rightSchema Right schema to be merged
   * @param strictMode If set to true, namespaces are required to be same and the any mismatch of field types will
   *                   cause an exception.
   *                   If set to false, we don't check namespace and the following union of field types is allowed.
   *                   null UNION ALL int -> [null, int]
   *                   null UNION ALL [null, int] -> [null, int]
   *                   int UNION ALL [null, int] -> [null, int]
   *
   * @return Merged schema if the input schemas can be merged
   */
  static Schema mergeUnionRecordSchema(@Nonnull Schema leftSchema, @Nonnull Schema rightSchema, boolean strictMode) {
    Preconditions.checkNotNull(leftSchema);
    Preconditions.checkNotNull(rightSchema);
    if (leftSchema.toString(true).equals(rightSchema.toString(true))) {
      return leftSchema;
    }

    List<Schema.Field> leftSchemaFields = leftSchema.getFields();
    List<Schema.Field> rightSchemaFields = rightSchema.getFields();

    if (strictMode) {
      // we require namespace matches in strictMode
      if (!Objects.equals(leftSchema.getNamespace(), rightSchema.getNamespace())) {
        throw new RuntimeException("Found namespace mismatch while configured with strict mode. " + "Namespace for "
            + leftSchema.getName() + " is: " + leftSchema.getNamespace() + ". " + "Namespace for "
            + rightSchema.getName() + " is: " + rightSchema.getNamespace());
      }
    }

    Map<String, Schema.Field> leftSchemaFieldsMap =
        leftSchemaFields.stream().collect(Collectors.toMap(Schema.Field::name, Function.identity()));
    Map<String, Schema.Field> rightSchemaFieldsMap =
        rightSchemaFields.stream().collect(Collectors.toMap(Schema.Field::name, Function.identity()));

    for (Schema.Field field : leftSchemaFields) {
      if (!rightSchemaFieldsMap.containsKey(field.name())) {
        // field in leftSchema is missing in rightSchema
        throw new RuntimeException(
            field.name() + " is in schema " + leftSchema.getName() + ": " + leftSchema.toString(true)
                + ", but not in schema " + rightSchema.getName() + ": " + rightSchema.toString(true));
      }
    }

    for (Schema.Field field : rightSchemaFields) {
      if (!leftSchemaFieldsMap.containsKey(field.name())) {
        // field in rightSchema is missing in leftSchema
        throw new RuntimeException(
            field.name() + " is in schema " + rightSchema.getName() + ": " + rightSchema.toString(true)
                + ", but not in schema " + leftSchema.getName() + ": " + leftSchema.toString(true));
      }
    }

    List<Schema.Field> mergedSchemaFields = new ArrayList<>();

    for (Schema.Field leftField : leftSchemaFields) {
      Schema.Field rightField = rightSchemaFieldsMap.get(leftField.name());
      Schema unionFieldSchema = getUnionFieldSchema(leftField.schema(), rightField.schema(), strictMode);
      if (unionFieldSchema == null) { // types of leftField and rightField are not compatible
        throw new RuntimeException(leftField.name() + " is not compatible with " + rightField.name()
            + " for LogicalUnion operator. " + "inputSchema1 is: " + leftSchema.toString(true) + ", "
            + "inputSchema2 is: " + rightSchema.toString(true));
      }
      Schema.Field unionField = new Schema.Field(leftField.name(), unionFieldSchema, leftField.doc(),
          leftField.defaultValue(), leftField.order());
      leftField.aliases().forEach(unionField::addAlias);
      leftField.getJsonProps().forEach(unionField::addProp);
      mergedSchemaFields.add(unionField);
    }
    Schema schema = Schema.createRecord(leftSchema.getName(), leftSchema.getDoc(), leftSchema.getNamespace(), false);
    schema.setFields(mergedSchemaFields);
    return schema;
  }

  private static Schema getUnionFieldSchema(@Nonnull Schema leftSchema, @Nonnull Schema rightSchema,
      boolean strictMode) {
    Preconditions.checkNotNull(leftSchema);
    Preconditions.checkNotNull(rightSchema);

    // Because we use leftSchema as the base of `switch`, we need to exchange leftSchema and rightSchema if rightSchema is
    // a special type (null or union) and leftSchema is not.
    if (isUnionOrNull(rightSchema) && !isUnionOrNull(leftSchema)) {
      return getUnionFieldSchema(rightSchema, leftSchema, strictMode);
    }

    switch (leftSchema.getType()) {
      case BOOLEAN:
      case BYTES:
      case DOUBLE:
      case FLOAT:
      case INT:
      case LONG:
      case STRING:
        return leftSchema.getType() == rightSchema.getType() ? leftSchema : null;
      case FIXED:
        boolean isSameType = leftSchema.getType() == rightSchema.getType();
        boolean isSameNamespace = Objects.equals(leftSchema.getNamespace(), rightSchema.getNamespace());

        return isSameType && (!strictMode || isSameNamespace) ? leftSchema : null;
      case ENUM:
        if (leftSchema.getType() != rightSchema.getType()) {
          return null;
        }
        boolean isSameSymbolSize = (leftSchema.getEnumSymbols().size() == rightSchema.getEnumSymbols().size());
        boolean isSameEnumNamespace = Objects.equals(leftSchema.getNamespace(), rightSchema.getNamespace());

        return isSameSymbolSize && (!strictMode || isSameEnumNamespace) ? leftSchema : null;
      case RECORD:
        return leftSchema.getType() == rightSchema.getType()
            ? mergeUnionRecordSchema(leftSchema, rightSchema, strictMode) : null;

      case MAP:
        if (leftSchema.getType() != rightSchema.getType()) {
          return null;
        }
        Schema valueType = getUnionFieldSchema(leftSchema.getValueType(), rightSchema.getValueType(), strictMode);
        return valueType != null ? Schema.createMap(valueType) : null;

      case ARRAY:
        if (leftSchema.getType() != rightSchema.getType()) {
          return null;
        }
        Schema elementType = getUnionFieldSchema(leftSchema.getElementType(), rightSchema.getElementType(), strictMode);
        return elementType != null ? Schema.createArray(elementType) : null;

      case UNION:
        if (leftSchema.getType() == rightSchema.getType()) {
          boolean isBothNullableType =
              AvroSerdeUtils.isNullableType(leftSchema) && AvroSerdeUtils.isNullableType(rightSchema);

          Schema leftOtherType = AvroSerdeUtils.getOtherTypeFromNullableType(leftSchema);
          Schema rightOtherType = AvroSerdeUtils.getOtherTypeFromNullableType(rightSchema);
          final Schema unionFieldSchema = getUnionFieldSchema(leftOtherType, rightOtherType, strictMode);
          return isBothNullableType && unionFieldSchema != null
              ? Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), unionFieldSchema)) : null;
        } else {
          if (AvroSerdeUtils.isNullableType(leftSchema)) {
            Schema leftOtherType = AvroSerdeUtils.getOtherTypeFromNullableType(leftSchema);
            return rightSchema.getType() == Schema.Type.NULL
                || getUnionFieldSchema(leftOtherType, rightSchema, strictMode) != null ? leftSchema : null;
          }
          return null;
        }
      case NULL:
        if (rightSchema.getType() == Schema.Type.UNION) {
          return AvroSerdeUtils.isNullableType(rightSchema) ? rightSchema : null;
        } else if (rightSchema.getType() == Schema.Type.NULL) {
          return leftSchema;
        } else {
          return Schema.createUnion(Arrays.asList(leftSchema, rightSchema));
        }
      default:
        throw new IllegalArgumentException(
            "Unsupported Avro type " + leftSchema.getType() + " in schema: " + leftSchema.toString(true));
    }
  }

  private static boolean isUnionOrNull(Schema schema) {
    return schema.getType() == Schema.Type.UNION || schema.getType() == Schema.Type.NULL;
  }

  private static void appendFieldWithNewNamespace(@Nonnull Schema.Field field, @Nonnull String namespace,
      @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(namespace);
    Preconditions.checkNotNull(fieldAssembler);

    Schema fieldSchema = field.schema();
    switch (field.schema().getType()) {
      case ENUM:
        fieldSchema =
            Schema.createEnum(fieldSchema.getName(), fieldSchema.getDoc(), namespace, fieldSchema.getEnumSymbols());
        break;
      default:
        break;
    }

    JsonNode defaultValue = field.defaultValue();
    SchemaBuilder.GenericDefault genericDefault = fieldAssembler.name(field.name()).type(fieldSchema);
    if (defaultValue != null) {
      genericDefault.withDefault(defaultValue);
    } else {
      genericDefault.noDefault();
    }
  }

  private static Schema setupNestedNamespaceForRecord(@Nonnull Schema schema, @Nonnull String namespace) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(namespace);

    if (!schema.getType().equals(Schema.Type.RECORD)) {
      throw new IllegalArgumentException(
          "Input schemas must be of RECORD type. " + "The actual type is: " + schema.getType());
    }

    SchemaBuilder.FieldAssembler<Schema> fieldAssembler =
        SchemaBuilder.record(schema.getName()).namespace(namespace).fields();

    String nestedNamespace = namespace + "." + schema.getName();

    for (Schema.Field field : schema.getFields()) {
      switch (field.schema().getType()) {
        case BOOLEAN:
        case BYTES:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case STRING:
        case FIXED:
        case NULL:
          // TODO: verify whether FIXED type has namespace
          appendField(field, fieldAssembler);
          break;
        case MAP:
          Schema newMapFieldSchema = setupNestedNamespace(field.schema(), nestedNamespace);
          Schema.Field newMapField =
              new Schema.Field(field.name(), newMapFieldSchema, field.doc(), field.defaultValue(), field.order());
          appendField(newMapField, fieldAssembler);
          break;
        case ARRAY:
          Schema newArrayFieldSchema = setupNestedNamespace(field.schema(), nestedNamespace);
          Schema.Field newArrayField =
              new Schema.Field(field.name(), newArrayFieldSchema, field.doc(), field.defaultValue(), field.order());
          appendField(newArrayField, fieldAssembler);
          break;
        case ENUM:
          appendFieldWithNewNamespace(field, nestedNamespace, fieldAssembler);
          break;
        case RECORD:
          Schema recordSchemaWithNestedNamespace = setupNestedNamespaceForRecord(field.schema(), nestedNamespace);
          Schema.Field newRecordFiled = new Schema.Field(field.name(), recordSchemaWithNestedNamespace, field.doc(),
              field.defaultValue(), field.order());
          appendField(newRecordFiled, fieldAssembler);
          break;
        case UNION:
          Schema unionSchemaWithNestedNamespace = setupNestedNamespace(field.schema(), nestedNamespace);
          Schema.Field newUnionField = new Schema.Field(field.name(), unionSchemaWithNestedNamespace, field.doc(),
              field.defaultValue(), field.order());
          appendField(newUnionField, fieldAssembler);
          break;
        default:
          throw new IllegalArgumentException("Unsupported Schema type: " + field.schema().getType().toString());
      }
    }

    return fieldAssembler.endRecord();
  }

  private static Schema setupNestedNamespace(@Nonnull Schema schema, @Nonnull String namespace) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(namespace);

    switch (schema.getType()) {
      case BOOLEAN:
      case BYTES:
      case DOUBLE:
      case FLOAT:
      case INT:
      case LONG:
      case STRING:
      case FIXED:
        // TODO: verify whether FIXED type has namespace
        return schema;
      case MAP:
        Schema valueSchema = schema.getValueType();
        Schema valueSchemaWithNestedNamespace = setupNestedNamespace(valueSchema, namespace);
        Schema mapSchema = Schema.createMap(valueSchemaWithNestedNamespace);

        return mapSchema;
      case ARRAY:
        Schema elementSchema = schema.getElementType();
        Schema elementSchemaWithNestedNamespace = setupNestedNamespace(elementSchema, namespace);
        Schema arraySchema = Schema.createArray(elementSchemaWithNestedNamespace);

        return arraySchema;
      case ENUM:
        Schema enumSchemaWithNestedNamespace =
            Schema.createEnum(schema.getName(), schema.getDoc(), namespace, schema.getEnumSymbols());

        return enumSchemaWithNestedNamespace;
      case RECORD:
        Schema recordSchema = setupNestedNamespaceForRecord(schema, namespace);
        return recordSchema;
      case UNION:
        if (AvroSerdeUtils.isNullableType(schema)) {
          Schema otherType = AvroSerdeUtils.getOtherTypeFromNullableType(schema);
          Schema otherTypeWithNestedNamespace = setupNestedNamespace(otherType, namespace);
          Schema nullSchema = Schema.create(Schema.Type.NULL);
          List<Schema> types = new ArrayList<>();
          types.add(nullSchema);
          types.add(otherTypeWithNestedNamespace);
          Schema unionSchema = Schema.createUnion(types);

          return unionSchema;
        } else {
          throw new IllegalArgumentException(
              schema.toString(true) + " is unsupported UNION type. " + "Only nullable UNION is supported");
        }
      default:
        throw new IllegalArgumentException("Unsupported Schema type: " + schema.getType().toString());
    }
  }

  private static Schema setupTopLevelRecordName(@Nonnull Schema schema, @Nonnull String schemaName) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(schemaName);

    Schema avroSchema = Schema.createRecord(schemaName, schema.getDoc(), schema.getNamespace(), schema.isError());

    List<Schema.Field> fields = cloneFieldList(schema.getFields());
    avroSchema.setFields(fields);

    return avroSchema;
  }

  private static Schema convertFieldSchemaToAvroSchema(@Nonnull final String recordName,
      @Nonnull final String recordNamespace, @Nonnull final boolean mkFieldsOptional,
      @Nonnull final List<FieldSchema> columns) {
    Preconditions.checkNotNull(recordName);
    Preconditions.checkNotNull(recordNamespace);
    Preconditions.checkNotNull(mkFieldsOptional);
    Preconditions.checkNotNull(columns);

    final List<String> columnNames = new ArrayList<>(columns.size());
    final List<TypeInfo> columnsTypeInfo = new ArrayList<>(columns.size());

    columns.forEach(fs -> {
      columnNames.add(fs.getName());
      columnsTypeInfo.add(TypeInfoUtils.getTypeInfoFromTypeString(fs.getType()));
    });

    return new TypeInfoToAvroSchemaConverter(recordNamespace, mkFieldsOptional).convertFieldsTypeInfoToAvroSchema("",
        SchemaUtilities.getStandardName(recordName), columnNames, columnsTypeInfo);
  }

  private static List<FieldSchema> getPartitionCols(@Nonnull Table tableOrView) {
    Preconditions.checkNotNull(tableOrView);

    List<FieldSchema> partKeys = tableOrView.getPartitionKeys();
    if (partKeys == null) {
      partKeys = new ArrayList<>();
      tableOrView.setPartitionKeys(partKeys);
    }

    return partKeys;
  }

  /**
   * Note: This method is modified based on SchemaUtilities in Dali codebase
   *
   * @param table
   * @return
   */
  private static String readSchemaFromSchemaLiteral(@Nonnull Table table) {
    Preconditions.checkNotNull(table);

    String schemaStr = table.getParameters().get(AvroSerdeUtils.AVRO_SCHEMA_LITERAL);
    if (Strings.isNullOrEmpty(schemaStr)) {
      schemaStr = table.getSd().getSerdeInfo().getParameters().get(AvroSerdeUtils.AVRO_SCHEMA_LITERAL);
    }

    if (Strings.isNullOrEmpty(schemaStr)) {
      LOG.debug("No avro schema defined under table or serde property {} for table {}",
          AvroSerdeUtils.AVRO_SCHEMA_LITERAL, getCompleteName(table));
    }

    return schemaStr;
  }

  private static String getCompleteName(@Nonnull Table table) {
    Preconditions.checkNotNull(table);

    return table.getDbName() + "@" + table.getTableName();
  }

  private static String getStandardName(@Nonnull String name) {
    Preconditions.checkNotNull(name);

    String[] sArr = name.split("_");
    StringBuilder sb = new StringBuilder();
    for (String str : sArr) {
      sb.append(StringUtils.capitalize(str));
    }
    return sb.toString();
  }
}

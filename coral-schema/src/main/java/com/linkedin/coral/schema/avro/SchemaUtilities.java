/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.avro.AvroSerdeUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class SchemaUtilities {
  private static final Logger LOG = LoggerFactory.getLogger(SchemaUtilities.class);
  private static final String DALI_ROW_SCHEMA = "dali.row.schema";

  // private constructor for utility class
  private SchemaUtilities() {
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

  static void appendField(@Nonnull Schema.Field field,
      @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
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
  static void appendField(@Nonnull String fieldName,
      @Nonnull RelDataType fieldRelDataType,
      @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
    Preconditions.checkNotNull(fieldName);
    Preconditions.checkNotNull(fieldRelDataType);
    Preconditions.checkNotNull(fieldAssembler);

    Schema fieldSchema = RelDataTypeToAvroType.relDataTypeToAvroType(fieldRelDataType);

    // TODO: currently mark everything field transformed by Rel operators and udfs as nullable for now
    // Should handle nullability properly later
    // TODO: handle default value properly
    Schema fieldSchemaNullable = Schema.createUnion(Arrays.asList(fieldSchema, Schema.create(Schema.Type.NULL)));
    fieldAssembler.name(fieldName).type(fieldSchemaNullable).noDefault();
  }


  static void appendField(@Nonnull String fieldName,
      @Nonnull Schema.Field field,
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

  static Schema addPartitionColsToSchema(@Nonnull Schema schema, @Nonnull Table tableOrView) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(tableOrView);

    Schema partitionColumnsSchema = convertFieldSchemaToAvroSchema("partitionCols",
                                                               "partitionCols",
                                                                false,
                                                                              tableOrView.getPartitionKeys());

    List<Schema.Field> fieldsWithPartitionColumns = new ArrayList<>();

    for (Schema.Field field : schema.getFields()) {
      fieldsWithPartitionColumns.add(new Schema.Field(field.name(),
                                                      field.schema(),
                                                      field.doc(),
                                                      field.defaultValue(),
                                                      field.order()));
    }

    for (Schema.Field field : partitionColumnsSchema.getFields()) {
      fieldsWithPartitionColumns.add(new Schema.Field(field.name(),
          field.schema(),
          "This is the partition column. "
          + "Partition columns, if present in the schema, should also be projected in the data.",
          field.defaultValue(),
          field.order()));
    }

    Schema schemaWithPartitionColumns = Schema.createRecord(schema.getName(),
                                                            schema.getDoc(),
                                                            schema.getNamespace(),
                                                            schema.isError());
    schemaWithPartitionColumns.setFields(fieldsWithPartitionColumns);

    return schemaWithPartitionColumns;
  }

  static Schema setupNameAndNamespace(@Nonnull Schema schema,
      @Nonnull String schemaName,
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

    List<Schema.Field> combinedSchemaFields = new ArrayList<>();

    for (Schema.Field field : leftSchema.getFields()) {
      combinedSchemaFields.add(new Schema.Field(field.name(),
          field.schema(),
          field.doc(),
          field.defaultValue(),
          field.order()));
    }

    for (Schema.Field field : rightSchema.getFields()) {
      combinedSchemaFields.add(new Schema.Field(field.name(),
          field.schema(),
          field.doc(),
          field.defaultValue(),
          field.order()));
    }

    Schema combinedSchema = Schema.createRecord(leftSchema.getName(),
        leftSchema.getDoc(),
        leftSchema.getNamespace(),
        leftSchema.isError());
    combinedSchema.setFields(combinedSchemaFields);

    return combinedSchema;
  }

  private static void appendFieldWithNewNamespace(@Nonnull Schema.Field field,
      @Nonnull String namespace,
      @Nonnull SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(namespace);
    Preconditions.checkNotNull(fieldAssembler);

    Schema fieldSchema = field.schema();
    switch (field.schema().getType()) {
      case ENUM:
        fieldSchema = Schema.createEnum(fieldSchema.getName(),
                                        fieldSchema.getDoc(),
                                        namespace,
                                        fieldSchema.getEnumSymbols());
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
      throw new IllegalArgumentException("Input schemas must be of RECORD type. "
          + "The actual type is: " + schema.getType());
    }

    SchemaBuilder.FieldAssembler<Schema> fieldAssembler = SchemaBuilder.
        record(schema.getName()).
        namespace(namespace).
        fields();

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
          // TODO: verify whether FIXED type has namespace
          appendField(field, fieldAssembler);
          break;
        case MAP:
          Schema newMapFieldSchema = setupNestedNamespace(field.schema(), nestedNamespace);
          Schema.Field newMapField = new Schema.Field(field.name(),
              newMapFieldSchema,
              field.doc(),
              field.defaultValue(),
              field.order());
          appendField(newMapField, fieldAssembler);
          break;
        case ARRAY:
          Schema newArrayFieldSchema = setupNestedNamespace(field.schema(), nestedNamespace);
          Schema.Field newArrayField = new Schema.Field(field.name(),
              newArrayFieldSchema,
              field.doc(),
              field.defaultValue(),
              field.order());
          appendField(newArrayField, fieldAssembler);
          break;
        case ENUM:
          appendFieldWithNewNamespace(field, nestedNamespace, fieldAssembler);
          break;
        case RECORD:
          Schema recordSchemaWithNestedNamespace = setupNestedNamespaceForRecord(field.schema(), nestedNamespace);
          Schema.Field newRecordFiled = new Schema.Field(field.name(),
              recordSchemaWithNestedNamespace,
              field.doc(),
              field.defaultValue(),
              field.order());
          appendField(newRecordFiled, fieldAssembler);
          break;
        case UNION:
          Schema unionSchemaWithNestedNamespace = setupNestedNamespace(field.schema(), nestedNamespace);
          Schema.Field newUnionField = new Schema.Field(field.name(),
              unionSchemaWithNestedNamespace,
              field.doc(),
              field.defaultValue(),
              field.order());
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
        Schema enumSchemaWithNestedNamespace = Schema.createEnum(schema.getName(),
            schema.getDoc(),
            namespace,
            schema.getEnumSymbols());

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
          throw new IllegalArgumentException(schema.toString(true) + " is unsupported UNION type. "
              + "Only nullable UNION is supported");
        }
      default:
        throw new IllegalArgumentException("Unsupported Schema type: " + schema.getType().toString());
    }
  }

  private static Schema setupTopLevelRecordName(@Nonnull Schema schema, @Nonnull String schemaName) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(schemaName);

    Schema avroSchema = Schema.createRecord(schemaName, schema.getDoc(), schema.getNamespace(), schema.isError());

    List<Schema.Field> fields = new ArrayList<>();


    for (Schema.Field field : schema.getFields()) {
      fields.add(new Schema.Field(field.name(), field.schema(), field.doc(), field.defaultValue(), field.order()));
    }
    avroSchema.setFields(fields);

    return avroSchema;
  }

  private static Schema convertFieldSchemaToAvroSchema(@Nonnull final String recordName,
      @Nonnull final String recordNamespace,
      @Nonnull final boolean mkFieldsOptional,
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

    return new TypeInfoToAvroSchemaConverter(recordNamespace, mkFieldsOptional)
        .convertFieldsTypeInfoToAvroSchema("",
            SchemaUtilities.getStandardName(recordName),
            columnNames,
            columnsTypeInfo);
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

    String schemaStr = table.getParameters().get(AvroSerdeUtils.AvroTableProperties.SCHEMA_LITERAL.getPropName());
    if (Strings.isNullOrEmpty(schemaStr)) {
      schemaStr = table.getSd()
                      .getSerdeInfo()
                      .getParameters()
                      .get(AvroSerdeUtils.AvroTableProperties.SCHEMA_LITERAL.getPropName());
    }

    if (Strings.isNullOrEmpty(schemaStr)) {
      LOG.debug("No avro schema defined under table or serde property {} for table {}",
          AvroSerdeUtils.AvroTableProperties.SCHEMA_LITERAL.getPropName(), getCompleteName(table));
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

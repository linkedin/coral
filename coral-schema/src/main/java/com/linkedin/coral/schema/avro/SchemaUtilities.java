/**
 * Copyright 2019-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.base.Strings;
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.HiveTable;
import com.linkedin.coral.common.types.StructType;
import com.linkedin.coral.schema.avro.exceptions.SchemaNotFoundException;

import static com.linkedin.coral.schema.avro.AvroSerdeUtils.*;
import static org.apache.avro.Schema.Type.*;


class SchemaUtilities {
  private static final Logger LOG = LoggerFactory.getLogger(SchemaUtilities.class);
  private static final String DALI_ROW_SCHEMA = "dali.row.schema";

  // TODO: 2/2/22 Needs to refactor this into a separate registry class
  // if the num of functions in this set get bigger
  private static final Set<String> USE_CALCITE_NULLABILITY_FUNCS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("extract_union")));

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
    Schema resultTableSchema;
    Schema originalTableSchema = SchemaUtilities.getCasePreservedSchemaForTable(table);
    if (originalTableSchema == null) {
      if (!strictMode) {
        LOG.warn("Cannot determine Avro schema for table " + table.getDbName() + "." + table.getTableName() + ". "
            + "Deriving Avro schema from Hive schema for that table. "
            + "Please note every field will have lower-cased name and be nullable");

        resultTableSchema = SchemaUtilities.convertHiveSchemaToAvro(table);
      } else {
        throw new SchemaNotFoundException("strictMode is set to True and fallback to Hive schema is disabled. "
            + "Cannot determine Avro schema for table " + table.getDbName() + "." + table.getTableName() + ".");
      }
    } else {
      if ("org.apache.hadoop.hive.serde2.avro.AvroSerDe".equals(table.getSd().getSerdeInfo().getSerializationLib())
          || HasDuplicateLowercaseColumnNames.visit(originalTableSchema)) {
        // Case 1: If serde == AVRO, early escape; Hive column info is not reliable and can be empty for these tables
        //         Hive itself uses avro.schema.literal as source of truth for these tables, so this should be fine
        // Case 2: If avro.schema.literal has duplicate column names when lowercased, that means we cannot do reliable
        //         matching with Hive schema as multiple Avro fields can map to the same Hive field
        resultTableSchema = originalTableSchema;
      } else {
        final List<FieldSchema> cols = new ArrayList<>(table.getSd().getCols());
        // Add partition columns if table partitioned
        if (isPartitioned(table)) {
          cols.addAll(getPartitionCols(table));
        }

        resultTableSchema = MergeHiveSchemaWithAvro.visit(structTypeInfoFromCols(cols), originalTableSchema);
      }
    }

    return resultTableSchema;
  }

  /**
   * Returns the Avro schema for a {@link CoralTable}.
   *
   * For Hive-backed tables, delegates to {@link #getAvroSchemaForTable(Table, boolean)} to preserve
   * existing behavior unchanged. For other CoralTables (e.g. Iceberg-backed), reads partner Avro
   * from {@link CoralTable#properties()} and merges it with the Coral schema using Iceberg-first
   * semantics via {@link MergeCoralSchemaWithAvro#merge}. When partner Avro is missing, a pure
   * Coral-derived schema is generated; in strict mode this case throws {@link SchemaNotFoundException}.
   *
   * @param coralTable the table to resolve, must not be null
   * @param strictMode if true, throw when no partner Avro schema is available
   * @return the resolved Avro schema
   */
  static Schema getAvroSchemaForTable(@Nonnull final CoralTable coralTable, boolean strictMode) {
    Preconditions.checkNotNull(coralTable);

    // Hive-backed CoralTable: delegate to the existing Hive-table API to preserve current behavior.
    if (coralTable instanceof HiveTable) {
      // TODO(linkedin/coral#606): migrate off HiveTable.getHiveTable() (deprecated INTERNAL API).
      return getAvroSchemaForTable(((HiveTable) coralTable).getHiveTable(), strictMode);
    }

    // Non-Hive (e.g. Iceberg) path: parse partner Avro from properties and merge with the Coral schema.
    Schema partnerAvro = getCasePreservedSchemaFromPropertyMaps(coralTable.properties(), null, coralTable.name());
    if (partnerAvro == null && strictMode) {
      throw new SchemaNotFoundException("strictMode is set to True and fallback is disabled. "
          + "Cannot determine Avro schema for table " + coralTable.name() + ".");
    }

    Preconditions.checkState(coralTable.getSchema() instanceof StructType,
        "CoralTable schema must be a StructType but was " + coralTable.getSchema().getClass().getSimpleName());
    StructType coralSchema = (StructType) coralTable.getSchema();

    // Match the legacy {@link #convertHiveSchemaToAvro} behavior for the no-partner fallback:
    // recordName is the bare table name and recordNamespace is the fully qualified name.
    // When partnerAvro is non-null, MergeCoralSchemaWithAvro takes name/namespace from the
    // partner record (via copyRecord in mergeTopLevelStruct), so these args only apply here.
    String fullName = coralTable.name();
    int lastDot = fullName.lastIndexOf('.');
    String recordName = lastDot >= 0 ? fullName.substring(lastDot + 1) : fullName;
    String recordNamespace = fullName;

    return MergeCoralSchemaWithAvro.merge(coralSchema, partnerAvro, recordName, recordNamespace);
  }

  static Schema convertHiveSchemaToAvro(@Nonnull final Table table) {
    Preconditions.checkNotNull(table);

    String recordName = table.getTableName();
    String recordNamespace = table.getDbName() + "." + recordName;

    final List<FieldSchema> cols = new ArrayList<>(table.getSd().getCols());
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

    Map<String, String> serdeProperties = table.getSd() != null && table.getSd().getSerdeInfo() != null
        ? table.getSd().getSerdeInfo().getParameters() : null;

    return getCasePreservedSchemaFromPropertyMaps(table.getParameters(), serdeProperties, getCompleteName(table));
  }

  /**
   * Returns case sensitive schema from property maps or null if not present.
   * This is the shared implementation used by both the Hive Table path and the CoralTable path.
   *
   * @param tableProperties table-level properties (e.g. from Table.getParameters() or CoralTable.properties())
   * @param serdeProperties serde-level properties, or null if not available (CoralTable path)
   * @param tableName human-readable table name for logging
   * @return Avro schema stored under 'avro.schema.literal', under 'dali.row.schema',
   * or null if none of the above are present
   */
  static Schema getCasePreservedSchemaFromPropertyMaps(@Nonnull Map<String, String> tableProperties,
      @Nullable Map<String, String> serdeProperties, String tableName) {
    Preconditions.checkNotNull(tableProperties);

    // First try avro.schema.literal from table properties
    String schemaStr = tableProperties.get(AvroSerdeUtils.AVRO_SCHEMA_LITERAL);

    // Then try avro.schema.literal from serde properties
    if (Strings.isNullOrEmpty(schemaStr) && serdeProperties != null) {
      schemaStr = serdeProperties.get(AvroSerdeUtils.AVRO_SCHEMA_LITERAL);
    }

    if (Strings.isNullOrEmpty(schemaStr)) {
      LOG.debug("No avro schema defined under table or serde property {} for table {}",
          AvroSerdeUtils.AVRO_SCHEMA_LITERAL, tableName);
    }

    Schema schema = null;

    // Then, try dali.row.schema
    if (Strings.isNullOrEmpty(schemaStr)) {
      schemaStr = tableProperties.get(DALI_ROW_SCHEMA);
      if (!Strings.isNullOrEmpty(schemaStr)) {
        schemaStr = schemaStr.replaceAll("\n", "\\\\n");
        // Given schemas stored in `dali.row.schema` are all non-nullable, we need to convert them to be nullable to be compatible with Spark
        schema = ToNullableSchemaVisitor.visit(AvroCompatibilityHelper.parse(schemaStr));
      }
    } else {
      schema = AvroCompatibilityHelper.parse(schemaStr);
    }

    if (schema != null) {
      LOG.info("Schema found for table {}", tableName);
      LOG.debug("Schema is {}", schema.toString(true));
      return schema;
    } else {
      LOG.warn("Cannot determine avro schema for table {}", tableName);
      return null;
    }
  }

  public static Object defaultValue(Schema.Field field) {
    if (AvroCompatibilityHelper.fieldHasDefault(field)) {
      return AvroCompatibilityHelper.getGenericDefaultValue(field);
    }
    return null;
  }

  static void appendField(@Nonnull Schema.Field field, @Nonnull List<Schema.Field> fields) {
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(fields);

    fields.add(cloneField(field, field.name(), field.schema(), field.doc()));
  }

  /**
   * This method appends a field derived from a {@link RelDataType} to the list of fields of an avro record
   *
   * @param fieldName
   * @param fieldRelDataType
   * @param doc
   * @param fields
   */
  static void appendField(@Nonnull String fieldName, @Nonnull RelDataType fieldRelDataType, @Nullable String doc,
      @Nonnull List<Schema.Field> fields, boolean isNullable) {
    Preconditions.checkNotNull(fieldName);
    Preconditions.checkNotNull(fieldRelDataType);
    Preconditions.checkNotNull(fields);

    Schema fieldSchema = RelDataTypeToAvroType.relDataTypeToAvroTypeNonNullable(fieldRelDataType, fieldName);

    // TODO: handle default value properly
    if (isNullable && fieldSchema.getType() != Schema.Type.NULL) {
      fieldSchema = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), fieldSchema));
    }
    fields.add(AvroCompatibilityHelper.newField(null).setName(fieldName).setSchema(fieldSchema).setDoc(doc).build());
  }

  static boolean isFieldNullable(@Nonnull RexCall rexCall, @Nonnull Schema inputSchema) {
    Preconditions.checkNotNull(rexCall);
    Preconditions.checkNotNull(inputSchema);

    // we first filter against these static list of functions, whose nullability should be
    // determined by calcite rather than avro.schema.literal
    if (USE_CALCITE_NULLABILITY_FUNCS.contains(rexCall.getOperator().getName().toLowerCase())) {
      return rexCall.getType().isNullable();
    }

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

  static void appendField(@Nonnull String fieldName, @Nonnull Schema.Field field, @Nonnull List<Schema.Field> fields) {
    appendField(fieldName, field, field.schema(), fields);
  }

  /**
   * Appends {@code field} under the name {@code fieldName}, but writes {@code fieldSchema} in place of the
   * schema the field carries.
   *
   * <p>This is needed when a field is lifted out of its original position and its nullability has to change
   * as a result - for example when a nested field is projected out of a nullable ancestor, where the leaf is
   * {@code required} within its parent record but the flattened column can still be null because the parent
   * itself may be absent.
   */
  static void appendField(@Nonnull String fieldName, @Nonnull Schema.Field field, @Nonnull Schema fieldSchema,
      @Nonnull List<Schema.Field> fields) {
    Preconditions.checkNotNull(fieldName);
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(fieldSchema);
    Preconditions.checkNotNull(fields);

    fields.add(cloneField(field, fieldName, fieldSchema, field.doc()));
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

  private static String getLiteralValueAsString(@Nonnull RexLiteral rexLiteral) {
    StringWriter documentationWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(documentationWriter);

    rexLiteral.printAsJava(printWriter);
    printWriter.flush();

    return documentationWriter.toString();
  }

  /**
   * Given an input {@link RelNode} and the index of a field in the {@link RelNode}'s corresponding Avro schema,
   * determine if the field with the specified index is a column from a table.
   * @param fieldIndex the index of a field in the <code>inputRelNode</code>'s corresponding Avro schema
   * @param inputRelNode the input {@link RelNode}
   * @return true if the field at <code>fieldIndex</code> is a column from a table
   */
  private static boolean isColumn(int fieldIndex, @Nonnull RelNode inputRelNode) {
    return !(inputRelNode instanceof LogicalAggregate)
        || fieldIndex < ((LogicalAggregate) inputRelNode).getGroupSet().cardinality();
  }

  static String generateDocumentationForLiteral(@Nonnull RexLiteral rexLiteral) {
    return "Field created from view literal with value: " + getLiteralValueAsString(rexLiteral);
  }

  static String generateDocumentationForAggregate(@Nonnull AggregateCall aggregateCall) {
    return "Field created in view by applying aggregate function of type: " + aggregateCall.getAggregation().getKind();
  }

  static String generateDocumentationForFunctionCall(@Nonnull RexCall rexCall, @Nonnull Schema inputSchema,
      @Nonnull RelNode inputRelNode) {
    StringJoiner args = new StringJoiner(", ");

    for (RexNode rexNode : rexCall.getOperands()) {
      SqlKind nodeKind = rexNode.getKind();
      switch (nodeKind) {
        case LITERAL:
          args.add(getLiteralValueAsString((RexLiteral) rexNode));
          break;
        case INPUT_REF:
          int fieldIndex = ((RexInputRef) rexNode).getIndex();
          if (isColumn(fieldIndex, inputRelNode)) {
            args.add(inputSchema.getFullName() + "." + inputSchema.getFields().get(fieldIndex).name());
            break;
          }
        default:
          args.add("value with type " + rexNode.getType().toString());
          break;
      }
    }

    String functionType = rexCall.getOperator() instanceof SqlUserDefinedFunction ? "UDF" : "operator";
    return "Field created in view by applying " + functionType + " '" + rexCall.getOperator().getName() + "'"
        + (args.length() > 0 ? " with argument(s): " + args : "");
  }

  static String toAvroQualifiedName(@Nonnull String name) {
    Preconditions.checkNotNull(name);
    return name.replace("$", "_");
  }

  static boolean isPartitioned(@Nonnull Table tableOrView) {
    Preconditions.checkNotNull(tableOrView);

    List<FieldSchema> partitionColumns = getPartitionCols(tableOrView);

    return (partitionColumns.size() != 0);
  }

  private static List<Schema.Field> cloneFieldList(List<Schema.Field> fieldList, boolean isPartCol) {
    List<Schema.Field> result = new ArrayList<>();
    for (Schema.Field field : fieldList) {
      String fieldDoc = isPartCol ? "This is the partition column. "
          + "Partition columns, if present in the schema, should also be projected in the data." : field.doc();
      result.add(cloneField(field, field.name(), field.schema(), fieldDoc));
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

  static void replicateFieldProps(Schema.Field srcField, Schema.Field targetField) {
    final List<String> existingPropNames = AvroCompatibilityHelper.getAllPropNames(targetField);
    for (String propName : AvroCompatibilityHelper.getAllPropNames(srcField)) {
      if (existingPropNames.contains(propName)) {
        continue;
      }
      final String fieldPropAsJsonString = AvroCompatibilityHelper.getFieldPropAsJsonString(srcField, propName);
      AvroCompatibilityHelper.setFieldPropFromJsonString(targetField, propName, fieldPropAsJsonString, false);
    }
  }

  static void replicateSchemaProps(Schema srcSchema, Schema targetSchema) {
    final List<String> existingPropNames = AvroCompatibilityHelper.getAllPropNames(targetSchema);
    for (String propName : AvroCompatibilityHelper.getAllPropNames(srcSchema)) {
      if (existingPropNames.contains(propName)) {
        continue;
      }
      final String schemaPropAsJsonString = AvroCompatibilityHelper.getSchemaPropAsJsonString(srcSchema, propName);
      AvroCompatibilityHelper.setSchemaPropFromJsonString(targetSchema, propName, schemaPropAsJsonString, false);
    }
  }

  static Schema addPartitionColsToSchema(@Nonnull Schema schema, @Nonnull Table tableOrView) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(tableOrView);

    if (!isPartitioned(tableOrView)) {
      return schema;
    }

    Schema partitionColumnsSchema =
        convertFieldSchemaToAvroSchema("partitionCols", "partitionCols", true, tableOrView.getPartitionKeys());

    List<Schema.Field> fieldsWithPartitionColumns = cloneFieldList(schema.getFields());
    fieldsWithPartitionColumns.addAll(cloneFieldList(partitionColumnsSchema.getFields(), true));

    return newRecord(schema, schema.getName(), schema.getNamespace(), fieldsWithPartitionColumns);
  }

  /**
   * Assigns the view name and a namespace per nesting level (non-strict mode). Records and enums are rebuilt in their
   * new namespace without their declared aliases, because those aliases are qualified by the namespace they were
   * declared in, which this normalization replaces. Fixed schemas, field aliases and all other metadata are kept.
   */
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
        newRecord(leftSchema, leftSchema.getName(), leftSchema.getNamespace(), combinedSchemaFields);
    // In case there are conflicts of property values among leftSchema and rightSchema, the former-applied leftSchema
    // will be the winner as Schema object doesn't support prop-overwrite.
    replicateSchemaProps(rightSchema, combinedSchema);

    return combinedSchema;
  }

  /**
   * This method merges two input schemas of LogicalUnion operator, or throws exception if they can't be merged.
   *
   * @param originalLeftSchema Left schema to be merged
   * @param originalRightSchema Right schema to be merged
   * @param strictMode If set to true, namespaces are required to be same.
   *                   If set to false, we don't check namespaces.
   * @param forceLowercase If set to true, cast schema to lowercase
   * @return Merged schema if the input schemas can be merged
   */
  static Schema mergeUnionRecordSchema(@Nonnull Schema originalLeftSchema, @Nonnull Schema originalRightSchema,
      boolean strictMode, boolean forceLowercase) {
    Preconditions.checkNotNull(originalLeftSchema);
    Preconditions.checkNotNull(originalRightSchema);

    Schema leftSchema = originalLeftSchema;
    Schema rightSchema = originalRightSchema;

    // uniquify namespaces for schemas with multiple fields with the same name
    if (!strictMode) {
      leftSchema = modifySchemaBeforeMerge(originalLeftSchema);
      rightSchema = modifySchemaBeforeMerge(originalRightSchema);
    }

    // TODO: we should investigate simplify casing transformations
    if (forceLowercase) {
      leftSchema = ToLowercaseSchemaVisitor.visit(leftSchema);
      rightSchema = ToLowercaseSchemaVisitor.visit(rightSchema);
    }

    return mergeRecords(leftSchema, rightSchema, strictMode, leftSchema.getName());
  }

  /**
   * Merges two record schemas field by field. The first (left) schema is canonical: the result keeps its record
   * metadata, field order, field spelling and field metadata. Fields are paired by exact name; fields that only
   * differ in casing are aligned when each side has exactly one candidate (see {@link #alignFields}).
   */
  private static Schema mergeRecords(Schema left, Schema right, boolean strictMode, String path) {
    if (left.toString(true).equals(right.toString(true))) {
      return left;
    }

    if (strictMode) {
      // We require namespace to match in strictMode
      if (!Objects.equals(left.getNamespace(), right.getNamespace())) {
        throw new RuntimeException("Found namespace mismatch while configured with strict mode at " + path
            + ". Namespace for " + left.getName() + " is: " + left.getNamespace() + ". " + "Namespace for "
            + right.getName() + " is: " + right.getNamespace());
      }
    }

    List<Schema.Field> mergedFields = new ArrayList<>();
    for (Schema.Field[] pair : alignFields(left, right, path)) {
      Schema.Field leftField = pair[0];
      Schema unionFieldSchema =
          getUnionFieldSchema(leftField.schema(), pair[1].schema(), strictMode, path + "." + leftField.name());
      // We need to reorder the union options if necessary, here and inside the field's type: the defaults the field
      // retains have to stay valid, i.e. defaultValue = 1, unionFieldSchema = [null, int], we need to reorder
      // `unionFieldSchema` to be [int, null], otherwise, schema validation will fail and cause exception
      Schema reordered =
          AvroSchemaProjection.orderOptionsForDefault(unionFieldSchema, leftField, path + "." + leftField.name());
      mergedFields.add(cloneField(leftField, leftField.name(), reordered, leftField.doc()));
    }
    return newRecord(left, left.getName(), left.getNamespace(), mergedFields);
  }

  /**
   * Pairs the fields of two records. Names are resolved case-insensitively, but never by guessing between several
   * candidates: a group of names that fold to the same key is paired when both sides declare exactly the same names
   * (an exact bijection), or when each side has a single member. Anything else is ambiguous. Avro aliases are not
   * lookup keys.
   */
  private static List<Schema.Field[]> alignFields(Schema left, Schema right, String path) {
    Map<String, List<Schema.Field>> leftGroups = groupByCaseFoldedName(left);
    Map<String, List<Schema.Field>> rightGroups = groupByCaseFoldedName(right);

    for (Map.Entry<String, List<Schema.Field>> group : rightGroups.entrySet()) {
      if (!leftGroups.containsKey(group.getKey())) {
        throw missingField(group.getValue().get(0), right, left, path);
      }
    }

    List<Schema.Field[]> pairs = new ArrayList<>();
    for (Schema.Field leftField : left.getFields()) {
      String key = caseFoldedName(leftField.name());
      List<Schema.Field> leftGroup = leftGroups.get(key);
      List<Schema.Field> rightGroup = rightGroups.get(key);
      if (rightGroup == null) {
        throw missingField(leftField, left, right, path);
      }

      Schema.Field rightField = null;
      if (fieldNames(leftGroup).equals(fieldNames(rightGroup))) {
        for (Schema.Field candidate : rightGroup) {
          if (candidate.name().equals(leftField.name())) {
            rightField = candidate;
          }
        }
      } else if (leftGroup.size() == 1 && rightGroup.size() == 1) {
        rightField = rightGroup.get(0);
      } else {
        throw new RuntimeException("Cannot align the fields of the UNION branches at " + path
            + ": field names that differ only in casing are ambiguous. Left candidates: " + fieldNames(leftGroup)
            + ", right candidates: " + fieldNames(rightGroup));
      }
      pairs.add(new Schema.Field[] { leftField, rightField });
    }
    return pairs;
  }

  private static Map<String, List<Schema.Field>> groupByCaseFoldedName(Schema record) {
    Map<String, List<Schema.Field>> groups = new LinkedHashMap<>();
    for (Schema.Field field : record.getFields()) {
      groups.computeIfAbsent(caseFoldedName(field.name()), k -> new ArrayList<>()).add(field);
    }
    return groups;
  }

  private static String caseFoldedName(String name) {
    return name.toLowerCase(Locale.ROOT);
  }

  private static Set<String> fieldNames(List<Schema.Field> fields) {
    return fields.stream().map(Schema.Field::name).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static RuntimeException missingField(Schema.Field field, Schema owner, Schema other, String path) {
    return new RuntimeException(field.name() + " is in schema " + owner.getName() + ": " + owner.toString(true)
        + ", but not in schema " + other.getName() + ": " + other.toString(true) + " (at " + path + ")");
  }

  static Schema extractIfOption(Schema schema) {
    if (isNullableType(schema)) {
      return getOtherTypeFromNullableType(schema);
    } else {
      return schema;
    }
  }

  private static Schema getUnionFieldSchema(@Nonnull Schema leftSchema, @Nonnull Schema rightSchema, boolean strictMode,
      String path) {
    Preconditions.checkNotNull(leftSchema);
    Preconditions.checkNotNull(rightSchema);

    Schema.Type leftSchemaType = leftSchema.getType();
    Schema.Type rightSchemaType = rightSchema.getType();
    if (leftSchemaType == NULL) {
      return makeNullable(rightSchema, false);
    }
    if (rightSchemaType == NULL) {
      return makeNullable(leftSchema, false);
    }
    if (isNullableType(leftSchema) || isNullableType(rightSchema)) {
      // If leftSchema and rightSchema are nullable union types with different order,
      // we choose the order of the leftSchema.
      // i.e. leftSchema = [int, null], rightSchema = [null, int], resultant schema is [int, null]
      return makeNullable(
          getUnionFieldSchema(makeNonNullable(leftSchema), makeNonNullable(rightSchema), strictMode, path),
          isNullSecond(leftSchema));
    }

    if (leftSchemaType == rightSchemaType) {
      switch (leftSchema.getType()) {
        case BOOLEAN:
        case BYTES:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case STRING:
          if (hasSameLogicalType(leftSchema, rightSchema)) {
            return leftSchema;
          }
          break;
        case UNION:
          // A union that is not a nullable option is opaque: it is only valid when both sides agree exactly
          if (leftSchema.equals(rightSchema)) {
            return leftSchema;
          }
          break;
        case FIXED:
          if (leftSchema.getFixedSize() != rightSchema.getFixedSize()) {
            throw new RuntimeException("Found two fixed schemas of different sizes at " + path + ": "
                + leftSchema.getFullName() + " has size " + leftSchema.getFixedSize() + ", " + rightSchema.getFullName()
                + " has size " + rightSchema.getFixedSize());
          }
          if (isSameNamespace(leftSchema, rightSchema, strictMode) && hasSameLogicalType(leftSchema, rightSchema)) {
            return leftSchema;
          }
          break;
        case ENUM:
          // Union symbols of two Enum
          ImmutableSet<String> schemaSymbols = ImmutableSet.<String> builder().addAll(leftSchema.getEnumSymbols())
              .addAll(rightSchema.getEnumSymbols()).build();
          return createEnumLike(leftSchema, leftSchema.getName(), leftSchema.getNamespace(), schemaSymbols.asList());
        case RECORD:
          return mergeRecords(leftSchema, rightSchema, strictMode, path);
        case MAP:
          Schema valueType =
              getUnionFieldSchema(leftSchema.getValueType(), rightSchema.getValueType(), strictMode, path + ".values");
          return createMapLike(leftSchema, valueType);
        case ARRAY:
          Schema elementType = getUnionFieldSchema(leftSchema.getElementType(), rightSchema.getElementType(),
              strictMode, path + ".items");
          return createArrayLike(leftSchema, elementType);
        default:
          throw new IllegalArgumentException(
              "Unsupported Avro type " + leftSchema.getType() + " in schema: " + leftSchema.toString(true));
      }
    } else {
      final ImmutableSet<Schema.Type> types = ImmutableSet.of(leftSchemaType, rightSchemaType);
      if (ImmutableSet.of(ENUM, STRING).equals(types)) {
        return Schema.create(STRING);
      }
      if (ImmutableSet.of(FIXED, BYTES).equals(types)) {
        return Schema.create(BYTES);
      }
      if (ImmutableSet.of(INT, LONG).equals(types)) {
        return Schema.create(LONG);
      }
      if (ImmutableSet.of(INT, FLOAT).equals(types)) {
        return Schema.create(FLOAT);
      }
      if (ImmutableSet.of(INT, DOUBLE).equals(types)) {
        return Schema.create(DOUBLE);
      }
      if (ImmutableSet.of(LONG, FLOAT).equals(types)) {
        return Schema.create(FLOAT);
      }
      if (ImmutableSet.of(LONG, DOUBLE).equals(types)) {
        return Schema.create(DOUBLE);
      }
      if (ImmutableSet.of(FLOAT, DOUBLE).equals(types)) {
        return Schema.create(DOUBLE);
      }
    }

    throw new RuntimeException("Found two incompatible schemas for LogicalUnion operator at " + path
        + ". Left schema is: " + leftSchema.toString(true) + ". " + "Right schema is: " + rightSchema.toString(true));
  }

  /** Two schemas that both carry a logical type must carry the same one, including decimal precision and scale. */
  private static boolean hasSameLogicalType(Schema left, Schema right) {
    String leftLogicalType = AvroCompatibilityHelper.getSchemaPropAsJsonString(left, "logicalType");
    String rightLogicalType = AvroCompatibilityHelper.getSchemaPropAsJsonString(right, "logicalType");
    if (leftLogicalType == null || rightLogicalType == null) {
      return true;
    }
    for (String prop : Arrays.asList("logicalType", "precision", "scale")) {
      if (!Objects.equals(AvroCompatibilityHelper.getSchemaPropAsJsonString(left, prop),
          AvroCompatibilityHelper.getSchemaPropAsJsonString(right, prop))) {
        return false;
      }
    }
    return true;
  }

  /** True if the field declares a default other than null, which forces the matching union option to come first. */
  static boolean hasNonNullDefault(Schema.Field field) {
    return AvroCompatibilityHelper.fieldHasDefault(field)
        && !"null".equals(AvroCompatibilityHelper.getDefaultValueAsJsonString(field));
  }

  static Schema makeNonNullable(Schema schema) {
    if (isNullableType(schema)) {
      return getOtherTypeFromNullableType(schema);
    } else {
      return schema;
    }
  }

  static Schema makeNullable(Schema schema, boolean nullAsSecond) {
    if (schema.getType() == NULL || isNullableType(schema)) {
      return schema;
    } else if (schema.getType() == UNION) {
      for (Schema innerSchema : schema.getTypes()) {
        if (innerSchema.getType() == NULL) {
          return schema;
        }
      }
      final List<Schema> types = new ArrayList<>();
      types.add(Schema.create(NULL));
      types.addAll(schema.getTypes());
      return Schema.createUnion(types);
    } else {
      if (nullAsSecond) {
        return Schema.createUnion(Arrays.asList(schema, Schema.create(Schema.Type.NULL)));
      } else {
        return Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), schema));
      }
    }
  }

  static boolean isNullSecond(Schema schema) {
    return schema != null && isNullableType(schema) && schema.getTypes().get(1).getType().equals(Schema.Type.NULL);
  }

  static Schema discardNullFromUnionIfExist(Schema schema) {
    Preconditions.checkArgument(schema.getType() == Schema.Type.UNION, "Expected union schema but was passed: %s",
        schema);
    List<Schema> result = new ArrayList<>();
    for (Schema nested : schema.getTypes()) {
      if (!(nested.getType() == Schema.Type.NULL)) {
        result.add(nested);
      }
    }
    return Schema.createUnion(result);
  }

  static boolean nullExistInUnion(Schema schema) {
    Preconditions.checkArgument(schema.getType() == Schema.Type.UNION, "Expected union schema but was passed: %s",
        schema);
    for (Schema nested : schema.getTypes()) {
      if (nested.getType() == Schema.Type.NULL) {
        return true;
      }
    }
    return false;
  }

  private static boolean isSameNamespace(@Nonnull Schema leftSchema, @Nonnull Schema rightSchema, boolean strictMode) {
    return !strictMode || Objects.equals(leftSchema.getNamespace(), rightSchema.getNamespace());
  }

  private static Schema setupNestedNamespaceForRecord(@Nonnull Schema schema, @Nonnull String namespace) {
    // Detect collisions and build mapping for suffix assignment
    Map<String, List<String>> collisionMap = detectNamespaceCollisions(schema);
    return setupNestedNamespaceForRecord(schema, namespace, collisionMap);
  }

  /**
   * Detects which record names would collide (same name, different original namespace) at the current level.
   * This includes records that appear directly in fields, within unions, arrays, or maps.
   * 
   * @param schema The parent record schema to scan
   * @return Map from record name -> ordered list of original namespaces (only for records with collisions)
   */
  private static Map<String, List<String>> detectNamespaceCollisions(@Nonnull Schema schema) {
    Map<String, List<String>> recordKeyToOriginalNamespaces = new LinkedHashMap<>();

    // Scan all fields to collect record types with their parent paths
    for (Schema.Field field : schema.getFields()) {
      collectRecordTypes(field.schema(), "", recordKeyToOriginalNamespaces);
    }

    // Build collision map: record name -> list of original namespaces (only for actual collisions)
    Map<String, List<String>> collisions = new LinkedHashMap<>();
    for (Map.Entry<String, List<String>> entry : recordKeyToOriginalNamespaces.entrySet()) {
      // Use a Set to check uniqueness while preserving order in List
      Set<String> uniqueNamespaces = new LinkedHashSet<>(entry.getValue());
      if (uniqueNamespaces.size() > 1) {
        // Extract just the record name from the key (format: "parentPath::recordName")
        String key = entry.getKey();
        String recordName = key.substring(key.lastIndexOf("::") + 2);
        collisions.put(recordName, entry.getValue());
      }
    }

    return collisions;
  }

  /**
   * Recursively collects all record types and their original namespaces from a schema.
   * This traverses through unions, arrays, and maps to find all nested record types.
   * Records are keyed by their parent path to ensure only records at the same hierarchical
   * level are considered for collision detection.
   * 
   * @param schema The schema to scan
   * @param parentPath The hierarchical path to this schema element (e.g., "Parent.Child")
   * @param recordKeyToNamespaces Map to populate with (parentPath::recordName) -> ordered list of original namespaces
   */
  private static void collectRecordTypes(@Nonnull Schema schema, @Nonnull String parentPath,
      @Nonnull Map<String, List<String>> recordKeyToNamespaces) {
    switch (schema.getType()) {
      case RECORD:
        String originalNamespace = schema.getNamespace() != null ? schema.getNamespace() : "";
        String recordName = schema.getName();
        // Create a unique key combining parent path and record name
        String key = parentPath + "::" + recordName;
        recordKeyToNamespaces.computeIfAbsent(key, k -> new ArrayList<>()).add(originalNamespace);

        // Recursively collect records from this record's fields
        // Update parent path to include this record
        String newParentPath = parentPath.isEmpty() ? recordName : parentPath + "." + recordName;
        for (Schema.Field field : schema.getFields()) {
          collectRecordTypes(field.schema(), newParentPath, recordKeyToNamespaces);
        }
        break;
      case UNION:
        for (Schema type : schema.getTypes()) {
          collectRecordTypes(type, parentPath, recordKeyToNamespaces);
        }
        break;
      case ARRAY:
        collectRecordTypes(schema.getElementType(), parentPath, recordKeyToNamespaces);
        break;
      case MAP:
        collectRecordTypes(schema.getValueType(), parentPath, recordKeyToNamespaces);
        break;
      case ENUM:
      case BOOLEAN:
      case BYTES:
      case DOUBLE:
      case FLOAT:
      case INT:
      case LONG:
      case STRING:
      case FIXED:
      case NULL:
        // These types don't contain nested records
        break;
      default:
        break;
    }
  }

  private static Schema setupNestedNamespaceForRecord(@Nonnull Schema schema, @Nonnull String namespace,
      @Nonnull Map<String, List<String>> collisionMap) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(namespace);
    Preconditions.checkNotNull(collisionMap);

    if (!schema.getType().equals(Schema.Type.RECORD)) {
      throw new IllegalArgumentException(
          "Input schemas must be of RECORD type. " + "The actual type is: " + schema.getType());
    }

    // Add numeric suffix to avoid collisions when multiple fields have nested records with the same name
    String recordNamespace = namespace;
    if (collisionMap.containsKey(schema.getName())) {
      List<String> namespaces = collisionMap.get(schema.getName());
      String originalNamespace = schema.getNamespace() != null ? schema.getNamespace() : "";
      int index = namespaces.indexOf(originalNamespace);
      if (index >= 0) {
        // Append numeric suffix based on order encountered: -0, -1, -2, etc.
        recordNamespace = namespace + "-" + index;
      }
    }

    String nestedNamespace = recordNamespace + "." + schema.getName();

    List<Schema.Field> fields = new ArrayList<>();
    for (Schema.Field field : schema.getFields()) {
      fields.add(cloneField(field, field.name(), setupNestedNamespace(field.schema(), nestedNamespace, collisionMap),
          field.doc()));
    }

    return newRecord(schema, schema.getName(), recordNamespace, fields, false);
  }

  private static Schema setupNestedNamespace(@Nonnull Schema schema, @Nonnull String namespace,
      @Nonnull Map<String, List<String>> collisionMap) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(namespace);
    Preconditions.checkNotNull(collisionMap);

    switch (schema.getType()) {
      case NULL:
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
        Schema valueSchemaWithNestedNamespace = setupNestedNamespace(valueSchema, namespace, collisionMap);
        return createMapLike(schema, valueSchemaWithNestedNamespace);
      case ARRAY:
        Schema elementSchema = schema.getElementType();
        Schema elementSchemaWithNestedNamespace = setupNestedNamespace(elementSchema, namespace, collisionMap);
        return createArrayLike(schema, elementSchemaWithNestedNamespace);
      case ENUM:
        return createEnumLike(schema, schema.getName(), namespace, schema.getEnumSymbols(), false);
      case RECORD:
        return setupNestedNamespaceForRecord(schema, namespace, collisionMap);
      case UNION:
        List<Schema> types = new ArrayList<>();

        for (Schema type : schema.getTypes()) {
          Schema typeWithNestNamespace = setupNestedNamespace(type, namespace, collisionMap);
          types.add(typeWithNestNamespace);
        }
        return Schema.createUnion(types);
      default:
        throw new IllegalArgumentException("Unsupported Schema type: " + schema.getType().toString());
    }
  }

  static Schema modifySchemaBeforeMerge(@Nonnull Schema originalSchema) {
    Schema modifiedSchema = originalSchema;

    if (originalSchema.getNamespace() == null) {
      modifiedSchema = newRecord(originalSchema, originalSchema.getName(), originalSchema.getName(),
          cloneFieldList(originalSchema.getFields()), false);
    }

    return SchemaUtilities.setupNameAndNamespace(modifiedSchema, modifiedSchema.getName(),
        modifiedSchema.getNamespace());
  }

  private static Schema setupTopLevelRecordName(@Nonnull Schema schema, @Nonnull String schemaName) {
    Preconditions.checkNotNull(schema);
    Preconditions.checkNotNull(schemaName);

    return newRecord(schema, schemaName, schema.getNamespace(), cloneFieldList(schema.getFields()), false);
  }

  private static Schema convertFieldSchemaToAvroSchema(@Nonnull final String recordName,
      @Nonnull final String recordNamespace, final boolean mkFieldsOptional, @Nonnull final List<FieldSchema> columns) {
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

  protected static class HasDuplicateLowercaseColumnNames extends AvroSchemaVisitor<Boolean> {
    protected static boolean visit(Schema schema) {
      return AvroSchemaVisitor.visit(schema, new HasDuplicateLowercaseColumnNames());
    }

    @Override
    public Boolean record(Schema record, List<String> names, List<Boolean> fieldResults) {
      return fieldResults.stream().anyMatch(x -> x) || names.stream()
          .collect(Collectors.groupingBy(String::toLowerCase)).values().stream().anyMatch(x -> x.size() > 1);
    }

    @Override
    public Boolean union(Schema union, List<Boolean> optionResults) {
      return optionResults.stream().anyMatch(x -> x);
    }

    @Override
    public Boolean array(Schema array, Boolean elementResult) {
      return elementResult;
    }

    @Override
    public Boolean map(Schema map, Boolean valueResult) {
      return valueResult;
    }

    @Override
    public Boolean primitive(Schema primitive) {
      return false;
    }
  }

  static Schema copyRecord(Schema record, List<Schema.Field> newFields) {
    return newRecord(record, record.getName(), record.getNamespace(), cloneFieldList(newFields));
  }

  /**
   * Copies a field of a table schema onto a reconciled schema. Doc, order, aliases and properties are kept; the
   * default is passed on like the table schema reconciliation always did.
   */
  static Schema.Field copyField(Schema.Field field, Schema newSchema) {
    Schema.Field copy = AvroCompatibilityHelper.createSchemaField(field.name(), newSchema, field.doc(),
        defaultValue(field), field.order());
    replicateFieldProps(field, copy);
    field.aliases().forEach(copy::addAlias);
    return copy;
  }

  /**
   * Returns a new field named {@code name} that carries {@code schema} and keeps everything else {@code field}
   * declares: doc, declared aliases, sort order, custom properties and its default, which stays absent, explicit
   * null or the declared value exactly as in the source field.
   */
  static Schema.Field cloneField(Schema.Field field, String name, Schema schema, String doc) {
    Schema.Field copy = AvroCompatibilityHelper.newField(field).setName(name).setSchema(schema).setDoc(doc).build();
    field.aliases().forEach(copy::addAlias);
    return copy;
  }

  /**
   * Creates a record that takes doc, error flag, declared aliases and custom properties from {@code template}, while
   * name, namespace and the (unattached) fields are given. Callers pass fields that are not part of another record.
   */
  static Schema newRecord(Schema template, String name, String namespace, List<Schema.Field> fields) {
    return newRecord(template, name, namespace, fields, true);
  }

  /**
   * As {@link #newRecord(Schema, String, String, List)}; with {@code keepAliases=false} the record declares no
   * aliases. The non-strict namespace normalization rebuilds records this way, which is how it has always behaved:
   * aliases are qualified by the namespace they were declared in, which normalization replaces.
   */
  static Schema newRecord(Schema template, String name, String namespace, List<Schema.Field> fields,
      boolean keepAliases) {
    Schema record = Schema.createRecord(name, template.getDoc(), namespace, template.isError());
    record.setFields(fields);
    replicateNamedSchemaMetadata(template, record, keepAliases);
    return record;
  }

  /** A record with the given name, namespace and (unattached) fields and no further metadata. */
  static Schema createRecord(String name, String namespace, List<Schema.Field> fields) {
    Schema record = Schema.createRecord(name, null, namespace, false);
    record.setFields(fields);
    return record;
  }

  /** An array of {@code elementType} that keeps the custom properties of {@code template}. */
  static Schema createArrayLike(Schema template, Schema elementType) {
    Schema array = Schema.createArray(elementType);
    replicateSchemaProps(template, array);
    return array;
  }

  /** A map of {@code valueType} that keeps the custom properties of {@code template}. */
  static Schema createMapLike(Schema template, Schema valueType) {
    Schema map = Schema.createMap(valueType);
    replicateSchemaProps(template, map);
    return map;
  }

  /** An enum that keeps doc, enum default, declared aliases and custom properties of {@code template}. */
  static Schema createEnumLike(Schema template, String name, String namespace, List<String> symbols) {
    return createEnumLike(template, name, namespace, symbols, true);
  }

  /** As above; with {@code keepAliases=false} the enum declares no aliases (see {@code newRecord}). */
  static Schema createEnumLike(Schema template, String name, String namespace, List<String> symbols,
      boolean keepAliases) {
    Schema enumSchema = AvroCompatibilityHelper.newEnumSchema(name, template.getDoc(), namespace, symbols,
        enumDefault(template, symbols));
    replicateNamedSchemaMetadata(template, enumSchema, keepAliases);
    return enumSchema;
  }

  private static String enumDefault(Schema template, List<String> symbols) {
    String enumDefault = AvroCompatibilityHelper.getEnumDefault(template);
    return enumDefault != null && symbols.contains(enumDefault) ? enumDefault : null;
  }

  /** Copies declared aliases (as their qualified names) and custom properties of a named or container schema. */
  static void replicateNamedSchemaMetadata(Schema src, Schema target) {
    replicateNamedSchemaMetadata(src, target, true);
  }

  private static void replicateNamedSchemaMetadata(Schema src, Schema target, boolean keepAliases) {
    replicateSchemaProps(src, target);
    if (keepAliases && (src.getType() == RECORD || src.getType() == ENUM || src.getType() == FIXED)) {
      // Aliases are qualified names; an alias without namespace must not inherit the namespace of the new schema.
      src.getAliases().forEach(alias -> target.addAlias(alias, ""));
    }
  }

  static String makeCompatibleName(String name) {
    if (!validAvroName(name)) {
      return sanitize(name);
    }
    return name;
  }

  static boolean validAvroName(String name) {
    int length = name.length();
    Preconditions.checkArgument(length > 0, "Empty name");
    char first = name.charAt(0);
    if (!(Character.isLetter(first) || first == '_')) {
      return false;
    }

    for (int i = 1; i < length; i++) {
      char character = name.charAt(i);
      if (!(Character.isLetterOrDigit(character) || character == '_')) {
        return false;
      }
    }
    return true;
  }

  static String sanitize(String name) {
    int length = name.length();
    StringBuilder sb = new StringBuilder(name.length());
    char first = name.charAt(0);
    if (!(Character.isLetter(first) || first == '_')) {
      sb.append(sanitize(first));
    } else {
      sb.append(first);
    }

    for (int i = 1; i < length; i++) {
      char character = name.charAt(i);
      if (!(Character.isLetterOrDigit(character) || character == '_')) {
        sb.append(sanitize(character));
      } else {
        sb.append(character);
      }
    }
    return sb.toString();
  }

  private static String sanitize(char character) {
    if (Character.isDigit(character)) {
      return "_" + character;
    }
    return "_x" + Integer.toHexString(character).toUpperCase();
  }

  static StructTypeInfo structTypeInfoFromCols(List<FieldSchema> cols) {
    Preconditions.checkArgument(cols != null && cols.size() > 0, "No Hive schema present");
    List<String> fieldNames = cols.stream().map(FieldSchema::getName).collect(Collectors.toList());
    List<TypeInfo> fieldTypeInfos =
        cols.stream().map(f -> TypeInfoUtils.getTypeInfoFromTypeString(f.getType())).collect(Collectors.toList());
    return (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTypeInfos);
  }

  /**
   * Reorders an option schema so that the type of the provided default value is the first type in the option schema
   *
   * e.g. If the schema is [null, int] and the default value is 1, the returned schema is [int, null]
   * If the schema is not an option schema or if there is no default value, schema is returned as-is
   */
  static Schema reorderOptionIfRequired(Schema schema, Object defaultValue) {
    if (isNullableType(schema) && defaultValue != null && schema.getTypes().get(0).getType() == Schema.Type.NULL) {
      return Schema.createUnion(Arrays.asList(schema.getTypes().get(1), schema.getTypes().get(0)));
    } else {
      return schema;
    }
  }
}

/**
 * Copyright 2021-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;
import com.linkedin.avroutil1.compatibility.Jackson1Utils;

import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.avro.AvroSerDe;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;
import org.codehaus.jackson.node.JsonNodeFactory;

import com.linkedin.coral.com.google.common.base.Preconditions;

import static com.linkedin.coral.schema.avro.AvroSerdeUtils.*;


/**
 * A {@link HiveSchemaWithPartnerVisitor} which augments a Hive schema with extra metadata from a partner Avro schema
 * and generates a resultant "merged" Avro schema
 *
 * 1. Fields are matched between Hive and Avro schemas using a case insensitive search by field name
 * 2. Copies field names, nullability, default value, field props from the Avro schema
 * 3. Copies field type from the Hive schema.
 * 4. Retains fields found only in the Hive schema; Ignores fields found only in the Avro schema
 * 5. Fields found only in Hive schema are represented as optional fields in the resultant Avro schema
 * 6. For fields found only in Hive schema, field names are sanitized to make them compatible with Avro identifier spec
 */
class MergeHiveSchemaWithAvro extends HiveSchemaWithPartnerVisitor<Schema, Schema.Field, Schema, Schema.Field> {

  static Schema visit(StructTypeInfo typeInfo, Schema schema) {
    return HiveSchemaWithPartnerVisitor.visit(typeInfo, schema, new MergeHiveSchemaWithAvro(),
        AvroPartnerAccessor.INSTANCE);
  }
  private final AtomicInteger recordCounter = new AtomicInteger(0);

  @Override
  public Schema struct(StructTypeInfo struct, Schema partner, List<Schema.Field> fieldResults) {
    // Check if this is a union-encoded-as-struct by looking for "tag" field in fieldResults
    boolean isUnionEncodedAsStruct = fieldResults.stream()
        .anyMatch(f -> f.name().equalsIgnoreCase("tag"));
    
    boolean shouldResultBeOptional = partner == null || isNullableType(partner);
    Schema result;
    if (partner == null || SchemaUtilities.extractIfOption(partner).getType() != Schema.Type.RECORD) {
      // if there was no matching Avro struct, return a struct with new record/namespace
      int recordNum = recordCounter.incrementAndGet();
      result = Schema.createRecord("record" + recordNum, null, "namespace" + recordNum, false);
      result.setFields(fieldResults);
    } else {
      result = SchemaUtilities.copyRecord(SchemaUtilities.extractIfOption(partner), fieldResults);
    }
    // While calling `makeNullable`, we should respect the option order of `partner`
    // i.e. if the schema of `partner` is [int, null], the resultant schema should also be [int, null] rather than [null, int]
    // However, if this is a union-encoded-as-struct, don't make it nullable - it represents the union structure itself
    return (shouldResultBeOptional && !isUnionEncodedAsStruct) 
        ? SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner))
        : result;
  }

  /**
   * Helper method to get an appropriate default value for a schema type.
   * Used for union-encoded-as-struct fields that need non-null defaults.
   *
   * @param schema The schema to get a default value for
   * @return An appropriate default value, or null if none can be determined
   */
  private Object getDefaultValueForSchema(Schema schema) {
    switch (schema.getType()) {
      case INT:
        return 0;
      case LONG:
        return 0L;
      case FLOAT:
        return 0.0f;
      case DOUBLE:
        return 0.0;
      case BOOLEAN:
        return false;
      case STRING:
        return "";
      default:
        return null;
    }
  }

  @Override
  public Schema.Field field(String name, TypeInfo field, Schema.Field partner, Schema fieldResult) {
    // No need to infer `shouldResultBeOptional`. We expect other visitor methods to return optional schemas
    // in their field results if required
    if (partner == null) {
      // if there was no matching Avro field, use name from the Hive schema
      // Special case: for union-encoded-as-struct fields (tag, field0, field1, ...), keep them non-nullable
      // even though they don't have Avro partners, because they're part of the union encoding structure
      boolean isUnionEncodingField = name.equals("tag") || name.matches("field\\d+");
      if (isUnionEncodingField && !AvroSerdeUtils.isNullableType(fieldResult)) {
        // For union encoding fields, use a non-null default appropriate for the type
        Object defaultValue = getDefaultValueForSchema(fieldResult);
        return AvroCompatibilityHelper.createSchemaField(SchemaUtilities.makeCompatibleName(name), fieldResult, null,
            defaultValue);
      } else {
        // For regular fields not found in Avro, make them optional with null default
        return AvroCompatibilityHelper.createSchemaField(SchemaUtilities.makeCompatibleName(name), fieldResult, null,
            null);
      }
    } else {
      // TODO: How to ensure that field default value is compatible with new field type generated from Hive?
      // Copy field type from the visitor result, copy everything else from the partner
      // Avro requires the default value to match the first type in the option, reorder option if required
      Schema reordered = SchemaUtilities.reorderOptionIfRequired(fieldResult, SchemaUtilities.defaultValue(partner));
      return SchemaUtilities.copyField(partner, reordered);
    }
  }

  @Override
  public Schema list(ListTypeInfo list, Schema partner, Schema elementResult) {
    // if there was no matching Avro list, or if matching Avro list was an option, return an optional list
    boolean shouldResultBeOptional = partner == null || isNullableType(partner);
    Schema result = Schema.createArray(elementResult);
    // While calling `makeNullable`, we should respect the option order of `partner`
    // i.e. if the schema of `partner` is [int, null], the resultant schema should also be [int, null] rather than [null, int]
    return shouldResultBeOptional ? SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner))
        : result;
  }

  @Override
  public Schema map(MapTypeInfo map, Schema partner, Schema keyResult, Schema valueResult) {
    Preconditions.checkArgument(SchemaUtilities.extractIfOption(keyResult).getType() == Schema.Type.STRING,
        "Map keys should always be non-nullable strings. Found: %s", keyResult);
    // if there was no matching Avro map, or if matching Avro map was an option, return an optional map
    boolean shouldResultBeOptional = partner == null || isNullableType(partner);
    Schema result = Schema.createMap(valueResult);
    // While calling `makeNullable`, we should respect the option order of `partner`
    // i.e. if the schema of `partner` is [int, null], the resultant schema should also be [int, null] rather than [null, int]
    return shouldResultBeOptional ? SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner))
        : result;
  }

  @Override
  public Schema primitive(PrimitiveTypeInfo primitive, Schema partner) {
    boolean shouldResultBeOptional = partner == null || isNullableType(partner);
    Schema hivePrimitive = hivePrimitiveToAvro(primitive);
    // if there was no matching Avro primitive, use the Hive primitive
    Schema result = partner == null ? hivePrimitive : checkCompatibilityAndPromote(hivePrimitive, partner);
    // While calling `makeNullable`, we should respect the option order of `partner`
    // i.e. if the schema of `partner` is [int, null], the resultant schema should also be [int, null] rather than [null, int]
    return shouldResultBeOptional ? SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner))
        : result;
  }

  @Override
  public Schema union(UnionTypeInfo union, Schema partner, List<Schema> results) {
    if (SchemaUtilities.nullExistInUnion(partner)) {
      List<Schema> toAddNull = new ArrayList<>();
      toAddNull.add(Schema.create(Schema.Type.NULL));
      toAddNull.addAll(results);
      return Schema.createUnion(toAddNull);
    }
    return Schema.createUnion(results);
  }

  private Schema checkCompatibilityAndPromote(Schema schema, Schema partner) {
    // TODO: Check if schema is compatible with partner
    Schema extractedPartnerSchema = SchemaUtilities.extractIfOption(partner);
    switch (schema.getType()) {
      case BYTES:
        if (extractedPartnerSchema.getType().equals(Schema.Type.FIXED)) {
          return partner;
        }
        return schema;
      case STRING:
        if (extractedPartnerSchema.getType().equals(Schema.Type.ENUM)) {
          return partner;
        }
        return schema;
      default:
        return schema;
    }
  }

  /**
   * A {@link PartnerAccessor} which matches the requested field from a partner Avro struct by case insensitive
   * field name match
   */
  private static class AvroPartnerAccessor implements PartnerAccessor<Schema, Schema.Field> {
    private static final AvroPartnerAccessor INSTANCE = new AvroPartnerAccessor();

    private static final Schema MAP_KEY = Schema.create(Schema.Type.STRING);

    /**
     * Extracts the actual type from a schema that may be a union.
     * Handles two cases:
     * 1. Single-element unions like [{"type":"record",...}] - extracts the single type
     * 2. Nullable unions like ["null", "string"] - extracts the non-null type
     *
     * @param schema The schema to extract from
     * @return The extracted schema
     */
    private static Schema extractFromUnion(Schema schema) {
      if (schema.getType() == Schema.Type.UNION) {
        List<Schema> types = schema.getTypes();
        if (types.size() == 1) {
          // Single-element union: just extract that single type
          return types.get(0);
        }
      }
      // Extract the non-null type from nullable unions like ["null", "string"]
      return SchemaUtilities.extractIfOption(schema);
    }

    @Override
    public Schema.Field fieldPartner(Schema partner, String fieldName) {
      Schema schema = SchemaUtilities.extractIfOption(partner);
      
      // Special case: If the partner is a union and the field name matches the union-encoded-as-struct pattern,
      // this is a Hive union-encoded-as-struct. We need to return a synthetic field that wraps the corresponding union branch.
      // Check the ORIGINAL partner, not the extracted schema, because extractIfOption may unwrap 2-way unions
      if (partner.getType() == Schema.Type.UNION) {
        // Handle "tag" field - this is the discriminator for the union, should be non-nullable int
        if (fieldName.equalsIgnoreCase("tag")) {
          // Return a synthetic non-nullable int field for the tag
          // This ensures primitive() gets a non-null partner and doesn't make it nullable
          Schema intSchema = Schema.create(Schema.Type.INT);
          return AvroCompatibilityHelper.createSchemaField(fieldName, intSchema, "Union tag discriminator", 0);
        }
        
        // Handle "fieldN" fields - these correspond to union branches
        if (fieldName.matches("field\\d+")) {
          try {
            int fieldIndex = Integer.parseInt(fieldName.substring(5)); // Extract number from "fieldN"
            // Use the original partner (which is the union), not the extracted schema
            Schema unionWithoutNull = SchemaUtilities.discardNullFromUnionIfExist(partner);
            List<Schema> branches = unionWithoutNull.getTypes();
            if (fieldIndex < branches.size()) {
              // Create a synthetic field that wraps the union branch schema
              Schema branchSchema = branches.get(fieldIndex);
              return AvroCompatibilityHelper.createSchemaField(fieldName, branchSchema, null, null);
            }
          } catch (NumberFormatException e) {
            // Not a valid field index, fall through to regular logic
          }
        }
      }
      
      return (schema.getType() == Schema.Type.RECORD) ? findCaseInsensitive(schema, fieldName) : null;
    }

    @Override
    public Schema fieldType(Schema.Field partnerField) {
      return partnerField.schema();
    }

    @Override
    public Schema mapKeyPartner(Schema partner) {
      Schema schema = SchemaUtilities.extractIfOption(partner);
      return (schema.getType() == Schema.Type.MAP) ? MAP_KEY : null;
    }

    @Override
    public Schema mapValuePartner(Schema partner) {
      Schema schema = SchemaUtilities.extractIfOption(partner);
      if (schema.getType() != Schema.Type.MAP) {
        return null;
      }
      return extractFromUnion(schema.getValueType());
    }

    @Override
    public Schema listElementPartner(Schema partner) {
      Schema schema = SchemaUtilities.extractIfOption(partner);
      if (schema.getType() != Schema.Type.ARRAY) {
        return null;
      }
      return extractFromUnion(schema.getElementType());
    }

    @Override
    public Schema unionObjectPartner(Schema partner, int ordinal) {
      if (partner.getType() != Schema.Type.UNION) {
        return null;
      }
      Schema schema = SchemaUtilities.discardNullFromUnionIfExist(partner);

      return schema.getTypes().get(ordinal);
    }

    private Schema.Field findCaseInsensitive(Schema struct, String fieldName) {
      Preconditions.checkArgument(struct.getType() == Schema.Type.RECORD);
      // TODO: Optimize? This will be called for every struct field, we will run the for loop for every struct field
      for (Schema.Field field : struct.getFields()) {
        if (field.name().equalsIgnoreCase(fieldName)) {
          return field;
        }
      }
      return null;
    }
  }

  // Additional numeric type, similar to other logical type names in AvroSerde
  private static final String SHORT_TYPE_NAME = "short";
  private static final String BYTE_TYPE_NAME = "byte";

  // TODO: This should be refactored into a visitor if we ever require conversion of complex types
  public Schema hivePrimitiveToAvro(PrimitiveTypeInfo primitive) {
    switch (primitive.getPrimitiveCategory()) {
      case INT:
      case BYTE:
      case SHORT:
        return Schema.create(Schema.Type.INT);

      case LONG:
        return Schema.create(Schema.Type.LONG);

      case FLOAT:
        return Schema.create(Schema.Type.FLOAT);

      case DOUBLE:
        return Schema.create(Schema.Type.DOUBLE);

      case BOOLEAN:
        return Schema.create(Schema.Type.BOOLEAN);

      case CHAR:
      case STRING:
      case VARCHAR:
        return Schema.create(Schema.Type.STRING);

      case BINARY:
        return Schema.create(Schema.Type.BYTES);

      case VOID:
        return Schema.create(Schema.Type.NULL);

      case DATE:
        Schema dateSchema = Schema.create(Schema.Type.INT);
        dateSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DATE_TYPE_NAME);

        return dateSchema;

      case TIMESTAMP:
        Schema timestampSchema = Schema.create(Schema.Type.LONG);
        timestampSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.TIMESTAMP_TYPE_NAME);

        return timestampSchema;

      case DECIMAL:
        DecimalTypeInfo dti = (DecimalTypeInfo) primitive;
        JsonNodeFactory factory = JsonNodeFactory.instance;
        Schema decimalSchema = Schema.create(Schema.Type.BYTES);
        decimalSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DECIMAL_TYPE_NAME);
        AvroCompatibilityHelper.setSchemaPropFromJsonString(decimalSchema, AvroSerDe.AVRO_PROP_PRECISION,
            Jackson1Utils.toJsonString(factory.numberNode(dti.getPrecision())), false);
        AvroCompatibilityHelper.setSchemaPropFromJsonString(decimalSchema, AvroSerDe.AVRO_PROP_SCALE,
            Jackson1Utils.toJsonString(factory.numberNode(dti.getScale())), false);

        return decimalSchema;

      default:
        throw new UnsupportedOperationException(primitive + " is not supported.");
    }
  }
}

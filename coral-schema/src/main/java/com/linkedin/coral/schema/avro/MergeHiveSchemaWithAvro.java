/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
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
    return shouldResultBeOptional ? SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner))
        : result;
  }

  @Override
  public Schema.Field field(String name, TypeInfo field, Schema.Field partner, Schema fieldResult) {
    // No need to infer `shouldResultBeOptional`. We expect other visitor methods to return optional schemas
    // in their field results if required
    if (partner == null) {
      // if there was no matching Avro field, use name form the Hive schema and set a null default
      return AvroCompatibilityHelper.createSchemaField(SchemaUtilities.makeCompatibleName(name), fieldResult, null,
          null);
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

    @Override
    public Schema.Field fieldPartner(Schema partner, String fieldName) {
      Schema schema = SchemaUtilities.extractIfOption(partner);
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
      return (schema.getType() == Schema.Type.MAP) ? schema.getValueType() : null;
    }

    @Override
    public Schema listElementPartner(Schema partner) {
      Schema schema = SchemaUtilities.extractIfOption(partner);
      return (schema.getType() == Schema.Type.ARRAY) ? schema.getElementType() : null;
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

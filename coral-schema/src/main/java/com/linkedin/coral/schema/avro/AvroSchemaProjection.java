/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.com.google.common.base.Preconditions;

import static com.linkedin.coral.schema.avro.AvroSerdeUtils.*;


/**
 * Projects an Avro field onto the structure requested by a Calcite type, keeping everything the Avro schema says
 * about what remains: spelling, record/field metadata, aliases, nullability, logical/fixed/enum identities and
 * defaults.
 *
 * <p>This is the structural projection behind a fuzzy-UNION {@code generic_project}: the requested type selects and
 * orders record fields (recursively through arrays and maps), it never adds fields, renames fields or changes a
 * primitive representation. A requested field that the source does not have, that matches several source fields
 * that differ only in casing, or whose representation differs from the source, is an error that names the path.
 * Nullability is not taken from the requested type; the source's null envelopes are kept as they are.
 */
final class AvroSchemaProjection {
  private AvroSchemaProjection() {
  }

  /**
   * @param source the Avro field of the projected operand, which the result keeps the identity of
   * @param sourceType the relational type of the operand
   * @param targetType the requested relational type, which has to be a projection of {@code sourceType}
   * @return a new field that carries the projected schema, and the source field's metadata and (projected) default
   */
  static Schema.Field project(@Nonnull Schema.Field source, @Nonnull RelDataType sourceType,
      @Nonnull RelDataType targetType) {
    Preconditions.checkNotNull(source);
    Preconditions.checkNotNull(sourceType);
    Preconditions.checkNotNull(targetType);
    return projectField(source, sourceType, targetType, source.name(),
        Collections.newSetFromMap(new IdentityHashMap<>()));
  }

  private static Schema.Field projectField(Schema.Field field, RelDataType sourceType, RelDataType targetType,
      String path, Set<Schema> active) {
    Schema projected = projectSchema(field.schema(), sourceType, targetType, path, active);
    if (projected == field.schema() || !AvroCompatibilityHelper.fieldHasDefault(field)) {
      return SchemaUtilities.cloneField(field, field.name(), projected, field.doc());
    }

    Object defaultValue =
        projectDefault(new JsonReader(AvroCompatibilityHelper.getDefaultValueAsJsonString(field)).readValue(),
            field.schema(), projected, path);
    Schema.Field copy = AvroCompatibilityHelper.newField(field).setSchema(projected).setDefault(defaultValue).build();
    field.aliases().forEach(copy::addAlias);
    return copy;
  }

  private static Schema projectSchema(Schema schema, RelDataType sourceType, RelDataType targetType, String path,
      Set<Schema> active) {
    if (schema.getType() == Schema.Type.UNION) {
      if (!isNullableType(schema)) {
        // A union with several members is opaque; it can be kept, but not reshaped
        if (sameRepresentation(sourceType, targetType)) {
          return schema;
        }
        throw error(path,
            "a union with several members cannot be reshaped, requested " + targetType.getFullTypeString());
      }
      Schema option = getOtherTypeFromNullableType(schema);
      Schema projected = projectSchema(option, sourceType, targetType, path, active);
      if (projected == option) {
        return schema;
      }
      Schema nullSchema = Schema.create(Schema.Type.NULL);
      return Schema.createUnion(SchemaUtilities.isNullSecond(schema) ? Arrays.asList(projected, nullSchema)
          : Arrays.asList(nullSchema, projected));
    }

    switch (schema.getType()) {
      case RECORD:
        return projectRecord(schema, sourceType, targetType, path, active);
      case ARRAY:
        if (!isOfType(sourceType, SqlTypeName.ARRAY) || !isOfType(targetType, SqlTypeName.ARRAY)) {
          throw mismatch(path, schema, sourceType, targetType);
        }
        Schema element = projectSchema(schema.getElementType(), sourceType.getComponentType(),
            targetType.getComponentType(), path + ".items", active);
        return element == schema.getElementType() ? schema : SchemaUtilities.createArrayLike(schema, element);
      case MAP:
        if (!isOfType(sourceType, SqlTypeName.MAP) || !isOfType(targetType, SqlTypeName.MAP)) {
          throw mismatch(path, schema, sourceType, targetType);
        }
        if (!SqlTypeName.CHAR_TYPES.contains(targetType.getKeyType().getSqlTypeName())) {
          throw error(path,
              "the key of a map must be a string, requested " + targetType.getKeyType().getFullTypeString());
        }
        Schema value = projectSchema(schema.getValueType(), sourceType.getValueType(), targetType.getValueType(),
            path + ".values", active);
        return value == schema.getValueType() ? schema : SchemaUtilities.createMapLike(schema, value);
      default:
        // BOOLEAN, INT, LONG, FLOAT, DOUBLE, BYTES, STRING, FIXED, ENUM and NULL are kept as they are, which is only
        // valid if the request denotes the same value representation
        if (schema.getType() != Schema.Type.NULL && !sameLeafRepresentation(schema, sourceType, targetType)) {
          throw mismatch(path, schema, sourceType, targetType);
        }
        return schema;
    }
  }

  private static Schema projectRecord(Schema record, RelDataType sourceType, RelDataType targetType, String path,
      Set<Schema> active) {
    if (!sourceType.isStruct() || !targetType.isStruct()) {
      throw mismatch(path, record, sourceType, targetType);
    }
    if (!active.add(record)) {
      throw error(path, "recursive schemas are not supported");
    }

    try {
      boolean changed = targetType.getFieldCount() != record.getFields().size();
      List<Schema.Field> fields = new ArrayList<>();
      for (RelDataTypeField requested : targetType.getFieldList()) {
        Schema.Field sourceField = findField(record, requested.getName(), path);
        RelDataTypeField sourceTypeField = sourceType.getField(requested.getName(), false, false);
        if (sourceTypeField == null) {
          throw error(path,
              "field '" + requested.getName() + "' is not in the source type " + sourceType.getFullTypeString());
        }

        Schema.Field projected = projectField(sourceField, sourceTypeField.getType(), requested.getType(),
            path + "." + sourceField.name(), active);
        changed |= projected.schema() != sourceField.schema() || record.getFields().get(fields.size()) != sourceField;
        fields.add(projected);
      }
      return changed ? SchemaUtilities.newRecord(record, record.getName(), record.getNamespace(), fields) : record;
    } finally {
      active.remove(record);
    }
  }

  /** The only field whose name matches ignoring case; aliases are not lookup keys and ambiguity is never resolved. */
  private static Schema.Field findField(Schema record, String name, String path) {
    List<Schema.Field> matches =
        record.getFields().stream().filter(f -> f.name().equalsIgnoreCase(name)).collect(Collectors.toList());
    if (matches.isEmpty()) {
      throw error(path, "requested field '" + name + "' is not in record " + record.getFullName() + " with fields "
          + record.getFields().stream().map(Schema.Field::name).collect(Collectors.toList()));
    }
    if (matches.size() > 1) {
      throw error(path, "requested field '" + name + "' is ambiguous in record " + record.getFullName()
          + ", candidates: " + matches.stream().map(Schema.Field::name).collect(Collectors.toList()));
    }
    return matches.get(0);
  }

  private static boolean isOfType(RelDataType type, SqlTypeName typeName) {
    return type.getSqlTypeName() == typeName;
  }

  /**
   * A leaf is projected only if the request denotes the representation the Avro schema actually has. A request that
   * spells out its precision (BINARY(8), TIMESTAMP(6), DECIMAL(10,2)) is compared with the parameters the Avro schema
   * defines (fixed size, timestamp unit, decimal precision and scale); a request without parameters has to equal
   * the relational type the source was converted to.
   */
  private static boolean sameLeafRepresentation(Schema schema, RelDataType source, RelDataType target) {
    SqlTypeName name = target.getSqlTypeName();
    if (source.getSqlTypeName() == name && hasRepresentationParameters(name) && hasExplicitParameters(target)) {
      int[] expected = avroParameters(schema);
      if (expected != null) {
        return expected[0] == target.getPrecision()
            && (name != SqlTypeName.DECIMAL || expected[1] == target.getScale());
      }
    }
    return sameLeafRepresentation(source, target);
  }

  /** Fixed size, timestamp/time unit digits, or decimal precision and scale of the Avro schema; null if none. */
  private static int[] avroParameters(Schema schema) {
    if (schema.getType() == Schema.Type.FIXED
        && AvroCompatibilityHelper.getSchemaPropAsJsonString(schema, "logicalType") == null) {
      return new int[] { schema.getFixedSize(), 0 };
    }
    String logicalType = AvroCompatibilityHelper.getSchemaPropAsJsonString(schema, "logicalType");
    if (logicalType == null) {
      return null;
    }
    switch (logicalType.replace("\"", "")) {
      case "timestamp-millis":
      case "local-timestamp-millis":
      case "time-millis":
        return new int[] { 3, 0 };
      case "timestamp-micros":
      case "local-timestamp-micros":
      case "time-micros":
        return new int[] { 6, 0 };
      case "decimal":
        String precision = AvroCompatibilityHelper.getSchemaPropAsJsonString(schema, "precision");
        String scale = AvroCompatibilityHelper.getSchemaPropAsJsonString(schema, "scale");
        return precision == null ? null
            : new int[] { Integer.parseInt(precision), scale == null ? 0 : Integer.parseInt(scale) };
      default:
        return null;
    }
  }

  /** True if the type spells out its precision, as opposed to the default of its type name. */
  private static boolean hasExplicitParameters(RelDataType type) {
    return type.toString().indexOf('(') >= 0;
  }

  private static boolean sameLeafRepresentation(RelDataType source, RelDataType target) {
    SqlTypeName sourceName = source.getSqlTypeName();
    SqlTypeName targetName = target.getSqlTypeName();
    if (sourceName == targetName) {
      return !hasRepresentationParameters(sourceName)
          || (source.getPrecision() == target.getPrecision() && source.getScale() == target.getScale());
    }
    return (SqlTypeName.CHAR_TYPES.contains(sourceName) && SqlTypeName.CHAR_TYPES.contains(targetName))
        || (SqlTypeName.BINARY_TYPES.contains(sourceName) && SqlTypeName.BINARY_TYPES.contains(targetName));
  }

  /** Types whose precision or scale changes the Avro representation (fixed size, timestamp unit, decimal). */
  private static boolean hasRepresentationParameters(SqlTypeName typeName) {
    return typeName == SqlTypeName.DECIMAL || typeName == SqlTypeName.BINARY || typeName == SqlTypeName.TIMESTAMP
        || typeName == SqlTypeName.TIME;
  }

  private static boolean sameRepresentation(RelDataType source, RelDataType target) {
    if (source.isStruct() || target.isStruct()) {
      if (!source.isStruct() || !target.isStruct() || source.getFieldCount() != target.getFieldCount()) {
        return false;
      }
      for (int i = 0; i < source.getFieldCount(); i++) {
        RelDataTypeField s = source.getFieldList().get(i);
        RelDataTypeField t = target.getFieldList().get(i);
        if (!s.getName().equalsIgnoreCase(t.getName()) || !sameRepresentation(s.getType(), t.getType())) {
          return false;
        }
      }
      return true;
    }
    if (isOfType(source, SqlTypeName.ARRAY) && isOfType(target, SqlTypeName.ARRAY)) {
      return sameRepresentation(source.getComponentType(), target.getComponentType());
    }
    if (isOfType(source, SqlTypeName.MAP) && isOfType(target, SqlTypeName.MAP)) {
      return sameRepresentation(source.getKeyType(), target.getKeyType())
          && sameRepresentation(source.getValueType(), target.getValueType());
    }
    return sameLeafRepresentation(source, target);
  }

  /**
   * Projects a default value, given as parsed JSON, from the schema a field had to the schema it now has. Only a
   * record loses or reorders members, following the projected record's fields; array elements and map values are
   * projected one by one and everything else is kept.
   */
  private static Object projectDefault(Object value, Schema from, Schema to, String path) {
    if (from == to || value == JsonProperties.NULL_VALUE) {
      return value;
    }

    switch (from.getType()) {
      case UNION:
        return projectDefault(value, getOtherTypeFromNullableType(from), getOtherTypeFromNullableType(to), path);
      case RECORD:
        Map<?, ?> members = (Map<?, ?>) value;
        Map<String, Object> projected = new LinkedHashMap<>();
        for (Schema.Field field : to.getFields()) {
          if (members.containsKey(field.name())) {
            projected.put(field.name(), projectDefault(members.get(field.name()), from.getField(field.name()).schema(),
                field.schema(), path + "." + field.name()));
          } else if (!AvroCompatibilityHelper.fieldHasDefault(field)) {
            throw error(path, "the default value has no member for field '" + field.name() + "'");
          }
        }
        return projected;
      case ARRAY:
        List<Object> elements = new ArrayList<>();
        for (Object element : (List<?>) value) {
          elements.add(projectDefault(element, from.getElementType(), to.getElementType(), path + ".items"));
        }
        return elements;
      case MAP:
        Map<String, Object> entries = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
          entries.put((String) entry.getKey(),
              projectDefault(entry.getValue(), from.getValueType(), to.getValueType(), path + ".values"));
        }
        return entries;
      default:
        return value;
    }
  }

  private static RuntimeException mismatch(String path, Schema schema, RelDataType source, RelDataType target) {
    boolean leaf = schema.getType() != Schema.Type.RECORD && schema.getType() != Schema.Type.ARRAY
        && schema.getType() != Schema.Type.MAP;
    return error(path, "avro " + (leaf ? schema.toString() : schema.getType().toString()) + " of relational type "
        + source.getFullTypeString() + " cannot be projected as " + target.getFullTypeString());
  }

  private static RuntimeException error(String path, String message) {
    return new IllegalArgumentException("Cannot apply generic_project to " + path + ": " + message);
  }

  /** Reads the JSON text of a field default into Maps, Lists, Strings, Numbers, Booleans and NULL_VALUE. */
  private static final class JsonReader {
    private final String text;
    private int position;

    private JsonReader(String text) {
      this.text = text;
    }

    private Object readValue() {
      skipWhitespace();
      char c = text.charAt(position);
      switch (c) {
        case '{':
          return readObject();
        case '[':
          return readArray();
        case '"':
          return readString();
        case 't':
          position += 4;
          return Boolean.TRUE;
        case 'f':
          position += 5;
          return Boolean.FALSE;
        case 'n':
          position += 4;
          return JsonProperties.NULL_VALUE;
        default:
          return readNumber();
      }
    }

    private Map<String, Object> readObject() {
      Map<String, Object> object = new LinkedHashMap<>();
      position++;
      skipWhitespace();
      if (text.charAt(position) == '}') {
        position++;
        return object;
      }
      while (true) {
        skipWhitespace();
        String key = readString();
        skipWhitespace();
        position++; // ':'
        object.put(key, readValue());
        skipWhitespace();
        if (text.charAt(position++) == '}') {
          return object;
        }
      }
    }

    private List<Object> readArray() {
      List<Object> array = new ArrayList<>();
      position++;
      skipWhitespace();
      if (text.charAt(position) == ']') {
        position++;
        return array;
      }
      while (true) {
        array.add(readValue());
        skipWhitespace();
        if (text.charAt(position++) == ']') {
          return array;
        }
      }
    }

    private String readString() {
      StringBuilder string = new StringBuilder();
      position++;
      while (text.charAt(position) != '"') {
        char c = text.charAt(position++);
        if (c != '\\') {
          string.append(c);
          continue;
        }
        char escaped = text.charAt(position++);
        switch (escaped) {
          case 'b':
            string.append('\b');
            break;
          case 'f':
            string.append('\f');
            break;
          case 'n':
            string.append('\n');
            break;
          case 'r':
            string.append('\r');
            break;
          case 't':
            string.append('\t');
            break;
          case 'u':
            string.append((char) Integer.parseInt(text.substring(position, position + 4), 16));
            position += 4;
            break;
          default:
            string.append(escaped);
        }
      }
      position++;
      return string.toString();
    }

    private Number readNumber() {
      int start = position;
      while (position < text.length() && "+-0123456789.eE".indexOf(text.charAt(position)) >= 0) {
        position++;
      }
      String number = text.substring(start, position);
      if (number.indexOf('.') >= 0 || number.indexOf('e') >= 0 || number.indexOf('E') >= 0) {
        return Double.valueOf(number);
      }
      long value = Long.parseLong(number);
      return value == (int) value ? (Number) Integer.valueOf((int) value) : (Number) Long.valueOf(value);
    }

    private void skipWhitespace() {
      while (Character.isWhitespace(text.charAt(position))) {
        position++;
      }
    }
  }
}

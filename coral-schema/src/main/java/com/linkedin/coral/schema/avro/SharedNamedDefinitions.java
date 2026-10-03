/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;

import static com.linkedin.coral.schema.avro.AvroSerdeUtils.*;


/**
 * Makes every use of one named record in a merged schema denote one definition.
 *
 * <p>A named record can be used several times in a schema (as a field, an array element, a map value, ...), and its
 * full name has to denote a single definition. While a UNION result is merged field by field, each use is ordered
 * on its own by the defaults retained at that use, so a use without a default and a use with a default can end up
 * with different bodies (only the order of a nullable option differs) under one name, which cannot be serialized.
 *
 * <p>This pass, which works on one finished merge result, groups the uses by the full name of the output, collects the
 * retained default demands on every nullable option of every definition (from all uses, including uses without a
 * default), selects the one order that satisfies them all, and relinks every use to one rebuilt body. Names, field
 * sets, field order, defaults and metadata are not changed; a use that cannot denote the same type as the others (a
 * different retained body) or demands contradicting orders is rejected with the name and the paths of both uses.
 * Source schemas are never modified and nothing is registered across conversions.
 */
final class SharedNamedDefinitions {
  private final Map<String, List<Use>> usesByName = new LinkedHashMap<>();
  private final Map<String, Demand> demands = new HashMap<>();
  private final Map<String, Schema> rebuilt = new HashMap<>();
  private final Set<String> rebuilding = new HashSet<>();

  private SharedNamedDefinitions() {
  }

  static Schema unify(@Nonnull Schema root) {
    if (root.getType() != Schema.Type.RECORD) {
      return root;
    }
    SharedNamedDefinitions unifier = new SharedNamedDefinitions();
    try {
      unifier.collect(root, root.getName(), Collections.newSetFromMap(new IdentityHashMap<>()));
    } catch (RecursiveSchemaException e) {
      // Recursive schemas are not newly supported here; a recursive input keeps passing through unchanged
      return root;
    }
    unifier.checkBodies();
    return unifier.rebuild(root.getFullName());
  }

  private static final class RecursiveSchemaException extends RuntimeException {
    private RecursiveSchemaException() {
      super("recursive schemas are not supported", null, false, false);
    }
  }

  private static final class Use {
    private final Schema body;
    private final String path;

    private Use(Schema body, String path) {
      this.body = body;
      this.path = path;
    }
  }

  /** The first alternative a retained default needs at one nullable option, and where the default is. */
  private static final class Demand {
    private final boolean nullFirst;
    private final String origin;

    private Demand(boolean nullFirst, String origin) {
      this.nullFirst = nullFirst;
      this.origin = origin;
    }
  }

  private void collect(Schema record, String path, Set<Schema> active) {
    if (!active.add(record)) {
      throw new IllegalArgumentException(
          "Cannot unify the uses of " + record.getFullName() + " at " + path + ": recursive schemas are not supported");
    }
    usesByName.computeIfAbsent(record.getFullName(), k -> new ArrayList<>()).add(new Use(record, path));
    for (Schema.Field field : record.getFields()) {
      String fieldPath = path + "." + field.name();
      collectUses(field.schema(), fieldPath, active);
      if (AvroCompatibilityHelper.fieldHasDefault(field)) {
        walkDefault(field.schema(), AvroSchemaProjection.defaultValue(field), record.getFullName(),
            Collections.singletonList(field.name()), fieldPath + ".default");
      }
    }
    active.remove(record);
  }

  private void collectUses(Schema schema, String path, Set<Schema> active) {
    switch (schema.getType()) {
      case RECORD:
        collect(schema, path, active);
        break;
      case UNION:
        schema.getTypes().forEach(type -> collectUses(type, path, active));
        break;
      case ARRAY:
        collectUses(schema.getElementType(), path + ".items", active);
        break;
      case MAP:
        collectUses(schema.getValueType(), path + ".values", active);
        break;
      default:
        break;
    }
  }

  /**
   * Records the first alternative that {@code value}, a retained default of {@code schema}, needs at each nullable
   * option it reaches. Positions are relative to the innermost named record, so every use of a record contributes to
   * the same positions.
   */
  private void walkDefault(Schema schema, Object value, String owner, List<String> path, String origin) {
    Schema type = schema;
    if (schema.getType() == Schema.Type.UNION) {
      if (!isNullableType(schema)) {
        return;
      }
      demand(owner, path, value == JsonProperties.NULL_VALUE, origin);
      if (value == JsonProperties.NULL_VALUE) {
        return;
      }
      type = getOtherTypeFromNullableType(schema);
    }

    switch (type.getType()) {
      case RECORD:
        if (value instanceof Map) {
          for (Schema.Field member : type.getFields()) {
            if (((Map<?, ?>) value).containsKey(member.name())) {
              walkDefault(member.schema(), ((Map<?, ?>) value).get(member.name()), type.getFullName(),
                  Collections.singletonList(member.name()), origin + "." + member.name());
            }
          }
        }
        break;
      case ARRAY:
        if (value instanceof List) {
          List<String> elementPath = extend(path, "items");
          int index = 0;
          for (Object element : (List<?>) value) {
            walkDefault(type.getElementType(), element, owner, elementPath, origin + "[" + index++ + "]");
          }
        }
        break;
      case MAP:
        if (value instanceof Map) {
          List<String> valuePath = extend(path, "values");
          for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            walkDefault(type.getValueType(), entry.getValue(), owner, valuePath, origin + "['" + entry.getKey() + "']");
          }
        }
        break;
      default:
        break;
    }
  }

  private void demand(String owner, List<String> path, boolean nullFirst, String origin) {
    String key = key(owner, path);
    Demand existing = demands.get(key);
    if (existing == null) {
      demands.put(key, new Demand(nullFirst, origin));
    } else if (existing.nullFirst != nullFirst) {
      throw new IllegalArgumentException("Cannot order the options of " + owner + " member " + String.join(".", path)
          + ": the retained default at " + existing.origin + " needs the " + (existing.nullFirst ? "null" : "non-null")
          + " option first, the retained default at " + origin + " needs the " + (nullFirst ? "null" : "non-null")
          + " option first");
    }
  }

  /** Every use of one full name has to be the same definition, except for the order of its nullable options. */
  private void checkBodies() {
    for (Map.Entry<String, List<Use>> entry : usesByName.entrySet()) {
      List<Use> uses = entry.getValue();
      String base = signature(uses.get(0).body);
      for (Use use : uses) {
        if (!signature(use.body).equals(base)) {
          throw new IllegalArgumentException("Cannot use " + entry.getKey() + " for different definitions: the use at "
              + uses.get(0).path + " is " + base + " but the use at " + use.path + " is " + signature(use.body));
        }
      }
    }
  }

  private Schema rebuild(String fullName) {
    Schema done = rebuilt.get(fullName);
    if (done != null) {
      return done;
    }
    if (!rebuilding.add(fullName)) {
      throw new IllegalArgumentException(
          "Cannot unify the uses of " + fullName + ": recursive schemas are not supported");
    }
    Schema base = usesByName.get(fullName).get(0).body;
    List<Schema.Field> fields = new ArrayList<>();
    for (Schema.Field field : base.getFields()) {
      Schema schema = relink(field.schema(), fullName, Collections.singletonList(field.name()));
      fields.add(SchemaUtilities.cloneField(field, field.name(), schema, field.doc()));
    }
    Schema record = SchemaUtilities.newRecord(base, base.getName(), base.getNamespace(), fields);
    rebuilding.remove(fullName);
    rebuilt.put(fullName, record);
    return record;
  }

  private Schema relink(Schema schema, String owner, List<String> path) {
    switch (schema.getType()) {
      case RECORD:
        return rebuild(schema.getFullName());
      case UNION:
        if (isNullableType(schema)) {
          Schema option = relink(getOtherTypeFromNullableType(schema), owner, path);
          Demand demand = demands.get(key(owner, path));
          boolean nullFirst = demand != null ? demand.nullFirst : !SchemaUtilities.isNullSecond(schema);
          Schema nullSchema = Schema.create(Schema.Type.NULL);
          return Schema.createUnion(nullFirst ? Arrays.asList(nullSchema, option) : Arrays.asList(option, nullSchema));
        }
        List<Schema> members = new ArrayList<>();
        schema.getTypes().forEach(type -> members.add(relink(type, owner, path)));
        return Schema.createUnion(members);
      case ARRAY:
        return SchemaUtilities.createArrayLike(schema, relink(schema.getElementType(), owner, extend(path, "items")));
      case MAP:
        return SchemaUtilities.createMapLike(schema, relink(schema.getValueType(), owner, extend(path, "values")));
      default:
        return schema;
    }
  }

  /**
   * What makes two uses the same definition: fields (name, order, type, default, properties) and record properties.
   * Docs and aliases may differ, and the order of a nullable option is reconciled separately. Named types inside are
   * compared by their own full name.
   */
  private static String signature(Schema schema) {
    switch (schema.getType()) {
      case RECORD:
        StringBuilder record = new StringBuilder("record " + schema.getFullName() + props(schema) + " {");
        for (Schema.Field field : schema.getFields()) {
          record.append(field.name()).append(": ").append(signature(field.schema()));
          if (AvroCompatibilityHelper.fieldHasDefault(field)) {
            record.append(" = ").append(AvroCompatibilityHelper.getDefaultValueAsJsonString(field));
          }
          record.append(field.order() == Schema.Field.Order.ASCENDING ? "" : " " + field.order());
          record.append(fieldProps(field)).append("; ");
        }
        return record.append("}").toString();
      case UNION:
        if (isNullableType(schema)) {
          return "nullable " + signature(getOtherTypeFromNullableType(schema));
        }
        List<String> members = new ArrayList<>();
        schema.getTypes().forEach(type -> members.add(signature(type)));
        return "union " + members;
      case ARRAY:
        return "array<" + signature(schema.getElementType()) + ">" + props(schema);
      case MAP:
        return "map<" + signature(schema.getValueType()) + ">" + props(schema);
      case ENUM:
        return "enum " + schema.getFullName() + schema.getEnumSymbols();
      case FIXED:
        return "fixed " + schema.getFullName() + " " + schema.getFixedSize() + props(schema);
      default:
        return schema.getType() + props(schema);
    }
  }

  private static String props(Schema schema) {
    Map<String, String> props = new TreeMap<>();
    for (String name : AvroCompatibilityHelper.getAllPropNames(schema)) {
      props.put(name, AvroCompatibilityHelper.getSchemaPropAsJsonString(schema, name));
    }
    return props.isEmpty() ? "" : String.valueOf(props);
  }

  private static String fieldProps(Schema.Field field) {
    Map<String, String> props = new TreeMap<>();
    for (String name : AvroCompatibilityHelper.getAllPropNames(field)) {
      props.put(name, AvroCompatibilityHelper.getFieldPropAsJsonString(field, name));
    }
    return props.isEmpty() ? "" : String.valueOf(props);
  }

  private static List<String> extend(List<String> path, String step) {
    List<String> extended = new ArrayList<>(path);
    extended.add(step);
    return extended;
  }

  private static String key(String owner, List<String> path) {
    return owner + "#" + String.join("/", path);
  }
}

/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.base.Throwables;
import com.linkedin.coral.com.google.common.collect.ImmutableList;

import static org.apache.calcite.sql.type.SqlTypeName.*;


/**
 * A TranslatableTable (ViewTable) version of HiveTable that supports
 * recursive expansion of view definitions
 */
public class HiveViewTable extends HiveTable implements TranslatableTable {
  private final List<String> schemaPath;

  /**
   * Constructor to create bridge from hive table to calcite table
   *
   * @param hiveTable Hive table
   * @param schemaPath Calcite schema path
   */
  public HiveViewTable(Table hiveTable, List<String> schemaPath) {
    super(hiveTable);
    this.schemaPath = schemaPath;
  }

  @Override
  public RelNode toRel(RelOptTable.ToRelContext relContext, RelOptTable relOptTable) {
    try {
      RelRoot root = relContext.expandView(relOptTable.getRowType(), hiveTable.getViewExpandedText(), schemaPath,
          ImmutableList.of(hiveTable.getTableName()));
      root = root.withRel(createCastRel(root.rel, relOptTable.getRowType(), RelFactories.DEFAULT_PROJECT_FACTORY));
      //root = root.withRel(RelOptUtil.createCastRel(root.rel, relOptTable.getRowType()));
      return root.rel;
    } catch (Exception e) {
      Throwables.propagateIfInstanceOf(e, RuntimeException.class);
      throw new RuntimeException("Error while parsing view definition", e);
    }
  }

  public static RelNode createCastRel(final RelNode rel, RelDataType castRowType,
      RelFactories.ProjectFactory projectFactory) {
    Preconditions.checkNotNull(projectFactory);

    RelDataType rowType = rel.getRowType();
    RelDataTypeFactory typeFactory = rel.getCluster().getTypeFactory();
    if (isRowCastRequired(rowType, castRowType, typeFactory)) {
      final RexBuilder rexBuilder = rel.getCluster().getRexBuilder();
      final List<RexNode> castExps = RexUtil.generateCastExpressions(rexBuilder, castRowType, rowType);
      return projectFactory.createProject(rel, castExps, rowType.getFieldNames());
    } else {
      return rel;
    }
  }

  // Hive-based Dali readers allow extra fields on struct type columns to flow through. We
  // try to match that behavior. Hive-based Dali readers do not allow top level columns to flow through
  // Returns true if an explicit cast is required from rowType to castRowType, false otherwise
  private static boolean isRowCastRequired(RelDataType rowType, RelDataType castRowType,
      RelDataTypeFactory typeFactory) {
    if (rowType == castRowType) {
      return false;
    }
    List<RelDataTypeField> relFields = rowType.getFieldList();
    List<RelDataTypeField> castFields = castRowType.getFieldList();
    if (relFields.size() != castFields.size()) {
      return true;
    }
    // the following method will return false if the schema evolution is backward compatible.
    // i.e., when new fields are inserted to the middle or appended to the end of the field list.
    return isFieldListCastRequired(relFields, castFields, typeFactory);
  }

  // Returns true if an explicit cast is required from inputType to castToType, false otherwise
  // From what we noticed, cast will be required when there's schema promotion
  private static boolean isTypeCastRequired(RelDataType inputType, RelDataType castToType,
      RelDataTypeFactory typeFactory) {
    if (inputType == castToType) {
      return false;
    }
    if (inputType.getSqlTypeName() == ANY || castToType.getSqlTypeName() == ANY) {
      return false;
    }
    // Hive has string type which is varchar(65k). This results in lot of redundant
    // cast operations due to different precision(length) of varchar.
    // Also, all projections of string literals are of type CHAR but the hive schema
    // may mark that column as string. This again results in unnecessary cast operations.
    // We avoid all these casts
    if ((inputType.getSqlTypeName() == CHAR || inputType.getSqlTypeName() == VARCHAR)
        && castToType.getSqlTypeName() == VARCHAR) {
      return false;
    }

    // Make sure both source and target has same root SQL Type
    if (inputType.getSqlTypeName() != castToType.getSqlTypeName()) {
      return true;
    }

    // Calcite gets the castToType from the view schema, and it's nullable by default, but the inputType
    // is inferred from the view sql and for some columns where functions like named_strcut, ISNULL are applied,
    // calcite will set the inputType to be non-nullable. Thus it generates unnecessary cast operators.
    // we should ignore the nullability check when deciding whether to do field casting
    RelDataType nullableFieldDataType = typeFactory.createTypeWithNullability(inputType, true);
    switch (inputType.getSqlTypeName()) {
      case ARRAY:
        return isTypeCastRequired(inputType.getComponentType(), castToType.getComponentType(), typeFactory);
      case MAP:
        return isTypeCastRequired(inputType.getKeyType(), castToType.getKeyType(), typeFactory)
            || isTypeCastRequired(inputType.getValueType(), inputType.getValueType(), typeFactory);
      case ROW:
        return isFieldListCastRequired(inputType.getFieldList(), castToType.getFieldList(), typeFactory);
      default:
        return !nullableFieldDataType.equals(castToType);
    }
  }

  /**
   * The method will check if the inputFields has to be casted to castToFields.
   *
   * e.g.
   * (1) if castToFields = List(f1, f3), inputFields = List(f1, f2, f3), then return false.
   * (2) if castToFields = List(f1, f3), inputFields = List(f1, f3, f4), then return false.
   * (3) if castToFields = List(f1, f3), inputFields = List(f1, f4), then return true.
   *
   * @param inputFields a list of RelDataTypeField to cast from if required.
   * @param castToFields a list of RelDataTypeField to cast to if required.
   *
   * @return if a CAST operator will be generated from inputFields to castToFields
   */
  private static boolean isFieldListCastRequired(List<RelDataTypeField> inputFields,
      List<RelDataTypeField> castToFields, RelDataTypeFactory typeFactory) {
    if (inputFields.size() < castToFields.size()) {
      return true;
    }

    int inputIndex = 0;
    int inputFieldsSize = inputFields.size();
    for (RelDataTypeField castToField : castToFields) {
      while (inputIndex < inputFieldsSize) {
        RelDataTypeField inputField = inputFields.get(inputIndex);
        if (inputField.getName().equalsIgnoreCase(castToField.getName())) {
          if (isTypeCastRequired(inputField.getType(), castToField.getType(), typeFactory)) {
            return true;
          }
          break;
        } else {
          ++inputIndex;
        }
      }
      // If there's no match for a field in castToFields to inputFields
      if (inputIndex++ >= inputFieldsSize) {
        return true;
      }
    }
    return false;
  }
}

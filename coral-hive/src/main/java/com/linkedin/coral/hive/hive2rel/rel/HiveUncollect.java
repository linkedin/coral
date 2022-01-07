/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.rel;

import java.util.List;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelInput;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.SqlUnnestOperator;
import org.apache.calcite.sql.type.MapSqlType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RelNode to represent Hive's Uncollect semantics.
 * Hive semantics differ from Calcite Uncollect in handling operand types array(struct).
 * For array(struct), Calcite flattens internal struct to multiple columns whereas Hive
 * returns single column of struct type
 */
public class HiveUncollect extends Uncollect {
  private static final Logger LOGGER = LoggerFactory.getLogger(HiveUncollect.class);

  public HiveUncollect(RelOptCluster cluster, RelTraitSet traitSet, RelNode input, boolean withOrdinality) {
    super(cluster, traitSet, input, withOrdinality);
  }

  public HiveUncollect(RelInput input) {
    super(input);
  }

  @Override
  public RelNode copy(RelTraitSet traitSet, RelNode input) {
    assert traitSet.containsIfApplicable(Convention.NONE);
    HiveUncollect result = new HiveUncollect(getCluster(), traitSet, input, withOrdinality);
    // copy the rowType as well
    result.rowType = this.rowType;
    return result;
  }

  /**
   * Create a copy of this HiveUncollect object with the specified rowType.
   * @param rowType rowType of the newly created HiveUncollect object
   * @return a new HiveUncollect object
   */
  public RelNode copy(RelDataType rowType) {
    assert traitSet.containsIfApplicable(Convention.NONE);
    HiveUncollect result = new HiveUncollect(getCluster(), traitSet, input, withOrdinality);
    // set the rowType of the new HiveUncollect object
    result.rowType = rowType;
    return result;
  }

  @Override
  protected RelDataType deriveRowType() {
    if (rowType != null) {
      return rowType;
    }
    RelDataType inputType = input.getRowType();
    assert inputType.isStruct() : inputType + " is not a struct";
    final List<RelDataTypeField> fields = inputType.getFieldList();
    final RelDataTypeFactory.Builder builder = input.getCluster().getTypeFactory().builder();
    for (RelDataTypeField field : fields) {
      if (field.getType() instanceof MapSqlType) {
        builder.add(SqlUnnestOperator.MAP_KEY_COLUMN_NAME, field.getType().getKeyType());
        builder.add(SqlUnnestOperator.MAP_VALUE_COLUMN_NAME, field.getType().getValueType());
      } else {
        RelDataType ret = field.getType().getComponentType();
        builder.add(field.getName(), ret);
      }
    }
    if (withOrdinality) {
      builder.add(SqlUnnestOperator.ORDINALITY_COLUMN_NAME, SqlTypeName.INTEGER);
    }
    return builder.build();
  }
}

/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import org.apache.calcite.plan.Context;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.Pair;

import com.linkedin.coral.hive.hive2rel.rel.HiveUncollect;

import static org.apache.calcite.rel.core.RelFactories.DEFAULT_AGGREGATE_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_EXCHANGE_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_FILTER_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_JOIN_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_MATCH_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_PROJECT_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_REPEAT_UNION_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SET_OP_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SNAPSHOT_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SORT_EXCHANGE_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SORT_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SPOOL_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_TABLE_SCAN_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_VALUES_FACTORY;


/**
 * HiveRelBuilder overrides {@link #rename} method.
 * Instead of wrapping round HiveUncollect with a Project RelNode, it tries to rebuild
 * HiveUncollect by calling {@link com.linkedin.coral.hive.hive2rel.rel.HiveUncollect#copy(org.apache.calcite.rel.type.RelDataType)}
 * which sets the rowType.
 *
 * The benefit of eliminating the Project RelNode is that it avoids an extra and unnecessary
 * (SELECT ... FROM ... AS ...) wrapper in the unparsed SQL queries.  For example, in Trino,
 * this allows us to generate "FROM ... CROSS JOIN UNNEST(...)" instead of
 * "FROM ... CROSS JOIN (SELECT ... FROM UNNEST(...))".
 */
public class HiveRelBuilder extends RelBuilder {

  private HiveRelBuilder(Context context, RelOptCluster cluster, RelOptSchema relOptSchema) {
    super(context, cluster, relOptSchema);
  }

  /**
   * Creates a RelBuilder.
   */
  public static RelBuilder create(FrameworkConfig config) {
    return Frameworks.withPrepare(config, (cluster, relOptSchema, rootSchema,
        statement) -> new HiveRelBuilder(config.getContext(), cluster, relOptSchema));
  }

  /** Creates a {@link RelBuilderFactory}, a partially-created RelBuilder.
   * Just add a {@link RelOptCluster} and a {@link RelOptSchema}
   *
   * Note that this function creates a HiveRelBuilder instead of a RelBuilder as in its parent.
   * */
  public static RelBuilderFactory proto(final Context context) {
    return (cluster, schema) -> new HiveRelBuilder(context, cluster, schema);
  }

  /**
   * Note that this static variable is created with HiveRelBuilder.proto instead of RelBuilder.proto as in its parent.
   */
  public static final RelBuilderFactory LOGICAL_BUILDER =
      HiveRelBuilder.proto(Contexts.of(DEFAULT_PROJECT_FACTORY, DEFAULT_FILTER_FACTORY, DEFAULT_JOIN_FACTORY,
          DEFAULT_SORT_FACTORY, DEFAULT_EXCHANGE_FACTORY, DEFAULT_SORT_EXCHANGE_FACTORY, DEFAULT_AGGREGATE_FACTORY,
          DEFAULT_MATCH_FACTORY, DEFAULT_SET_OP_FACTORY, DEFAULT_VALUES_FACTORY, DEFAULT_TABLE_SCAN_FACTORY,
          DEFAULT_SNAPSHOT_FACTORY, DEFAULT_SPOOL_FACTORY, DEFAULT_REPEAT_UNION_FACTORY));

  /** Almost the same as the parent method except the handling of HiveUncollect.
   *  See also {@link org.apache.calcite.tools.RelBuilder#rename} for details.
   *
   * @param fieldNames List of desired field names; may contain null values or
   * have fewer fields than the current row type
   */
  @Override
  public RelBuilder rename(List<String> fieldNames) {
    final List<String> oldFieldNames = peek().getRowType().getFieldNames();
    Preconditions.checkArgument(fieldNames.size() <= oldFieldNames.size(), "More names than fields");
    final List<String> newFieldNames = new ArrayList<>(oldFieldNames);
    for (int i = 0; i < fieldNames.size(); i++) {
      final String s = fieldNames.get(i);
      if (s != null) {
        newFieldNames.set(i, s);
      }
    }
    if (oldFieldNames.equals(newFieldNames)) {
      return this;
    }
    if (peek() instanceof Values) {
      // Special treatment for VALUES. Re-build it rather than add a project.
      final Values v = (Values) build();
      final RelDataTypeFactory.Builder b = getTypeFactory().builder();
      for (Pair<String, RelDataTypeField> p : Pair.zip(newFieldNames, v.getRowType().getFieldList())) {
        b.add(p.left, p.right.getType());
      }
      return values(v.tuples, b.build());
    }
    if (peek() instanceof HiveUncollect) {
      // Special treatment for HiveUncollect. Re-build it rather than add a project.
      final HiveUncollect v = (HiveUncollect) build();
      final RelDataTypeFactory.Builder b = getTypeFactory().builder();
      for (Pair<String, RelDataTypeField> p : Pair.zip(newFieldNames, v.getRowType().getFieldList())) {
        b.add(p.left, p.right.getType());
      }
      push(v.copy(b.build()));
      return this;
    }

    return project(fields(), newFieldNames, true);
  }

}

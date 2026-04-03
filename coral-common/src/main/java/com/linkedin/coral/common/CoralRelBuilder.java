/**
 * Copyright 2023-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

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
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.Pair;

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
 * CoralRelBuilder overrides {@link #rename} to handle {@link CoralUncollect} nodes.
 * Instead of wrapping CoralUncollect with a Project RelNode, it rebuilds the CoralUncollect
 * using {@link CoralUncollect#copy(org.apache.calcite.rel.type.RelDataType)} to set the rowType.
 *
 * <p>This avoids an extra and unnecessary (SELECT ... FROM ... AS ...) wrapper in unparsed SQL.
 *
 * <p>This class was previously named {@code HiveRelBuilder} and has been renamed to better
 * reflect that it is used across all Coral translation targets.
 */
public class CoralRelBuilder extends RelBuilder {
  protected CoralRelBuilder(Context context, RelOptCluster cluster, RelOptSchema relOptSchema) {
    super(context, cluster, relOptSchema);
  }

  public static RelBuilder create(FrameworkConfig config) {
    return Frameworks.withPrepare(config, (cluster, relOptSchema, rootSchema, statement) -> {
      cluster = RelOptCluster.create(cluster.getPlanner(),
          new RexBuilder(new CoralJavaTypeFactoryImpl(cluster.getTypeFactory().getTypeSystem())));
      return new CoralRelBuilder(config.getContext(), cluster, relOptSchema);
    });
  }

  public static RelBuilderFactory proto(final Context context) {
    return (cluster, schema) -> new CoralRelBuilder(context, cluster, schema);
  }

  public static final RelBuilderFactory LOGICAL_BUILDER =
      CoralRelBuilder.proto(Contexts.of(DEFAULT_PROJECT_FACTORY, DEFAULT_FILTER_FACTORY, DEFAULT_JOIN_FACTORY,
          DEFAULT_SORT_FACTORY, DEFAULT_EXCHANGE_FACTORY, DEFAULT_SORT_EXCHANGE_FACTORY, DEFAULT_AGGREGATE_FACTORY,
          DEFAULT_MATCH_FACTORY, DEFAULT_SET_OP_FACTORY, DEFAULT_VALUES_FACTORY, DEFAULT_TABLE_SCAN_FACTORY,
          DEFAULT_SNAPSHOT_FACTORY, DEFAULT_SPOOL_FACTORY, DEFAULT_REPEAT_UNION_FACTORY));

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
      final Values v = (Values) build();
      final RelDataTypeFactory.Builder b = getTypeFactory().builder();
      for (Pair<String, RelDataTypeField> p : Pair.zip(newFieldNames, v.getRowType().getFieldList())) {
        b.add(p.left, p.right.getType());
      }
      return values(v.tuples, b.build());
    }
    if (peek() instanceof CoralUncollect) {
      final CoralUncollect v = (CoralUncollect) build();
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

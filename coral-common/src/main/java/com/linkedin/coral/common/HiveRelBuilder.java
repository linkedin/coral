/**
 * Copyright 2023-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.calcite.plan.Context;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelBuilderFactory;


/**
 * @deprecated Use {@link CoralRelBuilder} instead. This class has been renamed to better reflect
 * that it is used across all Coral translation targets, not just Hive.
 * This class will be removed in a future release.
 */
@Deprecated
public class HiveRelBuilder extends CoralRelBuilder {
  private HiveRelBuilder(Context context, RelOptCluster cluster, RelOptSchema relOptSchema) {
    super(context, cluster, relOptSchema);
  }

  /** @deprecated Use {@link CoralRelBuilder#create(FrameworkConfig)} instead. */
  @Deprecated
  public static RelBuilder create(FrameworkConfig config) {
    return CoralRelBuilder.create(config);
  }

  /** @deprecated Use {@link CoralRelBuilder#proto(Context)} instead. */
  @Deprecated
  public static RelBuilderFactory proto(final Context context) {
    return CoralRelBuilder.proto(context);
  }

  /** @deprecated Use {@link CoralRelBuilder#LOGICAL_BUILDER} instead. */
  @Deprecated
  public static final RelBuilderFactory LOGICAL_BUILDER = CoralRelBuilder.LOGICAL_BUILDER;
}

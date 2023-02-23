/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.ir;

import java.util.List;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.Context;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;

import com.linkedin.coral.hive.hive2rel.HiveRelBuilder;


public class CoralIRRelBuilder extends HiveRelBuilder {

  private CoralIRRelBuilder(Context context, RelOptCluster cluster, RelOptSchema relOptSchema) {
    super(context, cluster, relOptSchema);
  }

  public static RelBuilder create(FrameworkConfig config) {
    return Frameworks.withPrepare(config, (cluster, relOptSchema, rootSchema,
        statement) -> new CoralIRRelBuilder(config.getContext(), cluster, relOptSchema));
  }

  @Override
  public RelDataTypeFactory getTypeFactory() {
    return new JavaTypeFactoryImpl() {
      @Override
      public FieldInfoBuilder builder() {
        return new FieldInfoBuilder(this).kind(StructKind.PEEK_FIELDS);
      }

      @Override
      public RelDataType createStructType(final List<RelDataType> typeList, final List<String> fieldNameList) {
        return createStructType(StructKind.PEEK_FIELDS, typeList, fieldNameList);
      }
    };
  }
}

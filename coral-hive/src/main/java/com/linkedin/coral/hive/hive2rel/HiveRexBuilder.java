/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.rel.type.RelDataTypeFactory;


/**
 * @deprecated Use {@link CoralRexBuilder} instead.
 */
@Deprecated
public class HiveRexBuilder extends CoralRexBuilder {
  /**
   * Creates a RexBuilder.
   *
   * @param typeFactory Type factory
   */
  public HiveRexBuilder(RelDataTypeFactory typeFactory) {
    super(typeFactory);
  }
}

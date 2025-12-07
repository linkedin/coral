/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;


/**
 * Test helper utilities for creating Calcite objects.
 */
public class TestHelper {

  /**
   * Creates a RexBuilder for testing purposes.
   */
  public static RexBuilder createRexBuilder() {
    RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
    return new RexBuilder(typeFactory);
  }
}

/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;


public class HiveRexBuilder extends RexBuilder {
  /**
   * Creates a RexBuilder.
   *
   * @param typeFactory Type factory
   */
  public HiveRexBuilder(RelDataTypeFactory typeFactory) {
    super(typeFactory);
  }

  /**
   * HiveRexBuilder overrides this method to make field access case-insensitively,
   * because in Hive 1.1, if the base table `t` contains non-lowercase struct field like `s struct(A:string)`,
   * the schema of the view `v` based on the base table would become `s struct(a:string)`,
   * translation for SQL `SELECT * FROM v WHERE v.s.A='xxx'` will fail with the following exception
   * if caseSensitive=true, given Calcite would convert `v.s.A` to `v.s.a` to be aligned with the
   * schema of view `v` during the validation phase:
   *
   * java.lang.AssertionError: Type 'RecordType(VARCHAR(2147483647) A)' has no field 'a'
   *
   * Setting caseSensitive=false would not cause regression because Calcite doesn't allow
   * two struct fields which only differ in casing like `struct(a:string,A:string)`, check
   * org.apache.calcite.sql.validate.DelegatingScope.fullyQualify for more info
   */
  @Override
  public RexNode makeFieldAccess(RexNode expr, String fieldName, boolean caseSensitive) {
    return super.makeFieldAccess(expr, fieldName, false);
  }
}

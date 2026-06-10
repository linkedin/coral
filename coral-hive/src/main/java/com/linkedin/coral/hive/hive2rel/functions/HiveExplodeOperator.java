/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;


/**
 * @deprecated Use {@link CoralExplodeOperator} instead.
 */
@Deprecated
public class HiveExplodeOperator extends CoralExplodeOperator {

  /**
   * @deprecated Use {@link CoralExplodeOperator#EXPLODE} instead.
   */
  @Deprecated
  public static final HiveExplodeOperator EXPLODE = new HiveExplodeOperator();

  public HiveExplodeOperator() {
    super();
  }
}

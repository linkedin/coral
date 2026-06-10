/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;


/**
 * @deprecated Use {@link CoralPosExplodeOperator} instead.
 */
@Deprecated
public class HivePosExplodeOperator extends CoralPosExplodeOperator {

  /**
   * @deprecated Use {@link CoralPosExplodeOperator#POS_EXPLODE} instead.
   */
  @Deprecated
  public static final HivePosExplodeOperator POS_EXPLODE = new HivePosExplodeOperator();

  public HivePosExplodeOperator() {
    super();
  }
}

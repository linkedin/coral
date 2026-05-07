/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;


/**
 * @deprecated Use {@link CoralRLikeOperator} instead.
 */
@Deprecated
public class HiveRLikeOperator extends CoralRLikeOperator {

  /**
   * @deprecated Use {@link CoralRLikeOperator#RLIKE} instead.
   */
  @Deprecated
  public static final HiveRLikeOperator RLIKE = new HiveRLikeOperator("RLIKE", false);

  /**
   * @deprecated Use {@link CoralRLikeOperator#REGEXP} instead.
   */
  @Deprecated
  public static final HiveRLikeOperator REGEXP = new HiveRLikeOperator("REGEXP", false);

  public HiveRLikeOperator(String name, boolean negated) {
    super(name, negated);
  }
}

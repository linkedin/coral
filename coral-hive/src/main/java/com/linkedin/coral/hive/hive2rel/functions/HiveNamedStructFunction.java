/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;


/**
 * @deprecated Use {@link CoralNamedStructFunction} instead.
 */
@Deprecated
public class HiveNamedStructFunction extends CoralNamedStructFunction {

  /**
   * @deprecated Use {@link CoralNamedStructFunction#NAMED_STRUCT} instead.
   */
  @Deprecated
  public static final HiveNamedStructFunction NAMED_STRUCT = new HiveNamedStructFunction();

  public HiveNamedStructFunction() {
    super();
  }
}

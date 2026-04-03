/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import com.linkedin.coral.common.functions.Function;


/**
 * @deprecated Use {@link CoralFunction} instead.
 */
@Deprecated
public class HiveFunction extends CoralFunction {

  /**
   * @deprecated Use {@link CoralFunction#CAST} instead.
   */
  @Deprecated
  public static final Function CAST = CoralFunction.CAST;

  /**
   * @deprecated Use {@link CoralFunction#CASE} instead.
   */
  @Deprecated
  public static final Function CASE = CoralFunction.CASE;

  /**
   * @deprecated Use {@link CoralFunction#WHEN} instead.
   */
  @Deprecated
  public static final Function WHEN = CoralFunction.WHEN;

  /**
   * @deprecated Use {@link CoralFunction#BETWEEN} instead.
   */
  @Deprecated
  public static final Function BETWEEN = CoralFunction.BETWEEN;

  /**
   * @deprecated Use {@link CoralFunction#IN} instead.
   */
  @Deprecated
  public static final Function IN = CoralFunction.IN;
}

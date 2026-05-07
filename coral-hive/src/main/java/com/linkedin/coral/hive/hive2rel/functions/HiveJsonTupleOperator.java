/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;


/**
 * @deprecated Use {@link CoralJsonTupleOperator} instead.
 */
@Deprecated
public class HiveJsonTupleOperator extends CoralJsonTupleOperator {

  /**
   * @deprecated Use {@link CoralJsonTupleOperator#JSON_TUPLE} instead.
   */
  @Deprecated
  public static final HiveJsonTupleOperator JSON_TUPLE = new HiveJsonTupleOperator();

  public HiveJsonTupleOperator() {
    super();
  }
}

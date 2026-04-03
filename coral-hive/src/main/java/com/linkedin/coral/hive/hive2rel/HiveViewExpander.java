/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import javax.annotation.Nonnull;


/**
 * @deprecated Use {@link CoralViewExpander} instead.
 */
@Deprecated
public class HiveViewExpander extends CoralViewExpander {

  /**
   * Instantiates a new Hive view expander.
   *
   * @param hiveToRelConverter Hive to Rel converter
   */
  public HiveViewExpander(@Nonnull HiveToRelConverter hiveToRelConverter) {
    super(hiveToRelConverter);
  }
}

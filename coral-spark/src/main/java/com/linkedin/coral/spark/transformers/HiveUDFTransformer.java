/**
 * Copyright 2023-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import java.util.Set;

import com.linkedin.coral.spark.containers.SparkUDFInfo;


/**
 * @deprecated Use {@link CoralUDFTransformer} instead. This class will be removed in a future release.
 */
@Deprecated
public class HiveUDFTransformer extends CoralUDFTransformer {

  public HiveUDFTransformer(Set<SparkUDFInfo> sparkUDFInfos) {
    super(sparkUDFInfos);
  }
}

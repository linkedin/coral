/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

/**
 * Contains configuration keys for {@link RelToTrinoConverter#configs}
 */
public class CoralTrinoConfigKeys {
  public static final String SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT = "SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT";
  public static final String AVOID_TRANSFORM_TO_DATE_UDF = "AVOID_TRANSFORM_TO_DATE_UDF";
}

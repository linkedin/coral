/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

/**
 * Contains configuration keys for {@link RelToTrinoConverter#configs}
 */
public class CoralTrinoConfigKeys {
  /**
   * SQL standard (which Trino follows) defines that when we do CROSS JOIN UNNEST over ARRAY(ROW(...)), both ARRAY and ROW are unnested.
   * This is not what Hive's LATERAL VIEW EXPLODE does, which only unnests ARRAY.
   * Therefore, https://github.com/linkedin/coral/pull/93 adds extra ROW wrapping (translating ARRAY(ROW(...)) to ARRAY(ROW(ROW(...)))) on purpose.
   * However, LinkedIn's internal Trino is still extending the support for Hive’s legacy behavior of unnest, which only unnests ARRAY.
   * Therefore, we add this config for LinkedIn's internal use, if the value is set to true, we don't add extra ROW wrapping.
   */
  public static final String SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT = "SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT";

  /**
   * https://github.com/linkedin/coral/pull/132 converts `to_date(xxx)` (returns string type pre Hive 2.1.0, returns date type on or after Hive 2.1.0)
   * to `date(cast(xxx as timestamp))` (returns date type).
   * Since LinkedIn is using Hive 1.x, our users expect `to_date(xxx)` function to return `string` type instead of `date` type.
   * And we have registered `to_date` UDF in Trino which returns `string` type to meet the users’ needs.
   * Therefore, we add this config for LinkedIn's internal use, if the value is set to true, we don't convert `to_date(xxx)` to `date(cast(xxx as timestamp))`.
   */
  public static final String AVOID_TRANSFORM_TO_DATE_UDF = "AVOID_TRANSFORM_TO_DATE_UDF";

  /**
   * Similar to {@link CoralTrinoConfigKeys#AVOID_TRANSFORM_TO_DATE_UDF}, `date_add` should return `string` type at LinkedIn, so we need to add the `cast` for it
   */
  public static final String CAST_DATEADD_TO_STRING = "CAST_DATE_ADD_TO_STRING";
}

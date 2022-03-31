/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.utils;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.util.Util;


public class SparkSqlValidatorUtil {
  // refer to SqlValidatorUtil.getAlias from Calcite
  // the only customization is to use the custom deriveAliasFromOrdinal
  public static String getAlias(SqlNode node, int ordinal) {
    switch (node.getKind()) {
      case AS:
        // E.g. "1 + 2 as foo" --> "foo"
        return ((SqlCall) node).operand(1).toString();

      case OVER:
        // E.g. "bids over w" --> "bids"
        return getAlias(((SqlCall) node).operand(0), ordinal);

      case IDENTIFIER:
        // E.g. "foo.bar" --> "bar"
        return Util.last(((SqlIdentifier) node).names);

      default:
        if (ordinal < 0) {
          return null;
        } else {
          return deriveAliasFromOrdinal(ordinal);
        }
    }
  }

  // refer to SqlUtil.deriveAliasFromOrdinal from Calcite
  // the customization is to use '_' instead of '$'
  public static String deriveAliasFromOrdinal(int ordinal) {
    // Use a '_' separator to align with current Coral-Schema output names
    return "EXPR_" + ordinal;
  }
}

/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import org.apache.calcite.sql.SqlNode;


/**
 * This class converts a Coral SqlNode to a Spark SqlNode
 */
public class CoralSqlNodeToSparkSqlNodeConverter {

  public SqlNode convert(SqlNode coralSqlNode) {
    return coralSqlNode.accept(new CoralToSparkSqlCallTransformer());
  }

}

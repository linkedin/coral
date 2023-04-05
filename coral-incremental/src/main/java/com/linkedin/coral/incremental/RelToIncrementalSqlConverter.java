/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;


public class RelToIncrementalSqlConverter {

  public String convert(RelNode relNode) {
    RelNode incrementalRelNode = RelNodeIncrementalTransformer.convertRelIncremental(relNode);
    CoralRelToSqlNodeConverter converter = new CoralRelToSqlNodeConverter();
    SqlNode sqlNode = converter.convert(incrementalRelNode);
    return sqlNode.toSqlString(converter.INSTANCE).getSql();
  }

}

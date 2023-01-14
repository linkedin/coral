/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.calcite.sql;

import org.apache.calcite.sql.SqlNode;


/**
 * Interface for SqlNodes containing select statements as a child node. Ex: CTAS queries
 */
public interface SqlCommand {

  SqlNode getSelectQuery();

  void setSelectQuery(SqlNode selectQuery);
}

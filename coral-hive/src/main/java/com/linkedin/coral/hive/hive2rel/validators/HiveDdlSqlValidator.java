/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.validators;

import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.common.calcite.DdlSqlValidator;


public class HiveDdlSqlValidator implements DdlSqlValidator {
  @Override
  public void validate(SqlNode ddlSqlNode) {
    switch (ddlSqlNode.getKind()) {
      case CREATE_TABLE:
        validateCreateTable(ddlSqlNode);
    }
  }

  private void validateCreateTable(SqlNode sqlNode) {
    //Todo need to add appropriate validations
  }
}

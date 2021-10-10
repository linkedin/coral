/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlNode;


public class ParserVisitorContext {
  private final List<SqlNode> whenList = new ArrayList<>();
  private final List<SqlNode> thenList = new ArrayList<>();

  public List<SqlNode> getWhenList() {
    return whenList;
  }

  public List<SqlNode> getThenList() {
    return thenList;
  }

  public void resetCaseWhen() {
    whenList.clear();
    thenList.clear();
  }
}

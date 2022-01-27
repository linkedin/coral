/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.validate.SqlNameMatcher;
import org.apache.calcite.util.Util;

import com.linkedin.coral.common.functions.Function;


/**
 * Class to resolve Trino function names in SQL definition based on
 * the mapping stored in table parameters in the metastore.
 */
public class TrinoOperatorTable implements SqlOperatorTable {
  // TODO: support injection framework to inject same function registry here and ParseTreeBuilder.
  // For now, we create another instance since the function registry is simple.
  private final StaticTrinoFunctionRegistry functionRegistry = new StaticTrinoFunctionRegistry();

  @Override
  public void lookupOperatorOverloads(SqlIdentifier sqlIdentifier, SqlFunctionCategory sqlFunctionCategory,
      SqlSyntax sqlSyntax, List<SqlOperator> list, SqlNameMatcher sqlNameMatcher) {
    String functionName = Util.last(sqlIdentifier.names);
    Collection<Function> functions = functionRegistry.lookup(functionName);
    functions.stream().map(Function::getSqlOperator).collect(Collectors.toCollection(() -> list));
  }

  @Override
  public List<SqlOperator> getOperatorList() {
    // TODO: return list of Trino operators
    return ImmutableList.of();
  }
}

/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

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


/**
 * Class to resolve Coral operator names in SQL definition based on
 * the mapping stored in table parameters in the metastore.
 */
public class CoralOperatorTable implements SqlOperatorTable {
  // TODO: support injection framework to inject same function resolver here and ParseTreeBuilder.
  // For now, we create another instance since the function registry is simple.
  private final FunctionResolver funcResolver;

  public CoralOperatorTable(FunctionResolver funcResolver) {
    this.funcResolver = funcResolver;
  }

  /**
   * Resolves operator names to corresponding Calcite operator. FunctionResolver ensures that
   * {@code sqlIdentifier} has operator name or corresponding class name for Coral operator. All registry
   * lookups performed by this class are case-insensitive.
   *
   * Calcite invokes this function multiple times during analysis phase to validate SqlCall operators. This is
   * also used to resolve overloaded function names by using number and type of function parameters.
   */
  @Override
  public void lookupOperatorOverloads(SqlIdentifier sqlIdentifier, SqlFunctionCategory sqlFunctionCategory,
      SqlSyntax sqlSyntax, List<SqlOperator> list, SqlNameMatcher sqlNameMatcher) {
    String functionName = Util.last(sqlIdentifier.names);
    Collection<Function> functions = funcResolver.resolve(functionName);
    functions.stream().map(Function::getSqlOperator).collect(Collectors.toCollection(() -> list));
  }

  @Override
  public List<SqlOperator> getOperatorList() {
    // TODO: return list of dali operators
    return ImmutableList.of();
  }
}

/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

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
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;


/**
 * Class to resolve Dali function names in SQL definition based on
 * the mapping stored in table parameters in the metastore.
 */
public class DaliOperatorTable implements SqlOperatorTable {
  // TODO: support injection framework to inject same function resolver here and ParseTreeBuilder.
  // For now, we create another instance since the function registry is simple.
  private final HiveFunctionResolver funcResolver;

  public DaliOperatorTable(HiveFunctionResolver funcResolver) {
    this.funcResolver = funcResolver;
  }

  /**
   * Resolves functions names to corresponding Calcite UDF. HiveFunctionResolver ensures that
   * {@code sqlIdentifier} has function name or corresponding class name for Dali functions. All function registry
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

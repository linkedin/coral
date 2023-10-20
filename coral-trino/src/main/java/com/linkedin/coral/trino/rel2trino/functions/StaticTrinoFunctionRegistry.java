/**
 * Copyright 2019-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.Collection;

import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.collect.HashMultimap;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.Multimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionRegistry;

import static org.apache.calcite.sql.type.OperandTypes.STRING;
import static org.apache.calcite.sql.type.OperandTypes.family;
import static org.apache.calcite.sql.type.OperandTypes.or;
import static org.apache.calcite.sql.type.ReturnTypes.explicit;


public class StaticTrinoFunctionRegistry implements FunctionRegistry {

  static final Multimap<String, Function> FUNCTION_MAP = HashMultimap.create();

  static {
    createAddUserDefinedFunction("$canonicalize_hive_timezone_id", explicit(SqlTypeName.VARCHAR), STRING);
    createAddUserDefinedFunction("timestamp_from_unixtime", explicit(SqlTypeName.TIMESTAMP),
        or(family(ImmutableList.of(SqlTypeFamily.NUMERIC)),
            family(ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING)),
            family(ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC))));
  }

  /**
   * Returns a list of functions matching given name case-insensitively. This returns empty list if the
   * function name is not found.
   * @param functionName function name to match
   * @return list of matching HiveFunctions or empty collection.
   */
  @Override
  public Collection<Function> lookup(String functionName) {
    return FUNCTION_MAP.get(functionName.toLowerCase());
  }

  /**
   * Adds the function to registry, the key is lowercase functionName to make lookup case-insensitive.
   */
  private static void addFunctionEntry(String functionName, SqlOperator operator) {
    FUNCTION_MAP.put(functionName.toLowerCase(), new Function(functionName, operator));
  }

  public static void createAddUserDefinedFunction(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    addFunctionEntry(functionName, createCalciteUDF(functionName, returnTypeInference, operandTypeChecker));
  }

  private static SqlOperator createCalciteUDF(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), returnTypeInference, null,
        operandTypeChecker, null, null);
  }
}

/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlPrefixOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlUnresolvedFunction;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.HiveTable;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionRegistry;
import com.linkedin.coral.common.functions.UnknownSqlFunctionException;

import static com.google.common.base.Preconditions.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;
import static org.apache.calcite.sql.type.OperandTypes.*;


/**
 * Class to resolve hive function names in SQL to Function.
 */
public class HiveFunctionResolver {

  public final FunctionRegistry registry;
  private final ConcurrentHashMap<String, Function> dynamicFunctionRegistry;
  private final List<SqlOperator> operators;

  public HiveFunctionResolver(FunctionRegistry registry, ConcurrentHashMap<String, Function> dynamicRegistry) {
    this.registry = registry;
    this.dynamicFunctionRegistry = dynamicRegistry;
    this.operators = new ArrayList<>(SqlStdOperatorTable.instance().getOperatorList());
    operators.add(HiveRLikeOperator.REGEXP);
    operators.add(HiveRLikeOperator.RLIKE);
  }

  /**
   * Resolves {@code name} to calcite unary operator.
   * @param name operator text (Ex: '-')
   * @return SqlOperator matching input name
   * @throws IllegalStateException if there is zero or more than one matching operator
   */
  public SqlOperator resolveUnaryOperator(String name) {
    // We convert `!` to `not` directly
    if ("!".equals(name)) {
      return SqlStdOperatorTable.NOT;
    }
    List<SqlOperator> matches = operators.stream()
        .filter(o -> o.getName().equalsIgnoreCase(name) && o instanceof SqlPrefixOperator).collect(Collectors.toList());
    checkState(matches.size() == 1, "%s operator %s", matches.isEmpty() ? "Unknown" : "Ambiguous", name);
    return matches.get(0);
  }

  /**
   * Resolves {@code name} to calcite binary operator case-insensitively.
   * @param name operator text (Ex: '+' or 'LIKE')
   * @return SqlOperator matching input name
   * @throws IllegalStateException if there are zero or more than one matching operator
   */
  public SqlOperator resolveBinaryOperator(String name) {
    final String lowerCaseOperator = name.toLowerCase();
    List<SqlOperator> matches = operators.stream().filter(o -> o.getName().toLowerCase().equals(lowerCaseOperator)
        && (o instanceof SqlBinaryOperator || o instanceof SqlSpecialOperator)).collect(Collectors.toList());
    if (matches.size() == 0) {
      Function f = tryResolve(lowerCaseOperator, null, 2);
      if (f != null) {
        matches.add(f.getSqlOperator());
      }
    }

    // CORAL-23: For `+` and `-` we have 2 matching operators for each:
    // SqlMonotonicBinaryOperator and SqlDatetimePlusOperator for `+`
    // SqlMonotonicBinaryOperator and SqlDatetimeSubtractionOperator for `-`
    // Hive does not support the SqlDatetime[Plus|Subtraction]Operator operator, so we return SqlMonotonicBinaryOperator by default.
    if (lowerCaseOperator.equals("+")) {
      return SqlStdOperatorTable.PLUS;
    } else if (lowerCaseOperator.equals("-")) {
      return SqlStdOperatorTable.MINUS;
    }

    checkState(matches.size() == 1, "%s operator %s", operators.isEmpty() ? "Unknown" : "Ambiguous", name);
    return matches.get(0);
  }

  /**
   * Resolves hive function name to specific Function. This method
   * first attempts to resolve function by its name case-insensitively. If there is no match,
   * this attempts to match dali-style function names (DB_TABLE_VERSION_FUNCTION).
   * Right now, this method does not validate parameters leaving it to
   * the subsequent validator and analyzer phases to validate parameter types.
   * @param functionName hive function name
   * @param hiveTable handle to Hive table representing metastore information. This is used for resolving
   *                  Dali function names, which are resolved using table parameters
   * @param numOfOperands number of operands this function takes. This is needed to
   *                  create SqlOperandTypeChecker to resolve Dali function dynamically
   * @return resolved hive functions
   * @throws UnknownSqlFunctionException if the function name can not be resolved.
   */
  public Function tryResolve(@Nonnull String functionName, @Nullable Table hiveTable, int numOfOperands) {
    checkNotNull(functionName);
    Collection<Function> functions = registry.lookup(functionName);
    if (functions.isEmpty() && hiveTable != null) {
      functions = tryResolveAsDaliFunction(functionName, hiveTable, numOfOperands);
    }
    if (functions.isEmpty()) {
      throw new UnknownSqlFunctionException(functionName);
    }
    if (functions.size() == 1) {
      return functions.iterator().next();
    }
    // we've overloaded function names. Calcite will resolve overload later during semantic analysis.
    // For now, create a placeholder SqlNode for the function. We want to use Dali class name as function
    // name if this is overloaded function name.
    return unresolvedFunction(functions.iterator().next().getSqlOperator().getName());
  }

  /**
   * Resolves function to concrete operator case-insensitively.
   * @param functionName function name to resolve
   * @return list of matching Functions or empty list if there is no match
   */
  public Collection<Function> resolve(String functionName) {
    Collection<Function> staticLookup = registry.lookup(functionName);
    if (!staticLookup.isEmpty()) {
      return staticLookup;
    } else {
      Collection<Function> dynamicLookup = ImmutableList.of();
      Function Function = dynamicFunctionRegistry.get(functionName);
      if (Function != null) {
        dynamicLookup = ImmutableList.of(Function);
      }

      return dynamicLookup;
    }
  }

  /**
   * Tries to resolve function name as Dali function name using the provided Hive table catalog information.
   * This uses table parameters 'function' property to resolve the function name to the implementing class.
   * @param functionName function name to resolve
   * @param table Hive metastore table handle
   * @param numOfOperands number of operands this function takes. This is needed to
   *                      create SqlOperandTypeChecker to resolve Dali function dynamically
   * @return list of matching Functions or empty list if the function name is not in the
   *   dali function name format of db_tableName_functionName
   * @throws UnknownSqlFunctionException if the function name is in Dali function name format but there is no mapping
   */
  public Collection<Function> tryResolveAsDaliFunction(String functionName, @Nonnull Table table, int numOfOperands) {
    Preconditions.checkNotNull(table);
    String functionPrefix = String.format("%s_%s_", table.getDbName(), table.getTableName());
    if (!functionName.toLowerCase().startsWith(functionPrefix.toLowerCase())) {
      // Don't throw UnknownSqlFunctionException here because this is not a dali function
      // and this method is trying to resolve only Dali functions
      return ImmutableList.of();
    }
    String funcBaseName = functionName.substring(functionPrefix.length());
    HiveTable hiveTable = new HiveTable(table);
    Map<String, String> functionParams = hiveTable.getDaliFunctionParams();
    String funcClassName = functionParams.get(funcBaseName);
    if (funcClassName == null) {
      return ImmutableList.of();
    }
    final Collection<Function> Functions = registry.lookup(funcClassName);
    if (Functions.size() == 0) {
      Collection<Function> dynamicResolvedFunctions =
          resolveDaliFunctionDynamically(functionName, funcClassName, hiveTable, numOfOperands);

      if (dynamicResolvedFunctions.size() == 0) {
        // we want to see class name in the exception message for coverage testing
        // so throw exception here
        throw new UnknownSqlFunctionException(funcClassName);
      }

      return dynamicResolvedFunctions;
    }

    return Functions.stream()
        .map(f -> new Function(f.getFunctionName(), new VersionedSqlUserDefinedFunction(
            (SqlUserDefinedFunction) f.getSqlOperator(), hiveTable.getDaliUdfDependencies(), functionName)))
        .collect(Collectors.toList());
  }

  private @Nonnull Collection<Function> resolveDaliFunctionDynamically(String functionName, String funcClassName,
      HiveTable hiveTable, int numOfOperands) {
    Function Function = new Function(funcClassName,
        new VersionedSqlUserDefinedFunction(
            new SqlUserDefinedFunction(new SqlIdentifier(funcClassName, ZERO),
                new HiveGenericUDFReturnTypeInference(funcClassName, hiveTable.getDaliUdfDependencies()), null,
                createSqlOperandTypeChecker(numOfOperands), null, null),
            hiveTable.getDaliUdfDependencies(), functionName));
    dynamicFunctionRegistry.put(funcClassName, Function);
    return ImmutableList.of(Function);
  }

  private @Nonnull Function unresolvedFunction(String functionName) {
    SqlIdentifier funcIdentifier = new SqlIdentifier(ImmutableList.of(functionName), ZERO);
    return new Function(functionName,
        new SqlUnresolvedFunction(funcIdentifier, null, null, null, null, SqlFunctionCategory.USER_DEFINED_FUNCTION));
  }

  private @Nonnull SqlOperandTypeChecker createSqlOperandTypeChecker(int numOfOperands) {
    List<SqlTypeFamily> families = new ArrayList<>();
    for (int i = 0; i < numOfOperands; i++) {
      families.add(SqlTypeFamily.ANY);
    }
    SqlOperandTypeChecker sqlOperandTypeChecker = family(families);

    return sqlOperandTypeChecker;
  }
}

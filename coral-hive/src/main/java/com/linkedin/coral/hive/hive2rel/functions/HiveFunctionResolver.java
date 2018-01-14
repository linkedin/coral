package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlUnresolvedFunction;
import org.apache.calcite.sql.parser.SqlParserPos;


/**
 * Class to resolve hive function names in SQL to HiveFunction.
 */
public class HiveFunctionResolver {

  public final HiveFunctionRegistry registry;

  public HiveFunctionResolver(HiveFunctionRegistry registry) {
    this.registry = registry;
  }

  /**
   * Resolves hive function name to specific HiveFunction. This method
   * first attempts to resolve function by its name. If there is no match,
   * this attempts to match dali-style function names (DB_TABLE_VERSION_FUNCTION).
   * Right now, this method does not validate parameters leaving it to
   * the subsequent validator and analyzer phases to validate parameter types.
   * @param functionName hive function name
   * @param table fully qualified table name
   * @return resolved hive functions
   * @throws UnknownSqlFunctionException if the function name can not be resolved.
   */
  public HiveFunction tryResolve(String functionName, SqlIdentifier table) {
    Collection<HiveFunction> functions = registry.lookup(functionName);
    if (functions.size() == 1) {
      return functions.iterator().next();
    }

    // return unresolved function and let analyzer handle resolution
    // of unknown or overloaded functions
    return unresolvedFunction(functionName, table);
  }

  /**
   * Resolves function to concrete operator.
   * @param functionName function name to resolve
   * @return list of matching HiveFunctions or empty list if there is no match
   */
  public Collection<HiveFunction> resolve(String functionName) {
    return registry.lookup(functionName);
  }

  private HiveFunction unresolvedFunction(String functionName, SqlIdentifier table) {
    ImmutableList<String> tableNames = table.names;
    Preconditions.checkState(tableNames.size() >= 2);
    int namesSize = tableNames.size();
    // we need only (db, table) parts of the `table` identifier. If the table identifier
    // has more components like catalog name, remove those
    if (namesSize > 2) {
      tableNames = tableNames.subList(namesSize - 2, namesSize);
    }
    ImmutableList<String> funcNameList = ImmutableList.<String>builder()
        .addAll(tableNames)
        .add(functionName)
        .build();
    SqlIdentifier funcIdentifier = new SqlIdentifier(funcNameList, SqlParserPos.ZERO);
    return new HiveFunction(functionName,
        new SqlUnresolvedFunction(funcIdentifier, null,
            null, null,
            null, SqlFunctionCategory.USER_DEFINED_FUNCTION));
  }
}

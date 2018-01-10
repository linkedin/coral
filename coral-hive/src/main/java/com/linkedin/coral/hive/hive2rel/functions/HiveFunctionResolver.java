package com.linkedin.coral.hive.hive2rel.functions;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlUnresolvedFunction;

import static com.google.common.base.Preconditions.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


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
   * @param isCaseSensitive is function name case-sensitive
   * @param table fully qualified table name
   * @return resolved hive functions
   * @throws UnknownSqlFunctionException if the function name can not be resolved.
   */
  public HiveFunction tryResolve(@Nonnull String functionName, boolean isCaseSensitive, @Nullable SqlIdentifier table) {
    checkNotNull(functionName);
    Collection<HiveFunction> functions = registry.lookup(functionName, isCaseSensitive);
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
   * @param isCaseSensitive is the function name case-sensitive
   * @return list of matching HiveFunctions or empty list if there is no match
   */
  public Collection<HiveFunction> resolve(String functionName, boolean isCaseSensitive) {
    return registry.lookup(functionName, isCaseSensitive);
  }

  private HiveFunction unresolvedFunction(String functionName, @Nullable SqlIdentifier table) {
    SqlIdentifier funcIdentifier = createFunctionIdentifier(functionName, table);
    return new HiveFunction(functionName,
        new SqlUnresolvedFunction(funcIdentifier, null,
            null, null,
            null, SqlFunctionCategory.USER_DEFINED_FUNCTION));
  }

  private SqlIdentifier createFunctionIdentifier(String functionName, @Nullable SqlIdentifier table) {
    if (table == null) {
      return new SqlIdentifier(ImmutableList.of(functionName), ZERO);
    }
    ImmutableList<String> tableNames = table.names;
    checkState(tableNames.size() >= 2);
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
    return new SqlIdentifier(funcNameList, ZERO);
  }
}

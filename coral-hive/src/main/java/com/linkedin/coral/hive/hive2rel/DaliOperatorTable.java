package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunction;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.SqlUnresolvedFunction;
import org.apache.calcite.sql.parser.SqlParserPos;


/**
 * Class to resolve Dali function names in SQL definition based on
 * the mapping stored in table parameters in the metastore.
 */
public class DaliOperatorTable implements SqlOperatorTable {
  private final HiveSchema schema;
  // TODO: support injection framework to inject same function resolver here and ParseTreeBuilder.
  // For now, we create another instance since the function registry is simple.
  private final HiveFunctionResolver funcResolver = new HiveFunctionResolver(new StaticHiveFunctionRegistry());

  public DaliOperatorTable(HiveSchema schema) {
    this.schema = schema;
  }

  /**
   * Resolves dali function names to corresponding Calcite UDF. HiveFunctionResolver ensures that
   * {@code sqlIdentifier} parameter to this method has db, table and function name. All function registry
   * lookups performed by this class are case-sensitive.
   *
   * There are, however, phases during analysis when Calcite calls this method to resolve UDFs including
   * already resolved UDFs. In that case, sqlIdentifier is a single function name. All SqlUnresolvedUDFs are
   * resolved before such calls. So, for those cases this method simply looks up registry by name
   */
  @Override
  public void lookupOperatorOverloads(SqlIdentifier sqlIdentifier, SqlFunctionCategory sqlFunctionCategory,
      SqlSyntax sqlSyntax, List<SqlOperator> list) {
    com.linkedin.coral.com.google.common.collect.ImmutableList<String> names = sqlIdentifier.names;
    if (names.size() == 1) {
      funcResolver.resolve(names.get(0), true).stream()
          .map(HiveFunction::getSqlOperator)
          .collect(Collectors.toCollection(() -> list));
      return;
    }
    if (names.size() != 3) {
      // need db, table, functionName. Otherwise, we don't  know how to resolve the functions
      return;
    }
    // Dali function names are of the form 'db_table_function'
    // we first verify that the function name starts with 'db_table'
    // and then lookup java class name for 'function' in table parameters.
    // Registry tracks Dali UDFs by implementing class name so we
    String dbName = names.get(0);
    String tableName = names.get(1);
    String functionPrefix = String.format("%s_%s_", dbName, tableName);
    String fullFuncName = names.get(2);
    if (!fullFuncName.startsWith(functionPrefix)) {
      return;
    }
    String funcBaseName = fullFuncName.substring(functionPrefix.length());
    Schema db = schema.getSubSchema(dbName);
    if (db == null) {
      return;
    }

    HiveTable table = ((HiveTable) db.getTable(tableName));
    if (table == null) {
      return;
    }

    Map<String, String> daliFunctionParams = table.getDaliFunctionParams();
    String funcClassName = daliFunctionParams.get(funcBaseName);
    if (funcClassName == null) {
      return;
    }
    HiveFunction hiveFunction = funcResolver.tryResolve(funcClassName, true,
        new SqlIdentifier(ImmutableList.of(dbName, tableName), SqlParserPos.ZERO));
    if (hiveFunction != null
        && !(hiveFunction.getSqlOperator() instanceof SqlUnresolvedFunction)) {
      list.add(hiveFunction.getSqlOperator());
    }
  }

  @Override
  public List<SqlOperator> getOperatorList() {
    // TODO: return list of dali operators
    return ImmutableList.of();
  }
}

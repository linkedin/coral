package com.linkedin.coral.spark;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParserPos;

public class SparkRelToSparkSQLConverter extends RelToSqlConverter {
  /**
   * Creates a RelToSqlConverter.
   *
   * This class converts a Spark RelNode to Spark understandable Hive SQL
   * and is used by CoralSpark's constructSparkSQL() method.
   *
   * One functionality that is overridden is while reading table and
   * changing basetable names from "catalogname.dbname.tablename" to "dbname.tablename".
   *
   */
  SparkRelToSparkSQLConverter() {
    super(SqlDialect.DatabaseProduct.HIVE.getDialect());
  }


  /**
   * This overridden function makes sure that the basetable names in the output SQL
   * will be in the form of "dbname.tablename" instead of "catalogname.dbname.tablename"
   *
   * This is necessary to handle SparkSession.sql() inability to handle table names in
   * the form of 'catalogname.dbname.tablename'. This is because Spark SQL parser doesn't support it and throws
   * "org.apache.spark.sql.catalyst.parser.ParseException: mismatched input '.' expecting <EOF>" Error.
   *
   * For Example:
   *  hive.default.foo_bar -> default.foo_bar
   */
  @Override
  public Result visit(TableScan e) {
    checkQualifiedName(e.getTable().getQualifiedName());
    List<String> tableNameWithoutCatalog = e.getTable().getQualifiedName().subList(1, 3);
    final SqlIdentifier identifier =
        new SqlIdentifier(tableNameWithoutCatalog, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  private void checkQualifiedName(List<String> qualifiedName) {
    if (qualifiedName.size() != 3) {
      throw new RuntimeException("SparkRelToSparkSQLConverter Error: Qualified name has incorrect number of elements");
    }
  }

}

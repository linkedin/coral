package com.linkedin.coral.presto.rel2presto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * Test utility class to create a calcite table to add to a Schema class
 */
public class TestTable implements Table {

  public static final TestTable TABLE_ONE = new TestTable("tableOne", ImmutableMap.of(
      "icol", SqlTypeName.INTEGER,
      "dcol", SqlTypeName.DOUBLE,
      "scol", SqlTypeName.VARCHAR,
      "tcol", SqlTypeName.TIMESTAMP,
      "acol", SqlTypeName.ARRAY
  ));

  public static final TestTable TABLE_TWO = new TestTable("tableTwo",
      ImmutableMap.of(
          "ifield", SqlTypeName.INTEGER,
          "dfield", SqlTypeName.DOUBLE,
          "sfield", SqlTypeName.VARCHAR,
          "tfield", SqlTypeName.TIMESTAMP,
          "decfield", SqlTypeName.DECIMAL
      ));

  private final ImmutableMap<String, SqlTypeName> columns;
  private final String tableName;

  public TestTable(String tableName, ImmutableMap<String, SqlTypeName> columns) {
    this.tableName = tableName;
    this.columns = ImmutableMap.copyOf(columns);
  }

  public String getTableName() {
    return tableName;
  }

  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    ImmutableList<String> fields = columns.keySet().asList();

    List<RelDataType> fieldTypes = columns.values()
        .stream()
        .map(s -> getRelType(typeFactory, s))
        .collect(Collectors.toList());

    RelDataType rowType = typeFactory.createStructType(fieldTypes, fields);
    return rowType;
  }


  public static RelDataType getRelType(RelDataTypeFactory typeFactory, SqlTypeName type) {
    if (type.equals(SqlTypeName.ARRAY)) {
      // TODO: default array type...
      return typeFactory.createArrayType(typeFactory.createSqlType(SqlTypeName.INTEGER), -1);
    }
    return typeFactory.createSqlType(type);
  }

  public Statistic getStatistic() {
    return Statistics.UNKNOWN;
  }

  public Schema.TableType getJdbcTableType() {
    return Schema.TableType.TABLE;
  }

  public ImmutableList<String> getColumnNames() {
    return columns.keySet().asList();
  }
}

/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.calcite.sql;

import java.util.List;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.ImmutableNullableList;

import com.linkedin.coral.javax.annotation.Nonnull;
import com.linkedin.coral.javax.annotation.Nullable;


/**
 * SQL parse tree node to represent {@code CREATE} statements,
 * <p>Supported Syntax:
 *
 * <blockquote><code>
 * CREATE TABLE [ IF NOT EXISTS ] name
 * [ROW FORMAT SERDE serde]
 * [ROW FORMAT DELIMITED FIELDS TERMINATED BY rowFormat]
 * [STORED AS fileFormat]
 * [STORED AS INPUTFORMAT inputFormat STORED AS OUTPUTFORMAT outputFormat]
 * [ AS query ]
 *
 * </code></blockquote>
 *
 * <p>Examples:
 *
 * <ul>
 * <li><code>CREATE TABLE IF NOT EXISTS sample AS SELECT * FROM tmp</code></li>
 * <li><code>CREATE TABLE sample STORED AS ORC AS SELECT * FROM tmp</code></li>
 * <li><code>CREATE TABLE sample STORED AS INPUTFORMAT 'SerDeExampleInputFormat' OUTPUTFORMAT 'SerDeExampleOutputFormat' AS SELECT * FROM tmp</code></li>
 * <li><code>CREATE TABLE sample ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' AS SELECT * FROM tmp</code></li>
 * <li><code>CREATE TABLE sample ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' AS SELECT * FROM tmp</code></li>
 * </ul>
 */
public class SqlCreateTable extends SqlCreate implements SqlCommand {
  // name of the table to be created
  private final SqlIdentifier name;
  // column details like column name, data type, etc. This may be null, like in case of CTAS
  private final @Nullable SqlNodeList columnList;
  // select query node in case of "CREATE TABLE ... AS query"; else may be null
  private @Nullable SqlNode selectQuery;
  // specifying serde property
  private final @Nullable SqlNode serDe;
  // specifying file format such as Parquet, ORC, etc.
  private final @Nullable SqlNodeList fileFormat;
  // specifying delimiter fields for row format
  private final @Nullable SqlCharStringLiteral rowFormat;

  private static final SqlOperator OPERATOR = new SqlSpecialOperator("CREATE TABLE", SqlKind.CREATE_TABLE);

  /** Creates a SqlCreateTable. */
  public SqlCreateTable(SqlParserPos pos, boolean replace, boolean ifNotExists, @Nonnull SqlIdentifier name,
      @Nullable SqlNodeList columnList, @Nullable SqlNode selectQuery, @Nullable SqlNode serDe,
      @Nullable SqlNodeList fileFormat, @Nullable SqlCharStringLiteral rowFormat) {
    super(OPERATOR, pos, replace, ifNotExists);
    this.name = name;
    this.columnList = columnList;
    this.selectQuery = selectQuery;
    this.serDe = serDe;
    this.fileFormat = fileFormat;
    this.rowFormat = rowFormat;
  }

  @SuppressWarnings("nullness")
  @Override
  public List<SqlNode> getOperandList() {
    return ImmutableNullableList.of(name, columnList, selectQuery, serDe, fileFormat, rowFormat);
  }

  @Override
  public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    writer.keyword("CREATE");
    writer.keyword("TABLE");
    if (ifNotExists) {
      writer.keyword("IF NOT EXISTS");
    }
    name.unparse(writer, leftPrec, rightPrec);
    if (columnList != null) {
      SqlWriter.Frame frame = writer.startList("(", ")");
      for (SqlNode c : columnList) {
        writer.sep(",");
        c.unparse(writer, 0, 0);
      }
      writer.endList(frame);
    }
    if (serDe != null) {
      writer.keyword("ROW FORMAT SERDE");
      serDe.unparse(writer, 0, 0);
      writer.newlineAndIndent();
    }
    if (rowFormat != null) {
      writer.keyword("ROW FORMAT DELIMITED FIELDS TERMINATED BY");
      rowFormat.unparse(writer, 0, 0);
      writer.newlineAndIndent();
    }
    if (fileFormat != null) {
      if (fileFormat.size() == 1) {
        writer.keyword("STORED AS");
        fileFormat.get(0).unparse(writer, 0, 0);
      } else {
        writer.keyword("STORED AS INPUTFORMAT");
        fileFormat.get(0).unparse(writer, 0, 0);
        writer.keyword("OUTPUTFORMAT");
        fileFormat.get(1).unparse(writer, 0, 0);
      }
      writer.newlineAndIndent();
    }
    if (selectQuery != null) {
      writer.keyword("AS");
      writer.newlineAndIndent();
      selectQuery.unparse(writer, 0, 0);
    }
  }

  @Override
  public SqlNode getSelectQuery() {
    return selectQuery;
  }

  @Override
  public void setSelectQuery(SqlNode query) {
    this.selectQuery = query;
  }
}

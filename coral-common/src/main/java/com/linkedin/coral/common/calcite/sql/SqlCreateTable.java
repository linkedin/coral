package com.linkedin.coral.common.calcite.sql;

import com.linkedin.coral.javax.annotation.Nullable;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.ImmutableNullableList;

import java.util.List;
import java.util.Objects;

public class SqlCreateTable extends SqlCreate {
    private final SqlIdentifier name;
    private final @Nullable SqlNodeList columnList;
    private @Nullable SqlNode query;
    private final @Nullable SqlNode tableSerializer;
    private final @Nullable SqlNodeList tableFileFormat;
    private final @Nullable SqlCharStringLiteral tableRowFormat;

    private static final SqlOperator OPERATOR =
            new SqlSpecialOperator("CREATE TABLE", SqlKind.CREATE_TABLE);

    /** Creates a SqlCreateTable. */
    public SqlCreateTable(SqlParserPos pos, boolean replace, boolean ifNotExists,
                          SqlIdentifier name, @Nullable SqlNodeList columnList, @Nullable SqlNode query, SqlNode tableSerializer, SqlNodeList tableFileFormat, SqlCharStringLiteral tableRowFormat) {
        super(OPERATOR, pos, replace, ifNotExists);
        this.name = Objects.requireNonNull(name, "name");
        this.columnList = columnList; // may be null, like in case of ctas
        this.query = query; // for "CREATE TABLE ... AS query"; may be null
        this.tableSerializer = tableSerializer;
        this.tableFileFormat = tableFileFormat;
        this.tableRowFormat = tableRowFormat;
    }

    @SuppressWarnings("nullness")
    @Override public List<SqlNode> getOperandList() {
        return ImmutableNullableList.of(name, columnList, query);
    }

    @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
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
        if(tableSerializer != null){
            writer.keyword("ROW FORMAT SERDE");
            tableSerializer.unparse(writer, 0, 0);
            writer.newlineAndIndent();
        }
        if(tableRowFormat != null){
            writer.keyword("ROW FORMAT DELIMITED FIELDS TERMINATED BY");
            tableRowFormat.unparse(writer, 0, 0);
            writer.newlineAndIndent();
        }
        if(tableFileFormat != null){
            if(tableFileFormat.size() == 1){
                writer.keyword("STORED AS");
                tableFileFormat.get(0).unparse(writer, 0, 0);
                writer.newlineAndIndent();
            } else {
                writer.keyword("STORED AS INPUTFORMAT");
                tableFileFormat.get(0).unparse(writer, 0, 0);
                writer.keyword("OUTPUTFORMAT");
                tableFileFormat.get(1).unparse(writer, 0, 0);
                writer.newlineAndIndent();
            }
        }
        if (query != null) {
            writer.keyword("AS");
            writer.newlineAndIndent();
            query.unparse(writer, 0, 0);
        }
    }

    public SqlNode getSelectQuery() {
        return query;
    }

    public void setQuery(SqlNode query) {
        this.query = query;
    }
}

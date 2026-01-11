package com.linkedin.coral.hive.hive2rel.sql;

import java.util.Collections;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;

/**
 * Copy from org.apache.flink.sql.parser.ddl.SqlUseDatabase
 *
 * USE [catalog.]database sql call.
 * */
public class SqlUseDatabase extends SqlCall {

    public static final SqlSpecialOperator OPERATOR =
            new SqlSpecialOperator("USE DATABASE", SqlKind.OTHER_DDL);
    private final SqlIdentifier databaseName;

    public SqlUseDatabase(SqlParserPos pos, SqlIdentifier databaseName) {
        super(pos);
        this.databaseName = databaseName;
    }

    @Override
    public SqlOperator getOperator() {
        return OPERATOR;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return Collections.singletonList(databaseName);
    }

    public SqlIdentifier getDatabaseName() {
        return databaseName;
    }

    public String[] fullDatabaseName() {
        return databaseName.names.toArray(new String[0]);
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        writer.keyword("USE");
        databaseName.unparse(writer, leftPrec, rightPrec);
    }
}
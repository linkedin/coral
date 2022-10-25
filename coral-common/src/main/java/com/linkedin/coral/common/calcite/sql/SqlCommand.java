package com.linkedin.coral.common.calcite.sql;

import org.apache.calcite.sql.SqlNode;

public interface SqlCommand {

    public SqlNode getSelectQuery();

    public void setSelectQuery(SqlNode selectQuery);
}
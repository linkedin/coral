/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.List;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.SqlSingleOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.NlsString;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.sql.fun.SqlStdOperatorTable.CURRENT_TIMESTAMP;
import static org.apache.calcite.sql.type.ReturnTypes.explicit;
import static org.apache.calcite.sql.type.SqlTypeName.DOUBLE;


/**
 * This class implements the transformation from the operation of "unix_timestamp()、unix_timestamp(string date)、unix_timestamp(string date, string pattern)"
 */
public class UnixTimestampOperatorTransformer extends SqlCallTransformer {
  private static final String UNIX_TIMESTAMP_FUNCTION_NAME = "unix_timestamp";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().getName().equalsIgnoreCase(UNIX_TIMESTAMP_FUNCTION_NAME);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> operandList = sqlCall.getOperandList();
    SqlSingleOperandTypeChecker sqlSingleOperandTypeChecker =
        OperandTypes.or(OperandTypes.POSITIVE_INTEGER_LITERAL, OperandTypes.NILADIC);
    SqlFunction sqlFunction = CURRENT_TIMESTAMP;
    if (operandList != null && operandList.size() > 0) {
      int size = operandList.size();
      if (size == 2) {
        SqlNode sqlNode = operandList.get(1);
        if (sqlNode instanceof SqlCharStringLiteral) {
          SqlCharStringLiteral sqlCharStringLiteral = (SqlCharStringLiteral) sqlNode;
          NlsString value = (NlsString) (sqlCharStringLiteral.getValue());
          String sourceFormat = value.getValue();
          // hive  https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
          // trino https://trino.io/docs/current/functions/datetime.html#date
          String formatNew =
              sourceFormat.replaceAll("yyyy", "%Y").replaceAll("MM", "%m").replaceAll("LL", "%M").replaceAll("dd", "%d")
                  .replaceAll("HH", "%H").replaceAll("hh", "%h").replaceAll("mm", "%i").replaceAll("ss", "%s");
          SqlCharStringLiteral sqlCharStringLiteralNew =
              SqlLiteral.createCharString(formatNew, value.getCharsetName(), sqlCharStringLiteral.getParserPosition());
          sqlCall.setOperand(1, sqlCharStringLiteralNew);
          sqlFunction = new SqlFunction("date_parse", SqlKind.OTHER_FUNCTION, null, null, sqlSingleOperandTypeChecker,
              SqlFunctionCategory.TIMEDATE);
        }
      } else if (size == 1) {
        String formatNew = "%Y-%m-%d %H:%i:%s";
        SqlCharStringLiteral sqlCharStringLiteralNew = SqlLiteral.createCharString(formatNew, SqlParserPos.ZERO);
        SqlNode[] sqlNodes = { operandList.get(0), sqlCharStringLiteralNew };
        sqlCall = new SqlBasicCall(sqlCall.getOperator(), sqlNodes, sqlCall.getParserPosition());
        sqlFunction = new SqlFunction("date_parse", SqlKind.OTHER_FUNCTION, null, null, sqlSingleOperandTypeChecker,
            SqlFunctionCategory.TIMEDATE);
      }
    }

    SqlDataTypeSpec timestampType =
        new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.TIMESTAMP, 0, SqlParserPos.ZERO), SqlParserPos.ZERO);
    SqlBasicCall sqlBasicCall =
        new SqlBasicCall(sqlFunction, ((SqlBasicCall) sqlCall).operands, sqlCall.getParserPosition());
    SqlCall call = SqlStdOperatorTable.CAST.createCall(SqlParserPos.ZERO, sqlBasicCall, timestampType);
    SqlFunction toUnixTimeFun = new SqlFunction("to_unixtime", SqlKind.OTHER_FUNCTION, explicit(DOUBLE), null,
        sqlSingleOperandTypeChecker, SqlFunctionCategory.TIMEDATE);
    return toUnixTimeFun.createCall(SqlParserPos.ZERO, call);
  }
}

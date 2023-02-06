/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import org.apache.calcite.sql.SqlArrayTypeSpec;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIntervalLiteral;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlMapTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlRowTypeSpec;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.spark.dialect.SparkSqlDialect;


/**
 * This class makes Spark changes in AST generated by RelToSparkSQLConverter
 *
 * This is the Second last Step in translation pipeline.
 * There may be AST manipulation logic here.
 *
 * This class is a supplement to RelToSqlConverter + SqlDialect. Technically the SQL generated by RelToSqlConverter
 * by consulting SqlDialect should be compliant to the dialect, and just the combination of the two should be
 * sufficient. But in case it is not compliant, that would require lot of behavior overrides, possibly upstream.
 * This class serves as an alternative temporary option.
 */
public class SparkSqlRewriter extends SqlShuttle {

  /**
   *  Spark SQL doesn't support CASTING to a row/struct.
   *
   *  For example:
   *
   *  SELECT CAST(named_struct(.....) to ROW)
   *   is translated to
   *  SELECT named_struct(.....)
   *
   *  Also replaces:
   *
   *  CAST(NULL AS NULL)
   *    to
   *  NULL
   */
  @Override
  public SqlNode visit(SqlCall call) {
    if (call.getOperator().getKind() == SqlKind.CAST
        && containsSqlRowTypeSpec((SqlDataTypeSpec) call.getOperandList().get(1))) {
      return call.getOperandList().get(0).accept(this);
    } else if (call.getOperator().getKind() == SqlKind.CAST && SqlUtil.isNull(call.getOperandList().get(0)) && call
        .getOperandList().get(1).toSqlString(SparkSqlDialect.INSTANCE).getSql().equals(SqlTypeName.NULL.getName())) {
      return call.getOperandList().get(0);
    }
    return super.visit(call);
  }

  /**
   * Check if the SqlDataTypeSpec contains `ROW`
   */
  private boolean containsSqlRowTypeSpec(SqlDataTypeSpec sqlDataTypeSpec) {
    if (sqlDataTypeSpec instanceof SqlRowTypeSpec) {
      return true;
    }
    if (sqlDataTypeSpec instanceof SqlArrayTypeSpec) {
      return containsSqlRowTypeSpec(((SqlArrayTypeSpec) sqlDataTypeSpec).getElementTypeSpec());
    }
    if (sqlDataTypeSpec instanceof SqlMapTypeSpec) {
      return containsSqlRowTypeSpec(((SqlMapTypeSpec) sqlDataTypeSpec).getKeyTypeSpec())
          || containsSqlRowTypeSpec(((SqlMapTypeSpec) sqlDataTypeSpec).getValueTypeSpec());
    }
    return false;
  }

  /**
   *  Spark SQL historically supported VARCHAR but latest documentation doesn't support it.
   *
   *  Source: https://spark.apache.org/docs/latest/sql-reference.html
   *  We convert a VARCHAR datatype to a STRING
   *
   */
  @Override
  public SqlNode visit(SqlDataTypeSpec type) {
    // Spark Sql Types are listed here: https://spark.apache.org/docs/latest/sql-reference.html
    SqlTypeNameSpec typeNameSpec = type.getTypeNameSpec();
    if (typeNameSpec instanceof SqlBasicTypeNameSpec) {
      final SqlBasicTypeNameSpec basicTypeNameSpec = (SqlBasicTypeNameSpec) typeNameSpec;
      final SqlParserPos parserPos = type.getParserPosition();
      switch (type.getTypeName().toString()) {
        case "VARCHAR":
          final SqlBasicTypeNameSpec stringTypeName = new SqlBasicTypeNameSpec("STRING", SqlTypeName.VARCHAR, -1,
              basicTypeNameSpec.getScale(), basicTypeNameSpec.getCharSetName(), parserPos);
          return new SqlDataTypeSpec(stringTypeName, type.getTimeZone(), parserPos);
        default:
          return type;
      }
    } else if (type instanceof SqlArrayTypeSpec) {
      final SqlParserPos parserPos = type.getParserPosition();
      SqlDataTypeSpec componentSpec = type.getComponentTypeSpec();
      SqlDataTypeSpec revisedSpec = (SqlDataTypeSpec) visit(componentSpec);
      return new SqlArrayTypeSpec(revisedSpec, type.getNullable(), parserPos);
    } else {
      return type;
    }
  }

  /**
   * SparkSQL support intervals in HiveQL syntax like below:
   *    INTERVAL '-7' DAY
   * instead of ANSI SQL like below:
   *    INTERVAL -'7' DAY
   *
   * This function will translate ANSI SQL style to HiveQL style.
   *
   * @param call the input SqlNode
   * @return the translated SqlNode
   */
  @Override
  public SqlNode visit(SqlLiteral call) {
    if (call instanceof SqlIntervalLiteral) {
      SqlIntervalLiteral literal = (SqlIntervalLiteral) call;
      SqlIntervalLiteral.IntervalValue value = (SqlIntervalLiteral.IntervalValue) literal.getValue();
      if (value.getSign() == -1) {
        // Create a new SqlIntervalLiteral by moving the negative sign to the front of the literalString
        String intervalLiteralString = "-" + value.getIntervalLiteral();
        call = SqlLiteral.createInterval(1, intervalLiteralString, value.getIntervalQualifier(),
            literal.getParserPosition());
      }
    }
    return super.visit(call);
  }
}

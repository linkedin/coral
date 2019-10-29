/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.util.Util;

import static com.google.common.base.Preconditions.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * Class to represent builtin or user-defined Hive function. This provides
 * information required to analyze the function call in SQL statement and to
 * convert the Hive function to intermediate representation in Calcite. This does
 * not provide function definition to actually evaluate the function. Right now,
 * this also does not provide implementation to dynamically figure out return type
 * based on input parameters.
 *
 * NOTE: HiveFunction is designed to be "re-usable" class.
 *
 */
public class HiveFunction {

  // Function class name specified in TBLPROPERTIES clause.  It contains path leading to the class file.
  // Example: "com.linkedin.dali.udf.date.hive.DateFormatToEpoch"
  private final String hiveName;
  private final SqlOperator sqlOperator;

  public HiveFunction(String functionName, SqlOperator sqlOperator) {
    this.hiveName = functionName;
    this.sqlOperator = sqlOperator;
  }

  public String getHiveFunctionName() {
    return hiveName;
  }

  public SqlOperator getSqlOperator() {
    return sqlOperator;
  }

  public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
    return sqlOperator.createCall(qualifier, ZERO, operands.toArray(new SqlNode[operands.size()]));
  }

  // Specific instances of HiveFunction to override default behavior
  /**
   * Instance of cast() function
   */
  public static final HiveFunction CAST = new HiveFunction("cast", SqlStdOperatorTable.CAST) {
    @Override
    public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
      checkNotNull(operands);
      checkArgument(operands.size() == 1);
      return super.createCall(null, ImmutableList.of(operands.get(0), function), null);
    }
  };

  /**
   * Hive {@code CASE} operator
   */
  public static final HiveFunction CASE = new HiveFunction("case", SqlStdOperatorTable.CASE) {
    @Override
    public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
      checkNotNull(operands);
      List<SqlNode> whenNodes = new ArrayList<>();
      List<SqlNode> thenNodes = new ArrayList<>();
      for (int i = 1; i < operands.size() - 1; i += 2) {
        whenNodes.add(operands.get(i));
        thenNodes.add(operands.get(i + 1));
      }
      // 1 node for case, 2n for when/then nodes, and optionally 1 else node
      SqlNode elseNode = operands.size() % 2 == 1 ? SqlLiteral.createNull(ZERO) : Util.last(operands);
      return getSqlOperator().createCall(ZERO, operands.get(0), new SqlNodeList(whenNodes, ZERO),
          new SqlNodeList(thenNodes, ZERO), elseNode);
    }
  };

  public static final HiveFunction WHEN = new HiveFunction("when", SqlStdOperatorTable.CASE) {
    @Override
    public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
      checkNotNull(operands);
      List<SqlNode> whenNodes = new ArrayList<>();
      List<SqlNode> thenNodes = new ArrayList<>();
      for (int i = 0; i < operands.size() - 1; i += 2) {
        whenNodes.add(operands.get(i));
        thenNodes.add(operands.get(i + 1));
      }
      // 2n for when/then nodes, and optionally 1 else node
      SqlNode elseNode = operands.size() % 2 == 0 ? SqlLiteral.createNull(ZERO) : Util.last(operands);
      return getSqlOperator().createCall(ZERO, null, new SqlNodeList(whenNodes, ZERO), new SqlNodeList(thenNodes, ZERO),
          elseNode);
    }
  };

  // this handles both between and not_between...it's odd because hive parse tree for between operator is odd!
  public static final HiveFunction BETWEEN = new HiveFunction("between", SqlStdOperatorTable.BETWEEN) {
    @Override
    public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
      checkNotNull(operands);
      checkArgument(operands.size() >= 3 && operands.get(0) instanceof SqlLiteral);
      SqlLiteral opType = (SqlLiteral) operands.get(0);
      List<SqlNode> callParams = operands.subList(1, operands.size());
      if (opType.booleanValue()) {
        return SqlStdOperatorTable.NOT_BETWEEN.createCall(ZERO, callParams);
      } else {
        return SqlStdOperatorTable.BETWEEN.createCall(ZERO, callParams);
      }
    }
  };

  public static final HiveFunction IN = new HiveFunction("in", HiveInOperator.IN) {
    @Override
    public SqlCall createCall(SqlNode function, List<SqlNode> operands, SqlLiteral qualifier) {
      checkState(operands.size() >= 2);
      SqlNode lhs = operands.get(0);
      SqlNode firstRhs = operands.get(1);
      if (firstRhs instanceof SqlSelect) {
        // for IN subquery use Calcite IN operator. Calcite IN operator
        // will turn it into inner join, which not ideal but that's better
        // tested.
        return SqlStdOperatorTable.IN.createCall(ZERO, operands);
      } else {
        // column IN values () clause
        List<SqlNode> rhsList = operands.subList(1, operands.size());
        SqlNodeList rhs = new SqlNodeList(rhsList, ZERO);
        return getSqlOperator().createCall(ZERO, lhs, rhs);
      }
    }
  };
}

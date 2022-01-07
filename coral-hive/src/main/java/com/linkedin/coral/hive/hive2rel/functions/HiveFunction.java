/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlCase;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.util.Util;

import com.linkedin.coral.common.functions.Function;

import static com.google.common.base.Preconditions.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


public class HiveFunction {

  // Specific instances of HiveFunction to override default behavior
  /**
   * Instance of cast() function
   */
  public static final Function CAST = new Function("cast", SqlStdOperatorTable.CAST) {
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
  public static final Function CASE = new Function("case", SqlStdOperatorTable.CASE) {
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
      return SqlCase.createSwitched(ZERO, operands.get(0), new SqlNodeList(whenNodes, ZERO),
          new SqlNodeList(thenNodes, ZERO), elseNode);
    }
  };

  public static final Function WHEN = new Function("when", SqlStdOperatorTable.CASE) {
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
      return new SqlCase(ZERO, null, new SqlNodeList(whenNodes, ZERO), new SqlNodeList(thenNodes, ZERO), elseNode);
    }
  };

  // this handles both between and not_between...it's odd because hive parse tree for between operator is odd!
  public static final Function BETWEEN = new Function("between", SqlStdOperatorTable.BETWEEN) {
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

  public static final Function IN = new Function("in", HiveInOperator.IN) {
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

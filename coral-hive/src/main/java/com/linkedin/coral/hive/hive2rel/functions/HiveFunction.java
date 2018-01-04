package com.linkedin.coral.hive.hive2rel.functions;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.util.Util;

import static com.linkedin.coral.com.google.common.base.Preconditions.*;
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

  private final String hiveName;
  private SqlOperator sqlOperator;

  public HiveFunction(String functionName, SqlOperator sqlOperator) {
    this.hiveName = functionName;
    this.sqlOperator = sqlOperator;
  }

  public SqlOperator getSqlOperator() {
    return sqlOperator;
  }

  public SqlCall createCall(SqlNode function, List<SqlNode> operands) {
    return createCall(operands);
  }

  protected SqlCall createCall(List<SqlNode> operands) {
    return sqlOperator.createCall(ZERO, operands);
  }

  // Specific instances of HiveFunction to override default behavior
  /**
   * Instance of cast() function
   */
  public static final HiveFunction CAST = new HiveFunction("cast", SqlStdOperatorTable.CAST) {
    @Override
    public SqlCall createCall(SqlNode function, List<SqlNode> operands) {
      checkNotNull(operands);
      checkArgument(operands.size() == 1);
      return super.createCall(ImmutableList.of(operands.get(0), function));
    }
  };

  /**
   * Hive {@code CASE} operator
   */
  public static final HiveFunction CASE = new HiveFunction("case", SqlStdOperatorTable.CASE) {
    @Override
    public SqlCall createCall(List<SqlNode> operands) {
      checkNotNull(operands);
      List<SqlNode> whenNodes = new ArrayList<>();
      List<SqlNode> thenNodes = new ArrayList<>();
      for (int i = 1; i < operands.size() - 1; i += 2) {
        whenNodes.add(operands.get(i));
        thenNodes.add(operands.get(i + 1));
      }
      return getSqlOperator().createCall(ZERO, operands.get(0), new SqlNodeList(whenNodes, ZERO),
          new SqlNodeList(thenNodes, ZERO), Util.last(operands));
    }
  };

  public static final HiveFunction WHEN = new HiveFunction("when", SqlStdOperatorTable.CASE) {
    @Override
    public SqlCall createCall(List<SqlNode> operands) {
      checkNotNull(operands);
      List<SqlNode> whenNodes = new ArrayList<>();
      List<SqlNode> thenNodes = new ArrayList<>();
      for (int i = 0; i < operands.size() - 1; i += 2) {
        whenNodes.add(operands.get(i));
        thenNodes.add(operands.get(i + 1));
      }
      return getSqlOperator().createCall(ZERO, null, new SqlNodeList(whenNodes, ZERO), new SqlNodeList(thenNodes, ZERO),
          Util.last(operands));
    }
  };

  // this handles both between and not_between...it's odd because hive parse tree for between operator is odd!
  public static final HiveFunction BETWEEN = new HiveFunction("between", SqlStdOperatorTable.BETWEEN) {
    @Override
    public SqlCall createCall(List<SqlNode> operands) {
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
}

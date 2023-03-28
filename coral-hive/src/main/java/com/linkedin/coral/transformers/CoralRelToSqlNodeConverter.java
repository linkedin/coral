/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.rel.BiRel;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.logical.LogicalTableFunctionScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexProgram;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLateralOperator;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;


/**
 * This class converts a Coral intermediate representation, CoralRelNode, to CoralSqlNode.
 */
public class CoralRelToSqlNodeConverter extends RelToSqlConverter {

  public static final SqlDialect INSTANCE = returnInstance();

  /**
   * Creates a CoralRelToSqlNodeConverter.
   */
  public CoralRelToSqlNodeConverter() {
    super(INSTANCE);
  }

  /**
   * Converts a CoralRelNode to its CoralSqlNode representation.
   * @param coralRelNode Coral intermediate representation.
   * @return Result of converting the RelNode to a SqlNode.
   */
  public SqlNode convert(RelNode coralRelNode) {
    return visitChild(0, coralRelNode).asStatement();
  }

  private static SqlDialect returnInstance() {
    SqlDialect.Context context = SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(SqlDialect.DatabaseProduct.HIVE)
        .withNullCollation(NullCollation.HIGH);

    return new SqlDialect(context) {
      @Override
      public boolean requireCastOnString() {
        /**
         * The default value is `false`, then Coral will drop `CAST` for RexNode like `CAST(number_string AS BIGINT) > 0`,
         * which might cause translation quality issue.
         * For example, without explicit `CAST`, Spark will cast the `number_string` to {@link Integer} implicitly,
         * but if the value of the number_string is greater than {@link Integer.MAX_VALUE}, the result of the condition is wrong.
         * Note: Changing the default value to `true` only preserves the existing `CAST` and doesn't introduce new `CAST`.
         *
         * Check `CoralSparkTest#testCastOnString` for an example.
         */
        return true;
      }

      /**
       * Override this method so that so that table alias is prepended to all field references (e.g., "table.column"
       * or "table.struct.filed" instead of "column" or "struct.field"), which is necessary for
       * data type derivation on struct fields.
       *
       * For the following input SQL:
       * CREATE TABLE db.tbl(s struct(name:string, age:int));
       * SELECT split(tbl.s.name, ' ') name_array
       * FROM db.tbl
       * WHERE tbl.s.age = 25;
       *
       * The input RelNode is:
       * LogicalProject(name_array=[split($0.name, ' ')])
       *   LogicalFilter(condition=[=($0.age, 25)])
       *     LogicalTableScan(table=[[hive, db, tbl]])
       *
       * With this override, the generated SqlNode is:
       * SELECT `split`(`tbl`.`s`.`name`, ' ') AS `name_array`
       * FROM `db`.`tbl` AS `tbl`
       * WHERE `tbl`.`s`.`age` = 25
       *
       * Without this override, the generated SqlNode is:
       * SELECT `split`(`s`.`name`, ' ') AS `name_array`
       * FROM `db`.`tbl`
       * WHERE `s`.`age` = 25
       *
       * Without this override, if we want to get the data type of a struct field like `s`.`name`, validation will fail
       * because Calcite uses {@link org.apache.calcite.rel.type.StructKind#FULLY_QUALIFIED} for standard SQL and it
       * expects each field inside the struct to be referenced explicitly with table alias in the method
       * {@link org.apache.calcite.sql.validate.DelegatingScope#fullyQualify(SqlIdentifier)}.
       * Therefore, we need to generate `complex`.`s`.`name` with this override, which will also affect other non-struct
       * fields and add table alias in `FROM` clause, but it won't affect the SQL correctness.
       */
      @Override
      public boolean hasImplicitTableAlias() {
        return false;
      }
    };
  }

  /**
   * TableScan RelNode represents a relational operator that returns the contents of a table.
   * Super's implementation generates a table namespace with the catalog, schema, and table name.
   * This overriding implementation removes the catalog name from the table namespace, if present.
   *
   * @param e TableScan RelNode. An example:
   *         <pre>
   *            LogicalTableScan(table=[[hive, default, complex]])
   *         </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *         The SqlNode generated by converting the above RelNode example is:
   *        <pre>
   *               (SqlIdentifier)
   *            names: `default`.`complex`
   *        </pre>
   */
  @Override
  public Result visit(TableScan e) {
    List<String> qualifiedName = e.getTable().getQualifiedName();
    if (qualifiedName.size() > 2) {
      qualifiedName = qualifiedName.subList(qualifiedName.size() - 2, qualifiedName.size());
    }
    final SqlIdentifier identifier = new SqlIdentifier(qualifiedName, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * Correlate represents a RelNode with two child relational expressions linked by a join type.
   * Super's implementation introduces a LATERAL operator and an AS operator with a single alias as parents of the conversion result of the right child.
   * This overriding implementation performs the same operations only when the right child of the Correlate node is an Uncollect / TableFunction type RelNode.
   * Moreover, AS operator inserts two aliases - table and column aliases.
   *
   * @param e RelNode of type Correlate with two child nodes as input. Example:
   *        <pre>
   *           LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{2}])
   *                          /                                                  \
   *    LogicalTableScan(table=[[hive, default, complex]])                   HiveUncollect
   *                                                                               \
   *           			          			             			          		  	LogicalProject(col=[$cor0.c])
   *                                                                                 \
   *           			          			            			         			 	LogicalValues(tuples=[[{ 0 }]])
   *        </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *        The SqlNode generated by converting the above RelNode example is:
   *        <pre>
   *                                      (SqlCall)
   *        SqlJoin[default.complex , LATERAL UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)]
   *                                         |
   *                _________________________|_____________________________
   *               |                         |                            |
   *  left: `default`.`complex`         joinType: ,       right: LATERAL UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)
   *
   *        </pre>
   */
  @Override
  public Result visit(Correlate e) {
    final Result leftResult = visitChild(0, e.getLeft()).resetAlias();

    // Add context specifying correlationId has same context as its left child
    correlTableMap.put(e.getCorrelationId(), leftResult.qualifiedContext());

    final Result rightResult = visitChild(1, e.getRight()).resetAlias();

    SqlNode rightSqlNode = rightResult.asFrom();

    if (e.getRight() instanceof LogicalTableFunctionScan || e.getRight() instanceof Uncollect) {
      rightSqlNode = generateRightChildForSqlJoinWithLateralViews(e, rightResult);
    }

    SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS),
        JoinType.COMMA.symbol(POS), rightSqlNode, JoinConditionType.NONE.symbol(POS), null);

    return result(join, leftResult, rightResult);
  }

  /**
   * Custom table-valued functions are represented as LogicalTableFunctionScan type relational expression.
   * Current version of Calcite used in Coral does not implement traversing
   * a LogicalTableFunctionScan type RelNode. Hence, this implementation is added.
   *
   * @param e RelNode of type LogicalTableFunctionScan as input. Example:
   *           <pre>
   *             LogicalTableFunctionScan(invocation=[foo_udtf_CountOfRow($cor0.a)])
   *           </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *         The conversion result of the above example is:
   *         The SqlNode generated by converting the above RelNode example is:
   *         <pre>
   *                                (SqlBasicCall)
   *          COLLECTION_TABLE(`foo_udtf_CountOfRow`(`complex`.`a`))
   *                                     |
   *            _________________________|__________________________
   *           |                                                   |
   *  Operator: COLLECTION_TABLE               Operand: `foo_udtf_CountOfRow`(`complex`.`a`)
   *                                                              |
   *                                             _________________|______________________
   *                                            |                                       |
   *                             Operator: foo_udtf_CountOfRow               Operand: `complex`.`a`
   *         </pre>
   */
  public Result visit(LogicalTableFunctionScan e) {
    RexCall call = (RexCall) e.getCall();
    SqlOperator functionOperator = call.getOperator();
    final List<SqlNode> functionOperands = new ArrayList<>();
    for (RexNode rexOperand : call.getOperands()) {
      RexFieldAccess rexFieldAccess = (RexFieldAccess) rexOperand;
      RexCorrelVariable rexCorrelVariable = (RexCorrelVariable) rexFieldAccess.getReferenceExpr();
      SqlNode sqlNodeOperand = correlTableMap.get(rexCorrelVariable.id).toSql(null, rexOperand);
      functionOperands.add(sqlNodeOperand);
    }

    SqlCall functionSqlCall = functionOperator.createCall(POS, functionOperands.toArray(new SqlNode[0]));

    SqlNode tableCall = new SqlLateralOperator(SqlKind.COLLECTION_TABLE).createCall(POS, functionSqlCall);

    Result tableCallResultWithAlias = result(tableCall, ImmutableList.of(Clause.FROM), e, null);

    return new Result(tableCall, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(tableCallResultWithAlias.neededAlias, e.getRowType()));
  }

  /**
   * Join represents a RelNode with two child relational expressions linked by a join type.
   * Super's implementation uses the conversion result of the right child as is.
   * When the right child of a Join node is an Uncollect / TableFunction type RelNode,
   * this overriding implementation introduces LATERAL and AS operators as parents of the conversion result of the right child.
   *
   * @param e RelNode of type Join with two child nodes as input. Example:
   *        <pre>
   *                             LogicalJoin(condition=[true], joinType=[inner])
   *                                /                                      \
   *    LogicalTableScan(table=[[hive, default, complex]])              HiveUncollect
   *                                                                         \
   *           			          			                        		  	LogicalProject(col=[$cor0.c])
   *                                                                           \
   *           			          			         			          			 	LogicalValues(tuples=[[{ 0 }]])
   *        </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *        The SqlNode generated by converting the above RelNode example is:
   *        <pre>
   *                                      (SqlCall)
   *          SqlJoin[`default`.`complex` , LATERAL UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)]
   *                                         |
   *                _________________________|_____________________________
   *               |                         |                            |
   *  left: `default`.`complex`         joinType: ,       right: LATERAL UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)
   *
   *        </pre>
   */
  @Override
  public Result visit(Join e) {
    Result leftResult = this.visitChild(0, e.getLeft()).resetAlias();
    Result rightResult = this.visitChild(1, e.getRight()).resetAlias();
    Context leftContext = leftResult.qualifiedContext();
    Context rightContext = rightResult.qualifiedContext();
    SqlNode sqlCondition = null;
    SqlLiteral condType = JoinConditionType.ON.symbol(POS);
    JoinType joinType = joinType(e.getJoinType());

    if (e.getJoinType() == JoinRelType.INNER && e.getCondition().isAlwaysTrue()) {
      joinType = dialect.emulateJoinTypeForCrossJoin();
      condType = JoinConditionType.NONE.symbol(POS);
    } else {
      sqlCondition = convertConditionToSqlNode(e.getCondition(), leftContext, rightContext,
          e.getLeft().getRowType().getFieldCount());
    }

    SqlNode rightSqlNode = rightResult.asFrom();

    if (e.getRight() instanceof LogicalTableFunctionScan || e.getRight() instanceof Uncollect) {
      rightSqlNode = generateRightChildForSqlJoinWithLateralViews(e, rightResult);
    }

    SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS), joinType.symbol(POS),
        rightSqlNode, condType, sqlCondition);

    return result(join, leftResult, rightResult);
  }

  /**
   * Uncollect RelNode represents a table function that expands an array/map column into a relation.
   * Super's implementation uses the conversion result of the child node as is and appends the function operator and an AS operator with two aliases.
   * This generates a SqlNode like:
   * <pre>
   *     UNNEST (SELECT `complex`.`c` AS `col` FROM (VALUES  (0)) AS `t` (`ZERO`)) AS `t_alias` (`col_alias`)
   * </pre>
   *
   * However, the above result adds complexity to the overall transformations for the following reasons:
   *   1. When expanding an array of type struct, the super's implementation generates individual columns for each data type inside the struct.
   *   2. Super's SqlNode has an expendable SELECT clause inside the UNNEST operator due to the preexisting extraneous child LogicalProject RelNode.
   *   3. Super's SqlNode does not mimic the original SqlNode constructed from an input SQL.
   *
   * Coral overrides Uncollect type RelNode with HiveUncollect to support returning a row set of a single column for operand type array[struct]
   * as opposed to super's behavior which returns individual columns for each data type inside the struct.
   * For the map operand, HiveUncollect's row type is same as the Uncollect's - a row set with two columns corresponding to (key, value).
   * This overriding implementation also extracts the column(s) to expand from the conversion result of the child node instead of appending the nested SELECT clause as is
   * and introduces the function operator as its parent.
   * This override outputs a more easily parsable SqlNode that is consistent with the original SqlNode.
   *
   * @param e RelNode of type HiveUncollect as input. Example:
   *          <pre>
   *             HiveUncollect
   *                      \
   *               LogicalProject(col=[$cor0.c])
   *                        \
   *                 LogicalValues(tuples=[[{ 0 }]])
   *          </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *         The SqlNode generated by converting the above RelNode example is:
   *        <pre>
   *                               (SqlBasicCall)
   *                           UNNEST(`complex`.`c`)
   *                                     |
   *            _________________________|__________________________
   *           |                                                   |
   *  Operator: CoralSqlUnnestOperator                 Operand: `complex`.`c`
   *
   *        </pre>
   */
  @Override
  public Result visit(Uncollect e) {

    // projectResult's SqlNode representation: SELECT `complex`.`c` AS `col` FROM (VALUES  (0)) AS `t` (`ZERO`)
    final Result projectResult = visitChild(0, e.getInput());

    // Extract column(s) to unnest from projectResult
    // to generate simpler operand for UNNEST operator
    final List<SqlNode> unnestOperands = new ArrayList<>();

    RelDataType recordType = null;
    boolean withOrdinality = e.withOrdinality;

    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      unnestOperands.add(projectResult.qualifiedContext().toSql(null, unnestCol));
      if (unnestCol.getType().getSqlTypeName().equals(SqlTypeName.ARRAY)
          && unnestCol.getType().getComponentType().getSqlTypeName().equals(SqlTypeName.ROW)) {
        recordType = unnestCol.getType().getComponentType();
      }
    }

    // Generate SqlCall with Coral's UNNEST Operator and the unnestOperands. Also, persist ordinality and operand's data type
    final SqlNode unnestCall =
        new CoralSqlUnnestOperator(withOrdinality, recordType).createCall(POS, unnestOperands.toArray(new SqlNode[0]));

    // Reuse the same projectResult.neededAlias since that's already unique by directly calling "new Result(...)"
    // instead of calling super.result(...), which will generate a new table alias and cause an extra
    // "AS" to be added to the generated SQL statement and make it invalid.
    return new Result(unnestCall, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(projectResult.neededAlias, e.getRowType()));
  }

  /**
   * Override this method to avoid the duplicated alias for {@link org.apache.calcite.rel.logical.LogicalValues}.
   * The original {@link org.apache.calcite.rel.rel2sql.SqlImplementor.Result#neededAlias} is `t`, we override
   * it to be `null`.
   * So that for the input SQL like `SELECT 1`, the translated SQL will be like:
   *
   * SELECT 1
   * FROM (VALUES  (0)) t (ZERO)
   *
   * Without this override, the translated SQL contains duplicated alias `t`:
   *
   * SELECT 1
   * FROM (VALUES  (0)) t (ZERO) t
   *
   * which is wrong.
   *
   * TODO: Identify and backport the fix from apache-calcite to linkedin-calcite since the duplicated alias issue only happens in linkedin-calcite
   */
  @Override
  public Result visit(Values e) {
    final Result originalResult = super.visit(e);
    return new Result(originalResult.node, originalResult.clauses, null, originalResult.neededType,
        originalResult.aliases);
  }

  private SqlNode generateRightChildForSqlJoinWithLateralViews(BiRel e, Result rightResult) {
    SqlNode rightSqlNode = rightResult.asFrom();

    final SqlNode rightLateral = SqlStdOperatorTable.LATERAL.createCall(POS, rightSqlNode);

    // Append the alias to unnestCall by generating SqlCall with AS operator
    RelDataType relDataType = e.getRight().getRowType();
    String alias = rightResult.aliases.entrySet().stream().filter(entry -> relDataType.equals(entry.getValue()))
        .findFirst().map(Map.Entry::getKey).orElse("coralDefaultColumnAlias");

    List<SqlNode> asOperands = createAsFullOperands(relDataType, rightLateral, alias);

    return SqlStdOperatorTable.AS.createCall(POS, asOperands);
  }

  /**
   * Override this method to handle the conversion for {@link RexFieldAccess} `f(x).y` where `f` is an operator,
   * which returns a struct containing field `y`.
   *
   * Calcite converts it to a {@link SqlIdentifier} with {@link SqlIdentifier#names} as ["f(x)", "y"] where "f(x)" and "y" are String,
   * which is opaque and not aligned with our expectation, since we want to apply transformations on `f(x)` with
   * {@link com.linkedin.coral.common.transformers.SqlCallTransformer}. Therefore, we override this
   * method to convert `f(x)` to {@link SqlCall} and `.` to {@link com.linkedin.coral.common.functions.FunctionFieldReferenceOperator#DOT}.
   *
   * With this override, the converted CoralSqlNode matches the previous SqlNode handed over to Calcite for validation and conversion
   * in `HiveSqlToRelConverter#convertQuery`.
   *
   * Check `CoralSparkTest#testConvertFieldAccessOnFunctionCall` for a more complex example with nested field access.
   */
  @Override
  public Context aliasContext(Map<String, RelDataType> aliases, boolean qualified) {
    return new AliasContext(INSTANCE, aliases, qualified) {
      @Override
      public SqlNode toSql(RexProgram program, RexNode rex) {
        if (rex.getKind() == SqlKind.FIELD_ACCESS) {
          final List<String> accessNames = new ArrayList<>();
          RexNode referencedExpr = rex;
          // Use the loop to get the top-level struct (`f(x)` in the example above),
          // and store the accessed field names ([`y`] in the example above)
          while (referencedExpr.getKind() == SqlKind.FIELD_ACCESS) {
            accessNames.add(((RexFieldAccess) referencedExpr).getField().getName());
            referencedExpr = ((RexFieldAccess) referencedExpr).getReferenceExpr();
          }
          final SqlKind sqlKind = referencedExpr.getKind();
          if (sqlKind == SqlKind.OTHER_FUNCTION || sqlKind == SqlKind.CAST || sqlKind == SqlKind.ROW) {
            SqlNode functionCall = toSql(program, referencedExpr);
            Collections.reverse(accessNames);
            for (String accessName : accessNames) {
              functionCall = FunctionFieldReferenceOperator.DOT.createCall(SqlParserPos.ZERO, functionCall,
                  new SqlIdentifier(accessName, POS));
            }
            return functionCall;
          }
        }
        return super.toSql(program, rex);
      }
    };
  }
}

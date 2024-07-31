/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.BiRel;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.*;
import org.apache.calcite.rel.logical.LogicalTableFunctionScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexProgram;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;
import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;

import static com.google.common.base.Preconditions.*;
import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


public class RelToTrinoConverter extends RelToSqlConverter {

  /**
   * We introduce this configuration for LinkedIn's internal use since our Trino is extending the following legacy/internal supports:
   * (1) Unnest array of struct, refer to https://github.com/linkedin/coral/pull/93#issuecomment-912698600 for more information.
   *     If the value of key {@link CoralTrinoConfigKeys#SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT} is set to true, we don't add extra ROW
   *     wrapping in {@link RelToTrinoConverter#visit(Uncollect)}
   * (2) Some internally registered UDFs which should not be converted, like `to_date`.
   *     If the value of key {@link CoralTrinoConfigKeys#AVOID_TRANSFORM_TO_DATE_UDF} is set to true, we don't transform `to_date` UDF
   *     in {@link com.linkedin.coral.trino.rel2trino.transformers.ToDateOperatorTransformer}
   * (3) We need to adjust the return type for some functions using cast, since the converted Trino function's return type is not
   *     aligned with the Hive function's return type. For example, if the value of key {@link CoralTrinoConfigKeys#CAST_DATEADD_TO_STRING}
   *     is set to true, we would cast the converted RexCall to `varchar` type (date_add(xxx) -> cast(date_add(xxx) as varchar))
   * For uses outside LinkedIn, just ignore this configuration.
   */
  private Map<String, Boolean> configs = new HashMap<>();
  private HiveMetastoreClient _hiveMetastoreClient;

  /**
   * Creates a RelToTrinoConverter.
   * @param mscClient client interface used to interact with the Hive Metastore service.
   */
  public RelToTrinoConverter(HiveMetastoreClient mscClient) {
    super(CoralRelToSqlNodeConverter.INSTANCE);
    _hiveMetastoreClient = mscClient;
  }

  /**
   * Creates a RelToTrinoConverter.
   * @param mscClient client interface used to interact with the Hive Metastore service.
   * @param configs configs
   */
  public RelToTrinoConverter(HiveMetastoreClient mscClient, Map<String, Boolean> configs) {
    super(CoralRelToSqlNodeConverter.INSTANCE);
    checkNotNull(configs);
    this.configs = configs;
    _hiveMetastoreClient = mscClient;
  }

  /**
   * Convert relational algebra to Trino's SQL
   * @param relNode calcite relational algebra representation of SQL
   * @return SQL string
   */
  public String convert(RelNode relNode) {
    SqlNode sqlNode = convertToSqlNode(relNode);

    SqlNode sqlNodeWithRelDataTypeDerivedConversions =
        sqlNode.accept(new DataTypeDerivedSqlCallConverter(_hiveMetastoreClient, sqlNode));

    SqlNode sqlNodeWithUDFOperatorConverted =
        sqlNodeWithRelDataTypeDerivedConversions.accept(new CoralToTrinoSqlCallConverter(configs));
    return sqlNodeWithUDFOperatorConverted.accept(new TrinoSqlRewriter()).toSqlString(TrinoSqlDialect.INSTANCE)
        .toString();
  }

  /**
   * Convert input relational algebra to calcite SqlNode
   * @param relNode relation algebra
   * @return calcite SqlNode representation for input
   */
  public SqlNode convertToSqlNode(RelNode relNode) {
    return visitChild(0, relNode).asStatement();
  }

  /**
   * @see #dispatch(RelNode)
   * @param window Relnode representing window clause
   * @return result of translation to sql
   */
  public Result visit(Window window) {
    return null;
  }

  /**
   * A Project RelNode represents a relational operator that projects a subset of columns from an input relational expression.
   * When projecting a NULL field, super's implementation casts this column to the derived data type to ensure that the column can be interpreted correctly.
   * If the derived data type is NULL, super's implementation casts the NULL field to a NULL data type explicitly.
   * This method override is intended to avoid adding a NULL cast to a NULL column.
   *
   * By default, super's implementation would generate SqlSelect statement of the form: SELECT CAST(NULL AS NULL) FROM foo.
   * However, the overriden implementation generates a modified SqlSelect statement that omits the CAST: SELECT NULL FROM foo.
   *
   * @param e Project RelNode.
   * @return Result of converting the RelNode to a SqlNode.
   */
  @Override
  public Result visit(Project e) {
    e.getVariablesSet();
    Result x = visitChild(0, e.getInput());
    parseCorrelTable(e, x);
    if (isStar(e.getChildExps(), e.getInput().getRowType(), e.getRowType())) {
      return x;
    }
    final Builder builder = x.builder(e, Clause.SELECT);
    final List<SqlNode> selectList = new ArrayList<>();
    for (RexNode ref : e.getChildExps()) {
      SqlNode sqlExpr = builder.context.toSql(null, ref);
      // Append the CAST operator when the derived data type is NON-NULL.
      RelDataTypeField targetField = e.getRowType().getFieldList().get(selectList.size());
      if (SqlUtil.isNullLiteral(sqlExpr, false) && !targetField.getValue().getSqlTypeName().equals(SqlTypeName.NULL)) {
        sqlExpr = SqlStdOperatorTable.CAST.createCall(POS, sqlExpr, dialect.getCastSpec(targetField.getType()));
      }
      addSelect(selectList, sqlExpr, e.getRowType());
    }
    builder.setSelect(new SqlNodeList(selectList, POS));
    return builder.result();
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
        com.linkedin.coral.com.google.common.collect.ImmutableMap.of(projectResult.neededAlias, e.getRowType()));
  }

  /**
   * This overridden function makes sure that the basetable names in the output SQL
   * will be in the form of "dbname.tablename" instead of "catalogname.dbname.tablename"
   *
   * Trino can have configurable catalog names. In that case the HiveToRelConverter's default "hive" catalog will
   * cause failures:  https://github.com/trinodb/trino/issues/5785. If catalogname is not prepended, Trino uses
   * the catalog name of the view being translated. If for example a view "hive2.db.view" whose coral-trino
   * translation returns "SELECT * FROM db.table" will be evaluated as "SELECT * FROM hive2.db.table" in Trino.
   *
   * Example:
   *  hive.default.foo_bar -&gt; default.foo_bar
   */
  @Override
  public Result visit(TableScan e) {
    List<String> qualifiedName = e.getTable().getQualifiedName();
    if (qualifiedName.size() > 2) {
      qualifiedName = qualifiedName.subList(qualifiedName.size() - 2, qualifiedName.size()); // take last two entries
    }
    final SqlIdentifier identifier = new SqlIdentifier(qualifiedName, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * Checks whether we do unnest in Trino. In this case the plan should have this structure:
   *    Uncollect
   *      LogicalProject(<List of projections>)
   *        LogicalValues(tuples=[[{ 0 }]])
   * Then when producing Trino SQL, we just generate UNNEST(<List of projections>). For other cases
   * Trino does not support UNNEST a query: UNNEST(SELECT ..).
   *  TODO: verify for HIVE parser if we get a Calcite plan for lateral view explode() in this same structure
   */
  private boolean isTrinoSupportedUnnest(Uncollect uncollect) {
    if (!(uncollect.getInput() instanceof Project)
        || !(((Project) uncollect.getInput()).getInput() instanceof Values)) {
      return false;
    }
    Values values = (Values) ((Project) uncollect.getInput()).getInput();
    // Values should have only single row with a single column with value 0.
    if (values.getTuples().size() == 1 && values.getTuples().get(0).size() == 1) {
      RexLiteral val = values.getTuples().get(0).get(0);
      return val.getValue().equals(new BigDecimal(0));
    }
    return false;
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

    SqlNode rightSqlNode = generateRightChildForSqlJoinWithLateralViews(e, rightResult);

    SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS),
        JoinType.COMMA.symbol(POS), rightSqlNode, JoinConditionType.NONE.symbol(POS), null);

    return result(join, leftResult, rightResult);
  }

  private SqlNode generateRightChildForSqlJoinWithLateralViews(BiRel e, Result rightResult) {
    SqlNode rightSqlNode = rightResult.asFrom();
    SqlNode lateralNode;

    // Drop the AS operator from the rightSqlNode if it exists and append the LATERAL operator on the inner SqlNode.
    if (rightSqlNode instanceof SqlCall && ((SqlCall) rightSqlNode).getOperator().kind == SqlKind.AS) {
      lateralNode = SqlStdOperatorTable.LATERAL.createCall(POS, (SqlNode) ((SqlCall) rightSqlNode).operand(0));
    } else {
      lateralNode = SqlStdOperatorTable.LATERAL.createCall(POS, rightSqlNode);
    }

    // Append the alias to lateralNode by generating SqlCall with AS operator
    RelDataType relDataType = e.getRight().getRowType();
    String alias = rightResult.aliases.entrySet().stream().filter(entry -> relDataType.equals(entry.getValue()))
        .findFirst().map(Map.Entry::getKey).orElse("coralDefaultColumnAlias");

    List<SqlNode> asOperands = createAsFullOperands(relDataType, lateralNode, alias);

    return SqlStdOperatorTable.AS.createCall(POS, asOperands);
  }

  /**
   * Override this method to avoid the duplicated alias for {@link org.apache.calcite.rel.logical.LogicalValues}.
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
   */
  @Override
  public Result visit(Values e) {
    final Result originalResult = super.visit(e);
    return new Result(originalResult.node, originalResult.clauses, null, originalResult.neededType,
        originalResult.aliases);
  }

  @Override
  public Context aliasContext(Map<String, RelDataType> aliases, boolean qualified) {
    // easier to keep inner class for accessing 'aliases' and 'qualified' variables as closure
    return new AliasContext(TrinoSqlDialect.INSTANCE, aliases, qualified) {
      /**
       * This function is overriden to handle the conversion of {@Link org.apache.calcite.rex.RexFieldAccess} like `F(X1..Xn).Y`
       * where `F` is a Coral function which is required to be converted into a Trino function by a specific SqlCallTransformer.
       * {@link SqlCallTransformer} only converts the function defined in a {@link SqlCall}, however,RexFieldAccess is converted
       * into a SqlIdentifier by default in Calcite referring to
       * {@link org.apache.calcite.rel.rel2sql.SqlImplementor.Context#toSql(RexProgram, RexNode)}
       * Therefore RexFieldAccess is converted into a SqlCall instead in this function.
       * @param program Required only if {@code rex} contains {@link RexLocalRef}
       * @param rex Expression to convert
       * @return SqlNode
       */
      @Override
      public SqlNode toSql(RexProgram program, RexNode rex) {
        if (rex.getKind() == SqlKind.FIELD_ACCESS) {
          final List<String> accessNames = new ArrayList<>();
          RexNode referencedExpr = rex;

          while (referencedExpr.getKind() == SqlKind.FIELD_ACCESS) {
            accessNames.add(((RexFieldAccess) referencedExpr).getField().getName());
            referencedExpr = ((RexFieldAccess) referencedExpr).getReferenceExpr();
          }
          if (referencedExpr.getKind() == SqlKind.OTHER_FUNCTION) {
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

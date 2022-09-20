/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.calcite.avatica.util.TimeUnit;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlAsOperator;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLateralOperator;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlSelectKeyword;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.SqlWindow;
import org.apache.calcite.sql.SqlWith;
import org.apache.calcite.sql.SqlWithItem;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.Iterables;
import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.HiveJsonTupleOperator;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.ASTNode;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.CoralParseDriver;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.HiveParser;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.Node;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.ParseDriver;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.ParseException;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


/**
 * Class to convert Hive Abstract Syntax Tree(AST) represented by {@link ASTNode} to
 * Calcite based AST represented using {@link SqlNode}.
 *
 * Hive AST nodes do not support polymorphic behavior for processing AST using, for example, visitors
 * ASTNode carries all the information as type and text fields and children nodes are of base class Node.
 * This requires constant casting of nodes and string processing to get the type and value out of a node.
 * This complicates analysis of the tree.
 *
 * This class converts the AST to Calcite based AST using {@link SqlNode}.This is more structured
 * allowing for simpler analysis code.
 *
 * NOTE:
 * There are certain difficulties in correct translation.
 * For example, for identifier named {@code s.field1} it's hard to ascertain if {@code s} is a
 * table name or column name of type struct. This is typically resolved by validators using scope but
 * we haven't specialized that part yet.
 */
public class ParseTreeBuilder extends AbstractASTVisitor<SqlNode, ParseTreeBuilder.ParseContext> {
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final HiveFunctionResolver functionResolver;

  /**
   * Constructs a parse tree builder
   */
  public ParseTreeBuilder(HiveFunctionResolver functionResolver) {
    this.functionResolver = functionResolver;
  }

  /**
   * Creates a parse tree for input sql. The input SQL must NOT contain any Dali function names.
   * It is okay for the sql to refer to dali views that use dali functions.
   * @param sql sql statement to convert to parse tree
   * @return Calcite SqlNode representing parse tree that calcite framework can understand
   */
  public SqlNode processSql(String sql) {
    return process(sql, null);
  }

  public SqlNode process(String sql, @Nullable Table hiveView) {
    ParseDriver pd = new CoralParseDriver();
    try {
      ASTNode root = pd.parse(sql);
      return processAST(root, hiveView);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private SqlNode processAST(ASTNode node, @Nullable Table hiveView) {
    ParseContext ctx = new ParseContext(hiveView);
    return visit(node, ctx);
  }

  @Override
  protected SqlNode visitTabAlias(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 1);
    return sqlNodes.get(0);
  }

  @Override
  protected SqlNode visitLateralView(ASTNode node, ParseContext ctx) {
    return visitLateralViewInternal(node, ctx, false);
  }

  @Override
  protected SqlNode visitLateralViewOuter(ASTNode node, ParseContext ctx) {
    return visitLateralViewInternal(node, ctx, true);
  }

  // lateral views are turned to:
  // "...FROM table, lateral (select col FROM UNNEST(table.col) as t(col))" query
  // For lateral outer, we need to replace UNNEST with
  //    UNNEST(if(table.col is null or cadinality(table.col) == 0, [null], table.col))
  private SqlNode visitLateralViewInternal(ASTNode node, ParseContext ctx, boolean isOuter) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2 && sqlNodes.get(0) instanceof SqlNodeList);

    // rightNode is AS.createCall(unnest, t, col)
    SqlNode rightNode = Iterables.getOnlyElement((SqlNodeList) sqlNodes.get(0));
    if (!(rightNode instanceof SqlCall) || !(((SqlCall) rightNode).getOperator() instanceof SqlAsOperator)) {
      throw new UnsupportedOperationException(format("Unsupported LATERAL VIEW without AS: %s", rightNode));
    }
    SqlCall aliasCall = (SqlCall) rightNode;
    List<SqlNode> aliasOperands = aliasCall.getOperandList();
    checkState(aliasOperands.get(0) instanceof SqlCall);
    SqlCall tableFunctionCall = (SqlCall) aliasOperands.get(0);

    if (tableFunctionCall.getOperator() instanceof CoralSqlUnnestOperator) {
      return visitLateralViewExplode(sqlNodes, aliasOperands, tableFunctionCall, isOuter);
    }

    if (tableFunctionCall.getOperator() instanceof HiveJsonTupleOperator) {
      return visitLateralViewJsonTuple(sqlNodes, aliasOperands, tableFunctionCall);
    }

    if (tableFunctionCall.getOperator() instanceof VersionedSqlUserDefinedFunction) {
      return visitLateralViewUDTF(sqlNodes, aliasOperands, tableFunctionCall);
    }

    throw new UnsupportedOperationException(format("Unsupported LATERAL VIEW operator: %s", tableFunctionCall));
  }

  /**
   * For generic UDTFs, we treat them as LinkedIn UDFs and make the following conversion:
   *
   * SELECT a, t.col1
   * FROM test.tableOne
   * LATERAL VIEW `com.linkedin.coral.hive.hive2rel.CoralTestUDTF`(`tableone`.`a`) `t`
   * ->
   * SELECT a, t.col1
   * FROM test.tableOne
   * LATERAL COLLECTION_TABLE(`com.linkedin.coral.hive.hive2rel.CoralTestUDTF`(`tableone`.`a`)) AS `t` (`col1`)
   *
   * therefore, we need to get the return field names (`col1` in the above example) of the UDTF from
   * `StaticHiveFunctionRegistry.UDTF_RETURN_FIELD_NAME_MAP`.
   */
  private SqlNode visitLateralViewUDTF(List<SqlNode> sqlNodes, List<SqlNode> aliasOperands, SqlCall tableFunctionCall) {
    SqlNode lateralCall = SqlStdOperatorTable.LATERAL.createCall(ZERO,
        new SqlLateralOperator(SqlKind.COLLECTION_TABLE).createCall(ZERO, tableFunctionCall));
    final String functionName = tableFunctionCall.getOperator().getName();
    ImmutableList<String> fieldNames =
        StaticHiveFunctionRegistry.UDTF_RETURN_FIELD_NAME_MAP.getOrDefault(functionName, null);
    if (fieldNames == null) {
      throw new RuntimeException("User defined table function " + functionName + " is not registered.");
    }
    List<SqlNode> asOperands = new ArrayList<>();
    asOperands.add(lateralCall);
    asOperands.add(aliasOperands.get(1));
    fieldNames.forEach(name -> asOperands.add(new SqlIdentifier(name, ZERO)));
    SqlCall aliasCall = SqlStdOperatorTable.AS.createCall(ZERO, asOperands);
    return new SqlJoin(ZERO, sqlNodes.get(1), SqlLiteral.createBoolean(false, ZERO), JoinType.COMMA.symbol(ZERO),
        aliasCall/*lateralCall*/, JoinConditionType.NONE.symbol(ZERO), null);
  }

  private SqlNode visitLateralViewExplode(List<SqlNode> sqlNodes, List<SqlNode> aliasOperands,
      SqlCall tableFunctionCall, boolean isOuter) {
    final int operandCount = aliasOperands.size();
    // explode array if operandCount == 3: LATERAL VIEW EXPLODE(op0) op1 AS op2
    // explode map if operandCount == 4: LATERAL VIEW EXPLODE(op0) op1 AS op2, op3
    // posexplode array if operandCount == 4: LATERAL VIEW POSEXPLODE(op0) op1 AS op2, op3
    // if operandCount == 2: `LATERAL VIEW EXPLODE(op0) op1` or `LATERAL VIEW POSEXPLODE(op0) op1`
    //   if op0 is a map, it implies `AS key, value` where key, value are auto-named by Hive
    //   if op0 is an array, it implies `AS col` for `explode` or `AS ORDINALITY, col` for `posexplode`, which are auto-generated by Hive
    //   The logic above will be implemented as part of Calcite SqlNode validation
    //   Note that `operandCount == 2 && isOuter` is not supported yet due to the lack of type information needed
    //   to derive the correct IF function parameters.
    checkState(operandCount == 2 || operandCount == 3 || operandCount == 4,
        format("Unsupported LATERAL VIEW EXPLODE operand number: %d", operandCount));
    // TODO The code below assumes LATERAL VIEW is used with UNNEST EXPLODE/POSEXPLODE only. It should be made more generic.
    SqlCall unnestCall = tableFunctionCall;
    SqlNode unnestOperand = unnestCall.operand(0);
    final SqlOperator operator = unnestCall.getOperator();

    if (isOuter) {
      checkState(operandCount > 2,
          "LATERAL VIEW OUTER EXPLODE without column aliases is not supported. Add 'AS col' or 'AS key, value' to fix it");
      // transforms unnest(b) to unnest( if(b is null or cardinality(b) = 0, ARRAY(null)/MAP(null, null), b))
      SqlNode operandIsNull = SqlStdOperatorTable.IS_NOT_NULL.createCall(ZERO, unnestOperand);
      SqlNode emptyArray = SqlStdOperatorTable.GREATER_THAN.createCall(ZERO,
          SqlStdOperatorTable.CARDINALITY.createCall(ZERO, unnestOperand), SqlLiteral.createExactNumeric("0", ZERO));
      SqlNode ifCondition = SqlStdOperatorTable.AND.createCall(ZERO, operandIsNull, emptyArray);
      // array of [null] or map of (null, null) should be 3rd param to if function. With our type inference, calcite acts
      // smart and for unnest(array[null]) or unnest(map(null, null)) determines return type to be null
      SqlNode arrayOrMapOfNull;
      if (operandCount == 3
          || (operator instanceof CoralSqlUnnestOperator && ((CoralSqlUnnestOperator) operator).withOrdinality)) {
        arrayOrMapOfNull = SqlStdOperatorTable.ARRAY_VALUE_CONSTRUCTOR.createCall(ZERO, SqlLiteral.createNull(ZERO));
      } else {
        arrayOrMapOfNull = SqlStdOperatorTable.MAP_VALUE_CONSTRUCTOR.createCall(ZERO, SqlLiteral.createNull(ZERO),
            SqlLiteral.createNull(ZERO));
      }
      Function hiveIfFunction = functionResolver.tryResolve("if", null, 1);
      unnestOperand = hiveIfFunction.createCall(SqlLiteral.createCharString("if", ZERO),
          ImmutableList.of(ifCondition, unnestOperand, arrayOrMapOfNull), null);
    }
    unnestCall = operator.createCall(ZERO, unnestOperand);

    SqlNode lateralCall = SqlStdOperatorTable.LATERAL.createCall(ZERO, unnestCall);

    // The following code can work in both of the two cases:
    // A. Table alias only, no column aliases.
    // B. Both table and column aliases.  Note that in this case, the number of column aliases need to match the
    //    actual number of columns generated from the EXPLODE function, which is calculated by HiveUncollect.deriveRowType
    /** See also {@link HiveUncollect#deriveRowType()} */
    List<SqlNode> asOperands = new ArrayList<>();
    asOperands.add(lateralCall);

    // For POSEXPLODE case, we need to change the order of 2 alias. i.e. `pos, val` -> `val, pos` to be aligned with calcite validation
    if (operator instanceof CoralSqlUnnestOperator && ((CoralSqlUnnestOperator) operator).withOrdinality
        && operandCount == 4) {
      asOperands.add(aliasOperands.get(1));
      asOperands.add(aliasOperands.get(3));
      asOperands.add(aliasOperands.get(2));
    } else {
      asOperands.addAll(aliasOperands.subList(1, aliasOperands.size()));
    }
    SqlNode as = SqlStdOperatorTable.AS.createCall(ZERO, asOperands);

    return new SqlJoin(ZERO, sqlNodes.get(1), SqlLiteral.createBoolean(false, ZERO), JoinType.COMMA.symbol(ZERO), as,
        JoinConditionType.NONE.symbol(ZERO), null);
  }

  private SqlNode visitLateralViewJsonTuple(List<SqlNode> sqlNodes, List<SqlNode> aliasOperands, SqlCall sqlCall) {
    /*
     Represent
        LATERAL VIEW json_tuple(json, p1, p2) jt AS a, b
     as
        LATERAL (
          SELECT
            IF(p1 is supported JSON key, get_json_object(json, '$["${p1}"]'), NULL) a,
            IF(p2 is supported JSON key, get_json_object(json, '$["${p1}"]'), NULL) b
        ) AS jt(a, b)
     TODO the relation alias `jt` is being lost by downstream transformations
     */

    Function getJsonObjectFunction = functionResolver.tryResolve("get_json_object", null, 2);
    Function ifFunction = functionResolver.tryResolve("if", null, 3);

    List<SqlNode> jsonTupleOperands = sqlCall.getOperandList();
    SqlNode jsonInput = jsonTupleOperands.get(0);

    List<SqlNode> projections = new ArrayList<>();
    for (int jsonKeyPosition = 0; jsonKeyPosition < jsonTupleOperands.size() - 1; jsonKeyPosition++) {
      SqlNode jsonKey = jsonTupleOperands.get(1 + jsonKeyPosition);
      SqlNode keyAlias = aliasOperands.get(2 + jsonKeyPosition);

      // '$["jsonKey"]'
      SqlCall jsonPath = SqlStdOperatorTable.CONCAT.createCall(ZERO,
          SqlStdOperatorTable.CONCAT.createCall(ZERO, SqlLiteral.createCharString("$[\"", ZERO), jsonKey),
          SqlLiteral.createCharString("\"]", ZERO));

      SqlCall getJsonObjectCall =
          getJsonObjectFunction.createCall(SqlLiteral.createCharString(getJsonObjectFunction.getFunctionName(), ZERO),
              ImmutableList.of(jsonInput, jsonPath), null);
      // TODO Hive get_json_object returns a string, but currently is mapped in Trino to json_extract which returns a json. Once fixed, remove the CAST
      SqlCall castToString = SqlStdOperatorTable.CAST.createCall(ZERO, getJsonObjectCall,
          // TODO This results in CAST to VARCHAR(65535), which may be too short, but there seems to be no way to avoid that.
          //  even `new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.VARCHAR, Integer.MAX_VALUE - 1, ZERO), ZERO)` results in a limited VARCHAR precision.
          createBasicTypeSpec(SqlTypeName.VARCHAR));
      // TODO support jsonKey containing a quotation mark (") or backslash (\)
      SqlCall ifCondition =
          HiveRLikeOperator.RLIKE.createCall(ZERO, jsonKey, SqlLiteral.createCharString("^[^\\\"]*$", ZERO));
      SqlCall ifFunctionCall = ifFunction.createCall(SqlLiteral.createCharString(ifFunction.getFunctionName(), ZERO),
          ImmutableList.of(ifCondition, castToString, SqlLiteral.createNull(ZERO)), null);
      SqlNode projection = ifFunctionCall;
      // Currently only explicit aliasing is supported. Implicit alias would be c0, c1, etc.
      projections.add(SqlStdOperatorTable.AS.createCall(ZERO, projection, keyAlias));
    }

    SqlNode select =
        new SqlSelect(ZERO, null, new SqlNodeList(projections, ZERO), null, null, null, null, null, null, null, null);
    SqlNode lateral = SqlStdOperatorTable.LATERAL.createCall(ZERO, select);
    SqlCall lateralAlias = SqlStdOperatorTable.AS.createCall(ZERO,
        ImmutableList.<SqlNode> builder().add(lateral).addAll(aliasOperands.subList(1, aliasOperands.size())).build());
    SqlNode joinNode = new SqlJoin(ZERO, sqlNodes.get(1), SqlLiteral.createBoolean(false, ZERO),
        JoinType.COMMA.symbol(ZERO), lateralAlias, JoinConditionType.NONE.symbol(ZERO), null);
    return joinNode;
  }

  @Override
  protected SqlNode visitLeftSemiJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitCrossJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitFullOuterJoin(ASTNode node, ParseContext ctx) {
    return processJoin(node, ctx, JoinType.FULL);
  }

  @Override
  protected SqlNode visitRightOuterJoin(ASTNode node, ParseContext ctx) {
    return processJoin(node, ctx, JoinType.RIGHT);
  }

  @Override
  protected SqlNode visitJoin(ASTNode node, ParseContext ctx) {
    return processJoin(node, ctx, JoinType.INNER);
  }

  @Override
  protected SqlNode visitLeftOuterJoin(ASTNode node, ParseContext ctx) {
    return processJoin(node, ctx, JoinType.LEFT);
  }

  private SqlNode processJoin(ASTNode node, ParseContext ctx, JoinType joinType) {
    List<SqlNode> children = visitChildren(node, ctx);
    checkState(children.size() == 2 || children.size() == 3);
    JoinConditionType conditionType;
    SqlNode condition = null;
    if (children.size() == 2) {
      conditionType = JoinConditionType.NONE;
    } else {
      conditionType = JoinConditionType.ON;
      condition = children.get(2);
    }

    return new SqlJoin(ZERO, children.get(0), SqlLiteral.createBoolean(false, ZERO), joinType.symbol(ZERO),
        children.get(1), conditionType.symbol(ZERO), condition);
  }

  @Override
  protected SqlNode visitFalse(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createBoolean(false, ZERO);
  }

  @Override
  protected SqlNode visitTrue(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createBoolean(true, ZERO);
  }

  @Override
  protected SqlNode visitNullToken(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createNull(ZERO);
  }

  @Override
  protected SqlNode visitLimit(ASTNode node, ParseContext ctx) {
    ctx.fetch = visitChildren(node, ctx).get(0);
    return ctx.fetch;
  }

  @Override
  protected SqlNode visitUnion(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);

    // We use Hive 1.1 in which UNION defaults to UNION_ALL
    return new SqlBasicCall(SqlStdOperatorTable.UNION_ALL, sqlNodes.toArray(new SqlNode[0]), ZERO);
  }

  @Override
  protected SqlNode visitNumber(ASTNode node, ParseContext ctx) {
    String strval = node.getText();
    return SqlLiteral.createExactNumeric(strval, ZERO);
  }

  @Override
  protected SqlNode visitAllColRef(ASTNode node, ParseContext ctx) {
    List<SqlNode> children = visitChildren(node, ctx);
    // This is to allow t.*
    // In Hive ASTNode Tree, "t.*" has the following shape
    // TOK_ALLCOLREF
    // - TOK_TABLE_OR_COL
    //   - "t"
    List<String> names = new ArrayList<>();
    if (children != null) {
      for (SqlNode child : children) {
        names.addAll(((SqlIdentifier) child).names);
      }
    }
    names.add("*");
    List<SqlParserPos> sqlParserPos = Collections.nCopies(names.size(), ZERO);
    SqlNode star = SqlIdentifier.star(names, ZERO, sqlParserPos);
    return star;
  }

  @Override
  protected SqlNode visitHaving(ASTNode node, ParseContext ctx) {
    checkState(node.getChildren().size() == 1);
    ctx.having = visit((ASTNode) node.getChildren().get(0), ctx);
    return ctx.having;
  }

  @Override
  protected SqlNode visitWhere(ASTNode node, ParseContext ctx) {
    checkState(node.getChildren().size() == 1);
    ctx.where = visit((ASTNode) node.getChildren().get(0), ctx);
    return ctx.where;
  }

  @Override
  protected SqlNode visitSortColNameDesc(ASTNode node, ParseContext ctx) {
    return visitSortColName(node, ctx, true);
  }

  @Override
  protected SqlNode visitSortColNameAsc(ASTNode node, ParseContext ctx) {
    return visitSortColName(node, ctx, false);
  }

  private SqlNode visitSortColName(ASTNode node, ParseContext ctx, boolean descending) {
    List<SqlNode> children = visitChildren(node, ctx);
    checkState(children.size() == 1);
    if (!descending) {
      return children.get(0);
    }
    return new SqlBasicCall(SqlStdOperatorTable.DESC, new SqlNode[] { children.get(0) }, ZERO);
  }

  @Override
  protected SqlNode visitOrderBy(ASTNode node, ParseContext ctx) {
    List<SqlNode> orderByCols = visitChildren(node, ctx);
    ctx.orderBy = new SqlNodeList(orderByCols, ZERO);
    return ctx.orderBy;
  }

  @Override
  protected SqlNode visitGroupBy(ASTNode node, ParseContext ctx) {
    List<SqlNode> grpCols = visitChildren(node, ctx);
    ctx.grpBy = new SqlNodeList(grpCols, ZERO);
    return ctx.grpBy;
  }

  @Override
  protected SqlNode visitOperator(ASTNode node, ParseContext ctx) {
    ArrayList<Node> children = node.getChildren();
    if (children.size() == 1) {
      return visitUnaryOperator(node, ctx);
    } else if (children.size() == 2) {
      return visitBinaryOperator(node, ctx);
    } else {
      throw new RuntimeException(
          String.format("Unhandled AST operator: %s with > 2 children, tree: %s", node.getText(), node.dump()));
    }
  }

  private SqlNode visitUnaryOperator(ASTNode node, ParseContext ctx) {
    SqlNode operand = visit((ASTNode) node.getChildren().get(0), ctx);
    SqlOperator operator = functionResolver.resolveUnaryOperator(node.getText());
    return operator.createCall(ZERO, operand);
  }

  private SqlNode visitBinaryOperator(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2);
    return functionResolver.resolveBinaryOperator(node.getText()).createCall(ZERO, sqlNodes);
  }

  @Override
  protected SqlNode visitDotOperator(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes != null && sqlNodes.size() == 2);
    // return SqlStdOperatorTable.DOT.createCall(ZERO, sqlNodes);
    if (sqlNodes.get(0) instanceof SqlIdentifier) {
      SqlIdentifier left = (SqlIdentifier) sqlNodes.get(0);
      SqlIdentifier right = (SqlIdentifier) sqlNodes.get(1);
      Iterable<String> names = Iterables.concat(left.names, right.names);
      return new SqlIdentifier(ImmutableList.copyOf(names), ZERO);
    } else {
      return FunctionFieldReferenceOperator.DOT.createCall(ZERO, sqlNodes);
    }
  }

  @Override
  protected SqlNode visitLParen(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2);
    return SqlStdOperatorTable.ITEM.createCall(ZERO, sqlNodes);
  }

  @Override
  protected SqlNode visitFunctionStar(ASTNode node, ParseContext ctx) {
    ASTNode functionNode = (ASTNode) node.getChildren().get(0);
    List<SqlOperator> functions = SqlStdOperatorTable.instance().getOperatorList().stream()
        .filter(f -> functionNode.getText().equalsIgnoreCase(f.getName())).collect(Collectors.toList());
    checkState(functions.size() == 1);
    return new SqlBasicCall(functions.get(0), new SqlNode[] { new SqlIdentifier("", ZERO) }, ZERO);
  }

  @Override
  protected SqlNode visitFunctionDistinct(ASTNode node, ParseContext ctx) {
    return visitFunctionInternal(node, ctx, SqlSelectKeyword.DISTINCT.symbol(ZERO));
  }

  @Override
  protected SqlNode visitFunction(ASTNode node, ParseContext ctx) {
    return visitFunctionInternal(node, ctx, null);
  }

  private SqlNode visitFunctionInternal(ASTNode node, ParseContext ctx, SqlLiteral quantifier) {
    ArrayList<Node> children = node.getChildren();
    checkState(children.size() > 0);
    ASTNode functionNode = (ASTNode) children.get(0);
    String functionName = functionNode.getText();
    List<SqlNode> sqlOperands = visitChildren(children, ctx);
    Function hiveFunction = functionResolver.tryResolve(functionName, ctx.hiveTable.orElse(null),
        // The first element of sqlOperands is the operator itself. The actual # of operands is sqlOperands.size() - 1
        sqlOperands.size() - 1);

    // Special treatment for Window Function
    SqlNode lastSqlOperand = sqlOperands.get(sqlOperands.size() - 1);
    if (lastSqlOperand instanceof SqlWindow) {
      // For a SQL example of "func() OVER (PARTITIONED BY ...)":
      // In Hive, TOK_WINDOWSPEC (the window spec) is the last operand of the function "func":
      //    TOK_FUNCTION will have 1+N+1 children, where the first is the function name, the last is TOK_WINDOWSPEC
      //    and everything in between are the operands of the function
      // In Calcite, SqlWindow (the window spec) is a sibling of the function "func":
      //    SqlBasicCall("OVER") will have 2 children: "func" and SqlWindow
      /** See {@link #visitWindowSpec(ASTNode, ParseContext)} for SQL, AST Tree and SqlNode Tree examples */
      SqlNode func =
          hiveFunction.createCall(sqlOperands.get(0), sqlOperands.subList(1, sqlOperands.size() - 1), quantifier);
      SqlNode window = lastSqlOperand;
      return new SqlBasicCall(SqlStdOperatorTable.OVER, new SqlNode[] { func, window }, ZERO);
    }

    if (functionName.equalsIgnoreCase("SUBSTRING")) {
      // Calcite overrides instance of SUBSTRING with its default SUBSTRING function as defined in SqlStdOperatorTable,
      // so we rewrite instances of SUBSTRING as SUBSTR
      SqlNode originalNode = sqlOperands.get(0);
      SqlNode substrNode = new SqlIdentifier(ImmutableList.of("SUBSTR"), null, originalNode.getParserPosition(), null);
      hiveFunction = functionResolver.tryResolve("SUBSTR", ctx.hiveTable.orElse(null), sqlOperands.size() - 1);
      return hiveFunction.createCall(substrNode, sqlOperands.subList(1, sqlOperands.size()), quantifier);
    }

    return hiveFunction.createCall(sqlOperands.get(0), sqlOperands.subList(1, sqlOperands.size()), quantifier);
  }

  @Override
  protected SqlNode visitSelectExpr(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    if (sqlNodes.size() == 1) {
      return sqlNodes.get(0);
    } else if (sqlNodes.size() == 2) {
      return new SqlBasicCall(SqlStdOperatorTable.AS, sqlNodes.toArray(new SqlNode[0]), ZERO);
    } else if (sqlNodes.size() >= 3) {
      // lateral view alias have 3+ args
      List<SqlNode> nodes = new ArrayList<>();
      nodes.add(sqlNodes.get(0));
      nodes.add(sqlNodes.get(sqlNodes.size() - 1)); // last
      nodes.addAll(sqlNodes.subList(1, sqlNodes.size() - 1));
      return new SqlBasicCall(SqlStdOperatorTable.AS, nodes.toArray(new SqlNode[0]), ZERO);
    } else {
      throw new UnhandledASTTokenException(node);
    }
  }

  @Override
  protected SqlNode visitSelectDistinct(ASTNode node, ParseContext ctx) {
    ctx.keywords = new SqlNodeList(ImmutableList.of(SqlSelectKeyword.DISTINCT.symbol(ZERO)), ZERO);
    return visitSelect(node, ctx);
  }

  @Override
  protected SqlNode visitSelect(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    ctx.selects = new SqlNodeList(sqlNodes, ZERO);
    return ctx.selects;
  }

  @Override
  protected SqlNode visitTabRefNode(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes != null && !sqlNodes.isEmpty());
    if (sqlNodes.size() == 1) {
      return sqlNodes.get(0);
    }
    if (sqlNodes.size() == 2) {
      return new SqlBasicCall(SqlStdOperatorTable.AS, sqlNodes.toArray(new SqlNode[0]), ZERO);
    }

    throw new UnhandledASTTokenException(node);
  }

  @Override
  protected SqlNode visitTabnameNode(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    List<String> names =
        sqlNodes.stream().map(s -> ((SqlIdentifier) s).names).flatMap(List::stream).collect(Collectors.toList());

    return new SqlIdentifier(names, ZERO);
  }

  protected SqlNode visitSubqueryOp(ASTNode node, ParseContext ctx) {
    throw new UnhandledASTTokenException(node);
  }

  private SqlOperator getSubQueryOp(ASTNode node, ParseContext ctx) {
    checkState(node.getChildren().size() == 1, node.dump());
    String opName = ((ASTNode) node.getChildren().get(0)).getText();
    if (opName.equalsIgnoreCase("in")) {
      return SqlStdOperatorTable.IN;
    } else if (opName.equalsIgnoreCase("exists")) {
      return SqlStdOperatorTable.EXISTS;
    } else {
      throw new UnhandledASTTokenException(node);
    }
  }

  @Override
  protected SqlNode visitSubqueryExpr(ASTNode node, ParseContext ctx) {
    ArrayList<Node> children = node.getChildren();
    checkState(children.size() >= 2);
    SqlOperator op = getSubQueryOp((ASTNode) node.getChildren().get(0), ctx);
    SqlNode subQuery = visit((ASTNode) children.get(1), ctx);
    List<SqlNode> operands = new ArrayList<>();
    operands.add(subQuery);
    if (children.size() == 3) {
      SqlNode lhs = visit(((ASTNode) children.get(2)), ctx);
      operands.add(0, lhs);
    }
    return new SqlBasicCall(op, operands.toArray(new SqlNode[0]), ZERO);
  }

  @Override
  protected SqlNode visitSubquery(ASTNode node, ParseContext ctx) {
    ParseContext subQueryContext = new ParseContext(ctx.getHiveTable().orElse(null));
    List<SqlNode> sqlNodes = visitChildren(node, subQueryContext);
    if (sqlNodes.size() == 1) {
      return sqlNodes.get(0);
    } else if (sqlNodes.size() == 2) {
      return new SqlBasicCall(SqlStdOperatorTable.AS, sqlNodes.toArray(new SqlNode[0]), ZERO);
    }
    throw new UnhandledASTTokenException(node);
  }

  @Override
  protected SqlNode visitFrom(ASTNode node, ParseContext ctx) {
    List<SqlNode> children = visitChildren(node, ctx);
    if (children.size() == 1) {
      ctx.from = children.get(0);
      return children.get(0);
    }
    // TODO: handle join
    throw new UnsupportedASTException(node.dump());
  }

  @Override
  protected SqlNode visitIdentifier(ASTNode node, ParseContext ctx) {
    return new SqlIdentifier(node.getText(), ZERO);
  }

  @Override
  protected SqlNode visitStringLiteral(ASTNode node, ParseContext ctx) {
    // TODO: Add charset here. UTF-8 is not supported by calcite
    String text = node.getText();
    checkState(text.length() >= 2);
    return SqlLiteral.createCharString(text.substring(1, text.length() - 1), ZERO);
  }

  @Override
  protected SqlNode visitBigintLiteral(ASTNode node, ParseContext ctx) {
    String text = node.getText();
    checkState(text.length() >= 2);
    return SqlLiteral.createExactNumeric(text.substring(0, text.length() - 1), ZERO);
  }

  @Override
  protected SqlNode visitDateLiteral(ASTNode node, ParseContext ctx) {
    String text = node.getText();
    checkState(text.length() >= 2);
    return SqlLiteral.createCharString(text.substring(1, text.length() - 1), ZERO);
  }

  @Override
  protected SqlNode visitQueryNode(ASTNode node, ParseContext ctx) {
    ArrayList<Node> children = node.getChildren();
    checkState(children != null && !children.isEmpty());
    SqlNode cte = null;
    ParseContext qc = new ParseContext(ctx.getHiveTable().orElse(null));
    for (Node child : node.getChildren()) {
      ASTNode ast = (ASTNode) child;
      if (ast.getType() == HiveParser.TOK_CTE) {
        // Child of type TOK_CTE represents the "WITH" list
        /** See {@link #visitCTE(ASTNode, ParseContext) visitCTE} for the return value */
        cte = visit(ast, new ParseContext(null));
      } else {
        // The return values are ignored since all other children of SELECT query will be captures via ParseConext qc.
        visit(ast, qc);
      }
    }
    SqlSelect select = new SqlSelect(ZERO, qc.keywords, qc.selects, qc.from, qc.where, qc.grpBy, qc.having, null,
        qc.orderBy, null, qc.fetch);
    if (cte != null) {
      // Calcite uses "SqlWith(SqlNodeList of SqlWithItem, SqlSelect)" to represent queries with WITH
      /** See {@link #visitCTE(ASTNode, ParseContext) visitCTE} for details */
      return new SqlWith(ZERO, (SqlNodeList) cte, select);
    } else {
      return select;
    }
  }

  protected SqlNode visitNil(ASTNode node, ParseContext ctx) {
    return visitChildren(node, ctx).get(0);
  }

  @Override
  protected SqlNode visitBoolean(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.BOOLEAN);
  }

  @Override
  protected SqlNode visitInt(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.INTEGER);
  }

  @Override
  protected SqlNode visitSmallInt(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.SMALLINT);
  }

  @Override
  protected SqlNode visitBigInt(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.BIGINT);
  }

  @Override
  protected SqlNode visitTinyInt(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.TINYINT);
  }

  @Override
  protected SqlNode visitFloat(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.FLOAT);
  }

  @Override
  protected SqlNode visitDouble(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.DOUBLE);
  }

  @Override
  protected SqlNode visitVarchar(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.VARCHAR);
  }

  @Override
  protected SqlNode visitChar(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.CHAR);
  }

  @Override
  protected SqlNode visitString(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.VARCHAR);
  }

  @Override
  protected SqlNode visitBinary(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.BINARY);
  }

  @Override
  protected SqlNode visitDecimal(ASTNode node, ParseContext ctx) {
    if (node.getChildCount() == 2) {
      try {
        final SqlTypeNameSpec typeNameSpec = new SqlBasicTypeNameSpec(SqlTypeName.DECIMAL,
            Integer.parseInt(((ASTNode) node.getChildren().get(0)).getText()),
            Integer.parseInt(((ASTNode) node.getChildren().get(1)).getText()), ZERO);
        return new SqlDataTypeSpec(typeNameSpec, ZERO);
      } catch (NumberFormatException e) {
        return createBasicTypeSpec(SqlTypeName.DECIMAL);
      }
    }
    return createBasicTypeSpec(SqlTypeName.DECIMAL);
  }

  @Override
  protected SqlNode visitDate(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.DATE);
  }

  @Override
  protected SqlNode visitTimestamp(ASTNode node, ParseContext ctx) {
    return createBasicTypeSpec(SqlTypeName.TIMESTAMP);
  }

  @Override
  protected SqlNode visitIsNull(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createCharString("is null", ZERO);
  }

  @Override
  protected SqlNode visitIsNotNull(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createCharString("is not null", ZERO);
  }

  @Override
  protected SqlNode visitKeywordLiteral(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createCharString(node.getText(), ZERO);
  }

  @Override
  protected SqlNode visitCTE(ASTNode node, ParseContext ctx) {
    // ASTNode tree from Hive Antlr
    // TOK_QUERY
    // - TOK_FROM
    // - TOK_INSERT
    // -- TOK_DESTINATION
    // -- TOK_SELECT
    // - TOK_CTE       <-- processed by this method visitCTE
    // -- TOK_SUBQUERY
    // --- TOK_QUERY
    // --- LITERAL (alias of the subquery)
    // -- TOK_SUBQUERY
    // --- TOK_QUERY
    // --- LITERAL (alias of the subquery)

    // SqlNode tree expected by Calcite
    // - SqlWith
    // -- withList: SqlNodeList  <-- returned by this method visitCTE
    // --- element: SqlWithItem
    // ---- id: SimpleIdentifier
    // ---- columnList: SqlNodeList (column aliases - not supported by Hive)
    // ---- definition: SqlSelect
    // -- node: SqlSelect

    ArrayList<Node> children = node.getChildren();
    checkState(children != null && !children.isEmpty());
    // First, visit the children to capture all their translation result (in List<SqlNode>)
    // All children are expected to be translated into SqlBasicCall(definition, alias) by visitSubquery
    /** See {@link #visitSubquery(ASTNode, ParseContext) visitSubquery} for details */
    List<SqlNode> sqlNodeList = visitChildren(node, ctx);
    // Second, translate the list of SqlBasicCall to list of SqlWithItem
    List<SqlWithItem> withItemList = new ArrayList<>();
    for (SqlNode sqlNode : sqlNodeList) {
      SqlBasicCall call = (SqlBasicCall) sqlNode;
      SqlNode definition = call.getOperandList().get(0);
      SqlNode alias = call.getOperandList().get(1);
      SqlWithItem withItem = new SqlWithItem(ZERO, (SqlIdentifier) alias, null, definition);
      withItemList.add(withItem);
    }
    // Return a SqlNodeList with the contents of the withItemList
    SqlNodeList result = new SqlNodeList(withItemList, ZERO);
    return result;
  }

  @Override
  protected SqlNode visitWindowSpec(ASTNode node, ParseContext ctx) {
    // See Apache Hive source code: https://github.com/apache/hive/blob/master/ql/src/java/org/apache/hadoop/hive/ql/parse/CalcitePlanner.java#L4339
    // "private RelNode genSelectForWindowing(QB qb, RelNode srcRel, HashSet<ColumnInfo> newColumns)"

    // Example SQL:
    //   ROW_NUMBER() OVER (PARTITION BY x ORDER BY y ROWS BETWEEN CURRENT ROW AND 1 FOLLOWING
    // Hive Antlr ASTNode Tree:
    //TOK_FUNCTION
    //   ROW_NUMBER
    //   TOK_WINDOWSPEC  <-- processed by this node
    //      TOK_PARTITIONINGSPEC
    //         TOK_DISTRIBUTEBY
    //         TOK_ORDERBY
    //      TOK_WINDOWRANGE
    //         CURRENT
    //         FOLLOWING
    //            1

    // Calcite SqlNode Tree:
    // SqlBasicCall(Over):
    // - SqlBasicCall(ROW_NUMBER)
    // - SqlWindow  <-- returned by this node
    // -- partitionList
    // -- orderList
    // -- isRows
    // -- lowerBound
    // -- upperBound
    // -- allowPartial

    // Use a separate context to avoid inadvertently having the "ORDER BY" clause from the window added to the SELECT query.
    ctx = new ParseContext(ctx.hiveTable.orElse(null));

    SqlWindow partitionSpec = (SqlWindow) visitOptionalChildByType(node, ctx, HiveParser.TOK_PARTITIONINGSPEC);
    SqlWindow windowRange = (SqlWindow) visitOptionalChildByType(node, ctx, HiveParser.TOK_WINDOWRANGE);
    SqlWindow windowValues = (SqlWindow) visitOptionalChildByType(node, ctx, HiveParser.TOK_WINDOWVALUES);
    SqlWindow window = windowRange != null ? windowRange : windowValues;

    return new SqlWindow(ZERO, null, null, partitionSpec == null ? SqlNodeList.EMPTY : partitionSpec.getPartitionList(),
        partitionSpec == null ? SqlNodeList.EMPTY : partitionSpec.getOrderList(),
        SqlLiteral.createBoolean(windowRange != null, ZERO), window == null ? null : window.getLowerBound(),
        window == null ? null : window.getUpperBound(), null);
  }

  @Override
  protected SqlNode visitPartitioningSpec(ASTNode node, ParseContext ctx) {
    SqlNode partitionList = visitOptionalChildByType(node, ctx, HiveParser.TOK_DISTRIBUTEBY);
    SqlNode orderList = visitOptionalChildByType(node, ctx, HiveParser.TOK_ORDERBY);
    return new SqlWindow(ZERO, null, null, partitionList != null ? (SqlNodeList) partitionList : SqlNodeList.EMPTY,
        orderList != null ? (SqlNodeList) orderList : SqlNodeList.EMPTY, null, null, null, null);
  }

  @Override
  protected SqlNode visitDistributeBy(ASTNode node, ParseContext ctx) {
    return new SqlNodeList(visitChildren(node, ctx), ZERO);
  }

  @Override
  protected SqlNode visitWindowRange(ASTNode node, ParseContext ctx) {
    // Hive AST:
    //      TOK_WINDOWRANGE  (ROWS ...)
    //         CURRENT
    //         FOLLOWING
    //            1
    List<SqlNode> sqlNodeList = visitChildren(node, ctx);
    SqlNode preceding = sqlNodeList.get(0);
    SqlNode following = (sqlNodeList.size() < 2 ? null : sqlNodeList.get(1));

    return new SqlWindow(ZERO, null, null, SqlNodeList.EMPTY, SqlNodeList.EMPTY, null, preceding, following, null);
  }

  @Override
  protected SqlNode visitWindowValues(ASTNode node, ParseContext ctx) {
    // Hive AST:
    //      TOK_WINDOWVALUES  (VALUES ...)
    //         CURRENT
    //         FOLLOWING
    //            1

    // Reuse the same code of visitWindowRange since the AST structure is exactly the same
    return visitWindowRange(node, ctx);
  }

  @Override
  protected SqlNode visitPreceding(ASTNode node, ParseContext ctx) {
    SqlNode sqlNode = visitChildren(node, ctx).get(0);
    if (sqlNode.getKind() == SqlKind.LITERAL && sqlNode.toString().equalsIgnoreCase("'UNBOUNDED'")) {
      return SqlWindow.createUnboundedPreceding(ZERO);
    } else {
      return SqlWindow.createPreceding(sqlNode, ZERO);
    }
  }

  @Override
  protected SqlNode visitFollowing(ASTNode node, ParseContext ctx) {
    SqlNode sqlNode = visitChildren(node, ctx).get(0);
    if (sqlNode.getKind() == SqlKind.LITERAL && sqlNode.toString().equalsIgnoreCase("'UNBOUNDED'")) {
      return SqlWindow.createUnboundedFollowing(ZERO);
    } else {
      return SqlWindow.createFollowing(sqlNode, ZERO);
    }
  }

  @Override
  protected SqlNode visitCurrentRow(ASTNode node, ParseContext ctx) {
    return SqlWindow.createCurrentRow(ZERO);
  }

  private SqlDataTypeSpec createBasicTypeSpec(SqlTypeName type) {
    final SqlTypeNameSpec typeNameSpec = new SqlBasicTypeNameSpec(type, ZERO);
    return new SqlDataTypeSpec(typeNameSpec, ZERO);
  }

  @Override
  protected SqlNode visitTableTokOrCol(ASTNode node, ParseContext ctx) {
    return visitChildren(node, ctx).get(0);
  }

  private SqlIntervalQualifier fromASTIntervalTypeToSqlIntervalQualifier(ASTNode node) {
    switch (node.getType()) {
      case HiveParser.TOK_INTERVAL_DAY_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.DAY, null, ZERO);
      case HiveParser.TOK_INTERVAL_DAY_TIME_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.DAY, TimeUnit.SECOND, ZERO);
      case HiveParser.TOK_INTERVAL_HOUR_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.HOUR, null, ZERO);
      case HiveParser.TOK_INTERVAL_MINUTE_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.MINUTE, null, ZERO);
      case HiveParser.TOK_INTERVAL_MONTH_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.MONTH, null, ZERO);
      case HiveParser.TOK_INTERVAL_SECOND_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.SECOND, null, ZERO);
      case HiveParser.TOK_INTERVAL_YEAR_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.YEAR, null, ZERO);
      case HiveParser.TOK_INTERVAL_YEAR_MONTH_LITERAL:
        return new SqlIntervalQualifier(TimeUnit.YEAR, TimeUnit.MONTH, ZERO);
    }
    throw new UnhandledASTTokenException(node);
  }

  @Override
  protected SqlNode visitIntervalLiteral(ASTNode node, ParseContext ctx) {
    // Hive Antlr Tree looks like the following:
    //   ASTNode(token.type = TOK_INTERVAL_DAY_LITERAL, token.text="'28'")
    // Calcite SqlNode Tree looks like the following:
    //   SqlIntervalLiteral(1 /* sign */, "28" /* unquotedText */, SqlIntervalQualifier, SqlParserPos)

    SqlIntervalQualifier intervalQualifier = fromASTIntervalTypeToSqlIntervalQualifier(node);

    String text = node.getToken().getText();
    String unquotedText = text.replaceAll("['\"]", "");

    return SqlLiteral.createInterval(1, unquotedText, intervalQualifier, ZERO);
  }

  static class ParseContext {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Table> hiveTable;
    SqlNodeList keywords;
    SqlNode from;
    SqlNodeList selects;
    SqlNode where;
    SqlNodeList grpBy;
    SqlNode having;
    SqlNode fetch;
    SqlNodeList orderBy;

    ParseContext(@Nullable Table hiveTable) {
      this.hiveTable = Optional.ofNullable(hiveTable);
    }

    Optional<Table> getHiveTable() {
      return hiveTable;
    }
  }
}

package com.linkedin.coral.hive.hive2rel.parsetree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.ASTNode;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.HiveParser;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.Node;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.ParseDriver;
import com.linkedin.coral.hive.hive2rel.parsetree.parser.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlPrefixOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import static com.google.common.base.Preconditions.*;
import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * Class to convert Hive Abstract Syntax Tree(AST) represented by {@link ASTNode} to
 * Calcite based AST represented using {@link SqlNode}.
 *
 * Hive AST nodes are poorly structured and do not support polymorphic behavior for processing
 * AST using, for example, visitors. ASTNode carries all the information as type and text fields
 * and children nodes are of base class Node. This requires constant casting of nodes and string
 * processing to get the type and value out of a node. This complicates analysis of the tree.
 *
 * This class converts the AST to Calcite based AST using {@link SqlNode}.This is more structured
 * allowing for simpler analysis code.
 *
 * NOTE:
 * There are, certain difficulties in correct translation.
 * For example, for identifier named {@code s.field1} it's hard to ascertain if {@code s} is a
 * table name or column name of type struct. This is typically resolved by validators using scope but
 * we haven't specialized that part yet.
 */
public class ParseTreeBuilder extends AbstractASTVisitor<SqlNode, ParseTreeBuilder.ParseContext> {

  public SqlNode process(String sql) {
    ParseDriver pd = new ParseDriver();
    try {
      ASTNode root = pd.parse(sql);
      return process(root);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public SqlNode process(ASTNode node) {
    return visit(node, new ParseContext());
  }

  protected SqlNode visitTabAlias(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitLateralView(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitLeftSemiJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitCrossJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitFullOuterJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitRightOuterJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitLeftOuterJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitFalse(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createBoolean(false, ZERO);
  }

  protected SqlNode visitNullToken(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createNull(ZERO);
  }

  protected SqlNode visitLimit(ASTNode node, ParseContext ctx) {
    ctx.fetch = visitChildren(node, ctx).get(0);
    return ctx.fetch;
  }

  protected SqlNode visitUnion(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    return new SqlBasicCall(SqlStdOperatorTable.UNION, sqlNodes.toArray(new SqlNode[0]), ZERO);
  }

  protected SqlNode visitNumber(ASTNode node, ParseContext ctx) {
    String strval = node.getText();
    return SqlLiteral.createExactNumeric(strval, ZERO);
  }

  protected SqlNode visitAllColRef(ASTNode node, ParseContext ctx) {
    return SqlIdentifier.star(ZERO);
  }

  protected SqlNode visitHaving(ASTNode node, ParseContext ctx) {
    checkState(node.getChildren().size() == 1);
    ctx.having = visit((ASTNode) node.getChildren().get(0), ctx);
    return ctx.having;
  }

  protected SqlNode visitWhere(ASTNode node, ParseContext ctx) {
    checkState(node.getChildren().size() == 1);
    ctx.where = visit((ASTNode) node.getChildren().get(0), ctx);
    return ctx.where;
  }

  protected SqlNode visitSortColNameDesc(ASTNode node, ParseContext ctx) {
    return visitSortColName(node, ctx, true);
  }

  protected SqlNode visitSortColNameAsc(ASTNode node, ParseContext ctx) {
    return visitSortColName(node, ctx, false);
  }

  protected SqlNode visitSortColName(ASTNode node, ParseContext ctx, boolean descending) {
    List<SqlNode> children = visitChildren(node, ctx);
    checkState(children.size() == 1);
    if (!descending) {
      return children.get(0);
    }
    return new SqlBasicCall(SqlStdOperatorTable.DESC, new SqlNode[] {children.get(0)}, ZERO);
  }

  protected SqlNode visitOrderBy(ASTNode node, ParseContext ctx) {
    List<SqlNode> orderByCols = visitChildren(node, ctx);
    ctx.orderBy = new SqlNodeList(orderByCols, ZERO);
    return ctx.orderBy;
  }

  protected SqlNode visitGroupBy(ASTNode node, ParseContext ctx) {
    List<SqlNode> grpCols = visitChildren(node, ctx);
    ctx.grpBy = new SqlNodeList(grpCols, ZERO);
    return ctx.grpBy;
  }

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

  protected SqlNode visitUnaryOperator(ASTNode node, ParseContext ctx) {
    SqlNode operand = visit((ASTNode) node.getChildren().get(0), ctx);
    List<SqlOperator> operators = SqlStdOperatorTable.instance()
        .getOperatorList()
        .stream()
        .filter(o -> o.getName().toUpperCase().equals(node.getText()) && o instanceof SqlPrefixOperator)
        .collect(Collectors.toList());
    checkState(operators.size() == 1, "%s operator %s, tree: %s",
        operators.isEmpty() ? "Unknown" : "Ambiguous", node.getText(), node.dump());
    return operators.get(0).createCall(ZERO, operand);
  }

  protected SqlNode visitBinaryOperator(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2);
    List<SqlOperator> operators = SqlStdOperatorTable.instance()
        .getOperatorList()
        .stream()
        .filter(o -> o.getName().toUpperCase().equals(node.getText()) && o instanceof SqlBinaryOperator)
        .collect(Collectors.toList());
    checkState(operators.size() == 1, "%s operator %s, tree: %s",
        operators.isEmpty() ? "Unknown" : "Ambiguous", node.getText(), node.dump());
    return operators.get(0).createCall(ZERO, sqlNodes);
  }

  protected SqlNode visitDotOperator(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes != null && sqlNodes.size() == 2);
    SqlIdentifier left = (SqlIdentifier) sqlNodes.get(0);
    SqlIdentifier right = (SqlIdentifier) sqlNodes.get(1);
    Iterable<String> names = Iterables.concat(left.names, right.names);
    return new SqlIdentifier(ImmutableList.copyOf(names), ZERO);
  }

  protected SqlNode visitLParen(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2);
    return new SqlBasicCall(SqlStdOperatorTable.ITEM,
        new SqlNode[]{sqlNodes.get(0), sqlNodes.get(1)}, ZERO);
  }

  protected SqlNode visitFunctionStar(ASTNode node, ParseContext ctx) {
    ASTNode functionNode = (ASTNode) node.getChildren().get(0);
    List<SqlOperator> functions = SqlStdOperatorTable.instance()
        .getOperatorList()
        .stream()
        .filter(f -> functionNode.getText().equalsIgnoreCase(f.getName()))
        .collect(Collectors.toList());
    checkState(functions.size() == 1);
    return new SqlBasicCall(functions.get(0), new SqlNode[] {new SqlIdentifier("", ZERO)}, ZERO);
  }

  protected SqlNode visitFunctionDistinct(ASTNode node, ParseContext ctx) {
    return visitFunctionInternal(node, ctx, SqlLiteral.createCharString("DISTINCT", ZERO));
  }

  protected SqlNode visitFunction(ASTNode node, ParseContext ctx) {
    return visitFunctionInternal(node, ctx, null);
  }

  protected SqlNode visitFunctionInternal(ASTNode node, ParseContext ctx, SqlLiteral quantifier) {
    ArrayList<Node> children = node.getChildren();
    checkState(children.size() > 0);
    ASTNode functionNode = (ASTNode) children.get(0);

    SqlOperator operator = null;
    List<Node> operands = children.subList(1, children.size());

    if (functionNode.getType() == HiveParser.TOK_ISNULL) {
      operator = SqlStdOperatorTable.IS_NULL;

    } else if (functionNode.getType() == HiveParser.TOK_ISNOTNULL) {
      operator = SqlStdOperatorTable.IS_NOT_NULL;

    } else if (functionNode.getText().equalsIgnoreCase("between")) {
      if (((ASTNode) children.get(1)).getType() == HiveParser.KW_TRUE) {
        operator = SqlStdOperatorTable.NOT_BETWEEN;
      } else {
        operator = SqlStdOperatorTable.BETWEEN;
      }
      operands = children.subList(2, children.size());

    } else if (functionNode.getText().equalsIgnoreCase("in")) {
      operator = SqlStdOperatorTable.IN;
    } else if (functionNode.getText().equalsIgnoreCase("sum")) {
      operator = SqlStdOperatorTable.SUM;
    } else if (functionNode.getText().equalsIgnoreCase("count")) {
      operator = SqlStdOperatorTable.COUNT;
    } else if (functionNode.getText().equalsIgnoreCase("max")) {
      operator = SqlStdOperatorTable.MAX;
    } else if (functionNode.getText().equalsIgnoreCase("min")) {
      operator = SqlStdOperatorTable.MIN;
    } else if (functionNode.getText().equalsIgnoreCase("avg")) {
      operator = SqlStdOperatorTable.AVG;
    } /*else if (isCastFunction(functionNode)) {
      visitCastFunction(node, queryBuilder);
    } else if (functionNode.getText().equalsIgnoreCase("when")) {
      visitCaseFunction(node, queryBuilder);
    } else {
      queryBuilder.currentBuilder.append(functionNode.getText()).append("(");
      if (children.size() > 1) {
        visit((ASTNode) children.get(1), queryBuilder);
      }

      for (int i = 2; i < children.size(); i++) {
        queryBuilder.currentBuilder.append(", ");
        visit((ASTNode) children.get(i), queryBuilder);
      }
      queryBuilder.currentBuilder.append(")");
    }*/
    List<SqlNode> sqlNodes = visitChildren(operands, ctx);
    return new SqlBasicCall(operator, sqlNodes.toArray(new SqlNode[0]), ZERO);
  }

  protected SqlNode visitSelectExpr(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    if (sqlNodes.size() == 1) {
      return sqlNodes.get(0);
    } else if (sqlNodes.size() == 2) {
      return new SqlBasicCall(SqlStdOperatorTable.AS, sqlNodes.toArray(new SqlNode[0]), ZERO);
    } else {
      throw new UnhandledASTTokenException(node);
    }
  }

  protected SqlNode visitSelectDistinct(ASTNode node, ParseContext ctx) {
    ctx.keywords = new SqlNodeList(ImmutableList.of(SqlLiteral.createCharString("DISTINCT", ZERO)), ZERO);
    return visitSelects(node, ctx);
  }

  protected SqlNode visitSelects(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    ctx.selects = new SqlNodeList(sqlNodes, ZERO);
    return ctx.selects;
  }

  protected SqlNode visitTabRefNode(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes != null && !sqlNodes.isEmpty());
    if (sqlNodes.size() == 1) {
      return sqlNodes.get(0);
    }
    if (sqlNodes.size() == 2) {
      return new SqlBasicCall(SqlStdOperatorTable.AS, ((SqlNode[]) sqlNodes.toArray()), ZERO);
    }
    throw new UnhandledASTTokenException(node);
  }

  protected SqlNode visitTabnameNode(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    List<String> names = sqlNodes.stream()
        .map(s -> ((SqlIdentifier) s).names)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    // TODO: these should be configured in or transformed through
    // a set of rules
    if (names.size() == 1) {
      names.add(0, "hive");
      names.add(1, "default");
    } else if (names.size() == 2) {
      names.add(0, "hive");
    }

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

  protected SqlNode visitSubquery(ASTNode node, ParseContext ctx) {
    ParseContext subQueryContext = new ParseContext();
    List<SqlNode> sqlNodes = visitChildren(node, subQueryContext);
    if (sqlNodes.size() == 1) {
      return sqlNodes.get(0);
    } else if (sqlNodes.size() == 2) {
      return new SqlBasicCall(SqlStdOperatorTable.AS, sqlNodes.toArray(new SqlNode[0]), ZERO);
    }
    throw new UnhandledASTTokenException(node);
  }

  protected SqlNode visitFrom(ASTNode node, ParseContext ctx) {
    List<SqlNode> children = visitChildren(node, ctx);
    if (children.size() == 1) {
      ctx.from = children.get(0);
      return children.get(0);
    }
    // TODO: handle join
    throw new UnsupportedASTException(node.dump());
  }

  protected  SqlNode visitIdentifier(ASTNode node, ParseContext ctx) {
    return new SqlIdentifier(node.getText(), ZERO);
  }

  protected SqlNode visitStringLiteral(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createCharString(node.getText(), ZERO);
  }

  protected SqlNode visitQueryNode(ASTNode node, ParseContext ctx) {
    ArrayList<Node> children = node.getChildren();
    checkState(children != null && !children.isEmpty());
    ParseContext qc = new ParseContext();
    List<SqlNode> sqlNodes = visitChildren(node, qc);
    return new SqlSelect(ZERO, qc.keywords, qc.selects, qc.from, qc.where, qc.grpBy,
        qc.having, null, qc.orderBy, null, ctx.fetch);
  }

  protected SqlNode visitNil(ASTNode node, ParseContext ctx) {
    return visitChildren(node, ctx).get(0);
  }

  protected SqlNode visitBoolean(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s %s %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitInt(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitSmallInt(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitBigInt(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitTinyInt(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitFloat(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitDouble(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitVarchar(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitChar(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitString(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitDecimal(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitDate(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  protected SqlNode visitTimestamp(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitTableTokOrCol(ASTNode node, ParseContext ctx) {
    return visitChildren(node, ctx).get(0);
  }

  public static class ParseContext {
    SqlNodeList keywords;
    SqlNode from;
    SqlNodeList selects;
    SqlNode where;
    SqlNodeList grpBy;
    SqlNode having;
    SqlNode fetch;
    SqlNodeList orderBy;
  }
}

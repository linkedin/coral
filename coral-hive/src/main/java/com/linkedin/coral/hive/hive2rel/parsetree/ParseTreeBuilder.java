package com.linkedin.coral.hive.hive2rel.parsetree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunction;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
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
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlPrefixOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;

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

  private final Config config;
  private final HiveFunctionResolver functionResolver;

  public ParseTreeBuilder(Config config) {
    Preconditions.checkState(config.catalogName.isEmpty() || !config.defaultDBName.isEmpty(),
        "Default DB is required if catalog name is not empty");
    this.config = config;
    this.functionResolver = new HiveFunctionResolver(new StaticHiveFunctionRegistry());
  }

  public ParseTreeBuilder() {
    this(new Config());
  }

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

  @Override
  protected SqlNode visitTabAlias(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitLateralView(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
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
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitRightOuterJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
  }

  @Override
  protected SqlNode visitLeftOuterJoin(ASTNode node, ParseContext ctx) {
    throw new RuntimeException(String.format("%s, %s, %s", node.getType(), node.getText(), node.dump()));
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
    return new SqlBasicCall(SqlStdOperatorTable.UNION, sqlNodes.toArray(new SqlNode[0]), ZERO);
  }

  @Override
  protected SqlNode visitNumber(ASTNode node, ParseContext ctx) {
    String strval = node.getText();
    return SqlLiteral.createExactNumeric(strval, ZERO);
  }

  @Override
  protected SqlNode visitAllColRef(ASTNode node, ParseContext ctx) {
    return SqlIdentifier.star(ZERO);
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
    return new SqlBasicCall(SqlStdOperatorTable.DESC, new SqlNode[]{children.get(0)}, ZERO);
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
    List<SqlOperator> operators = SqlStdOperatorTable.instance()
        .getOperatorList()
        .stream()
        .filter(o -> o.getName().toUpperCase().equals(node.getText().toUpperCase()) && o instanceof SqlPrefixOperator)
        .collect(Collectors.toList());
    checkState(operators.size() == 1, "%s operator %s, tree: %s", operators.isEmpty() ? "Unknown" : "Ambiguous",
        node.getText(), node.dump());
    return operators.get(0).createCall(ZERO, operand);
  }

  private SqlNode visitBinaryOperator(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2);
    List<SqlOperator> operators = SqlStdOperatorTable.instance()
        .getOperatorList()
        .stream()
        .filter(o -> o.getName().toUpperCase().equals(node.getText().toUpperCase()) && o instanceof SqlBinaryOperator)
        .collect(Collectors.toList());
    checkState(operators.size() == 1, "%s operator %s, tree: %s", operators.isEmpty() ? "Unknown" : "Ambiguous",
        node.getText(), node.dump());
    return operators.get(0).createCall(ZERO, sqlNodes);
  }

  @Override
  protected SqlNode visitDotOperator(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes != null && sqlNodes.size() == 2);
    SqlIdentifier left = (SqlIdentifier) sqlNodes.get(0);
    SqlIdentifier right = (SqlIdentifier) sqlNodes.get(1);
    Iterable<String> names = Iterables.concat(left.names, right.names);
    return new SqlIdentifier(ImmutableList.copyOf(names), ZERO);
  }

  @Override
  protected SqlNode visitLParen(ASTNode node, ParseContext ctx) {
    List<SqlNode> sqlNodes = visitChildren(node, ctx);
    checkState(sqlNodes.size() == 2);
    return new SqlBasicCall(SqlStdOperatorTable.ITEM, new SqlNode[]{sqlNodes.get(0), sqlNodes.get(1)}, ZERO);
  }

  @Override
  protected SqlNode visitFunctionStar(ASTNode node, ParseContext ctx) {
    ASTNode functionNode = (ASTNode) node.getChildren().get(0);
    List<SqlOperator> functions = SqlStdOperatorTable.instance()
        .getOperatorList()
        .stream()
        .filter(f -> functionNode.getText().equalsIgnoreCase(f.getName()))
        .collect(Collectors.toList());
    checkState(functions.size() == 1);
    return new SqlBasicCall(functions.get(0), new SqlNode[]{new SqlIdentifier("", ZERO)}, ZERO);
  }

  @Override
  protected SqlNode visitFunctionDistinct(ASTNode node, ParseContext ctx) {
    return visitFunctionInternal(node, ctx, SqlLiteral.createCharString("DISTINCT", ZERO));
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
    // List<Node> operands = children.subList(1, children.size());
    List<SqlNode> sqlOperands = visitChildren(children, ctx);
    HiveFunction hiveFunction = functionResolver.tryResolve(functionName, ((SqlIdentifier) ctx.from));
    return hiveFunction.createCall(sqlOperands.get(0), sqlOperands.subList(1, sqlOperands.size()));
  }

  private boolean isCastFunction(ASTNode node) {
    int name = node.getType();
    return (name == HiveParser.TOK_BOOLEAN || name == HiveParser.TOK_INT || name == HiveParser.TOK_STRING
        || name == HiveParser.TOK_DOUBLE || name == HiveParser.TOK_FLOAT || name == HiveParser.TOK_BIGINT
        || name == HiveParser.TOK_TINYINT || name == HiveParser.TOK_SMALLINT || name == HiveParser.TOK_CHAR
        || name == HiveParser.TOK_DECIMAL || name == HiveParser.TOK_VARCHAR || name == HiveParser.TOK_BINARY
        || name == HiveParser.TOK_DATE || name == HiveParser.TOK_TIMESTAMP);
  }

  @Override
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

  @Override
  protected SqlNode visitSelectDistinct(ASTNode node, ParseContext ctx) {
    ctx.keywords = new SqlNodeList(ImmutableList.of(SqlLiteral.createCharString("DISTINCT", ZERO)), ZERO);
    return visitSelects(node, ctx);
  }

  @Override
  protected SqlNode visitSelects(ASTNode node, ParseContext ctx) {
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
    // TODO: these should be configured in or transformed through
    // a set of rules
    if (names.size() == 1) {
      if (!config.defaultDBName.isEmpty()) {
        names.add(0, config.defaultDBName);
      }
      if (!config.catalogName.isEmpty()) {
        names.add(0, config.catalogName);
      }
    } else if (names.size() == 2) {
      if (!config.catalogName.isEmpty()) {
        names.add(0, config.catalogName);
      }
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
    ParseContext subQueryContext = new ParseContext();
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
    Preconditions.checkState(text.length() >= 2);
    return SqlLiteral.createCharString(text.substring(1, text.length() - 1), ZERO);
  }

  @Override
  protected SqlNode visitQueryNode(ASTNode node, ParseContext ctx) {
    ArrayList<Node> children = node.getChildren();
    checkState(children != null && !children.isEmpty());
    ParseContext qc = new ParseContext();
    List<SqlNode> sqlNodes = visitChildren(node, qc);
    return new SqlSelect(ZERO, qc.keywords, qc.selects, qc.from, qc.where, qc.grpBy, qc.having, null, qc.orderBy, null,
        ctx.fetch);
  }

  protected SqlNode visitNil(ASTNode node, ParseContext ctx) {
    return visitChildren(node, ctx).get(0);
  }

  @Override
  protected SqlNode visitBoolean(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.BOOLEAN.getName());
  }

  @Override
  protected SqlNode visitInt(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.INTEGER.getName());
  }

  @Override
  protected SqlNode visitSmallInt(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.SMALLINT.getName());
  }

  @Override
  protected SqlNode visitBigInt(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.BIGINT.getName());
  }

  @Override
  protected SqlNode visitTinyInt(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.TINYINT.getName());
  }

  @Override
  protected SqlNode visitFloat(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.FLOAT.getName());
  }

  @Override
  protected SqlNode visitDouble(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.DOUBLE.getName());
  }

  @Override
  protected SqlNode visitVarchar(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.VARCHAR.getName());
  }

  @Override
  protected SqlNode visitChar(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.CHAR.getName());
  }

  @Override
  protected SqlNode visitString(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.VARCHAR.getName());
  }

  @Override
  protected SqlNode visitBinary(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.VARBINARY.getName());
  }

  @Override
  protected SqlNode visitDecimal(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.DECIMAL.getName());
  }

  @Override
  protected SqlNode visitDate(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.DATE.getName());
  }

  @Override
  protected SqlNode visitTimestamp(ASTNode node, ParseContext ctx) {
    return createTypeSpec(SqlTypeName.TIMESTAMP.getName());
  }

  @Override
  protected SqlNode visitIsNull(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createCharString("is null", ZERO);
  }

  @Override
  protected SqlNode visitIsNotNull(ASTNode node, ParseContext ctx) {
    return SqlLiteral.createCharString("is not null", ZERO);
  }

  private SqlDataTypeSpec createTypeSpec(String type) {
    return new SqlDataTypeSpec(new SqlIdentifier(type, ZERO), -1, -1, null, null, ZERO);
  }

  @Override
  protected SqlNode visitTableTokOrCol(ASTNode node, ParseContext ctx) {
    return visitChildren(node, ctx).get(0);
  }

  public static class Config {
    private String catalogName = "";
    private String defaultDBName = "";

    public Config setCatalogName(String catalogName) {
      this.catalogName = catalogName;
      return this;
    }

    public Config setDefaultDB(String defaultDBName) {
      this.defaultDBName = defaultDBName;
      return this;
    }
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

package com.linkedin.coral.presto.rel2presto;

import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.rel2sql.SqlImplementor;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.tools.FrameworkConfig;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.linkedin.coral.presto.rel2presto.TestTable.*;
import static org.testng.Assert.*;


public class UDFTransformerTest {
  static FrameworkConfig config;
  static final SqlOperator targetUDF = UDFMapUtils.createUDF("targetFunc", ReturnTypes.DOUBLE);
  static final SqlDialect sqlDialect = new SqlDialect(SqlDialect.DatabaseProduct.CALCITE,
      "Calcite", "", NullCollation.HIGH);

  @BeforeTest
  public static void beforeTest() {
    config = TestUtils.createFrameworkConfig(TABLE_ONE);
  }

  private SqlNode applyTransformation(String operandTransformer, String resultTransformer) {
    UDFTransformer udfTransformer = UDFTransformer.of(targetUDF, operandTransformer, resultTransformer);
    Project project = (Project) TestUtils.toRel("select scol, icol, dcol from " + TABLE_ONE.getTableName(), config);
    RexBuilder rexBuilder = project.getCluster().getRexBuilder();
    List<RexNode> sourceOperands = new ArrayList<>();
    for (int i = 0; i < project.getRowType().getFieldCount(); i++) {
      sourceOperands.add(rexBuilder.makeInputRef(project, i));
    }
    RelToSqlConverter sqlConverter = new RelToSqlConverter(sqlDialect);
    SqlImplementor.Result result = sqlConverter.visit(project);
    RexNode rexNode = udfTransformer.transformCall(rexBuilder, sourceOperands);
    return result.builder(project, SqlImplementor.Clause.SELECT).context.toSql(null, rexNode);
  }

  private void testTransformation(String operandTransformer, String resultTransformer, String expected) {
    SqlNode result = applyTransformation(operandTransformer, resultTransformer);
    assertEquals(result.toSqlString(sqlDialect).getSql(), expected);
  }

  private void testFailedTransformation(String operandTransformer, String resultTransformer, Class exceptionClass) {
    try {
      applyTransformation(operandTransformer, resultTransformer);
      fail();
    } catch (Exception e) {
      assertTrue(exceptionClass.isInstance(e));
    }
  }

  @Test
  public void testWrongOperandSyntax() {
    testFailedTransformation("", null, IllegalStateException.class);
    testFailedTransformation("{}", null, IllegalStateException.class);
    testFailedTransformation("[{input}]", null, JsonSyntaxException.class);
  }

  @Test
  public void testWrongResultSyntax() {
    testFailedTransformation(null, "", IllegalStateException.class);
    testFailedTransformation(null, "[]", IllegalStateException.class);
    testFailedTransformation(null, "{", JsonSyntaxException.class);
  }

  @Test
  public void testInvalidInput() {
    testFailedTransformation("[{\"input\":0}]", null, IllegalArgumentException.class);
    testFailedTransformation("[{\"input\":4}]", null, IllegalArgumentException.class);
    testFailedTransformation("[{\"input\":-1}]", null, IllegalArgumentException.class);
  }

  @Test
  public void testInvalidJsonNode() {
    testFailedTransformation("[{\"dummy\":0}]", null, IllegalArgumentException.class);
  }

  @Test
  public void testLiteral() {
    testTransformation("[{\"value\":'astring'}]", null, "targetFunc('astring')");
    testTransformation("[{\"value\":true}]", null, "targetFunc(TRUE)");
    testTransformation("[{\"value\":5}]", null, "targetFunc(5)");
    testTransformation("[{\"value\":2.5}]", null, "targetFunc(2.5)");
    testFailedTransformation("[{\"value\":[1, 2]}]", null, IllegalArgumentException.class);
  }

  @Test
  public void testResizeTransformation() {
    testTransformation("[{\"input\":1}, {\"input\":3}]", null, "targetFunc(scol, dcol)");
    testTransformation("[]", null, "targetFunc()");
    testTransformation("[{\"input\":2}, {\"input\":1}, {\"input\":2}, {\"input\":3}]", null,
        "targetFunc(icol, scol, icol, dcol)");
  }

  @Test
  public void testIdentityTransformation() {
    testTransformation(null, null, "targetFunc(scol, icol, dcol)");
    testTransformation("[{\"input\":1}, {\"input\":2}, {\"input\":3}]", "{\"input\":0}", "targetFunc(scol, icol, dcol)");
  }

  @Test
  public void testNormalTransformation() {
    testTransformation("[{\"op\":\"*\",\"operands\":[{\"input\":2},{\"input\":3}]}, {\"input\":1}]", null,
        "targetFunc(icol * dcol, scol)");
    testTransformation("[{\"op\":\"*\",\"operands\":[{\"input\":2},{\"input\":3}]}, {\"input\":1}]",
        "{\"op\":\"+\",\"operands\":[{\"input\":0},{\"input\":3}]}",
        "targetFunc(icol * dcol, scol) + dcol");

    testFailedTransformation("[{\"op\":\"%\",\"operands\":[{\"input\":2},{\"input\":3}]}, {\"input\":1}]", null, UnsupportedOperationException.class);
  }

}

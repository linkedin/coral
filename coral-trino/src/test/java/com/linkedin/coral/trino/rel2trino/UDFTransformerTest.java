/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.rel2sql.SqlImplementor;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.tools.FrameworkConfig;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.linkedin.coral.trino.rel2trino.TestTable.*;
import static org.testng.Assert.*;


public class UDFTransformerTest {
  static FrameworkConfig tableOneConfig;
  static String tableOneQuery;
  static FrameworkConfig tableThreeConfig;
  static String tableThreeQuery;
  static final SqlOperator targetUDF = UDFMapUtils.createUDF("targetFunc", ReturnTypes.DOUBLE);
  static final SqlOperator defaultUDF = UDFMapUtils.createUDF("", ReturnTypes.DOUBLE);
  static final SqlDialect sqlDialect =
      new SqlDialect(SqlDialect.DatabaseProduct.CALCITE, "Calcite", "", NullCollation.HIGH);

  @BeforeTest
  public static void beforeTest() {
    tableOneConfig = TestUtils.createFrameworkConfig(TABLE_ONE);
    tableThreeConfig = TestUtils.createFrameworkConfig(TABLE_THREE);
    tableOneQuery = "select scol, icol, dcol from " + TABLE_ONE.getTableName();
    tableThreeQuery = "select binaryfield, 'literal' from " + TABLE_THREE.getTableName();
  }

  private SqlNode applyTransformation(Project project, SqlOperator operator, String operandTransformer,
      String resultTransformer, String operatorTransformer) {
    UDFTransformer udfTransformer =
        UDFTransformer.of("", operator, operandTransformer, resultTransformer, operatorTransformer);
    RexBuilder rexBuilder = project.getCluster().getRexBuilder();
    List<RexNode> sourceOperands = new ArrayList<>();
    List<RexNode> projectOperands = project.getChildExps();
    for (int i = 0; i < projectOperands.size(); ++i) {
      // If the parameter is a reference to a column, we make it a RexInputRef.
      // We make a new reference because the RexBuilder refers to a column based on the output of the projection.
      // We cannot pass the the source operands from the Project operator directly because they are references to the
      // columns of the table.
      // We need to create a new input reference to each projection output because it is the new input to the UDF.
      // Unlike input references, other primitives such as RexLiteral can be added as a sourceOperand directly since
      // they are not normally projected as outputs and are not usually input references.
      if (projectOperands.get(i) instanceof RexInputRef) {
        sourceOperands.add(rexBuilder.makeInputRef(project, i));
      } else {
        sourceOperands.add(projectOperands.get(i));
      }
    }
    RelToSqlConverter sqlConverter = new RelToSqlConverter(sqlDialect);
    SqlImplementor.Result result = sqlConverter.visit(project);
    RexNode rexNode = udfTransformer.transformCall(rexBuilder, sourceOperands);
    return result.builder(project, SqlImplementor.Clause.SELECT).context.toSql(null, rexNode);
  }

  private void testTransformation(String query, FrameworkConfig config, SqlOperator operator, String operandTransformer,
      String resultTransformer, String operatorTransformer, String expected) {
    Project project = (Project) TestUtils.toRel(query, config);
    SqlNode result = applyTransformation(project, operator, operandTransformer, resultTransformer, operatorTransformer);
    assertEquals(result.toSqlString(sqlDialect).getSql(), expected);
  }

  private void testFailedTransformation(String query, FrameworkConfig config, SqlOperator operator,
      String operandTransformer, String resultTransformer, String operatorTransformer, Class exceptionClass) {
    try {
      Project project = (Project) TestUtils.toRel(query, config);
      applyTransformation(project, operator, operandTransformer, resultTransformer, operatorTransformer);
      fail();
    } catch (Exception e) {
      assertTrue(exceptionClass.isInstance(e));
    }
  }

  @Test
  public void testWrongOperandSyntax() {
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "", null, null, IllegalStateException.class);
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "{}", null, null, IllegalStateException.class);
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{input}]", null, null,
        JsonSyntaxException.class);
  }

  @Test
  public void testWrongResultSyntax() {
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, null, "", null, IllegalStateException.class);
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, null, "[]", null, IllegalStateException.class);
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, null, "{", null, JsonSyntaxException.class);
  }

  @Test
  public void testInvalidInput() {
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"input\":0}]", null, null,
        IllegalArgumentException.class);
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"input\":4}]", null, null,
        IllegalArgumentException.class);
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"input\":-1}]", null, null,
        IllegalArgumentException.class);
  }

  @Test
  public void testInvalidJsonNode() {
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"dummy\":0}]", null, null,
        IllegalArgumentException.class);
  }

  @Test
  public void testLiteral() {
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"value\":'astring'}]", null, null,
        "targetFunc('astring')");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"value\":true}]", null, null, "targetFunc(TRUE)");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"value\":5}]", null, null, "targetFunc(5)");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"value\":2.5}]", null, null, "targetFunc(2.5)");
    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"value\":[1, 2]}]", null, null,
        IllegalArgumentException.class);
  }

  @Test
  public void testResizeTransformation() {
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"input\":1}, {\"input\":3}]", null, null,
        "targetFunc(scol, dcol)");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[]", null, null, "targetFunc()");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF,
        "[{\"input\":2}, {\"input\":1}, {\"input\":2}, {\"input\":3}]", null, null,
        "targetFunc(icol, scol, icol, dcol)");
  }

  @Test
  public void testIdentityTransformation() {
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, null, null, null, "targetFunc(scol, icol, dcol)");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF, "[{\"input\":1}, {\"input\":2}, {\"input\":3}]",
        "{\"input\":0}", null, "targetFunc(scol, icol, dcol)");
  }

  @Test
  public void testNormalTransformation() {
    testTransformation(tableOneQuery, tableOneConfig, targetUDF,
        "[{\"op\":\"*\",\"operands\":[{\"input\":2},{\"input\":3}]}, {\"input\":1}]", null, null,
        "targetFunc(icol * dcol, scol)");
    testTransformation(tableOneQuery, tableOneConfig, targetUDF,
        "[{\"op\":\"*\",\"operands\":[{\"input\":2},{\"input\":3}]}, {\"input\":1}]",
        "{\"op\":\"+\",\"operands\":[{\"input\":0},{\"input\":3}]}", null, "targetFunc(icol * dcol, scol) + dcol");

    testFailedTransformation(tableOneQuery, tableOneConfig, targetUDF,
        "[{\"op\":\"@\",\"operands\":[{\"input\":2},{\"input\":3}]}, {\"input\":1}]", null, null,
        UnsupportedOperationException.class);
  }

  @Test
  public void testInputOperatorTransformer() {
    // Verifies that an operator transformer that has an exact match uses its target function.
    testTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"'literal'\", \"input\":2, \"name\":\"newFunc\"}]", "newFunc(binaryfield, 'literal')");

    // Verifies that an operator transformer that has a substring match uses its target function.
    testTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(liter).*$\", \"input\":2, \"name\":\"newFunc\"}]", "newFunc(binaryfield, 'literal')");

    // Verifies that an operator transformer that has no match uses the default trivial function and throws error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, defaultUDF, null, null,
        "[{\"regex\":\"^.*(?i)(noMatch).*$\", \"input\":2, \"name\":\"newFunc\"}]", IllegalArgumentException.class);
  }

  @Test
  public void testMultipleOperatorTransformers() {
    // Verifies that all operator transformers in the operator transformers array are tested.
    testTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(noMatch).*$\", \"input\":2, \"name\":\"unmatchFunc\"},"
            + "{\"regex\":\"^.*(?i)(literal).*$\", \"input\":2, \"name\":\"matchFunc\"}]",
        "matchFunc(binaryfield, 'literal')");

    // Verifies that the first target function matcher to match is selected has its target function selected.
    testTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(literal).*$\", \"input\":2, \"name\":\"firstFunc\"},"
            + "{\"regex\":\"^.*(?i)(literal).*$\", \"input\":2, \"name\":\"secondFunc\"}]",
        "firstFunc(binaryfield, 'literal')");
  }

  @Test
  public void testNoMatchOperatorTransformer() {
    // Verifies that a target function with no default target UDF throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, null, null, null,
        "[{\"regex\":\"^.*(?i)(noMatch).*$\", \"input\":2, \"name\":\"newFunc\"}]", IllegalArgumentException.class);
  }

  @Test
  public void testInvalidArgumentsOperatorTransformer() {
    // Verifies that an empty function name throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(literal).*$\", \"input\":2, \"name\":\"\"}]", IllegalArgumentException.class);

    // Verifies that an input parameter position <= 0 throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(literal).*$\", \"input\":0, \"name\":\"newFunc\"}]", IllegalArgumentException.class);

    // Verifies that an input parameter position > the input size throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(literal).*$\", \"input\":3, \"name\":\"newFunc\"}]", IllegalArgumentException.class);
  }

  @Test
  public void testSufficientArgumentsOperatorTransformer() {
    // Verifies that an operator transformer that does not define a matcher throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"input\":2, \"name\":\"newFunc\"}]", IllegalArgumentException.class);

    // Verifies that an operator transformer that does not define a parameter position throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(literal).*$\", \"name\":\"newFunc\"}]", IllegalArgumentException.class);

    // Verifies that an operator transformer that does not define a replacement function name throws an error.
    testFailedTransformation(tableThreeQuery, tableThreeConfig, targetUDF, null, null,
        "[{\"regex\":\"^.*(?i)(literal).*$\", \"input\":2}]", IllegalArgumentException.class);
  }

  @Test
  public void testOperandTransformerAndOperatorTransformer() {
    // Verifies that an operator transformer used in conjunction with an operand transformer works correctly.
    testTransformation(tableThreeQuery, tableThreeConfig, targetUDF, "[{\"input\":1}]", null,
        "[{\"regex\":\"'literal'\", \"input\":2, \"name\":\"newFunc\"}]", "newFunc(binaryfield)");
    testTransformation(tableThreeQuery, tableThreeConfig, targetUDF, "[{\"input\":2}]", null,
        "[{\"regex\":\"'literal'\", \"input\":2, \"name\":\"newFunc\"}]", "newFunc('literal')");
  }

}

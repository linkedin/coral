/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.linkedin.coral.datagen.domain.transformer.ConcatRegexTransformer;

import static org.testng.Assert.*;


/**
 * Tests for regex domain transformers (Concat). Trim is covered end-to-end via
 * the SQL-driven integration tests in {@link RegexDomainInferenceProgramTest}, since
 * constructing a Calcite TRIM RexCall requires a SqlTrimFunction.Flag operand that is
 * awkward to build without a parser. Lower/Upper/Substring are also covered there.
 */
public class RegexTransformerTest {

  private RexBuilder rexBuilder;
  private RelDataTypeFactory typeFactory;

  @BeforeMethod
  public void setup() {
    rexBuilder = TestHelper.createRexBuilder();
    typeFactory = rexBuilder.getTypeFactory();
  }

  private RexNode stringRef() {
    return rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
  }

  private RexNode stringLit(String value) {
    return rexBuilder.makeLiteral(value);
  }

  // ==================== Concat Transformer ====================

  @Test
  public void testConcatVariablePlusLiteralSuffix() {
    // CONCAT(x, 'World') = 'HelloWorld' => x = 'Hello'
    ConcatRegexTransformer transformer = new ConcatRegexTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.CONCAT, stringRef(), stringLit("World"));

    assertTrue(transformer.canHandle(expr));
    assertTrue(transformer.isVariableOperandPositionValid(expr));
    assertEquals(transformer.getChildForVariable(expr), ((RexCall) expr).getOperands().get(0));

    Domain<?, ?> result = transformer.refineInputDomain(expr, RegexDomain.literal("HelloWorld"));
    assertTrue(result instanceof RegexDomain);
    RegexDomain regexResult = (RegexDomain) result;
    assertTrue(regexResult.isLiteral());
    assertEquals(regexResult.getLiteralValue(), "Hello");
  }

  @Test
  public void testConcatLiteralPrefixPlusVariable() {
    // CONCAT('Hello', x) = 'HelloWorld' => x = 'World'
    ConcatRegexTransformer transformer = new ConcatRegexTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.CONCAT, stringLit("Hello"), stringRef());

    assertTrue(transformer.canHandle(expr));
    assertTrue(transformer.isVariableOperandPositionValid(expr));

    Domain<?, ?> result = transformer.refineInputDomain(expr, RegexDomain.literal("HelloWorld"));
    assertTrue(result instanceof RegexDomain);
    RegexDomain regexResult = (RegexDomain) result;
    assertTrue(regexResult.isLiteral());
    assertEquals(regexResult.getLiteralValue(), "World");
  }

  @Test
  public void testConcatSuffixMismatchProducesEmpty() {
    // CONCAT(x, 'World') = 'HelloEarth' => no valid input (suffix mismatch)
    ConcatRegexTransformer transformer = new ConcatRegexTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.CONCAT, stringRef(), stringLit("World"));

    Domain<?, ?> result = transformer.refineInputDomain(expr, RegexDomain.literal("HelloEarth"));
    assertTrue(result instanceof RegexDomain);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testConcatPrefixMismatchProducesEmpty() {
    // CONCAT('Hello', x) = 'GreetingsWorld' => no valid input (prefix mismatch)
    ConcatRegexTransformer transformer = new ConcatRegexTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.CONCAT, stringLit("Hello"), stringRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, RegexDomain.literal("GreetingsWorld"));
    assertTrue(result instanceof RegexDomain);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testConcatEmptySuffixIsIdentity() {
    // CONCAT(x, '') = 'Hello' => x = 'Hello'
    ConcatRegexTransformer transformer = new ConcatRegexTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.CONCAT, stringRef(), stringLit(""));

    Domain<?, ?> result = transformer.refineInputDomain(expr, RegexDomain.literal("Hello"));
    assertTrue(result instanceof RegexDomain);
    RegexDomain regexResult = (RegexDomain) result;
    assertTrue(regexResult.isLiteral());
    assertEquals(regexResult.getLiteralValue(), "Hello");
  }

  @Test
  public void testConcatNonLiteralOutputReturnsAsIs() {
    // CONCAT(x, 'World') with a non-literal regex output: ConcatRegexTransformer falls
    // back to a sound but lossy passthrough (returns the output regex unchanged). Verify
    // the passthrough by checking the returned automaton accepts the same strings as the input.
    ConcatRegexTransformer transformer = new ConcatRegexTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.CONCAT, stringRef(), stringLit("World"));

    RegexDomain nonLiteral = new RegexDomain("^[A-Z][a-z]+World$");
    Domain<?, ?> result = transformer.refineInputDomain(expr, nonLiteral);
    assertTrue(result instanceof RegexDomain);
    RegexDomain regexResult = (RegexDomain) result;

    // Passthrough: should accept whatever the input pattern accepted.
    assertTrue(regexResult.getAutomaton().run("HelloWorld"), "should still accept 'HelloWorld'");
    assertTrue(regexResult.getAutomaton().run("AbcWorld"), "should still accept 'AbcWorld'");
    assertFalse(regexResult.getAutomaton().run("helloWorld"), "should reject lowercase first letter");
    assertFalse(regexResult.getAutomaton().run("Hello"), "should reject missing 'World' suffix");
  }
}

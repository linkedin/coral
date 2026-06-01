/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.rel;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Targeted unit tests for {@link ProjectPullUpRewriter} and {@link ProjectPullUpController}.
 *
 * These tests build Calcite RelNode trees programmatically (no Hive) to exercise
 * specific edge cases in the Project pull-up logic.
 */
public class ProjectPullUpRewriterTest {

  private RelBuilder builder;

  @BeforeClass
  public void setup() {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);

    // T1(a VARCHAR, b INT, c VARCHAR) — 3 columns
    RelTestTable t1 = new RelTestTable("T1", new String[] { "a", "b", "c" },
        new SqlTypeName[] { SqlTypeName.VARCHAR, SqlTypeName.INTEGER, SqlTypeName.VARCHAR });
    rootSchema.add("T1", t1);

    // T2(x INT, y VARCHAR) — 2 columns
    RelTestTable t2 = new RelTestTable("T2", new String[] { "x", "y" },
        new SqlTypeName[] { SqlTypeName.INTEGER, SqlTypeName.VARCHAR });
    rootSchema.add("T2", t2);

    // T3(p INT, q INT, r INT) — 3 columns
    RelTestTable t3 = new RelTestTable("T3", new String[] { "p", "q", "r" },
        new SqlTypeName[] { SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER });
    rootSchema.add("T3", t3);

    FrameworkConfig config = Frameworks.newConfigBuilder().defaultSchema(rootSchema).build();
    builder = RelBuilder.create(config);
  }

  // ==================== Filter->Project Pull-Up ====================

  @Test
  public void testFilterProjectPullUp() {
    // Build: Filter(b > 5) -> Project(a, b) -> Scan(T1)
    RelNode tree = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    assertTrue(tree instanceof Filter, "Root should be Filter");
    assertTrue(((Filter) tree).getInput() instanceof Project, "Filter child should be Project");

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    // After: Project(a, b) -> Filter(b > 5) -> Scan(T1)
    assertTrue(result instanceof Project, "Root should now be Project");
    RelNode projectChild = ((Project) result).getInput();
    assertTrue(projectChild instanceof Filter, "Project child should now be Filter");
    RelNode filterChild = ((Filter) projectChild).getInput();
    assertTrue(filterChild instanceof TableScan, "Filter child should be TableScan");
  }

  @Test
  public void testNoChangeWhenNoProjectUnderFilter() {
    // Build: Filter(b > 5) -> Scan(T1) — no Project to pull up
    RelNode tree = builder.scan("T1")
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    assertSame(result, tree, "Should return same instance when no pull-up needed");
  }

  @Test
  public void testFilterProjectConditionRewrite() {
    // Build: Filter($0 = 'abc') -> Project(c, a, b) -> Scan(T1)
    // Project maps: $0->c($2), $1->a($0), $2->b($1)
    // After pull-up, filter condition $0='abc' should become $2='abc' (the original c column)
    RelNode tree = builder.scan("T1").project(builder.field("c"), builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field(0), builder.literal("abc"))).build();

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    assertTrue(result instanceof Project);
    Filter newFilter = (Filter) ((Project) result).getInput();
    RexNode condition = newFilter.getCondition();

    // The condition should reference $2 (c column in original scan)
    String condStr = condition.toString();
    assertTrue(condStr.contains("$2"), "Condition should reference column $2 (c), got: " + condStr);
  }

  @Test
  public void testProjectPreservesRowType() {
    // After pull-up, the pulled-up Project should have the same row type as the original
    RelNode tree = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);
    assertTrue(result instanceof Project);

    assertEquals(result.getRowType().getFieldCount(), tree.getRowType().getFieldCount(),
        "Row type field count should be preserved");
  }

  // ==================== Join->Project Pull-Up ====================

  @Test
  public void testJoinLeftProjectPullUpFieldCountPreserved() {
    // Build: Join with left = Project(b, a, c) -> Scan(T1), right = Scan(T2)
    // The Project reorders but preserves all 3 fields (field count 3->3)
    builder.scan("T1").project(builder.field("b"), builder.field("a"), builder.field("c"));
    builder.scan("T2");
    RelNode tree = builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x"))).build();

    assertTrue(tree instanceof Join);
    assertTrue(((Join) tree).getLeft() instanceof Project, "Left should be Project");

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    // After: Project -> Join -> [Scan(T1), Scan(T2)]
    assertTrue(result instanceof Project, "Root should be Project after pull-up");
    RelNode joinNode = ((Project) result).getInput();
    assertTrue(joinNode instanceof Join, "Project child should be Join");
    Join newJoin = (Join) joinNode;
    assertFalse(newJoin.getLeft() instanceof Project, "Left should no longer be Project");
  }

  @Test
  public void testJoinRightProjectPullUpFieldCountPreserved() {
    // Build: Join with left = Scan(T1), right = Project(y, x) -> Scan(T2)
    // The Project reorders but preserves all 2 fields (field count 2->2)
    builder.scan("T1");
    builder.scan("T2").project(builder.field("y"), builder.field("x"));
    RelNode tree = builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x"))).build();

    assertTrue(tree instanceof Join);
    assertTrue(((Join) tree).getRight() instanceof Project, "Right should be Project");

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    assertTrue(result instanceof Project, "Root should be Project after pull-up");
    RelNode joinNode = ((Project) result).getInput();
    assertTrue(joinNode instanceof Join, "Project child should be Join");
  }

  @Test
  public void testJoinBothProjectsPullUpFieldCountPreserved() {
    // Build: Join with both sides having field-count-preserving Projects
    builder.scan("T1").project(builder.field("b"), builder.field("a"), builder.field("c"));
    builder.scan("T2").project(builder.field("y"), builder.field("x"));
    RelNode tree = builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x"))).build();

    assertTrue(tree instanceof Join);
    assertTrue(((Join) tree).getLeft() instanceof Project);
    assertTrue(((Join) tree).getRight() instanceof Project);

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    assertTrue(result instanceof Project, "Root should be Project");
    RelNode joinNode = ((Project) result).getInput();
    assertTrue(joinNode instanceof Join, "Project child should be Join");
    Join newJoin = (Join) joinNode;
    assertFalse(newJoin.getLeft() instanceof Project, "Left should no longer be Project");
    assertFalse(newJoin.getRight() instanceof Project, "Right should no longer be Project");
  }

  // ==================== Field Count Change ====================

  @Test
  public void testJoinLeftProjectPullUpFieldCountChanged() {
    // Build: Join with left = Project(a, b) -> Scan(T1: a,b,c), right = Scan(T2)
    // Left Project reduces field count 3->2. Previously failed with type mismatch
    // because RexInputRef offsets used the Project's output field count (2) instead
    // of the new left side's scan field count (3). Fixed in items #5/#6.
    builder.scan("T1").project(builder.field("a"), builder.field("b"));
    builder.scan("T2");
    RelNode tree = builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x"))).build();

    assertTrue(tree instanceof Join);
    assertTrue(((Join) tree).getLeft() instanceof Project);

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    assertTrue(result instanceof Project, "Root should be Project after pull-up");
    RelNode joinNode = ((Project) result).getInput();
    assertTrue(joinNode instanceof Join, "Project child should be Join");
    assertFalse(((Join) joinNode).getLeft() instanceof Project, "Left should no longer be Project");
  }

  // ==================== Controller (Fixed Point) ====================

  @Test
  public void testControllerReachesFixedPoint() {
    // Build: Filter -> Project -> Scan — one iteration needed
    RelNode tree = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    RelNode result = ProjectPullUpController.applyUntilFixedPoint(tree, 10);

    // After fixed point: Project -> Filter -> Scan
    assertTrue(result instanceof Project, "Root should be Project");
    assertTrue(((Project) result).getInput() instanceof Filter, "Project child should be Filter");
  }

  @Test
  public void testControllerNoChange() {
    // Build: Filter -> Scan — nothing to pull up
    RelNode tree = builder.scan("T1")
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    RelNode result = ProjectPullUpController.applyUntilFixedPoint(tree, 10);

    assertSame(result, tree, "Should return same tree when no pull-up needed");
  }

  @Test
  public void testControllerNestedPullUp() {
    // Build a tree requiring multiple iterations:
    // Filter -> Project -> Filter -> Project -> Scan(T1)
    // Iteration 1: pulls inner Project above inner Filter
    // Iteration 2: inner Project merges/outer pattern matches
    RelNode tree = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5)))
        .project(builder.field("a"))
        .filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field("a"), builder.literal("xyz"))).build();

    RelNode result = ProjectPullUpController.applyUntilFixedPoint(tree, 100);

    // All Projects should be above all Filters at fixed point
    // Verify no Filter has a Project child
    assertNoFilterProjectPattern(result);
  }

  // ==================== Helpers ====================

  /**
   * Recursively asserts that no Filter node has a Project as its direct child.
   */
  private void assertNoFilterProjectPattern(RelNode node) {
    if (node instanceof Filter) {
      assertFalse(((Filter) node).getInput() instanceof Project,
          "Found Filter->Project pattern after fixed point:\n" + RelOptUtil.toString(node));
    }
    if (node instanceof Join) {
      assertFalse(((Join) node).getLeft() instanceof Project,
          "Found Join with Project left child after fixed point:\n" + RelOptUtil.toString(node));
      assertFalse(((Join) node).getRight() instanceof Project,
          "Found Join with Project right child after fixed point:\n" + RelOptUtil.toString(node));
    }
    for (RelNode input : node.getInputs()) {
      assertNoFilterProjectPattern(input);
    }
  }
}

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
    // (same field names and types, in the same order).
    RelNode tree = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);
    assertTrue(result instanceof Project);

    assertEquals(result.getRowType().getFieldCount(), tree.getRowType().getFieldCount(),
        "Row type field count should be preserved");
    assertEquals(result.getRowType().getFieldNames(), tree.getRowType().getFieldNames(),
        "Row type field names should be preserved (in order)");
    for (int i = 0; i < result.getRowType().getFieldCount(); i++) {
      assertEquals(result.getRowType().getFieldList().get(i).getType(),
          tree.getRowType().getFieldList().get(i).getType(), "Field type at index " + i + " should match");
    }
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

    // The pulled-up Project must reproduce the original row type (names + types).
    assertEquals(result.getRowType().getFieldNames(), tree.getRowType().getFieldNames(),
        "pulled-up row type should match the original");
    // Both join inputs are now raw scans (3-col T1 + 2-col T2 = 5 cols at join).
    assertEquals(newJoin.getRowType().getFieldCount(), 5, "join below pulled-up Project sees raw 3+2 columns");
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
    Join newJoin = (Join) joinNode;
    assertFalse(newJoin.getRight() instanceof Project, "Right should no longer be Project");

    assertEquals(result.getRowType().getFieldNames(), tree.getRowType().getFieldNames(),
        "pulled-up row type should match the original");
    assertEquals(newJoin.getRowType().getFieldCount(), 5, "join below pulled-up Project sees raw 3+2 columns");
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

    assertEquals(result.getRowType().getFieldNames(), tree.getRowType().getFieldNames(),
        "pulled-up row type should match the original");
    assertEquals(newJoin.getRowType().getFieldCount(), 5, "join below pulled-up Project sees raw 3+2 columns");
  }

  // ==================== Field Count Change ====================

  @Test
  public void testJoinLeftProjectPullUpFieldCountChanged() {
    // Build: Join with left = Project(a, b) -> Scan(T1: a,b,c), right = Scan(T2: x,y)
    // Left Project reduces field count 3->2. Originally the right-side reference $2 ("x" in
    // the projected join frame) must shift to $3 once the left expands back to 3 columns;
    // verify the rewritten condition uses the corrected offsets ($1 for b, $3 for x).
    builder.scan("T1").project(builder.field("a"), builder.field("b"));
    builder.scan("T2");
    RelNode tree = builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x"))).build();

    assertTrue(tree instanceof Join);
    assertTrue(((Join) tree).getLeft() instanceof Project);

    RelNode result = ProjectPullUpRewriter.rewriteOneStep(tree);

    assertTrue(result instanceof Project, "Root should be Project after pull-up");
    Join newJoin = (Join) ((Project) result).getInput();
    assertFalse(newJoin.getLeft() instanceof Project, "Left should no longer be Project");
    assertEquals(newJoin.getRowType().getFieldCount(), 5, "join below pulled-up Project sees raw 3+2 columns");

    // The join condition must now reference $1 (T1.b) and $3 (T2.x at offset 3 after left grew
    // from 2 to 3 columns). If the rewriter had kept the old offset $2, that would now point
    // at T1.c — a column type mismatch.
    String condStr = newJoin.getCondition().toString();
    assertTrue(condStr.contains("$1"), "condition should reference $1 (T1.b), got: " + condStr);
    assertTrue(condStr.contains("$3"), "condition should reference $3 (T2.x at shifted offset), got: " + condStr);
    assertFalse(condStr.contains("$2"),
        "condition should NOT reference stale $2 (T2.x at old offset), got: " + condStr);
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
    // At fixed point, all Projects should be above all Filters and the row type should
    // still equal the original (the output row count and field types are preserved).
    RelNode tree = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5)))
        .project(builder.field("a"))
        .filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field("a"), builder.literal("xyz"))).build();

    RelNode result = ProjectPullUpController.applyUntilFixedPoint(tree, 100);

    assertNoFilterProjectPattern(result);
    assertTrue(result instanceof Project, "Root should be a Project after fixed-point pull-up");
    assertEquals(result.getRowType().getFieldNames(), tree.getRowType().getFieldNames(),
        "Row type field names must be preserved by the pull-up");
    assertEquals(result.getRowType().getFieldCount(), 1, "Outer Project keeps only column 'a'");
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

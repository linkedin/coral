package com.linkedin.coral.datagen.rel;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;

import java.util.ArrayList;
import java.util.List;

/**
 * Single-Step Project Pull-Up Rewriter
 * 
 * Finds the FIRST occurrence of:
 * - Filter with a Project child, OR
 * - Join with one or two Project children
 * 
 * Pulls the Project(s) up by inlining expressions and stops.
 * 
 * This is designed to be called repeatedly by a controller program
 * until a fixed point is reached (no more changes).
 */
public final class ProjectPullUpRewriter {

  private ProjectPullUpRewriter() {
  }

  /**
   * Performs a single pull-up step.
   * Returns the rewritten tree if a change was made, or the original tree if no change.
   * 
   * @param rel The root of the relational tree
   * @return The rewritten tree, or the same tree if no Project pull-up occurred
   */
  public static RelNode rewriteOneStep(RelNode rel) {
    RewriteResult result = findAndPullUp(rel);
    return result.node;
  }

  /**
   * Result of a rewrite operation.
   */
  private static class RewriteResult {
    final RelNode node;
    final boolean changed;

    RewriteResult(RelNode node, boolean changed) {
      this.node = node;
      this.changed = changed;
    }
  }

  /**
   * Recursively finds the first Filter with Project child or Join with Project children,
   * pulls the Project(s) up, and returns.
   */
  private static RewriteResult findAndPullUp(RelNode node) {
    // Case 1: Filter with Project child
    if (node instanceof Filter) {
      Filter filter = (Filter) node;
      RelNode child = filter.getInput();
      
      if (child instanceof Project) {
        // Found it! Pull the Project above the Filter
        RelNode pulled = pullProjectAboveFilter(filter, (Project) child);
        return new RewriteResult(pulled, true);
      }
      
      // No Project child, recurse
      RewriteResult childResult = findAndPullUp(child);
      if (childResult.changed) {
        Filter newFilter = filter.copy(
            filter.getTraitSet(),
            childResult.node,
            filter.getCondition()
        );
        return new RewriteResult(newFilter, true);
      }
      return new RewriteResult(filter, false);
    }

    // Case 2: Join with Project children
    if (node instanceof Join) {
      Join join = (Join) node;
      RelNode left = join.getLeft();
      RelNode right = join.getRight();
      
      boolean leftIsProject = left instanceof Project;
      boolean rightIsProject = right instanceof Project;
      
      if (leftIsProject || rightIsProject) {
        // Found it! Pull the Project(s) above the Join
        RelNode pulled = pullProjectsAboveJoin(join, 
            leftIsProject ? (Project) left : null,
            rightIsProject ? (Project) right : null);
        return new RewriteResult(pulled, true);
      }
      
      // No Project children, recurse on left first
      RewriteResult leftResult = findAndPullUp(left);
      if (leftResult.changed) {
        Join newJoin = join.copy(
            join.getTraitSet(),
            join.getCondition(),
            leftResult.node,
            right,
            join.getJoinType(),
            join.isSemiJoinDone()
        );
        return new RewriteResult(newJoin, true);
      }
      
      // Recurse on right
      RewriteResult rightResult = findAndPullUp(right);
      if (rightResult.changed) {
        Join newJoin = join.copy(
            join.getTraitSet(),
            join.getCondition(),
            left,
            rightResult.node,
            join.getJoinType(),
            join.isSemiJoinDone()
        );
        return new RewriteResult(newJoin, true);
      }
      
      return new RewriteResult(join, false);
    }

    // Case 3: Project node - don't pull Projects above Projects, but still search below
    if (node instanceof Project) {
      Project project = (Project) node;
      RelNode child = project.getInput();
      
      // Recurse to find Filter->Project or Join->Project patterns below
      RewriteResult childResult = findAndPullUp(child);
      if (childResult.changed) {
        // Rebuild Project with new child
        Project newProject = project.copy(
            project.getTraitSet(),
            childResult.node,
            project.getProjects(),
            project.getRowType()
        );
        return new RewriteResult(newProject, true);
      }
      
      return new RewriteResult(project, false);
    }

    // Case 4: Other nodes - recurse on all children
    List<RelNode> oldInputs = node.getInputs();
    if (oldInputs.isEmpty()) {
      return new RewriteResult(node, false);
    }
    
    List<RelNode> newInputs = new ArrayList<>();
    boolean anyChanged = false;
    
    for (RelNode input : oldInputs) {
      RewriteResult inputResult = findAndPullUp(input);
      newInputs.add(inputResult.node);
      if (inputResult.changed) {
        anyChanged = true;
        // Found and pulled up - stop here and return
        // Fill rest with original inputs
        for (int i = newInputs.size(); i < oldInputs.size(); i++) {
          newInputs.add(oldInputs.get(i));
        }
        RelNode newNode = node.copy(node.getTraitSet(), newInputs);
        return new RewriteResult(newNode, true);
      }
    }
    
    return new RewriteResult(node, false);
  }

  /**
   * Pulls a Project above a Filter by inlining the Project's expressions
   * into the Filter's condition.
   * 
   * Before: Filter(cond) -> Project(exprs) -> child
   * After:  Project(exprs) -> Filter(cond') -> child
   * 
   * where cond' has Project expressions inlined.
   */
  private static RelNode pullProjectAboveFilter(Filter filter, Project project) {
    // 1) Inline project expressions into filter condition
    RexNode inlinedCondition = inlineExpressions(
        filter.getCondition(),
        project.getProjects()
    );

    // 2) Create new Filter over project's input
    Filter newFilter = filter.copy(
        filter.getTraitSet(),
        project.getInput(),
        inlinedCondition
    );

    // 3) Pull Project above the new Filter
    Project pulledProject = project.copy(
        project.getTraitSet(),
        newFilter,
        project.getProjects(),
        project.getRowType()
    );

    return pulledProject;
  }

  /**
   * Pulls Project(s) above a Join by inlining the Project expressions
   * into the Join's condition.
   * 
   * Before: Join(cond) with leftProj and/or rightProj as children
   * After:  Project (combined or single) -> Join(cond') -> children's inputs
   * 
   * where cond' has Project expressions inlined.
   */
  private static RelNode pullProjectsAboveJoin(Join join, Project leftProj, Project rightProj) {
    RelNode newLeft = join.getLeft();
    RelNode newRight = join.getRight();
    RexNode newCondition = join.getCondition();

    // Inline left Project if present
    if (leftProj != null) {
      newCondition = inlineLeftSide(
          newCondition,
          leftProj.getProjects(),
          leftProj.getRowType().getFieldCount()
      );
      newLeft = leftProj.getInput();
    }

    // Inline right Project if present
    if (rightProj != null) {
      int leftFieldCount = newLeft.getRowType().getFieldCount();
      newCondition = inlineRightSide(
          newCondition,
          rightProj.getProjects(),
          leftFieldCount
      );
      newRight = rightProj.getInput();
    }

    // Create new Join with inlined condition
    Join newJoin = join.copy(
        join.getTraitSet(),
        newCondition,
        newLeft,
        newRight,
        join.getJoinType(),
        join.isSemiJoinDone()
    );

    // Pull Project(s) above the Join
    if (leftProj != null && rightProj != null) {
      // Both sides had Projects - create combined Project
      List<RexNode> combinedExprs = new ArrayList<>();
      
      // Left side expressions (unchanged)
      combinedExprs.addAll(leftProj.getProjects());
      
      // Right side expressions (with offset adjusted)
      int leftCount = leftProj.getRowType().getFieldCount();
      for (RexNode expr : rightProj.getProjects()) {
        combinedExprs.add(adjustOffsets(expr, leftCount));
      }
      
      return leftProj.copy(
          leftProj.getTraitSet(),
          newJoin,
          combinedExprs,
          join.getRowType()
      );
    } else if (leftProj != null) {
      // Only left had Project
      List<RexNode> exprs = new ArrayList<>();
      exprs.addAll(leftProj.getProjects());
      
      // Add pass-through for right side
      int leftCount = leftProj.getRowType().getFieldCount();
      int rightCount = join.getRowType().getFieldCount() - leftCount;
      for (int i = 0; i < rightCount; i++) {
        exprs.add(new RexInputRef(leftCount + i,
            join.getRowType().getFieldList().get(leftCount + i).getType()));
      }
      
      return leftProj.copy(
          leftProj.getTraitSet(),
          newJoin,
          exprs,
          join.getRowType()
      );
    } else {
      // Only right had Project
      List<RexNode> exprs = new ArrayList<>();
      int leftCount = newLeft.getRowType().getFieldCount();
      
      // Add pass-through for left side
      for (int i = 0; i < leftCount; i++) {
        exprs.add(new RexInputRef(i,
            join.getRowType().getFieldList().get(i).getType()));
      }
      
      // Add right side expressions (with offset adjusted)
      for (RexNode expr : rightProj.getProjects()) {
        exprs.add(adjustOffsets(expr, leftCount));
      }
      
      return rightProj.copy(
          rightProj.getTraitSet(),
          newJoin,
          exprs,
          join.getRowType()
      );
    }
  }

  /**
   * Inline Project expressions into a parent expression.
   * Replaces $k with projectExprs[k].
   * 
   * Note: This does NOT recursively inline. It only replaces one level.
   * Multiple Projects are handled by repeated single-step applications.
   */
  private static RexNode inlineExpressions(RexNode expr, List<RexNode> projectExprs) {
    return expr.accept(new RexShuttle() {
      @Override
      public RexNode visitInputRef(RexInputRef ref) {
        int idx = ref.getIndex();
        if (idx >= 0 && idx < projectExprs.size()) {
          // Return the expression directly - don't recursively inline
          // The replacement refers to the Project's INPUT, not its output
          return projectExprs.get(idx);
        }
        return ref;
      }
    });
  }

  /**
   * Inline expressions for left side of join condition.
   * Only rewrites input refs < leftFieldCount.
   */
  private static RexNode inlineLeftSide(RexNode condition, List<RexNode> leftProjects, int leftFieldCount) {
    return condition.accept(new RexShuttle() {
      @Override
      public RexNode visitInputRef(RexInputRef ref) {
        int idx = ref.getIndex();
        if (idx < leftFieldCount && idx >= 0 && idx < leftProjects.size()) {
          // Return directly - no recursive inlining
          return leftProjects.get(idx);
        }
        return ref;
      }
    });
  }

  /**
   * Inline expressions for right side of join condition.
   * Only rewrites input refs >= leftFieldCount.
   */
  private static RexNode inlineRightSide(RexNode condition, List<RexNode> rightProjects, int leftFieldCount) {
    return condition.accept(new RexShuttle() {
      @Override
      public RexNode visitInputRef(RexInputRef ref) {
        int idx = ref.getIndex();
        if (idx >= leftFieldCount) {
          int rightIdx = idx - leftFieldCount;
          if (rightIdx >= 0 && rightIdx < rightProjects.size()) {
            RexNode replacement = rightProjects.get(rightIdx);
            // Return directly with adjusted offsets - no recursive inlining
            return adjustOffsets(replacement, leftFieldCount);
          }
        }
        return ref;
      }
    });
  }

  /**
   * Adjust input ref offsets by adding an offset.
   * Used for right-side expressions in join context.
   */
  private static RexNode adjustOffsets(RexNode expr, int offset) {
    return expr.accept(new RexShuttle() {
      @Override
      public RexNode visitInputRef(RexInputRef ref) {
        return new RexInputRef(ref.getIndex() + offset, ref.getType());
      }
    });
  }
}

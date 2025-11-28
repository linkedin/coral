package com.linkedin.coral.datagen.rel;

import org.apache.calcite.rel.RelNode;

/**
 * Controller for iteratively applying Project pull-up until a fixed point is reached.
 * 
 * This controller repeatedly calls {@link ProjectPullUpRewriter#rewriteOneStep(RelNode)}
 * until no more changes occur, ensuring all Projects are pulled to the top of the tree.
 */
public final class ProjectPullUpController {

  private ProjectPullUpController() {
  }

  /**
   * Applies Project pull-up repeatedly until reaching a fixed point.
   * 
   * A fixed point is reached when no more Filter->Project or Join->Project
   * patterns exist in the tree (all Projects have been pulled to the top).
   * 
   * @param root The root of the relational tree to rewrite
   * @return The rewritten tree with all Projects pulled to the top
   * @throws IllegalStateException if fixed point is not reached within max iterations
   */
  public static RelNode applyUntilFixedPoint(RelNode root) {
    return applyUntilFixedPoint(root, DEFAULT_MAX_ITERATIONS);
  }

  /**
   * Applies Project pull-up repeatedly until reaching a fixed point.
   * 
   * @param root The root of the relational tree to rewrite
   * @param maxIterations Maximum number of iterations before giving up
   * @return The rewritten tree with all Projects pulled to the top
   * @throws IllegalStateException if fixed point is not reached within max iterations
   */
  public static RelNode applyUntilFixedPoint(RelNode root, int maxIterations) {
    RelNode current = root;
    int iteration = 0;

    while (iteration < maxIterations) {
      RelNode next = ProjectPullUpRewriter.rewriteOneStep(current);

      // Check if anything changed
      if (next == current) {
        // Fixed point reached
        return current;
      }

      iteration++;
      current = next;
    }

    throw new IllegalStateException(
        "Failed to reach fixed point after " + maxIterations + " iterations. " +
        "This may indicate a bug in the rewrite logic or an unexpectedly complex tree."
    );
  }

  /**
   * Applies Project pull-up with a callback invoked after each iteration.
   * Useful for debugging, logging, or monitoring progress.
   * 
   * @param root The root of the relational tree to rewrite
   * @param callback Callback invoked after each iteration with iteration number and current tree
   * @return The rewritten tree with all Projects pulled to the top
   * @throws IllegalStateException if fixed point is not reached within max iterations
   */
  public static RelNode applyUntilFixedPoint(RelNode root, IterationCallback callback) {
    return applyUntilFixedPoint(root, DEFAULT_MAX_ITERATIONS, callback);
  }

  /**
   * Applies Project pull-up with a callback invoked after each iteration.
   * 
   * @param root The root of the relational tree to rewrite
   * @param maxIterations Maximum number of iterations before giving up
   * @param callback Callback invoked after each iteration with iteration number and current tree
   * @return The rewritten tree with all Projects pulled to the top
   * @throws IllegalStateException if fixed point is not reached within max iterations
   */
  public static RelNode applyUntilFixedPoint(RelNode root, int maxIterations, IterationCallback callback) {
    RelNode current = root;
    int iteration = 0;

    while (iteration < maxIterations) {
      RelNode next = ProjectPullUpRewriter.rewriteOneStep(current);

      // Check if anything changed
      if (next == current) {
        // Fixed point reached
        callback.onFixedPoint(iteration);
        return current;
      }

      iteration++;
      current = next;
      callback.onIteration(iteration, current);
    }

    throw new IllegalStateException(
        "Failed to reach fixed point after " + maxIterations + " iterations. " +
        "This may indicate a bug in the rewrite logic or an unexpectedly complex tree."
    );
  }

  /**
   * Callback interface for monitoring iteration progress.
   */
  public interface IterationCallback {
    /**
     * Called after each successful iteration.
     * 
     * @param iteration The iteration number (1-indexed)
     * @param tree The current state of the tree after this iteration
     */
    void onIteration(int iteration, RelNode tree);

    /**
     * Called when fixed point is reached.
     * 
     * @param totalIterations Total number of iterations performed
     */
    void onFixedPoint(int totalIterations);
  }

  /**
   * Default maximum number of iterations before giving up.
   * This should be more than enough for any realistic query plan.
   */
  private static final int DEFAULT_MAX_ITERATIONS = 1000;
}

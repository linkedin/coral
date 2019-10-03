package com.linkedin.coral.pig.rel2pig;

/**
 * PigLoadFunction is a functional interface that defines a mapping from a table to its
 * the LoadFunc to be used in Pig.
 *
 * For example, DaliViews generally use the dali.data.pig.DaliStorage().
 *
 * We could define the PigLoadFunction as the following:
 *   PigLoadFunction func =
 *     (String db, String table) -> "dali.data.pig.DaliStorage()";
 */
@FunctionalInterface
public interface PigLoadFunction {
  String getLoadFunction(String database, String table);
}

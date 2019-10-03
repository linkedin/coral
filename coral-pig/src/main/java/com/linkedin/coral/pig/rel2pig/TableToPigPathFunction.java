package com.linkedin.coral.pig.rel2pig;

/**
 * TableToPigPathFunction is a functional interface that defines a mapping from a table to its
 * fully qualified source to be read in Pig.
 *
 * For example, if we are given a DaliView with the following definition:
 * SELECT
 *   *
 * FROM DB1.TABLE1
 *
 * We would want to map the table, DB1.TABLE1 to its dali equivalent which would be given by:
 * dalids///DB1.TABLE1
 *
 * In this case, we could define a TableToPigPathFunction as:
 *   TableToPigPathFunction func =
 *     (String db, String table) -> String.format("dalids:///%s.%s", db, table);
 */
@FunctionalInterface
public interface TableToPigPathFunction {
  String getPigPath(String database, String table);
}

/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig;

/**
 * PigLoadFunction is a functional interface that defines a mapping from a table to its
 * the LoadFunc to be used in Pig.
 *
 * For example, DaliViews generally use the dali.data.pig.DaliStorage().
 *
 * We could define the PigLoadFunction as the following:
 *   PigLoadFunction func =
 *     (String db, String table) -&gt; "dali.data.pig.DaliStorage()";
 */
@FunctionalInterface
public interface PigLoadFunction {
  String getLoadFunction(String database, String table);
}

/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * RelToPigBuilder is used to store intermediary Pig Script state used in the conversion of a
 * SQL query from Calcite Relational Algebra to Pig Latin.
 */
class RelToPigBuilder {

  private static final String INTERMEDIATE_ALIAS_PREFIX = "CORAL_PIG_ALIAS_";
  private int intermediateAliasCount = 0;
  private final List<String> statements = new ArrayList<>();
  private final Set<String> functionDefinitions = new HashSet<>();

  public RelToPigBuilder() {

  }

  /**
   * Appends a Pig Latin statement to the current Pig Latin script.
   *
   * @param statement Pig Latin statement to be executed AFTER all statements that have been previously added.
   */
  public void addStatement(String statement) {
    statements.add(statement);
  }

  /**
   * Adds the set of given functionDefinitions to the current Pig Latin script.
   *
   * @param functionDefinitions
   */
  public void addFunctionDefinitions(Set<String> functionDefinitions) {
    this.functionDefinitions.addAll(functionDefinitions);
  }

  /**
   * Gets the the generated Pig Latin script.
   *
   * @return The derived Pig Latin script
   */
  public String getScript() {
    final String functionDefinitionsOutput = String.join("\n", functionDefinitions);
    final String statementsOutput = String.join("\n", statements);
    return functionDefinitionsOutput.isEmpty() ? statementsOutput
        : String.join("\n", functionDefinitionsOutput, statementsOutput);
  }

  /**
   * Gets a unique Pig Latin alias in this translation
   *
   * @return A unique Pig Latin identifier for this translation.
   */
  public String getUniqueAlias() {
    ++intermediateAliasCount;
    return INTERMEDIATE_ALIAS_PREFIX + intermediateAliasCount;
  }

}

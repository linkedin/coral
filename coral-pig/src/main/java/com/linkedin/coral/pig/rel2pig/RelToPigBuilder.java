package com.linkedin.coral.pig.rel2pig;

import java.util.ArrayList;
import java.util.List;


/**
 * RelToPigBuilder is used to store intermediary Pig Script state used in the conversion of a
 * SQL query from Calcite Relational Algebra to Pig Latin.
 */
class RelToPigBuilder {

  private static final String INTERMEDIATE_ALIAS_PREFIX = "CORAL_PIG_ALIAS_";
  private int intermediateAliasCount = 0;
  private List<String> statements = new ArrayList<>();

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
   * Gets the the generated Pig Latin script.
   *
   * @return The derived Pig Latin script
   */
  public String getScript() {
    return String.join("\n", statements);
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

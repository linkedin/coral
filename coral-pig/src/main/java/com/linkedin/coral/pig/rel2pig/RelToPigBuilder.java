package com.linkedin.coral.pig.rel2pig;

import java.util.ArrayList;
import java.util.List;


/**
 * RelToPigBuilder is used to store intermediary Pig Script state used in the conversion of a
 * SQL query from Calcite Relational Algebra to Pig Latin.
 */
class RelToPigBuilder {

  private List<String> statements = new ArrayList<>();

  public RelToPigBuilder() {

  }

  /**
   * Appends a Pig Latin statement to the current Pig Latin script.
   * @param statement Pig Latin statement to be executed AFTER all statements that have been previously added.
   */
  public void addStatement(String statement) {
    statements.add(statement);
  }

  /**
   * Gets the the generated Pig Latin script.
   * @return The derived Pig Latin script
   */
  public String getScript() {
    return String.join("\n", statements);
  }

}

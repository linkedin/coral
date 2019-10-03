package com.linkedin.coral.pig.rel2pig;

import java.util.ArrayList;
import java.util.List;


/**
 * RelToPigBuilder is a used to store intermediary Pig Script state in the
 */
class RelToPigBuilder {

  private List<String> statements = new ArrayList<>();

  public RelToPigBuilder() {

  }

  public void addStatement(String statement) {
    statements.add(statement);
  }

  public String getScript() {
    return String.join("\n", statements);
  }
}

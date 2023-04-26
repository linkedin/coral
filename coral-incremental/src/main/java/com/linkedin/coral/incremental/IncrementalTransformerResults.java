/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.rel.RelNode;


public class IncrementalTransformerResults {

  private RelNode incrementalRelNode;
  private RelNode refreshRelNode;
  private Map<String, RelNode> intermediateQueryRelNodes;

  public IncrementalTransformerResults() {
    incrementalRelNode = null;
    refreshRelNode = null;
    intermediateQueryRelNodes = new HashMap<>();
  }

  public boolean existsIncrementalRelNode() {
    return incrementalRelNode != null;
  }

  public RelNode getIncrementalRelNode() {
    return incrementalRelNode;
  }

  public boolean existsRefreshRelNode() {
    return refreshRelNode != null;
  }

  public RelNode getRefreshRelNode() {
    return refreshRelNode;
  }

  public Map<String, RelNode> getIntermediateQueryRelNodes() {
    return intermediateQueryRelNodes;
  }

  public boolean containsIntermediateQueryRelNodeKey(String name) {
    return intermediateQueryRelNodes.containsKey(name);
  }

  public RelNode getIntermediateQueryRelNodeCorrespondingToKey(String name) {
    return intermediateQueryRelNodes.get(name);
  }

  public void setIncrementalRelNode(RelNode incrementalRelNode) {
    this.incrementalRelNode = incrementalRelNode;
  }

  public void setRefreshRelNode(RelNode refreshRelNode) {
    this.refreshRelNode = refreshRelNode;
  }

  public void setIntermediateQueryRelNodes(Map<String, RelNode> intermediateQueryRelNodes) {
    this.intermediateQueryRelNodes = intermediateQueryRelNodes;
  }

  public void addIntermediateQueryRelNode(String name, RelNode intermediateRelNode) {
    this.intermediateQueryRelNodes.put(name, intermediateRelNode);
  }

  public void addMultipleIntermediateQueryRelNodes(Map<String, RelNode> intermediateQueryRelNodes) {
    if (intermediateQueryRelNodes != null) {
      this.intermediateQueryRelNodes.putAll(intermediateQueryRelNodes);
    }
  }

}

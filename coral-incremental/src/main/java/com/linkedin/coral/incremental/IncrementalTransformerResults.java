/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.RelNode;


public class IncrementalTransformerResults {

  private RelNode incrementalRelNode;
  private RelNode refreshRelNode;
  private Map<String, RelNode> intermediateQueryRelNodes;
  private List<String> intermediateOrderings;

  public IncrementalTransformerResults() {
    incrementalRelNode = null;
    refreshRelNode = null;
    intermediateQueryRelNodes = new LinkedHashMap<>();
    intermediateOrderings = new ArrayList<>();
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

  public List<String> getIntermediateOrderings() {
    return intermediateOrderings;
  }

  public int getIndexOfIntermediateOrdering(String name) {
    return intermediateOrderings.indexOf(name);
  }

  public void setIncrementalRelNode(RelNode incrementalRelNode) {
    this.incrementalRelNode = incrementalRelNode;
  }

  public void setRefreshRelNode(RelNode refreshRelNode) {
    this.refreshRelNode = refreshRelNode;
  }

  public void addIntermediateQueryRelNode(String name, RelNode intermediateRelNode) {
    this.intermediateQueryRelNodes.put(name, intermediateRelNode);
    addIntermediateOrdering(name);
  }

  public void addMultipleIntermediateQueryRelNodes(Map<String, RelNode> intermediateQueryRelNodes) {
    if (intermediateQueryRelNodes != null) {
      this.intermediateQueryRelNodes.putAll(intermediateQueryRelNodes);
      addMultipleIntermediateOrderings(new ArrayList<>(intermediateQueryRelNodes.keySet()));
    }
  }

  public void addIntermediateOrdering(String intermediateOrdering) {
    this.intermediateOrderings.add(intermediateOrdering);
  }

  public void addMultipleIntermediateOrderings(List<String> intermediateOrderings) {
    this.intermediateOrderings.addAll(intermediateOrderings);
  }

}

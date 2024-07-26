/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableFunctionScan;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalIntersect;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalMinus;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalSort;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;

import com.linkedin.coral.common.HiveUncollect;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.LinkTarget;
import guru.nidi.graphviz.model.Node;

import static guru.nidi.graphviz.model.Factory.*;


public class RelNodeVisualizationShuttle extends RelShuttleImpl {
  private final Map<RelNode, Node> nodeMap;

  public RelNodeVisualizationShuttle() {
    this.nodeMap = new HashMap<>();
  }

  @Override
  public RelNode visit(LogicalAggregate aggregate) {
    super.visit(aggregate);
    return aggregate;
  }

  @Override
  public RelNode visit(TableScan scan) {
    super.visit(scan);
    nodeMap.put(scan, node(String.join(".", scan.getTable().getQualifiedName())));
    return scan;
  }

  @Override
  public RelNode visit(TableFunctionScan scan) {
    super.visit(scan);
    nodeMap.put(scan, node(scan.getCall().toString()));
    return scan;
  }

  @Override
  public RelNode visit(LogicalValues values) {
    super.visit(values);
    String label = values.getRowType().toString() + " values";
    nodeMap.put(values, node(values.getRowType().toString() + " values"));
    return values;
  }

  @Override
  public RelNode visit(LogicalFilter filter) {
    super.visit(filter);
    Node filterNode = node("Filter: " + filter.getCondition().toString());
    Node input = nodeMap.get(filter.getInput());
    nodeMap.put(filter, filterNode.link(input));
    return filter;
  }

  @Override
  public RelNode visit(LogicalProject project) {
    super.visit(project);
    Node filterNode = node("Project: " + project.getProjects().toString());
    Node input = nodeMap.get(project.getInput());
    nodeMap.put(project, filterNode.link(input));
    return project;
  }

  @Override
  public RelNode visit(LogicalJoin join) {
    super.visit(join);
    Node joinNode = node("Join: " + join.getCondition().toString());
    RelNode leftChild = join.getLeft();
    RelNode rightChild = join.getRight();
    String leftLabel = getLabel(leftChild, 0, false);
    String rightLabel = getLabel(rightChild, leftChild.getRowType().getFieldCount(), false);
    LinkTarget leftEdge = edge(leftChild, leftLabel);
    LinkTarget rightEdge = edge(rightChild, rightLabel);
    nodeMap.put(join, joinNode.link(new LinkTarget[] { leftEdge, rightEdge }));
    return join;
  }

  @Override
  public RelNode visit(LogicalCorrelate correlate) {
    super.visit(correlate);
    Node correlateNode = node("Correlate: " + correlate.getCorrelVariable());
    RelNode leftChild = correlate.getLeft();
    RelNode rightChild = correlate.getRight();
    String leftLabel = getLabel(leftChild, 0, true);
    String rightLabel = getLabel(rightChild, leftChild.getRowType().getFieldCount(), false);
    LinkTarget leftEdge = edge(leftChild, leftLabel);
    LinkTarget rightEdge = edge(rightChild, rightLabel);
    nodeMap.put(correlate, correlateNode.link(new LinkTarget[] { leftEdge, rightEdge }));
    return correlate;
  }

  private String getLabel(RelNode target, int startIndex, boolean isCorrelateSide) {
    String label = "";
    String prefix = isCorrelateSide ? "$cor" : "$";
    for (int i = 0; i < target.getRowType().getFieldCount(); i++) {
      label += prefix + (i + startIndex) + " = " + target.getRowType().getFieldList().get(i).getName() + "\n";
    }
    return label;
  }

  @Override
  public RelNode visit(RelNode other) {
    super.visit(other);
    if (other instanceof HiveUncollect) {
      Node input = nodeMap.get(other.getInput(0));
      nodeMap.put(other, node("Uncollect").link(input));
    }
    return other;
  }

  @Override
  public RelNode visit(LogicalUnion union) {
    super.visit(union);
    Node unionNode = node("Union");
    List<LinkTarget> edges = new ArrayList<>();
    for (RelNode input : union.getInputs()) {
      edges.add(edge(input, ""));
    }
    nodeMap.put(union, unionNode.link(edges.toArray(new LinkTarget[0])));
    return union;
  }

  @Override
  public RelNode visit(LogicalIntersect intersect) {
    super.visit(intersect);
    return intersect;
  }

  @Override
  public RelNode visit(LogicalMinus minus) {
    super.visit(minus);
    return minus;
  }

  @Override
  public RelNode visit(LogicalSort sort) {
    super.visit(sort);
    return sort;
  }

  public Node getNode(RelNode relNode) {
    return nodeMap.get(relNode);
  }

  private Link edge(RelNode target, String label) {
    return to(nodeMap.get(target)).with(Label.of(label));
  }

  private static Node node(String label) {
    return Factory.node(UUID.randomUUID().toString()).with(Label.of(label));
  }

}

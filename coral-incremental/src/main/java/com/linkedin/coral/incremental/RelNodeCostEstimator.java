/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;

import static java.lang.Math.*;


public class RelNodeCostEstimator {

  class CostInfo {
    // TODO: we may also need to add TableName field.
    Double cost;
    Double row;

    public CostInfo(Double cost, Double row) {
      this.cost = cost;
      this.row = row;
    }
  }

  class JoinKey {
    String leftTableName;
    String rightTableName;
    String leftFieldName;
    String rightFieldName;

    public JoinKey(String leftTableName, String rightTableName, String leftFieldName, String rightFieldName) {
      this.leftTableName = leftTableName;
      this.rightTableName = rightTableName;
      this.leftFieldName = leftFieldName;
      this.rightFieldName = rightFieldName;
    }
  }

  private Map<String, Double> stat = new HashMap<>();

  private Map<String, Double> distinctStat = new HashMap<>();

  public void setStat(Map<String, Double> stat) {
    this.stat = stat;
  }

  public void setDistinctStat(Map<String, Double> distinctStat) {
    this.distinctStat = distinctStat;
  }

  private Double IOCostParam = 1.0;

  private Double shuffleCostParam = 1.0;

  public void setIOCostParam(Double IOCostParam) {
    this.IOCostParam = IOCostParam;
  }

  public void setShuffleCostParam(Double shuffleCostParam) {
    this.shuffleCostParam = shuffleCostParam;
  }

  public void loadStatistic(String configPath) {
    try {
      String content = new String(Files.readAllBytes(Paths.get(configPath)));
      // Parse JSON string to JsonObject
      JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
      // Iterate over each table in the JSON object
      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        String tableName = entry.getKey();
        JsonObject tableObject = entry.getValue().getAsJsonObject();

        // Extract row count
        Double rowCount = tableObject.get("RowCount").getAsDouble();

        // Extract distinct counts
        JsonObject distinctCounts = tableObject.getAsJsonObject("DistinctCounts");

        stat.put(tableName, rowCount);

        // Iterate over distinct counts
        for (Map.Entry<String, JsonElement> distinctEntry : distinctCounts.entrySet()) {
          String columnName = distinctEntry.getKey();
          Double distinctCount = distinctEntry.getValue().getAsDouble();

          distinctStat.put(tableName + ":" + columnName, distinctCount);
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public Double getCost(RelNode rel) {
    CostInfo executionCostInfo = getExecutionCost(rel);
    Double IOCost = executionCostInfo.row * IOCostParam;
    return executionCostInfo.cost * shuffleCostParam + IOCost;
  }

  public CostInfo getExecutionCost(RelNode rel) {
    if (rel instanceof TableScan) {
      return getExecutionCostTableScan((TableScan) rel);
    } else if (rel instanceof LogicalJoin) {
      return getExecutionCostJoin((LogicalJoin) rel);
    } else if (rel instanceof LogicalUnion) {
      return getExecutionCostUnion((LogicalUnion) rel);
    } else if (rel instanceof LogicalProject) {
      return getExecutionCostProject((LogicalProject) rel);
    }
    return new CostInfo(0.0, 0.0);
  }

  private CostInfo getExecutionCostTableScan(TableScan scan) {
    RelOptTable originalTable = scan.getTable();
    String tableName = getTableName(originalTable);
    Double row = stat.getOrDefault(tableName, 5.0);
    return new CostInfo(row, row);
  }

  private String getTableName(RelOptTable table) {
    return String.join(".", table.getQualifiedName());
  }

  private CostInfo getExecutionCostJoin(LogicalJoin join) {
    RelNode left = join.getLeft();
    RelNode right = join.getRight();
    if (!(left instanceof TableScan) || !(right instanceof TableScan)) {
      return new CostInfo(0.0, 0.0);
    }
    CostInfo leftCost = getExecutionCost(left);
    CostInfo rightCost = getExecutionCost(right);
    Double joinSize = estimateJoinSize(join, leftCost.row, rightCost.row);
    return new CostInfo(max(leftCost.cost, rightCost.cost), joinSize);
  }

  private List<JoinKey> findJoinKeys(LogicalJoin join) {
    List<JoinKey> joinKeys = new ArrayList<>();
    RexNode condition = join.getCondition();
    if (condition instanceof RexCall) {
      processRexCall((RexCall) condition, join, joinKeys);
    }
    return joinKeys;
  }

  private void processRexCall(RexCall call, LogicalJoin join, List<JoinKey> joinKeys) {
    if (call.getOperator().getName().equalsIgnoreCase("AND")) {
      // Process each operand of the AND separately
      for (RexNode operand : call.getOperands()) {
        if (operand instanceof RexCall) {
          processRexCall((RexCall) operand, join, joinKeys);
        }
      }
    } else {
      // Process the join condition (e.g., EQUALS)
      List<RexNode> operands = call.getOperands();
      if (operands.size() == 2 && operands.get(0) instanceof RexInputRef && operands.get(1) instanceof RexInputRef) {
        RexInputRef leftRef = (RexInputRef) operands.get(0);
        RexInputRef rightRef = (RexInputRef) operands.get(1);
        RelDataType leftType = join.getLeft().getRowType();
        RelDataType rightType = join.getRight().getRowType();

        int leftIndex = leftRef.getIndex();
        int rightIndex = rightRef.getIndex() - leftType.getFieldCount();

        RelDataTypeField leftField = leftType.getFieldList().get(leftIndex);
        String leftTableName = getTableName(join.getLeft().getTable());
        String leftFieldName = leftField.getName();
        RelDataTypeField rightField = rightType.getFieldList().get(rightIndex);
        String rightTableName = getTableName(join.getRight().getTable());
        String rightFieldName = rightField.getName();

        joinKeys.add(new JoinKey(leftTableName, rightTableName, leftFieldName, rightFieldName));
      }
    }
  }

  private Double estimateJoinSize(LogicalJoin join, Double leftSize, Double rightSize) {
    List<JoinKey> joinKeys = findJoinKeys(join);
    Double selectivity = 1.0;
    for (JoinKey joinKey : joinKeys) {
      String leftTableName = joinKey.leftTableName;
      String rightTableName = joinKey.rightTableName;
      String leftFieldName = joinKey.leftFieldName;
      String rightFieldName = joinKey.rightFieldName;
      Double leftCardinality = stat.getOrDefault(leftTableName, 5.0);
      Double rightCardinality = stat.getOrDefault(rightTableName, 5.0);
      Double leftDistinct = distinctStat.getOrDefault(leftTableName + ":" + leftFieldName, leftCardinality);
      Double rightDistinct = distinctStat.getOrDefault(rightTableName + ":" + rightFieldName, rightCardinality);
      selectivity *= 1 / max(leftDistinct, rightDistinct);
    }
    return leftSize * rightSize * selectivity;
  }

  private CostInfo getExecutionCostUnion(LogicalUnion union) {
    Double unionCost = 0.0;
    Double unionSize = 0.0;
    RelNode input;
    for (Iterator var4 = union.getInputs().iterator(); var4.hasNext();) {
      input = (RelNode) var4.next();
      CostInfo inputCost = getExecutionCost(input);
      unionSize += inputCost.row;
      unionCost = max(inputCost.cost, unionCost);
    }
    unionCost *= 2;
    return new CostInfo(unionCost, unionSize);
  }

  private CostInfo getExecutionCostProject(LogicalProject project) {
    return getExecutionCost(project.getInput());
  }
}

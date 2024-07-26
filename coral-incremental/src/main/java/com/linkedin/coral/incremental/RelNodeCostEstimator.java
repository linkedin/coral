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


/**
 * RelNodeCostEstimator is a utility class designed to estimate the cost of executing relational operations
 * in a query plan. It uses statistical information about table row counts and column distinct values
 * to compute costs associated with different types of relational operations like table scans, joins,
 * unions, and projections.
 *
 * <p>This class supports loading statistics from a JSON configuration file.
 * For a relational operations (RelNode), the execution cost and row count are estimated based on
 * these statistics and the input relational expressions.
 *
 * <p>The cost estimation takes into account factors such as I/O costs and data shuffling costs.
 * The cost of writing a row to disk is IOCostValue, and the cost of shuffling a row
 * between nodes is shuffleCostValue.
 *
 * <p>Cost is get from 'getCost' method, which returns the total cost of the query plan, and cost consists of
 * execution cost and I/O cost.
 */
public class RelNodeCostEstimator {

  class CostInfo {
    // TODO: we may also need to add TableName field.
    Double shuffleCost;
    Double rowCount;

    public CostInfo(Double shuffleCost, Double row) {
      this.shuffleCost = shuffleCost;
      this.rowCount = row;
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

  private Map<String, Double> rouCountStat = new HashMap<>();

  private Map<String, Double> distinctStat = new HashMap<>();

  private final Double IOCostValue;

  private final Double shuffleCostValue;

  public void setStat(Map<String, Double> stat) {
    this.rouCountStat = stat;
  }

  public void setDistinctStat(Map<String, Double> distinctStat) {
    this.distinctStat = distinctStat;
  }

  public RelNodeCostEstimator(Double IOCostValue, Double shuffleCostValue) {
    this.IOCostValue = IOCostValue;
    this.shuffleCostValue = shuffleCostValue;
  }

  /**
   * Loads statistics from a JSON configuration file and stores them in internal data structures.
   *
   * <p>This method reads a JSON file from the specified path, parses its content, and extracts
   * statistical information. For each table in the JSON object, it retrieves the row count and
   * distinct counts for each column. These values are then stored in the `stat` and `distinctStat`
   * maps, respectively.
   *
   * @param configPath the path to the JSON configuration file
   */
  public void loadStatistic(String configPath) {
    try {
      String content = new String(Files.readAllBytes(Paths.get(configPath)));
      JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        String tableName = entry.getKey();
        JsonObject tableObject = entry.getValue().getAsJsonObject();

        Double rowCount = tableObject.get("RowCount").getAsDouble();

        JsonObject distinctCounts = tableObject.getAsJsonObject("DistinctCounts");

        rouCountStat.put(tableName, rowCount);

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

  /**
   * Returns the total cost of executing a relational operation.
   *
   * <p>This method computes the cost of executing a relational operation based on the input
   * relational expression. The cost is calculated as the sum of the execution cost and the I/O cost.
   * We assume that I/O only occurs at the root of the query plan (Project) where we write the output to disk.
   * So the cost is the sum of the shuffle cost of all children RelNodes and IOCostValue * row count of the root Project RelNode.
   *
   * @param rel the input relational expression
   * @return the total cost of executing the relational operation
   */
  public Double getCost(RelNode rel) {
    CostInfo executionCostInfo = getExecutionCost(rel);
    Double IOCost = executionCostInfo.rowCount * IOCostValue;
    return executionCostInfo.shuffleCost * shuffleCostValue + IOCost;
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
    Double row = rouCountStat.getOrDefault(tableName, 5.0);
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
    Double joinSize = estimateJoinSize(join, leftCost.rowCount, rightCost.rowCount);
    // The shuffle cost of a join is the maximum shuffle cost of its children because
    // in modern distributed systems, the shuffle cost is dominated by the largest shuffle.
    return new CostInfo(max(leftCost.shuffleCost, rightCost.shuffleCost), joinSize);
  }

  private List<JoinKey> getJoinKeys(LogicalJoin join) {
    List<JoinKey> joinKeys = new ArrayList<>();
    RexNode condition = join.getCondition();
    if (condition instanceof RexCall) {
      getJoinKeysFromJoinCondition((RexCall) condition, join, joinKeys);
    }
    return joinKeys;
  }

  private void getJoinKeysFromJoinCondition(RexCall call, LogicalJoin join, List<JoinKey> joinKeys) {
    if (call.getOperator().getName().equalsIgnoreCase("AND")) {
      // Process each operand of the AND separately
      for (RexNode operand : call.getOperands()) {
        if (operand instanceof RexCall) {
          getJoinKeysFromJoinCondition((RexCall) operand, join, joinKeys);
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
    List<JoinKey> joinKeys = getJoinKeys(join);
    Double selectivity = 1.0;
    for (JoinKey joinKey : joinKeys) {
      String leftTableName = joinKey.leftTableName;
      String rightTableName = joinKey.rightTableName;
      String leftFieldName = joinKey.leftFieldName;
      String rightFieldName = joinKey.rightFieldName;
      Double leftCardinality = rouCountStat.getOrDefault(leftTableName, 5.0);
      Double rightCardinality = rouCountStat.getOrDefault(rightTableName, 5.0);
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
      unionSize += inputCost.rowCount;
      unionCost = max(inputCost.shuffleCost, unionCost);
    }
    unionCost *= 2;
    return new CostInfo(unionCost, unionSize);
  }

  private CostInfo getExecutionCostProject(LogicalProject project) {
    return getExecutionCost(project.getInput());
  }
}

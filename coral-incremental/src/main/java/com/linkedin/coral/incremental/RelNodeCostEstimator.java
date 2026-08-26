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
 * The cost weight of writing a row to disk is IOCostValue, and the cost weight of execution is executionCostValue.
 *
 * <p>Cost is get from 'getCost' method, which returns the total cost of the query plan, and cost consists of
 * execution cost and I/O cost.
 */
public class RelNodeCostEstimator {

  class CostInfo {
    // TODO: we may also need to add TableName field.
    Double executionCost;
    Double outputSize;

    public CostInfo(Double executionCost, Double row) {
      this.executionCost = executionCost;
      this.outputSize = row;
    }
  }

  class TableStatistic {
    // The number of rows in the table
    Double rowCount;
    // The number of distinct values in each column
    // This doesn't work for nested columns and complex types
    Map<String, Double> distinctCountByRow;

    public TableStatistic() {
      this.distinctCountByRow = new HashMap<>();
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

  private Map<String, TableStatistic> costStatistic = new HashMap<>();

  private final Double IOCostValue;

  private final Double executionCostValue;

  public RelNodeCostEstimator(Double IOCostValue, Double executionCostValue) {
    this.IOCostValue = IOCostValue;
    this.executionCostValue = executionCostValue;
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
  public void loadStatistic(String configPath) throws IOException {
    try {
      String content = new String(Files.readAllBytes(Paths.get(configPath)));
      JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        TableStatistic tableStatistic = new TableStatistic();
        String tableName = entry.getKey();
        JsonObject tableObject = entry.getValue().getAsJsonObject();

        Double rowCount = tableObject.get("RowCount").getAsDouble();

        JsonObject distinctCounts = tableObject.getAsJsonObject("DistinctCounts");

        tableStatistic.rowCount = rowCount;

        for (Map.Entry<String, JsonElement> distinctEntry : distinctCounts.entrySet()) {
          String columnName = distinctEntry.getKey();
          Double distinctCount = distinctEntry.getValue().getAsDouble();

          tableStatistic.distinctCountByRow.put(columnName, distinctCount);
        }
        costStatistic.put(tableName, tableStatistic);

      }
    } catch (IOException e) {
      throw new IOException("Failed to load statistics from the configuration file: " + configPath, e);
    }

  }

  /**
   * Returns the total cost of executing a relational operation.
   *
   * <p>This method computes the cost of executing a relational operation based on the input
   * relational expression. The cost is calculated as the sum of the execution cost and the I/O cost.
   * We assume that I/O only occurs at the root of the query plan (Project) where we write the output to disk.
   * So the cost is the sum of the execution cost of all children RelNodes and IOCostValue * outputSize of the root Project RelNode.
   *
   * @param rel the input relational expression
   * @return the total cost of executing the relational operation
   */
  public Double getCost(RelNode rel) {
    CostInfo executionCostInfo = getExecutionCost(rel);
    Double writeCost = executionCostInfo.outputSize * IOCostValue;
    return executionCostInfo.executionCost * executionCostValue + writeCost;
  }

  private CostInfo getExecutionCost(RelNode rel) {
    if (rel instanceof TableScan) {
      return getExecutionCostTableScan((TableScan) rel);
    } else if (rel instanceof LogicalJoin) {
      return getExecutionCostJoin((LogicalJoin) rel);
    } else if (rel instanceof LogicalUnion) {
      return getExecutionCostUnion((LogicalUnion) rel);
    } else if (rel instanceof LogicalProject) {
      return getExecutionCostProject((LogicalProject) rel);
    }
    throw new IllegalArgumentException("Unsupported relational operation: " + rel.getClass().getSimpleName());
  }

  private CostInfo getExecutionCostTableScan(TableScan scan) {
    RelOptTable originalTable = scan.getTable();
    String tableName = getTableName(originalTable);
    try {
      TableStatistic tableStat = costStatistic.get(tableName);
      Double rowCount = tableStat.rowCount;
      return new CostInfo(rowCount, rowCount);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("Table statistics not found for table: " + tableName);
    }
  }

  private String getTableName(RelOptTable table) {
    return String.join(".", table.getQualifiedName());
  }

  private CostInfo getExecutionCostJoin(LogicalJoin join) {
    RelNode left = join.getLeft();
    RelNode right = join.getRight();
    CostInfo leftCost = getExecutionCost(left);
    CostInfo rightCost = getExecutionCost(right);
    Double joinSize = estimateJoinSize(join, leftCost.outputSize, rightCost.outputSize);
    // The execution cost of a join is the maximum execution cost of its children because the execution cost of a single RelNode
    // is mainly determined by the cost of the shuffle operation.
    // And in modern distributed systems, the shuffle cost is dominated by the largest shuffle.
    return new CostInfo(max(leftCost.executionCost, rightCost.executionCost), joinSize);
  }

  private List<JoinKey> getJoinKeys(LogicalJoin join) {
    List<JoinKey> joinKeys = new ArrayList<>();
    RexNode condition = join.getCondition();
    if (condition instanceof RexCall) {
      getJoinKeysFromJoinCondition((RexCall) condition, join, joinKeys);
    }
    // Assertion to check if joinKeys.size() is greater than or equal to 1
    if (joinKeys.size() < 1) {
      throw new IllegalArgumentException("Join keys size is less than 1");
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
      try {
        TableStatistic leftTableStat = costStatistic.get(leftTableName);
        TableStatistic rightTableStat = costStatistic.get(rightTableName);
        Double leftCardinality = leftTableStat.rowCount;
        Double rightCardinality = rightTableStat.rowCount;
        Double leftDistinct = leftTableStat.distinctCountByRow.getOrDefault(leftFieldName, leftCardinality);
        Double rightDistinct = rightTableStat.distinctCountByRow.getOrDefault(rightFieldName, rightCardinality);
        selectivity *= 1 / max(leftDistinct, rightDistinct);
      } catch (NullPointerException e) {
        throw new IllegalArgumentException(
            "Table statistics not found for table: " + leftTableName + " or " + rightTableName);
      }
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
      unionSize += inputCost.outputSize;
      unionCost = max(inputCost.executionCost, unionCost);
    }
    unionCost *= 2;
    return new CostInfo(unionCost, unionSize);
  }

  private CostInfo getExecutionCostProject(LogicalProject project) {
    return getExecutionCost(project.getInput());
  }
}

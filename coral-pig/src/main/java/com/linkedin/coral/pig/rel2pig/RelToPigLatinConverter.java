/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableFunctionScan;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.logical.LogicalExchange;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalIntersect;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalMatch;
import org.apache.calcite.rel.logical.LogicalMinus;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalSort;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;

import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRelNodeException;
import com.linkedin.coral.pig.rel2pig.rel.PigLogicalAggregate;
import com.linkedin.coral.pig.rel2pig.rel.PigLogicalFilter;
import com.linkedin.coral.pig.rel2pig.rel.PigLogicalJoin;
import com.linkedin.coral.pig.rel2pig.rel.PigLogicalProject;
import com.linkedin.coral.pig.rel2pig.rel.PigLogicalUnion;
import com.linkedin.coral.pig.rel2pig.rel.PigRelUtils;
import com.linkedin.coral.pig.rel2pig.rel.PigTableScan;


/**
 * RelToPigLatinConverter translates a SQL query expressed in Calcite Relational Algebra
 * into equivalent Pig Latin statement(s).
 *
 * The interface to use RelToPigLatinConverter looks something like the following:
 *   String outputRelation = "OUTPUT_RELATION";
 *   PigLoadFunction loadFunc = (String db, String table) -&gt; "LOAD_FUNCTION()";
 *   TableToPigPathFunction pathFunc = (String db, String table) -&gt; "PATH_THAT_LOAD_FUNCTION_CAN_READ";
 *   RelToPigLatinConverter converter = new RelToPigLatinConverter(loadFunc, pathFunc);
 *   String pigLatin = converter.convert(relNode, outputRelation);
 *
 * The returned "pigLatin" can be passed as a Pig Script to the Pig engine.
 *
 * For example, if we use the following parameters:
 *   outputRelation         : "view"
 *   pigLoadFunction        : PigLoadFunction loadFunc =
 *                              (String db, String table) -&gt; "dali.data.pig.DaliStorage()";
 *   tableToPigPathFunction : TableToPigPathFunction pathFunc =
 *                              (String db, String table) -&gt; String.format("dalids:///%s.%s", db, table);
 *
 * Assuming these SQL queries are correctly parsed as Calcite Relational Algebra,
 * some example translations of are the following:
 *
 * Suppose we had a table defined as:
 *   CREATE TABLE IF NOT EXISTS pig.tableA(a int, b int, c int)
 *
 * Example 1:
 *   SQL:
 *     SELECT tableA.b FROM pig.tableA AS tableA
 *   Pig Latin:
 *     view = LOAD 'dalids:///pig.tableA' USING dali.data.pig.DaliStorage() ;
 *     view = FOREACH view GENERATE b ;
 *
 * TODO(ralam): Update with more examples as functionality is incrementally built.
 */
public class RelToPigLatinConverter {

  private final PigLoadFunction pigLoadFunction;
  private final TableToPigPathFunction tableToPigPathFunction;

  public RelToPigLatinConverter(PigLoadFunction pigLoadFunction, TableToPigPathFunction tableToPigPathFunction) {
    this.pigLoadFunction = pigLoadFunction;
    this.tableToPigPathFunction = tableToPigPathFunction;
  }

  /**
   * Converts a SQL query represented in Calcite Relational Algebra, root, to Pig Latin
   * where the final output of the table is stored in the alias, outputRelation.
   *
   * @param root Root node of the SQL query
   * @param outputRelation The alias of the variable that the SQL query is to be dumped
   * @return Pig Latin equivalent of the SQL query
   */
  public String convert(RelNode root, String outputRelation) {
    final RelToPigBuilder context = new RelToPigBuilder();
    context.addFunctionDefinitions(PigRelUtils.getAllFunctionDefinitions(root));
    visit(context, root, outputRelation);
    return context.getScript();
  }

  /**
   * Delegates RelNodes to its specific RelNode type handler
   *
   * @param relNode input RelNode
   * @param outputRelation variable where RelNode operation output will be stored
   * @return Pig Latin script for all operations in the DAG of RelNodes with root relNode
   */
  private void visit(RelToPigBuilder state, RelNode relNode, String outputRelation) {

    //TODO(ralam): Add more supported types.
    if (relNode instanceof TableScan) {
      visit(state, (TableScan) relNode, outputRelation);
    } else if (relNode instanceof LogicalFilter) {
      visit(state, (LogicalFilter) relNode, outputRelation);
    } else if (relNode instanceof LogicalProject) {
      visit(state, (LogicalProject) relNode, outputRelation);
    } else if (relNode instanceof LogicalAggregate) {
      visit(state, (LogicalAggregate) relNode, outputRelation);
    } else if (relNode instanceof LogicalJoin) {
      visit(state, (LogicalJoin) relNode, outputRelation);
    } else if (relNode instanceof LogicalUnion) {
      visit(state, (LogicalUnion) relNode, outputRelation);
    }
  }

  /**
   * Generates Pig Latin to perform a TableScan.
   *
   * @param state Intermediate state of the query translation
   * @param tableScan TableScan node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, TableScan tableScan, String outputRelation) {
    state.addStatement(PigTableScan.getScript(tableScan, outputRelation, pigLoadFunction, tableToPigPathFunction));
  }

  private void visit(RelToPigBuilder state, TableFunctionScan tableFunctionScan, String outputRelation) {
    throw new UnsupportedRelNodeException(tableFunctionScan);
  }

  private void visit(RelToPigBuilder state, LogicalValues logicalValues, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalValues);
  }

  /**
   * Generates Pig Latin to perform a TableScan.
   *
   * @param state Intermediate state of the query translation
   * @param logicalFilter LogicalFilter node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalFilter logicalFilter, String outputRelation) {
    visit(state, logicalFilter.getInput(), outputRelation);
    state.addStatement(PigLogicalFilter.getScript(logicalFilter, outputRelation, outputRelation));
  }

  /**
   * Generates Pig Latin to perform a LogicalProject.
   *
   * @param state Intermediate state of the query translation
   * @param logicalProject LogicalProject node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalProject logicalProject, String outputRelation) {
    visit(state, logicalProject.getInput(), outputRelation);
    state.addStatement(PigLogicalProject.getScript(logicalProject, outputRelation, outputRelation));
  }

  /**
   * Generates Pig Latin to perform a LogicalJoin.
   *
   * @param state Intermediate state of the query translation
   * @param logicalJoin LogicalJoin node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalJoin logicalJoin, String outputRelation) {
    final String leftInputRelation = state.getUniqueAlias();
    visit(state, logicalJoin.getLeft(), leftInputRelation);

    final String rightInputRelation = state.getUniqueAlias();
    visit(state, logicalJoin.getRight(), rightInputRelation);

    state.addStatement(PigLogicalJoin.getScript(logicalJoin, outputRelation, leftInputRelation, rightInputRelation));
  }

  private void visit(RelToPigBuilder state, LogicalCorrelate logicalCorrelate, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalCorrelate);
  }

  /**
   * Generates Pig Latin to perform a LogicalUnion.
   *
   * @param state Intermediate state of the query translation
   * @param logicalUnion LogicalUnion node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalUnion logicalUnion, String outputRelation) {
    List<String> inputRelations = logicalUnion.getInputs().stream().map(input -> {
      String inputRelation = state.getUniqueAlias();
      visit(state, input, inputRelation);
      return inputRelation;
    }).collect(Collectors.toList());
    state.addStatement(PigLogicalUnion.getScript(logicalUnion, outputRelation, inputRelations));
  }

  private void visit(RelToPigBuilder state, LogicalIntersect logicalIntersect, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalIntersect);
  }

  private void visit(RelToPigBuilder state, LogicalMinus logicalMinus, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalMinus);
  }

  /**
   * Generates Pig Latin to perform a LogicalAggregate.
   *
   * @param state Intermediary state of the query translation
   * @param logicalAggregate LogicalAggregate node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalAggregate logicalAggregate, String outputRelation) {
    visit(state, logicalAggregate.getInput(), outputRelation);
    state.addStatement(PigLogicalAggregate.getScript(logicalAggregate, outputRelation, outputRelation));
  }

  private void visit(RelToPigBuilder state, LogicalMatch logicalMatch, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalMatch);
  }

  private void visit(RelToPigBuilder state, LogicalSort logicalSort, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalSort);
  }

  private void visit(RelToPigBuilder state, LogicalExchange logicalExchange, String outputRelation) {
    throw new UnsupportedRelNodeException(logicalExchange);
  }
}

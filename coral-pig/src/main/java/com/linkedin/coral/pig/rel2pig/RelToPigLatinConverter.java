package com.linkedin.coral.pig.rel2pig;

import com.linkedin.coral.pig.rel2pig.rel.PigLogicalFilter;
import com.linkedin.coral.pig.rel2pig.rel.PigLogicalProject;
import com.linkedin.coral.pig.rel2pig.rel.PigTableScan;
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


/**
 * RelToPigLatinConverter translates a SQL query expressed in Calcite Relational Algebra
 * into equivalent Pig Latin statement(s).
 *
 * The interface to use RelToPigLatinConverter looks something like the following:
 *   String outputRelation = "OUTPUT_RELATION";
 *   PigLoadFunction loadFunc = (String db, String table) -> "LOAD_FUNCTION()";
 *   TableToPigPathFunction pathFunc = (String db, String table) -> "PATH_THAT_LOAD_FUNCTION_CAN_READ";
 *   RelToPigLatinConverter converter = new RelToPigLatinConverter(loadFunc, pathFunc);
 *   String pigLatin = converter.convert(relNode, outputRelation);
 *
 * The returned "pigLatin" can be passed as a Pig Script to the Pig engine.
 *
 * For example, if we use the following parameters:
 *   outputRelation         : "view"
 *   pigLoadFunction        : PigLoadFunction loadFunc =
 *                              (String db, String table) -> "dali.data.pig.DaliStorage()";
 *   tableToPigPathFunction : TableToPigPathFunction pathFunc =
 *                              (String db, String table) -> String.format("dalids:///%s.%s", db, table);
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

  private PigLoadFunction pigLoadFunction;
  private TableToPigPathFunction tableToPigPathFunction;

  public RelToPigLatinConverter(PigLoadFunction pigLoadFunction, TableToPigPathFunction tableToPigPathFunction) {
    this.pigLoadFunction = pigLoadFunction;
    this.tableToPigPathFunction = tableToPigPathFunction;
  }

  /**
   * Converts a SQL query represented in Calcite Relational Algebra, root, to Pig Latin
   * where the final output of the table is stored in the alias, outputRelation.
   * @param root Root node of the SQL query
   * @param outputRelation The alias of the variable that the SQL query is to be dumped
   * @return Pig Latin equivalent of the SQL query
   */
  public String convert(RelNode root, String outputRelation) {
    RelToPigBuilder context = new RelToPigBuilder();
    visit(context, root, outputRelation);
    return context.getScript();
  }

  /**
   * Delegates RelNodes to its specific RelNode type handler
   * @param var1 input RelNode
   * @param outputRelation variable where RelNode operation output will be stored
   * @return Pig Latin script for all operations in the DAG of RelNodes with root var1
   */
  private void visit(RelToPigBuilder state, RelNode var1, String outputRelation) {

    //TODO(ralam): Add more supported types.
    if (var1 instanceof TableScan) {
      visit(state, (TableScan) var1, outputRelation);
    } else if (var1 instanceof LogicalFilter) {
      visit(state, (LogicalFilter) var1, outputRelation);
    } else if (var1 instanceof LogicalProject) {
      visit(state, (LogicalProject) var1, outputRelation);
    }
  }

  /**
   * Generates Pig Latin to perform a TableScan.
   * @param state Intermediate state of the query translation
   * @param tableScan TableScan node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, TableScan tableScan, String outputRelation) {
    state.addStatement(PigTableScan.getScript(tableScan, outputRelation, pigLoadFunction, tableToPigPathFunction));
  }

  private void visit(RelToPigBuilder state, TableFunctionScan var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalValues var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  /**
   * Generates Pig Latin to perform a TableScan.
   * @param state Intermediate state of the query translation
   * @param logicalFilter LogicalFilter node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalFilter logicalFilter, String outputRelation) {
    visit(state, logicalFilter.getInput(), outputRelation);
    state.addStatement(PigLogicalFilter.getScript(logicalFilter, outputRelation, outputRelation));
  }

  /**
   * Generates Pig Latin to perform a TableScan.
   * @param state Intermediate state of the query translation
   * @param logicalProject LogicalProject node
   * @param outputRelation name of the variable to be outputted
   */
  private void visit(RelToPigBuilder state, LogicalProject logicalProject, String outputRelation) {
    visit(state, logicalProject.getInput(), outputRelation);
    state.addStatement(PigLogicalProject.getScript(logicalProject, outputRelation, outputRelation));
  }

  private void visit(RelToPigBuilder state, LogicalJoin var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalCorrelate var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalUnion var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalIntersect var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalMinus var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalAggregate var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalMatch var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalSort var1, String outputRelation) {
    //TODO(ralam): Implement function
  }

  private void visit(RelToPigBuilder state, LogicalExchange var1, String outputRelation) {
    //TODO(ralam): Implement function
  }
}

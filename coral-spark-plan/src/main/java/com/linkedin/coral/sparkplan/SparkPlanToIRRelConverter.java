/**
 * Copyright 2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.sparkplan;

import com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveSchema;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.sparkplan.containers.SparkPlanNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.tools.RelBuilder;

import static com.google.common.base.Preconditions.*;
import static com.linkedin.coral.sparkplan.containers.SparkPlanNode.PLANTYPE.*;


/**
 * This class converts Spark Optimized Logical Plan to an IR RelNode
 */
public class SparkPlanToIRRelConverter {

  private static RelContextProvider relContextProvider;
  private static HiveToRelConverter hiveToRelConverter;

  /**
   * Initializes converter and provider with hive configuration at provided path
   *
   * @param mscClient HiveMetaStoreClient. Hive metastore client provides small subset
   *                  of methods provided by Hive's metastore client interface.
   */
  public static SparkPlanToIRRelConverter create(HiveMetastoreClient mscClient) {
    checkNotNull(mscClient);
    HiveSchema schema = new HiveSchema(mscClient);
    RelContextProvider relContextProvider = new RelContextProvider(schema);
    SparkPlanToIRRelConverter.hiveToRelConverter = HiveToRelConverter.create(mscClient);
    return new SparkPlanToIRRelConverter(relContextProvider);
  }

  private SparkPlanToIRRelConverter(RelContextProvider relContextProvider) {
    checkNotNull(relContextProvider);
    SparkPlanToIRRelConverter.relContextProvider = relContextProvider;
  }

  /**
   * This API is used to convert Spark scan plans to IR RelNodes
   *
   * @param plan Spark logical or physical plan string with correct indents
   * @return Map containing the Spark scan plan information,
   * whose key is scan node itself, and the value is the nullable predicate node pushed down to that scan node
   */
  public Map<RelNode, RelNode> convertScanNodes(String plan) {
    return traverse(constructDAG(plan));
  }

  /**
   * This API is used to judge if there is complicated predicate which is pushed down to scan node in given plan
   * @param plan Spark logical or physical plan string with correct indents
   * @return true if there is complicated predicate pushed down to scan node in given plan, false otherwise
   */
  public boolean hasComplicatedPredicatePushedDown(String plan) {
    Map<RelNode, RelNode> relNodeMap = convertScanNodes(plan);
    for (RelNode scanNode : relNodeMap.keySet()) {
      RelNode predicateNode = relNodeMap.get(scanNode);
      if (predicateNode != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * This API is used to construct a DAG (directed acyclic graph) using input plan according to the indents
   * For example,
   * Using this plan:
   * +- Project [area_code#64, code#65]
   *    +- Filter (isnotnull(country#63) && (country#63 = US))
   *        +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65]
   * We can construct the Map {2:1, 1:0} where we use position (0, 1, 2) to represent each plan node
   *
   * @param plan Spark optimized logical plan or physical plan
   * @return Map<SparkPlanNode, SparkPlanNode>, whose key is the child node, value is the child's parent node
   */
  private static Map<SparkPlanNode, SparkPlanNode> constructDAG(String plan) {
    Map<SparkPlanNode, SparkPlanNode> dag = new HashMap<>();
    Map<Integer, Integer> indent = new HashMap<>();
    Set<Integer> parentNotFound = new HashSet<>();
    String[] lines = plan.split("\n");
    for (int i = lines.length - 1; i >= 0; --i) {
      indent.put(i, lines[i].indexOf("-"));
      Set<Integer> toRemove = new HashSet<>();
      for (int child : parentNotFound) {
        if (indent.get(i) < indent.get(child)) {
          String childDescription = lines[child].substring(indent.get(child) + 1).trim();
          String parentDescription = lines[i].substring(indent.get(i) + 1).trim();
          SparkPlanNode childNode = new SparkPlanNode(child, childDescription, UNDEFINED);
          SparkPlanNode parentNode = new SparkPlanNode(i, parentDescription, UNDEFINED);
          dag.put(childNode, parentNode);
          toRemove.add(child);
        }
      }
      parentNotFound.removeAll(toRemove);
      parentNotFound.add(i);
    }
    return dag;
  }

  /**
   * This API is used to traverse the DAG using toposort:
   * start from the node without child and with greatest position number,
   * visit it and update [[childrenOfNode]] and [[nodesToVisit]], repeat till all nodes are visited
   * For example,
   * Using this plan:
   * +- Project [area_code#64, code#65]
   *    +- Filter (isnotnull(country#63) && (country#63 = US))
   *        +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65]
   * After constructing the Map dag {2:1, 1:0} where we use position (0, 1, 2) to represent each plan node,
   * we need to visit nodes in 2->1->0 sequence
   *
   * @param dag whose key is the child node, value is the child's parent node
   * @return Map<RelNode, RelNode> containing the Spark scan plan information,
   * whose key is scan node itself, and the value is the nullable predicate node pushed down to that scan node
   */
  private static Map<RelNode, RelNode> traverse(Map<SparkPlanNode, SparkPlanNode> dag) {
    Map<SparkPlanNode, Set<SparkPlanNode>> childrenOfNode = new HashMap<>();
    Set<SparkPlanNode> nodesToVisit = new HashSet<>();
    Map<RelNode, RelNode> relNodeMap = new LinkedHashMap<>();
    for (SparkPlanNode child : dag.keySet()) {
      SparkPlanNode parent = dag.get(child);
      childrenOfNode.putIfAbsent(parent, new HashSet<>());
      childrenOfNode.get(parent).add(child);
      nodesToVisit.add(child);
      nodesToVisit.add(parent);
    }
    while (!nodesToVisit.isEmpty()) {
      SparkPlanNode nodeToVisit = null;
      for (SparkPlanNode node : nodesToVisit) {
        if (childrenOfNode.getOrDefault(node, new HashSet<>()).isEmpty() && (nodeToVisit == null
            || nodeToVisit.getPosition() < node.getPosition())) {
          nodeToVisit = node;
        }
      }
      if (nodeToVisit != null) {
        if (dag.containsKey(nodeToVisit)) {
          SparkPlanNode parent = dag.get(nodeToVisit);
          childrenOfNode.get(parent).remove(nodeToVisit);
        }
        nodesToVisit.remove(nodeToVisit);
        visit(nodeToVisit, relNodeMap);
      } else {
        throw new NullPointerException("nodeToVisit is null");
      }
    }
    return relNodeMap;
  }

  /**
   * This API is used to visit scan SparkPlanNode, and parse the description and update relNodeMap
   *
   * @param nodeToVisit current node needs to be visited
   * @param relNodeMap contains RelNodes and corresponding predicates before current node
   */
  private static void visit(SparkPlanNode nodeToVisit, Map<RelNode, RelNode> relNodeMap) {
    String description = nodeToVisit.getDescription();
    final Pattern noAlphaPattern = Pattern.compile("[^A-Za-z]");
    Matcher matcher = noAlphaPattern.matcher(description);
    if (matcher.find()) {
      String firstWord = description.substring(0, matcher.start());
      // TODO: Implement parser for each type
      switch (firstWord) {
        case "HiveTableRelation":
        case "HiveTableScan":
          visitScanNode(nodeToVisit, relNodeMap);
          break;
        case "Filter":
          System.out.println("Filter");
          break;
        case "Project":
          System.out.println("Project");
          break;
        case "Join":
          System.out.println("Join");
          break;
        default:
          System.out.println("Undefined");
          break;
      }
    }
  }

  /**
   * This API is used to parse the logical or physical plan's scan nodes to get the RelNodes and add them to [[relNodeMap]]
   * For example,
   * Using this scan node:
   *  +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65]
   * We need to add a LogicalScan RelNode and null as key and value to [[relNodeMap]] because there is no complex predicate
   * pushed down in this scan node
   *
   * @param nodeToVisit Spark scan node we need to parse
   * @param relNodeMap contains RelNodes and corresponding predicates before current node
   */
  private static void visitScanNode(SparkPlanNode nodeToVisit, Map<RelNode, RelNode> relNodeMap) {
    nodeToVisit.setPlanType(SCAN);
    String description = nodeToVisit.getDescription();
    Pattern namePattern = Pattern.compile("`(.*?)`\\.`(.*?)`");
    Matcher nameMatcher = namePattern.matcher(description);
    if (nameMatcher.find()) {
      String databaseName = nameMatcher.group(1);
      String tableName = nameMatcher.group(2);
      RelBuilder relBuilder = relContextProvider.getRelBuilder();
      // TODO: Avoid hard-coded 'hive' here
      RelNode scanRelNode = relBuilder.scan(ImmutableList.of("hive", databaseName, tableName)).build();
      relNodeMap.put(scanRelNode, null);
      relBuilder.clear();

      Pattern innerInfoPattern = Pattern.compile("\\[(.*?)]"); // find all [...]
      Matcher innerInfoMatcher = innerInfoPattern.matcher(description);
      List<String> potentialComplexPredicates = new ArrayList<>();
      while (innerInfoMatcher.find()) {
        String innerInfo = innerInfoMatcher.group(1);
        if (innerInfo.startsWith("(") || !containsOnlyFieldNames(innerInfo)) {
          potentialComplexPredicates.add(innerInfo);
        }
      }
      if (potentialComplexPredicates.size() == 1) {
        String filterCondition = potentialComplexPredicates.get(0);
        filterCondition = filterCondition.replaceAll("#\\d+\\w*", "");
        String sql = "SELECT * FROM " + databaseName + "." + tableName + " WHERE " + filterCondition;
        RelNode convertedNode = hiveToRelConverter.convertSql(sql);
        RexNode rexNode = convertedNode.getInput(0).getChildExps().get(0);
        RelNode predicateNode = relBuilder.push(scanRelNode).filter(rexNode).build();
        relNodeMap.put(scanRelNode, predicateNode);
        relBuilder.clear();
      }
    }
  }

  /**
   * This API is used to judge if the string [[info]] only contains field names of the table, if so, it is not a
   * complex predicate
   * @param info string we need to judge
   * @return true if it only contains field names like 'datepartition#20, engagement_p1#12, engagement_p2_detailed#15, id#17L'
   * otherwise, return false if it contains other things like '(datediff(18429, cast(substring(datepartition#20, 0, 10) as date)) <= 365)'
   */
  private static boolean containsOnlyFieldNames(String info) {
    String[] infos = info.split(", ");
    for (String word : infos) {
      if (!word.matches("\\w[\\w_\\d]*#\\d+\\w*")) {
        return false;
      }
    }
    return true;
  }
}

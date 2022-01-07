/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.sparkplan;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.sparkplan.containers.SparkPlanNode;

import static com.google.common.base.Preconditions.*;
import static com.linkedin.coral.sparkplan.containers.SparkPlanNode.PLANTYPE.*;


/**
 * This class converts a Spark Plan to an IR RelNode
 */
public class SparkPlanToIRRelConverter {

  private static HiveToRelConverter hiveToRelConverter;

  // this set contains all the simple predicate operators
  private static final Set<String> SIMPLE_PREDICATE_OPERATORS = new HashSet<>(
      Arrays.asList("=", ">", ">=", "<", "<=", "in", "is null", "is not null", "and", "or", "not", "like"));

  /**
   * Constructor for converter given Hive metastore data at provided localMetastorePath
   *
   * @param hiveMetastoreClient HiveMetaStoreClient. Hive metastore client provides small subset
   *                  of methods provided by Hive's metastore client interface.
   * @return {@link SparkPlanToIRRelConverter}
   */
  private SparkPlanToIRRelConverter(HiveMetastoreClient hiveMetastoreClient) {
    checkNotNull(hiveMetastoreClient);
    SparkPlanToIRRelConverter.hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
  }

  /**
   * Constructor for converter given Hive metastore data in localMetastorePath
   *
   * @param localMetastore In-memory Hive metastore represented in a map.
   * @return {@link SparkPlanToIRRelConverter}
   */
  private SparkPlanToIRRelConverter(Map<String, Map<String, List<String>>> localMetastore) {
    checkNotNull(localMetastore);
    SparkPlanToIRRelConverter.hiveToRelConverter = new HiveToRelConverter(localMetastore);
  }

  /**
   * Initializes converter using HiveMetastoreClient
   * @param hiveMetastoreClient
   * @return {@link SparkPlanToIRRelConverter}
   */
  public static SparkPlanToIRRelConverter create(HiveMetastoreClient hiveMetastoreClient) {
    checkNotNull(hiveMetastoreClient);
    return new SparkPlanToIRRelConverter(hiveMetastoreClient);
  }

  /**
   * Initializes converter and provider with localMetastorePath, which points to a json file
   * @param localMetastorePath path of local metastore json file, containing all the required
   *                           metadata (database name, table name, column name and type)
   * @return {@link SparkPlanToIRRelConverter}
   * @throws IOException when creating a buffer {@link Reader}
   */
  public static SparkPlanToIRRelConverter create(String localMetastorePath) throws IOException {
    checkNotNull(localMetastorePath);
    Gson gson = new Gson();
    Reader reader = Files.newBufferedReader(Paths.get(localMetastorePath));
    Map<String, Map<String, List<String>>> localMetastore = gson.fromJson(reader, Map.class);
    reader.close();
    return new SparkPlanToIRRelConverter(localMetastore);
  }

  /**
   * This API is used to get the complicated predicate push down in scan nodes
   *
   * @param plan well-formed Spark logical or physical plan string
   * @return a map whose key is table name and value is a list of predicate information of that table
   */
  public Map<String, List<String>> getComplicatedPredicatePushedDownInfo(String plan) {
    try {
      return traverse(constructDAG(plan));
    } catch (Exception e) {
      Map<String, List<String>> errorMap = new HashMap<>();
      errorMap.put("Error!", Collections.singletonList(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace())));
      return errorMap;
    }
  }

  /**
   * This API is used to judge if there is complicated predicate which is pushed down to scan node in given plan
   * @param plan well-formed Spark logical or physical plan string
   * @return "Yes" if there is complicated predicate pushed down to scan node in given plan; "No" if there is a
   * non-complicated predicate pushed down to scan node in given plan; "Fail to judge" if all the scan nodes in this plan can't be handled
   */
  public String containsComplicatedPredicatePushedDown(String plan) {
    try {
      Map<String, List<String>> predicateInfoMap = getComplicatedPredicatePushedDownInfo(plan);
      int failedTableCount = 0;
      List<String> failedReasons = new LinkedList<>();
      for (String databaseTableName : predicateInfoMap.keySet()) {
        final List<String> predicateInfos = predicateInfoMap.get(databaseTableName);
        for (String predicateInfo : predicateInfos) {
          if (predicateInfo.startsWith("Complicated")) {
            return "Yes";
          } else if (predicateInfo.startsWith("Simple") || predicateInfo.startsWith("No")) {
            return "No";
          } else if (predicateInfo.startsWith("Exception")) {
            failedTableCount++;
            failedReasons.add(databaseTableName + "(" + predicateInfo + ")");
          }
        }
      }
      return failedTableCount == 0 ? "No" : "Fail to judge, reasons: " + String.join(", ", failedReasons);
    } catch (RuntimeException e) {
      e.printStackTrace(System.err);
      return "Fail to judge, reason: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace());
    }
  }

  /**
   * This API is used to judge if the given node contains only simple predicates
   * which are =, >, >=, <, <=, in, isnull, isnotnull, and, or, not, startswith, endswith, contains
   * For example,
   * the node representing '[((code#65 NOT IN (12, 13) AND isnotnull(code#65))
   * OR (StartsWith(name#62, 'LA') AND EndsWith(name#62, 'X') AND Contains(name#62, 'Y') AND isnull(area_code#64)))]'
   * contains only simple predicates;
   * however, the node representing '[(datediff(18429, cast(substring(datepartition#66, 0, 10) as date)) <= 365)]'
   * doesn't contain only simple predicates because 'datediff' and 'substring' are not simple predicate operators
   *
   * @param condition node we need to judge if it contains only simple predicates
   * @return true if the node contains only simple predicates, false otherwise
   */
  private boolean containsOnlySimplePredicates(RexNode condition) {
    if (condition == null) {
      return true;
    }
    Queue<RexNode> queue = new LinkedList<>();
    queue.add(condition);
    while (!queue.isEmpty()) { // breadth first search
      RexNode rexNode = queue.poll();
      if (rexNode instanceof RexCall) {
        String operatorName = ((RexCall) rexNode).getOperator().getName().toLowerCase();
        if (!SIMPLE_PREDICATE_OPERATORS.contains(operatorName)) {
          return false;
        }
        queue.addAll(((RexCall) rexNode).getOperands());
      }
    }
    return true;
  }

  /**
   * This API is used to construct a DAG (directed acyclic graph) using input plan according to the indents
   * For example,
   * using this plan:
   * +- Project [area_code#64, code#65]
   *    +- Filter (isnotnull(country#63) && (country#63 = US))
   *        +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65]
   * we can construct the Map {2:1, 1:0} where we use position (0, 1, 2) to represent each plan node
   *
   * @param plan Spark optimized logical plan or physical plan
   * @return a map whose key is the child node, value is the child's parent node
   */
  private Map<SparkPlanNode, SparkPlanNode> constructDAG(String plan) {
    Map<SparkPlanNode, SparkPlanNode> dag = new HashMap<>();
    Map<Integer, Integer> indent = new HashMap<>();
    Set<Integer> parentNotFound = new HashSet<>();
    final List<String> lines =
        Arrays.stream(plan.split("\n")).filter(line -> line.contains("-")).collect(Collectors.toList());
    for (int i = lines.size() - 1; i >= 0; --i) {
      int dashIndex = lines.get(i).indexOf("-");
      if (dashIndex == -1) {
        continue;
      }
      indent.put(i, dashIndex);
      Set<Integer> toRemove = new HashSet<>();
      for (int child : parentNotFound) {
        if (indent.get(i) < indent.get(child)) {
          String childDescription = lines.get(child).substring(indent.get(child) + 1).trim();
          String parentDescription = lines.get(i).substring(indent.get(i) + 1).trim();
          SparkPlanNode childNode = new SparkPlanNode(child, childDescription, UNDEFINED);
          SparkPlanNode parentNode = new SparkPlanNode(i, parentDescription, UNDEFINED);
          dag.put(childNode, parentNode);
          toRemove.add(child);
        }
      }
      parentNotFound.removeAll(toRemove);
      parentNotFound.add(i);
    }
    if (dag.isEmpty() && lines.size() == 1) {
      dag.put(new SparkPlanNode(0, lines.get(0), UNDEFINED), null);
    }
    return dag;
  }

  /**
   * This API is used to traverse the DAG using toposort:
   * start from the node without child and with greatest position number,
   * visit it and update [[childrenOfNode]] and [[nodesToVisit]], repeat till all nodes are visited
   * For example,
   * using this plan:
   * +- Project [area_code#64, code#65]
   *    +- Filter (isnotnull(country#63) && (country#63 = US))
   *        +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65]
   * after constructing the Map dag {2:1, 1:0} where we use position (0, 1, 2) to represent each plan node,
   * we need to visit nodes in 2->1->0 sequence
   *
   * @param dag whose key is the child node, value is the child's parent node
   */
  private Map<String, List<String>> traverse(Map<SparkPlanNode, SparkPlanNode> dag) {
    Map<SparkPlanNode, Set<SparkPlanNode>> childrenOfNode = new HashMap<>();
    Set<SparkPlanNode> nodesToVisit = new HashSet<>();
    Map<String, List<String>> predicateInfoMap = new LinkedHashMap<>();
    for (SparkPlanNode child : dag.keySet()) {
      SparkPlanNode parent = dag.get(child);
      childrenOfNode.putIfAbsent(parent, new HashSet<>());
      childrenOfNode.get(parent).add(child);
      nodesToVisit.add(child);
      if (parent != null) {
        nodesToVisit.add(parent);
      }
    }
    while (!nodesToVisit.isEmpty()) {
      SparkPlanNode nodeToVisit = null;
      for (SparkPlanNode node : nodesToVisit) {
        if (childrenOfNode.getOrDefault(node, new HashSet<>()).isEmpty()
            && (nodeToVisit == null || nodeToVisit.getPosition() < node.getPosition())) {
          nodeToVisit = node;
        }
      }
      if (nodeToVisit != null) {
        if (dag.containsKey(nodeToVisit)) {
          SparkPlanNode parent = dag.get(nodeToVisit);
          childrenOfNode.get(parent).remove(nodeToVisit);
        }
        nodesToVisit.remove(nodeToVisit);
        try {
          visitScanNode(nodeToVisit, predicateInfoMap);
        } catch (Exception e) {
          String description = nodeToVisit.getDescription();
          predicateInfoMap.putIfAbsent(description, new LinkedList<>());
          predicateInfoMap.get(description)
              .add("Failed to judge, reason: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
      }
    }
    return predicateInfoMap;
  }

  /**
   * This API is used to visit scan SparkPlanNode, and parse the description and update predicateInfoMap
   *
   * @param nodeToVisit current node needs to be visited
   * @param predicateInfoMap a map whose key is table name and value is a list of predicate information of that table
   */
  private void visitScanNode(SparkPlanNode nodeToVisit, Map<String, List<String>> predicateInfoMap) {
    String description = nodeToVisit.getDescription();
    final String firstWord = getFirstWord(description);
    if (firstWord.length() > 0) {
      switch (firstWord) {
        case "HiveTableRelation":
        case "HiveTableScan":
          visitScanNode(nodeToVisit, predicateInfoMap, HIVE_SCAN);
          break;
        case "FileScan":
          visitScanNode(nodeToVisit, predicateInfoMap, FILE_SCAN);
          break;
        default:
      }
    }
  }

  /**
   * This API is used to get the first word from a string
   * @param s string from which we want to get the first word
   */
  private String getFirstWord(String s) {
    StringBuilder firstWord = new StringBuilder();
    boolean wordStarts = false;
    for (char c : s.toCharArray()) {
      if (Character.isAlphabetic(c)) {
        wordStarts = true;
        firstWord.append(c);
      } else {
        if (wordStarts) {
          break;
        }
      }
    }
    return firstWord.toString();
  }

  /**
   * This API is used to parse the logical or physical plan's scan nodes to get the predicate information and add that to [[predicateInfoMap]]
   * For example,
   * using this scan node:
   * +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65]
   * we need to add 'No Predicate' to the value list of 'default.airports' in [[predicateInfoMap]] because there is no predicate
   * pushed down in this scan node;
   * however, for this scan node:
   * '+-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe,
   * [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66],
   * [(datediff(18429, cast(substring(datepartition#66, 0, 10) as date)) <= 365)]'
   * we need to add 'Complicated Predicate: [(datediff(18429, cast(substring(datepartition#66, 0, 10) as date)) <= 365)]'
   * to the value list of 'test.airport' in [[predicateInfoMap]] because it is a complicated predicate
   *
   * @param nodeToVisit Spark scan node we need to parse
   * @param predicateInfoMap a map whose key is table name and value is a list of predicate information of that table
   * @param scanType type of the scan
   */
  private void visitScanNode(SparkPlanNode nodeToVisit, Map<String, List<String>> predicateInfoMap,
      SparkPlanNode.PLANTYPE scanType) {
    nodeToVisit.setPlanType(scanType);
    String description = nodeToVisit.getDescription();
    Pattern namePattern = null;
    if (scanType == HIVE_SCAN) {
      namePattern = Pattern.compile("`([\\w_\\d]*?)`\\.`([\\w_\\d]*?)`");
    } else if (scanType == FILE_SCAN) {
      namePattern = Pattern.compile("([\\w_\\d]*?)\\.([\\w_\\d]*?)\\[");
    }
    if (namePattern == null) {
      return;
    }
    Matcher nameMatcher = namePattern.matcher(description);
    if (nameMatcher.find()) {
      String databaseName = nameMatcher.group(1);
      String tableName = nameMatcher.group(2);
      String databaseTableName = databaseName + "." + tableName;
      predicateInfoMap.putIfAbsent(databaseTableName, new LinkedList<>());
      String exceptionPredicate = null;
      try {
        Pattern innerInfoPattern = null;
        if (scanType == HIVE_SCAN) {
          innerInfoPattern = Pattern.compile("\\[(.*?)]");
        } else {
          innerInfoPattern = Pattern.compile("PartitionFilters: \\[(.*?)],");
        }
        Matcher innerInfoMatcher = innerInfoPattern.matcher(description);
        List<String> potentialComplexPredicates = new ArrayList<>();
        while (innerInfoMatcher.find()) {
          String innerInfo = innerInfoMatcher.group(1);
          if (!containsOnlyFieldNames(innerInfo) && !"".equals(innerInfo.trim())) {
            potentialComplexPredicates.add(innerInfo);
          }
        }
        if (potentialComplexPredicates.size() == 1) {
          String filterCondition = potentialComplexPredicates.get(0).trim();
          exceptionPredicate = filterCondition;
          String modifiedFilterCondition = modifyFilterConditionString(filterCondition);
          RexNode rexNode = getRexNode(databaseName, tableName, modifiedFilterCondition);
          if (!containsOnlySimplePredicates(rexNode)) {
            predicateInfoMap.get(databaseTableName).add("Complicated Predicate: [" + filterCondition + "]");
          } else {
            predicateInfoMap.get(databaseTableName).add("Simple Predicate: [" + filterCondition + "]");
          }
        } else {
          predicateInfoMap.get(databaseTableName).add("No Predicate");
        }
      } catch (RuntimeException e) {
        e.printStackTrace();
        predicateInfoMap.get(databaseTableName)
            .add("Exception: " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e)
                + (exceptionPredicate == null ? "" : "\n" + "Predicate: [" + exceptionPredicate + "]"));
      }
    }
  }

  private RexNode getRexNode(String databaseName, String tableName, String modifiedFilterCondition) {
    if ("".equals(modifiedFilterCondition.trim())) {
      return null;
    }
    String sql = "SELECT * FROM " + databaseName + "." + tableName + " WHERE " + modifiedFilterCondition;
    RelNode convertedNode = hiveToRelConverter.convertSql(sql);
    return convertedNode.getInput(0).getChildExps().get(0);
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
      if (!word.matches("\\w[\\w_\\d]*#\\d+\\w*") && !word.startsWith("...")) {
        return false;
      }
    }
    return true;
  }

  /**
   * This API is used to modify the filterCondition to make sure it could be converted by Coral-hive
   * At present, we need to make the following changes:
   * EqualTo(field, value) -> field = value
   * StartsWith(field, value) -> field like 'value%',
   * EndsWith(field, value) -> field like '%value',
   * Contains(field, value) -> field like '%value%'
   * In(field, [value1, value2, value3, ...]) -> field in ('value1', 'value2', 'value3'...)
   * GreaterThan(field, value) -> field > value
   * LessThan(field, value) -> field < value
   * Besides, we need to replace the delimiter of different conditions from ',' to 'AND'
   * @param filterCondition condition string we need to modify
   * @return modified string which can be converted by Coral-hive
   */
  private static String modifyFilterConditionString(String filterCondition) {
    filterCondition = filterCondition.replaceAll("#\\d+\\w*", "");
    Pattern equalToPattern = Pattern.compile("[Ee]qual[Tt]o\\((.*?),(.*?)\\)");
    Matcher equalToMatcher = equalToPattern.matcher(filterCondition);
    while (equalToMatcher.find()) {
      filterCondition = filterCondition.replace(equalToMatcher.group(0),
          equalToMatcher.group(1).trim() + " = '" + equalToMatcher.group(2).trim() + "'");
    }
    Pattern startsWithPattern = Pattern.compile("[Ss]tarts[Ww]ith\\((.*?),(.*?)\\)");
    Matcher startsWithMatcher = startsWithPattern.matcher(filterCondition);
    while (startsWithMatcher.find()) {
      filterCondition = filterCondition.replace(startsWithMatcher.group(0),
          startsWithMatcher.group(1).trim() + " LIKE '" + startsWithMatcher.group(2).trim() + "%'");
    }
    Pattern endsWithPattern = Pattern.compile("[Ee]nds[Ww]ith\\((.*?),(.*?)\\)");
    Matcher endsWithMatcher = endsWithPattern.matcher(filterCondition);
    while (endsWithMatcher.find()) {
      filterCondition = filterCondition.replace(endsWithMatcher.group(0),
          endsWithMatcher.group(1).trim() + " LIKE '%" + endsWithMatcher.group(2).trim() + "'");
    }
    Pattern containsPattern = Pattern.compile("[Cc]ontains\\((.*?),(.*?)\\)");
    Matcher containsMatcher = containsPattern.matcher(filterCondition);
    while (containsMatcher.find()) {
      filterCondition = filterCondition.replace(containsMatcher.group(0),
          containsMatcher.group(1).trim() + " LIKE '%" + containsMatcher.group(2).trim() + "%'");
    }
    Pattern inPattern = Pattern.compile("[Ii]n\\((.*?), \\[(.*?)]\\)");
    Matcher inMatcher = inPattern.matcher(filterCondition);
    while (inMatcher.find()) {
      String[] words = inMatcher.group(2).trim().split(",");
      String modifiedWords = Arrays.stream(words).map(word -> "'" + word + "'").collect(Collectors.joining(","));
      filterCondition =
          filterCondition.replace(inMatcher.group(0), inMatcher.group(1).trim() + " in (" + modifiedWords + ")");
    }
    Pattern greaterThanPattern = Pattern.compile("[Gg]reater[Tt]han\\((.*?),(.*?)\\)");
    Matcher greaterThanMatcher = greaterThanPattern.matcher(filterCondition);
    while (greaterThanMatcher.find()) {
      filterCondition = filterCondition.replace(greaterThanMatcher.group(0),
          greaterThanMatcher.group(1).trim() + " > " + greaterThanMatcher.group(2).trim());
    }
    Pattern lessThanPattern = Pattern.compile("[Ll]ess[Tt]han\\((.*?),(.*?)\\)");
    Matcher lessThanMatcher = lessThanPattern.matcher(filterCondition);
    while (lessThanMatcher.find()) {
      filterCondition = filterCondition.replace(lessThanMatcher.group(0),
          lessThanMatcher.group(1).trim() + " < " + lessThanMatcher.group(2).trim());
    }
    return convertCommaToAnd(filterCondition);
  }

  private static String convertCommaToAnd(String filterCondition) {
    StringBuilder sb = new StringBuilder();
    List<String> conditions = new LinkedList<>();
    int parenthesis = 0;
    for (char c : filterCondition.toCharArray()) {
      sb.append(c);
      if (c == '(') {
        parenthesis++;
      } else if (c == ')') {
        parenthesis--;
      } else if (c == ',') {
        if (parenthesis == 0) {
          sb.deleteCharAt(sb.length() - 1);
          if (!"null".equalsIgnoreCase(sb.toString().trim())) {
            conditions.add(sb.toString());
          }
          sb = new StringBuilder();
        }
      }
    }
    if (!"".equals(sb.toString()) && !"null".equalsIgnoreCase(sb.toString().trim())) {
      conditions.add(sb.toString());
    }
    return String.join(" AND ", conditions);
  }
}

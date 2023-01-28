/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;
import com.linkedin.coral.trino.rel2trino.transfomers.MapStructAccessOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.ToDateOperatorTransformer;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;
import static com.linkedin.coral.trino.rel2trino.utils.TrinoSqlCallTransformerUtil.*;


/**
 * This utility class initialize a list of SqlCallTransformer which convert the function operators defined in SqlCalls
 * from Coral to Trino on SqlNode layer
 */
public final class CoralToTrinoSqlCallTransformersUtil {
  private static List<SqlCallTransformer> DEFAULT_SQL_CALL_TRANSFORMER_LIST;

  static {
    DEFAULT_SQL_CALL_TRANSFORMER_LIST = new ArrayList<>();
    addCommonSignatureBasedConditionTransformers();
    addAdHocTransformers();
    addLinkedInFunctionTransformers();
  }

  public static SqlCallTransformers getTransformers(Map<String, Boolean> configs) {
    List<SqlCallTransformer> sqlCallTransformerList = new ArrayList<>(DEFAULT_SQL_CALL_TRANSFORMER_LIST);
    // initialize SqlCallTransformer affected by the configuration and add them to the list
    sqlCallTransformerList.add(new ToDateOperatorTransformer(configs.getOrDefault(AVOID_TRANSFORM_TO_DATE_UDF, false)));
    return SqlCallTransformers.of(ImmutableList.copyOf(sqlCallTransformerList));
  }

  private static void addCommonSignatureBasedConditionTransformers() {
    // conditional functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("nvl"), 2, "coalesce"));
    // Array and map functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.ITEM, 2,
        TrinoElementAtFunction.INSTANCE, null, null, null));

    // Math Functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND, 1, "RANDOM", "[]", null, null));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(createSignatureBasedConditionSqlCallTransformer(
        SqlStdOperatorTable.RAND_INTEGER, 2, "RANDOM", "[{\"input\":2}]", null, null));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.TRUNCATE, 2, "TRUNCATE",
            "[{\"op\":\"*\",\"operands\":[{\"input\":1},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
            "{\"op\":\"/\",\"operands\":[{\"input\":0},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}",
            null));

    // String Functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(HiveRLikeOperator.RLIKE, 2, "REGEXP_LIKE"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(HiveRLikeOperator.REGEXP, 2, "REGEXP_LIKE"));

    // JSON Functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(
        createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("get_json_object"), 2, "json_extract"));

    // map various hive functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(createSignatureBasedConditionSqlCallTransformer(
        hiveToCoralSqlOperator("pmod"), 2, "mod",
        "[{\"op\":\"+\",\"operands\":[{\"op\":\"%\",\"operands\":[{\"input\":1},{\"input\":2}]},{\"input\":2}]},{\"input\":2}]",
        null, null));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("base64"), 1, "to_base64"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("unbase64"), 1, "from_base64"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("hex"), 1, "to_hex"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("unhex"), 1, "from_hex"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("array_contains"), 2, "contains"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(
        createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("regexp_extract"), 3, "regexp_extract",
            "[{\"input\": 1}, {\"op\": \"hive_pattern_to_trino\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]",
            null, null));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("instr"), 2, "strpos"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("decode"), 2,
            "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]", "[{\"input\":1}]", null, null));

    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("date_add"), 2, "date_add",
            "[{\"value\": 'day'}, {\"input\": 2},  "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("date_sub"), 2, "date_add",
            "[{\"value\": 'day'}, " + "{\"op\": \"*\", \"operands\":[{\"input\": 2}, {\"value\": -1}]}, "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(createSignatureBasedConditionSqlCallTransformer(
        hiveToCoralSqlOperator("datediff"), 2, "date_diff",
        "[{\"value\": 'day'}, {\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 2}]}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null, null));
  }

  private static void addLinkedInFunctionTransformers() {
    // Most "com.linkedin..." UDFs follow convention of having UDF names mapped from camel-cased name to snake-cased name.
    // For example: For class name IsGuestMemberId, the conventional udf name would be is_guest_member_id.
    // While this convention fits most UDFs it doesn't fit all. With the following mapping we override the conventional
    // UDF name mapping behavior to a hardcoded one.
    // For example instead of UserAgentParser getting mapped to user_agent_parser, we mapped it here to useragentparser
    Set<String> linkedInFunctionSignatureSet = new HashSet<>();
    addLinkedInFunctionTransformer("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup", 3,
        "wat_bot_crawler_lookup", linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.parsing.hive.Ip2Str", 1, "ip2str",
        linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.parsing.hive.Ip2Str", 3, "ip2str",
        linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.parsing.hive.UserAgentParser", 2, "useragentparser",
        linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.lookup.hive.BrowserLookup", 3, "browserlookup",
        linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.jobs.udf.hive.ConvertIndustryCode", 1, "converttoindustryv1",
        linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", 1,
        "urn_extractor", linkedInFunctionSignatureSet);
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", 1, "urn_extractor",
        linkedInFunctionSignatureSet);

    addLinkedInFunctionTransformerFromHiveRegistry(DEFAULT_SQL_CALL_TRANSFORMER_LIST, linkedInFunctionSignatureSet);
  }

  private static void addLinkedInFunctionTransformer(String linkedInFuncName, int numOperands, String trinoFuncName,
      Set<String> linkedInFunctionSignatureSet) {
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralSqlOperator(linkedInFuncName), numOperands, trinoFuncName));
    linkedInFunctionSignatureSet.add(linkedInFuncName + "_" + numOperands);
  }

  private static void addAdHocTransformers() {
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new MapStructAccessOperatorTransformer());
  }
}

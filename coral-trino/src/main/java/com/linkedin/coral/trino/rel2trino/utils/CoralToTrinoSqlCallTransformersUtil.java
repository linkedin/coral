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
import com.linkedin.coral.trino.rel2trino.transfomers.DateAddOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.DateDiffOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.DateSubOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.DecodeOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.MapStructAccessOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.ModOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.RandomIntegerOperatorWithTwoOperandsTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.RandomOperatorWithOneOperandTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.RegexpExtractOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.ToDateOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.TruncateOperatorTransformer;

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
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(
        createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.ITEM, 2, TrinoElementAtFunction.INSTANCE));

    // Math Functions
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new RandomOperatorWithOneOperandTransformer());
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new RandomIntegerOperatorWithTwoOperandsTransformer());
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new TruncateOperatorTransformer());

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
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new ModOperatorTransformer());
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
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new RegexpExtractOperatorTransformer());
    DEFAULT_SQL_CALL_TRANSFORMER_LIST
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralSqlOperator("instr"), 2, "strpos"));
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new DecodeOperatorTransformer());
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new DateAddOperatorTransformer());
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new DateSubOperatorTransformer());
    DEFAULT_SQL_CALL_TRANSFORMER_LIST.add(new DateDiffOperatorTransformer());
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

/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.SignatureBasedConditionSqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;
import com.linkedin.coral.trino.rel2trino.transfomers.MapStructAccessOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transfomers.ToDateOperatorTransformer;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


/**
 * This class defines a list of SqlCallTransformer which convert the function operators defined in SqlCalls
 * from Coral to Trino on SqlNode layer
 */
public final class CoralToTrinoSqlCallTransformersUtil {
  private static final StaticHiveFunctionRegistry HIVE_REGISTRY = new StaticHiveFunctionRegistry();
  private static List<SqlCallTransformer> DEFAULT_SQL_CALL_TRANSFORMER_LIST;

  public static SqlCallTransformers getTransformers(Map<String, Boolean> configs) {
    if (DEFAULT_SQL_CALL_TRANSFORMER_LIST == null) {
      DEFAULT_SQL_CALL_TRANSFORMER_LIST = initializeDefaultSqlCallTransformers();
    }

    List<SqlCallTransformer> sqlCallTransformerList = new ArrayList<>(DEFAULT_SQL_CALL_TRANSFORMER_LIST);
    // initialize SqlCallTransformer affected by the configuration and add them to the list
    sqlCallTransformerList.add(new ToDateOperatorTransformer(
        UDFMapUtils.createUDF("date", hiveToCoralOperator("to_date").getReturnTypeInference()),
        configs.getOrDefault(AVOID_TRANSFORM_TO_DATE_UDF, false)));

    return new SqlCallTransformers(ImmutableList.copyOf(sqlCallTransformerList));
  }

  private static List<SqlCallTransformer> initializeDefaultSqlCallTransformers() {
    List<SqlCallTransformer> sqlCallTransformerList = new ArrayList<>();

    initializeCommonSignatureBasedConditionTransformers(sqlCallTransformerList);
    initializeLinkedInFunctionTransformers(sqlCallTransformerList);

    // initialize ad-hoc transformers
    sqlCallTransformerList.add(new MapStructAccessOperatorTransformer());

    return sqlCallTransformerList;
  }

  private static void initializeCommonSignatureBasedConditionTransformers(
      List<SqlCallTransformer> sqlCallTransformerList) {
    // conditional functions
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("nvl"), 2, "coalesce"));
    // Array and map functions
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.ITEM, 2,
        TrinoElementAtFunction.INSTANCE, null, null, null));

    // Math Functions
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND, 1, "RANDOM", "[]", null, null));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"));
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 2,
        "RANDOM", "[{\"input\":2}]", null, null));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.TRUNCATE, 2, "TRUNCATE",
            "[{\"op\":\"*\",\"operands\":[{\"input\":1},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
            "{\"op\":\"/\",\"operands\":[{\"input\":0},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}",
            null));

    // String Functions
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(HiveRLikeOperator.RLIKE, 2, "REGEXP_LIKE"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(HiveRLikeOperator.REGEXP, 2, "REGEXP_LIKE"));

    // JSON Functions
    sqlCallTransformerList.add(
        createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("get_json_object"), 2, "json_extract"));

    // map various hive functions
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("pmod"), 2, "mod",
        "[{\"op\":\"+\",\"operands\":[{\"op\":\"%\",\"operands\":[{\"input\":1},{\"input\":2}]},{\"input\":2}]},{\"input\":2}]",
        null, null));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("base64"), 1, "to_base64"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("unbase64"), 1, "from_base64"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("hex"), 1, "to_hex"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("unhex"), 1, "from_hex"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("array_contains"), 2, "contains"));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("regexp_extract"), 3, "regexp_extract",
            "[{\"input\": 1}, {\"op\": \"hive_pattern_to_trino\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]",
            null, null));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("instr"), 2, "strpos"));
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("decode"), 2,
        "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]", "[{\"input\":1}]", null, null));

    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("date_add"), 2, "date_add",
            "[{\"value\": 'day'}, {\"input\": 2},  "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null));
    sqlCallTransformerList
        .add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("date_sub"), 2, "date_add",
            "[{\"value\": 'day'}, " + "{\"op\": \"*\", \"operands\":[{\"input\": 2}, {\"value\": -1}]}, "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null));
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(hiveToCoralOperator("datediff"), 2,
        "date_diff",
        "[{\"value\": 'day'}, {\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 2}]}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null, null));
  }

  private static void initializeLinkedInFunctionTransformers(List<SqlCallTransformer> sqlCallTransformerList) {
    // Most "com.linkedin..." UDFs follow convention of having UDF names mapped from camel-cased name to snake-cased name.
    // For example: For class name IsGuestMemberId, the conventional udf name would be is_guest_member_id.
    // While this convention fits most UDFs it doesn't fit all. With the following mapping we override the conventional
    // UDF name mapping behavior to a hardcoded one.
    // For example instead of UserAgentParser getting mapped to user_agent_parser, we mapped it here to useragentparser
    Set<String> linkedInFunctionIdSet = new HashSet<>();
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup"), 3,
        "wat_bot_crawler_lookup"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet,
        "com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup", 3);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.stdudfs.parsing.hive.Ip2Str"), 1, "ip2str"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet, "com.linkedin.stdudfs.parsing.hive.Ip2Str", 1);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.stdudfs.parsing.hive.Ip2Str"), 3, "ip2str"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet, "com.linkedin.stdudfs.parsing.hive.Ip2Str", 3);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.stdudfs.parsing.hive.UserAgentParser"), 2, "useragentparser"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet, "com.linkedin.stdudfs.parsing.hive.UserAgentParser", 2);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.stdudfs.lookup.hive.BrowserLookup"), 3, "browserlookup"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet, "com.linkedin.stdudfs.lookup.hive.BrowserLookup", 3);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.jobs.udf.hive.ConvertIndustryCode"), 1, "converttoindustryv1"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet, "com.linkedin.jobs.udf.hive.ConvertIndustryCode", 1);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper"), 1,
        "urn_extractor"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet,
        "com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", 1);
    sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(
        linkedInFunctionToCoralOperator("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper"), 1,
        "urn_extractor"));
    addLinkedInFunctionSignature(linkedInFunctionIdSet,
        "com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", 1);

    initializeLinkedInFunctionTransformerFromHiveRegistry(sqlCallTransformerList, linkedInFunctionIdSet);
  }

  private static SqlOperator hiveToCoralOperator(String functionName) {
    Collection<Function> lookup = HIVE_REGISTRY.lookup(functionName);
    // TODO: provide overloaded function resolution
    return lookup.iterator().next().getSqlOperator();
  }

  private static void addLinkedInFunctionSignature(Set<String> linkedInFunctionSignatureSet,
      String linkedInFunctionName, int operandNum) {
    linkedInFunctionSignatureSet.add(linkedInFunctionName + "_" + operandNum);
  }

  private static void initializeLinkedInFunctionTransformerFromHiveRegistry(
      List<SqlCallTransformer> sqlCallTransformerList, Set<String> linkedInFunctionSignatureSet) {
    ImmutableMultimap<String, Function> registry = HIVE_REGISTRY.getRegistry();
    Converter<String, String> caseConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    for (Map.Entry<String, Function> entry : registry.entries()) {
      // we cannot use entry.getKey() as function name directly, because keys are all lowercase, which will
      // fail to be converted to lowercase with underscore correctly
      final String hiveFunctionName = entry.getValue().getFunctionName();
      if (!hiveFunctionName.startsWith("com.linkedin")) {
        continue;
      }
      String[] nameSplit = hiveFunctionName.split("\\.");
      // filter above guarantees we've at least 2 entries
      String className = nameSplit[nameSplit.length - 1];
      String funcName = caseConverter.convert(className);
      SqlOperator op = entry.getValue().getSqlOperator();
      for (int i = op.getOperandCountRange().getMin(); i <= op.getOperandCountRange().getMax(); i++) {
        if (!linkedInFunctionSignatureSet.contains(funcName + "_" + i)) {
          sqlCallTransformerList
              .add(createSignatureBasedConditionSqlCallTransformer(op, i, funcName, null, null, null));
        }
      }
    }
  }

  private static SqlOperator linkedInFunctionToCoralOperator(String className) {
    return HIVE_REGISTRY.lookup(className).iterator().next().getSqlOperator();
  }

  private static SqlCallTransformer createSignatureBasedConditionSqlCallTransformer(SqlOperator coralOp,
      int numOperands, String trinoFuncName) {
    return createSignatureBasedConditionSqlCallTransformer(coralOp, numOperands, trinoFuncName, null, null, null);
  }

  private static SqlCallTransformer createSignatureBasedConditionSqlCallTransformer(SqlOperator coralOp,
      int numOperands, String trinoFuncName, String operandTransformer, String resultTransformer,
      String operatorTransformer) {
    return createSignatureBasedConditionSqlCallTransformer(coralOp, numOperands,
        UDFMapUtils.createUDF(trinoFuncName, coralOp.getReturnTypeInference()), operandTransformer, resultTransformer,
        operatorTransformer);
  }

  private static SqlCallTransformer createSignatureBasedConditionSqlCallTransformer(SqlOperator calciteOp,
      int numOperands, SqlOperator trinoOp, String operandTransformer, String resultTransformer,
      String operatorTransformer) {
    return new SignatureBasedConditionSqlCallTransformer(calciteOp.getName(), numOperands, trinoOp, operandTransformer,
        resultTransformer, operatorTransformer);
  }
}

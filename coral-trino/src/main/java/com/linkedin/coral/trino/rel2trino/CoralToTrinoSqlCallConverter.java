/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
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

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.OperatorBasedSqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;
import com.linkedin.coral.trino.rel2trino.transformers.LinkedInOperatorBasedSqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.MapStructAccessOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.ToDateOperatorTransformer;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


/**
 * This class extends the class of SqlShuttle and initialize a SqlCallTransformers which containing a list of SqlCallTransformers
 * to traverse the hierarchy of a SqlCall and converts the functions from Coral operator to Trino operator if it is required
 */
public class CoralToTrinoSqlCallConverter extends SqlShuttle {
  public static final StaticHiveFunctionRegistry HIVE_FUNCTION_REGISTRY = new StaticHiveFunctionRegistry();
  private final Map<String, Boolean> configs;
  private final SqlCallTransformers sqlCallTransformers;
  private final Set<String> linkedInFunctionSignatureSet;

  private final List<SqlCallTransformer> sqlCallTransformerList;
  public CoralToTrinoSqlCallConverter(Map<String, Boolean> configs) {
    this.configs = configs;
    this.linkedInFunctionSignatureSet = new HashSet<>();
    this.sqlCallTransformerList = new ArrayList<>();

    // conditional functions
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("nvl"), 2, "coalesce"));
    // array and map functions
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.ITEM.getName(), 2,
        TrinoElementAtFunction.INSTANCE, null, null, null));
    // math functions
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"));
    sqlCallTransformerList
        .add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.RAND, 1, "RANDOM", "[]", null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 2, "RANDOM",
        "[{\"input\":2}]", null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.TRUNCATE, 2, "TRUNCATE",
        "[{\"op\":\"*\",\"operands\":[{\"input\":1},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
        "{\"op\":\"/\",\"operands\":[{\"input\":0},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}",
        null));
    // string functions
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(HiveRLikeOperator.RLIKE, 2, "REGEXP_LIKE"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(HiveRLikeOperator.REGEXP, 2, "REGEXP_LIKE"));
    // JSON functions
    sqlCallTransformerList
        .add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("get_json_object"), 2, "json_extract"));
    // map various hive functions
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("pmod"), 2, "mod",
        "[{\"op\":\"+\",\"operands\":[{\"op\":\"%\",\"operands\":[{\"input\":1},{\"input\":2}]},{\"input\":2}]},{\"input\":2}]",
        null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("base64"), 1, "to_base64"));
    sqlCallTransformerList
        .add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("unbase64"), 1, "from_base64"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("hex"), 1, "to_hex"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("unhex"), 1, "from_hex"));
    sqlCallTransformerList
        .add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("array_contains"), 2, "contains"));
    sqlCallTransformerList
        .add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("regexp_extract"), 3, "regexp_extract",
            "[{\"input\": 1}, {\"op\": \"hive_pattern_to_trino\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]",
            null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("instr"), 2, "strpos"));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("decode"), 2,
        "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]", "[{\"input\":1}]", null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("date_add"), 2, "date_add",
        "[{\"value\": 'day'}, {\"input\": 2},  "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("date_sub"), 2, "date_add",
        "[{\"value\": 'day'}, " + "{\"op\": \"*\", \"operands\":[{\"input\": 2}, {\"value\": -1}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null, null));
    sqlCallTransformerList.add(new OperatorBasedSqlCallTransformer(hiveToCoralSqlOperator("datediff"), 2, "date_diff",
        "[{\"value\": 'day'}, {\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 2}]}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null, null));
    sqlCallTransformerList.add(new ToDateOperatorTransformer(configs.getOrDefault(AVOID_TRANSFORM_TO_DATE_UDF, false)));
    sqlCallTransformerList.add(new MapStructAccessOperatorTransformer());

    // LinkedIn specific functions
    addLinkedInFunctionTransformer("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup", 3,
        "wat_bot_crawler_lookup");
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.parsing.hive.Ip2Str", 1, "ip2str");
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.parsing.hive.Ip2Str", 3, "ip2str");
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.parsing.hive.UserAgentParser", 2, "useragentparser");
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.lookup.hive.BrowserLookup", 3, "browserlookup");
    addLinkedInFunctionTransformer("com.linkedin.jobs.udf.hive.ConvertIndustryCode", 1, "converttoindustryv1");
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", 1,
        "urn_extractor");
    addLinkedInFunctionTransformer("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", 1,
        "urn_extractor");
    addLinkedInFunctionTransformerFromHiveRegistry();

    this.sqlCallTransformers = SqlCallTransformers.of(ImmutableList.copyOf(sqlCallTransformerList));
  }

  private void addLinkedInFunctionTransformer(String linkedInFuncName, int numOperands, String trinoFuncName) {
    this.sqlCallTransformerList
        .add(new LinkedInOperatorBasedSqlCallTransformer(linkedInFuncName, numOperands, trinoFuncName));
    this.linkedInFunctionSignatureSet.add(linkedInFuncName.toLowerCase() + "_" + numOperands);
  }

  private void addLinkedInFunctionTransformerFromHiveRegistry() {
    ImmutableMultimap<String, Function> registry = HIVE_FUNCTION_REGISTRY.getRegistry();
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
        if (!linkedInFunctionSignatureSet.contains(hiveFunctionName.toLowerCase() + "_" + i)) {
          sqlCallTransformerList.add(new LinkedInOperatorBasedSqlCallTransformer(op, i, funcName));
        }
      }
    }
  }

  private SqlOperator hiveToCoralSqlOperator(String functionName) {
    Collection<Function> lookup = HIVE_FUNCTION_REGISTRY.lookup(functionName);
    return lookup.iterator().next().getSqlOperator();
  }

  @Override
  public SqlNode visit(SqlCall call) {
    SqlCall transformedCall = sqlCallTransformers.apply(call);
    return super.visit(transformedCall);
  }
}

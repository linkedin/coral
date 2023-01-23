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
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.common.transformers.StandardUDFOperatorTransformer;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;
import com.linkedin.coral.trino.rel2trino.transfomers.MapStructAccessOperatorTransformer;


/**
 * This class defines a list of SqlCallTransformers which convert UDF operator from Calcite to Trino
 * on SqlNode layer
 */
public class CalciteTrinoUDFOperatorTransformers extends SqlCallTransformers {
  private static final StaticHiveFunctionRegistry HIVE_REGISTRY = new StaticHiveFunctionRegistry();
  private static CalciteTrinoUDFOperatorTransformers instance;

  private CalciteTrinoUDFOperatorTransformers() {
  }

  private CalciteTrinoUDFOperatorTransformers(List<SqlCallTransformer> operatorTransformers) {
    super(ImmutableList.copyOf(operatorTransformers));
  }

  public static CalciteTrinoUDFOperatorTransformers getInstance() {
    if (instance == null) {
      return new CalciteTrinoUDFOperatorTransformers(initializeOperatorTransformers());
    }
    return instance;
  }

  private static List<SqlCallTransformer> initializeOperatorTransformers() {
    List<SqlCallTransformer> operatorTransformers = new ArrayList<>();
    // conditional functions
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("nvl"), 2, "coalesce"));
    // Array and map functions
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.ITEM, 2, TrinoElementAtFunction.INSTANCE));

    // Math Functions
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"));
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.RAND, 1, "RANDOM", "[]", null));
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"));
    operatorTransformers
        .add(createOperatorTransformer(SqlStdOperatorTable.RAND_INTEGER, 2, "RANDOM", "[{\"input\":2}]", null));
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.TRUNCATE, 2, "TRUNCATE",
        "[{\"op\":\"*\",\"operands\":[{\"input\":1},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
        "{\"op\":\"/\",\"operands\":[{\"input\":0},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}"));

    // String Functions
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR"));
    operatorTransformers.add(createOperatorTransformer(SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR"));
    operatorTransformers.add(createOperatorTransformer(HiveRLikeOperator.RLIKE, 2, "REGEXP_LIKE"));
    operatorTransformers.add(createOperatorTransformer(HiveRLikeOperator.REGEXP, 2, "REGEXP_LIKE"));

    // JSON Functions
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("get_json_object"), 2, "json_extract"));

    // map various hive functions
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("pmod"), 2, "mod",
        "[{\"op\":\"+\",\"operands\":[{\"op\":\"%\",\"operands\":[{\"input\":1},{\"input\":2}]},{\"input\":2}]},{\"input\":2}]",
        null));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("base64"), 1, "to_base64"));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("unbase64"), 1, "from_base64"));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("hex"), 1, "to_hex"));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("unhex"), 1, "from_hex"));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("array_contains"), 2, "contains"));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("regexp_extract"), 3, "regexp_extract",
        "[{\"input\": 1}, {\"op\": \"hive_pattern_to_trino\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]", null));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("instr"), 2, "strpos"));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("decode"), 2,
        "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]", "[{\"input\":1}]", null));

    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("to_date"), 1, "date",
        "[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]", null));
    operatorTransformers
        .add(
            createOperatorTransformer(hiveToCalciteOp("date_add"), 2, "date_add",
                "[{\"value\": 'day'}, {\"input\": 2},  "
                    + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
                null));
    operatorTransformers
        .add(
            createOperatorTransformer(hiveToCalciteOp("date_sub"), 2, "date_add",
                "[{\"value\": 'day'}, " + "{\"op\": \"*\", \"operands\":[{\"input\": 2}, {\"value\": -1}]}, "
                    + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
                null));
    operatorTransformers.add(createOperatorTransformer(hiveToCalciteOp("datediff"), 2, "date_diff",
        "[{\"value\": 'day'}, {\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 2}]}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null));

    // DALI functions
    // Most "com.linkedin..." UDFs follow convention of having UDF names mapped from camel-cased name to snake-cased name.
    // For example: For class name IsGuestMemberId, the conventional udf name would be is_guest_member_id.
    // While this convention fits most UDFs it doesn't fit all. With the following mapping we override the conventional
    // UDF name mapping behavior to a hardcoded one.
    // For example instead of UserAgentParser getting mapped to user_agent_parser, we mapped it here to useragentparser
    Set<String> daliFunctionIdSet = new HashSet<>();
    operatorTransformers.add(
        createOperatorTransformer(daliToCalciteOp("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup"),
            3, "wat_bot_crawler_lookup"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup", 3);
    operatorTransformers
        .add(createOperatorTransformer(daliToCalciteOp("com.linkedin.stdudfs.parsing.hive.Ip2Str"), 1, "ip2str"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.stdudfs.parsing.hive.Ip2Str", 1);
    operatorTransformers
        .add(createOperatorTransformer(daliToCalciteOp("com.linkedin.stdudfs.parsing.hive.Ip2Str"), 3, "ip2str"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.stdudfs.parsing.hive.Ip2Str", 3);
    operatorTransformers.add(createOperatorTransformer(
        daliToCalciteOp("com.linkedin.stdudfs.parsing.hive.UserAgentParser"), 2, "useragentparser"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.stdudfs.parsing.hive.UserAgentParser", 2);
    operatorTransformers.add(createOperatorTransformer(
        daliToCalciteOp("com.linkedin.stdudfs.lookup.hive.BrowserLookup"), 3, "browserlookup"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.stdudfs.lookup.hive.BrowserLookup", 3);
    operatorTransformers.add(createOperatorTransformer(
        daliToCalciteOp("com.linkedin.jobs.udf.hive.ConvertIndustryCode"), 1, "converttoindustryv1"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.jobs.udf.hive.ConvertIndustryCode", 1);
    operatorTransformers.add(createOperatorTransformer(
        daliToCalciteOp("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper"), 1, "urn_extractor"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", 1);
    operatorTransformers.add(createOperatorTransformer(
        daliToCalciteOp("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper"), 1, "urn_extractor"));
    addDaliFunctionId(daliFunctionIdSet, "com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", 1);

    addDaliUDFs(operatorTransformers, daliFunctionIdSet);

    //ad-hoc transformers
    operatorTransformers.add(new MapStructAccessOperatorTransformer());

    return operatorTransformers;
  }

  private static SqlOperator hiveToCalciteOp(String functionName) {
    Collection<Function> lookup = HIVE_REGISTRY.lookup(functionName);
    // TODO: provide overloaded function resolution
    return lookup.iterator().next().getSqlOperator();
  }

  private static void addDaliFunctionId(Set<String> daliFunctionIdSet, String daliFunctionName, int operandNum) {
    daliFunctionIdSet.add(daliFunctionName + "_" + operandNum);
  }

  private static void addDaliUDFs(List<SqlCallTransformer> operatorTransformers, Set<String> daliFunctionIdSet) {
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
        if (!daliFunctionIdSet.contains(hiveFunctionName + "_" + i)) {
          operatorTransformers.add(createOperatorTransformer(op, i, funcName));
        }
      }
    }
  }

  private static SqlOperator daliToCalciteOp(String className) {
    return HIVE_REGISTRY.lookup(className).iterator().next().getSqlOperator();
  }

  private static SqlCallTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      String trinoUDFName) {
    return createOperatorTransformer(calciteOp, numOperands,
        UDFMapUtils.createUDF(trinoUDFName, calciteOp.getReturnTypeInference()));
  }

  private static SqlCallTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      String trinoUDFName, String operandTransformer, String resultTransformer) {
    return createOperatorTransformer(calciteOp, numOperands,
        UDFMapUtils.createUDF(trinoUDFName, calciteOp.getReturnTypeInference()), operandTransformer, resultTransformer);
  }

  private static SqlCallTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      SqlOperator trinoOp) {
    return createOperatorTransformer(calciteOp, numOperands, trinoOp, null, null, null);
  }

  private static SqlCallTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      SqlOperator trinoOp, String operandTransformer, String resultTransformer) {
    return createOperatorTransformer(calciteOp, numOperands, trinoOp, operandTransformer, resultTransformer, null);
  }

  private static SqlCallTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      SqlOperator trinoOp, String operandTransformer, String resultTransformer, String operatorTransformer) {
    return new StandardUDFOperatorTransformer(calciteOp.getName(), numOperands, trinoOp, operandTransformer,
        resultTransformer, operatorTransformer);
  }
}

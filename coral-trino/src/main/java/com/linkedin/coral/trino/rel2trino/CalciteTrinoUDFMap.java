/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;

import static com.linkedin.coral.trino.rel2trino.UDFMapUtils.*;


public class CalciteTrinoUDFMap {
  private CalciteTrinoUDFMap() {
  }

  private static final Map<String, UDFTransformer> UDF_MAP = new HashMap<>();
  private static final StaticHiveFunctionRegistry HIVE_REGISTRY = new StaticHiveFunctionRegistry();
  static {
    // conditional functions
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("nvl"), 2, "coalesce");
    // Array and map functions
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.ITEM, 2, TrinoElementAtFunction.INSTANCE);

    // Math Functions
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.RAND, 0, "RANDOM");
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.RAND, 1, "RANDOM", "[]", null);
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM");
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.RAND_INTEGER, 2, "RANDOM", "[{\"input\":2}]", null);
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.TRUNCATE, 2, "TRUNCATE",
        "[{\"op\":\"*\",\"operands\":[{\"input\":1},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
        "{\"op\":\"/\",\"operands\":[{\"input\":0},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}");

    // String Functions
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR");
    createUDFMapEntry(UDF_MAP, SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR");
    createUDFMapEntry(UDF_MAP, HiveRLikeOperator.RLIKE, 2, "REGEXP_LIKE");
    createUDFMapEntry(UDF_MAP, HiveRLikeOperator.REGEXP, 2, "REGEXP_LIKE");

    // JSON Functions
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("get_json_object"), 2, "json_extract");

    // map various hive functions
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("pmod"), 2, "mod",
        "[{\"op\":\"+\",\"operands\":[{\"op\":\"%\",\"operands\":[{\"input\":1},{\"input\":2}]},{\"input\":2}]},{\"input\":2}]",
        null);
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("base64"), 1, "to_base64");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("unbase64"), 1, "from_base64");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("hex"), 1, "to_hex");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("unhex"), 1, "from_hex");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("array_contains"), 2, "contains");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("regexp_extract"), 3, "regexp_extract",
        "[{\"input\": 1}, {\"op\": \"hive_pattern_to_trino\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]", null);
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("instr"), 2, "strpos");
    createRuntimeUDFMapEntry(UDF_MAP, hiveToCalciteOp("decode"), 2,
        "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]", "[{\"input\":1}]", null);

    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("to_date"), 1, "date",
        "[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]", null);
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("date_add"), 2, "date_add", "[{\"value\": 'day'}, {\"input\": 2},  "
        + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]", null);
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("date_sub"), 2, "date_add",
        "[{\"value\": 'day'}, " + "{\"op\": \"*\", \"operands\":[{\"input\": 2}, {\"value\": -1}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null);
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("datediff"), 2, "date_diff",
        "[{\"value\": 'day'}, {\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 2}]}]}, "
            + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
        null);

    // DALI functions
    // Most "com.linkedin..." UDFs follow convention of having UDF names mapped from camel-cased name to snake-cased name.
    // For example: For class name IsGuestMemberId, the conventional udf name would be is_guest_member_id.
    // While this convention fits most UDFs it doesn't fit all. With the following mapping we override the conventional
    // UDF name mapping behavior to a hardcoded one.
    // For example instead of UserAgentParser getting mapped to user_agent_parser, we mapped it here to useragentparser
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup"), 3,
        "wat_bot_crawler_lookup");
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.stdudfs.parsing.hive.Ip2Str"), 1, "ip2str");
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.stdudfs.parsing.hive.Ip2Str"), 3, "ip2str");
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.stdudfs.parsing.hive.UserAgentParser"), 2,
        "useragentparser");

    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.stdudfs.lookup.hive.BrowserLookup"), 3, "browserlookup");
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.jobs.udf.hive.ConvertIndustryCode"), 1,
        "converttoindustryv1");
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper"), 1,
        "urn_extractor");
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper"), 1,
        "urn_extractor");

    addDaliUDFs();
  }

  private static void addDaliUDFs() {
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
        if (!isDaliUDFAlreadyAdded(hiveFunctionName, i)) {
          createUDFMapEntry(UDF_MAP, op, i, funcName);
        }
      }
    }
  }

  /**
   * Gets UDFTransformer for a given Calcite SQL Operator.
   *
   * @param calciteOpName Name of Calcite SQL operator
   * @param numOperands Number of operands
   * @return {@link UDFTransformer} object
   */
  public static UDFTransformer getUDFTransformer(String calciteOpName, int numOperands) {
    return UDF_MAP.get(getKey(calciteOpName, numOperands));
  }

  private static Boolean isDaliUDFAlreadyAdded(String classString, int numOperands) {
    return getUDFTransformer(classString, numOperands) != null;
  }

  /**
   * Looks up Hive functions using functionName case-insensitively.
   */
  private static SqlOperator hiveToCalciteOp(String functionName) {
    Collection<Function> lookup = HIVE_REGISTRY.lookup(functionName);
    // TODO: provide overloaded function resolution
    return lookup.iterator().next().getSqlOperator();
  }

  /**
   * Looks up Dali functions using className case-insensitively.
   */
  private static SqlOperator daliToCalciteOp(String className) {
    return HIVE_REGISTRY.lookup(className).iterator().next().getSqlOperator();
  }
}

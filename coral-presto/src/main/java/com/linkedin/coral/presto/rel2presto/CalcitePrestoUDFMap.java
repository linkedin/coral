package com.linkedin.coral.presto.rel2presto;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.functions.HiveFunction;
import com.linkedin.coral.functions.HiveRLikeOperator;
import com.linkedin.coral.functions.StaticHiveFunctionRegistry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import static com.linkedin.coral.presto.rel2presto.UDFMapUtils.*;


public class CalcitePrestoUDFMap {
  private CalcitePrestoUDFMap() {
  }

  private static final Map<String, UDFTransformer> UDF_MAP = new HashMap();
  private static final StaticHiveFunctionRegistry HIVE_REGISTRY = new StaticHiveFunctionRegistry();
  static {
    // conditional functions
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("nvl"), 2, "coalesce");
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

    // map various hive functions
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("base64"), 1, "to_base64");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("unbase64"), 1, "from_base64");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("hex"), 1, "to_hex");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("unhex"), 1, "from_hex");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("regexp_extract"), 3, "regexp_extract",
       "[{\"input\": 1}, {\"op\": \"hive_pattern_to_presto\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]",
        null);
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("instr"), 2, "strpos");
    createRuntimeUDFMapEntry(UDF_MAP, hiveToCalciteOp("decode"), 2,
        "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]",
        "[{\"input\":1}]", null);

    // FIXME: this is incorrect. Adding this to test correctness of the overall system
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("concat_ws"), 3, "concat_ws");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("from_unixtime"), 1, "unixtime_to_str");
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("from_unixtime"), 2, "unixtime_to_str");

    // DALI functions
    // This may not work for all but works for now...
    createUDFMapEntry(UDF_MAP, daliToCalciteOp("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup"),
        3, "wat_bot_crawler_lookup");
    addDaliUDFs();
  }

  private static void addDaliUDFs() {
    ImmutableMultimap<String, HiveFunction> registry = HIVE_REGISTRY.getRegistry();
    Converter<String, String> caseConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    for (Map.Entry<String, HiveFunction> entry : registry.entries()) {
      if (!entry.getKey().startsWith("com.linkedin")
          || entry.getKey().contains("WATBotCrawlerLookup")) {
        continue;
      }
      String[] nameSplit = entry.getKey().split("\\.");
      // filter above guarantees we've atleast 2 entries
      String className = nameSplit[nameSplit.length - 1];
      String funcName = caseConverter.convert(className);
      SqlOperator op = entry.getValue().getSqlOperator();
      for (int i = op.getOperandCountRange().getMin(); i <= op.getOperandCountRange().getMax(); i++) {
        createUDFMapEntry(UDF_MAP, op, i, funcName);
      }
    }
  }

  /**
   * Gets UDFTransformer for a given Calcite SQL Operator.
   *
   * @param calciteOpName Name of Calcite SQL operator
   * @param numOperands Number of operands
   */
  public static UDFTransformer getUDFTransformer(String calciteOpName, int numOperands) {
    return UDF_MAP.get(getKey(calciteOpName, numOperands));
  }

  private static SqlOperator hiveToCalciteOp(String functionName) {
    Collection<HiveFunction> lookup = HIVE_REGISTRY.lookup(functionName, false);
    // TODO: provide overloaded function resolution
    return lookup.iterator().next().getSqlOperator();
  }

  private static SqlOperator daliToCalciteOp(String className) {
    return HIVE_REGISTRY.lookup(className, true).iterator().next().getSqlOperator();
  }
}

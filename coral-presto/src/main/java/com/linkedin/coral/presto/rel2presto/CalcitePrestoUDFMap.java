package com.linkedin.coral.presto.rel2presto;

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
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("regexp_extract"), 3, "regexp_extract");
    // FIXME: this is incorrect. Adding this to test correctness of the overall system
    createUDFMapEntry(UDF_MAP, hiveToCalciteOp("concat_ws"), 3, "concat_ws");
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
}

package com.linkedin.coral.presto.rel2presto;

import java.util.HashMap;
import java.util.Map;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import static com.linkedin.coral.presto.rel2presto.UDFMapUtils.*;


public class CalcitePrestoUDFMap {
  private CalcitePrestoUDFMap() {
  }

  private static final Map<String, UDFTransformer> UDF_MAP = new HashMap();
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
}

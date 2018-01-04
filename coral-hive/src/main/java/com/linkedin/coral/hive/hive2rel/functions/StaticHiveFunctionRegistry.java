package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;


/**
 * Static implementation of HiveFunctionRegistry that has hard-coded list of all
 * function names. This has a major disadvantage that the user defined functions are
 * not available to the registry without manually adding the entry here and uploading
 * a new version of library.
 *
 * TODO: Provide function registry catalog
 */
public class StaticHiveFunctionRegistry implements HiveFunctionRegistry {

  static final Multimap<String, HiveFunction> FUNCTION_MAP = HashMultimap.create();

  // NOTE: all function names should be lowercase for case-insensitive comparison
  static {
    // FIXME: This mapping is currently incomplete
    // aggregation functions
    addFunctionEntry("sum", SUM);
    addFunctionEntry("count", COUNT);
    addFunctionEntry("avg", AVG);
    addFunctionEntry("min", MIN);
    addFunctionEntry("max", MAX);

    // operators
    addFunctionEntry("tok_isnull", IS_NULL);
    addFunctionEntry("tok_isnotnull", IS_NOT_NULL);
    addFunctionEntry("in", IN);
    FUNCTION_MAP.put("when", HiveFunction.WHEN);
    FUNCTION_MAP.put("case", HiveFunction.CASE);
    FUNCTION_MAP.put("between", HiveFunction.BETWEEN);

    // cast operator
    addCastOperatorEntries();

    // string functions
    createAddUserDefinedFunction("in_file", ReturnTypes.BOOLEAN, OperandTypes.STRING_STRING);
    createAddUserDefinedFunction("instr", ReturnTypes.INTEGER, OperandTypes.STRING_STRING);
    createAddUserDefinedFunction("length", ReturnTypes.INTEGER, OperandTypes.STRING);
    createAddUserDefinedFunction("regexp_extract", ReturnTypes.ARG0, OperandTypes.STRING_STRING_INTEGER);
    createAddUserDefinedFunction("reverse", ReturnTypes.ARG0, OperandTypes.STRING);

    // LinkedIn UDFs: Dali stores mapping from UDF name to the implementing Java class as table properties
    // in the HCatalog. So, an UDF implementation may be referred by different names by different views.
    // We register these UDFs by the implementing class name to create a single entry for each UDF.
    createAddUserDefinedFunction("com.linkedin.dali.udf.istestmemberid.hive.istestmemberid",
        ReturnTypes.BOOLEAN, OperandTypes.family(SqlTypeFamily.NUMERIC, SqlTypeFamily.CHARACTER));
    createAddUserDefinedFunction("com.linkedin.dali.udf.urnextractor.hive.urnextractor",
        ReturnTypes.explicit(SqlTypeName.ARRAY), OperandTypes.or(OperandTypes.STRING, OperandTypes.ARRAY));
  }

  /**
   * Returns a list of functions matching given name. This returns empty list if the
   * function name is not found
   * @param functionName function name to match
   * @return list of matching HiveFunctions or empty collection.
   */
  @Override
  public Collection<HiveFunction> lookup(String functionName) {
    return FUNCTION_MAP.get(functionName.toLowerCase());
  }

  private static void addFunctionEntry(String functionName, SqlOperator operator) {
    FUNCTION_MAP.put(functionName, new HiveFunction(functionName, operator));
  }

  private static void createAddUserDefinedFunction(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    addFunctionEntry(functionName, createCalciteUDF(functionName, returnTypeInference, operandTypeChecker));
  }

  private static void createAddUserDefinedFunction(String functionName, SqlReturnTypeInference returnTypeInference) {
    addFunctionEntry(functionName, createCalciteUDF(functionName, returnTypeInference));
  }

  private static SqlOperator createCalciteUDF(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO),
        returnTypeInference, null, operandTypeChecker, null, null);
  }

  private static SqlOperator createCalciteUDF(String functionName, SqlReturnTypeInference returnTypeInference) {
    return createCalciteUDF(functionName, returnTypeInference, null);
  }

  private static void addCastOperatorEntries() {
    String[] castFunctions = { "tok_boolean", "tok_int", "tok_string",
        "tok_double", "tok_float", "tok_bigint", "tok_tinyint", "tok_smallint",
        "tok_char", "tok_decimal", "tok_varchar", "tok_binary", "tok_date", "tok_timestamp"};
    for (String f : castFunctions) {
      FUNCTION_MAP.put(f, HiveFunction.CAST);
    }
  }
}

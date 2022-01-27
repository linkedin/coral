/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SameOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.collect.HashMultimap;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.Multimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionRegistry;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.common.functions.OperandTypeInference;
import com.linkedin.coral.common.functions.SameOperandTypeExceptFirstOperandChecker;
import com.linkedin.coral.hive.hive2rel.functions.HiveExplodeOperator;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunction;
import com.linkedin.coral.hive.hive2rel.functions.HiveJsonTupleOperator;
import com.linkedin.coral.hive.hive2rel.functions.HiveNamedStructFunction;
import com.linkedin.coral.hive.hive2rel.functions.HivePosExplodeOperator;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.HiveReflectOperator;

import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.*;
import static org.apache.calcite.sql.fun.SqlLibraryOperators.*;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.apache.calcite.sql.type.ReturnTypes.*;


/**
 * Static implementation of TrinoFunctionRegistry that has hard-coded list of all
 * function names. This has a major disadvantage that the user defined functions are
 * not available to the registry without manually adding the entry here and uploading
 * a new version of library.
 * TODO: Provide function registry catalog
 *
 * Note that Coral maintains a copy of StaticTrinoFunctionRegistry for read only at run time.
 * For individual query, we create a copy of function registry in a RelConTextProvider object.
 */
public class StaticTrinoFunctionRegistry implements FunctionRegistry {

  public static final String IS_TEST_MEMBER_ID_CLASS = "com.linkedin.dali.udf.istestmemberid.hive.IsTestMemberId";

  // TODO: Make this immutable using builder
  static final Multimap<String, Function> FUNCTION_MAP = HashMultimap.create();

  // Used for registering UDTFs, the key is the function name and the value is a list of field names returned by the UDTF
  // We need it because we need to know the return field names of UDTF to do the conversion in ParseTreeBuilder.visitLateralViewUDTF
  public static final Map<String, ImmutableList<String>> UDTF_RETURN_FIELD_NAME_MAP = new HashMap<>();

  static {

    // NOTE: All function names will be added as lowercase for case-insensitive comparison.
    // FIXME: This mapping is currently incomplete
    // aggregation functions
    addFunctionEntry("sum", SUM);
    addFunctionEntry("count", COUNT);
    addFunctionEntry("avg", AVG);
    addFunctionEntry("min", MIN);
    addFunctionEntry("max", MAX);
    createAddUserDefinedFunction("collect_list", FunctionReturnTypes.ARRAY_OF_ARG0_TYPE, ANY);
    createAddUserDefinedFunction("collect_set", FunctionReturnTypes.ARRAY_OF_ARG0_TYPE, ANY);

    // window functions
    addFunctionEntry("row_number", ROW_NUMBER);
    addFunctionEntry("rank", SqlStdOperatorTable.RANK); // qualification required due to naming conflict
    addFunctionEntry("dense_rank", DENSE_RANK);
    addFunctionEntry("cume_dist", CUME_DIST);
    addFunctionEntry("percent_rank", PERCENT_RANK);
    addFunctionEntry("first_value", FIRST_VALUE);
    addFunctionEntry("last_value", LAST_VALUE);
    addFunctionEntry("nth_value", NTH_VALUE);
    addFunctionEntry("lag", LAG);
    addFunctionEntry("lead", LEAD);
    addFunctionEntry("stddev", STDDEV);
    addFunctionEntry("stddev_samp", STDDEV_SAMP);
    addFunctionEntry("stddev_pop", STDDEV_POP);
    addFunctionEntry("variance", VARIANCE);
    addFunctionEntry("var_samp", VAR_SAMP);
    addFunctionEntry("var_pop", VAR_POP);

    //addFunctionEntry("in", HiveInOperator.IN);
    FUNCTION_MAP.put("in", HiveFunction.IN);

    //addFunctionEntry("in", SqlStdOperatorTable.IN);

    // operators
    addFunctionEntry("rlike", HiveRLikeOperator.RLIKE);
    addFunctionEntry("regexp", HiveRLikeOperator.REGEXP);
    addFunctionEntry("!=", NOT_EQUALS);
    addFunctionEntry("==", EQUALS);

    // conditional function
    addFunctionEntry("tok_isnull", IS_NULL);
    addFunctionEntry("tok_isnotnull", IS_NOT_NULL);
    FUNCTION_MAP.put("when", HiveFunction.WHEN);
    FUNCTION_MAP.put("case", HiveFunction.CASE);
    FUNCTION_MAP.put("between", HiveFunction.BETWEEN);
    addFunctionEntry("nullif", NULLIF);
    addFunctionEntry("isnull", IS_NULL);
    addFunctionEntry("isnotnull", IS_NOT_NULL);

    // TODO: this should be arg1 or arg2 nullable
    createAddUserDefinedFunction("nvl", ARG0_NULLABLE, and(family(SqlTypeFamily.ANY, SqlTypeFamily.ANY), SAME_SAME));

    // calcite models 'if' function as CASE operator. We can use CASE but that will cause translation
    // to SQL to be odd although correct. So, we add 'if' as UDF
    addFunctionEntry("if",
        createCalciteUDF("if", FunctionReturnTypes.IF_FUNC_RETURN_TYPE, OperandTypeInference.BOOLEAN_ANY_SAME,
            new SameOperandTypeExceptFirstOperandChecker(3, SqlTypeName.BOOLEAN), null));

    addFunctionEntry("coalesce", COALESCE);
    // cast operator
    addCastOperatorEntries();

    // Complex type constructors
    addFunctionEntry("array", ARRAY_VALUE_CONSTRUCTOR);
    addFunctionEntry("struct", ROW);
    addFunctionEntry("map", MAP_VALUE_CONSTRUCTOR);
    addFunctionEntry("named_struct", HiveNamedStructFunction.NAMED_STRUCT);
    addFunctionEntry("generic_project", GenericProjectFunction.GENERIC_PROJECT);

    // conversion functions
    createAddUserDefinedFunction("binary", FunctionReturnTypes.BINARY,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));

    // mathematical functions
    // we need to define new strategy for hive to allow null operands by default for everything
    createAddUserDefinedFunction("pmod", FunctionReturnTypes.BIGINT, NUMERIC_NUMERIC);
    createAddUserDefinedFunction("round", DOUBLE_NULLABLE,
        family(ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.INTEGER), optionalOrd(1)));
    createAddUserDefinedFunction("bround", DOUBLE_NULLABLE,
        family(ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.INTEGER), optionalOrd(1)));
    createAddUserDefinedFunction("floor", BIGINT_FORCE_NULLABLE, family(SqlTypeFamily.NUMERIC));
    createAddUserDefinedFunction("ceil", BIGINT_FORCE_NULLABLE, family(SqlTypeFamily.NUMERIC));
    createAddUserDefinedFunction("ceiling", BIGINT_FORCE_NULLABLE, family(SqlTypeFamily.NUMERIC));
    createAddUserDefinedFunction("rand", DOUBLE_NULLABLE,
        family(ImmutableList.of(SqlTypeFamily.INTEGER), optionalOrd(0)));
    createAddUserDefinedFunction("exp", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("ln", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("log10", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("log2", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("log", DOUBLE_NULLABLE, NUMERIC_NUMERIC);
    createAddUserDefinedFunction("pow", DOUBLE_NULLABLE, NUMERIC_NUMERIC);
    createAddUserDefinedFunction("power", DOUBLE_NULLABLE, NUMERIC_NUMERIC);
    createAddUserDefinedFunction("sqrt", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("hex", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.NUMERIC), family(SqlTypeFamily.BINARY)));
    createAddUserDefinedFunction("unhex", FunctionReturnTypes.BINARY, STRING);
    createAddUserDefinedFunction("conv", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.EXACT_NUMERIC, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER),
            family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER)));
    createAddUserDefinedFunction("abs", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("sin", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("asin", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("cos", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("acos", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("tan", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("atan", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("degrees", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("radians", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("positive", ARG0_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("negative", ARG0_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("sign", ARG0_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("e", DOUBLE, NILADIC);
    createAddUserDefinedFunction("pi", DOUBLE, NILADIC);
    createAddUserDefinedFunction("factorial", BIGINT_NULLABLE, family(SqlTypeFamily.INTEGER));
    createAddUserDefinedFunction("cbrt", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("shiftleft", ARG0_NULLABLE, EXACT_NUMERIC_EXACT_NUMERIC);
    createAddUserDefinedFunction("shiftright", ARG0_NULLABLE, EXACT_NUMERIC_EXACT_NUMERIC);
    createAddUserDefinedFunction("shiftrightunsigned", ARG0_NULLABLE, EXACT_NUMERIC_EXACT_NUMERIC);
    createAddUserDefinedFunction("greatest", ARG0_NULLABLE, SAME_VARIADIC);
    createAddUserDefinedFunction("least", ARG0_NULLABLE, SAME_VARIADIC);
    createAddUserDefinedFunction("width_bucket", INTEGER_NULLABLE,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC, SqlTypeFamily.INTEGER));

    // string functions
    // TODO: operand types are not strictly true since these functions can take null literal
    // and most of these entries don't allow null literals. This will work for most common usages
    // but it's easy to write HiveQL to make these fail
    createAddUserDefinedFunction("ascii", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("base64", FunctionReturnTypes.STRING, BINARY);
    createAddUserDefinedFunction("character_length", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("chr", FunctionReturnTypes.STRING, NUMERIC);
    createAddUserDefinedFunction("concat", FunctionReturnTypes.STRING, SAME_VARIADIC);
    // [CORAL-24] Tried setting this to
    // or(family(SqlTypeFamily.STRING, SqlTypeFamily.ARRAY),
    // and(variadic(SqlOperandCountRanges.from(2)), repeat(SqlOperandCountRanges.from(2), STRING)))
    // but calcite's composeable operand checker does not handle variadic operator counts correctly.
    createAddUserDefinedFunction("concat_ws", FunctionReturnTypes.STRING, new SqlOperandTypeChecker() {
      @Override
      public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
        return family(SqlTypeFamily.STRING, SqlTypeFamily.ARRAY).checkOperandTypes(callBinding, throwOnFailure)
            || new SameOperandTypeChecker(-1).checkOperandTypes(callBinding, throwOnFailure);
      }

      @Override
      public SqlOperandCountRange getOperandCountRange() {
        return SqlOperandCountRanges.from(2);
      }

      @Override
      public String getAllowedSignatures(SqlOperator op, String opName) {
        return opName + "(STRING, ARRAY|STRING, ...)";
      }

      @Override
      public Consistency getConsistency() {
        return Consistency.NONE;
      }

      @Override
      public boolean isOptional(int i) {
        return false;
      }
    });

    createAddUserDefinedFunction("context_ngrams", LEAST_RESTRICTIVE,
        family(SqlTypeFamily.ARRAY, SqlTypeFamily.ARRAY, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER));
    createAddUserDefinedFunction("decode", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.BINARY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("elt", FunctionReturnTypes.STRING, VARIADIC);
    createAddUserDefinedFunction("encode", FunctionReturnTypes.BINARY, STRING_STRING);
    createAddUserDefinedFunction("field", ReturnTypes.INTEGER, VARIADIC);
    createAddUserDefinedFunction("find_in_set", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("format_number", FunctionReturnTypes.STRING, NUMERIC_INTEGER);
    createAddUserDefinedFunction("get_json_object", FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("in_file", ReturnTypes.BOOLEAN, STRING_STRING);
    createAddUserDefinedFunction("initcap", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("instr", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("length", INTEGER_NULLABLE, STRING);
    createAddUserDefinedFunction("levenshtein", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("locate", FunctionReturnTypes.STRING,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.INTEGER), optionalOrd(2)));
    addFunctionEntry("lower", LOWER);
    addFunctionEntry("lcase", LOWER);
    addFunctionEntry("translate", TRANSLATE3);
    addFunctionEntry("translate3", TRANSLATE3);
    createAddUserDefinedFunction("lpad", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("ltrim", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("ngrams", LEAST_RESTRICTIVE,
        family(SqlTypeFamily.ARRAY, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER));
    createAddUserDefinedFunction("octet_length", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("parse_url", FunctionReturnTypes.STRING,
        family(Collections.nCopies(3, SqlTypeFamily.STRING), optionalOrd(2)));
    createAddUserDefinedFunction("printf", FunctionReturnTypes.STRING, VARIADIC);
    createAddUserDefinedFunction("regexp_extract", ARG0,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.INTEGER), optionalOrd(2)));
    createAddUserDefinedFunction("regexp_replace", FunctionReturnTypes.STRING, STRING_STRING_STRING);
    createAddUserDefinedFunction("repeat", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER));
    addFunctionEntry("replace", REPLACE);
    createAddUserDefinedFunction("reverse", ARG0, or(STRING, NULLABLE_LITERAL));
    createAddUserDefinedFunction("rpad", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("rtrim", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("sentences", LEAST_RESTRICTIVE, STRING_STRING_STRING);
    createAddUserDefinedFunction("soundex", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("space", FunctionReturnTypes.STRING, NUMERIC);
    createAddUserDefinedFunction("split", FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR), STRING_STRING);
    createAddUserDefinedFunction("str_to_map", FunctionReturnTypes.mapOfType(SqlTypeName.VARCHAR, SqlTypeName.VARCHAR),
        family(Collections.nCopies(3, SqlTypeFamily.STRING), optionalOrd(ImmutableList.of(1, 2))));
    createAddUserDefinedFunction("substr", FunctionReturnTypes.STRING,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER), optionalOrd(2)));
    createAddUserDefinedFunction("substring", FunctionReturnTypes.STRING,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER), optionalOrd(2)));

    createAddUserDefinedFunction("substring_index", FunctionReturnTypes.STRING, STRING_STRING_INTEGER);
    createAddUserDefinedFunction("trim", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("unbase64", explicit(SqlTypeName.VARBINARY), or(STRING, NULLABLE_LITERAL));
    addFunctionEntry("upper", UPPER);
    addFunctionEntry("ucase", UPPER);
    addFunctionEntry("initcap", INITCAP);
    createAddUserDefinedFunction("md5", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));
    createAddUserDefinedFunction("sha1", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));
    createAddUserDefinedFunction("sha", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));
    createAddUserDefinedFunction("crc32", FunctionReturnTypes.BIGINT,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));

    // xpath functions
    createAddUserDefinedFunction("xpath", FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR), STRING_STRING);
    createAddUserDefinedFunction("xpath_string", FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("xpath_boolean", ReturnTypes.BOOLEAN, STRING_STRING);
    createAddUserDefinedFunction("xpath_short", FunctionReturnTypes.SMALLINT, STRING_STRING);
    createAddUserDefinedFunction("xpath_int", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("xpath_long", FunctionReturnTypes.BIGINT, STRING_STRING);
    createAddUserDefinedFunction("xpath_float", DOUBLE, STRING_STRING);
    createAddUserDefinedFunction("xpath_double", DOUBLE, STRING_STRING);
    createAddUserDefinedFunction("xpath_number", DOUBLE, STRING_STRING);

    // Date Functions
    createAddUserDefinedFunction("from_unixtime", FunctionReturnTypes.STRING,
        family(ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING), optionalOrd(1)));
    createAddUserDefinedFunction("unix_timestamp", BIGINT,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.STRING), optionalOrd(ImmutableList.of(0, 1))));
    createAddUserDefinedFunction("to_date", FunctionReturnTypes.STRING, or(STRING, DATETIME));
    createAddUserDefinedFunction("date", DATE, or(STRING, DATETIME));
    createAddUserDefinedFunction("year", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("quarter", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("month", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("day", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("dayofmonth", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("hour", ReturnTypes.INTEGER, or(STRING, DATETIME));
    createAddUserDefinedFunction("minute", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("second", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("weekofyear", ReturnTypes.INTEGER, STRING);
    //TODO: add extract UDF
    createAddUserDefinedFunction("datediff", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("date_add", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.DATE, SqlTypeFamily.INTEGER), family(SqlTypeFamily.TIMESTAMP, SqlTypeFamily.INTEGER),
            family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER)));

    createAddUserDefinedFunction("date_sub", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.DATE, SqlTypeFamily.INTEGER), family(SqlTypeFamily.TIMESTAMP, SqlTypeFamily.INTEGER),
            family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER)));
    createAddUserDefinedFunction("from_utc_timestamp", explicit(SqlTypeName.TIMESTAMP),
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    addFunctionEntry("current_date", CURRENT_DATE);
    addFunctionEntry("current_timestamp", CURRENT_TIMESTAMP);
    createAddUserDefinedFunction("add_months", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER));
    createAddUserDefinedFunction("last_day", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("next_day", FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("trunc", FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("months_between", DOUBLE, family(SqlTypeFamily.DATE, SqlTypeFamily.DATE));
    createAddUserDefinedFunction("date_format", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.DATE, SqlTypeFamily.STRING), family(SqlTypeFamily.TIMESTAMP, SqlTypeFamily.STRING),
            family(SqlTypeFamily.STRING, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("to_utc_timestamp", FunctionReturnTypes.STRING,
        or(STRING_STRING, family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING)));

    // Collection functions
    addFunctionEntry("size", CARDINALITY);
    createAddUserDefinedFunction("array_contains", ReturnTypes.BOOLEAN, family(SqlTypeFamily.ARRAY, SqlTypeFamily.ANY));
    createAddUserDefinedFunction("map_keys", opBinding -> {
      RelDataType operandType = opBinding.getOperandType(0);
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      return typeFactory.createArrayType(operandType.getKeyType(), -1);
    }, family(SqlTypeFamily.MAP));

    createAddUserDefinedFunction("map_values", opBinding -> {
      RelDataType operandType = opBinding.getOperandType(0);
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      return typeFactory.createArrayType(operandType.getValueType(), -1);
    }, family(SqlTypeFamily.MAP));

    createAddUserDefinedFunction("array_contains", ReturnTypes.BOOLEAN, family(SqlTypeFamily.ARRAY, SqlTypeFamily.ANY));
    createAddUserDefinedFunction("sort_array", ARG0, ARRAY);

    createAddUserDefinedFunction("extract_union", COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY,
        or(ANY, family(SqlTypeFamily.ANY, SqlTypeFamily.INTEGER)));
    createAddUserDefinedFunction("coalesce_struct", COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY,
        or(ANY, family(SqlTypeFamily.ANY, SqlTypeFamily.INTEGER)));

    // UDTFs
    addFunctionEntry("explode", HiveExplodeOperator.EXPLODE);
    addFunctionEntry("posexplode", HivePosExplodeOperator.POS_EXPLODE);
    addFunctionEntry("json_tuple", HiveJsonTupleOperator.JSON_TUPLE);

    // reflect functions
    addFunctionEntry("reflect", HiveReflectOperator.REFLECT);
    addFunctionEntry("java_method", HiveReflectOperator.REFLECT);

    // Context functions
    addFunctionEntry("current_user", CURRENT_USER);

    // Trino specific functions
    createAddTrinoFunction("foo", "foo", ReturnTypes.INTEGER, NILADIC); // for testing
    createAddTrinoFunction("foo", "foo", ReturnTypes.INTEGER, family(SqlTypeFamily.ANY)); // for testing
    createAddTrinoFunction("strpos", "instr", ReturnTypes.INTEGER, STRING_STRING);
  }

  /**
   * Adds a Trino specific function to the registry.
   */
  public static void createAddTrinoFunction(String trinoFunctionName, String coralIRFunctionName,
      // Register Trino function for ParseTreeBuilder
      SqlReturnTypeInference returnTypeInference, SqlOperandTypeChecker operandTypeChecker) {
    Function coralIRFn = new Function(coralIRFunctionName,
        createCalciteUDF(coralIRFunctionName, returnTypeInference, operandTypeChecker));
    FUNCTION_MAP.put(trinoFunctionName.toLowerCase(), coralIRFn);

    // Register Coral function for TrinoOperatorTable
    FUNCTION_MAP.put(coralIRFunctionName.toLowerCase(), coralIRFn);
  }

  /**
   * Returns a list of functions matching given name case-insensitively. This returns empty list if the
   * function name is not found.
   * @param functionName function name to match
   * @return list of matching HiveFunctions or empty collection.
   */
  @Override
  public Collection<Function> lookup(String functionName) {
    return FUNCTION_MAP.get(functionName.toLowerCase());
  }

  /**
   * Adds the function to registry, the key is lowercase functionName to make lookup case-insensitive.
   */
  private static void addFunctionEntry(String functionName, SqlOperator operator) {
    FUNCTION_MAP.put(functionName.toLowerCase(), new Function(functionName, operator));
  }

  public static void createAddUserDefinedFunction(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    addFunctionEntry(functionName, createCalciteUDF(functionName, returnTypeInference, operandTypeChecker));
  }

  public static void createAddUserDefinedFunction(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker, String dependency) {
    String depPrefix = dependency.substring(0, 6).toLowerCase();

    // TODO: dependency not used. Consider removing it (maybe this method completely).
    if (!depPrefix.equals("ivy://")) {
      dependency = "ivy://" + dependency;
    }
    addFunctionEntry(functionName, createCalciteUDF(functionName, returnTypeInference, operandTypeChecker));
  }

  private static SqlOperator createCalciteUDF(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeInference operandTypeInference, SqlOperandTypeChecker operandTypeChecker,
      List<RelDataType> paramTypes) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), returnTypeInference,
        operandTypeInference, operandTypeChecker, paramTypes, null);
  }

  private static SqlOperator createCalciteUDF(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), returnTypeInference, null,
        operandTypeChecker, null, null);
  }

  private static void addCastOperatorEntries() {
    String[] castFunctions =
        { "tok_boolean", "tok_int", "tok_string", "tok_double", "tok_float", "tok_bigint", "tok_tinyint", "tok_smallint", "tok_char", "tok_decimal", "tok_varchar", "tok_binary", "tok_date", "tok_timestamp" };
    for (String f : castFunctions) {
      FUNCTION_MAP.put(f, HiveFunction.CAST);
    }
  }

  /**
   * Returns a predicate to test if ordinal parameter is optional
   * @param ordinal parameter ordinal number
   * @return predicate to test if the parameter is optional
   */
  private static Predicate<Integer> optionalOrd(final int ordinal) {
    return input -> input == ordinal;
  }

  private static Predicate<Integer> optionalOrd(final List<Integer> ordinals) {
    return ordinals::contains;
  }
}

/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.Collection;
import java.util.List;
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
import com.linkedin.coral.common.functions.OperandTypeInference;
import com.linkedin.coral.common.functions.SameOperandTypeExceptFirstOperandChecker;

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

  // TODO: Make this immutable using builder
  static final Multimap<String, Function> FUNCTION_MAP = HashMultimap.create();

  static {

    // NOTE: All function names will be added as lowercase for case-insensitive comparison.
    // FIXME: This mapping is currently incomplete
    // aggregation functions
    addFunctionEntry("sum", SUM);
    addFunctionEntry("count", COUNT);
    addFunctionEntry("avg", AVG);
    addFunctionEntry("min", MIN);
    addFunctionEntry("max", MAX);

    // window functions
    addFunctionEntry("row_number", ROW_NUMBER);
    addFunctionEntry("rank", SqlStdOperatorTable.RANK); // qualification required due to naming conflict
    addFunctionEntry("dense_rank", DENSE_RANK);
    addFunctionEntry("cume_dist", CUME_DIST);
    addFunctionEntry("percent_rank", PERCENT_RANK);
    addFunctionEntry("first_value", FIRST_VALUE);
    addFunctionEntry("last_value", LAST_VALUE);
    addFunctionEntry("nth_value", NTH_VALUE);
    addFunctionEntry("lead", LEAD);
    addFunctionEntry("stddev", STDDEV);
    addFunctionEntry("stddev_samp", STDDEV_SAMP);
    addFunctionEntry("stddev_pop", STDDEV_POP);
    addFunctionEntry("variance", VARIANCE);
    addFunctionEntry("var_samp", VAR_SAMP);
    addFunctionEntry("var_pop", VAR_POP);

    // operators
    addFunctionEntry("!=", NOT_EQUALS);
    addFunctionEntry("==", EQUALS);

    // conditional function
    addFunctionEntry("nullif", NULLIF);

    // calcite models 'if' function as CASE operator. We can use CASE but that will cause translation
    // to SQL to be odd although correct. So, we add 'if' as UDF
    addFunctionEntry("if",
        createCalciteUDF("if", FunctionReturnTypes.IF_FUNC_RETURN_TYPE, OperandTypeInference.BOOLEAN_ANY_SAME,
            new SameOperandTypeExceptFirstOperandChecker(3, SqlTypeName.BOOLEAN), null));

    addFunctionEntry("coalesce", COALESCE);

    // Complex type constructors
    addFunctionEntry("array", ARRAY_VALUE_CONSTRUCTOR);

    // mathematical functions
    // we need to define new strategy for hive to allow null operands by default for everything
    createAddUserDefinedFunction("round", DOUBLE_NULLABLE,
        family(ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.INTEGER), optionalOrd(1)));
    createAddUserDefinedFunction("floor", BIGINT_FORCE_NULLABLE, family(SqlTypeFamily.NUMERIC));
    createAddUserDefinedFunction("ceil", BIGINT_FORCE_NULLABLE, family(SqlTypeFamily.NUMERIC));
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
    createAddUserDefinedFunction("cbrt", DOUBLE_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("greatest", ARG0_NULLABLE, SAME_VARIADIC);
    createAddUserDefinedFunction("least", ARG0_NULLABLE, SAME_VARIADIC);
    createAddUserDefinedFunction("width_bucket", INTEGER_NULLABLE,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC, SqlTypeFamily.INTEGER));

    // string functions
    // TODO: operand types are not strictly true since these functions can take null literal
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

    createAddUserDefinedFunction("initcap", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("length", INTEGER_NULLABLE, STRING);
    addFunctionEntry("lower", LOWER);
    addFunctionEntry("translate", TRANSLATE3);
    createAddUserDefinedFunction("ltrim", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("ngrams", LEAST_RESTRICTIVE,
        family(SqlTypeFamily.ARRAY, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER));
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
    createAddUserDefinedFunction("split", FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR), STRING_STRING);
    createAddUserDefinedFunction("substr", FunctionReturnTypes.STRING,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER), optionalOrd(2)));
    createAddUserDefinedFunction("substring", FunctionReturnTypes.STRING,
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.INTEGER), optionalOrd(2)));

    createAddUserDefinedFunction("trim", FunctionReturnTypes.STRING, STRING);
    addFunctionEntry("upper", UPPER);
    addFunctionEntry("initcap", INITCAP);
    createAddUserDefinedFunction("md5", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));
    createAddUserDefinedFunction("sha1", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));
    createAddUserDefinedFunction("crc32", FunctionReturnTypes.BIGINT,
        or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));

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
    createAddUserDefinedFunction("hour", ReturnTypes.INTEGER, or(STRING, DATETIME));
    createAddUserDefinedFunction("minute", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("second", ReturnTypes.INTEGER, STRING);
    //TODO: add extract UDF
    createAddUserDefinedFunction("datediff", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("date_add", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.DATE, SqlTypeFamily.INTEGER), family(SqlTypeFamily.TIMESTAMP, SqlTypeFamily.INTEGER),
            family(SqlTypeFamily.STRING, SqlTypeFamily.INTEGER)));

    createAddUserDefinedFunction("from_utc_timestamp", explicit(SqlTypeName.TIMESTAMP),
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    addFunctionEntry("current_date", CURRENT_DATE);
    addFunctionEntry("current_timestamp", CURRENT_TIMESTAMP);
    createAddUserDefinedFunction("date_format", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.DATE, SqlTypeFamily.STRING), family(SqlTypeFamily.TIMESTAMP, SqlTypeFamily.STRING),
            family(SqlTypeFamily.STRING, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("to_utc_timestamp", FunctionReturnTypes.STRING,
        or(STRING_STRING, family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING)));

    // Collection functions
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

    // Context functions
    addFunctionEntry("current_user", CURRENT_USER);

    // Trino specific functions
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

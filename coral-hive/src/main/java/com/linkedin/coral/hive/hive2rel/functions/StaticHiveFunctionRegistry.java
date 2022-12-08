/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.*;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.collect.HashMultimap;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.com.google.common.collect.Multimap;
import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionRegistry;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.common.functions.OperandTypeInference;
import com.linkedin.coral.common.functions.SameOperandTypeExceptFirstOperandChecker;

import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.*;
import static org.apache.calcite.sql.fun.SqlLibraryOperators.*;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.apache.calcite.sql.type.ReturnTypes.*;


/**
 * Static implementation of HiveFunctionRegistry that has hard-coded list of all
 * function names. This has a major disadvantage that the user defined functions are
 * not available to the registry without manually adding the entry here and uploading
 * a new version of library.
 * TODO: Provide function registry catalog
 *
 * Note that Coral maintains a copy of StaticHiveFunctionRegistry for read only at run time.
 * For individual query, we create a copy of function registry in a RelConTextProvider object.
 */
public class StaticHiveFunctionRegistry implements FunctionRegistry {

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
    createAddUserDefinedFunction("pmod", BIGINT, NUMERIC_NUMERIC);
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
    createAddUserDefinedFunction("concat", cascade(FunctionReturnTypes.STRING, SqlTypeTransforms.TO_NULLABLE),
        SAME_VARIADIC);
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
    createAddUserDefinedFunction("crc32", BIGINT, or(family(SqlTypeFamily.STRING), family(SqlTypeFamily.BINARY)));

    // xpath functions
    createAddUserDefinedFunction("xpath", FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR), STRING_STRING);
    createAddUserDefinedFunction("xpath_string", FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("xpath_boolean", ReturnTypes.BOOLEAN, STRING_STRING);
    createAddUserDefinedFunction("xpath_short", FunctionReturnTypes.SMALLINT, STRING_STRING);
    createAddUserDefinedFunction("xpath_int", ReturnTypes.INTEGER, STRING_STRING);
    createAddUserDefinedFunction("xpath_long", BIGINT, STRING_STRING);
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

    // LinkedIn UDFs: Dali stores mapping from UDF name to the implementing Java class as table properties
    // in the HCatalog. So, an UDF implementation may be referred by different names by different views.
    // We register these UDFs by the implementing class name to create a single entry for each UDF.
    createAddUserDefinedFunction("com.linkedin.dali.bug.DummyUdf", FunctionReturnTypes.STRING, or(STRING, ARRAY));
    createAddUserDefinedFunction(IS_TEST_MEMBER_ID_CLASS, ReturnTypes.BOOLEAN,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.CHARACTER));
    createAddUserDefinedFunction("com.linkedin.dali.udf.urnextractor.hive.UrnExtractor",
        FunctionReturnTypes.ARRAY_OF_STR_STR_MAP, or(STRING, ARRAY));
    createAddUserDefinedFunction("com.linkedin.udf.aws.ReadJsonUDF", FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.udf.hdfs.GetDatasetNameFromPathUDF", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.dali.udf.isguestmemberid.hive.IsGuestMemberId", ReturnTypes.BOOLEAN,
        NUMERIC);
    createAddUserDefinedFunction("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup",
        FunctionReturnTypes.rowOf(ImmutableList.of("iscrawler", "crawlerid"),
            ImmutableList.of(SqlTypeName.BOOLEAN, SqlTypeName.VARCHAR)),
        family(ImmutableList.of(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING),
            optionalOrd(ImmutableList.of(2, 3))));

    createAddUserDefinedFunction("com.linkedin.dali.udf.userinterfacelookup.hive.UserInterfaceLookup",
        FunctionReturnTypes.STRING,
        or(family(Collections.nCopies(8, SqlTypeFamily.STRING)),
            family(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING,
                SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.dali.udf.portallookup.hive.PortalLookup", FunctionReturnTypes.STRING,
        STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.dali.udf.useragentparser.hive.UserAgentParser",
        FunctionReturnTypes.STRING, STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.dali.udf.maplookup.hive.MapLookup",
        cascade(FunctionReturnTypes.STRING, SqlTypeTransforms.FORCE_NULLABLE),
        family(SqlTypeFamily.MAP, SqlTypeFamily.STRING, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.dali.udf.monarch.UrnGenerator", FunctionReturnTypes.STRING, VARIADIC);
    createAddUserDefinedFunction("com.linkedin.dali.udf.genericlookup.hive.GenericLookup", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.ANY,
            SqlTypeFamily.ANY),
            family(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.ANY,
                SqlTypeFamily.ANY, SqlTypeFamily.ANY)));
    createAddUserDefinedFunction("com.linkedin.tscp.reporting.dali.udfs.UrnToID", FunctionReturnTypes.STRING, STRING);

    createAddUserDefinedFunction("com.linkedin.dali.udf.date.hive.DateFormatToEpoch", BIGINT_NULLABLE,
        STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.dali.udf.date.hive.EpochToDateFormat", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds", BIGINT_NULLABLE, NUMERIC);
    createAddUserDefinedFunction("com.linkedin.dali.udf.sanitize.hive.Sanitize", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("org.apache.hadoop.hive.ql.udf.generic.GenericProject", ARG0,
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.dali.view.udf.entityhandles.GetIdFromUrn", BIGINT, STRING);
    createAddUserDefinedFunction("com.linkedin.dali.view.udf.entityhandles.GetPermissionsString",
        FunctionReturnTypes.STRING, family(SqlTypeFamily.ARRAY));
    createAddUserDefinedFunction("com.linkedin.dali.view.udf.entityhandles.EpochTimeInSeconds", BIGINT, STRING);
    createAddUserDefinedFunction("com.linkedin.dali.view.udf.entityhandles.EpochTimeInSecondsNullable", BIGINT_NULLABLE,
        STRING);
    createAddUserDefinedFunction("com.linkedin.dali.view.udf.entityhandles.IsUrnForType", ReturnTypes.BOOLEAN,
        STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.dali.view.udf.entityhandles.PhoneNumberNormalizer",
        FunctionReturnTypes.STRING, STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.dali.views.job.udf.GetUUID", FunctionReturnTypes.STRING, BINARY);
    createAddUserDefinedFunction("com.linkedin.dali.views.premium.udf.GetOrderUrn", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.MAP, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.dali.views.premium.udf.GetChooserId", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.MAP));
    createAddUserDefinedFunction("com.linkedin.dali.views.premium.udf.GetFamily", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.MAP));
    createAddUserDefinedFunction("com.linkedin.dali.views.premium.udf.GetPriceUrnList",
        FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR), family(SqlTypeFamily.MAP));

    final SqlReturnTypeInference hitInfo = FunctionReturnTypes.rowOfInference(
        ImmutableList.of("secondarysearchresultinfo", "entityawaresuggestioninfo"),
        ImmutableList.of(FunctionReturnTypes.rowOf(ImmutableList.of("vertical"), ImmutableList.of(SqlTypeName.VARCHAR)),
            FunctionReturnTypes.rowOfInference(ImmutableList.of("suggestedentities"),
                ImmutableList.of(FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR, true)))));

    final SqlReturnTypeInference gridPositionInfo = FunctionReturnTypes.rowOf(ImmutableList.of("row", "column"),
        ImmutableList.of(SqlTypeName.INTEGER, SqlTypeName.INTEGER));

    createAddUserDefinedFunction("com.linkedin.dali.views.search.udf.CreateSearchActionResultUDF",
        FunctionReturnTypes.rowOfInference(
            ImmutableList.of("entityurn", "resulttype", "absoluteposition", "positioninvertical", "iscachehit",
                "isanonymized", "hitinfo", "gridposition", "isnamematch", "trackingid"),
            ImmutableList.of(FunctionReturnTypes.STRING, FunctionReturnTypes.STRING, INTEGER_NULLABLE, INTEGER_NULLABLE,
                ReturnTypes.BOOLEAN, ReturnTypes.BOOLEAN, hitInfo, gridPositionInfo, ReturnTypes.BOOLEAN,
                FunctionReturnTypes.BINARY)),
        family(SqlTypeFamily.MAP, SqlTypeFamily.STRING, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.dali.views.search.udf.GetActionTypeUDF", FunctionReturnTypes.STRING,
        or(STRING_STRING_STRING, STRING_STRING));
    createAddUserDefinedFunction("com.linkedin.dali.views.search.udf.GetTYAHResultTypeUDF", FunctionReturnTypes.STRING,
        STRING);
    createAddUserDefinedFunction("com.linkedin.dali.views.search.udf.GetVerticalUDF", FunctionReturnTypes.STRING,
        or(STRING_STRING_STRING, STRING_STRING));
    createAddUserDefinedFunction("com.linkedin.dali.views.search.udf.IsTYAHSearchResultsUDF", ReturnTypes.BOOLEAN,
        STRING);
    createAddUserDefinedFunction("com.linkedin.dali.views.search.udf.IsValidKeyUDF", ReturnTypes.BOOLEAN,
        or(STRING_STRING_STRING, STRING_STRING));
    createAddUserDefinedFunction("com.linkedin.ds.udf.hive.filter.IsTestMemberId", ReturnTypes.BOOLEAN,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.ds.udf.hive.lookup.PortalLookup", FunctionReturnTypes.STRING,
        STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.ds.udf.hive.lookup.UserInterfaceLookup", FunctionReturnTypes.STRING,
        family(Collections.nCopies(8, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.ds.udf.hive.lookup.WATBotCrawlerLookup", FunctionReturnTypes
        .rowOf(ImmutableList.of("iscrawler", "crawlerid"), ImmutableList.of(SqlTypeName.BOOLEAN, SqlTypeName.VARCHAR)),
        or(STRING_STRING_STRING, STRING_STRING));
    createAddUserDefinedFunction("com.linkedin.dwh.udf.hive.lookup.OsLookup",
        FunctionReturnTypes.rowOf(ImmutableList.of("os_name", "os_major_version", "os_full_version"),
            ImmutableList.of(SqlTypeName.VARCHAR, SqlTypeName.VARCHAR, SqlTypeName.VARCHAR)),
        or(STRING_STRING, family(SqlTypeFamily.STRING, SqlTypeFamily.ANY)));
    createAddUserDefinedFunction("com.linkedin.dwh.udf.profile.GetProfileUrl", FunctionReturnTypes.STRING, family(
        SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.dwh.udf.sessionization.CleanupBrowserId", FunctionReturnTypes.STRING,
        STRING);
    createAddUserDefinedFunction("com.linkedin.etg.business.common.udfs.MapSfdcProductName", FunctionReturnTypes.STRING,
        STRING);
    createAddUserDefinedFunction("com.linkedin.etg.business.common.udfs.MapSfdcProductCode", FunctionReturnTypes.STRING,
        STRING);
    createAddUserDefinedFunction("com.linkedin.etg.business.common.udfs.MapSfdcProductId", ReturnTypes.INTEGER, STRING);
    createAddUserDefinedFunction("udfs.SeoReferrerTrkUdf", FunctionReturnTypes.STRING, STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.vector.daliview.udf.PresentDataType", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.ANY));
    createAddUserDefinedFunction("com.linkedin.vector.daliview.udf.PresentMediaType", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.ANY));
    createAddUserDefinedFunction("com.linkedin.vector.daliview.udf.UnifyVideoOrAudioDurationMicroSeconds", BIGINT,
        family(SqlTypeFamily.ANY));
    createAddUserDefinedFunction("com.linkedin.tscp.reporting.dali.udfs.AdClickClassifier", FunctionReturnTypes.rowOf(
        ImmutableList.of("clicks", "landingPageClicks", "totalEngagements", "otherEngagements", "likes", "commentLikes",
            "comments", "shares", "follows", "oneClickLeadFormOpens", "companyPageClicks", "fullScreenPlays",
            "viralClicks", "viralLandingPageClicks", "viralLikes", "viralCommentLikes", "viralComments", "viralShares",
            "viralFollows", "viralOneClickLeadFormOpens", "viralCompanyPageClicks", "viralFullScreenPlays",
            "viralTotalEngagements", "viralOtherEngagements", "adUnitClicks", "actionClicks", "textUrlClicks", "opens",
            "cardClicks", "viralCardClicks", "costInUsd", "costInLocalCurrency"),
        ImmutableList.of(SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER,
            SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER,
            SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER,
            SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER,
            SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER,
            SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER, SqlTypeName.INTEGER,
            SqlTypeName.INTEGER, SqlTypeName.DOUBLE, SqlTypeName.DOUBLE)),
        family(SqlTypeFamily.INTEGER, SqlTypeFamily.STRING, SqlTypeFamily.INTEGER, SqlTypeFamily.MAP,
            SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.MAP, SqlTypeFamily.ANY,
            SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.tscp.reporting.dali.udfs.UnifiedCampaignType",
        FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.tscp.reporting.dali.udfs.ActivityId", BIGINT, family(SqlTypeFamily.MAP));
    createAddUserDefinedFunction("com.linkedin.tscp.reporting.dali.udfs.AdPlacementClassifier",
        FunctionReturnTypes.STRING, family(SqlTypeFamily.INTEGER));
    createAddUserDefinedFunction("com.linkedin.tscp.reporting.dali.udfs.SponsoredMessageNodeId", ReturnTypes.INTEGER,
        family(SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.orbit.emerger.coercerudfs.DynamicsLineOfBusinessCoercer",
        FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.etg.business.common.udfs.MapD365OptionSet", FunctionReturnTypes.STRING,
        STRING_STRING_STRING);

    SqlReturnTypeInference getProfileSectionsReturnTypeInference = opBinding -> {
      int numArgs = opBinding.getOperandCount();
      Preconditions.checkState(numArgs == 2, "UDF isb.GetProfileSections must take 2 arguments.");
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      RelDataType retType = opBinding.getOperandType(0).getValueType();
      return typeFactory.createArrayType(retType, -1);
    };
    createAddUserDefinedFunction("isb.GetProfileSections", getProfileSectionsReturnTypeInference,
        family(SqlTypeFamily.MAP, SqlTypeFamily.ARRAY));

    createAddUserDefinedFunction("com.linkedin.recruiter.udf.GetEventOriginUDF", FunctionReturnTypes.STRING,
        or(STRING_STRING_STRING,
            family(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.recruiter.udf.QueryRoutingTypeUDF", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.recruiter.udf.SearchQueryUDF", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.snapshot.udf.ConstructSnapshotUrnUdf", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.snapshot.udf.SnapshotPurgeEligibleUdf", ReturnTypes.BOOLEAN,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING));

    // The following UDFs are already defined using Transport UDF.
    // The class name is the corresponding Hive UDF.
    // We point their class files to the corresponding Spark jar file in TransportableUDFMap.
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.DateFormatToEpoch", BIGINT_NULLABLE,
        STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.EpochToDateFormat", FunctionReturnTypes.STRING,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.EpochToEpochMilliseconds", BIGINT_NULLABLE,
        NUMERIC);
    createAddUserDefinedFunction("com.linkedin.stdudfs.stringudfs.hive.InitCap", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.IsGuestMemberId", ReturnTypes.BOOLEAN, NUMERIC);
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.MapLookup",
        cascade(FunctionReturnTypes.STRING, SqlTypeTransforms.FORCE_NULLABLE),
        family(SqlTypeFamily.MAP, SqlTypeFamily.STRING, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.PortalLookup", FunctionReturnTypes.STRING,
        STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.Sanitize", FunctionReturnTypes.STRING, STRING);
    createAddUserDefinedFunction("com.linkedin.jemslookup.udf.hive.JemsLookup", FunctionReturnTypes.rowOfInference(
        ImmutableList.of("jobproductid", "jobproductname", "jobentitlementids", "jobentitlementnameswithnamespace",
            "listingtype", "sublistingtype", "istestjob"),
        ImmutableList.of(BIGINT, FunctionReturnTypes.STRING, FunctionReturnTypes.arrayOfType(SqlTypeName.BIGINT, true),
            FunctionReturnTypes.arrayOfType(SqlTypeName.VARCHAR, true), FunctionReturnTypes.STRING,
            FunctionReturnTypes.STRING, ReturnTypes.BOOLEAN)),
        family(
            ImmutableList.of(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.stdudfs.userinterfacelookup.hive.UserInterfaceLookup",
        FunctionReturnTypes.STRING,
        or(family(Collections.nCopies(8, SqlTypeFamily.STRING)),
            family(SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING,
                SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING, SqlTypeFamily.STRING, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.stdudfs.parsing.hive.UserAgentParser", FunctionReturnTypes.STRING,
        family(Collections.nCopies(2, SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.stdudfs.parsing.hive.Ip2Str", FunctionReturnTypes.STRING,
        or(family(SqlTypeFamily.STRING, SqlTypeFamily.NUMERIC, SqlTypeFamily.NUMERIC), family(SqlTypeFamily.STRING)));
    createAddUserDefinedFunction("com.linkedin.stdudfs.lookup.hive.BrowserLookup",
        FunctionReturnTypes.rowOfInference(
            ImmutableList.of("browser_name", "browser_major_version", "browser_full_version"),
            ImmutableList.of(FunctionReturnTypes.STRING, FunctionReturnTypes.STRING, FunctionReturnTypes.STRING)),
        STRING_STRING_STRING);
    createAddUserDefinedFunction("com.linkedin.stdudfs.daliudfs.hive.IsTestMemberId", ReturnTypes.BOOLEAN,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", opBinding -> {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      return typeFactory.createArrayType(typeFactory.createMapType(typeFactory.createSqlType(SqlTypeName.VARCHAR),
          typeFactory.createSqlType(SqlTypeName.VARCHAR)), -1);
    }, or(ARRAY, STRING));
    createAddUserDefinedFunction("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", opBinding -> {
      RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
      return typeFactory.createArrayType(typeFactory.createMapType(typeFactory.createSqlType(SqlTypeName.VARCHAR),
          typeFactory.createSqlType(SqlTypeName.VARCHAR)), -1);
    }, or(ARRAY, STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMemberIdNumeric", BIGINT,
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMemberIdNumericInt", BIGINT,
        family(SqlTypeFamily.INTEGER, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMemberIdNumericLong", BIGINT,
        family(SqlTypeFamily.NUMERIC, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateAll", ARG0,
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateArray", ARG0,
        family(SqlTypeFamily.ARRAY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateArrayEvolve", ARG0,
        family(SqlTypeFamily.ARRAY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateStruct", ARG0,
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMap", ARG0,
        family(SqlTypeFamily.MAP, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMapEvolve", ARG0,
        family(SqlTypeFamily.MAP, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMapKeyEvolve", ARG0,
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.udfs.standard.hive.ObfuscateMapValEvolve", ARG0,
        family(SqlTypeFamily.ANY, SqlTypeFamily.STRING));
    createAddUserDefinedFunction("com.linkedin.jobs.udf.hive.ConvertIndustryCode", FunctionReturnTypes.STRING, STRING);
    // This is a Hive Custom UDF which is a simplified version of 'date-converter' package.
    // This UDF is not converted to a transport UDF.
    createAddUserDefinedFunction("com.linkedin.dali.customudf.date.hive.DateFormatToEpoch", BIGINT_NULLABLE,
        STRING_STRING_STRING);

    // UDTFs
    addFunctionEntry("explode", new CoralSqlUnnestOperator(false));
    addFunctionEntry("posexplode", new CoralSqlUnnestOperator(true));
    addFunctionEntry("json_tuple", HiveJsonTupleOperator.JSON_TUPLE);

    // reflect functions
    addFunctionEntry("reflect", HiveReflectOperator.REFLECT);
    addFunctionEntry("java_method", HiveReflectOperator.REFLECT);

    // Generic UDTFs
    createAddUserDefinedTableFunction("com.linkedin.tsar.hive.udf.ToJymbiiScores",
        ImmutableList.of("job_urn", "rank", "glmix_score", "global_model_score", "sentinel_score", "job_effect_score",
            "member_effect_score"),
        ImmutableList.of(SqlTypeName.VARCHAR, SqlTypeName.INTEGER, SqlTypeName.DOUBLE, SqlTypeName.DOUBLE,
            SqlTypeName.DOUBLE, SqlTypeName.DOUBLE, SqlTypeName.DOUBLE),
        family(SqlTypeFamily.ARRAY, SqlTypeFamily.ARRAY));

    // Context functions
    addFunctionEntry("current_user", CURRENT_USER);
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
   * @return immutable copy of internal function registry
   */
  public ImmutableMultimap<String, Function> getRegistry() {
    return ImmutableMultimap.copyOf(FUNCTION_MAP);
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

  /**
   * Adds the generic UDTF, which is almost same as how we register for LinkedIn UDFs except that we need to register
   * the return field names in `UDTF_RETURN_FIELD_NAME_MAP`
   */
  public static void createAddUserDefinedTableFunction(String functionName, ImmutableList<String> returnFieldNames,
      ImmutableList<?> returnFieldTypes, SqlOperandTypeChecker operandTypeChecker) {
    // The type of returnFieldTypes can only be ImmutableList<SqlTypeName> or ImmutableList<SqlReturnTypeInference>
    // ImmutableList<SqlTypeName> is used with FunctionReturnTypes.rowOf(ImmutableList<String> fieldNames, ImmutableList<SqlTypeName> types)
    // ImmutableList<SqlReturnTypeInference> is used with FunctionReturnTypes.rowOfInference(ImmutableList<String> fieldNames, ImmutableList<SqlReturnTypeInference> types)
    Preconditions.checkArgument(!returnFieldTypes.isEmpty() && returnFieldTypes.size() == returnFieldNames.size()
        && (returnFieldTypes.stream().allMatch(type -> type instanceof SqlTypeName)
            || returnFieldTypes.stream().allMatch(type -> type instanceof SqlReturnTypeInference)));
    if (returnFieldTypes.get(0) instanceof SqlTypeName) {
      createAddUserDefinedFunction(functionName,
          FunctionReturnTypes.rowOf(returnFieldNames,
              ImmutableList
                  .copyOf(returnFieldTypes.stream().map(type -> (SqlTypeName) type).collect(Collectors.toList()))),
          operandTypeChecker);
    } else {
      createAddUserDefinedFunction(functionName,
          FunctionReturnTypes.rowOfInference(returnFieldNames,
              ImmutableList.copyOf(
                  returnFieldTypes.stream().map(type -> (SqlReturnTypeInference) type).collect(Collectors.toList()))),
          operandTypeChecker);
    }
    UDTF_RETURN_FIELD_NAME_MAP.put(functionName, returnFieldNames);
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

  private static SqlOperator createCalciteUDF(String functionName, SqlReturnTypeInference returnTypeInference) {
    return createCalciteUDF(functionName, returnTypeInference, null);
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

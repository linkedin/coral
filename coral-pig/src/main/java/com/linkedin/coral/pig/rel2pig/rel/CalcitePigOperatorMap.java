/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.Map;

import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.com.google.common.collect.ImmutableSet;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.pig.rel2pig.rel.functions.Function;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigBuiltinFunction;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigIfFunction;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigLog2Function;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigLogFunction;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigRandomFunction;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigRoundFunction;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigSubstringFunction;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigUDF;


/**
 * Map from Calcite Builtin Functions to Pig Builtin Functions.
 */
public class CalcitePigOperatorMap {

  private CalcitePigOperatorMap() {

  }

  private static final Map<String, Function> UDF_MAP = ImmutableMap.<String, Function> builder()
      // Calcite Builtin Functions
      .put("base64", PigUDF.create("base64")).put("concat", PigUDF.create("concat"))
      .put("concat_ws", PigUDF.create("concat_ws")).put("conv", PigUDF.create("conv"))
      .put("decode", PigUDF.create("decode")).put("degrees", PigUDF.create("degrees")).put("e", PigUDF.create("e"))
      .put("factorial", PigUDF.create("factorial")).put("hex", PigUDF.create("hex"))
      .put("instr", PigUDF.create("instr")).put("negative", PigUDF.create("negative")).put("nvl", PigUDF.create("nvl"))
      .put("pi", PigUDF.create("pi")).put("positive", PigUDF.create("positive")).put("pow", PigUDF.create("pow"))
      .put("power", PigUDF.create("power")).put("radians", PigUDF.create("radians"))
      .put("regexp_replace", PigUDF.create("regexp_replace")).put("split", PigUDF.create("split"))
      .put("unbase64", PigUDF.create("unbase64")).put("unhex", PigUDF.create("unhex"))
      // Dali UDFs
      .put(StaticHiveFunctionRegistry.IS_TEST_MEMBER_ID_CLASS,
          PigUDF.create(StaticHiveFunctionRegistry.IS_TEST_MEMBER_ID_CLASS, ImmutableSet.of(1)))
      .put("com.linkedin.dali.view.udf.entityhandles.GetIdFromUrn",
          PigUDF.create("com.linkedin.dali.view.udf.entityhandles.GetIdFromUrn"))
      // Pig Builtin Functions
      .put("abs", PigBuiltinFunction.create("ABS")).put("atan", PigBuiltinFunction.create("ATAN"))
      .put("acos", PigBuiltinFunction.create("ACOS")).put("cbrt", PigBuiltinFunction.create("CBRT"))
      .put("ceil", PigBuiltinFunction.create("CEIL")).put("ceiling", PigBuiltinFunction.create("CEIL"))
      .put("cos", PigBuiltinFunction.create("COS")).put("exp", PigBuiltinFunction.create("EXP"))
      .put("floor", PigBuiltinFunction.create("FLOOR")).put("log10", PigBuiltinFunction.create("LOG10"))
      .put("lcase", PigBuiltinFunction.create("LOWER")).put("ln", PigBuiltinFunction.create("LOG"))
      .put("lower", PigBuiltinFunction.create("LOWER")).put("ltrim", PigBuiltinFunction.create("LTRIM"))
      .put("regexp_extract", PigBuiltinFunction.create("REGEX_EXTRACT"))
      .put("rtrim", PigBuiltinFunction.create("RTRIM")).put("sin", PigBuiltinFunction.create("SIN"))
      .put("sqrt", PigBuiltinFunction.create("SQRT")).put("tan", PigBuiltinFunction.create("TAN"))
      .put("trim", PigBuiltinFunction.create("TRIM")).put("upper", PigBuiltinFunction.create("UPPER"))
      .put("ucase", PigBuiltinFunction.create("UPPER"))
      // Special Pig Builtin Functions
      .put("if", PigIfFunction.create()).put("log", PigLogFunction.create()).put("log2", PigLog2Function.create())
      .put("rand", PigRandomFunction.create()).put("round", PigRoundFunction.create())
      .put("substr", PigSubstringFunction.create()).put("substring", PigSubstringFunction.create()).build();

  /**
   * Returns the UDF with a given functionName in lowercase.
   * If a UDF is not found for the given functionName, null is returned.
   *
   * @param functionName Name of a function
   * @return UDF for the function
   */
  public static Function lookup(String functionName) {
    return lookup(functionName.toLowerCase(), false);
  }

  /**
   * Returns the UDF with a given functionName.
   * If caseSensitive is set to true, the casing of functionName is preserved.
   * If caseSensitive is set to false, the functionName is set to lower case.
   * If a UDF is not found for the given functionName, null is returned.
   *
   * @param functionName Name of a function
   * @param caseSensitive Set as true if a case-sensitive lookup to be performed. Else, set to false for lowercase.
   * @return UDF for the function
   */
  public static Function lookup(String functionName, Boolean caseSensitive) {
    final String functionLookupName = caseSensitive ? functionName : functionName.toLowerCase();
    return UDF_MAP.getOrDefault(functionLookupName, null);
  }

}

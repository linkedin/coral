/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.Collection;
import java.util.Map;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.OperatorRenameSqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.transformers.CoralRegistryOperatorRenameSqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.DateAddOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.DateDiffOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.DateSubOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.DecodeOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.GenericCoralRegistryOperatorRenameSqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.ModOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.RandomIntegerOperatorWithTwoOperandsTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.RandomOperatorWithOneOperandTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.RegexpExtractOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.ToDateOperatorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.TruncateOperatorTransformer;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


/**
 * This class extends the class of {@link SqlShuttle} and initialize a {@link SqlCallTransformers}
 * which containing a list of {@link com.linkedin.coral.common.transformers.SqlCallTransformer}to traverse the hierarchy of a {@link SqlCall}
 * and converts the functions from Coral operator to Trino operator if it is required
 */
public class CoralToTrinoSqlCallConverter extends SqlShuttle {
  private static final StaticHiveFunctionRegistry HIVE_FUNCTION_REGISTRY = new StaticHiveFunctionRegistry();
  private final SqlCallTransformers sqlCallTransformers;

  public CoralToTrinoSqlCallConverter(Map<String, Boolean> configs) {
    this.sqlCallTransformers = SqlCallTransformers.of(
        // conditional functions
        new CoralRegistryOperatorRenameSqlCallTransformer("nvl", 2, "coalesce"),
        // array and map functions
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR"),
        // math functions
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"),
        new RandomOperatorWithOneOperandTransformer(),
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"),
        new RandomIntegerOperatorWithTwoOperandsTransformer(), new TruncateOperatorTransformer(),
        // string functions
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR"),
        // JSON functions
        new CoralRegistryOperatorRenameSqlCallTransformer("get_json_object", 2, "json_extract"),
        // map various hive functions
        new ModOperatorTransformer(), new CoralRegistryOperatorRenameSqlCallTransformer("base64", 1, "to_base64"),
        new CoralRegistryOperatorRenameSqlCallTransformer("unbase64", 1, "from_base64"),
        new CoralRegistryOperatorRenameSqlCallTransformer("hex", 1, "to_hex"),
        new CoralRegistryOperatorRenameSqlCallTransformer("unhex", 1, "from_hex"),
        new CoralRegistryOperatorRenameSqlCallTransformer("array_contains", 2, "contains"),
        new RegexpExtractOperatorTransformer(), new CoralRegistryOperatorRenameSqlCallTransformer("instr", 2, "strpos"),
        new DecodeOperatorTransformer(), new DateAddOperatorTransformer(), new DateSubOperatorTransformer(),
        new DateDiffOperatorTransformer(),
        new ToDateOperatorTransformer(configs.getOrDefault(AVOID_TRANSFORM_TO_DATE_UDF, false)),

        // LinkedIn specific functions
        new CoralRegistryOperatorRenameSqlCallTransformer(
            "com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup", 3, "wat_bot_crawler_lookup"),
        new CoralRegistryOperatorRenameSqlCallTransformer("com.linkedin.stdudfs.parsing.hive.Ip2Str", 1, "ip2str"),
        new CoralRegistryOperatorRenameSqlCallTransformer("com.linkedin.stdudfs.parsing.hive.Ip2Str", 3, "ip2str"),
        new CoralRegistryOperatorRenameSqlCallTransformer("com.linkedin.stdudfs.parsing.hive.UserAgentParser", 2,
            "useragentparser"),
        new CoralRegistryOperatorRenameSqlCallTransformer("com.linkedin.stdudfs.lookup.hive.BrowserLookup", 3,
            "browserlookup"),
        new CoralRegistryOperatorRenameSqlCallTransformer("com.linkedin.jobs.udf.hive.ConvertIndustryCode", 1,
            "converttoindustryv1"),
        new CoralRegistryOperatorRenameSqlCallTransformer(
            "com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", 1, "urn_extractor"),
        new CoralRegistryOperatorRenameSqlCallTransformer(
            "com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", 1, "urn_extractor"),
        new GenericCoralRegistryOperatorRenameSqlCallTransformer());
  }

  public static SqlOperator hiveToCoralSqlOperator(String functionName) {
    Collection<Function> lookup = HIVE_FUNCTION_REGISTRY.lookup(functionName);
    return lookup.iterator().next().getSqlOperator();
  }

  @Override
  public SqlNode visit(SqlCall call) {
    SqlCall transformedCall = sqlCallTransformers.apply(call);
    return super.visit(transformedCall);
  }
}

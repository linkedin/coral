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
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.JsonTransformSqlCallTransformer;
import com.linkedin.coral.common.transformers.OperatorRenameSqlCallTransformer;
import com.linkedin.coral.common.transformers.SourceOperatorMatchSqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformers;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;
import com.linkedin.coral.trino.rel2trino.transformers.CollectListOrSetFunctionTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.CoralRegistryOperatorRenameSqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.CurrentTimestampTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.GenericCoralRegistryOperatorRenameSqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.MapValueConstructorTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.ReturnTypeAdjustmentTransformer;
import com.linkedin.coral.trino.rel2trino.transformers.ToDateOperatorTransformer;

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
        new MapValueConstructorTransformer(),
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 3, "SUBSTR"),
        new SourceOperatorMatchSqlCallTransformer("item", 2) {
          @Override
          protected SqlCall transform(SqlCall sqlCall) {
            return TrinoElementAtFunction.INSTANCE.createCall(SqlParserPos.ZERO, sqlCall.getOperandList());
          }
        }, new CollectListOrSetFunctionTransformer(),
        // math functions
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.RAND, 0, "RANDOM"),
        new JsonTransformSqlCallTransformer(SqlStdOperatorTable.RAND, 1, "RANDOM", "[]", null, null),
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 1, "RANDOM"),
        new JsonTransformSqlCallTransformer(SqlStdOperatorTable.RAND_INTEGER, 2, "RANDOM", "[{\"input\":2}]", null,
            null),
        new JsonTransformSqlCallTransformer(SqlStdOperatorTable.TRUNCATE, 2, "TRUNCATE",
            "[{\"op\":\"*\",\"operands\":[{\"input\":1},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
            "{\"op\":\"/\",\"operands\":[{\"input\":0},{\"op\":\"^\",\"operands\":[{\"value\":10},{\"input\":2}]}]}",
            null),
        // string functions
        new OperatorRenameSqlCallTransformer(SqlStdOperatorTable.SUBSTRING, 2, "SUBSTR"),
        // JSON functions
        new CoralRegistryOperatorRenameSqlCallTransformer("get_json_object", 2, "json_extract"),
        // map various hive functions
        new JsonTransformSqlCallTransformer(hiveToCoralSqlOperator("pmod"), 2, "mod",
            "[{\"op\":\"+\",\"operands\":[{\"op\":\"%\",\"operands\":[{\"input\":1},{\"input\":2}]},{\"input\":2}]},{\"input\":2}]",
            null, null),
        new CoralRegistryOperatorRenameSqlCallTransformer("base64", 1, "to_base64"),
        new CoralRegistryOperatorRenameSqlCallTransformer("unbase64", 1, "from_base64"),
        new CoralRegistryOperatorRenameSqlCallTransformer("hex", 1, "to_hex"),
        new CoralRegistryOperatorRenameSqlCallTransformer("unhex", 1, "from_hex"),
        new CoralRegistryOperatorRenameSqlCallTransformer("array_contains", 2, "contains"),
        new JsonTransformSqlCallTransformer(hiveToCoralSqlOperator("regexp_extract"), 3, "regexp_extract",
            "[{\"input\": 1}, {\"op\": \"hive_pattern_to_trino\", \"operands\":[{\"input\": 2}]}, {\"input\": 3}]",
            null, null),
        new OperatorRenameSqlCallTransformer(HiveRLikeOperator.REGEXP, 2, "REGEXP_LIKE"),
        new OperatorRenameSqlCallTransformer(HiveRLikeOperator.RLIKE, 2, "REGEXP_LIKE"),
        new CoralRegistryOperatorRenameSqlCallTransformer("instr", 2, "strpos"),
        new JsonTransformSqlCallTransformer(hiveToCoralSqlOperator("decode"), 2,
            "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]", "[{\"input\":1}]", null, null),
        new JsonTransformSqlCallTransformer(hiveToCoralSqlOperator("date_add"), 2, "date_add",
            "[{\"value\": 'day'}, {\"input\": 2},  "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null),
        new JsonTransformSqlCallTransformer(hiveToCoralSqlOperator("date_sub"), 2, "date_add",
            "[{\"value\": 'day'}, " + "{\"op\": \"*\", \"operands\":[{\"input\": 2}, {\"value\": -1}]}, "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null),
        new JsonTransformSqlCallTransformer(hiveToCoralSqlOperator("datediff"), 2, "date_diff",
            "[{\"value\": 'day'}, {\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 2}]}]}, "
                + "{\"op\": \"date\", \"operands\":[{\"op\": \"timestamp\", \"operands\":[{\"input\": 1}]}]}]",
            null, null),
        new ToDateOperatorTransformer(configs.getOrDefault(AVOID_TRANSFORM_TO_DATE_UDF, false)),
        new CurrentTimestampTransformer(),

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
        new GenericCoralRegistryOperatorRenameSqlCallTransformer(),

        new ReturnTypeAdjustmentTransformer(configs));
  }

  private SqlOperator hiveToCoralSqlOperator(String functionName) {
    Collection<Function> lookup = HIVE_FUNCTION_REGISTRY.lookup(functionName);
    return lookup.iterator().next().getSqlOperator();
  }

  @Override
  public SqlNode visit(SqlCall call) {
    return sqlCallTransformers.apply((SqlCall) super.visit(call));
  }
}

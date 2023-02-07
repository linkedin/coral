/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import org.apache.calcite.sql.SqlIdentifier;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.transformers.SqlIdentifierTransformer;


/**
 * This is a subclass of {@link SqlIdentifierTransformer} which transforms a Coral operator which exists
 * in a SqlIdentifier to a Trino operator by renaming the operator
 * e.g. a SqlIdentifier of "com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup"('a', 'b', 'c')
 * is transformed into a SqlIdentifier of "wat_bot_crawler_lookup"('a', 'b', 'c')
 */
public class GenericCoralRegistryOperatorRenameSqlIdentifierTransformer extends SqlIdentifierTransformer {
  private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("\\\"com\\.linkedin\\..+\\(.+\\)");
  private static final Map<String, String> FUNCTION_SIGNATURE_MAP =
      ImmutableMap.of("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup_3", "wat_bot_crawler_lookup");

  @Override
  protected boolean condition(SqlIdentifier sqlIdentifier) {
    if (sqlIdentifier.names.size() == 2) {
      Matcher matcher = FUNCTION_CALL_PATTERN.matcher(sqlIdentifier.names.get(0));
      return matcher.find();
    }
    return false;
  }

  @Override
  protected SqlIdentifier transform(SqlIdentifier sqlIdentifier) {
    String[] identifiers = sqlIdentifier.names.get(0).split("\\(");
    int numOperands = identifiers[1].split(",").length;
    String sourceFunctionName = identifiers[0].replaceAll("\"", "");
    String key = sourceFunctionName + "_" + numOperands;
    String targetFunctionName;
    if (FUNCTION_SIGNATURE_MAP.containsKey(key)) {
      targetFunctionName = FUNCTION_SIGNATURE_MAP.get(key);
    } else {
      targetFunctionName = deriveDefaultTargetFunctionName(sourceFunctionName);
    }
    String newName = "\"" + targetFunctionName + "\"(" + identifiers[1];
    sqlIdentifier.names = ImmutableList.of(newName, sqlIdentifier.names.get(1));
    return sqlIdentifier;
  }

  private String deriveDefaultTargetFunctionName(String fromFunctionName) {
    Converter<String, String> caseConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    String[] nameSplit = fromFunctionName.split("\\.");
    String className = nameSplit[nameSplit.length - 1];
    return caseConverter.convert(className);
  }
}

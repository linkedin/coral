/**
 * Copyright 2019-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.Function;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;


/**
 * Class that represents Dali versioned UDFs
 */
public class VersionedSqlUserDefinedFunction extends SqlUserDefinedFunction {

  // Predefined map that associates class names with their corresponding short function names.
  private static final Map<String, String> SHORT_FUNC_NAME_MAP = ImmutableMap.<String, String> builder()
      .put("com.linkedin.dali.udf.watbotcrawlerlookup.hive.WATBotCrawlerLookup", "wat_bot_crawler_lookup")
      .put("com.linkedin.stdudfs.parsing.hive.Ip2Str", "ip2str")
      .put("com.linkedin.stdudfs.parsing.hive.UserAgentParser", "useragentparser")
      .put("com.linkedin.stdudfs.lookup.hive.BrowserLookup", "browserlookup")
      .put("com.linkedin.jobs.udf.hive.ConvertIndustryCode", "converttoindustryv1")
      .put("com.linkedin.stdudfs.urnextractor.hive.UrnExtractorFunctionWrapper", "urn_extractor")
      .put("com.linkedin.stdudfs.hive.daliudfs.UrnExtractorFunctionWrapper", "urn_extractor")
      .put("com.linkedin.groot.runtime.udf.spark.HasMemberConsentUDF", "has_member_consent")
      .put("com.linkedin.groot.runtime.udf.spark.RedactFieldIfUDF", "redact_field_if")
      .put("com.linkedin.groot.runtime.udf.spark.RedactSecondarySchemaFieldIfUDF", "redact_secondary_schema_field_if")
      .put("com.linkedin.groot.runtime.udf.spark.GetMappedValueUDF", "get_mapped_value")
      .put("com.linkedin.coral.hive.hive2rel.CoralTestUDF", "coral_test").build();

  // The list of dependencies specified found in the view's "dependencies" property.
  // Example: "ivy://com.linkedin.udf-group:udf-artifact:0.1.8"
  private final List<String> ivyDependencies;

  // The view-dependent function name in the format of "dbName_viewName_functionName",
  // where functionName is defined in the "functions" property of the view.
  private final String viewDependentFunctionName;

  // The UDF class name value defined in the "functions" property of the view.
  // i.e. "functions = <viewDependentFunctionName> : <udfClassName>"
  private final String udfClassName;

  private VersionedSqlUserDefinedFunction(SqlIdentifier opName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeInference operandTypeInference, SqlOperandTypeChecker operandTypeChecker,
      List<RelDataType> paramTypes, Function function, List<String> ivyDependencies, String viewDependentFunctionName,
      String udfClassName) {
    super(opName, returnTypeInference, operandTypeInference, operandTypeChecker, paramTypes, function,
        SqlFunctionCategory.USER_DEFINED_FUNCTION);
    this.ivyDependencies = ivyDependencies;
    this.viewDependentFunctionName = viewDependentFunctionName;
    this.udfClassName = udfClassName;
  }

  public VersionedSqlUserDefinedFunction(SqlUserDefinedFunction sqlUdf, List<String> ivyDependencies,
      String viewDependentFunctionName, String udfClassName) {
    this(new SqlIdentifier(ImmutableList.of(sqlUdf.getName()), SqlParserPos.ZERO), sqlUdf.getReturnTypeInference(),
        null, sqlUdf.getOperandTypeChecker(), sqlUdf.getParamTypes(), sqlUdf.getFunction(), ivyDependencies,
        viewDependentFunctionName, udfClassName);
  }

  public List<String> getIvyDependencies() {
    return ivyDependencies;
  }

  public String getViewDependentFunctionName() {
    return viewDependentFunctionName;
  }

  /**
   * Retrieves the short function name based on the class name. If the class name is found
   * in the predefined {@link VersionedSqlUserDefinedFunction#SHORT_FUNC_NAME_MAP},
   * the corresponding short name is returned. Otherwise, the method converts the last
   * segment of the class name from UPPER_CAMEL to LOWER_UNDERSCORE format to generate
   * the short function name.
   */
  public String getShortFunctionName() {
    String unversionedClassName = getName();
    if (SHORT_FUNC_NAME_MAP.containsKey(unversionedClassName)) {
      return SHORT_FUNC_NAME_MAP.get(unversionedClassName);
    }
    Converter<String, String> caseConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    String[] nameSplit = unversionedClassName.split("\\.");
    return caseConverter.convert(nameSplit[nameSplit.length - 1]);
  }

  public String getUDFClassName() {
    return udfClassName;
  }

  // This method is called during SQL validation. The super-class implementation resets the call's sqlOperator to one
  // that is looked up from the StaticHiveFunctionRegistry or inferred dynamically if it's a Dali UDF. Since UDFs in the StaticHiveFunctionRegistry are not
  // versioned, this method overrides the super-class implementation to properly restore the call's operator as
  // a VersionedSqlUserDefinedFunction based on the already existing call's sqlOperator obtained from the
  // StaticHiveFunctionRegistry, and hence preserve ivyDependencies and viewDependentFunctionName.
  @Override
  public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
    RelDataType relDataType = super.deriveType(validator, scope, call);
    ((SqlBasicCall) call).setOperator(new VersionedSqlUserDefinedFunction((SqlUserDefinedFunction) (call.getOperator()),
        ivyDependencies, viewDependentFunctionName, udfClassName));
    return relDataType;
  }
}

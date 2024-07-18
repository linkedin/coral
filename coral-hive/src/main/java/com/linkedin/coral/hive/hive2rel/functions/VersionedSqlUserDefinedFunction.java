/**
 * Copyright 2019-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

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

import static com.linkedin.coral.hive.hive2rel.functions.utils.FunctionUtils.*;


/**
 * Class that represents Dali versioned UDFs
 */
public class VersionedSqlUserDefinedFunction extends SqlUserDefinedFunction {

  // The list of dependencies specified found in the view's "dependencies" property.
  // Example: "ivy://com.linkedin.udf-group:udf-artifact:0.1.8"
  private final List<String> ivyDependencies;

  // The view-dependent function name in the format of "dbName_viewName_functionName",
  // where functionName is defined in the "functions" property of the view.
  private final String viewDependentFunctionName;

  private VersionedSqlUserDefinedFunction(SqlIdentifier opName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeInference operandTypeInference, SqlOperandTypeChecker operandTypeChecker,
      List<RelDataType> paramTypes, Function function, List<String> ivyDependencies, String viewDependentFunctionName) {
    super(opName, returnTypeInference, operandTypeInference, operandTypeChecker, paramTypes, function,
        SqlFunctionCategory.USER_DEFINED_FUNCTION);
    this.ivyDependencies = ivyDependencies;
    this.viewDependentFunctionName = viewDependentFunctionName;
  }

  public VersionedSqlUserDefinedFunction(String name, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker, List<RelDataType> paramTypes, Function function,
      List<String> ivyDependencies, String viewDependentFunctionName) {
    this(new SqlIdentifier(ImmutableList.of(name), SqlParserPos.ZERO), returnTypeInference, null, operandTypeChecker,
        paramTypes, function, ivyDependencies, viewDependentFunctionName);
  }

  public VersionedSqlUserDefinedFunction(SqlUserDefinedFunction sqlUdf, List<String> ivyDependencies,
      String viewDependentFunctionName) {
    this(new SqlIdentifier(ImmutableList.of(sqlUdf.getName()), SqlParserPos.ZERO), sqlUdf.getReturnTypeInference(),
        null, sqlUdf.getOperandTypeChecker(), sqlUdf.getParamTypes(), sqlUdf.getFunction(), ivyDependencies,
        viewDependentFunctionName);
  }

  public List<String> getIvyDependencies() {
    return ivyDependencies;
  }

  public String getViewDependentFunctionName() {
    return viewDependentFunctionName;
  }

  /**
   * Get the versioned function name based on the `viewDependentFunctionName` and UDF class name.
   * For example, if the function name is "myFunction" and the class name is "coral_udf_version_1_0_0.com.linkedin.MyClass",
   * the versioned function name will be "myFunction_1_0_0". If the class name is not versioned, such as "com.linkedin.MyClass",
   * the versioned function name will be "myFunction".
   */
  public String getVersionedViewDependentFunctionName() {
    String className = getName();
    String versionedPrefix = className.substring(0, className.indexOf('.'));
    Matcher matcher = Pattern.compile(CORAL_VERSIONED_UDF_PREFIX).matcher(versionedPrefix);
    if (matcher.find()) {
      return String.join("_",
          ImmutableList.of(viewDependentFunctionName, matcher.group(1), matcher.group(2), matcher.group(3)));
    } else {
      return viewDependentFunctionName;
    }
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
        ivyDependencies, viewDependentFunctionName));
    return relDataType;
  }
}

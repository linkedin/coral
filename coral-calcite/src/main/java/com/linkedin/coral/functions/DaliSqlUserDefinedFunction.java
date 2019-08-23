package com.linkedin.coral.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.Function;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Class to represent Dali UDF objects
 */
public class DaliSqlUserDefinedFunction extends SqlUserDefinedFunction {

  // The list of dependencies information specified in TBLPROPERTIES clause.
  // Example: "ivy://com.linkedin.identity-datasets:isb-get-profile-sections:0.1.8"
  // We maintain a list of dependencies because it is possible to have multiple dependencies
  // if a Dali view has multiple UDF.
  private final List<String> daliUdfDependencies;
  // The expanded function name in the format of "dbName_viewName_funcBaseName",
  // where funcBaseNmae is defined in TBLPROPERTIES clause.  For example,
  // suppose db name is "default", view name is "foo_dali_udf", funcBaseName is "LessThanHundred",
  // then the expandedFunctionName is "default_foo_dali_udf_LessThanHundred".
  // expandedFunctionName is saved when we parse the view definition at run time.
  private final String expandedFunctionName;

  private DaliSqlUserDefinedFunction(SqlIdentifier opName,
      SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeInference operandTypeInference,
      SqlOperandTypeChecker operandTypeChecker,
      List<RelDataType> paramTypes,
      Function function,
      List<String> daliUdfDependencies,
      String expandedFunctionName) {
    super(opName, returnTypeInference, operandTypeInference, operandTypeChecker, paramTypes,
        function,
        SqlFunctionCategory.USER_DEFINED_FUNCTION);
    this.daliUdfDependencies = daliUdfDependencies;
    this.expandedFunctionName = expandedFunctionName;
  }

  public DaliSqlUserDefinedFunction(String name,
      SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker,
      List<RelDataType> paramTypes,
      Function function,
      List<String> daliUdfDependencies,
      String expandedFunctionName) {
    this(new SqlIdentifier(ImmutableList.of(name), SqlParserPos.ZERO),
        returnTypeInference,
        null,
        operandTypeChecker,
        paramTypes,
        function,
        daliUdfDependencies,
        expandedFunctionName);
  }

  public DaliSqlUserDefinedFunction(
      SqlUserDefinedFunction sqlUdf,
      List<String> daliUdfDependencies,
      String expandedFunctionName) {
    this(new SqlIdentifier(ImmutableList.of(sqlUdf.getName()), SqlParserPos.ZERO),
        sqlUdf.getReturnTypeInference(),
        null,
        sqlUdf.getOperandTypeChecker(),
        sqlUdf.getParamTypes(),
        sqlUdf.getFunction(),
        daliUdfDependencies,
        expandedFunctionName);
  }

  public List<String> getDaliUdfDependencies() {
    return daliUdfDependencies;
  }

  public String getExpandedFunctionName() {
    return expandedFunctionName;
  }

}


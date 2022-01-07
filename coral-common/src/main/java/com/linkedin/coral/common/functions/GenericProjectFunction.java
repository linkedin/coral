/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.util.Static;


/**
 * GenericProjectFunction is a place holder function that takes two parameters
 *   - a reference to a column
 *   - a literal representing the name of the column passed as the first parameter
 *
 * The first parameter is necessary to pass type information regarding the columns to the proceeding rewriters.
 * The second parameter is necessary to pass the name of the column to rewriters that need to know the column name
 * in the RexCall/RexNode (Relational Algebra) phase.
 *
 * Currently, this GenericProject UDF is used internally to indicate that coral-spark/coral-trino need to perform
 * rewrites to further update the query because there is an inconsistent branch in a union function.
 *
 * The GenericProject function should be limited to be used by internal APIs and not in external Dali Views.
 * This is because it is difficult to derive the return type of an external GenericProject call in a Dali View.
 */
public class GenericProjectFunction extends SqlUserDefinedFunction {
  public static final GenericProjectFunction GENERIC_PROJECT = new GenericProjectFunction(null);

  private final RelDataType tableDataType;

  public GenericProjectFunction(RelDataType tableDataType) {
    super(new SqlIdentifier("generic_project", SqlParserPos.ZERO), null, null, null, null, null);
    this.tableDataType = tableDataType;
  }

  @Override
  public RelDataType inferReturnType(final SqlOperatorBinding opBinding) {
    return tableDataType;
  }

  @Override
  public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
    return tableDataType;
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.of(2);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    if (callBinding.getOperandCount() != 2) {
      if (throwOnFailure) {
        throw callBinding.newError(Static.RESOURCE.invalidArgCount("GenericProject", callBinding.getOperandCount()));
      }
      return false;
    }

    // The first operand must be a reference to a column.
    // However, Calcite will resolve the type of the SqlIdentifier instead.
    // Therefore, it can be any type and we do not need to check.

    // The second operand must be a string literal.
    SqlNode literal = callBinding.operand(1);
    RelDataType secondOperandDataType = callBinding.getValidator().getValidatedNodeType(literal);
    if (SqlUtil.isNull(literal) || !SqlTypeFamily.STRING.contains(secondOperandDataType)) {
      if (throwOnFailure) {
        throw callBinding.newError(Static.RESOURCE.incompatibleTypes());
      }
      return false;
    }

    return true;
  }

  @Override
  protected void checkOperandCount(SqlValidator validator, SqlOperandTypeChecker argTypeChecker, SqlCall call) {
    if (call.operandCount() == 2) {
      return;
    }
    throw validator.newValidationError(call, Static.RESOURCE.wrongNumOfArguments());
  }
}

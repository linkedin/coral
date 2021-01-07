/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.ExplicitOperatorBinding;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.ComparableOperandTypeChecker;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorImpl;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import static org.apache.calcite.util.Static.*;


/**
 * Class to implement SQL IN operator.
 * This is currently only tested for IN (&lt;values&gt;) but can be
 * extended to support subqueries as well.
 * We reimplement this separate from SqlStdOperator.IN operator because:
 *  1. For IN operators, calcite SqlToRel converter turn IN clause into OR predicates.
 *     We want to preserve IN clause because destination engine may handle IN clauses
 *     different from set of OR predicates
 *  2. In some cases, calcite turns IN clause to INNER JOIN query on a set of values. We
 *     again want to prevent this conversion.
 */
public class HiveInOperator extends SqlSpecialOperator {

  public static final HiveInOperator IN = new HiveInOperator();

  public HiveInOperator() {
    super("IN", SqlKind.OTHER, 32, true, ReturnTypes.BOOLEAN_NULLABLE, InferTypes.FIRST_KNOWN, null);
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.any();
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    call.operand(0).unparse(writer, getLeftPrec(), getRightPrec());
    writer.sep(getName());
    SqlWriter.Frame listFrame = writer.startList("(", ")");
    for (int i = 1; i < call.operandCount(); i++) {
      writer.sep(",");
      call.operand(i).unparse(writer, getLeftPrec(), getRightPrec());
    }
    writer.endList(listFrame);
  }

  // copied from Calcite' SqlInOperator
  public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
    final List<SqlNode> operands = call.getOperandList();
    assert operands.size() == 2;
    final SqlNode left = operands.get(0);
    final SqlNode right = operands.get(1);

    final RelDataTypeFactory typeFactory = validator.getTypeFactory();
    RelDataType leftType = validator.deriveType(scope, left);
    RelDataType rightType;

    // Derive type for RHS.
    if (right instanceof SqlNodeList) {
      // Handle the 'IN (expr, ...)' form.
      List<RelDataType> rightTypeList = new ArrayList<>();
      SqlNodeList nodeList = (SqlNodeList) right;
      for (int i = 0; i < nodeList.size(); i++) {
        SqlNode node = nodeList.get(i);
        RelDataType nodeType = validator.deriveType(scope, node);
        rightTypeList.add(nodeType);
      }
      rightType = typeFactory.leastRestrictive(rightTypeList);

      // First check that the expressions in the IN list are compatible
      // with each other. Same rules as the VALUES operator (per
      // SQL:2003 Part 2 Section 8.4, <in predicate>).
      if (null == rightType && validator.isTypeCoercionEnabled()) {
        // Do implicit type cast if it is allowed to.
        rightType = validator.getTypeCoercion().getWiderTypeFor(rightTypeList, true);
      }
      if (null == rightType) {
        throw validator.newValidationError(right, RESOURCE.incompatibleTypesInList());
      }

      // Record the RHS type for use by SqlToRelConverter.
      ((SqlValidatorImpl) validator).setValidatedNodeType(nodeList, rightType);
    } else {
      // Handle the 'IN (query)' form.
      rightType = validator.deriveType(scope, right);
    }
    SqlCallBinding callBinding = new SqlCallBinding(validator, scope, call);
    // Coerce type first.
    if (callBinding.getValidator().isTypeCoercionEnabled()) {
      boolean coerced = callBinding.getValidator().getTypeCoercion().inOperationCoercion(callBinding);
      if (coerced) {
        // Update the node data type if we coerced any type.
        leftType = validator.deriveType(scope, call.operand(0));
        rightType = validator.deriveType(scope, call.operand(1));
      }
    }

    // Now check that the left expression is compatible with the
    // type of the list. Same strategy as the '=' operator.
    // Normalize the types on both sides to be row types
    // for the purposes of compatibility-checking.
    RelDataType leftRowType = SqlTypeUtil.promoteToRowType(typeFactory, leftType, null);
    RelDataType rightRowType = SqlTypeUtil.promoteToRowType(typeFactory, rightType, null);

    final ComparableOperandTypeChecker checker =
        (ComparableOperandTypeChecker) OperandTypes.COMPARABLE_UNORDERED_COMPARABLE_UNORDERED;
    if (!checker.checkOperandTypes(
        new ExplicitOperatorBinding(callBinding, ImmutableList.of(leftRowType, rightRowType)), callBinding)) {
      throw validator.newValidationError(call, RESOURCE.incompatibleValueType(SqlStdOperatorTable.IN.getName()));
    }

    // Result is a boolean, nullable if there are any nullable types
    // on either side.
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BOOLEAN),
        anyNullable(leftRowType.getFieldList()) || anyNullable(rightRowType.getFieldList()));
  }

  private static boolean anyNullable(List<RelDataTypeField> fieldList) {
    for (RelDataTypeField field : fieldList) {
      if (field.getType().isNullable()) {
        return true;
      }
    }
    return false;
  }
}

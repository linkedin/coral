/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlMapValueConstructor;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;


/**
 * Hive's {@code map(k1, v1, k2, v2, ...)} constructor.
 *
 * Calcite's {@link SqlMapValueConstructor} requires all keys (and all values) to have a least restrictive common
 * type, so {@code map('k1', 'v1', 'k2', 2)} fails validation with "Parameters must be of the same type". Hive
 * accepts it: {@code GenericUDFMap} resolves keys and values with {@code ReturnObjectInspectorResolver(true)}, which
 * converts mixed string and numeric (or string and date/timestamp) arguments to {@code string}
 * (see {@code FunctionRegistry.getCommonClass} and {@code getPrimitiveCommonCategory} in Hive 2.3).
 *
 * This operator applies the same rule before Calcite's checks: when the keys (or the values) have no Calcite common
 * type but Hive would convert them to {@code string}, the non-string operands are wrapped in
 * {@code CAST(operand AS STRING)}, i.e. exactly what Hive does at runtime. The coercion mirrors Calcite's own
 * implicit type coercion ({@code AbstractTypeCoercion#coerceOperandType}), which also rewrites call operands during
 * validation. Argument lists Calcite already accepts are left untouched, and combinations Hive rejects
 * (e.g. {@code boolean} and {@code int}) still fail with the original error.
 */
public class HiveMapValueConstructor extends SqlMapValueConstructor {

  public static final HiveMapValueConstructor MAP_VALUE_CONSTRUCTOR = new HiveMapValueConstructor();

  // Hive's numeric widening order (TypeInfoUtils.numericTypes), where string is the widest "numeric" type
  private static final int NOT_NUMERIC = -1;
  private static final int STRING_RANK = 8;

  @Override
  public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
    for (SqlNode operand : call.getOperandList()) {
      validator.deriveType(scope, operand);
    }
    // Unlike SqlOperator#deriveType, do not look "MAP" up in the operator table: that would replace this operator
    // with Calcite's SqlMapValueConstructor and skip the Hive coercion in checkOperandTypes.
    return adjustType(validator, call, validateOperands(validator, scope, call));
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    final SqlCall call = callBinding.getCall();
    final int operandCount = call.operandCount();
    // An odd number of arguments is reported by the parent class
    if (operandCount > 0 && operandCount % 2 == 0) {
      final List<RelDataType> argTypes =
          SqlTypeUtil.deriveAndCollectTypes(callBinding.getValidator(), callBinding.getScope(), call.getOperandList());
      coerceToHiveCommonType(callBinding, argTypes, 0);
      coerceToHiveCommonType(callBinding, argTypes, 1);
    }
    return super.checkOperandTypes(callBinding, throwOnFailure);
  }

  /**
   * Coerces the keys ({@code parity == 0}) or the values ({@code parity == 1}) of the map constructor to
   * {@code string} if that is what Hive would do and Calcite cannot find a common type on its own.
   */
  private static void coerceToHiveCommonType(SqlCallBinding callBinding, List<RelDataType> argTypes, int parity) {
    final List<RelDataType> types = new ArrayList<>();
    for (int i = parity; i < argTypes.size(); i += 2) {
      types.add(argTypes.get(i));
    }
    if (callBinding.getTypeFactory().leastRestrictive(types) != null || !hiveConvertsToString(types)) {
      return;
    }

    final SqlValidator validator = callBinding.getValidator();
    final SqlValidatorScope scope = callBinding.getScope();
    final SqlCall call = callBinding.getCall();
    for (int i = parity; i < argTypes.size(); i += 2) {
      final RelDataType type = argTypes.get(i);
      if (SqlTypeUtil.isCharacter(type) || type.getSqlTypeName() == SqlTypeName.NULL) {
        continue;
      }
      final SqlNode operand = call.operand(i);
      final SqlParserPos pos = operand.getParserPosition();
      // Same type spec as Hive's `CAST(x AS STRING)` produces in ParseTreeBuilder
      final SqlDataTypeSpec stringType = new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.VARCHAR, pos), pos);
      final SqlNode cast = SqlStdOperatorTable.CAST.createCall(pos, operand, stringType);
      call.setOperand(i, cast);
      validator.deriveType(scope, cast);
    }
  }

  /**
   * Returns true if Hive's {@code ReturnObjectInspectorResolver} (with type conversion allowed) would resolve the
   * given argument types to {@code string}, false if it would resolve them to another type or reject them.
   * NULL arguments are ignored, as in Hive.
   */
  private static boolean hiveConvertsToString(List<RelDataType> types) {
    SqlTypeName common = null;
    for (RelDataType type : types) {
      final SqlTypeName typeName = type.getSqlTypeName();
      if (typeName == SqlTypeName.NULL) {
        continue;
      }
      if (common == null) {
        common = typeName;
        continue;
      }
      common = hiveCommonPrimitiveType(common, typeName);
      if (common == null) {
        return false;
      }
    }
    return common != null && SqlTypeName.CHAR_TYPES.contains(common);
  }

  /**
   * Calcite counterpart of Hive's {@code FunctionRegistry.getPrimitiveCommonCategory}, returning
   * {@link SqlTypeName#VARCHAR} for Hive's {@code string} and null when Hive has no common type.
   */
  private static SqlTypeName hiveCommonPrimitiveType(SqlTypeName a, SqlTypeName b) {
    if (a == b) {
      return a;
    }
    final boolean aIsString = SqlTypeName.CHAR_TYPES.contains(a);
    final boolean bIsString = SqlTypeName.CHAR_TYPES.contains(b);
    if (aIsString && bIsString) {
      return SqlTypeName.VARCHAR;
    }
    if ((aIsString && isHiveDateGroup(b)) || (bIsString && isHiveDateGroup(a))) {
      return SqlTypeName.VARCHAR;
    }
    final int aRank = hiveNumericRank(a);
    final int bRank = hiveNumericRank(b);
    if (aRank == NOT_NUMERIC || bRank == NOT_NUMERIC) {
      return null;
    }
    final int rank = Math.max(aRank, bRank);
    if (rank == STRING_RANK) {
      return SqlTypeName.VARCHAR;
    }
    return aRank >= bRank ? a : b;
  }

  private static boolean isHiveDateGroup(SqlTypeName typeName) {
    return typeName == SqlTypeName.DATE || typeName == SqlTypeName.TIMESTAMP;
  }

  private static int hiveNumericRank(SqlTypeName typeName) {
    switch (typeName) {
      case TINYINT:
        return 1;
      case SMALLINT:
        return 2;
      case INTEGER:
        return 3;
      case BIGINT:
        return 4;
      case DECIMAL:
        return 5;
      case FLOAT:
      case REAL:
        return 6;
      case DOUBLE:
        return 7;
      case CHAR:
      case VARCHAR:
        return STRING_RANK;
      default:
        return NOT_NUMERIC;
    }
  }
}

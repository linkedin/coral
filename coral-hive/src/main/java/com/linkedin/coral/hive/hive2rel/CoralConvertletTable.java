/**
 * Copyright 2018-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlCastFunction;
import org.apache.calcite.sql2rel.ReflectiveConvertletTable;
import org.apache.calcite.sql2rel.SqlRexContext;
import org.apache.calcite.sql2rel.SqlRexConvertlet;
import org.apache.calcite.sql2rel.StandardConvertletTable;


/**
 * ConvertletTable for transformations only relevant to Coral's Intermediate Representation, not specific
 * any SQL dialect. These transformations keep data parity between the SqlNode and RelNode layer, keeping the IR intact.
 * @see ReflectiveConvertletTable documentation for method naming and visibility rules
 */
public class CoralConvertletTable extends ReflectiveConvertletTable {

  //  @SuppressWarnings("unused")
  //  public RexNode convertFunctionFieldReferenceOperator(SqlRexContext cx, FunctionFieldReferenceOperator op,
  //      SqlCall call) {
  //    RexNode funcExpr = cx.convertExpression(call.operand(0));
  //    String fieldName = FunctionFieldReferenceOperator.fieldNameStripQuotes(call.operand(1));
  //    return cx.getRexBuilder().makeFieldAccess(funcExpr, fieldName, false);
  //  }

  /**
   * Override {@link StandardConvertletTable#convertCast} to avoid cast optimizations that remove the cast.
   */
  @SuppressWarnings("unused")
  public RexNode convertCast(SqlRexContext cx, SqlCastFunction cast, SqlCall call) {
    final SqlNode left = call.operand(0);
    RexNode leftRex = cx.convertExpression(left);
    SqlDataTypeSpec dataType = call.operand(1);
    RelDataType castType = dataType.deriveType(cx.getValidator(), true);
    return cx.getRexBuilder().makeAbstractCast(castType, leftRex);
  }

  @Override
  public SqlRexConvertlet get(SqlCall call) {
    SqlRexConvertlet convertlet = super.get(call);
    return convertlet != null ? convertlet : StandardConvertletTable.INSTANCE.get(call);
  }
}

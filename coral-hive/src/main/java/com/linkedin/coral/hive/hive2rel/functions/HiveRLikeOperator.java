/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeUtil;


public class HiveRLikeOperator extends SqlSpecialOperator {

  public static final HiveRLikeOperator RLIKE = new HiveRLikeOperator("RLIKE", false);
  public static final HiveRLikeOperator REGEXP = new HiveRLikeOperator("REGEXP", false);

  private final boolean negated;

  /**
   * Creates an operator to represent Hive's RLIKE operator as calcite operator
   *  @param name   Operator name
   * @param negated Whether this is 'NOT LIKE'
   */
  public HiveRLikeOperator(String name, boolean negated) {
    super(name, SqlKind.LIKE, 32, false, ReturnTypes.BOOLEAN_NULLABLE, InferTypes.FIRST_KNOWN,
        OperandTypes.STRING_SAME_SAME);
    this.negated = negated;
  }

  public boolean isNegated() {
    return negated;
  }

  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.of(2);
  }

  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    return OperandTypes.STRING_SAME_SAME.checkOperandTypes(callBinding, throwOnFailure)
        && SqlTypeUtil.isCharTypeComparable(callBinding, callBinding.operands(), throwOnFailure);
  }

  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    final SqlWriter.Frame frame = writer.startList("", "");
    call.operand(0).unparse(writer, getLeftPrec(), getRightPrec());
    writer.sep(getName());

    call.operand(1).unparse(writer, getLeftPrec(), getRightPrec());
    writer.endList(frame);
  }
}

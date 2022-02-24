/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlValidatorImpl;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;


public class HiveSqlValidator extends SqlValidatorImpl {

  public HiveSqlValidator(SqlOperatorTable opTab, CalciteCatalogReader catalogReader, JavaTypeFactory typeFactory,
      SqlConformance conformance) {
    super(opTab, catalogReader, typeFactory, conformance);
    setDefaultNullCollation(NullCollation.LOW);
  }

  @Override
  protected RelDataType getLogicalSourceRowType(RelDataType sourceRowType, SqlInsert insert) {
    final RelDataType superType = super.getLogicalSourceRowType(sourceRowType, insert);
    return ((JavaTypeFactory) typeFactory).toSql(superType);
  }

  @Override
  protected RelDataType getLogicalTargetRowType(RelDataType targetRowType, SqlInsert insert) {
    final RelDataType superType = super.getLogicalTargetRowType(targetRowType, insert);
    return ((JavaTypeFactory) typeFactory).toSql(superType);
  }

  @Override
  protected void inferUnknownTypes(RelDataType inferredType, SqlValidatorScope scope, SqlNode node) {
    if (SqlUtil.isNullLiteral(node, false)) {
      setValidatedNodeType(node, typeFactory.createSqlType(SqlTypeName.NULL));
      return;
    }
    super.inferUnknownTypes(inferredType, scope, node);
  }

  @Override
  public SqlNode expand(SqlNode expr, SqlValidatorScope scope) {
    if (expr instanceof SqlBasicCall
        && ((SqlBasicCall) expr).getOperator().equals(FunctionFieldReferenceOperator.DOT)) {
      SqlBasicCall dotCall = (SqlBasicCall) expr;
      if (dotCall.operand(0) instanceof SqlBasicCall) {
        return expr;
      }
    }
    return super.expand(expr, scope);
  }
}

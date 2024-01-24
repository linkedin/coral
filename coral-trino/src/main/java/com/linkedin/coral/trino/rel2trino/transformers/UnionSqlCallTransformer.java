/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlCastFunction;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.POS;
import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


/**
 * This transformer class is used to adapt expressions in
 * set statements (e.g.: `UNION`, `INTERSECT`, `MINUS`)
 * in case that the branches of the set statement contain fields in
 * char family which have different types.
 * The `char` fields which are differing from the expected `varchar` output
 * of the set statement will be adapted through an explicit `CAST` to the `varchar` type.
 *
 * @see <a href="https://github.com/trinodb/trino/issues/9031">Change char/varchar coercion in Trino</a>
 */
public class UnionSqlCallTransformer extends SqlCallTransformer {

  public UnionSqlCallTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().kind == SqlKind.UNION || sqlCall.getOperator().kind == SqlKind.INTERSECT
        || sqlCall.getOperator().kind == SqlKind.MINUS;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> operandsList = sqlCall.getOperandList();
    if (sqlCall.getOperandList().isEmpty()) {
      return sqlCall;
    }
    Integer selectListSize = null;
    // Ensure all the operand are SELECT nodes and they have the same number of expressions projected
    for (SqlNode operand : operandsList) {
      if (operand.getKind() == SqlKind.SELECT && ((SqlSelect) operand).getSelectList() != null) {
        SqlSelect select = (SqlSelect) operand;
        List<SqlNode> selectList = select.getSelectList().getList();
        if (selectListSize == null) {
          selectListSize = selectList.size();
        } else if (selectListSize != selectList.size()) {
          return sqlCall;
        }
      } else {
        return sqlCall;
      }
    }

    List<Optional<RelDataType>> leastRestrictiveSelectItemTypes = new ArrayList<>(selectListSize);
    for (int i = 0; i < selectListSize; i++) {
      List<RelDataType> selectItemTypes = new ArrayList<>();
      boolean selectItemTypesDerived = true;
      for (SqlNode operand : operandsList) {
        SqlSelect select = (SqlSelect) operand;
        List<SqlNode> selectList = select.getSelectList().getList();
        SqlNode selectItem = selectList.get(i);
        if (selectItem.getKind() == SqlKind.IDENTIFIER && ((SqlIdentifier) selectItem).isStar()) {
          // The type derivation of * on the SqlNode layer is not supported now.
          return sqlCall;
        }

        try {
          selectItemTypes.add(deriveRelDatatype(selectItem));
        } catch (RuntimeException e) {
          // The type derivation may fail for complex expressions
          selectItemTypesDerived = false;
          break;
        }
      }

      Optional<RelDataType> leastRestrictiveSelectItemType =
          selectItemTypesDerived ? Optional.ofNullable(leastRestrictive(selectItemTypes)) : Optional.empty();
      leastRestrictiveSelectItemTypes.add(leastRestrictiveSelectItemType);
    }

    boolean operandsUpdated = false;
    for (SqlNode operand : operandsList) {
      SqlSelect select = (SqlSelect) operand;
      List<SqlNode> selectList = select.getSelectList().getList();
      List<SqlNode> rewrittenSelectList = null;
      for (int i = 0; i < selectList.size(); i++) {
        SqlNode selectItem = selectList.get(i);
        if (!leastRestrictiveSelectItemTypes.get(i).isPresent()) {
          // Couldn't determine the type for all the expressions corresponding to the selection index
          if (rewrittenSelectList != null) {
            rewrittenSelectList.add(selectItem);
          }
          continue;
        }
        RelDataType leastRestrictiveSelectItemType = leastRestrictiveSelectItemTypes.get(i).get();
        RelDataType selectItemType = deriveRelDatatype(selectItem);
        if (selectItemType.getSqlTypeName() == SqlTypeName.CHAR
            && leastRestrictiveSelectItemType.getSqlTypeName() == SqlTypeName.VARCHAR) {
          // Work-around for the Trino limitation in dealing UNION statements between `char` and `varchar`.
          // See https://github.com/trinodb/trino/issues/9031
          SqlNode rewrittenSelectItem = castNode(selectItem, leastRestrictiveSelectItemType);
          if (rewrittenSelectList == null) {
            rewrittenSelectList = new ArrayList<>(selectListSize);
            rewrittenSelectList.addAll(selectList.subList(0, i));
            operandsUpdated = true;
          }
          rewrittenSelectList.add(rewrittenSelectItem);
        } else if (rewrittenSelectList != null) {
          rewrittenSelectList.add(selectItem);
        }
      }

      if (rewrittenSelectList != null) {
        select.setSelectList(new SqlNodeList(rewrittenSelectList, SqlParserPos.ZERO));
      }
    }

    if (operandsUpdated) {
      return sqlCall.getOperator().createCall(POS, operandsList);
    }

    return sqlCall;
  }

  private SqlNode castNode(SqlNode node, RelDataType type) {
    if (node.getKind() == SqlKind.AS) {
      SqlNode expression = ((SqlCall) node).getOperandList().get(0);
      SqlIdentifier identifier = (SqlIdentifier) ((SqlCall) node).getOperandList().get(1);
      return SqlStdOperatorTable.AS.createCall(POS, new SqlCastFunction() {
        @Override
        public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
          SqlCallBinding opBinding = new SqlCallBinding(validator, scope, call);
          return inferReturnType(opBinding);
        }
      }.createCall(ZERO, expression, getSqlDataTypeSpec(type)), identifier);
    } else {
      // If there's no existing alias, just do the cast
      return new SqlCastFunction() {
        @Override
        public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
          SqlCallBinding opBinding = new SqlCallBinding(validator, scope, call);
          return inferReturnType(opBinding);
        }
      }.createCall(ZERO, node, getSqlDataTypeSpec(type));
    }
  }

  private static SqlDataTypeSpec getSqlDataTypeSpec(RelDataType relDataType) {
    final SqlTypeNameSpec typeNameSpec = new SqlBasicTypeNameSpec(relDataType.getSqlTypeName(),
        relDataType.getPrecision(), relDataType.getScale(), null, ZERO);
    return new SqlDataTypeSpec(typeNameSpec, ZERO);
  }
}

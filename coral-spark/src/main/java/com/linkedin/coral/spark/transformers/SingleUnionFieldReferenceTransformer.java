/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTypeNameSpec;
import org.apache.calcite.sql.fun.SqlCase;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * This transformer focuses on SqlCalls that involve a FunctionFieldReferenceOperator with the following characteristics:
 * (1) The first operand is a SqlBasicCall with a non-struct RelDataType, and the second operand is tag_0.
 * This indicates that the first operand represents a Union data type with a single data type inside.
 * (2) Examples of such SqlCalls include extract_union(product.value).tag_0 or (extract_union(product.value).id).tag_0.
 * (3) The transformation for such SqlCalls is to return the first operand.
 */
public class SingleUnionFieldReferenceTransformer extends SqlCallTransformer {
  private static final String TAG_0_OPERAND = "tag_0";

  public SingleUnionFieldReferenceTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return (sqlCall.getOperator().kind == SqlKind.SELECT || (sqlCall instanceof SqlBasicCall && FunctionFieldReferenceOperator.DOT.getName().equalsIgnoreCase(sqlCall.getOperator().getName())));
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {

    if (sqlCall.getOperator().kind == SqlKind.SELECT) {
      List<SqlNode> newSelectNodes = new ArrayList<>();
      SqlSelect selectCall = ((SqlSelect) sqlCall);
      for (SqlNode selectItem : selectCall.getSelectList()) {
        if (!(selectItem instanceof SqlCall)) {
          continue;
        }
        SqlBasicCall functionCall = null;


        if (FunctionFieldReferenceOperator.DOT.getName().equalsIgnoreCase(((SqlCall) selectItem).getOperator().getName())) {
          functionCall = ((SqlCall) selectItem).operand(0);
        }

        if (functionCall != null && isExtractUnionOnSingleUnionType(functionCall)) {
          newSelectNodes.add(functionCall.operand(0));
        } else {
          newSelectNodes.add(selectItem);
        }
      }

      selectCall.setSelectList(new SqlNodeList(newSelectNodes, SqlParserPos.ZERO));
    } else if (sqlCall.getOperator().kind == SqlKind.AS) {

      List<SqlNode> newAliasOperands = new ArrayList<>();

      SqlNode aliasFirstOperand = sqlCall.operand(0);

      if (aliasFirstOperand instanceof SqlBasicCall && FunctionFieldReferenceOperator.DOT.getName().equalsIgnoreCase(((SqlBasicCall) aliasFirstOperand).getOperator().getName())) {
        if (isExtractUnionOnSingleUnionType(((SqlBasicCall) aliasFirstOperand).operand(0))) {
          newAliasOperands.add(((SqlBasicCall) (((SqlBasicCall) aliasFirstOperand).operand(0))).operand(0));
          newAliasOperands.add(sqlCall.operand(1));

          return SqlStdOperatorTable.AS.createCall(ZERO, newAliasOperands);
        }
      }

      return sqlCall;

    } else if (sqlCall instanceof SqlBasicCall) {
      SqlBasicCall call = (SqlBasicCall) sqlCall;
//      if (call.getOperator().kind == SqlKind.AS) {
//        // Aliases are handled as part of the select list for SqlSelect calls
//        return sqlCall;
//      }

      List<SqlNode> operands = Arrays.asList(call.getOperands());
      for (int i = 0; i < operands.size(); i++) {
        SqlNode operand = operands.get(i);
        if (operand instanceof SqlBasicCall && FunctionFieldReferenceOperator.DOT.getName().equalsIgnoreCase(((SqlBasicCall) operand).getOperator().getName())) {
          if (isExtractUnionOnSingleUnionType(((SqlCall) operand).operand(0))) {

            sqlCall.setOperand(i, ((SqlCall) ((SqlCall) operand).operand(0)).operand(0));
          }
        }
      }

    }

    return sqlCall;
  }

  private boolean isExtractUnionOnSingleUnionType(SqlBasicCall sqlBasicCall) {
    RelDataType sqlBasicCallType = deriveRelDatatype(sqlBasicCall);
    return sqlBasicCallType.isStruct()
          && sqlBasicCallType.getFieldList().size() == 1
          && sqlBasicCallType.getFieldList().get(0).getKey().equalsIgnoreCase(TAG_0_OPERAND);
  }

  private SqlDataTypeSpec getSqlDataTypeSpecForCasting(RelDataType relDataType) {
    final SqlTypeNameSpec typeNameSpec = new SqlBasicTypeNameSpec(relDataType.getSqlTypeName(),
        relDataType.getPrecision(), relDataType.getScale(), null, ZERO);
    return new SqlDataTypeSpec(typeNameSpec, ZERO);
  }
}

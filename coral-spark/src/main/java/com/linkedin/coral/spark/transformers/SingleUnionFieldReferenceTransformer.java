/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.transformers;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.functions.FunctionFieldReferenceOperator;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility;
import com.linkedin.coral.spark.functions.NOP;


/**
 * Necessary context: Spark has a mechanism that unwraps uniontypes that only hold one datatype, to simply, the underlying datatype itself.
 * For example, if we have a column "single_union_col" in Hive, of type uniontype&lt;array&lt;string&gt;&gt;&gt;, Spark will unwrap single_union_col to simply array&lt;string&gt; and treat it as such.
 * For this reason, we must be careful about that we are actually passing a struct type (that represents Hive's uniontype) into the
 * {@link CoalesceStructUtility} function, and not the underlying data type. Note the conversion from extract_union to coalesce_struct happens after this transformer.
 *
 * This transformer transforms on function field references for extract_union(union_col) calls where union_col is a single uniontype, in other words,
 * an incorrect attempt to fetch the underlying object in the uniontype.
 *
 * Examples of such function field references SqlCalls include:
 * 1) extract_union(product.value).tag_0
 *    In this case, we simply want to return product.value as we know that product.value is a no longer a uniontype (been unwrapped by Spark).
 * 2) (extract_union(product.value).urns).tag_0.
 *    In this case, what we're actually dealing with is calling extract_union on a regular struct (not a struct in Spark representing a uniontype) that
 *    has a field urns, where urns is now the underlying data type before it was unwrapped from a single uniontype. So once again,
 *    we don't want to (and can't) access "tag_0" field of urns. Rather simply return it as is: extract_union(product.value).urns
 */
public class SingleUnionFieldReferenceTransformer extends SqlCallTransformer {
  private static final String TAG_0_OPERAND = "tag_0";
  public static final String functionFieldReferenceOperatorName = FunctionFieldReferenceOperator.DOT.getName();

  public SingleUnionFieldReferenceTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall instanceof SqlBasicCall && isSingleUnionFieldReference((SqlBasicCall) sqlCall);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlBasicCall functionFieldReference = (SqlBasicCall) sqlCall;

    assert functionFieldReference.operand(0) instanceof SqlBasicCall;
    SqlBasicCall functionCall = functionFieldReference.operand(0);

    if (functionFieldReferenceOperatorName.equalsIgnoreCase(functionCall.getOperator().getName())) {
      // (extract_union(struct).single_union).tag_0 -> extract_union(struct).single_union
      return functionCall;
    }

    // extract_union(single_union).tag_0 -> single_union
    // Since the transformer contract requires we return a SqlCall object, we need to wrap the SqlIdentifier in a NOP operator.
    // Instantiating a SqlBasicCall with the NOP operator instead of directly creating a NOP call avoids
    // parentheses wrapped around the SqlIdentifier.
    return new SqlBasicCall(new NOP(), new SqlNode[] { functionCall.operand(0) }, SqlParserPos.ZERO);
  }

  private boolean isSingleUnionFieldReference(SqlBasicCall sqlCall) {
    if (functionFieldReferenceOperatorName.equalsIgnoreCase(sqlCall.getOperator().getName())) {
      if (sqlCall.operand(0) instanceof SqlBasicCall) {
        SqlBasicCall functionCall = sqlCall.operand(0);
        return isExtractUnionOnSingleUnionType(functionCall);
      }
    }
    return false;
  }

  private boolean isExtractUnionOnSingleUnionType(SqlBasicCall sqlBasicCall) {
    // Also captures the extract_union(struct).single_union case where we call extract_union on a struct containing a single uniontype

    RelDataType sqlBasicCallType = deriveRelDatatype(sqlBasicCall);
    return sqlBasicCallType.isStruct() && sqlBasicCallType.getFieldList().size() == 1
        && sqlBasicCallType.getFieldList().get(0).getKey().equalsIgnoreCase(TAG_0_OPERAND);
  }
}

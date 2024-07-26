/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlArrayTypeSpec;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlRowTypeSpec;
import org.apache.calcite.sql.fun.SqlCastFunction;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import com.linkedin.coral.common.HiveTypeSystem;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.common.utils.TypeDerivationUtil;
import com.linkedin.coral.hive.hive2rel.functions.HiveNamedStructFunction;

import static org.apache.calcite.sql.parser.SqlParserPos.ZERO;


/**
 * This transformer converts Coral IR function `named_struct` to a Trino compatible representation.
 * For example, the SqlCall: `named_struct('abc', 123, 'def', 'xyz')` will be transformed to
 * `CAST(ROW(123, 'xyz') AS ROW(`abc` INTEGER, `def` CHAR(3) CHARACTER SET `ISO-8859-1`))`
 */
public class NamedStructToCastTransformer extends SqlCallTransformer {

  public NamedStructToCastTransformer(TypeDerivationUtil typeDerivationUtil) {
    super(typeDerivationUtil);
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().equals(HiveNamedStructFunction.NAMED_STRUCT);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> inputOperands = sqlCall.getOperandList();

    List<SqlDataTypeSpec> rowTypes = new ArrayList<>();
    List<String> fieldNames = new ArrayList<>();
    for (int i = 0; i < inputOperands.size(); i += 2) {
      assert inputOperands.get(i) instanceof SqlLiteral;
      fieldNames.add(((SqlLiteral) inputOperands.get(i)).getStringValue());
    }

    List<SqlNode> rowCallOperands = new ArrayList<>();
    for (int i = 1; i < inputOperands.size(); i += 2) {
      rowCallOperands.add(inputOperands.get(i));
      RelDataType type = deriveRelDatatype(inputOperands.get(i));
      SqlDataTypeSpec sqlDataTypeSpec = SqlTypeUtil.convertTypeToSpec(type);
      // When the Coral IR operator `named_struct` has a field with value as an empty array, for example:
      // NAMED_STRUCT('value', ARRAY())
      // the array is assigned type VARCHAR in the transformed SqlCall to be compatible with Trino, for example:
      // CAST(ROW(ARRAY[]) AS ROW('value' ARRAY<VARCHAR(65535)>))
      if (sqlDataTypeSpec instanceof SqlArrayTypeSpec
          && ((SqlArrayTypeSpec) sqlDataTypeSpec).getElementTypeSpec().toString().equalsIgnoreCase("null")) {
        int defaultVarcharPrecision = new HiveTypeSystem().getDefaultPrecision(SqlTypeName.VARCHAR);
        sqlDataTypeSpec = new SqlArrayTypeSpec(
            new SqlDataTypeSpec(new SqlBasicTypeNameSpec(SqlTypeName.VARCHAR, defaultVarcharPrecision, ZERO), ZERO),
            ZERO);
      }
      rowTypes.add(sqlDataTypeSpec);
    }
    SqlNode rowCall = SqlStdOperatorTable.ROW.createCall(ZERO, rowCallOperands);

    // This following override enables operand relDatatType derivation for a nested named_struct() SqlCalls.
    // For a SqlCall:
    // `named_struct('outerF1', 123, 'outerF2', 'xyz', 'nestedNamedStruct', named_struct('innerF1', 123, 'innerF2', 'xyz'))`
    // the inner `named_struct()`, is visited first and transformed to its Trino compatible representation:
    // `CAST(ROW(123, 'xyz') AS ROW(`innerF1` INTEGER, `innerF2` CHAR(3) CHARACTER SET `ISO-8859-1`))`
    // When the outer named_struct() is visited, the transformer accepts input sqlCall:
    // `named_struct('outerF1', 123, 'outerF2', 'xyz',
    //                'nestedNamedStruct', CAST(ROW(123, 'xyz') AS ROW(`innerF1` INTEGER, `innerF2` CHAR(3) CHARACTER SET `ISO-8859-1`)))`
    // and fails to derive the relDataType for its operand: nestedNamedStruct in format - CAST(ROW() AS ROW())
    // The following overriden deriveType() implementation enables type derivation for `CAST(ROW() AS ROW())` type sqlCalls.
    return new SqlCastFunction() {
      @Override
      public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
        SqlCallBinding opBinding = new SqlCallBinding(validator, scope, call);
        return inferReturnType(opBinding);
      }
    }.createCall(ZERO, rowCall, new SqlRowTypeSpec(fieldNames, rowTypes, ZERO));
  }
}

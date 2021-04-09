/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.calcite.sql.SqlArrayTypeSpec;
import org.apache.calcite.sql.SqlBasicTypeNameSpec;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlMapTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlRowTypeSpec;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.util.SqlShuttle;


/**
 * Rewrites the SqlNode tree to adapt to Trino specification
 */
public class TrinoSqlRewriter extends SqlShuttle {

  /**
   * Replace Calcite data types present in the SQL query with their respective Trino data types
   */
  private static SqlDataTypeSpec convertTypeSpec(SqlDataTypeSpec type) {
    if (type instanceof SqlArrayTypeSpec) {
      SqlArrayTypeSpec arrayType = (SqlArrayTypeSpec) type;
      return new SqlArrayTypeSpec(convertTypeSpec(arrayType.getElementTypeSpec()), arrayType.getParserPosition());
    } else if (type instanceof SqlMapTypeSpec) {
      SqlMapTypeSpec mapType = (SqlMapTypeSpec) type;
      return new SqlMapTypeSpec(convertTypeSpec(mapType.getKeyTypeSpec()), convertTypeSpec(mapType.getValueTypeSpec()),
          mapType.getParserPosition());
    } else if (type instanceof SqlRowTypeSpec) {
      SqlRowTypeSpec rowType = (SqlRowTypeSpec) type;
      return new SqlRowTypeSpec(rowType.getFieldNames(),
          rowType.getFieldTypeSpecs().stream().map(TrinoSqlRewriter::convertTypeSpec).collect(Collectors.toList()),
          rowType.getParserPosition());
    } else {
      assert type.getTypeNameSpec() instanceof SqlBasicTypeNameSpec;
      final SqlBasicTypeNameSpec typeNameSpec = (SqlBasicTypeNameSpec) type.getTypeNameSpec();

      int precision = typeNameSpec.getPrecision();
      int scale = typeNameSpec.getScale();
      final String charSetName = typeNameSpec.getCharSetName();
      final TimeZone timeZone = type.getTimeZone();
      final SqlParserPos parserPos = type.getParserPosition();

      switch (typeNameSpec.getTypeName().toString()) {
        case "BINARY":
        case "VARBINARY":
          final SqlBasicTypeNameSpec binaryTypeName =
              new SqlBasicTypeNameSpec(SqlTypeName.VARBINARY, -1, -1, charSetName, parserPos);
          return new SqlDataTypeSpec(binaryTypeName, timeZone, parserPos);
        case "FLOAT":
          final SqlBasicTypeNameSpec realTypeName =
              new SqlBasicTypeNameSpec(SqlTypeName.REAL, precision, scale, charSetName, parserPos);
          return new SqlDataTypeSpec(realTypeName, timeZone, parserPos);
        default:
          return type;
      }
    }
  }

  @Override
  public SqlNode visit(SqlDataTypeSpec type) {
    return convertTypeSpec(type);
  }
}

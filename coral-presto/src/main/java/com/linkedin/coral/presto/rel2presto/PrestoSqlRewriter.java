package com.linkedin.coral.presto.rel2presto;

import java.util.TimeZone;
import java.util.stream.Collectors;
import org.apache.calcite.sql.SqlArrayTypeSpec;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlMapTypeSpec;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlRowTypeSpec;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;


/**
 * Rewrites the SqlNode tree to adapt to Presto specification
 */
public class PrestoSqlRewriter extends SqlShuttle {

  /**
   * Replace Calcite data types present in the SQL query with their respective Presto data types
   */
  private static SqlDataTypeSpec convertTypeSpec(SqlDataTypeSpec type) {
    if (type instanceof SqlArrayTypeSpec) {
      SqlArrayTypeSpec arrayType = (SqlArrayTypeSpec) type;
      return new SqlArrayTypeSpec(convertTypeSpec(arrayType.getElementTypeSpec()), arrayType.getNullable(),
          arrayType.getParserPosition());
    } else if (type instanceof SqlMapTypeSpec) {
      SqlMapTypeSpec mapType = (SqlMapTypeSpec) type;
      return new SqlMapTypeSpec(convertTypeSpec(mapType.getKeyTypeSpec()), convertTypeSpec(mapType.getValueTypeSpec()),
          mapType.getNullable(), mapType.getParserPosition());
    } else if (type instanceof SqlRowTypeSpec) {
      SqlRowTypeSpec rowType = (SqlRowTypeSpec) type;
      return new SqlRowTypeSpec(rowType.getFieldNames(),
          rowType.getFieldTypeSpecs().stream().map(PrestoSqlRewriter::convertTypeSpec).collect(Collectors.toList()),
          rowType.getNullable(), rowType.getParserPosition());
    } else {
      int precision = type.getPrecision();
      int scale = type.getScale();
      String charSetName = type.getCharSetName();
      TimeZone timeZone = type.getTimeZone();
      SqlParserPos parserPos = type.getParserPosition();
      switch (type.getTypeName().toString()) {
        case "BINARY":
        case "VARBINARY":
          return new SqlDataTypeSpec(new SqlIdentifier("VARBINARY", parserPos), -1, -1, charSetName, timeZone,
              parserPos);
        case "FLOAT":
          return new SqlDataTypeSpec(new SqlIdentifier("REAL", parserPos), precision, scale, charSetName, timeZone,
              parserPos);
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
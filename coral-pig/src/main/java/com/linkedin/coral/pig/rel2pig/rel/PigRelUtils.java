package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.BasicSqlType;


//TODO(ralam): Add comments and clean up code
public class PigRelUtils {

  private PigRelUtils() {
  }

  public static List<String> convertInputColumnNameReferences(RelDataType relDataType) {
    List<String> columnNameList = new ArrayList<>();
    addInputColumnNamesReferences(relDataType, columnNameList, "");
    return columnNameList;
  }

  public static List<String> convertInputColumnNameReferences(List<RelDataType> relDataTypes) {
    List<String> columnNameList = new ArrayList<>();
    for (RelDataType relDataType : relDataTypes) {
      addInputColumnNamesReferences(relDataType, columnNameList, "");
    }
    return columnNameList;
  }

  private static void addInputColumnNamesReferences(RelDataType relDataType, List<String> columnNameList,
      String key) {

    if (relDataType instanceof RelRecordType) {
      if (!key.isEmpty()) {
        columnNameList.add(key);
      }

      for (RelDataTypeField field : relDataType.getFieldList()) {
        String fieldKey = resolveKey(key, field.getKey());
        addInputColumnNamesReferences(field.getType(), columnNameList, fieldKey);
      }
    } else if (relDataType instanceof BasicSqlType) {
      columnNameList.add(key);
    } else {
      // TODO: Test how Calcite numbers fields in a RelDataType; add nested struct/map/array/etc support and tests
      throw new UnsupportedOperationException("Only struct and primitive data types are supported.");
    }

  }

  private static String resolveKey(String prefix, String key) {
    if (prefix.isEmpty()) {
      return key;
    }

    return String.join(".", prefix, key);
  }
}

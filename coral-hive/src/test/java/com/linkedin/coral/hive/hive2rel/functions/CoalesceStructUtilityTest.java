/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.coalesce;
import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.isTrinoStructPattern;


public class CoalesceStructUtilityTest {
  private RelDataTypeFactory typeFactory;
  private RelDataType trinoStruct;
  private RelDataType extractUnionStruct;
  private RelDataType nonTrinoStruct;

  @BeforeClass
  public void setup() throws Exception {
    typeFactory = new JavaTypeFactoryImpl();

    List<String> names = ImmutableList.of("tag", "field0", "field1");
    List<RelDataType> types = ImmutableList.of(typeFactory.createSqlType(SqlTypeName.INTEGER),
        typeFactory.createSqlType(SqlTypeName.BOOLEAN), typeFactory.createSqlType(SqlTypeName.DOUBLE));
    trinoStruct = typeFactory.createStructType(types, names);

    List<String> names2 = ImmutableList.of("tag_0", "tag_1");
    List<RelDataType> types2 =
        ImmutableList.of(typeFactory.createSqlType(SqlTypeName.BOOLEAN), typeFactory.createSqlType(SqlTypeName.DOUBLE));
    extractUnionStruct = typeFactory.createStructType(types2, names2);

    List<String> names3 = ImmutableList.of("tag", "field1", "field2");
    List<RelDataType> types3 = ImmutableList.of(typeFactory.createSqlType(SqlTypeName.INTEGER),
        typeFactory.createSqlType(SqlTypeName.INTEGER), typeFactory.createSqlType(SqlTypeName.DOUBLE));
    nonTrinoStruct = typeFactory.createStructType(types3, names3);
  }

  @Test
  public void testStruct() {
    // coalesce the trino struct directly
    RelDataType coalescedType = coalesce(trinoStruct, typeFactory);
    Assert.assertEquals(coalescedType, extractUnionStruct);

    // A negative case: a struct that doesn't fulfill trino's pattern should not be changed at all.
    Assert.assertEquals(coalesce(nonTrinoStruct, typeFactory), nonTrinoStruct);
  }

  @Test
  public void testTrinoPatternRecognition() {
    // Multi-digit number case
    ArrayList<String> names = new ArrayList<>();
    Set<Integer> upperBounds = ImmutableSet.of(20, 200, 2000);
    for (int upperBound : upperBounds) {
      names.add("tag");
      for (int i = 0; i < upperBound; i++) {
        names.add("field" + i);
      }
      Assert.assertTrue(isTrinoStructPattern(names));
      names.clear();
    }
  }

  @Test
  public void testMap() {
    RelDataType structInMap = typeFactory.createMapType(typeFactory.createSqlType(SqlTypeName.VARCHAR), trinoStruct);
    RelDataType expectedCoalesced =
        typeFactory.createMapType(typeFactory.createSqlType(SqlTypeName.VARCHAR), extractUnionStruct);
    Assert.assertEquals(coalesce(structInMap, typeFactory), expectedCoalesced);
  }

  @Test
  public void testArray() {
    RelDataType structAsArrayElem = typeFactory.createArrayType(trinoStruct, -1);
    RelDataType expectedCoalesced = typeFactory.createArrayType(extractUnionStruct, -1);
    Assert.assertEquals(coalesce(structAsArrayElem, typeFactory), expectedCoalesced);
  }

  @Test
  public void testNested() {
    // Create a complex nested schema:
    // struct<tag:int, field0: trinoStruct, field1:nonTrinoStruct>
    // expected: struct<tag_0: exStruct, tag_1:nonTrinoStruct>
    List<String> names = ImmutableList.of("tag", "field0", "field1");
    List<RelDataType> types =
        ImmutableList.of(typeFactory.createSqlType(SqlTypeName.INTEGER), trinoStruct, nonTrinoStruct);
    RelDataType nested = typeFactory.createStructType(types, names);

    List<String> names2 = ImmutableList.of("tag_0", "tag_1");
    List<RelDataType> types2 = ImmutableList.of(extractUnionStruct, nonTrinoStruct);
    RelDataType coalescedNestedExpected = typeFactory.createStructType(types2, names2);

    Assert.assertEquals(coalesce(nested, typeFactory), coalescedNestedExpected);
  }
}

/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.ToRelConverterTestUtils;
import com.linkedin.coral.hive.hive2rel.TestUtils;

import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.coalesce;
import static com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility.isTrinoStructPattern;


public class CoalesceStructUtilityTest {
  private RelDataTypeFactory typeFactory;
  private RelDataType trinoStruct;
  private RelDataType extractUnionStruct;
  private RelDataType nonTrinoStruct;
  private static HiveConf conf;

  @BeforeClass
  public void setup() throws Exception {
    this.conf = TestUtils.loadResourceHiveConf();
    ToRelConverterTestUtils.setup(this.conf);
    typeFactory = ToRelConverterTestUtils.createRelBuilder().getTypeFactory();

    List<String> names = ImmutableList.of("tag", "field0", "field1");
    List<RelDataType> types = ImmutableList.of(typeFactory.createSqlType(SqlTypeName.INTEGER),
        typeFactory.createSqlType(SqlTypeName.BOOLEAN), typeFactory.createSqlType(SqlTypeName.DOUBLE));
    trinoStruct = typeFactory.createStructType(types, names);

    List<String> names2 = ImmutableList.of("tag_0", "tag_1");
    List<RelDataType> types2 =
        ImmutableList.of(typeFactory.createSqlType(SqlTypeName.BOOLEAN), typeFactory.createSqlType(SqlTypeName.DOUBLE));
    extractUnionStruct = typeFactory.createTypeWithNullability(typeFactory.createStructType(types2, names2), true);

    List<String> names3 = ImmutableList.of("tag", "field1", "field2");
    List<RelDataType> types3 = ImmutableList.of(typeFactory.createSqlType(SqlTypeName.INTEGER),
        typeFactory.createSqlType(SqlTypeName.INTEGER), typeFactory.createSqlType(SqlTypeName.DOUBLE));
    nonTrinoStruct = typeFactory.createTypeWithNullability(typeFactory.createStructType(types3, names3), true);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  @Test
  public void testStruct() {
    // coalesce the trino struct directly
    RelDataType coalescedType = coalesce(trinoStruct, typeFactory);
    Assert.assertEquals(coalescedType.getFullTypeString(), extractUnionStruct.getFullTypeString());

    // A negative case: a struct that doesn't fulfill trino's pattern should not be changed at all.
    Assert.assertEquals(coalesce(nonTrinoStruct, typeFactory).getFullTypeString(), nonTrinoStruct.getFullTypeString());
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
    RelDataType expectedCoalesced = typeFactory.createTypeWithNullability(
        typeFactory.createMapType(typeFactory.createSqlType(SqlTypeName.VARCHAR), extractUnionStruct), true);

    RelDataType actual = coalesce(structInMap, typeFactory);
    Assert.assertEquals(actual.getFullTypeString(), expectedCoalesced.getFullTypeString());
  }

  @Test
  public void testArray() {
    RelDataType structAsArrayElem = typeFactory.createArrayType(trinoStruct, -1);
    RelDataType expectedCoalesced =
        typeFactory.createTypeWithNullability(typeFactory.createArrayType(extractUnionStruct, -1), true);
    RelDataType actualRelDataType = coalesce(structAsArrayElem, typeFactory);
    Assert.assertEquals(actualRelDataType.getFullTypeString(), expectedCoalesced.getFullTypeString());
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
    RelDataType coalescedNestedExpected =
        typeFactory.createTypeWithNullability(typeFactory.createStructType(types2, names2), true);

    RelDataType actualRelDataType = coalesce(nested, typeFactory);
    Assert.assertEquals(actualRelDataType.getFullTypeString(), coalescedNestedExpected.getFullTypeString());
  }
}

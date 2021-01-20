/**
 * Copyright 2020-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;


public class RelDataTypeToAvroTypeTests {
  private HiveToRelConverter hiveToRelConverter;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException {
    hiveToRelConverter = TestUtils.setupRelDataTypeToAvroTypeTests();
  }

  @Test
  public void testNestedRecord() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);
    RelNode relNode = hiveToRelConverter.convertView("default", "v");
    Schema actualAvroType =
        RelDataTypeToAvroType.relDataTypeToAvroTypeNonNullable(relNode.getRowType(), "nestedRecord");

    Assert.assertEquals(actualAvroType.toString(true), TestUtils.loadSchema("rel2avro-testNestedRecord-expected.avsc"));
  }

}

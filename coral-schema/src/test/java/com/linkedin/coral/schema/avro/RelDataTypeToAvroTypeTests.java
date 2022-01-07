/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.schema.avro.TestUtils.*;


public class RelDataTypeToAvroTypeTests {
  private HiveToRelConverter hiveToRelConverter;
  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.getHiveConf();
    HiveMetastoreClient metastoreClient = setup(conf);
    hiveToRelConverter = new HiveToRelConverter(metastoreClient);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_SCHEMA_TEST_DIR)));
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

  @Test
  public void testTimestampTypeField() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basetimestamptypefield";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);
    RelNode relNode = hiveToRelConverter.convertView("default", "v");
    Schema actualAvroType =
        RelDataTypeToAvroType.relDataTypeToAvroTypeNonNullable(relNode.getRowType(), "timestampTypeField");

    Assert.assertEquals(actualAvroType.toString(true),
        TestUtils.loadSchema("rel2avro-testTimestampTypeField-expected.avsc"));
  }
}

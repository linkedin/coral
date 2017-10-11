package com.linkedin.coral.hive.hive2rel;

import com.linkedin.coral.hive.hive2rel.TestUtils.*;
import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.TestUtils.*;
import static org.testng.Assert.*;


public class HiveRelBuilderTest {

  private static TestHive hive;

  @BeforeClass
  public static void beforeClass() {
    hive = setupDefaultHive();
  }

  @Test
  public static void testScan() throws HiveException, IOException {
    String sql = "SELECT * from foo";
    HiveRelBuilder builder = HiveRelBuilder.create(hive.context.getConf());
    RelNode rel = builder.process(sql);
    assertNotNull(rel);
    String scanStr = "LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(RelOptUtil.toString(rel), scanStr);
  }
}

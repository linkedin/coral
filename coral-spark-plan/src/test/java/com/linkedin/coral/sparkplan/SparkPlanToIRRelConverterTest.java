/**
 * Copyright 2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.sparkplan;

import com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveSchema;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import java.io.IOException;
import java.util.Map;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class SparkPlanToIRRelConverterTest {

  private static SparkPlanToIRRelConverter converter;
  private static RelContextProvider relContextProvider;
  private static HiveToRelConverter hiveToRelConverter;

  @BeforeClass
  public static void beforeClass() throws IOException, HiveException, MetaException {
    TestUtils.TestHive testHive = TestUtils.setupDefaultHive();
    final IMetaStoreClient msc = testHive.getMetastoreClient();
    HiveMscAdapter hiveMscAdapter = new HiveMscAdapter(msc);
    converter = SparkPlanToIRRelConverter.create(hiveMscAdapter);
    HiveSchema schema = new HiveSchema(hiveMscAdapter);
    relContextProvider = new RelContextProvider(schema);
    hiveToRelConverter = HiveToRelConverter.create(hiveMscAdapter);
  }

  @Test
  public void testConvertLogicalPlanScanNodes() {
    String plan = "+- Project [area_code#64, code#65]\n"
        + "   +- Filter ((isnotnull(country#63)) && (datediff(18429, cast(substring(datepartition#66, 0, 10) as date)) <= 365) && (country#63 = US))\n"
        + "      +- HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#73]";
    Map<RelNode, RelNode> relNodeMap = converter.convertScanNodes(plan);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expectedRelNode = relBuilder.scan(ImmutableList.of("hive", "test", "airport")).build();
    assertEquals(relNodeMap.size(), 1);
    Map.Entry<RelNode, RelNode> entry = relNodeMap.entrySet().iterator().next();
    verifyRel(entry.getKey(), expectedRelNode);
    assert entry.getValue() == null;
  }

  @Test
  public void testConvertPhysicalPlanScanNodesWithPredicate() {
    String plan =
        "+- *(1) Project [area_code#64, code#65]\n" + "   +- *(1) Filter (isnotnull(country#63) && (country#63 = US))\n"
            + "      +-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66], [(datediff(18429, cast(substring(datepartition#66, 0, 10) as date)) <= 365)]";
    Map<RelNode, RelNode> relNodeMap = converter.convertScanNodes(plan);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expectedKeyNode = relBuilder.scan(ImmutableList.of("hive", "test", "airport")).build();
    assertEquals(relNodeMap.size(), 1);
    Map.Entry<RelNode, RelNode> entry = relNodeMap.entrySet().iterator().next();
    verifyRel(entry.getKey(), expectedKeyNode);
    String sql =
        "SELECT * FROM test.airport WHERE (datediff(18429, cast(substring(datepartition, 0, 10) as date)) <= 365)";
    RelNode convertedNode = hiveToRelConverter.convertSql(sql);
    RexNode rexNode = convertedNode.getInput(0).getChildExps().get(0);
    RelNode expectedValueNode = relBuilder.push(expectedKeyNode).filter(rexNode).build();
    verifyRel(entry.getValue(), expectedValueNode);
  }

  @Test
  public void testHasComplicatedPredicatePushedDown() {
    String plan =
        "+- *(1) Project [area_code#64, code#65]\n" + "   +- *(1) Filter (isnotnull(country#63) && (country#63 = US))\n"
            + "      +-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66], [(datediff(18429, cast(substring(datepartition#66, 0, 10) as date)) <= 365)]";
    assertTrue(converter.hasComplicatedPredicatePushedDown(plan));
  }

  @Test
  public void testHasNoComplicatedPredicatePushedDown() {
    String plan =
        "+- *(1) Project [area_code#64, code#65]\n" + "   +- *(1) Filter (isnotnull(country#63) && (country#63 = US))\n"
            + "      +-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66]";
    assertFalse(converter.hasComplicatedPredicatePushedDown(plan));
  }

  private RelBuilder createRelBuilder() {
    return RelBuilder.create(relContextProvider.getConfig());
  }

  private void verifyRel(RelNode input, RelNode expected) {
    assertEquals(input.getInputs().size(), expected.getInputs().size());
    for (int i = 0; i < input.getInputs().size(); i++) {
      verifyRel(input.getInput(i), expected.getInput(i));
    }
    RelOptUtil.areRowTypesEqual(input.getRowType(), expected.getRowType(), true);
  }
}

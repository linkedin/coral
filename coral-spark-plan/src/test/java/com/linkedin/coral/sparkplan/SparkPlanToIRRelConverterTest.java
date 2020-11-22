/**
 * Copyright 2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.sparkplan;

import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.RelContextProvider;

import java.io.IOException;

import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class SparkPlanToIRRelConverterTest {

  private static SparkPlanToIRRelConverter converter;
  private static RelContextProvider relContextProvider;

  @BeforeClass
  public static void beforeClass() throws IOException, HiveException, MetaException {
    TestUtils.TestHive testHive = TestUtils.setupDefaultHive();
    final IMetaStoreClient msc = testHive.getMetastoreClient();
    HiveMscAdapter hiveMscAdapter = new HiveMscAdapter(msc);
    converter = SparkPlanToIRRelConverter.create(hiveMscAdapter);
    relContextProvider = new RelContextProvider(hiveMscAdapter);
  }


  @Test
  public void testContainsComplicatedPredicatePushedDown() {
    String plan =
        "+- *(1) Project [area_code#64, code#65]\n" + "   +- *(1) Filter (isnotnull(country#63) && (country#63 = US))\n"
            +
            "      +-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy"
            + ".LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66], [(datediff(18429, cast(substring"
            + "(datepartition#66, 0, 10) as date)) <= 365)]";
    assertEquals(converter.containsComplicatedPredicatePushedDown(plan), "Yes");
  }

  @Test
  public void testContainsNoPredicatePushedDown() {
    String plan =
        "+- *(1) Project [area_code#64, code#65]\n" + "   +- *(1) Filter (isnotnull(country#63) && (country#63 = US))\n"
            +
            "      +-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy"
            + ".LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66]";
    assertEquals(converter.containsComplicatedPredicatePushedDown(plan), "No");
  }

  @Test
  public void testContainsNoComplicatedPredicatePushedDown() {
    String plan =
        "+- *(1) Project [area_code#64, code#65]\n" + "   +- *(1) Filter (isnotnull(country#63) && (country#63 = US))\n"
            +
            "      +-  HiveTableScan [area_code#64, code#65, country#63], HiveTableRelation `test`.`airport`, org.apache.hadoop.hive.serde2.lazy"
            + ".LazySimpleSerDe, [name#62, country#63, area_code#64, code#65, datepartition#66], [datepartition#66], [code#65 NOT IN (12, 13), isnotnull"
            + "(code#65), StartsWith(name#62, LA), EndsWith(name#62, X), Contains(name#62, Y), isnull(area_code#64)]";
    assertEquals(converter.containsComplicatedPredicatePushedDown(plan), "No");
  }
}

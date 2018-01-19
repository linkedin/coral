package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;

import static org.testng.Assert.*;


class ToRelConverter {

  private static TestUtils.TestHive hive;
  private static IMetaStoreClient msc;
  static HiveToRelConverter converter;
  static RelContextProvider relContextProvider;

  static void setup() throws IOException, HiveException, MetaException {
    hive = TestUtils.setupDefaultHive();
    msc = hive.getMetastoreClient();
    HiveMscAdapter mscAdapter = new HiveMscAdapter(msc);
    converter = HiveToRelConverter.create(mscAdapter);
    // for validation
    HiveSchema schema = new HiveSchema(mscAdapter);
    relContextProvider = new RelContextProvider(schema);
  }

  static String relToString(String sql) {
    return RelOptUtil.toString(converter.convertSql(sql));
  }

  static RelBuilder createRelBuilder() {
    return RelBuilder.create(relContextProvider.getConfig());
  }

  static void verifyRel(RelNode input, RelNode expected) {
    assertEquals(input.getInputs().size(), expected.getInputs().size());
    for (int i = 0; i < input.getInputs().size(); i++) {
      verifyRel(input.getInput(i), expected.getInput(i));
    }
    RelOptUtil.areRowTypesEqual(input.getRowType(), expected.getRowType(), true);
  }
}

package com.linkedin.coral.vis;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import org.apache.calcite.sql.util.SqlVisitor;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;


public class CoraIlRVisualizationUtil {
  private static final String CORAL_HIVE_TEST_DIR = "coral.hive.test.dir";
  private HiveToRelConverter converter;
  private File outputDirectory;

  private CoraIlRVisualizationUtil(HiveToRelConverter converter, File outputDirectory) {
    this.converter = converter;
    this.outputDirectory = outputDirectory;
  }

  public static CoraIlRVisualizationUtil create(File outputDirectory)  {
    HiveMscAdapter mscAdapter = createMscAdapter();
    HiveToRelConverter converter = new HiveToRelConverter(mscAdapter);
    return new CoraIlRVisualizationUtil(converter, outputDirectory);
  }

  public void visualizeCoralSqlNodeToFile(String sql, String fileName) {
    SqlVisitor<Node> sqlVisitor = new CoralSqlNodeVisualisationVisitor();
    Node node = converter.toSqlNode(sql).accept(sqlVisitor);
    Graph graph = Factory.graph("test").directed().nodeAttr().with(Font.size(10)).linkAttr().with(Font.size(10)).with(node);
    File outputFile = new File(outputDirectory, fileName);
    try {
      Graphviz.fromGraph(graph).width(1000).render(Format.PNG).toFile(outputFile);
    } catch (Exception e) {
      throw new RuntimeException("Could not render graphviz file:" + e);
    }
  }

  private static HiveMscAdapter createMscAdapter() {
    try {
      InputStream hiveConfStream =
          CoralSqlNodeVisualisationVisitor.class.getClassLoader().getResourceAsStream("hive.xml");
      HiveConf hiveConf = new HiveConf();
      hiveConf.set(CORAL_HIVE_TEST_DIR, System.getProperty("java.io.tmpdir") + "/coral/hive/" + UUID.randomUUID());
      hiveConf.addResource(hiveConfStream);
      hiveConf.set("mapreduce.framework.name", "local");
      hiveConf.set("_hive.hdfs.session.path", "/tmp/coral");
      hiveConf.set("_hive.local.session.path", "/tmp/coral");
      return new HiveMscAdapter(Hive.get(hiveConf).getMSC());
    } catch (MetaException | HiveException e) {
      throw new RuntimeException("Could not initialize Hive Metastore Client Adapter:" + e);
    }
  }
}

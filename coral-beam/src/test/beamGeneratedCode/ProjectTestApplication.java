import com.google.common.collect.ImmutableList;
import com.linkedin.beam.excution.BeamAPIUtil;
import com.linkedin.beam.excution.BeamExecUtil;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import java.util.Objects;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.calcite.piglet.PigRelSqlUdfs;
import org.apache.pig.builtin.ENDSWITH;
import org.apache.pig.data.Tuple;

public class ProjectTestApplication {
  private static final Schema PROJECT2_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intStringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"longStringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"doubleStringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"stringStringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"constStringString\",\"type\":\"string\"},{\"name\":\"constLongString\",\"type\":[\"string\",\"null\"]},{\"name\":\"intLongCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"longLongCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"doubleLongCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"stringLongCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"constLongLong\",\"type\":\"long\"},{\"name\":\"constStringLong\",\"type\":[\"long\",\"null\"]},{\"name\":\"intIntCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"longIntCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"doubleIntCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringIntCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"constIntInt\",\"type\":\"int\"},{\"name\":\"constStringInt\",\"type\":[\"int\",\"null\"]},{\"name\":\"intDoubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"longDoubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"doubleDoubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"stringDoubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"intConstDoubleCol\",\"type\":\"double\"},{\"name\":\"stringContsDoubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"recordCol\",\"type\":{\"type\":\"record\",\"name\":\"record0_recordCol\",\"fields\":[{\"name\":\"_0\",\"type\":\"int\"},{\"name\":\"_1\",\"type\":\"string\"}]}},{\"name\":\"bagCol\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"record0_bagCol\",\"fields\":[{\"name\":\"_0\",\"type\":\"int\"},{\"name\":\"_1\",\"type\":\"string\"}]}}},{\"name\":\"udfCol\",\"type\":[\"boolean\",\"null\"]}]}");
  private static final Schema PROJECT2_COLUMN25_SCHEMA_CONST_11_COMP =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"_0\",\"type\":\"int\"},{\"name\":\"_1\",\"type\":\"string\"}]}");
  private static final Schema PROJECT2_COLUMN24_SCHEMA_CONST_10 =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"_0\",\"type\":\"int\"},{\"name\":\"_1\",\"type\":\"string\"}]}");
  private static final Schema PROJECT2_COLUMN25_SCHEMA_CONST_11 =
      new Schema.Parser()
          .parse(
              "{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"_0\",\"type\":\"int\"},{\"name\":\"_1\",\"type\":\"string\"}]}}");
  private static boolean staticVarInit = false;
  private static ENDSWITH org_apache_pig_builtin_ENDSWITH;

  public static void main(String[] args) {
    if (!staticVarInit) {
      org_apache_pig_builtin_ENDSWITH = new ENDSWITH();
      staticVarInit = true;
    }
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read projectStream",
            KafkaIOGenericRecord.read()
                .withTopic("projectStream")
                .withTimestampFn(BeamAPIUtil.withTimeFunction(ImmutableList.of("longCol")))
                .withoutMetadata());
    PCollection<KV<String, GenericRecord>> project2 =
        stream1
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream1KV) {
                        final GenericRecord stream1Record = stream1KV.getValue();
                        GenericRecord project2Record = new GenericData.Record(PROJECT2_SCHEMA);
                        final Integer var0 = (Integer) stream1Record.get("intCol");
                        final String var1 = Objects.toString(var0, null);
                        project2Record.put("intStringCol", var1);
                        final Long var2 = (Long) stream1Record.get("longCol");
                        final String var3 = Objects.toString(var2, null);
                        project2Record.put("longStringCol", var3);
                        final Double var4 = (Double) stream1Record.get("doubleCol");
                        final String var5 = Objects.toString(var4, null);
                        project2Record.put("doubleStringCol", var5);
                        final String var6 = Objects.toString(stream1Record.get("stringCol"), null);
                        project2Record.put("stringStringCol", var6);
                        project2Record.put("constStringString", "abc");
                        final String var7 = String.valueOf(1L);
                        project2Record.put("constLongString", var7);
                        final Integer var8 = (Integer) stream1Record.get("intCol");
                        final Long var9 = var8 != null ? var8.longValue() : (Long) null;
                        project2Record.put("intLongCol", var9);
                        final Long var10 = (Long) stream1Record.get("longCol");
                        final Long var11 = var10 != null ? var10 + 2L : (Long) null;
                        project2Record.put("longLongCol", var11);
                        final Double var12 = (Double) stream1Record.get("doubleCol");
                        final Long var13 = var12 != null ? var12.longValue() : (Long) null;
                        project2Record.put("doubleLongCol", var13);
                        final String var14 = Objects.toString(stream1Record.get("stringCol"), null);
                        final Long var15 = Long.valueOf(var14);
                        project2Record.put("stringLongCol", var15);
                        project2Record.put("constLongLong", 3L);
                        final Long var16 = Long.valueOf("4");
                        project2Record.put("constStringLong", var16);
                        final Integer var17 = (Integer) stream1Record.get("intCol");
                        final Integer var18 = var17 != null ? var17 + 5 : (Integer) null;
                        project2Record.put("intIntCol", var18);
                        final Long var19 = (Long) stream1Record.get("longCol");
                        final Integer var20 = var19 != null ? var19.intValue() : (Integer) null;
                        project2Record.put("longIntCol", var20);
                        final Double var21 = (Double) stream1Record.get("doubleCol");
                        final Integer var22 = var21 != null ? var21.intValue() : (Integer) null;
                        project2Record.put("doubleIntCol", var22);
                        final String var23 = Objects.toString(stream1Record.get("stringCol"), null);
                        final Integer var24 = Integer.valueOf(var23);
                        project2Record.put("stringIntCol", var24);
                        project2Record.put("constIntInt", 6);
                        final Integer var25 = Integer.valueOf("7");
                        project2Record.put("constStringInt", var25);
                        final Integer var26 = (Integer) stream1Record.get("intCol");
                        final Double var27 = var26 != null ? var26.doubleValue() : (Double) null;
                        project2Record.put("intDoubleCol", var27);
                        final Long var28 = (Long) stream1Record.get("longCol");
                        final Double var29 = var28 != null ? var28.doubleValue() : (Double) null;
                        project2Record.put("longDoubleCol", var29);
                        final Double var30 = (Double) stream1Record.get("doubleCol");
                        project2Record.put("doubleDoubleCol", var30);
                        final String var31 = Objects.toString(stream1Record.get("stringCol"), null);
                        final Double var32 = Double.valueOf(var31);
                        project2Record.put("stringDoubleCol", var32);
                        project2Record.put("intConstDoubleCol", 9.0D);
                        final Double var33 = Double.valueOf("10.5");
                        project2Record.put("stringContsDoubleCol", var33);
                        project2Record.put(
                            "recordCol",
                            BeamExecUtil.toAvroRecord(
                                PigRelSqlUdfs.buildTuple(1, "a"),
                                PROJECT2_COLUMN24_SCHEMA_CONST_10));
                        project2Record.put(
                            "bagCol",
                            BeamExecUtil.toAvroArray(
                                PigRelSqlUdfs.buildBag(
                                    BeamExecUtil.toAvroRecord(
                                        PigRelSqlUdfs.buildTuple(1, "a"),
                                        PROJECT2_COLUMN25_SCHEMA_CONST_11_COMP),
                                    BeamExecUtil.toAvroRecord(
                                        PigRelSqlUdfs.buildTuple(2, "b"),
                                        PROJECT2_COLUMN25_SCHEMA_CONST_11_COMP)),
                                PROJECT2_COLUMN25_SCHEMA_CONST_11));
                        final String var34 = Objects.toString(stream1Record.get("stringCol"), null);
                        final Tuple var35 = PigRelSqlUdfs.buildTuple(var34, "foo");
                        final Boolean var36 = org_apache_pig_builtin_ENDSWITH.exec(var35);
                        project2Record.put("udfCol", var36);
                        return KV.of(stream1KV.getKey(), project2Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT2_SCHEMA)));
    project2.apply(KafkaIOGenericRecord.write().withTopic("project_output"));
    pipeline.run();
  }
}

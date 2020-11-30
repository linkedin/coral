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
import org.apache.beam.sdk.schemas.transforms.Join;
import org.apache.beam.sdk.schemas.utils.AvroUtils;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Duration;

public class JoinTestApplication {
  private static final Schema PROJECT2_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"doubleCol\",\"type\":[\"double\",\"null\"]}]}");
  private static final Schema PROJECT4_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"longCol\",\"type\":[\"long\",\"null\"]}]}");
  private static final Schema JOIN5_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"doubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"intCol0\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol0\",\"type\":[\"string\",\"null\"]},{\"name\":\"longCol\",\"type\":[\"long\",\"null\"]}]}");

  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read joinStream",
            KafkaIOGenericRecord.read().withTopic("joinStream").withoutMetadata());
    PCollection<KV<String, GenericRecord>> project2 =
        stream1
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream1KV) {
                        final GenericRecord stream1Record = stream1KV.getValue();
                        GenericRecord project2Record = new GenericData.Record(PROJECT2_SCHEMA);
                        final Integer var0 = (Integer) stream1Record.get("intCol");
                        project2Record.put("intCol", var0);
                        final String var1 = Objects.toString(stream1Record.get("stringCol"), null);
                        project2Record.put("stringCol", var1);
                        final Double var2 = (Double) stream1Record.get("doubleCol");
                        project2Record.put("doubleCol", var2);
                        return KV.of(stream1KV.getKey(), project2Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT2_SCHEMA)));
    PCollection<KV<String, GenericRecord>> stream3 =
        pipeline.apply(
            "Read joinStream2",
            KafkaIOGenericRecord.read().withTopic("joinStream2").withoutMetadata());
    PCollection<KV<String, GenericRecord>> project4 =
        stream3
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream3KV) {
                        final GenericRecord stream3Record = stream3KV.getValue();
                        GenericRecord project4Record = new GenericData.Record(PROJECT4_SCHEMA);
                        final Integer var0 = (Integer) stream3Record.get("intCol");
                        project4Record.put("intCol", var0);
                        final String var1 = Objects.toString(stream3Record.get("stringCol"), null);
                        project4Record.put("stringCol", var1);
                        final Long var2 = (Long) stream3Record.get("longCol");
                        project4Record.put("longCol", var2);
                        return KV.of(stream3KV.getKey(), project4Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT4_SCHEMA)));
    PCollection<KV<String, GenericRecord>> join5Left =
        project2
            .apply(Values.create())
            .apply(
                WithKeys.of(
                        new SerializableFunction<GenericRecord, String>() {
                          public String apply(GenericRecord project2Record) {
                            {
                              return BeamExecUtil.buildStringKey(
                                  project2Record, "intCol", "stringCol");
                            }
                          }
                        })
                    .withKeyType(TypeDescriptors.strings()))
            .apply(Window.into(FixedWindows.of(Duration.standardMinutes(5L))));
    PCollection<KV<String, GenericRecord>> join5Right =
        project4
            .apply(Values.create())
            .apply(
                WithKeys.of(
                        new SerializableFunction<GenericRecord, String>() {
                          public String apply(GenericRecord project4Record) {
                            {
                              return BeamExecUtil.buildStringKey(
                                  project4Record, "intCol", "stringCol");
                            }
                          }
                        })
                    .withKeyType(TypeDescriptors.strings()))
            .apply(Window.into(FixedWindows.of(Duration.standardMinutes(5L))));
    PCollection<KV<String, GenericRecord>> join5 =
        join5Left
            .apply(Join.leftOuterJoin(join5Right))
            .apply(
                MapElements.via(
                    new SimpleFunction<Row, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(Row join5Row) {
                        GenericRecord join5Record =
                            AvroUtils.toGenericRecord(join5Row, JOIN5_SCHEMA);
                        String key =
                            BeamExecUtil.buildStringKey(join5Record, "intCol", "stringCol");
                        return KV.of(key, join5Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(JOIN5_SCHEMA)))
            .apply(Window.into(new GlobalWindows()));
    join5.apply(KafkaIOGenericRecord.write().withTopic("ss_join_output"));
    pipeline.run();
  }
}

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
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;

public class UnionTestApplication {
  private static final Schema PROJECT2_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]}]}");
  private static final Schema PROJECT4_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]}]}");
  private static final Schema PROJECT6_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]}]}");

  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read unionStream",
            KafkaIOGenericRecord.read().withTopic("unionStream").withoutMetadata());
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
                        return KV.of(stream1KV.getKey(), project2Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT2_SCHEMA)));
    PCollection<KV<String, GenericRecord>> stream3 =
        pipeline.apply(
            "Read unionStream2",
            KafkaIOGenericRecord.read().withTopic("unionStream2").withoutMetadata());
    PCollection<KV<String, GenericRecord>> project4 =
        stream3
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream3KV) {
                        final GenericRecord stream3Record = stream3KV.getValue();
                        GenericRecord project4Record = new GenericData.Record(PROJECT4_SCHEMA);
                        final Integer var0 = (Integer) stream3Record.get("intCol");
                        final Integer var1 = var0 != null ? var0 + 1 : (Integer) null;
                        project4Record.put("intCol", var1);
                        final String var2 = Objects.toString(stream3Record.get("stringCol"), null);
                        project4Record.put("stringCol", var2);
                        return KV.of(stream3KV.getKey(), project4Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT4_SCHEMA)));
    PCollection<KV<String, GenericRecord>> stream5 =
        pipeline.apply(
            "Read unionStream3",
            KafkaIOGenericRecord.read().withTopic("unionStream3").withoutMetadata());
    PCollection<KV<String, GenericRecord>> project6 =
        stream5
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream5KV) {
                        final GenericRecord stream5Record = stream5KV.getValue();
                        GenericRecord project6Record = new GenericData.Record(PROJECT6_SCHEMA);
                        final Integer var0 = (Integer) stream5Record.get("intCol");
                        final Integer var1 = var0 != null ? var0 + 2 : (Integer) null;
                        project6Record.put("intCol", var1);
                        final String var2 = Objects.toString(stream5Record.get("stringCol"), null);
                        project6Record.put("stringCol", var2);
                        return KV.of(stream5KV.getKey(), project6Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT6_SCHEMA)));
    PCollection<KV<String, GenericRecord>> union7 =
        PCollectionList.of(project2).and(project4).and(project6).apply(Flatten.pCollections());
    union7.apply(KafkaIOGenericRecord.write().withTopic("union_output"));
    pipeline.run();
  }
}

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.excution.BeamArrayFlatten;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import java.util.Collection;
import java.util.Objects;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

public class ArrayFlatten1CTestApplication {
  private static final Schema PROJECT2_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"key\",\"type\":[\"string\",\"null\"]},{\"name\":\"longCol\",\"type\":[{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"record0_longCol\",\"fields\":[{\"name\":\"elem\",\"type\":[\"long\",\"null\"]}]},\"null\"]},\"null\"]}]}");
  private static final Schema ARRAYFLATTEN3_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"key\",\"type\":[\"string\",\"null\"]},{\"name\":\"longCol\",\"type\":[{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"record0_longCol\",\"fields\":[{\"name\":\"elem\",\"type\":[\"long\",\"null\"]}]},\"null\"]},\"null\"]},{\"name\":\"elem\",\"type\":[\"long\",\"null\"]}]}");
  private static final Schema PROJECT4_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"key\",\"type\":[\"string\",\"null\"]},{\"name\":\"longCol\",\"type\":[\"long\",\"null\"]}]}");

  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read arrayFlattenStream",
            KafkaIOGenericRecord.read().withTopic("arrayFlattenStream").withoutMetadata());
    PCollection<KV<String, GenericRecord>> project2 =
        stream1
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream1KV) {
                        final GenericRecord stream1Record = stream1KV.getValue();
                        GenericRecord project2Record = new GenericData.Record(PROJECT2_SCHEMA);
                        final String var0 = Objects.toString(stream1Record.get("key"), null);
                        project2Record.put("key", var0);
                        final GenericArray var1 = (GenericArray) stream1Record.get("longArray");
                        project2Record.put("longCol", var1);
                        return KV.of(stream1KV.getKey(), project2Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT2_SCHEMA)));
    PCollection<KV<String, GenericRecord>> arrayflatten3 =
        project2
            .apply(
                FlatMapElements.via(
                    new SimpleFunction<
                        KV<String, GenericRecord>, Collection<KV<String, GenericRecord>>>() {
                      public Collection<KV<String, GenericRecord>> apply(
                          KV<String, GenericRecord> project2KV) {
                        return BeamArrayFlatten.flatten(
                            project2KV, ImmutableList.of("longCol"), ARRAYFLATTEN3_SCHEMA);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(ARRAYFLATTEN3_SCHEMA)));
    PCollection<KV<String, GenericRecord>> project4 =
        arrayflatten3
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(
                          KV<String, GenericRecord> arrayflatten3KV) {
                        final GenericRecord arrayflatten3Record = arrayflatten3KV.getValue();
                        GenericRecord project4Record = new GenericData.Record(PROJECT4_SCHEMA);
                        final String var0 = Objects.toString(arrayflatten3Record.get("key"), null);
                        project4Record.put("key", var0);
                        final Long var1 = (Long) arrayflatten3Record.get("elem");
                        project4Record.put("longCol", var1);
                        return KV.of(arrayflatten3KV.getKey(), project4Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT4_SCHEMA)));
    project4.apply(KafkaIOGenericRecord.write().withTopic("array_flatten1_output"));
    pipeline.run();
  }
}

import com.linkedin.beam.excution.BeamExecUtil;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import com.linkedin.beam.excution.MessageDedup;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Duration;

public class DistinctTestApplication {
  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read testStream",
            KafkaIOGenericRecord.read().withTopic("testStream").withoutMetadata());
    PCollection<KV<String, GenericRecord>> aggregate2 =
        stream1
            .apply(Values.create())
            .apply(
                WithKeys.of(
                        new SerializableFunction<GenericRecord, String>() {
                          public String apply(GenericRecord stream1Record) {
                            {
                              return BeamExecUtil.buildDistinctStringKeyFromRecord(
                                  "aggregate2_", stream1Record, "timestamp");
                            }
                          }
                        })
                    .withKeyType(TypeDescriptors.strings()))
            .apply(MessageDedup.within(Duration.standardDays(1)));
    aggregate2.apply(KafkaIOGenericRecord.write().withTopic("distinct_output"));
    pipeline.run();
  }
}

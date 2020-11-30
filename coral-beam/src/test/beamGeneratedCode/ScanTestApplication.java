import com.google.common.collect.ImmutableList;
import com.linkedin.beam.excution.BeamAPIUtil;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

public class ScanTestApplication {
  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read testStream",
            KafkaIOGenericRecord.read()
                .withTopic("testStream")
                .withTimestampFn(BeamAPIUtil.withTimeFunction(ImmutableList.of("longCol")))
                .withoutMetadata());
    stream1.apply(KafkaIOGenericRecord.write().withTopic("scan_output"));
    pipeline.run();
  }
}

import com.linkedin.beam.excution.KafkaIOGenericRecord;
import java.util.Objects;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

public class FilterTestApplication {
  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read testStream",
            KafkaIOGenericRecord.read().withTopic("testStream").withoutMetadata());
    PCollection<KV<String, GenericRecord>> filter2 =
        stream1.apply(
            Filter.by(
                new SerializableFunction<KV<String, GenericRecord>, Boolean>() {
                  public Boolean apply(KV<String, GenericRecord> stream1KV) {
                    final GenericRecord stream1Record = stream1KV.getValue();
                    final Integer var0 = (Integer) stream1Record.get("intCol");
                    final Double var1 = (Double) stream1Record.get("doubleCol");
                    final Double var2 = var1 != null ? var1 * var1 : (Double) null;
                    final String var3 = Objects.toString(stream1Record.get("stringCol"), null);
                    return var0 != null
                        && var0 > 2
                        && (var2 != null && var2 < 1.8D || var3 != null && var3.equals("test"));
                  }
                }));
    filter2.apply(KafkaIOGenericRecord.write().withTopic("filter_output"));
    pipeline.run();
  }
}

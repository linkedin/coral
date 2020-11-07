import com.linkedin.beam.excution.BeamExecUtil;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.beam.sdk.transforms.Combine;
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
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.Duration;

public class AggregateTestApplication {
  private static final Schema PROJECT2_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"strGroupCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"longGroupCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"longCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"intCol\",\"type\":[\"int\",\"null\"]},{\"name\":\"doubleCol\",\"type\":[\"double\",\"null\"]},{\"name\":\"stringCol\",\"type\":[\"string\",\"null\"]}]}");
  private static final Schema AGGREGATE3_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"strGroupCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"longGroupCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"_f2\",\"type\":[\"long\",\"null\"]},{\"name\":\"_f3\",\"type\":[\"int\",\"null\"]},{\"name\":\"_f4\",\"type\":[\"double\",\"null\"]},{\"name\":\"_f5\",\"type\":[\"string\",\"null\"]},{\"name\":\"_f6\",\"type\":[\"long\",\"null\"]},{\"name\":\"_f7\",\"type\":[\"int\",\"null\"]},{\"name\":\"_f8\",\"type\":[\"double\",\"null\"]},{\"name\":\"_f9\",\"type\":[\"string\",\"null\"]},{\"name\":\"_f10\",\"type\":[\"long\",\"null\"]},{\"name\":\"_f11\",\"type\":[\"int\",\"null\"]},{\"name\":\"_f12\",\"type\":[\"double\",\"null\"]},{\"name\":\"_f13\",\"type\":\"long\"},{\"name\":\"_f14\",\"type\":\"long\"},{\"name\":\"_f15\",\"type\":{\"type\":\"array\",\"items\":[\"long\",\"null\"]}}]}");
  private static final Schema PROJECT4_SCHEMA =
      new Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"record0\",\"namespace\":\"rel_avro\",\"fields\":[{\"name\":\"strGroupCol\",\"type\":[\"string\",\"null\"]},{\"name\":\"longGroupCol\",\"type\":[\"long\",\"null\"]},{\"name\":\"maxLong\",\"type\":[\"long\",\"null\"]},{\"name\":\"maxInt\",\"type\":[\"int\",\"null\"]},{\"name\":\"maxDouble\",\"type\":[\"double\",\"null\"]},{\"name\":\"maxString\",\"type\":[\"string\",\"null\"]},{\"name\":\"minLong\",\"type\":[\"long\",\"null\"]},{\"name\":\"minInt\",\"type\":[\"int\",\"null\"]},{\"name\":\"minDouble\",\"type\":[\"double\",\"null\"]},{\"name\":\"minString\",\"type\":[\"string\",\"null\"]},{\"name\":\"sumLong\",\"type\":[\"long\",\"null\"]},{\"name\":\"sumInt\",\"type\":[\"long\",\"null\"]},{\"name\":\"sumDouble\",\"type\":[\"double\",\"null\"]},{\"name\":\"countLong\",\"type\":[\"long\",\"null\"]},{\"name\":\"countStar\",\"type\":[\"long\",\"null\"]},{\"name\":\"longMultiset\",\"type\":{\"type\":\"array\",\"items\":[\"long\",\"null\"]}}]}");
  private static final Schema AGGREGATE3_F15_SCHEMA =
      new Schema.Parser().parse("{\"type\":\"array\",\"items\":[\"long\",\"null\"]}");

  public static void main(String[] args) {
    final PipelineOptions pipelineOpts = PipelineOptionsFactory.create();
    final Pipeline pipeline = Pipeline.create(pipelineOpts);
    PCollection<KV<String, GenericRecord>> stream1 =
        pipeline.apply(
            "Read testStream",
            KafkaIOGenericRecord.read().withTopic("testStream").withoutMetadata());
    PCollection<KV<String, GenericRecord>> project2 =
        stream1
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(KV<String, GenericRecord> stream1KV) {
                        final GenericRecord stream1Record = stream1KV.getValue();
                        GenericRecord project2Record = new GenericData.Record(PROJECT2_SCHEMA);
                        final String var0 =
                            Objects.toString(stream1Record.get("strGroupCol"), null);
                        project2Record.put("strGroupCol", var0);
                        final Long var1 = (Long) stream1Record.get("longGroupCol");
                        project2Record.put("longGroupCol", var1);
                        final Long var2 = (Long) stream1Record.get("longCol");
                        project2Record.put("longCol", var2);
                        final Integer var3 = (Integer) stream1Record.get("intCol");
                        project2Record.put("intCol", var3);
                        final Double var4 = (Double) stream1Record.get("doubleCol");
                        project2Record.put("doubleCol", var4);
                        final String var5 = Objects.toString(stream1Record.get("stringCol"), null);
                        project2Record.put("stringCol", var5);
                        return KV.of(stream1KV.getKey(), project2Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT2_SCHEMA)));
    PCollection<KV<String, GenericRecord>> aggregate3KV =
        project2
            .apply(Values.create())
            .apply(
                WithKeys.of(
                        new SerializableFunction<GenericRecord, String>() {
                          public String apply(GenericRecord project2Record) {
                            {
                              return BeamExecUtil.buildStringKey(
                                  project2Record, "strGroupCol", "longGroupCol");
                            }
                          }
                        })
                    .withKeyType(TypeDescriptors.strings()))
            .apply(Window.into(FixedWindows.of(Duration.standardMinutes(5L))));
    PCollection<KV<String, GenericRecord>> aggregate3 =
        aggregate3KV
            .apply(
                Combine.perKey(
                    new SerializableFunction<Iterable<GenericRecord>, GenericRecord>() {
                      public GenericRecord apply(Iterable<GenericRecord> project2GroupedRecords) {
                        GenericRecord aggregate3Record = new GenericData.Record(AGGREGATE3_SCHEMA);
                        Iterator<GenericRecord> iterator = project2GroupedRecords.iterator();
                        GenericRecord inputRecord = iterator.next();
                        aggregate3Record.put(
                            "strGroupCol",
                            (Object) Objects.toString(inputRecord.get("strGroupCol"), null));
                        aggregate3Record.put("longGroupCol", inputRecord.get("longGroupCol"));
                        long aggregate3_f2 = (Long) inputRecord.get("longCol");
                        int aggregate3_f3 = (Integer) inputRecord.get("intCol");
                        double aggregate3_f4 = (Double) inputRecord.get("doubleCol");
                        String aggregate3_f5 = Objects.toString(inputRecord.get("stringCol"), null);
                        long aggregate3_f6 = (Long) inputRecord.get("longCol");
                        int aggregate3_f7 = (Integer) inputRecord.get("intCol");
                        double aggregate3_f8 = (Double) inputRecord.get("doubleCol");
                        String aggregate3_f9 = Objects.toString(inputRecord.get("stringCol"), null);
                        long aggregate3_f10 = 0;
                        int aggregate3_f11 = 0;
                        double aggregate3_f12 = 0;
                        long aggregate3_f13 = 0;
                        long aggregate3_f14 = 0;
                        GenericArray aggregate3_f15 =
                            new GenericData.Array(AGGREGATE3_F15_SCHEMA, new ArrayList());
                        boolean doLoop = true;
                        while (doLoop) {
                          Long project2longCol = (Long) inputRecord.get("longCol");
                          Integer project2intCol = (Integer) inputRecord.get("intCol");
                          Double project2doubleCol = (Double) inputRecord.get("doubleCol");
                          String project2stringCol =
                              Objects.toString(inputRecord.get("stringCol"), null);
                          if (project2longCol != null) {
                            aggregate3_f2 =
                                aggregate3_f2 > project2longCol ? aggregate3_f2 : project2longCol;
                          }
                          if (project2intCol != null) {
                            aggregate3_f3 =
                                aggregate3_f3 > project2intCol ? aggregate3_f3 : project2intCol;
                          }
                          if (project2doubleCol != null) {
                            aggregate3_f4 =
                                aggregate3_f4 > project2doubleCol
                                    ? aggregate3_f4
                                    : project2doubleCol;
                          }
                          if (project2stringCol != null) {
                            aggregate3_f5 =
                                ObjectUtils.compare(aggregate3_f5, project2stringCol) > 0
                                    ? aggregate3_f5
                                    : project2stringCol;
                          }
                          if (project2longCol != null) {
                            aggregate3_f6 =
                                aggregate3_f6 < project2longCol ? aggregate3_f6 : project2longCol;
                          }
                          if (project2intCol != null) {
                            aggregate3_f7 =
                                aggregate3_f7 < project2intCol ? aggregate3_f7 : project2intCol;
                          }
                          if (project2doubleCol != null) {
                            aggregate3_f8 =
                                aggregate3_f8 < project2doubleCol
                                    ? aggregate3_f8
                                    : project2doubleCol;
                          }
                          if (project2stringCol != null) {
                            aggregate3_f9 =
                                ObjectUtils.compare(aggregate3_f9, project2stringCol) < 0
                                    ? aggregate3_f9
                                    : project2stringCol;
                          }
                          if (project2longCol != null) {
                            aggregate3_f10 = aggregate3_f10 + project2longCol;
                          }
                          if (project2intCol != null) {
                            aggregate3_f11 = aggregate3_f11 + project2intCol;
                          }
                          if (project2doubleCol != null) {
                            aggregate3_f12 = aggregate3_f12 + project2doubleCol;
                          }
                          if (project2longCol != null) {
                            aggregate3_f13 = aggregate3_f13 + 1;
                          }
                          aggregate3_f14 = aggregate3_f14 + 1;
                          aggregate3_f15.add(project2longCol);
                          if (iterator.hasNext()) {
                            inputRecord = iterator.next();
                          } else {
                            doLoop = false;
                          }
                        }
                        aggregate3Record.put("_f2", (Object) aggregate3_f2);
                        aggregate3Record.put("_f3", (Object) aggregate3_f3);
                        aggregate3Record.put("_f4", (Object) aggregate3_f4);
                        aggregate3Record.put("_f5", (Object) aggregate3_f5);
                        aggregate3Record.put("_f6", (Object) aggregate3_f6);
                        aggregate3Record.put("_f7", (Object) aggregate3_f7);
                        aggregate3Record.put("_f8", (Object) aggregate3_f8);
                        aggregate3Record.put("_f9", (Object) aggregate3_f9);
                        aggregate3Record.put("_f10", (Object) aggregate3_f10);
                        aggregate3Record.put("_f11", (Object) aggregate3_f11);
                        aggregate3Record.put("_f12", (Object) aggregate3_f12);
                        aggregate3Record.put("_f13", (Object) aggregate3_f13);
                        aggregate3Record.put("_f14", (Object) aggregate3_f14);
                        aggregate3Record.put("_f15", (Object) aggregate3_f15);
                        return aggregate3Record;
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(AGGREGATE3_SCHEMA)))
            .apply(Window.into(new GlobalWindows()));
    PCollection<KV<String, GenericRecord>> project4 =
        aggregate3
            .apply(
                MapElements.via(
                    new SimpleFunction<KV<String, GenericRecord>, KV<String, GenericRecord>>() {
                      public KV<String, GenericRecord> apply(
                          KV<String, GenericRecord> aggregate3KV) {
                        final GenericRecord aggregate3Record = aggregate3KV.getValue();
                        GenericRecord project4Record = new GenericData.Record(PROJECT4_SCHEMA);
                        final String var0 =
                            Objects.toString(aggregate3Record.get("strGroupCol"), null);
                        project4Record.put("strGroupCol", var0);
                        final Long var1 = (Long) aggregate3Record.get("longGroupCol");
                        project4Record.put("longGroupCol", var1);
                        final Long var2 = (Long) aggregate3Record.get("_f2");
                        project4Record.put("maxLong", var2);
                        final Integer var3 = (Integer) aggregate3Record.get("_f3");
                        project4Record.put("maxInt", var3);
                        final Double var4 = (Double) aggregate3Record.get("_f4");
                        project4Record.put("maxDouble", var4);
                        final String var5 = Objects.toString(aggregate3Record.get("_f5"), null);
                        project4Record.put("maxString", var5);
                        final Long var6 = (Long) aggregate3Record.get("_f6");
                        project4Record.put("minLong", var6);
                        final Integer var7 = (Integer) aggregate3Record.get("_f7");
                        project4Record.put("minInt", var7);
                        final Double var8 = (Double) aggregate3Record.get("_f8");
                        project4Record.put("minDouble", var8);
                        final String var9 = Objects.toString(aggregate3Record.get("_f9"), null);
                        project4Record.put("minString", var9);
                        final Long var10 = (Long) aggregate3Record.get("_f10");
                        project4Record.put("sumLong", var10);
                        final Integer var11 = (Integer) aggregate3Record.get("_f11");
                        final Long var12 = var11 != null ? var11.longValue() : (Long) null;
                        project4Record.put("sumInt", var12);
                        final Double var13 = (Double) aggregate3Record.get("_f12");
                        project4Record.put("sumDouble", var13);
                        final Long var14 = (Long) aggregate3Record.get("_f13");
                        project4Record.put("countLong", var14);
                        final Long var15 = (Long) aggregate3Record.get("_f14");
                        project4Record.put("countStar", var15);
                        final GenericArray var16 = (GenericArray) aggregate3Record.get("_f15");
                        project4Record.put("longMultiset", var16);
                        return KV.of(aggregate3KV.getKey(), project4Record);
                      }
                    }))
            .setCoder(KvCoder.of(StringUtf8Coder.of(), AvroCoder.of(PROJECT4_SCHEMA)));
    project4.apply(KafkaIOGenericRecord.write().withTopic("aggregate_output"));
    pipeline.run();
  }
}

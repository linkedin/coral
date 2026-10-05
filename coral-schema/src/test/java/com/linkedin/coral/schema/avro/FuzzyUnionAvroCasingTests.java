/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Union;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;


/**
 * ACTIONITEM-25474: a stored view's fuzzy UNION projects an evolved struct column with {@code generic_project}, whose
 * Avro schema is derived from lowercase Calcite names, while the other branch passes the source's camelCase Avro
 * names through, so the exact-name Avro UNION merge rejects the view.
 *
 * <p>Only conversions that fail at the baseline for that reason may change, and only by using the source's field
 * spelling for the generated fields. Every other outcome (successful schemas and all other failures, in every strict
 * and lowercase mode) is frozen byte for byte from the unchanged baseline in {@code fuzzyunion-casing-baseline.txt}.
 * The repaired schemas in {@code fuzzyunion-casing-expected.txt} differ from what the baseline merger would produce
 * for name-matched inputs only in that the names now match.
 */
public class FuzzyUnionAvroCasingTests {
  private static final String DB = "fz";
  private static final String BASELINE = "fuzzyunion-casing-baseline.txt";
  private static final String EXPECTED = "fuzzyunion-casing-expected.txt";

  /** Views that fail at the baseline only on field casing, with their repaired schemas in {@link #EXPECTED}. */
  private static final String[] REPAIRED =
      { "v_n1", "v_n1_r", "v_n2_3", "v_n3_header", "v_n3_rh", "v_n4", "v_n4_access", "v_n5", "v_n7", "v_n8_ordinal", "v_n8_outer", "v_n12", "v_n12_r" };

  /**
   * Views that fail at the baseline and must keep exactly that failure. v_n12_paren is the approved limitation (an
   * ordinary Project between the UNIONs carries no repair); in v_n12_latent the working inner UNION's source spellings
   * (pageKey, PageKey) disagree, so the outer UNION keeps its own original failure, not the inner one.
   */
  private static final String[] STILL_FAILING =
      { "v_n9_amb", "v_n9_missing", "v_n9_container", "v_n10_pascal", "v_n12_paren", "v_n12_latent", "v_n13_pascal", "v_n13_union" };

  /** Views that succeed at the baseline (non-strict); their outputs must not change. */
  private static final String[] WORKING =
      { "v_n4_access_r", "v_n6_direct", "v_n6_gg", "v_n6_lower", "v_n7_lc", "v_n8_item", "v_n8_outer_direct", "v_n9_amb_gg", "v_n9_missing_gg", "v_n11_fallback", "v_n11_fallback_r", "v_n11_lower", "v_n11_lower_r", "v_n11_derived", "v_n11_derived_r", "v_n12_gen", "v_n12_lower", "v_n12_fallback" };

  private FuzzyUnionCasingCatalog catalog;
  private Map<String, String> baseline;
  private Map<String, String> expected;

  @BeforeClass
  public void beforeClass() {
    catalog = buildCatalog();
    baseline = sections(TestUtils.loadSchema(BASELINE));
    expected = sections(TestUtils.loadSchema(EXPECTED));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // Natural plans: helper placement, operand kinds and UNION nesting, recorded at the baseline
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testNaturalPlanShapes() {
    // G = three-operand generic_project over an input ref, G[...] over another (inlined) operand; D = no helper;
    // P = an ordinary Project between UNIONs. N4's aliased nested struct and N8's wrapper become the operand.
    String[][] shapes =
        { { "v_n1", "U(D,G)" }, { "v_n1_r", "U(G,D)" }, { "v_n2_3", "U(U(G,D),G)" }, { "v_n3_header", "U(D,G)" }, { "v_n4", "U(D,G)" }, { "v_n4_access", "U(D,G[$1.device])" }, { "v_n4_access_r", "U(G[$1.device],G[$1.device])" }, { "v_n5", "U(D,G,G,G,G)" }, { "v_n7", "U(D,G)" }, { "v_n8_ordinal", "U(D,G[li_groot_cast_nullability($0, $1)])" }, { "v_n8_item", "U(D,G[ITEM($1, 1)])" }, { "v_n12", "U(U(G,G),D)" }, { "v_n12_r", "U(U(D,G),G)" }, { "v_n12_paren", "U(D,P(U(G,G)))" }, { "v_n12_latent", "U(U(G,G),D)" }, { "v_n6_gg", "U(G,G)" }, { "v_n11_fallback", "U(D,G)" }, { "v_n11_derived_r", "U(G,D)" } };
    for (String[] shape : shapes) {
      RelNode union = new HiveToRelConverter(catalog).convertView(DB, shape[0]).getInput(0);
      Assert.assertEquals(shape(union), shape[1], shape[0]);
    }
  }

  @Test
  public void testGeneratedHelperIsUnchanged() {
    // The helper's own output is the baseline one: [null, record] without a default, lowercase names, synthetic
    // rel_avro record, nullable nested fields; converting the generated branch alone must stay exactly that.
    RelNode union = new HiveToRelConverter(catalog).convertView(DB, "v_n1").getInput(0);
    Project generated = (Project) union.getInput(1);
    RexCall call = (RexCall) generated.getProjects().get(1);
    Assert.assertTrue(call.getOperator() instanceof GenericProjectFunction);
    Assert.assertEquals(call.getOperands().size(), 3);
    Assert.assertEquals(call.getOperands().get(0).toString(), "$1");
    Assert.assertEquals(((RexLiteral) call.getOperands().get(1)).getValueAs(String.class), "requestheader");
    Assert.assertEquals(((RexLiteral) call.getOperands().get(2)).getValueAs(String.class),
        "struct<pagekey:string,memberid:bigint>");

    for (String view : REPAIRED) {
      RelNode root = new HiveToRelConverter(catalog).convertView(DB, view);
      List<RelNode> branches = new ArrayList<>();
      leafBranches(root.getInput(0), branches);
      for (int i = 0; i < branches.size(); i++) {
        RelNode branch = branches.get(i);
        String key = view + " branch" + i;
        Assert.assertEquals(outcome(() -> new RelToAvroSchemaConverter(catalog).convert(branch, false, false)),
            frozen(key), key);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------
  // The repair
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testCasingOnlyUnionFailuresAreRepaired() {
    // N1-N5, N7, N8 (ordinary access above a repaired UNION; an ordinal-return wrapper operand) and N12 (adjacent
    // UNION chains); N4 also with an aliased nested struct as the helper operand. The generated fields
    // take the source spelling; field order, selected fields, defaults, docs, nullability and record identities are
    // the baseline merge of the two branches. Extra evolved fields stay removed.
    List<String> failures = new ArrayList<>();
    for (String view : REPAIRED) {
      for (ViewToAvroSchemaConverter converter : converters()) {
        String actual = outcome(() -> converter.toAvroSchema(DB, view, false, false));
        if (!actual.equals(expected.get(view))) {
          failures.add(view + ":\n  expected " + expected.get(view) + "\n  actual   " + actual);
        } else {
          new Schema.Parser().parse(actual); // with default validation
        }
      }
    }
    Assert.assertTrue(failures.isEmpty(), String.join("\n", failures));
  }

  @Test
  public void testRepairIsRepeatableAndLeavesSourcesUnchanged() {
    for (String view : REPAIRED) { // each oracle is exactly Avro's serialization of itself (attainable)
      Assert.assertEquals(new Schema.Parser().parse(expected.get(view)).toString(true), expected.get(view), view);
    }
    ViewToAvroSchemaConverter converter = ViewToAvroSchemaConverter.create(catalog);
    String literal = catalog.getTable(DB, "pv_evolved").getParameters().toString();
    String first = outcome(() -> converter.toAvroSchema(DB, "v_n1", false, false));
    Assert.assertEquals(outcome(() -> converter.toAvroSchema(DB, "v_n1", false, false)), first, "repeat conversion");
    Assert.assertEquals(catalog.getTable(DB, "pv_evolved").getParameters().toString(), literal, "source unchanged");
  }

  @Test
  public void testRepairedMetadataIsTheLowercaseTwinsBaseline() {
    // N7: v_n7 and v_n7_lc have the same tables except that v_n7_lc's Avro field names are lowercase. The twin works
    // at the baseline; the repaired mixed-case view must be its frozen schema with only the field names restored.
    String twin = frozen("v_n7_lc strict=false lower=false").replace("v_n7_lc", "v_n7");
    for (String name : CAMEL_META_FIELDS) {
      twin = twin.replace("\"name\" : \"" + name.toLowerCase() + "\"", "\"name\" : \"" + name + "\"");
    }
    Assert.assertEquals(expected.get("v_n7"), twin);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // Everything else is the baseline
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testAllOtherOutcomesAreTheBaseline() {
    // N2 (strict modes), N6, N9-N11, N12 (non-adjacent chain, working chains), N13; forceLowercase twins of every
    // view; strict mode of the repaired views, whose generated records keep their rel_avro namespace and therefore
    // still fail. Failures keep the exact exception class and message.
    List<String> failures = new ArrayList<>();
    List<String> views = new ArrayList<>();
    for (String[] group : new String[][] { REPAIRED, STILL_FAILING, WORKING }) {
      views.addAll(Arrays.asList(group));
    }
    for (String view : views) {
      for (boolean strict : new boolean[] { false, true }) {
        for (boolean lower : new boolean[] { false, true }) {
          String key = view + " strict=" + strict + " lower=" + lower;
          if (!strict && !lower && expected.containsKey(view)) {
            continue;
          }
          for (ViewToAvroSchemaConverter converter : converters()) {
            String actual = outcome(() -> converter.toAvroSchema(DB, view, strict, lower));
            if (!actual.equals(frozen(key))) {
              failures.add(key + ":\n  expected " + frozen(key) + "\n  actual   " + actual);
            }
          }
        }
      }
    }
    Assert.assertTrue(failures.isEmpty(), String.join("\n", failures));
  }

  private static final String[] CAMEL_META_FIELDS =
      { "eventCount", "eventTime", "pageKey", "createdAt", "optionalNote", "retryCount" };

  private static String json(String singleQuoted) {
    return singleQuoted.replace('\'', '"');
  }

  private static String event(String name, String namespace, String fields) {
    return json("{'type':'record','name':'" + name + "','namespace':'" + namespace + "','fields':[" + fields + "]}");
  }

  /** {@code id} plus a {@code requestHeader} record {pageKey, memberId, extra...}. */
  private static String pageView(String namespace, String header, String... extra) {
    StringBuilder fields = new StringBuilder(
        "{'name':'id','type':'long'},{'name':'requestHeader','type':{'type':'record','name':'RequestHeader',"
            + "'fields':[" + header);
    for (String field : extra) {
      fields.append(',').append(field);
    }
    return event("PageViewEvent", namespace, fields.append("]}}").toString());
  }

  private static String header(String name, String... extra) {
    StringBuilder record = new StringBuilder("{'type':'record','name':'" + name
        + "','fields':[{'name':'pageKey','type':'string'},{'name':'memberId','type':'long'}");
    for (String field : extra) {
      record.append(',').append(field);
    }
    return record.append("]}").toString();
  }

  static FuzzyUnionCasingCatalog buildCatalog() {
    String ns = "com.linkedin.events";
    String pageKey = "{'name':'pageKey','type':'string'},{'name':'memberId','type':'long'}";
    String tracking = "{'name':'trackingId','type':['null','string'],'default':null}";
    String session = "{'name':'sessionId','type':['null','string'],'default':null}";
    String h = "requestheader struct<pagekey:string,memberid:bigint>";
    String ht = "requestheader struct<pagekey:string,memberid:bigint,trackingid:string>";
    String hs = "requestheader struct<pagekey:string,memberid:bigint,sessionid:string>";
    FuzzyUnionCasingCatalog c = new FuzzyUnionCasingCatalog();

    // N1/N2: pv_small keeps the original header; the evolved tables add a field. pv_small is a non-AvroSerDe table
    // with an avro.schema.literal, as in the incident.
    c.addTable(DB, "pv_small", pageView(ns, pageKey), "id bigint", h);
    c.addAvroTable(DB, "pv_evolved", pageView(ns, pageKey, tracking), "id bigint", ht);
    c.addTable(DB, "pv_evolved2", pageView(ns, pageKey, session), "id bigint", hs);
    // N11: direct branches whose schemas are lowercase: lowercase literal, no literal (Hive fallback)
    c.addTable(DB, "pv_lower", pageView(ns, "{'name':'pagekey','type':'string'},{'name':'memberid','type':'long'}")
        .replace("requestHeader", "requestheader"), "id bigint", h);
    c.addTable(DB, "pv_lower_evolved",
        pageView(ns, "{'name':'pagekey','type':'string'},{'name':'memberid','type':'long'}",
            "{'name':'trackingid','type':['null','string'],'default':null}").replace("requestHeader", "requestheader"),
        "id bigint", ht);
    c.addTable(DB, "pv_fallback", null, "id bigint", h);
    // N9/N10/N13: provenance that cannot repair the UNION
    c.addTable(
        DB, "pv_amb_evolved", pageView(ns,
            pageKey.replace("{'name':'memberId'", "{'name':'PageKey','type':'string'},{'name':'memberId'"), tracking),
        "id bigint", ht);
    c.addTable(DB, "pv_missing_evolved", pageView(ns, "{'name':'memberId','type':'long'}", tracking), "id bigint", ht);
    // the Avro literal has an array where the Hive column is a struct
    c.addTable(DB, "pv_container_evolved",
        event("PageViewEvent", ns, "{'name':'id','type':'long'},"
            + "{'name':'requestHeader','type':{'type':'array','items':" + header("RequestHeader", tracking) + "}}"),
        "id bigint", ht);
    c.addTable(DB, "pv_pascal", pageView(ns, pageKey.replace("'pageKey'", "'PageKey'")), "id bigint", h);
    c.addTable(DB, "pv_pascal_evolved", pageView(ns, pageKey.replace("'pageKey'", "'PageKey'"), tracking), "id bigint",
        ht);
    String payload = "{'name':'payload','type':['null','int','string'],'default':null}";
    String hu = "requestheader struct<pagekey:string,memberid:bigint,payload:uniontype<int,string>";
    c.addTable(DB, "u_small", pageView(ns, pageKey, payload), "id bigint", hu + ">");
    c.addTable(DB, "u_evolved", pageView(ns, pageKey, payload, tracking), "id bigint", hu + ",trackingid:string>");

    // N4: a deeper record gains and reorders fields
    String device = "{'name':'device','type':{'type':'record','name':'Device','fields':[%s]}}";
    String osName = "{'name':'osName','type':'string'}";
    String osVersion = "{'name':'osVersion','type':'string'}";
    c.addTable(DB, "deep_small",
        pageView(ns, "{'name':'pageKey','type':'string'}," + String.format(device, osName + "," + osVersion)),
        "id bigint", "requestheader struct<pagekey:string,device:struct<osname:string,osversion:string>>");
    c.addTable(DB, "deep_evolved",
        pageView(ns,
            "{'name':'pageKey','type':'string'}," + String.format(device,
                osVersion + ",{'name':'screenWidth','type':['null','int'],'default':null}," + osName)),
        "id bigint",
        "requestheader struct<pagekey:string,device:struct<osversion:string,screenwidth:int,osname:string>>");

    // N5: arrays and maps of records, required and optional containers and elements/values
    String coll = "{'name':'id','type':'long'},{'name':'headers','type':{'type':'array','items':%s}},"
        + "{'name':'optHeaders','type':['null',{'type':'array','items':['null',%s]}],'default':null},"
        + "{'name':'byKey','type':{'type':'map','values':%s}},"
        + "{'name':'optByKey','type':['null',{'type':'map','values':['null',%s]}],'default':null}";
    String s = "struct<pagekey:string,memberid:bigint>";
    String st = "struct<pagekey:string,memberid:bigint,trackingid:string>";
    c.addTable(DB, "coll_small",
        event("CollEvent", ns,
            String.format(coll, header("ArrHeader"), header("OptArrHeader"), header("MapHeader"),
                header("OptMapHeader"))),
        "id bigint", "headers array<" + s + ">", "optheaders array<" + s + ">", "bykey map<string," + s + ">",
        "optbykey map<string," + s + ">");
    c.addTable(DB, "coll_evolved",
        event("CollEvent", ns,
            String.format(coll, header("ArrHeader", tracking), header("OptArrHeader", tracking),
                header("MapHeader", tracking), header("OptMapHeader", tracking))),
        "id bigint", "headers array<" + st + ">", "optheaders array<" + st + ">", "bykey map<string," + st + ">",
        "optbykey map<string," + st + ">");

    // N7: unchanged siblings and generated leaves with defaults, docs, aliases, logical, enum and fixed types.
    // meta_*_lc is the same schema with lowercase field names.
    String meta = "{'name':'id','type':'long','doc':'Event id'},"
        + "{'name':'eventCount','type':'int','doc':'How many','default':0,'aliases':['countOld']},"
        + "{'name':'note','type':['null','string'],'default':null},"
        + "{'name':'label','type':['string','null'],'default':'none'},"
        + "{'name':'status','type':{'type':'enum','name':'Status','symbols':['ON','OFF']},'default':'ON'},"
        + "{'name':'eventTime','type':{'type':'long','logicalType':'timestamp-millis'}},"
        + "{'name':'header','type':{'type':'record','name':'Header','doc':'Header doc','fields':["
        + "{'name':'pageKey','type':'string','doc':'Page key'},"
        + "{'name':'kind','type':{'type':'enum','name':'Kind','symbols':['A','B']}},"
        + "{'name':'md5','type':{'type':'fixed','name':'Md5','size':16}},"
        + "{'name':'createdAt','type':{'type':'long','logicalType':'timestamp-millis'}},"
        + "{'name':'optionalNote','type':['null','string'],'default':null},"
        + "{'name':'retryCount','type':'int','default':1},"
        + "{'name':'tags','type':{'type':'array','items':'string'}},"
        + "{'name':'attrs','type':{'type':'map','values':'long'}}%s]}}";
    String metaCols = "header struct<pagekey:string,kind:string,md5:binary,createdat:timestamp,optionalnote:string,"
        + "retrycount:int,tags:array<string>,attrs:map<string,bigint>%s>";
    String metaColumns = "id bigint|eventcount int|note string|label string|status string|eventtime timestamp|";
    for (boolean lowercase : new boolean[] { false, true }) {
      String small = String.format(meta, "");
      String evolved = String.format(meta, ",{'name':'extra','type':['null','string'],'default':null}");
      String suffix = lowercase ? "_lc" : "";
      if (lowercase) {
        for (String name : CAMEL_META_FIELDS) {
          small = small.replace("'" + name + "'", "'" + name.toLowerCase() + "'");
          evolved = evolved.replace("'" + name + "'", "'" + name.toLowerCase() + "'");
        }
      }
      c.addTable(DB, "meta_small" + suffix, event("MetaEvent", ns, small),
          (metaColumns + String.format(metaCols, "")).split("\\|"));
      c.addTable(DB, "meta_evolved" + suffix, event("MetaEvent", ns, evolved),
          (metaColumns + String.format(metaCols, ",extra:string")).split("\\|"));
    }

    Map<String, String> views = new LinkedHashMap<>();
    String rh = "SELECT id, requestheader FROM fz.";
    views.put("v_n1", rh + "pv_small UNION ALL " + rh + "pv_evolved");
    views.put("v_n1_r", rh + "pv_evolved UNION ALL " + rh + "pv_small");
    views.put("v_n2_3", rh + "pv_evolved UNION ALL " + rh + "pv_small UNION ALL " + rh + "pv_evolved2");
    views.put("v_n3_header", "SELECT id, requestheader AS header FROM fz.pv_small UNION ALL "
        + "SELECT id, requestheader AS header FROM fz.pv_evolved");
    views.put("v_n3_rh", "SELECT id, requestheader AS request_header FROM fz.pv_small UNION ALL "
        + "SELECT id, requestheader AS request_header FROM fz.pv_evolved");
    views.put("v_n4", rh + "deep_small UNION ALL " + rh + "deep_evolved");
    String coll5 = "SELECT id, headers, optheaders, bykey, optbykey FROM fz.";
    String access = "SELECT id, requestheader.device AS device FROM fz.";
    views.put("v_n4_access", access + "deep_small UNION ALL " + access + "deep_evolved");
    views.put("v_n4_access_r", access + "deep_evolved UNION ALL " + access + "deep_small");
    views.put("v_n5", coll5 + "coll_small UNION ALL " + coll5 + "coll_evolved");
    views.put("v_n6_direct", rh + "pv_small UNION ALL " + rh + "pv_small");
    views.put("v_n6_gg", rh + "pv_evolved UNION ALL " + rh + "pv_evolved2");
    views.put("v_n6_lower", rh + "pv_lower UNION ALL " + rh + "pv_lower_evolved");
    String meta7 = "SELECT id, eventcount, note, label, status, eventtime, header FROM fz.";
    views.put("v_n7", meta7 + "meta_small UNION ALL " + meta7 + "meta_evolved");
    views.put("v_n7_lc", meta7 + "meta_small_lc UNION ALL " + meta7 + "meta_evolved_lc");
    views.put("v_n8_item",
        "SELECT id, headers[0] AS h FROM fz.coll_small UNION ALL " + "SELECT id, headers[0] AS h FROM fz.coll_evolved");
    String cast = "SELECT id, li_groot_cast_nullability(id, requestheader) AS requestheader FROM fz.";
    views.put("v_n8_ordinal", cast + "pv_small UNION ALL " + cast + "pv_evolved");
    views.put("v_n8_outer", "SELECT v.requestheader.pagekey AS pk, v.requestheader FROM fz.v_n1 v");
    views.put("v_n8_outer_direct", "SELECT v.requestheader.pagekey AS pk, v.requestheader FROM fz.v_n6_direct v");
    views.put("v_n9_amb", rh + "pv_small UNION ALL " + rh + "pv_amb_evolved");
    views.put("v_n9_amb_gg", rh + "pv_amb_evolved UNION ALL " + rh + "pv_evolved2");
    views.put("v_n9_missing", rh + "pv_small UNION ALL " + rh + "pv_missing_evolved");
    views.put("v_n9_missing_gg", rh + "pv_missing_evolved UNION ALL " + rh + "pv_evolved2");
    views.put("v_n9_container", rh + "pv_small UNION ALL " + rh + "pv_container_evolved");
    views.put("v_n10_pascal", rh + "pv_small UNION ALL " + rh + "pv_pascal");
    String derived = "SELECT id, named_struct('pagekey', requestheader.pagekey, 'memberid', requestheader.memberid) "
        + "AS requestheader FROM fz.pv_small";
    for (String[] direct : new String[][] { { "fallback", rh + "pv_fallback" }, { "lower", rh
        + "pv_lower" }, { "derived", derived } }) {
      views.put("v_n11_" + direct[0], direct[1] + " UNION ALL " + rh + "pv_evolved");
      views.put("v_n11_" + direct[0] + "_r", rh + "pv_evolved UNION ALL " + direct[1]);
    }
    views.put("v_n12", rh + "pv_evolved UNION ALL " + rh + "pv_evolved2 UNION ALL " + rh + "pv_small");
    views.put("v_n12_r", rh + "pv_small UNION ALL " + rh + "pv_evolved UNION ALL " + rh + "pv_evolved2");
    views.put("v_n12_paren",
        rh + "pv_small UNION ALL SELECT * FROM (" + rh + "pv_evolved UNION ALL " + rh + "pv_evolved2) x");
    views.put("v_n12_latent", rh + "pv_evolved2 UNION ALL " + rh + "pv_pascal_evolved UNION ALL " + rh + "pv_small");
    views.put("v_n12_gen", rh + "pv_evolved UNION ALL " + rh + "pv_evolved2 UNION ALL " + rh + "pv_evolved");
    views.put("v_n12_lower", rh + "pv_evolved UNION ALL " + rh + "pv_evolved2 UNION ALL " + rh + "pv_lower");
    views.put("v_n12_fallback", rh + "pv_evolved UNION ALL " + rh + "pv_evolved2 UNION ALL " + rh + "pv_fallback");
    views.put("v_n13_pascal", rh + "pv_small UNION ALL " + rh + "pv_pascal_evolved");
    views.put("v_n13_union", rh + "u_small UNION ALL " + rh + "u_evolved");
    // Declared view columns only matter where a view is read by another one (N8).
    views.forEach((name, sql) -> c.addView(DB, name, sql, "id bigint", h));
    return c;
  }

  /** Both public entry points over the same fixture. */
  private ViewToAvroSchemaConverter[] converters() {
    return new ViewToAvroSchemaConverter[] { ViewToAvroSchemaConverter.create(catalog), ViewToAvroSchemaConverter
        .create(catalog.asCoralCatalog()) };
  }

  /** A pretty-printed schema, or {@code !exceptionClass} and the exact message on the next line. */
  static String outcome(java.util.function.Supplier<Schema> conversion) {
    try {
      return conversion.get().toString(true);
    } catch (RuntimeException e) {
      return "!" + e.getClass().getName() + "\n" + e.getMessage();
    }
  }

  private String frozen(String key) {
    String value = baseline.get(key);
    Assert.assertNotNull(value, "no frozen baseline outcome for " + key);
    return value;
  }

  /** Parses {@code === key} sections; each value runs to the next section header. */
  static Map<String, String> sections(String text) {
    Map<String, String> result = new LinkedHashMap<>();
    for (String section : ("\n" + text).split("\n=== ")) {
      if (!section.trim().isEmpty()) {
        int newline = section.indexOf('\n');
        result.put(section.substring(0, newline), section.substring(newline + 1));
      }
    }
    return result;
  }

  /** The UNION tree: U(...) for a UNION, one G per generic_project call of a branch, D without, P(...) above. */
  private static String shape(RelNode node) {
    if (node instanceof Union) {
      List<String> inputs = new ArrayList<>();
      node.getInputs().forEach(input -> inputs.add(shape(input)));
      return "U(" + String.join(",", inputs) + ")";
    }
    if (node instanceof Project && !(node.getInput(0) instanceof TableScan)) {
      return "P(" + shape(node.getInput(0)) + ")";
    }
    List<String> helpers = new ArrayList<>();
    for (RexNode expression : ((Project) node).getProjects()) {
      if (expression instanceof RexCall && ((RexCall) expression).getOperator() instanceof GenericProjectFunction) {
        RexCall call = (RexCall) expression;
        Assert.assertEquals(call.getOperands().size(), 3, call.toString());
        Assert.assertTrue(call.getOperands().get(1) instanceof RexLiteral, call.toString());
        Assert.assertTrue(call.getOperands().get(2) instanceof RexLiteral, call.toString());
        RexNode operand = call.getOperands().get(0);
        helpers.add(operand instanceof RexInputRef ? "G" : "G[" + operand + "]");
      }
    }
    return helpers.isEmpty() ? "D" : String.join(",", helpers);
  }

  private static void leafBranches(RelNode node, List<RelNode> branches) {
    if (node instanceof Union) {
      node.getInputs().forEach(input -> leafBranches(input, branches));
    } else if (node instanceof Project && node.getInput(0) instanceof TableScan) {
      branches.add(node);
    } else {
      node.getInputs().forEach(input -> leafBranches(input, branches));
    }
  }
}

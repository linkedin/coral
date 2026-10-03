/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Union;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.Assert;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.functions.OrdinalReturnTypeInferenceV2;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static org.apache.calcite.sql.type.OperandTypes.*;


/**
 * Shared fixtures and oracles for the fuzzy-UNION Avro schema tests.
 */
final class FuzzyUnionFixtures {
  static final String DB = "fz";
  static final String MAKE_HEADER_UDF = "com.linkedin.coral.schema.avro.FuzzyUnionMakeHeader";
  static final String RETURN_ARG_UDF = "com.linkedin.coral.schema.avro.FuzzyUnionReturnSecondArg";
  static final String MAKE_NESTED_UDF = "com.linkedin.coral.schema.avro.FuzzyUnionMakeNested";
  private static final String RESOURCE_DIR = "fuzzyunion/";

  private FuzzyUnionFixtures() {
  }

  /** Registers the struct-returning and ordinal-return test UDFs once per JVM. */
  static synchronized void registerUdfs() {
    StaticHiveFunctionRegistry registry = new StaticHiveFunctionRegistry();
    if (registry.lookup(MAKE_HEADER_UDF).isEmpty()) {
      StaticHiveFunctionRegistry.createAddUserDefinedFunction(MAKE_HEADER_UDF,
          FunctionReturnTypes.rowOf(ImmutableList.of("pageKey", "memberId", "extra"),
              ImmutableList.of(SqlTypeName.VARCHAR, SqlTypeName.INTEGER, SqlTypeName.VARCHAR)),
          family(SqlTypeFamily.NUMERIC));
    }
    if (registry.lookup(MAKE_NESTED_UDF).isEmpty()) {
      // Returns struct<child:struct<pageKey,memberId,extra>, other:int>: a metadata-free parent struct.
      StaticHiveFunctionRegistry.createAddUserDefinedFunction(MAKE_NESTED_UDF,
          FunctionReturnTypes.rowOfInference(ImmutableList.of("child", "other"),
              ImmutableList.of(opBinding -> opBinding.getTypeFactory().createStructType(
                  ImmutableList.of(opBinding.getTypeFactory().createSqlType(SqlTypeName.VARCHAR),
                      opBinding.getTypeFactory().createSqlType(SqlTypeName.INTEGER),
                      opBinding.getTypeFactory().createSqlType(SqlTypeName.VARCHAR)),
                  ImmutableList.of("pageKey", "memberId", "extra")), ReturnTypes.INTEGER)),
          family(SqlTypeFamily.NUMERIC));
    }
    if (registry.lookup(RETURN_ARG_UDF).isEmpty()) {
      StaticHiveFunctionRegistry.createAddUserDefinedFunction(RETURN_ARG_UDF, new OrdinalReturnTypeInferenceV2(1),
          family(SqlTypeFamily.STRING, SqlTypeFamily.ANY));
    }
  }

  static String load(String name) {
    return TestUtils.loadSchema(RESOURCE_DIR + name);
  }

  /** Builds every base table and stored view used by the fuzzy-UNION tests. */
  static FuzzyUnionTestCatalog buildCatalog() {
    FuzzyUnionTestCatalog c = new FuzzyUnionTestCatalog();

    // Incident path: lowercase Hive columns, case-preserved Avro only in avro.schema.literal.
    c.addHiveTableWithAvroLiteral(DB, "pv_base", load("pv_base.avsc"), "eventid bigint",
        "requestheader struct<pagekey:string,path:string>", "tag string");
    c.addHiveTableWithAvroLiteral(DB, "pv_evolved", load("pv_evolved.avsc"), "eventid bigint",
        "requestheader struct<pagekey:string,trackingcode:string,path:string>", "tag string");
    c.addHiveTableWithAvroLiteral(DB, "pv_evolved2", load("pv_evolved2.avsc"), "eventid bigint",
        "requestheader struct<pagekey:string,path:string,referrer:string>", "tag string");
    c.addHiveTableWithAvroLiteral(DB, "pv_reordered", load("pv_reordered.avsc"), "eventid bigint",
        "requestheader struct<path:string,pagekey:string>", "tag string");
    c.addHiveTableWithAvroLiteral(DB, "hdr_base", load("hdr_base.avsc"), "header struct<memberid:int,eventtime:bigint>",
        "viewerid bigint");
    c.addHiveTableWithAvroLiteral(DB, "hdr_evolved", load("hdr_evolved.avsc"),
        "header struct<memberid:int,eventtime:bigint,device:string>", "viewerid bigint");
    c.addHiveTableWithAvroLiteral(DB, "amb_base", load("amb_base.avsc"), "id bigint", "s struct<foo:int>");
    c.addHiveTableWithAvroLiteral(DB, "amb_dup", load("amb_dup.avsc"), "id bigint", "s struct<foo:int,extra:int>");
    c.addHiveTableWithAvroLiteral(DB, "amb_b", load("amb_b.avsc"), "id bigint", "b struct<x:int>");
    c.addHiveTableWithAvroLiteral(DB, "amb_acc", load("amb_acc.avsc"), "id bigint",
        "s struct<foo:struct<x:int,y:int>>");

    // AvroSerDe tables: Hive reads the columns from the Avro schema itself.
    c.addAvroSerdeTable(DB, "ev_a", load("ev_a.avsc"), "id bigint", "hdr struct<memberid:int,checksum:binary>");
    c.addAvroSerdeTable(DB, "ev_b", load("ev_b.avsc"), "id bigint",
        "hdr struct<memberid:int,checksum:binary,browser:string>");
    c.addAvroSerdeTable(DB, "ev_c", load("ev_c.avsc"), "id bigint",
        "hdr struct<memberid:int,checksum:binary,device:string>");
    c.addAvroSerdeTable(DB, "ev_c_bad", load("ev_c_bad.avsc"), "id bigint",
        "hdr struct<memberid:int,checksum:binary,device:string>");
    c.addAvroSerdeTable(DB, "lc_base", load("lc_base.avsc"), "id bigint", "hdr struct<a:string,b:int>");
    c.addAvroSerdeTable(DB, "lc_evolved", load("lc_evolved.avsc"), "id bigint", "hdr struct<a:string,b:int,c:string>");
    c.addAvroSerdeTable(DB, "nest_base", load("nest_base.avsc"), "items array<struct<itemid:bigint,itemname:string>>",
        "attrs map<string,struct<attrkey:string,attrval:int>>", "mixed map<string,array<struct<leafid:int>>>");
    c.addAvroSerdeTable(DB, "nest_evolved", load("nest_evolved.avsc"),
        "items array<struct<itemid:bigint,itemextra:string,itemname:string>>",
        "attrs map<string,struct<attrkey:string,attrval:int,attrextra:string>>",
        "mixed map<string,array<struct<leafextra:int,leafid:int>>>");
    String nulS = "s struct<req:int,opt:int,arr:array<int>,arrnullelem:array<int>,m:map<string,string>";
    c.addAvroSerdeTable(DB, "nul_base", load("nul_base.avsc"), nulS + ">", "r struct<a:int,b:int>");
    c.addAvroSerdeTable(DB, "nul_evolved", load("nul_evolved.avsc"), nulS + ",sextra:string>",
        "r struct<a:int,b:int,rextra:int>");
    c.addAvroSerdeTable(DB, "nul_evolved_nullable", load("nul_evolved_nullable.avsc"), nulS + ",sextra:string>",
        "r struct<a:int,b:int,rextra:int>");
    String defD = "d struct<nodef:string,nulldef:string,intdef:int,strdef:string,";
    c.addAvroSerdeTable(DB, "def_base", load("def_base.avsc"),
        defD + "recdef:struct<x:int,y:string>,arrdef:array<int>,mapdef:map<string,int>>");
    c.addAvroSerdeTable(DB, "def_evolved", load("def_evolved.avsc"),
        defD + "recdef:struct<x:int,z:boolean,y:string>,arrdef:array<int>,mapdef:map<string,int>,dextra:string>");
    c.addAvroSerdeTable(DB, "meta_base", load("meta_base.avsc"), "id bigint",
        "info struct<firstfield:string,errinfo:struct<code:int>,lastfield:bigint>");
    c.addAvroSerdeTable(DB, "meta_evolved", load("meta_evolved.avsc"), "id bigint",
        "info struct<firstfield:string,extrafield:string,errinfo:struct<code:int,msg:string>,lastfield:bigint>");
    String ltL = "l struct<birthdate:date,createdat:timestamp,token:string,digest:binary,color:string";
    c.addAvroSerdeTable(DB, "lt_base", load("lt_base.avsc"), "id bigint", "amount decimal(10,2)", ltL + ">");
    c.addAvroSerdeTable(DB, "lt_evolved", load("lt_evolved.avsc"), "id bigint", "amount decimal(10,2)",
        ltL + ",lextra:string>");
    c.addAvroSerdeTable(DB, "neg_base", load("neg_base.avsc"), "id bigint", "rh struct<alpha:string,b:int>");
    c.addAvroSerdeTable(DB, "neg_leaf", load("neg_leaf.avsc"), "id bigint", "rh struct<alpha:int,b:int,c:int>");
    c.addAvroSerdeTable(DB, "neg_rec", load("neg_rec.avsc"), "id bigint", "rh struct<alpha:struct<x:int>,b:int,c:int>");
    c.addAvroSerdeTable(DB, "negc_base", load("negc_base.avsc"), "id bigint", "rh struct<arrfield:array<int>,b:int>");
    c.addAvroSerdeTable(DB, "negc_evolved", load("negc_evolved.avsc"), "id bigint",
        "rh struct<arrfield:map<string,int>,b:int,c:int>");
    c.addAvroSerdeTable(DB, "depth_direct", load("depth_direct.avsc"), "id bigint", "b struct<a:bigint>");
    c.addAvroSerdeTable(DB, "depth_src", load("depth_src.avsc"), "id bigint",
        "s struct<x:struct<a:int>,child:struct<x:struct<a:bigint,extra:int>>>");
    c.addAvroSerdeTable(DB, "depth2_direct", load("depth2_direct.avsc"), "id bigint", "b struct<id:bigint>");
    c.addAvroSerdeTable(DB, "depth2_src", load("depth2_src.avsc"), "id bigint",
        "s struct<id:int,child:struct<id:bigint,extra:int>>");
    c.addAvroSerdeTable(DB, "acc_direct", load("acc_direct.avsc"), "id bigint",
        "b struct<pagekey:string,memberid:int>");
    String accS = "s struct<child:struct<pagekey:string,memberid:int,extra:string>,other:int>";
    c.addAvroSerdeTable(DB, "acc_nested", load("acc_nested.avsc"), "id bigint", accS);
    c.addAvroSerdeTable(DB, "acc_nested_nullable", load("acc_nested_nullable.avsc"), "id bigint", accS);
    c.addAvroSerdeTable(DB, "flat_src", load("flat_src.avsc"), "id bigint", "pk string", "mid int", "e string");
    c.addAvroSerdeTable(DB, "case_camel", load("case_camel.avsc"), "pagekey string", "hdr struct<memberid:int>");
    c.addAvroSerdeTable(DB, "case_lower", load("case_lower.avsc"), "pagekey string", "hdr struct<memberid:int>");
    c.addAvroSerdeTable(DB, "dec_src", load("dec_src.avsc"), "id bigint",
        "p struct<amount:decimal(10,2),extra:string>");
    c.addAvroSerdeTable(DB, "fx_a", load("fx_a.avsc"), "id bigint", "c binary");
    c.addAvroSerdeTable(DB, "fx_a2", load("fx_a2.avsc"), "id bigint", "c binary");
    c.addAvroSerdeTable(DB, "fx_b", load("fx_b.avsc"), "id bigint", "c binary");
    c.addAvroSerdeTable(DB, "fx_c", load("fx_c.avsc"), "id bigint", "c binary");
    c.addAvroSerdeTable(DB, "d6_noscale", load("d6_noscale.avsc"), "id bigint", "amount decimal(10,0)",
        "r struct<amt:decimal(10,0)>");
    c.addAvroSerdeTable(DB, "d6_zero", load("d6_zero.avsc"), "id bigint", "amount decimal(10,0)",
        "r struct<amt:decimal(10,0)>");
    c.addAvroSerdeTable(DB, "d6_scale2", load("d6_scale2.avsc"), "id bigint", "amount decimal(10,2)",
        "r struct<amt:decimal(10,2)>");
    c.addAvroSerdeTable(DB, "d6_prec12", load("d6_prec12.avsc"), "id bigint", "amount decimal(12,0)",
        "r struct<amt:decimal(12,0)>");
    c.addAvroSerdeTable(DB, "req_a", load("req_a.avsc"), "id bigint", "f string");
    c.addAvroSerdeTable(DB, "req_b", load("req_a.avsc"), "id bigint", "f string");
    c.addAvroSerdeTable(DB, "req_c", load("req_c.avsc"), "id bigint", "f string");
    String addr = "struct<street:string,city:string>";
    String addrEvolved = "struct<street:string,zip:string,city:string>";
    c.addAvroSerdeTable(DB, "addr_base", load("addr_base.avsc"), "id bigint", "home " + addr, "work " + addr);
    c.addAvroSerdeTable(DB, "addr_evolved", load("addr_evolved.avsc"), "id bigint", "home " + addrEvolved,
        "work " + addrEvolved);
    String cdefC = "c struct<recs:array<struct<ea:int,eb:string>>,bykey:map<string,struct<va:int,vb:string>>,"
        + "rec:struct<ra:int,rb:string>";
    c.addAvroSerdeTable(DB, "cdef_base", load("cdef_base.avsc"), "id bigint", cdefC + ">");
    c.addAvroSerdeTable(DB, "cdef_evolved", load("cdef_evolved.avsc"), "id bigint",
        "c struct<recs:array<struct<ea:int,eextra:boolean,eb:string>>,"
            + "bykey:map<string,struct<vextra:boolean,va:int,vb:string>>,rec:struct<ra:int,rextra:boolean,rb:string>,"
            + "cextra:string>");
    String inner = "struct<pagekey:string,memberid:int,extra:string>";
    c.addAvroSerdeTable(DB, "itm_src", load("itm_src.avsc"), "id bigint",
        "arr array<struct<child:" + inner + ",other:int>>", "m map<string,struct<child:" + inner + ">>");
    c.addHiveTableWithAvroLiteral(DB, "itm_amb", load("itm_amb.avsc"), "id bigint",
        "arr array<struct<foo:struct<x:int,y:int>>>");
    c.addAvroSerdeTable(DB, "f3rec_a", load("f3rec_a.avsc"), "id bigint", "c struct<x:int,inner:struct<y:int>>");
    c.addAvroSerdeTable(DB, "f3rec_b", load("f3rec_b.avsc"), "id bigint",
        "c struct<x:int,inner:struct<y:int>,extra:string>");
    c.addAvroSerdeTable(DB, "f3col_a", load("f3col_a.avsc"), "id bigint", "arr array<struct<x:int>>",
        "m map<string,struct<x:int>>");
    c.addAvroSerdeTable(DB, "f3col_b", load("f3col_b.avsc"), "id bigint", "arr array<struct<x:int,extra:string>>",
        "m map<string,struct<x:int,extra:string>>");
    c.addAvroSerdeTable(DB, "f3ctl_a", load("f3ctl_a.avsc"), "id bigint", "c struct<z:int>", "d struct<x:int>");
    c.addAvroSerdeTable(DB, "f3ctl_b", load("f3ctl_b.avsc"), "id bigint", "c struct<z:int,extra:string>",
        "d struct<x:int,extra:string>");
    // D6: named types with namespace-free (alias_src) and qualified (alias_qsrc) declared aliases.
    c.addAvroSerdeTable(DB, "alias_src", load("alias_src.avsc"), "id bigint", "fielda string", "hash binary",
        "kind string", "nested struct<n:int>", "items array<struct<i:int>>", "levels map<string,string>",
        "hashes array<binary>", "err struct<code:int>");
    c.addAvroSerdeTable(DB, "alias_src_evolved", load("alias_src_evolved.avsc"), "id bigint", "fielda string",
        "hash binary", "kind string", "nested struct<n:int,extra:string>", "items array<struct<i:int>>",
        "levels map<string,string>", "hashes array<binary>", "err struct<code:int>");
    c.addAvroSerdeTable(DB, "alias_qsrc", load("alias_qsrc.avsc"), "id bigint", "fielda string", "hash binary",
        "kind string", "nested struct<n:int>", "items array<struct<i:int>>", "levels map<string,string>",
        "hashes array<binary>", "err struct<code:int>");
    c.addAvroSerdeTable(DB, "alias_qsrc_evolved", load("alias_qsrc_evolved.avsc"), "id bigint", "fielda string",
        "hash binary", "kind string", "nested struct<n:int,extra:string>", "items array<struct<i:int>>",
        "levels map<string,string>", "hashes array<binary>", "err struct<code:int>");
    c.addAvroSerdeTable(DB, "f4_a", load("f4_a.avsc"), "c struct<x:int,label:string>",
        "arr array<struct<x:int,tag:string>>");
    c.addAvroSerdeTable(DB, "f4_b", load("f4_b.avsc"), "c struct<x:int,label:string,extra:int>",
        "arr array<struct<x:int,tag:string,extra:int>>");
    c.addAvroSerdeTable(DB, "rep_src", load("rep_src.avsc"), "id bigint",
        "r struct<millis:timestamp,f8:binary,f16:binary,optf16:binary,arrf16:array<binary>,"
            + "mapmillis:map<string,timestamp>,extra:string>");
    c.addAvroSerdeTable(DB, "dur_src", load("dur_src.avsc"), "id bigint",
        "r struct<duration:binary,optduration:binary,extra:int>");
    // F8: one named record (com.linkedin.f8.Shared) used by several fields; _r evolves it (x nullable, extra).
    String sh = "struct<x:int>";
    String shE = "struct<x:int,extra:int>";
    for (String name : new String[] { "f8", "f8m", "f8n", "f8env", "f8ns" }) {
      c.addAvroSerdeTable(DB, name + "_l", load(name + "_l.avsc"), "a " + sh, "b " + sh);
      c.addAvroSerdeTable(DB, name + "_r", load(name + "_r.avsc"), "a " + shE, "b " + shE);
    }
    c.addAvroSerdeTable(DB, "f8e_l", load("f8e_l.avsc"), "b " + sh, "a " + sh);
    c.addAvroSerdeTable(DB, "f8e_r", load("f8e_r.avsc"), "b " + shE, "a " + shE);
    c.addAvroSerdeTable(DB, "f8c_l", load("f8c_l.avsc"), "d " + sh, "arr array<" + sh + ">", "m map<string," + sh + ">",
        "h struct<s:" + sh + ">");
    c.addAvroSerdeTable(DB, "f8c_r", load("f8c_r.avsc"), "d " + shE, "arr array<" + shE + ">",
        "m map<string," + shE + ">", "h struct<s:" + shE + ">");
    c.addAvroSerdeTable(DB, "f8neg_l", load("f8neg_l.avsc"), "leftuse struct<x:int,y:int>",
        "rightuse struct<x:int,y:int>");
    c.addAvroSerdeTable(DB, "f8neg_r", load("f8neg_r.avsc"), "leftuse struct<x:int>", "rightuse struct<y:int>");
    c.addAvroSerdeTable(DB, "bin_src", load("bin_src.avsc"), "id bigint",
        "b struct<raw:binary,optraw:binary,arrraw:array<binary>,text:string,extra:string>");
    c.addAvroSerdeTable(DB, "un_base", load("un_base.avsc"), "id bigint", "s struct<u:uniontype<int,string>,a:int>");
    c.addAvroSerdeTable(DB, "un_evolved", load("un_evolved.avsc"), "id bigint",
        "s struct<u:uniontype<int,string>,a:int,extra:int>");
    c.addAvroSerdeTable(DB, "unr_base", load("unr_base.avsc"), "id bigint", "choice uniontype<int,struct<a:int>>");
    c.addAvroSerdeTable(DB, "unr_evolved", load("unr_evolved.avsc"), "id bigint",
        "choice uniontype<int,struct<a:int,extra:int>>");

    // Stored views. Each is the view text as already saved before its base tables evolved.
    unionView(c, "v_t1", "SELECT * FROM fz.pv_base", "SELECT * FROM fz.pv_evolved");
    unionView(c, "v_t2", "SELECT * FROM fz.pv_evolved", "SELECT * FROM fz.pv_base");
    unionView(c, "v_t3", "SELECT header, viewerid FROM fz.hdr_base", "SELECT header, viewerid FROM fz.hdr_evolved");
    unionView(c, "v_t3r", "SELECT header, viewerid FROM fz.hdr_evolved", "SELECT header, viewerid FROM fz.hdr_base");
    unionView(c, "v_t4",
        "SELECT eventid AS eventid, requestheader AS request_header, tag AS tag_col FROM fz.pv_evolved",
        "SELECT eventid AS eventid, requestheader AS request_header, tag AS tag_col FROM fz.pv_base");
    unionView(c, "v_t4_meta", "SELECT id, info AS details FROM fz.meta_evolved",
        "SELECT id, info AS details FROM fz.meta_base");
    unionView(c, "v_t5", "SELECT * FROM fz.ev_a", "SELECT * FROM fz.ev_b", "SELECT * FROM fz.ev_c");
    unionView(c, "v_t5_four", "SELECT * FROM fz.ev_a", "SELECT * FROM fz.ev_b", "SELECT * FROM fz.ev_c",
        "SELECT * FROM fz.ev_b");
    unionView(c, "v_t5_bad", "SELECT * FROM fz.ev_a", "SELECT * FROM fz.ev_b", "SELECT * FROM fz.ev_c_bad");
    c.addView(DB, "v_ab", "SELECT * FROM fz.ev_a UNION ALL SELECT * FROM fz.ev_b", "id bigint",
        "hdr struct<memberid:int,checksum:binary>");
    unionView(c, "v_t5_nested", "SELECT * FROM fz.v_ab", "SELECT * FROM fz.ev_c");
    unionView(c, "v_t7_same", "SELECT * FROM fz.pv_base", "SELECT * FROM fz.pv_base");
    unionView(c, "v_t7_gengen", "SELECT * FROM fz.pv_evolved", "SELECT * FROM fz.pv_evolved2");
    unionView(c, "v_t7_reorder", "SELECT * FROM fz.pv_base", "SELECT * FROM fz.pv_reordered");
    unionView(c, "v_t7_reorder_r", "SELECT * FROM fz.pv_reordered", "SELECT * FROM fz.pv_base");
    unionView(c, "v_t7_lower", "SELECT * FROM fz.lc_base", "SELECT * FROM fz.lc_evolved");
    unionView(c, "v_t8", "SELECT * FROM fz.nest_base", "SELECT * FROM fz.nest_evolved");
    unionView(c, "v_t8r", "SELECT * FROM fz.nest_evolved", "SELECT * FROM fz.nest_base");
    unionView(c, "v_t9", "SELECT * FROM fz.nul_base", "SELECT * FROM fz.nul_evolved");
    unionView(c, "v_t9r", "SELECT * FROM fz.nul_evolved", "SELECT * FROM fz.nul_base");
    unionView(c, "v_t9n", "SELECT * FROM fz.nul_base", "SELECT * FROM fz.nul_evolved_nullable");
    unionView(c, "v_t9nr", "SELECT * FROM fz.nul_evolved_nullable", "SELECT * FROM fz.nul_base");
    unionView(c, "v_t10", "SELECT * FROM fz.def_base", "SELECT * FROM fz.def_evolved");
    unionView(c, "v_t10r", "SELECT * FROM fz.def_evolved", "SELECT * FROM fz.def_base");
    unionView(c, "v_t11", "SELECT * FROM fz.meta_base", "SELECT * FROM fz.meta_evolved");
    unionView(c, "v_t11r", "SELECT * FROM fz.meta_evolved", "SELECT * FROM fz.meta_base");
    unionView(c, "v_t12", "SELECT * FROM fz.lt_base", "SELECT * FROM fz.lt_evolved");
    unionView(c, "v_t12r", "SELECT * FROM fz.lt_evolved", "SELECT * FROM fz.lt_base");
    unionView(c, "v_neg_leaf", "SELECT * FROM fz.neg_base", "SELECT * FROM fz.neg_leaf");
    unionView(c, "v_neg_rec", "SELECT * FROM fz.neg_base", "SELECT * FROM fz.neg_rec");
    unionView(c, "v_neg_kind", "SELECT * FROM fz.negc_base", "SELECT * FROM fz.negc_evolved");
    unionView(c, "v_amb", "SELECT * FROM fz.amb_base", "SELECT * FROM fz.amb_dup");
    unionView(c, "v_amb_acc", "SELECT id, b FROM fz.amb_b", "SELECT id, s.foo AS b FROM fz.amb_acc");
    c.addView(DB, "v_amb_ord",
        "SELECT id, b FROM fz.amb_b UNION ALL SELECT id, fz_v_amb_ord_ReturnArg('x', s.foo) AS b FROM fz.amb_acc",
        functions("ReturnArg", RETURN_ARG_UDF), "id bigint", "b struct<x:int>");
    unionView(c, "v_depth", "SELECT id, b FROM fz.depth_direct", "SELECT id, s.child.x AS b FROM fz.depth_src");
    unionView(c, "v_depth_r", "SELECT id, s.child.x AS b FROM fz.depth_src", "SELECT id, b FROM fz.depth_direct");
    unionView(c, "v_depth2", "SELECT id, b FROM fz.depth2_direct", "SELECT id, s.child AS b FROM fz.depth2_src");
    unionView(c, "v_depth2_r", "SELECT id, s.child AS b FROM fz.depth2_src", "SELECT id, b FROM fz.depth2_direct");
    unionView(c, "v_d6", "SELECT * FROM fz.d6_noscale", "SELECT * FROM fz.d6_zero");
    unionView(c, "v_d6_r", "SELECT * FROM fz.d6_zero", "SELECT * FROM fz.d6_noscale");
    unionView(c, "v_d6_scale", "SELECT * FROM fz.d6_noscale", "SELECT * FROM fz.d6_scale2");
    unionView(c, "v_d6_prec", "SELECT * FROM fz.d6_noscale", "SELECT * FROM fz.d6_prec12");
    unionView(c, "v_fx_size", "SELECT * FROM fz.fx_a", "SELECT * FROM fz.fx_b");
    unionView(c, "v_fx_name", "SELECT * FROM fz.fx_a", "SELECT * FROM fz.fx_c");
    unionView(c, "v_fx_same", "SELECT * FROM fz.fx_a", "SELECT * FROM fz.fx_a");
    unionView(c, "v_fx_ns", "SELECT * FROM fz.fx_a", "SELECT * FROM fz.fx_a2");
    unionView(c, "v_addr", "SELECT * FROM fz.addr_base", "SELECT * FROM fz.addr_evolved");
    unionView(c, "v_un", "SELECT * FROM fz.un_base", "SELECT * FROM fz.un_evolved");
    unionView(c, "v_unr", "SELECT * FROM fz.unr_base", "SELECT * FROM fz.unr_evolved");
    unionView(c, "v_t19", "SELECT id, b FROM fz.acc_direct", "SELECT id, s.child AS b FROM fz.acc_nested");
    unionView(c, "v_t19r", "SELECT id, s.child AS b FROM fz.acc_nested", "SELECT id, b FROM fz.acc_direct");
    unionView(c, "v_t20", "SELECT id, b FROM fz.acc_direct", "SELECT id, s.child AS b FROM fz.acc_nested_nullable");
    unionView(c, "v_t20r", "SELECT id, s.child AS b FROM fz.acc_nested_nullable", "SELECT id, b FROM fz.acc_direct");
    String namedStruct = "named_struct('pageKey', pk, 'memberId', mid, 'extra', e)";
    unionView(c, "v_t21", "SELECT id, b FROM fz.acc_direct", "SELECT id, " + namedStruct + " AS b FROM fz.flat_src");
    unionView(c, "v_t21r", "SELECT id, " + namedStruct + " AS b FROM fz.flat_src", "SELECT id, b FROM fz.acc_direct");
    unionView(c, "v_t21_if", "SELECT id, b FROM fz.acc_direct", "SELECT id, IF(id > 0, " + namedStruct
        + ", named_struct('pageKey', e, 'memberId', mid, 'extra', pk)) AS b " + "FROM fz.flat_src");
    c.addView(DB, "v_t22",
        "SELECT id, b FROM fz.acc_direct UNION ALL SELECT id, fz_v_t22_MakeHeader(id) AS b " + "FROM fz.flat_src",
        functions("MakeHeader", MAKE_HEADER_UDF), "id bigint", "b struct<pagekey:string,memberid:int>");
    c.addView(DB, "v_t22r",
        "SELECT id, fz_v_t22r_MakeHeader(id) AS b FROM fz.flat_src UNION ALL SELECT id, b " + "FROM fz.acc_direct",
        functions("MakeHeader", MAKE_HEADER_UDF), "id bigint", "b struct<pagekey:string,memberid:int,extra:string>");
    c.addView(DB, "v_t22_ord",
        "SELECT id, b FROM fz.acc_direct UNION ALL "
            + "SELECT id, fz_v_t22_ord_ReturnArg('x', s.child) AS b FROM fz.acc_nested_nullable",
        functions("ReturnArg", RETURN_ARG_UDF), "id bigint", "b struct<pagekey:string,memberid:int>");
    c.addView(DB, "v_t22_ord_r",
        "SELECT id, fz_v_t22_ord_r_ReturnArg('x', s.child) AS b FROM fz.acc_nested_nullable "
            + "UNION ALL SELECT id, b FROM fz.acc_direct",
        functions("ReturnArg", RETURN_ARG_UDF), "id bigint", "b struct<pagekey:string,memberid:int,extra:string>");
    c.addView(DB, "inner_rename", "SELECT id, s.child AS hdr FROM fz.acc_nested", "id bigint",
        "hdr struct<pagekey:string,memberid:int>");
    unionView(c, "v_t23", "SELECT id, b FROM fz.acc_direct", "SELECT id, hdr AS b FROM fz.inner_rename");
    unionView(c, "v_t23r", "SELECT id, hdr AS b FROM fz.inner_rename", "SELECT id, b FROM fz.acc_direct");
    c.addView(DB, "v_t23_outer", "SELECT u.b.pagekey FROM (SELECT id, b FROM fz.acc_direct UNION ALL "
        + "SELECT id, s.child AS b FROM fz.acc_nested) u", "pagekey string");
    c.addView(DB, "v_plain_outer", "SELECT u.b.pagekey FROM (SELECT id, b FROM fz.acc_direct) u", "pagekey string");
    unionView(c, "v_t10c", "SELECT * FROM fz.cdef_base", "SELECT * FROM fz.cdef_evolved");
    unionView(c, "v_t10cr", "SELECT * FROM fz.cdef_evolved", "SELECT * FROM fz.cdef_base");
    unionView(c, "v_item_arr", "SELECT id, b FROM fz.acc_direct", "SELECT id, arr[0].child AS b FROM fz.itm_src");
    unionView(c, "v_item_arr_r", "SELECT id, arr[0].child AS b FROM fz.itm_src", "SELECT id, b FROM fz.acc_direct");
    unionView(c, "v_item_map_r", "SELECT id, m['k'].child AS b FROM fz.itm_src", "SELECT id, b FROM fz.acc_direct");
    unionView(c, "v_item_amb", "SELECT id, b FROM fz.amb_b", "SELECT id, arr[0].foo AS b FROM fz.itm_amb");
    c.addView(DB, "v_udf_access",
        "SELECT id, b FROM fz.acc_direct UNION ALL "
            + "SELECT id, fz_v_udf_access_MakeNested(id).child AS b FROM fz.flat_src",
        functions("MakeNested", MAKE_NESTED_UDF), "id bigint", "b struct<pagekey:string,memberid:int>");
    c.addView(DB, "v_udf_access_r",
        "SELECT id, fz_v_udf_access_r_MakeNested(id).child AS b FROM fz.flat_src "
            + "UNION ALL SELECT id, b FROM fz.acc_direct",
        functions("MakeNested", MAKE_NESTED_UDF), "id bigint", "b struct<pagekey:string,memberid:int,extra:string>");
    c.addView(DB, "v_udf_access_plain", "SELECT id, fz_v_udf_access_plain_MakeNested(id).child AS b FROM fz.flat_src",
        functions("MakeNested", MAKE_NESTED_UDF), "id bigint", "b struct<pagekey:string,memberid:int,extra:string>");
    c.addView(DB, "v_amb_plain", "SELECT id, s.foo AS b FROM fz.amb_acc", "id bigint", "b struct<x:int,y:int>");
    c.addView(DB, "v_cdef_plain", "SELECT * FROM fz.cdef_evolved", "id bigint", "c string");
    c.addView(DB, "v_cdef_base_plain", "SELECT * FROM fz.cdef_base", "id bigint", "c string");
    unionView(c, "v_f3rec", "SELECT * FROM fz.f3rec_a", "SELECT * FROM fz.f3rec_b");
    unionView(c, "v_f3rec_r", "SELECT * FROM fz.f3rec_b", "SELECT * FROM fz.f3rec_a");
    unionView(c, "v_f3ctl", "SELECT * FROM fz.f3ctl_a", "SELECT * FROM fz.f3ctl_b");
    unionView(c, "v_f3ctl_r", "SELECT * FROM fz.f3ctl_b", "SELECT * FROM fz.f3ctl_a");
    unionView(c, "v_f3col", "SELECT * FROM fz.f3col_a", "SELECT * FROM fz.f3col_b");
    unionView(c, "v_f3col_r", "SELECT * FROM fz.f3col_b", "SELECT * FROM fz.f3col_a");
    c.addView(DB, "alias_view", "SELECT * FROM fz.alias_src", "id bigint");
    c.addView(DB, "alias_qview", "SELECT * FROM fz.alias_qsrc", "id bigint");
    unionView(c, "v_alias_fz", "SELECT * FROM fz.alias_src", "SELECT * FROM fz.alias_src_evolved");
    unionView(c, "v_alias_fz_r", "SELECT * FROM fz.alias_src_evolved", "SELECT * FROM fz.alias_src");
    unionView(c, "v_alias_qfz", "SELECT * FROM fz.alias_qsrc", "SELECT * FROM fz.alias_qsrc_evolved");
    unionView(c, "v_alias_qfz_r", "SELECT * FROM fz.alias_qsrc_evolved", "SELECT * FROM fz.alias_qsrc");
    unionView(c, "v_f4", "SELECT * FROM fz.f4_a", "SELECT * FROM fz.f4_b");
    unionView(c, "v_f4_r", "SELECT * FROM fz.f4_b", "SELECT * FROM fz.f4_a");
    for (String name : new String[] { "f8", "f8e", "f8m", "f8n", "f8c", "f8env", "f8ns", "f8neg" }) {
      unionView(c, "v_" + name, "SELECT * FROM fz." + name + "_l", "SELECT * FROM fz." + name + "_r");
      unionView(c, "v_" + name + "_r", "SELECT * FROM fz." + name + "_r", "SELECT * FROM fz." + name + "_l");
    }
    unionView(c, "v_t24", "SELECT * FROM fz.case_camel", "SELECT * FROM fz.case_lower");
    unionView(c, "v_t24r", "SELECT * FROM fz.case_lower", "SELECT * FROM fz.case_camel");
    return c;
  }

  private static Map<String, String> functions(String name, String udfClass) {
    Map<String, String> properties = new HashMap<>();
    properties.put("functions", name + ":" + udfClass);
    properties.put("dependencies", "ivy://com.linkedin:udf:1.0");
    return properties;
  }

  /**
   * Registers a stored UNION ALL view. Its declared columns are irrelevant to fuzzy rewriting of a top-level view, so
   * a placeholder column is used.
   */
  private static void unionView(FuzzyUnionTestCatalog c, String name, String... branches) {
    c.addView(DB, name, String.join(" UNION ALL ", branches), "placeholder string");
  }

  /** The schema a hand-written contract resource denotes, printed by Avro itself for exact comparison. */
  static String expected(String resource) {
    return new Schema.Parser().parse(load(resource)).toString(true);
  }

  /** Asserts that {@code actual} is exactly the contract schema, including docs/aliases/props/defaults/order. */
  static void assertSchema(Schema actual, String resource) {
    assertReparses(actual);
    Assert.assertEquals(actual.toString(true), expected(resource));
  }

  /** Output must serialize and reparse with default validation (defaults and names) enabled. */
  static Schema assertReparses(Schema actual) {
    Schema reparsed = new Schema.Parser().parse(actual.toString());
    Assert.assertEquals(reparsed.toString(true), actual.toString(true));
    return reparsed;
  }

  /**
   * Returns the {@link GenericProjectFunction} calls found in each UNION leaf branch, in SQL order. Nested binary
   * unions are flattened, so {@code A UNION B UNION C} yields three entries.
   */
  static List<List<RexCall>> genericProjectsPerBranch(RelNode root) {
    List<List<RexCall>> result = new ArrayList<>();
    Union union = findFirstUnion(root);
    Assert.assertNotNull(union, "Expected a UNION in plan:\n" + org.apache.calcite.plan.RelOptUtil.toString(root));
    collectBranches(union, result);
    return result;
  }

  /** All GenericProjectFunction calls in a plan, wherever they appear. */
  static List<RexCall> allGenericProjects(RelNode node) {
    List<RexCall> calls = new ArrayList<>();
    collectGenericProjects(node, calls, false);
    return calls;
  }

  private static Union findFirstUnion(RelNode node) {
    if (node instanceof Union) {
      return (Union) node;
    }
    for (RelNode input : node.getInputs()) {
      Union union = findFirstUnion(input);
      if (union != null) {
        return union;
      }
    }
    return null;
  }

  private static void collectBranches(Union union, List<List<RexCall>> result) {
    for (RelNode input : union.getInputs()) {
      if (input instanceof Union) {
        collectBranches((Union) input, result);
      } else {
        List<RexCall> calls = new ArrayList<>();
        collectGenericProjects(input, calls, true);
        result.add(calls);
      }
    }
  }

  private static void collectGenericProjects(RelNode node, List<RexCall> calls, boolean stopAtUnion) {
    if (stopAtUnion && node instanceof Union) {
      return;
    }
    node.accept(new RexShuttle() {
      @Override
      public RexNode visitCall(RexCall call) {
        if (call.getOperator() instanceof GenericProjectFunction) {
          calls.add(call);
        }
        return super.visitCall(call);
      }
    });
    for (RelNode input : node.getInputs()) {
      collectGenericProjects(input, calls, stopAtUnion);
    }
  }

  /** Counts of GenericProjectFunction calls per branch, e.g. {@code [0, 1]}. */
  static List<Integer> projectionCounts(RelNode root) {
    List<Integer> counts = new ArrayList<>();
    for (List<RexCall> calls : genericProjectsPerBranch(root)) {
      counts.add(calls.size());
    }
    return counts;
  }

  static List<Integer> counts(Integer... counts) {
    List<Integer> result = new ArrayList<>();
    Collections.addAll(result, counts);
    return result;
  }
}

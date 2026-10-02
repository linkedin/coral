/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.List;
import java.util.Locale;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.schema.avro.FuzzyUnionAvroSchemaTests.assertFailsMentioning;
import static com.linkedin.coral.schema.avro.FuzzyUnionAvroSchemaTests.assertFailsMentioningIgnoringCase;
import static com.linkedin.coral.schema.avro.FuzzyUnionFixtures.*;


/**
 * Structural checks that natural view SQL cannot reach: an n-ary {@link LogicalUnion}, and internally typed
 * {@link GenericProjectFunction} calls that request missing fields or are malformed. Plans are built from real
 * converted table scans; only the node or call under test is constructed directly.
 */
public class FuzzyUnionStructuralTests {
  private FuzzyUnionTestCatalog catalog;
  private HiveToRelConverter hiveToRelConverter;
  private RelToAvroSchemaConverter relToAvroSchemaConverter;

  @BeforeClass
  public void beforeClass() {
    registerUdfs();
    catalog = buildCatalog();
    hiveToRelConverter = new HiveToRelConverter(catalog);
    relToAvroSchemaConverter = new RelToAvroSchemaConverter(catalog);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T6: a LogicalUnion with more than two inputs
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT6NaryUnionUsesThirdInputNullability() {
    LogicalUnion union =
        union("SELECT id, f FROM fz.req_a", "SELECT id, f FROM fz.req_b", "SELECT id, f FROM fz.req_c");
    Assert.assertEquals(union.getInputs().size(), 3);

    Schema actual = relToAvroSchemaConverter.convert(union, false, false);
    Assert.assertEquals(actual.getField("f").schema().toString(), "[\"null\",\"string\"]",
        "only the third input makes f nullable");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(actual.getField("f")));
  }

  @Test
  public void testT6BinaryUnionControl() {
    Schema actual = relToAvroSchemaConverter.convert(union("SELECT id, f FROM fz.req_a", "SELECT id, f FROM fz.req_b"),
        false, false);
    Assert.assertEquals(actual.getField("f").schema().toString(), "\"string\"");
  }

  @Test
  public void testT6NaryUnionChecksThirdInputCompatibility() {
    // All three inputs are BINARY to Calcite; only the third Avro fixed has a different size.
    LogicalUnion union = union("SELECT id, c FROM fz.fx_a", "SELECT id, c FROM fz.fx_a", "SELECT id, c FROM fz.fx_b");
    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(union, false, false), "Md5", "16", "8");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T13: projection requests a field the source does not have
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT13MissingRequestedFieldFails() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataType target = struct(scan, "pagekey", "nosuchfield");
    RelNode project = projectColumn(scan, 1, "requestheader", genericProject(scan, target, inputRef(scan, 1)));

    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(project, false, false), "nosuchfield");
  }

  @Test
  public void testT13DeclaredAvroAliasIsNotALookupKey() {
    // meta_evolved Info.firstField declares alias first_field; requesting first_field must not resolve to it.
    RelNode scan = scan("SELECT * FROM fz.meta_evolved");
    RelDataType target = struct(scan, "first_field");
    RelNode project = projectColumn(scan, 1, "info", genericProject(scan, target, inputRef(scan, 1)));

    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(project, false, false), "first_field");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T16: truly malformed internal calls, and a different operator printed as generic_project
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT16GenericProjectWithoutOperandsFails() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataType target = struct(scan, "pagekey", "path");
    RexNode call = rexBuilder(scan).makeCall(target, new GenericProjectFunction(target), ImmutableList.of());
    RelNode project = projectColumn(scan, 1, "requestheader", call);

    assertFailsClearly(() -> relToAvroSchemaConverter.convert(project, false, false));
  }

  @Test
  public void testT16GenericProjectWithNonStructReturnForStructOperandFails() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType bigint = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BIGINT), true);
    RelNode project = projectColumn(scan, 1, "requestheader", genericProject(scan, bigint, inputRef(scan, 1)));

    assertFailsMentioningIgnoringCase(() -> relToAvroSchemaConverter.convert(project, false, false), "requestheader");
  }

  @Test
  public void testT16DifferentOperatorNamedGenericProjectKeepsOrdinaryUdfInference() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataType target = struct(scan, "pagekey", "path");
    SqlOperator sameName = new SqlUserDefinedFunction(new SqlIdentifier("generic_project", SqlParserPos.ZERO),
        ReturnTypes.explicit(target), null, OperandTypes.VARIADIC, null, null);
    RexBuilder rexBuilder = rexBuilder(scan);
    RexNode call = rexBuilder.makeCall(target, sameName, ImmutableList.of(inputRef(scan, 1),
        rexBuilder.makeLiteral("requestheader"), rexBuilder.makeLiteral("struct<pagekey:string,path:string>")));
    RelNode project = projectColumn(scan, 1, "requestheader", call);

    Schema.Field field = relToAvroSchemaConverter.convert(project, false, false).getField("requestheader");
    Assert.assertNotNull(field, "ordinary UDF output keeps the suggested lowercase column name");
    Assert.assertEquals(field.schema().toString(true),
        new Schema.Parser().parse(("['null',{'type':'record','name':'requestheader','namespace':'rel_avro','fields':["
            + "{'name':'pagekey','type':['null','string'],'default':null},"
            + "{'name':'path','type':['null','string'],'default':null}]}]").replace('\'', '"')).toString(true));
    Assert.assertEquals(field.doc(),
        "Field created in view by applying UDF 'generic_project' with argument(s): "
            + "com.linkedin.events.PageViewEvent.requestHeader, \"requestheader\", "
            + "\"struct<pagekey:string,path:string>\"");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(field));
  }

  @Test
  public void testNestedGenericProjectionKeepsSourceMetadata() {
    // generic_project(generic_project($1, {pagekey, path}), {pagekey}): each level projects the source field.
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RexNode inner = genericProject(scan, struct(scan, "pagekey", "path"), inputRef(scan, 1));
    RexNode outer = genericProject(scan, struct(scan, "pagekey"), inner);
    RelNode project = projectColumn(scan, 1, "requestheader", outer);

    Schema.Field field = relToAvroSchemaConverter.convert(project, false, false).getField("requestHeader");
    Assert.assertNotNull(field, "projected field keeps the source spelling requestHeader");
    Assert.assertEquals(field.doc(), "The request header");
    Assert.assertEquals(field.schema().toString(true),
        new Schema.Parser().parse(("{'type':'record','name':'RequestHeader','namespace':'com.linkedin.events',"
            + "'doc':'Request header record','fields':[{'name':'pageKey','type':'string','doc':'Page key'}]}")
            .replace('\'', '"')).toString(true));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------------------------

  private LogicalUnion union(String... branchSql) {
    ImmutableList.Builder<RelNode> inputs = ImmutableList.builder();
    for (String sql : branchSql) {
      inputs.add(hiveToRelConverter.convertSql(sql));
    }
    return LogicalUnion.create(inputs.build(), true);
  }

  /** The table scan under a converted {@code SELECT *}. */
  private RelNode scan(String selectStar) {
    RelNode rel = hiveToRelConverter.convertSql(selectStar);
    return rel instanceof TableScan ? rel : rel.getInput(0);
  }

  private static RexBuilder rexBuilder(RelNode node) {
    return node.getCluster().getRexBuilder();
  }

  private static RexNode inputRef(RelNode input, int index) {
    return rexBuilder(input).makeInputRef(input, index);
  }

  /** A nullable struct of nullable VARCHAR fields, like the rewriter's fuzzy target types. */
  private static RelDataType struct(RelNode node, String... names) {
    RelDataTypeFactory typeFactory = rexBuilder(node).getTypeFactory();
    RelDataType varchar = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
    ImmutableList.Builder<RelDataType> types = ImmutableList.builder();
    for (int i = 0; i < names.length; i++) {
      types.add(varchar);
    }
    return typeFactory
        .createTypeWithNullability(typeFactory.createStructType(types.build(), ImmutableList.copyOf(names)), true);
  }

  /** An internally typed call shaped like the rewriter's: operand, column-name literal, Hive type string. */
  private static RexNode genericProject(RelNode node, RelDataType target, RexNode operand) {
    RexBuilder rexBuilder = rexBuilder(node);
    return rexBuilder.makeCall(target, new GenericProjectFunction(target),
        ImmutableList.of(operand, rexBuilder.makeLiteral("col"), rexBuilder.makeLiteral("struct<...>")));
  }

  /** Projects every input column unchanged except {@code index}, which becomes {@code replacement}. */
  private static RelNode projectColumn(RelNode input, int index, String name, RexNode replacement) {
    ImmutableList.Builder<RexNode> exprs = ImmutableList.builder();
    ImmutableList.Builder<String> names = ImmutableList.builder();
    List<String> inputNames = input.getRowType().getFieldNames();
    for (int i = 0; i < inputNames.size(); i++) {
      exprs.add(i == index ? replacement : inputRef(input, i));
      names.add(i == index ? name : inputNames.get(i));
    }
    return LogicalProject.create(input, exprs.build(), names.build());
  }

  /** Must fail with an exception that names the generic projection, not an incidental index/null error. */
  private static void assertFailsClearly(Runnable conversion) {
    try {
      conversion.run();
    } catch (IndexOutOfBoundsException | NullPointerException e) {
      Assert.fail("Malformed call must be rejected explicitly, not by " + e, e);
    } catch (RuntimeException e) {
      String message = String.valueOf(e.getMessage()).toLowerCase(Locale.ROOT);
      Assert.assertTrue(message.contains("generic_project") || message.contains("genericproject"),
          "Expected the error to name the generic projection but was: " + e.getMessage());
      return;
    }
    Assert.fail("Expected malformed generic_project call to be rejected");
  }
}

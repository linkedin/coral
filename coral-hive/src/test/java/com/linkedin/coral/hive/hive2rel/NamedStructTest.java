package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.testng.Assert.*;


public class NamedStructTest {

  @BeforeClass
  public static void beforeClass() throws HiveException, MetaException, IOException {
    ToRelConverter.setup();
  }

  @Test
  public void testMixedTypes() {
    final String sql = "SELECT named_struct('abc', 123, 'def', 'xyz')";
    RelNode rel = toRel(sql);
    final String generated = relToStr(rel);
    final String expected = "LogicalProject(EXPR$0=[CAST(ROW(123, 'xyz')):RecordType(INTEGER NOT NULL abc,"
        + " CHAR(3) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL def) NOT NULL])\n" +
        "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testNullFieldValue() {
    final String sql = "SELECT named_struct('abc', cast(NULL as int), 'def', 150)";
    final String generated = sqlToRelStr(sql);
    final String expected = "LogicalProject(EXPR$0=[CAST(ROW(CAST(null):INTEGER, 150)):RecordType(INTEGER abc, INTEGER NOT NULL def) NOT NULL])\n" +
         "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testAllNullValues() {
    final String sql = "SELECT named_struct('abc', cast(NULL as int), 'def', cast(NULL as double))";
    final String generated = sqlToRelStr(sql);
    final String expected = "LogicalProject(EXPR$0=[CAST(ROW(CAST(null):INTEGER, CAST(null):DOUBLE)):RecordType(INTEGER abc, DOUBLE def) NOT NULL])\n" +
        "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testNestedComplexTypes() {
    final String sql = "SELECT named_struct('arr', array(10, 15), 's', named_struct('f1', 123, 'f2', array(20.5)))";
    final String generated = sqlToRelStr(sql);
    final String expected = "LogicalProject(EXPR$0=[CAST(ROW(ARRAY(10, 15), CAST(ROW(123, ARRAY(20.5))):"
        + "RecordType(INTEGER NOT NULL f1, DECIMAL(3, 1) NOT NULL ARRAY NOT NULL f2) NOT NULL)):"
        + "RecordType(INTEGER NOT NULL ARRAY NOT NULL arr, RecordType(INTEGER NOT NULL f1, DECIMAL(3, 1) NOT NULL ARRAY NOT NULL f2) NOT NULL s) NOT NULL])\n"
        + "  LogicalValues(tuples=[[{ 0 }]])\n";
    // verified by human that expected string is correct and retained here to protect from future changes
    assertEquals(generated, expected);
  }

  @Test(expectedExceptions = CalciteContextException.class,
      expectedExceptionsMessageRegExp = ".*Type 'INTEGER' is not supported")
  public void testBadFieldName() {
    final String sql = "SELECT named_struct(123, 18, 'def', 56)";
    sqlToRelStr(sql);
  }

  @Test(expectedExceptions = CalciteContextException.class,
      expectedExceptionsMessageRegExp = ".*Wrong number of arguments.*")
  public void testBadArgumentList() {
    final String sql = "SELECT named_struct('abc', 123, 'def')";
    sqlToRelStr(sql);
  }

  // This is a valid hive query but we disallow this for now due to
  // various analysis issues
  @Test(expectedExceptions = CalciteContextException.class,
      expectedExceptionsMessageRegExp = ".*Wrong number of arguments.*")
  public void testEmptyArgumentList() {
    final String sql = "SELECT named_struct()";
    sqlToRelStr(sql);
  }
}

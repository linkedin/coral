package com.linkedin.coral.spark.dialect;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlUnnestOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlMultisetValueConstructor;

/**
 * This class represents the Spark SQL Dialect.
 *
 * It overrides Hive Dialect with following behavior
 *    - Disables character set names
 *    - converts ARRAY[] to ARRAY() or MAP[] to MAP()
 *    - converts UNNEST to EXPLODE
 *
 * This is the Final Step in translation pipeline.
 * There should not be any AST manipulation logic here, just Spark translation logic.
 */
public class SparkSqlDialect extends SqlDialect {

  public static final SparkSqlDialect INSTANCE = new SparkSqlDialect();

  private SparkSqlDialect() {
    super(DatabaseProduct.HIVE, DatabaseProduct.HIVE.name(), null, NullCollation.HIGH, HiveHandler.INSTANCE);
  }

  public static class HiveHandler extends SqlDialect.BaseHandler {
    private static final HiveHandler INSTANCE = new HiveHandler();

    /**
     * Overrides unparse call:
     *      All SqlCall translations will go through here.
     *
     * We specifically target two
     *    - UNNEST
     *    - MAP or ARRAY
     * */
    @Override
    public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
      if (call.getOperator() instanceof SqlMultisetValueConstructor) {
        unparseMapOrArray(writer, call, leftPrec, rightPrec);
      } else if (call.getOperator() instanceof SqlUnnestOperator) {
        unparseUnnest(writer, call, leftPrec, rightPrec);
      } else {
        super.unparseCall(writer, call, leftPrec, rightPrec);
      }
    }

    /**
     *  Converts UNNEST(...) to EXPLODE(...)
     *
     *  Code referred from SqlFunctionalOperator.java
     * */
    private void unparseUnnest(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
      writer.keyword("EXPLODE");
      final SqlWriter.Frame frame =
          writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "(", ")");
      for (SqlNode operand : call.getOperandList()) {
        writer.sep(",");
        operand.unparse(writer, 0, 0);
      }
      writer.endList(frame);
    }

    /**
     *  Converts ARRAY[] to ARRAY()
     *  Converts MAP[] to MAP()
     * */
    private void unparseMapOrArray(
        SqlWriter writer,
        SqlCall call,
        int leftPrec,
        int rightPrec) {
      writer.keyword(call.getOperator().getName()); // "MULTISET" or "ARRAY"
      final SqlWriter.Frame frame = writer.startList("(", ")");
      for (SqlNode operand : call.getOperandList()) {
        writer.sep(",");
        operand.unparse(writer, leftPrec, rightPrec);
      }
      writer.endList(frame);
    }
  }

  /**
   *  Disables character set.
   *
   *  Otherwise data types are accompanied with character sets
   *
   *  For ex:
   *  @code VARCHAR(30) CHARACTER SET `ISO-8859-1`
   *
   * */
  @Override
  public boolean supportsCharSet() {
    return false;
  }

}

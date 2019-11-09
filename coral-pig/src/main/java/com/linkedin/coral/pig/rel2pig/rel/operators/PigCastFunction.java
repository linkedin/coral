package com.linkedin.coral.pig.rel2pig.rel.operators;

import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.pig.rel2pig.exceptions.IllegalPigCastException;
import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedPigTypeException;
import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;
import java.util.List;
import java.util.Map;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * PigCastFunction translates SqlCastFunctions to Pig Latin.
 */
public class PigCastFunction extends PigOperator {
  public PigCastFunction(RexCall rexCall, List<String> inputFieldNames) {
    super(rexCall, inputFieldNames);
  }

  // SQL_TO_PIG_TYPE_MAP maps a SqlTypeName to its equivalent PigType.
  // The full list of supported data types in Pig can be found here:
  //   https://pig.apache.org/docs/r0.15.0/basic.html#data-types
  private static final Map<SqlTypeName, PigType> SQL_TO_PIG_TYPE_MAP =
      ImmutableMap.<SqlTypeName, PigType>builder()
          .put(SqlTypeName.BOOLEAN, PigType.BOOLEAN)
          .put(SqlTypeName.TINYINT, PigType.INT)
          .put(SqlTypeName.SMALLINT, PigType.INT)
          .put(SqlTypeName.INTEGER, PigType.INT)
          .put(SqlTypeName.BIGINT, PigType.LONG)
          .put(SqlTypeName.DECIMAL, PigType.DOUBLE)
          .put(SqlTypeName.DOUBLE, PigType.DOUBLE)
          .put(SqlTypeName.FLOAT, PigType.FLOAT)
          .put(SqlTypeName.REAL, PigType.FLOAT)
          .put(SqlTypeName.DATE, PigType.DATETIME)
          .put(SqlTypeName.TIME, PigType.DATETIME)
          .put(SqlTypeName.CHAR, PigType.CHARARRAY)
          .put(SqlTypeName.VARCHAR, PigType.CHARARRAY)
          .put(SqlTypeName.BINARY, PigType.BYTEARRAY)
          .put(SqlTypeName.VARBINARY, PigType.BYTEARRAY)
          .put(SqlTypeName.ANY, PigType.BYTEARRAY)
          .put(SqlTypeName.ARRAY, PigType.BAG)
          .put(SqlTypeName.MAP, PigType.MAP)
          .put(SqlTypeName.ROW, PigType.TUPLE)
          .build();

  // PIG_TYPE_CAST_MAP has an entry if and only if the PigType key can be casted to the PigType value.
  // The full matrix of CAST semantics for Pig can be found here:
  //   https://pig.apache.org/docs/r0.15.0/basic.html#cast
  private static final ImmutableMultimap<PigType, PigType> PIG_TYPE_CAST_MAP =
      ImmutableMultimap.<PigType, PigType>builder()
          // BAG
          .put(PigType.BAG, PigType.BYTEARRAY)
          .put(PigType.BAG, PigType.BAG)
          // TUPLE
          .put(PigType.TUPLE, PigType.BYTEARRAY)
          .put(PigType.TUPLE, PigType.TUPLE)
          // MAP
          .put(PigType.MAP, PigType.BYTEARRAY)
          .put(PigType.MAP, PigType.MAP)
          // INT
          .put(PigType.INT, PigType.INT)
          .put(PigType.INT, PigType.LONG)
          .put(PigType.INT, PigType.FLOAT)
          .put(PigType.INT, PigType.DOUBLE)
          .put(PigType.INT, PigType.CHARARRAY)
          .put(PigType.INT, PigType.BYTEARRAY)
          // LONG
          .put(PigType.LONG, PigType.INT)
          .put(PigType.LONG, PigType.LONG)
          .put(PigType.LONG, PigType.FLOAT)
          .put(PigType.LONG, PigType.DOUBLE)
          .put(PigType.LONG, PigType.CHARARRAY)
          .put(PigType.LONG, PigType.BYTEARRAY)
          // FLOAT
          .put(PigType.FLOAT, PigType.INT)
          .put(PigType.FLOAT, PigType.LONG)
          .put(PigType.FLOAT, PigType.FLOAT)
          .put(PigType.FLOAT, PigType.DOUBLE)
          .put(PigType.FLOAT, PigType.CHARARRAY)
          .put(PigType.FLOAT, PigType.BYTEARRAY)
          // DOUBLE
          .put(PigType.DOUBLE, PigType.INT)
          .put(PigType.DOUBLE, PigType.LONG)
          .put(PigType.DOUBLE, PigType.FLOAT)
          .put(PigType.DOUBLE, PigType.DOUBLE)
          .put(PigType.DOUBLE, PigType.CHARARRAY)
          .put(PigType.DOUBLE, PigType.BYTEARRAY)
          // CHARARRAY
          .put(PigType.CHARARRAY, PigType.INT)
          .put(PigType.CHARARRAY, PigType.LONG)
          .put(PigType.CHARARRAY, PigType.FLOAT)
          .put(PigType.CHARARRAY, PigType.DOUBLE)
          .put(PigType.CHARARRAY, PigType.CHARARRAY)
          .put(PigType.CHARARRAY, PigType.BYTEARRAY)
          .put(PigType.CHARARRAY, PigType.BOOLEAN)
          // BYTEARRAY
          .put(PigType.BYTEARRAY, PigType.BYTEARRAY)
          // BOOLEAN
          .put(PigType.BOOLEAN, PigType.CHARARRAY)
          .put(PigType.BOOLEAN, PigType.BYTEARRAY)
          .put(PigType.BOOLEAN, PigType.BOOLEAN)
          // DATETIME
          .put(PigType.DATETIME, PigType.DATETIME)
          // BIGINTEGER
          .put(PigType.BIGINTEGER, PigType.BIGINTEGER)
          // BIGDECIMAL
          .put(PigType.BIGDECIMAL, PigType.BIGDECIMAL)
          .build();

  @Override
  public String unparse() {
    final RexNode castNode = rexCall.getOperands().get(0);
    final PigType castFromType = getPigType(castNode.getType().getSqlTypeName());
    final PigType castToType = getPigType(rexCall.getType().getSqlTypeName());
    if (!PIG_TYPE_CAST_MAP.containsEntry(castFromType, castToType)) {
      throw new IllegalPigCastException(castFromType, castToType);
    }

    final String castField = PigRexUtils.convertRexNodeToPigExpression(castNode, inputFieldNames);

    return String.format("(%s)%s", castToType.getName(), castField);
  }

  /**
   * Returns the equivalent PigType for a given SqlTypeName.
   * If the SqlTypeName cannot be tranlsated to Pig Latin, throw an UnsupportedPigTypeException.
   *
   * @param sqlTypeName The SqlTypeName to be translated.
   * @return Equivalent PigType for the given sqlTypeName.
   */
  private PigType getPigType(SqlTypeName sqlTypeName) {
    if (!SQL_TO_PIG_TYPE_MAP.containsKey(sqlTypeName)) {
      throw new UnsupportedPigTypeException(sqlTypeName);
    }

    return SQL_TO_PIG_TYPE_MAP.get(sqlTypeName);
  }

  /**
   * PigType represents a datatype in the Pig engine.
   */
  public enum PigType {

    BAG("bag"),
    TUPLE("tuple"),
    MAP("map"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    CHARARRAY("chararray"),
    BYTEARRAY("bytearray"),
    BOOLEAN("boolean"),
    DATETIME("datetime"),
    BIGINTEGER("biginteger"),
    BIGDECIMAL("bigdecimal");

    private final String name;

    PigType(String name) {
      this.name = name;
    }

    /**
     * Returns the name of the PigType.
     */
    public String getName() {
      return name;
    }

  }

}

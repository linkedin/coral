package com.linkedin.coral.pig.rel2pig.exceptions;

import org.apache.calcite.sql.type.SqlTypeName;


/**
 * UnsupportedPigTypeException indicates that a SQL/Calcite type does not have an equivalent type in the Pig engine.
 *
 * For example:
 *   In SQL, there exists types that are not supported in Pig such as:
 *     - INTERVAL
 *     - TIMESTAMP
 *     - ANY
 *
 *   Since there is no equivalent type in Pig, an UnsupportedPigTypeException is thrown.
 *
 * The full list of supported data types in Pig can be found here:
 *   https://pig.apache.org/docs/r0.15.0/basic.html#data-types
 */
public class UnsupportedPigTypeException extends RuntimeException {

  private static final String UNSUPPORTED_PIG_TYPE_TEMPLATE =
      "SQL/Calcite type '%s' is not supported in Pig Latin.";

  public UnsupportedPigTypeException(SqlTypeName sqlTypeName) {
    super(String.format(UNSUPPORTED_PIG_TYPE_TEMPLATE, sqlTypeName.getName()));
  }

}

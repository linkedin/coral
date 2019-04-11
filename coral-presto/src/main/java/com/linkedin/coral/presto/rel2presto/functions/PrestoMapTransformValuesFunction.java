package com.linkedin.coral.presto.rel2presto.functions;

import org.apache.calcite.rel.type.RelDataType;

/**
 * PrestoMapTransformValuesFunctionrepresents the Presto built-in UDF, transform_values, defined to take a map input
 * and a lambda function to determine the new values of the map
 *
 * This UDF requires a special definition outside the CalcitePrestoUDFMap because of the following:
 *   - the return type of the map transform_values function is dynamic based on the input
 *   - the lambda syntax is not easily parseable by Calcite
 *
 * Instead, we represent the input to this UDF as a string and we set its return type is passed as a parameter
 * on creation.
 */
class PrestoMapTransformValuesFunction extends GenericTemplateFunction {
  public PrestoMapTransformValuesFunction(RelDataType transformValuesDataType) {
    super(transformValuesDataType, "transform_values");
  }
}

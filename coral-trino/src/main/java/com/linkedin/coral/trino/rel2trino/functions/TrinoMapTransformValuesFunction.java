/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import org.apache.calcite.rel.type.RelDataType;


/**
 * TrinoMapTransformValuesFunction represents the Trino built-in UDF, transform_values, defined to take a map input
 * and a lambda function to determine the new values of the map
 *
 * This UDF requires a special definition outside the CalciteTrinoUDFMap because of the following:
 *   - the return type of the map transform_values function is dynamic based on the input
 *   - the lambda syntax is not easily parseable by Calcite
 *
 * Instead, we represent the input to this UDF as a string and we set its return type is passed as a parameter
 * on creation.
 */
class TrinoMapTransformValuesFunction extends GenericTemplateFunction {
  public TrinoMapTransformValuesFunction(RelDataType transformValuesDataType) {
    super(transformValuesDataType, "transform_values");
  }
}

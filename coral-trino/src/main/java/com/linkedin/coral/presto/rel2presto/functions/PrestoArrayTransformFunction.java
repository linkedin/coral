/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.rel2presto.functions;

import org.apache.calcite.rel.type.RelDataType;


/**
 * PrestoArrayTransformFunction represents the Presto built-in UDF, transform, defined to take an array input and
 * a lambda function to apply over the array.
 *
 * This UDF requires a special definition outside the CalcitePrestoUDFMap because of the following:
 *   - the return type of the array transform function is dynamic based on the input
 *   - the lambda syntax is not easily parseable by Calcite
 *
 * Instead, we represent the input to this UDF as a string and we set its return type is passed as a parameter
 * on creation.
 */
class PrestoArrayTransformFunction extends GenericTemplateFunction {
  public PrestoArrayTransformFunction(RelDataType transformDataType) {
    super(transformDataType, "transform");
  }
}

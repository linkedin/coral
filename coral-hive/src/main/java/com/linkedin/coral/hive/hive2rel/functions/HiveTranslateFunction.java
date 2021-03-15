/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.ReturnTypes;

import static org.apache.calcite.sql.type.OperandTypes.STRING_STRING_STRING;

public class HiveTranslateFunction extends SqlFunction {
    public static HiveTranslateFunction TRANSLATE = new HiveTranslateFunction();

    HiveTranslateFunction() {
        super("TRANSLATE",
                SqlKind.OTHER_FUNCTION,
                ReturnTypes.ARG0_NULLABLE_VARYING,
                null,
                STRING_STRING_STRING,
                SqlFunctionCategory.STRING);
    }
}

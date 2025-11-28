package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * Domain transformer for CAST operations.
 * Supports casts between numeric types, string types, and date types.
 */
public class CastTransformer implements DomainTransformer {

    @Override
    public boolean canHandle(RexNode expr) {
        if (!(expr instanceof RexCall)) {
            return false;
        }
        RexCall call = (RexCall) expr;
        return call.getKind() == SqlKind.CAST;
    }

    @Override
    public boolean isVariableOperandPositionValid(RexNode expr) {
        RexCall call = (RexCall) expr;
        RexNode operand = call.getOperands().get(0);
        return operand instanceof RexInputRef || operand instanceof RexCall;
    }

    @Override
    public RexNode getChildForVariable(RexNode expr) {
        RexCall call = (RexCall) expr;
        return call.getOperands().get(0);
    }

    @Override
    public Domain deriveChildDomain(RexNode expr, Domain outputDomain) {
        RexCall call = (RexCall) expr;
        RelDataType targetType = call.getType();
        RexNode operand = call.getOperands().get(0);
        RelDataType sourceType = operand.getType();

        SqlTypeName targetTypeName = targetType.getSqlTypeName();
        SqlTypeName sourceTypeName = sourceType.getSqlTypeName();

        return new Domain() {
            @Override
            public String sample() {
                String outputValue = outputDomain.sample();
                return convertType(outputValue, targetTypeName, sourceTypeName);
            }

            @Override
            public boolean contains(String value) {
                if (value == null) {
                    return false;
                }
                String converted = convertType(value, sourceTypeName, targetTypeName);
                return converted.equals(outputDomain.sample());
            }
        };
    }

    /**
     * Converts a value from source type to target type.
     */
    private String convertType(String value, SqlTypeName sourceType, SqlTypeName targetType) {
        if (value == null) {
            return null;
        }

        // If types are the same, no conversion needed
        if (sourceType == targetType) {
            return value;
        }

        // Converting TO string (source is non-string, target is string)
        if (isStringType(targetType) && !isStringType(sourceType)) {
            if (isNumericType(sourceType)) {
                // Numeric to String - just return as-is (already string representation)
                return value;
            } else if (isDateType(sourceType)) {
                // Date to String - return as-is
                return value;
            }
        }

        // Converting FROM string (source is string, target is non-string)
        if (isStringType(sourceType) && !isStringType(targetType)) {
            if (isNumericType(targetType)) {
                // String to Numeric - parse and return
                try {
                    if (isIntegerType(targetType)) {
                        return String.valueOf(Long.parseLong(value.trim()));
                    } else if (isFloatingPointType(targetType)) {
                        return String.valueOf(Double.parseDouble(value.trim()));
                    } else {
                        return value;
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot convert '" + value + "' to " + targetType);
                }
            } else if (isDateType(targetType)) {
                // String to Date - return as-is (assume valid date format)
                return value;
            }
        }

        // Numeric type conversions
        if (isNumericType(sourceType) && isNumericType(targetType)) {
            try {
                if (isIntegerType(targetType)) {
                    return String.valueOf((long) Double.parseDouble(value));
                } else {
                    return String.valueOf(Double.parseDouble(value));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert '" + value + "' to " + targetType);
            }
        }

        // Default: return value as-is
        return value;
    }

    private boolean isStringType(SqlTypeName type) {
        return type == SqlTypeName.CHAR
            || type == SqlTypeName.VARCHAR;
    }

    private boolean isNumericType(SqlTypeName type) {
        return isIntegerType(type) || isFloatingPointType(type);
    }

    private boolean isIntegerType(SqlTypeName type) {
        return type == SqlTypeName.TINYINT
            || type == SqlTypeName.SMALLINT
            || type == SqlTypeName.INTEGER
            || type == SqlTypeName.BIGINT;
    }

    private boolean isFloatingPointType(SqlTypeName type) {
        return type == SqlTypeName.FLOAT
            || type == SqlTypeName.REAL
            || type == SqlTypeName.DOUBLE
            || type == SqlTypeName.DECIMAL;
    }

    private boolean isDateType(SqlTypeName type) {
        return type == SqlTypeName.DATE
            || type == SqlTypeName.TIME
            || type == SqlTypeName.TIMESTAMP
            || type == SqlTypeName.TIMESTAMP_WITH_LOCAL_TIME_ZONE;
    }
}

package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedAggFunction;
import org.apache.calcite.util.Optionality;

import java.util.List;

public class HiveSqlUserDefinedAggFunction extends SqlUserDefinedAggFunction {

    private final List<RelDataType> paramTypes;

    public HiveSqlUserDefinedAggFunction(
            SqlIdentifier opName,
            SqlReturnTypeInference returnTypeInference,
            SqlOperandTypeInference operandTypeInference,
            SqlOperandTypeChecker operandTypeChecker,
            boolean requiresOrder, boolean requiresOver,
            Optionality requiresGroupOrder,
            List<RelDataType> paramTypes) {
        super(opName, returnTypeInference, operandTypeInference, operandTypeChecker, null, requiresOrder, requiresOver, requiresGroupOrder, null);
        this.paramTypes = paramTypes;
    }

    @Override
    public List<RelDataType> getParamTypes() {
        return paramTypes;
    }
}

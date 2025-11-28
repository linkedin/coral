package com.linkedin.coral.datagen.domain;

import java.util.List;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;

public class DomainInferenceProgram {
    private final List<DomainTransformer> transformers;

    public DomainInferenceProgram(List<DomainTransformer> transformers) {
        this.transformers = transformers;
    }

    public Domain derive(RexNode expr, Domain outputDomain) {
        if (expr instanceof RexInputRef) {
            return outputDomain;
        }

        for (DomainTransformer t : transformers) {
            if (t.canHandle(expr) && t.isVariableOperandPositionValid(expr)) {
                RexNode child = t.getChildForVariable(expr);
                Domain childOutputDomain = t.deriveChildDomain(expr, outputDomain);
                return derive(child, childOutputDomain);
            }
        }

        throw new IllegalStateException("No applicable transformer for expression: " + expr);
    }
}

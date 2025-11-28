package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rex.RexNode;

public interface DomainTransformer {
    boolean canHandle(RexNode expr);
    boolean isVariableOperandPositionValid(RexNode expr);
    RexNode getChildForVariable(RexNode expr);
    Domain deriveChildDomain(RexNode expr, Domain outputDomain);
}

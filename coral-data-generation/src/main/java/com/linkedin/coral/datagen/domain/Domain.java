package com.linkedin.coral.datagen.domain;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;

public interface Domain {
    String sample();
    boolean contains(String value);
}

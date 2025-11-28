package com.linkedin.coral.datagen.domain;

import java.util.Objects;

public class ConstantDomain implements Domain {
    private final String constant;

    public ConstantDomain(String constant) {
        this.constant = Objects.requireNonNull(constant);
    }

    @Override
    public String sample() {
        return constant;
    }

    @Override
    public boolean contains(String value) {
        return constant.equals(value);
    }
}

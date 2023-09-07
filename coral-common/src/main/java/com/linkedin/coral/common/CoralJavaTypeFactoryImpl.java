package com.linkedin.coral.common;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.StructKind;

import java.util.List;

public class CoralJavaTypeFactoryImpl extends JavaTypeFactoryImpl {
    public CoralJavaTypeFactoryImpl(RelDataTypeSystem typeSystem) {
        super(typeSystem);
    }

    @Override
    public RelDataType createStructType(
            final List<RelDataType> typeList,
            final List<String> fieldNameList) {
        return createStructType(StructKind.PEEK_FIELDS, typeList, fieldNameList);
    }
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.linkedin.coral.hive.hive2rel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.UnionTypeInfo;


/**
 * Copied from org.apache.hadoop.hive.ql.optimizer.calcite.translator.TypeConverter
 */

public class TypeConverter {

  private TypeConverter() {

  }

  public static RelDataType convert(TypeInfo typeInfo, RelDataTypeFactory relTypeFactory) {
    switch (typeInfo.getCategory()) {
      case PRIMITIVE:
        return convert((PrimitiveTypeInfo) typeInfo, relTypeFactory);
      case LIST:
        return convert((ListTypeInfo) typeInfo, relTypeFactory);
      case MAP:
        return convert((MapTypeInfo) typeInfo, relTypeFactory);
      case STRUCT:
        return convert((StructTypeInfo) typeInfo, relTypeFactory);
      case UNION:
        return convert((UnionTypeInfo) typeInfo, relTypeFactory);
      default:
        throw new RuntimeException("Unknown type category: " + typeInfo.getCategory());
    }
  }

  public static RelDataType convert(PrimitiveTypeInfo type, RelDataTypeFactory dtFactory) {
    RelDataType convertedType = null;

    switch (type.getPrimitiveCategory()) {
      case VOID:
        convertedType = dtFactory.createSqlType(SqlTypeName.NULL);
        break;
      case BOOLEAN:
        convertedType = dtFactory.createSqlType(SqlTypeName.BOOLEAN);
        break;
      case BYTE:
        convertedType = dtFactory.createSqlType(SqlTypeName.TINYINT);
        break;
      case SHORT:
        convertedType = dtFactory.createSqlType(SqlTypeName.SMALLINT);
        break;
      case INT:
        convertedType = dtFactory.createSqlType(SqlTypeName.INTEGER);
        break;
      case LONG:
        convertedType = dtFactory.createSqlType(SqlTypeName.BIGINT);
        break;
      case FLOAT:
        convertedType = dtFactory.createSqlType(SqlTypeName.FLOAT);
        break;
      case DOUBLE:
        convertedType = dtFactory.createSqlType(SqlTypeName.DOUBLE);
        break;
      case STRING:
        convertedType = dtFactory.createSqlType(SqlTypeName.VARCHAR, Integer.MAX_VALUE);
        break;
      case DATE:
        convertedType = dtFactory.createSqlType(SqlTypeName.DATE);
        break;
      case TIMESTAMP:
        convertedType = dtFactory.createSqlType(SqlTypeName.TIMESTAMP);
        break;
      case BINARY:
        convertedType = dtFactory.createSqlType(SqlTypeName.BINARY);
        break;
      case DECIMAL:
        DecimalTypeInfo dtInf = (DecimalTypeInfo) type;
        convertedType = dtFactory.createSqlType(SqlTypeName.DECIMAL, dtInf.precision(), dtInf.scale());
        break;
      case VARCHAR:
        convertedType = dtFactory.createSqlType(SqlTypeName.VARCHAR, ((BaseCharTypeInfo) type).getLength());
        break;
      case CHAR:
        convertedType = dtFactory.createSqlType(SqlTypeName.CHAR, ((BaseCharTypeInfo) type).getLength());
        break;
      case UNKNOWN:
        convertedType = dtFactory.createSqlType(SqlTypeName.OTHER);
        break;
      default:
        throw new RuntimeException("Unknown primitive type category: " + type.getPrimitiveCategory());
    }

    if (null == convertedType) {
      throw new RuntimeException("Unsupported Type : " + type.getTypeName());
    }

    return dtFactory.createTypeWithNullability(convertedType, true);
  }

  public static RelDataType convert(ListTypeInfo lstType, RelDataTypeFactory dtFactory) {
    RelDataType elemType = convert(lstType.getListElementTypeInfo(), dtFactory);
    return dtFactory.createArrayType(elemType, -1);
  }

  public static RelDataType convert(MapTypeInfo mapType, RelDataTypeFactory dtFactory) {
    RelDataType keyType = convert(mapType.getMapKeyTypeInfo(), dtFactory);
    RelDataType valueType = convert(mapType.getMapValueTypeInfo(), dtFactory);
    return dtFactory.createMapType(keyType, valueType);
  }

  public static RelDataType convert(StructTypeInfo structType, final RelDataTypeFactory dtFactory) {
    List<RelDataType> fTypes = new ArrayList<RelDataType>(structType.getAllStructFieldTypeInfos().size());
    for (TypeInfo ti : structType.getAllStructFieldTypeInfos()) {
      fTypes.add(convert(ti, dtFactory));
    }
    return dtFactory.createStructType(fTypes, structType.getAllStructFieldNames());
  }

  public static RelDataType convert(UnionTypeInfo unionType, RelDataTypeFactory dtFactory) {
    // Union type is not supported in Calcite.
    throw new RuntimeException("Union type is not supported");
  }

  public static TypeInfo convert(RelDataType rType) {
    if (rType.isStruct()) {
      return convertStructType(rType);
    } else if (rType.getComponentType() != null) {
      return convertListType(rType);
    } else if (rType.getKeyType() != null) {
      return convertMapType(rType);
    } else {
      return convertPrimtiveType(rType);
    }
  }

  public static TypeInfo convertStructType(RelDataType rType) {
    List<TypeInfo> fTypes = Lists.transform(rType.getFieldList(), new Function<RelDataTypeField, TypeInfo>() {
      @Override
      public TypeInfo apply(RelDataTypeField f) {
        return convert(f.getType());
      }
    });
    List<String> fNames = Lists.transform(rType.getFieldList(), new Function<RelDataTypeField, String>() {
      @Override
      public String apply(RelDataTypeField f) {
        return f.getName();
      }
    });
    return TypeInfoFactory.getStructTypeInfo(fNames, fTypes);
  }

  public static TypeInfo convertMapType(RelDataType rType) {
    return TypeInfoFactory.getMapTypeInfo(convert(rType.getKeyType()), convert(rType.getValueType()));
  }

  public static TypeInfo convertListType(RelDataType rType) {
    return TypeInfoFactory.getListTypeInfo(convert(rType.getComponentType()));
  }

  public static TypeInfo convertPrimtiveType(RelDataType rType) {
    switch (rType.getSqlTypeName()) {
      case BOOLEAN:
        return TypeInfoFactory.booleanTypeInfo;
      case TINYINT:
        return TypeInfoFactory.byteTypeInfo;
      case SMALLINT:
        return TypeInfoFactory.shortTypeInfo;
      case INTEGER:
        return TypeInfoFactory.intTypeInfo;
      case BIGINT:
        return TypeInfoFactory.longTypeInfo;
      case FLOAT:
        return TypeInfoFactory.floatTypeInfo;
      case DOUBLE:
        return TypeInfoFactory.doubleTypeInfo;
      case DATE:
        return TypeInfoFactory.dateTypeInfo;
      case TIMESTAMP:
        return TypeInfoFactory.timestampTypeInfo;
      case BINARY:
        return TypeInfoFactory.binaryTypeInfo;
      case DECIMAL:
        return TypeInfoFactory.getDecimalTypeInfo(rType.getPrecision(), rType.getScale());
      case VARCHAR:
        if (rType.getPrecision() == Integer.MAX_VALUE) {
          return TypeInfoFactory.getPrimitiveTypeInfo(serdeConstants.STRING_TYPE_NAME);
        } else {
          return TypeInfoFactory.getVarcharTypeInfo(rType.getPrecision());
        }
      case CHAR:
        return TypeInfoFactory.getCharTypeInfo(rType.getPrecision());
      case OTHER:
      default:
        return TypeInfoFactory.voidTypeInfo;
    }
  }
}

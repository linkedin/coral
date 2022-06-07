/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.linkedin.coral.common.TypeConverter;


public class HiveGenericUDFReturnTypeInference implements SqlReturnTypeInference {
  private final String _udfClassName;
  private final List<String> _udfDependencies;
  private final ArtifactsResolver _artifactsResolver;
  private URLClassLoader _udfClassLoader = null;

  public HiveGenericUDFReturnTypeInference(String udfClassName, List<String> udfDependencies) {
    _udfClassName = udfClassName;
    _udfDependencies = udfDependencies;
    _artifactsResolver = new ArtifactsResolver();
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding sqlOperatorBinding) {
    int operandCount = sqlOperatorBinding.getOperandCount();
    ObjectInspector[] inputObjectInspectors = new ObjectInspector[operandCount];
    for (int i = 0; i < operandCount; i++) {
      inputObjectInspectors[i] = getObjectInspector(sqlOperatorBinding.getOperandType(i));
    }
    return inferReturnType(inputObjectInspectors, sqlOperatorBinding.getTypeFactory());
  }

  public RelDataType inferReturnType(ObjectInspector[] inputObjectInspectors, RelDataTypeFactory relDataTypeFactory) {
    try {
      Class dynamicallyLoadedUdfClass = getDynamicallyLoadedUdfClass();
      final Method initializeMethod =
          dynamicallyLoadedUdfClass.getMethod("initialize", getDynamicallyLoadedObjectInspectorArrayClass());
      final Object oi = initializeMethod.invoke(dynamicallyLoadedUdfClass.newInstance(),
          getDynamicallyLoadedObjectInspectors(inputObjectInspectors));
      return getRelDataType(getContextObjectInspector(oi), relDataTypeFactory);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(
          "Unable to find org.apache.hadoop.hive.ql.udf.generic.GenericUDF.initialize() on: " + _udfClassName, e);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Unable to instantiate a new instance of " + _udfClassName, e);
    } catch (InvocationTargetException | IllegalArgumentException e) {
      throw new RuntimeException(
          "Unable to call org.apache.hadoop.hive.ql.udf.generic.GenericUDF.initialize() on: " + _udfClassName, e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Could not load class  " + _udfClassName, e);
    }
  }

  /**
   * This method converts RelDataType to ObjectInspector
   *
   * @param relDataType RelDataType to convert to ObjectInspector
   * @return converted ObjectInspector based on input RelDataType
   */
  private ObjectInspector getObjectInspector(RelDataType relDataType) {
    TypeInfo typeInfo = TypeConverter.convert(relDataType);
    return TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
  }

  /**
   * This method converts ObjectInspector to RelDataType
   *
   * @param hiveObjectInspector ObjectInspector to convert to RelDataType
   * @param relDataTypeFactory RelDataTypeFactory used during the conversion
   * @return converted RelDataType based on input ObjectInspector
   */
  private RelDataType getRelDataType(ObjectInspector hiveObjectInspector, RelDataTypeFactory relDataTypeFactory) {
    TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromObjectInspector(hiveObjectInspector);
    return TypeConverter.convert(typeInfo, relDataTypeFactory);
  }

  private URLClassLoader getUdfClassLoader() {
    if (_udfClassLoader == null) {
      URL[] urls = _udfDependencies.stream()
          .flatMap(udfDependency -> _artifactsResolver.resolve(_udfClassName, udfDependency).stream())
          .map(file -> url(file.toURI())).toArray(URL[]::new);

      _udfClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
    }
    return _udfClassLoader;
  }

  private Class getDynamicallyLoadedUdfClass() throws ClassNotFoundException {
    try {
      return Class.forName(_udfClassName, true, getUdfClassLoader());
    } catch (NoClassDefFoundError error) {
      if (error.getMessage().contains("GenericUDF")) {
        // If GenericUDF class could not be found, add `hive-exec:core` in `_udfDependencies` to download the missing class
        _udfClassLoader = null; // set it to null to re-download
        _udfDependencies.add("org.apache.hive:hive-exec:1.1.0");
        return Class.forName(_udfClassName, true, getUdfClassLoader());
      }
      throw error;
    }
  }

  private Class getDynamicallyLoadedObjectInspectorArrayClass() throws ClassNotFoundException {
    return Class.forName("[Lorg.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;", true, getUdfClassLoader());
  }

  private Object getDynamicallyLoadedObjectInspectors(ObjectInspector[] ois) {
    try {
      Class objectInspectorClass =
          Class.forName("org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector", true, getUdfClassLoader());
      Object objectInspectorArray = Array.newInstance(objectInspectorClass, ois.length);
      for (int i = 0; i < ois.length; i++) {
        Array.set(objectInspectorArray, i, getDynamicallyLoadedObjectInspector(
            TypeInfoUtils.getTypeInfoFromObjectInspector(ois[i]).getQualifiedName()));
      }
      return objectInspectorArray;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Could not get UDF ObjectInspector from context class loader ObjectInspector.", e);
    }
  }

  private Object getDynamicallyLoadedObjectInspector(String typeName) {
    try {
      Class typeInfoUtilsClass =
          Class.forName("org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils", true, getUdfClassLoader());
      Class typeInfoClass = Class.forName("org.apache.hadoop.hive.serde2.typeinfo.TypeInfo", true, getUdfClassLoader());
      Object typeInfo = typeInfoUtilsClass.getMethod("getTypeInfoFromTypeString", String.class).invoke(null, typeName);
      return typeInfoUtilsClass.getMethod("getStandardJavaObjectInspectorFromTypeInfo", typeInfoClass).invoke(null,
          typeInfo);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException("Could not get UDF ObjectInspector from String type name.", e);
    }
  }

  private ObjectInspector getContextObjectInspector(Object oi) {
    try {
      Class objectInspectorClass =
          Class.forName("org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector", true, getUdfClassLoader());
      Class typeInfoUtilsClass =
          Class.forName("org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils", true, getUdfClassLoader());
      Class typeInfoClass = Class.forName("org.apache.hadoop.hive.serde2.typeinfo.TypeInfo", true, getUdfClassLoader());
      Object typeInfo =
          typeInfoUtilsClass.getMethod("getTypeInfoFromObjectInspector", objectInspectorClass).invoke(null, oi);
      String typeInfoString = (String) typeInfoClass.getMethod("getQualifiedName").invoke(typeInfo);
      return TypeInfoUtils
          .getStandardJavaObjectInspectorFromTypeInfo(TypeInfoUtils.getTypeInfoFromTypeString(typeInfoString));
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException("Could not get Context ObjectInspector from dynamically loaded ObjectInspector.", e);
    }
  }

  private static URL url(URI uri) {
    try {
      return uri.toURL();
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Malformed URL: " + uri, e);
    }
  }
}

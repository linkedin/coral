/**
 * Copyright 2020-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

import com.linkedin.coral.hive.hive2rel.TypeConverter;


public class HiveGenericUDFReturnTypeInference implements SqlReturnTypeInference {
  private final String _udfClassName;
  private final List<String> _udfDependencies;
  private final ArtifactsResolver _artifactsResolver;

  public HiveGenericUDFReturnTypeInference(String udfClassName, List<String> udfDependencies) {
    _udfClassName = udfClassName;
    _udfDependencies = udfDependencies;
    _artifactsResolver = new ArtifactsResolver();
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding sqlOperatorBinding) {
    int operandCount = sqlOperatorBinding.getOperandCount();
    ObjectInspector[] inputObjectInspectors = new ObjectInspector[operandCount];
    for (int i = 0; i < sqlOperatorBinding.getOperandCount(); i++) {
      inputObjectInspectors[i] = getHiveObjectInspector(sqlOperatorBinding.getOperandType(i));
    }
    Object[] inputObjectInspectorsParam = { inputObjectInspectors };

    try {
      Class udfClass = getUDFClass();

      return getCalciteRelDataType((ObjectInspector) udfClass.getMethod("initialize", ObjectInspector[].class)
          .invoke(udfClass.newInstance(), inputObjectInspectorsParam), sqlOperatorBinding.getTypeFactory());
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
  private ObjectInspector getHiveObjectInspector(RelDataType relDataType) {
    TypeInfo typeInfo = TypeConverter.convert(relDataType);
    ObjectInspector objectInspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);

    return objectInspector;
  }

  /**
   * This method converts ObjectInspector to RelDataType
   *
   * @param hiveObjectInspector ObjectInspector to convert to RelDataType
   * @param relDataTypeFactory RelDataTypeFactory used during the conversion
   * @return converted RelDataType based on input ObjectInspector
   */
  private RelDataType getCalciteRelDataType(ObjectInspector hiveObjectInspector,
      RelDataTypeFactory relDataTypeFactory) {
    TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromObjectInspector(hiveObjectInspector);
    RelDataType relDataType = TypeConverter.convert(typeInfo, relDataTypeFactory);

    return relDataType;
  }

  private final Class getUDFClass() throws ClassNotFoundException {
    try {
      URL[] urls = _udfDependencies.stream()
          .flatMap(udfDependency -> _artifactsResolver.downloadDependencies(uri(udfDependency)).stream())
          .map(uri -> url(uri)).toArray(URL[]::new);

      URLClassLoader classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

      return Class.forName(_udfClassName, true, classLoader);
    } catch (ClassNotFoundException e) {
      throw new ClassNotFoundException("Could not load class " + _udfClassName, e);
    }
  }

  private static URI uri(String dependency) {
    String ivyDependency = dependency.startsWith("ivy://") ? dependency : "ivy://" + dependency;
    try {
      return new URI(ivyDependency);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid UDF dependency syntax in DaliView definition: " + ivyDependency, e);
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

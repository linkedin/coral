/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.catalog;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.avro.SchemaConverters;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.analysis.NamespaceAlreadyExistsException;
import org.apache.spark.sql.catalyst.analysis.NoSuchNamespaceException;
import org.apache.spark.sql.catalyst.analysis.NoSuchTableException;
import org.apache.spark.sql.catalyst.analysis.NoSuchViewException;
import org.apache.spark.sql.catalyst.analysis.TableAlreadyExistsException;
import org.apache.spark.sql.catalyst.analysis.ViewAlreadyExistsException;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.FunctionResource;
import org.apache.spark.sql.catalyst.catalog.FunctionResourceType;
import org.apache.spark.sql.connector.catalog.CatalogExtension;
import org.apache.spark.sql.connector.catalog.CatalogPlugin;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.connector.catalog.NamespaceChange;
import org.apache.spark.sql.connector.catalog.SupportsNamespaces;
import org.apache.spark.sql.connector.catalog.TableCatalog;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.connector.catalog.View;
import org.apache.spark.sql.connector.catalog.ViewCatalog;
import org.apache.spark.sql.connector.catalog.ViewChange;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.internal.SQLConf;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.apache.spark.util.Utils;
import org.apache.thrift.TException;

import scala.Option;
import scala.collection.JavaConverters;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.schema.avro.ViewToAvroSchemaConverter;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.spark.containers.SparkUDFInfo;


public class CoralSparkViewCatalog<T extends TableCatalog & SupportsNamespaces>
    implements ViewCatalog, CatalogExtension {
  private String catalogName;
  private T sessionCatalog;
  private IMetaStoreClient hiveClient;
  private HiveToRelConverter hiveToRelConverter;
  private ViewToAvroSchemaConverter viewToAvroSchemaConverter;

  @Override
  public Identifier[] listViews(String... strings) throws NoSuchNamespaceException {
    return new Identifier[0];
  }

  @Override
  public View loadView(Identifier identifier) {
    String db = identifier.namespace()[0];
    String tbl = identifier.name();

    final Table table;
    try {
      table = hiveClient.getTable(db, tbl);
    } catch (TException e) {
      return null;
    }
    if (!table.getTableType().equalsIgnoreCase("VIRTUAL_VIEW")) {
      return null;
    }

    try {
      boolean forceLowercaseSchema = (Boolean) SparkSession.active().conf().get(SQLConf.FORCE_LOWERCASE_DALI_SCHEMA());
      final Schema avroSchema = viewToAvroSchemaConverter.toAvroSchema(db, tbl, false, forceLowercaseSchema);
      final DataType schema = SchemaConverters.toSqlType(avroSchema).dataType();
      final RelNode relNode = hiveToRelConverter.convertView(db, tbl);
      final CoralSpark coralSpark = CoralSpark.create(relNode, avroSchema);
      registerUDFs(coralSpark.getSparkUDFInfoList());
      return new CoralSparkView(tbl, coralSpark.getSparkSql(), (StructType) schema, name(), new String[] { db });
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public View createView(Identifier identifier, String s, String s1, String[] strings, StructType structType,
      String[] strings1, String[] strings2, Map<String, String> map)
      throws ViewAlreadyExistsException, NoSuchNamespaceException {
    return null;
  }

  @Override
  public View alterView(Identifier identifier, ViewChange... viewChanges)
      throws NoSuchViewException, IllegalArgumentException {
    return null;
  }

  @Override
  public boolean dropView(Identifier identifier) {
    return false;
  }

  @Override
  public void renameView(Identifier identifier, Identifier identifier1)
      throws NoSuchViewException, ViewAlreadyExistsException {

  }

  private void registerUDFs(List<SparkUDFInfo> sparkUDFInfoList) {
    final SparkSession sparkSession = SparkSession.active();
    Set<URI> mavenDependencies = new HashSet<>();
    sparkUDFInfoList.forEach(sparkUDFInfo -> mavenDependencies.addAll(sparkUDFInfo.getArtifactoryUrls()));

    final List<FunctionResource> resolvedFunctionResources =
        mavenDependencies.stream().map(f -> new FunctionResource(FunctionResourceType.fromString("jar"), f.toString()))
            .collect(Collectors.toList());
    try {
      sparkSession.sessionState().catalog()
          .loadFunctionResources(JavaConverters.asScalaBuffer(resolvedFunctionResources));
    } catch (Exception e) {
      e.printStackTrace();
    }
    sparkUDFInfoList.forEach(udf -> {
      switch (udf.getUdfType()) {
        case HIVE_CUSTOM_UDF:
          sparkSession.sessionState().catalog()
              .registerFunction(new CatalogFunction(new FunctionIdentifier(udf.getFunctionName()), udf.getClassName(),
                  JavaConverters.asScalaBuffer(resolvedFunctionResources)), true, Option.apply(null));
          break;
        case TRANSPORTABLE_UDF:
          try {
            Utils.classForName(udf.getClassName(), true, false).getMethod("register", String.class).invoke(null,
                udf.getFunctionName());
          } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
          }
          break;
        default:
          throw new RuntimeException("Unsupported UDF type: " + udf.getUdfType());
      }
    });
  }

  @Override
  public void setDelegateCatalog(CatalogPlugin sparkSessionCatalog) {
    if (sparkSessionCatalog instanceof TableCatalog && sparkSessionCatalog instanceof SupportsNamespaces) {
      this.sessionCatalog = (T) sparkSessionCatalog;
    } else {
      throw new IllegalArgumentException("Invalid session catalog: " + sparkSessionCatalog);
    }
  }

  @Override
  public String[][] listNamespaces() throws NoSuchNamespaceException {
    return new String[0][];
  }

  @Override
  public String[][] listNamespaces(String[] namespace) throws NoSuchNamespaceException {
    return new String[0][];
  }

  @Override
  public Map<String, String> loadNamespaceMetadata(String[] namespace) throws NoSuchNamespaceException {
    return null;
  }

  @Override
  public void createNamespace(String[] namespace, Map<String, String> metadata) throws NamespaceAlreadyExistsException {

  }

  @Override
  public void alterNamespace(String[] namespace, NamespaceChange... changes) throws NoSuchNamespaceException {

  }

  @Override
  public boolean dropNamespace(String[] namespace) throws NoSuchNamespaceException {
    return false;
  }

  @Override
  public Identifier[] listTables(String[] namespace) throws NoSuchNamespaceException {
    return new Identifier[0];
  }

  @Override
  public org.apache.spark.sql.connector.catalog.Table loadTable(Identifier ident) throws NoSuchTableException {
    return sessionCatalog.loadTable(ident);
  }

  @Override
  public org.apache.spark.sql.connector.catalog.Table createTable(Identifier ident, StructType schema,
      Transform[] partitions, Map<String, String> properties)
      throws TableAlreadyExistsException, NoSuchNamespaceException {
    return null;
  }

  @Override
  public org.apache.spark.sql.connector.catalog.Table alterTable(Identifier ident, TableChange... changes)
      throws NoSuchTableException {
    return null;
  }

  @Override
  public boolean dropTable(Identifier ident) {
    return false;
  }

  @Override
  public void renameTable(Identifier oldIdent, Identifier newIdent)
      throws NoSuchTableException, TableAlreadyExistsException {

  }

  @Override
  public void initialize(String name, CaseInsensitiveStringMap options) {
    this.catalogName = name;
    try {
      hiveClient = new HiveMetaStoreClient(new HiveConf());
    } catch (MetaException e) {
      e.printStackTrace();
    }
    hiveToRelConverter = new HiveToRelConverter(new HiveMscAdapter(hiveClient));
    viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(new HiveMscAdapter(hiveClient));
  }

  @Override
  public String name() {
    return catalogName;
  }
}

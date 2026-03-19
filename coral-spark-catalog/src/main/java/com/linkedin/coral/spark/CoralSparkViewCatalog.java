/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.analysis.NamespaceAlreadyExistsException;
import org.apache.spark.sql.catalyst.analysis.NoSuchFunctionException;
import org.apache.spark.sql.catalyst.analysis.NoSuchNamespaceException;
import org.apache.spark.sql.catalyst.analysis.NoSuchTableException;
import org.apache.spark.sql.catalyst.analysis.NoSuchViewException;
import org.apache.spark.sql.catalyst.analysis.NonEmptyNamespaceException;
import org.apache.spark.sql.catalyst.analysis.TableAlreadyExistsException;
import org.apache.spark.sql.catalyst.analysis.ViewAlreadyExistsException;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.FunctionResource;
import org.apache.spark.sql.catalyst.catalog.FunctionResourceType;
import org.apache.spark.sql.catalyst.catalog.SessionCatalog;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.catalyst.parser.ParseException;
import org.apache.spark.sql.connector.catalog.CatalogExtension;
import org.apache.spark.sql.connector.catalog.CatalogPlugin;
import org.apache.spark.sql.connector.catalog.FunctionCatalog;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.connector.catalog.NamespaceChange;
import org.apache.spark.sql.connector.catalog.SupportsNamespaces;
import org.apache.spark.sql.connector.catalog.TableCatalog;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.connector.catalog.View;
import org.apache.spark.sql.connector.catalog.ViewCatalog;
import org.apache.spark.sql.connector.catalog.ViewChange;
import org.apache.spark.sql.connector.catalog.functions.UnboundFunction;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.apache.spark.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.schema.avro.ViewToAvroSchemaConverter;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.spark.containers.SparkUDFInfo;


/**
 * A Spark 3.5 {@link CatalogExtension} that intercepts view resolution to translate
 * Hive view definitions into Spark SQL via Coral's translation pipeline
 * (Hive view &rarr; Calcite RelNode &rarr; Spark SQL). Table, namespace, and function
 * operations are delegated to the underlying session catalog.
 */
public class CoralSparkViewCatalog<T extends TableCatalog & SupportsNamespaces>
    implements ViewCatalog, CatalogExtension {
  private static final Logger LOG = LoggerFactory.getLogger(CoralSparkViewCatalog.class);
  private String catalogName;
  private T sessionCatalog;
  @Override
  public void initialize(String name, CaseInsensitiveStringMap options) {
    this.catalogName = name;
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
  public String name() {
    return catalogName;
  }

  @Override
  public View loadView(Identifier identifier) {
    String db = identifier.namespace()[0];
    String tbl = identifier.name();

    String tableName = db + "." + tbl;

    HiveToRelConverter hiveToRelConverter;
    ViewToAvroSchemaConverter viewToAvroSchemaConverter;
    HiveMetastoreClient adapter;
    IMetaStoreClient hiveClient;

    try {
      hiveClient = new HiveMetaStoreClient(new HiveConf());
    } catch (MetaException e) {
      throw new RuntimeException("Failed to create HiveMetaStoreClient", e);
    }

    adapter = new HiveMscAdapter(hiveClient);
    hiveToRelConverter = new HiveToRelConverter(adapter);
    viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(adapter);
    final Table table = adapter.getTable(db, tbl);
    if (table == null || !table.getTableType().equalsIgnoreCase(TableType.VIRTUAL_VIEW.name())) {
      throw new NoSuchElementException(String.format("View %s doesn't exist.", tbl));
    }

    final Schema avroSchema = viewToAvroSchemaConverter.toAvroSchema(db, tbl, false, false);
    // Allow Spark to parse the schema string, enabling it to recognize logical types.
    // If we simply call `SchemaConverters.toSqlType(avroSchema)`, Spark won't be able to identify these logical types.
    final DataType catalystSchema = org.apache.spark.sql.avro.SchemaConverters
        .toSqlType(new Schema.Parser().setValidate(false).setValidateDefaults(false).parse(avroSchema.toString()))
        .dataType();
    final RelNode relNode = hiveToRelConverter.convertView(db, tbl);
    final CoralSpark coralSpark = CoralSpark.create(relNode, avroSchema, adapter);

    String sparkSQL = coralSpark.getSparkSql();

    try {
      // Parse the Spark SQL, should the provided SQL be incorrect, it will fail and not use the Coral Spark view catalog.
      SparkSession.active().sessionState().sqlParser().parsePlan(sparkSQL);
    } catch (ParseException e) {
      throw new RuntimeException("Failed to parse Spark SQL: " + sparkSQL, e);
    }
    registerUDFs(coralSpark.getSparkUDFInfoList(), db + "." + tbl);
    LOG.info(String.format("Loaded view %s via CoralSparkViewCatalog successfully.", tableName));
    String resultTbl = tbl;
    String resultDb = db;
    return new View() {
      @Override
      public String name() {
        return resultTbl;
      }

      @Override
      public String query() {
        return sparkSQL;
      }

      @Override
      public String currentCatalog() {
        return catalogName;
      }

      @Override
      public String[] currentNamespace() {
        return new String[] { resultDb };
      }

      @Override
      public StructType schema() {
        return (StructType) catalystSchema;
      }

      @Override
      public String[] queryColumnNames() {
        return schema().fieldNames();
      }

      @Override
      public String[] columnAliases() {
        throw new UnsupportedOperationException("columnAliases() is not supported.");
      }

      @Override
      public String[] columnComments() {
        throw new UnsupportedOperationException("columnComments() is not supported.");
      }

      @Override
      public Map<String, String> properties() {
        return ImmutableMap.of(CatalogTable.VIEW_REFERRED_TEMP_FUNCTION_NAMES(), jsonArrayString(
            coralSpark.getSparkUDFInfoList().stream().map(SparkUDFInfo::getFunctionName).collect(Collectors.toList())));
      }
    };
  }

  private static String jsonArrayString(List<String> list) {
    return list.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ", "[", "]"));
  }

  @Override
  public org.apache.spark.sql.connector.catalog.Table loadTable(Identifier ident) throws NoSuchTableException {
    return sessionCatalog.loadTable(ident);
  }

  private void registerUDFs(List<SparkUDFInfo> sparkUDFInfoList, String viewName) {
    final SparkSession sparkSession = SparkSession.active();
    Set<URI> mavenDependencies = new HashSet<>();
    sparkUDFInfoList.forEach(sparkUDFInfo -> mavenDependencies.addAll(sparkUDFInfo.getArtifactoryUrls()));

    final List<FunctionResource> resolvedFunctionResources =
        mavenDependencies.stream().map(f -> new FunctionResource(FunctionResourceType.fromString("jar"), f.toString()))
            .collect(Collectors.toList());
    SessionCatalog sessionCatalog = sparkSession.sessionState().catalog();
    // Calling SessionCatalog apis such as loadFunctionResources must be thread-safe
    synchronized (sessionCatalog) {
      sessionCatalog.loadFunctionResources(JavaConverters.asScalaBuffer(resolvedFunctionResources));

      sparkUDFInfoList.forEach(udf -> {
        String className = udf.getClassName();
        String functionName = udf.getFunctionName();
        switch (udf.getUdfType()) {
          case HIVE_CUSTOM_UDF:
            if (isSparkUdf(className)) {
              // Registers a Spark UDF following the same approach used in the Transport:
              // https://github.com/linkedin/transport/blob/e4c56a443be01ea4b76e9bcfd68ce7c6516cf754/transportable-udfs-spark_2.11/src/main/scala/org/apache/spark/sql/StdUDFUtils.scala#L17
              Class<Expression> udfClass = Utils.classForName(className, true, false);
              sparkSession.sessionState().functionRegistry().registerFunction(new FunctionIdentifier(functionName),
                  (Seq<Expression> children) -> {
                    try {
                      Constructor<Expression> constructor = udfClass.getDeclaredConstructor(Seq.class);
                      constructor.setAccessible(true); // Required because the constructor might be private
                      return constructor.newInstance(children);
                    } catch (Exception e) {
                      throw new IllegalStateException(e);
                    }
                  }, functionName);
              LOG.info(String.format("Registered %s as Spark UDF with class %s for view %s.", functionName, className,
                  viewName));
            } else {
              sparkSession.sessionState().catalog()
                  .registerFunction(new CatalogFunction(new FunctionIdentifier(functionName), className,
                      JavaConverters.asScalaBuffer(resolvedFunctionResources)), true, Option.apply(null));
              LOG.info(String.format("Registered %s as Hive UDF with class %s for view %s.", functionName, className,
                  viewName));
            }
            break;
          case TRANSPORTABLE_UDF:
            try {
              Utils.classForName(className, true, false).getMethod("register", String.class).invoke(null, functionName);
              LOG.info(String.format("Registered %s as Transport UDF with class %s for view %s.", functionName,
                  className, viewName));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
              throw new RuntimeException(e);
            }
            break;
          default:
            throw new RuntimeException("Unsupported UDF type: " + udf.getUdfType());
        }
      });
    }
  }

  /**
   * Checks if a given class name corresponds to a Spark UDF.
   *
   * This method attempts to load the specified class and verify if it has a constructor
   * that takes a single argument of type Seq[Expression]. If such a constructor exists,
   * the class is considered a Spark UDF.
   */
  private boolean isSparkUdf(String className) {
    try {
      Class<?> udfClass = Utils.classForName(className, true, false);
      Constructor<?> constructor = udfClass.getDeclaredConstructor(Seq.class);
      // Check if the parameter type is parameterized and its argument is Expression
      Type[] genericParameterTypes = constructor.getGenericParameterTypes();
      if (genericParameterTypes.length == 1 && genericParameterTypes[0] instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) genericParameterTypes[0];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length == 1
            && actualTypeArguments[0].getTypeName().equals("org.apache.spark.sql.catalyst.expressions.Expression")) {
          return true;
        }
      }
    } catch (NoSuchMethodException e) {
      return false;
    }
    return false;
  }

  // --- ViewCatalog delegation (read-only; write operations are unsupported) ---

  @Override
  public Identifier[] listViews(String... namespace) throws NoSuchNamespaceException {
    return new Identifier[0];
  }

  @Override
  public View createView(Identifier ident, String sql, String currentCatalog, String[] currentNamespace,
      StructType schema, String[] queryColumnNames, String[] columnAliases, String[] columnComments,
      Map<String, String> properties) throws ViewAlreadyExistsException, NoSuchNamespaceException {
    throw new UnsupportedOperationException("createView is not supported by CoralSparkViewCatalog");
  }

  @Override
  public View alterView(Identifier ident, ViewChange... changes) throws NoSuchViewException {
    throw new UnsupportedOperationException("alterView is not supported by CoralSparkViewCatalog");
  }

  @Override
  public boolean dropView(Identifier ident) {
    throw new UnsupportedOperationException("dropView is not supported by CoralSparkViewCatalog");
  }

  @Override
  public void renameView(Identifier oldIdent, Identifier newIdent)
      throws NoSuchViewException, ViewAlreadyExistsException {
    throw new UnsupportedOperationException("renameView is not supported by CoralSparkViewCatalog");
  }

  // --- TableCatalog delegation ---

  @Override
  public Identifier[] listTables(String[] namespace) throws NoSuchNamespaceException {
    return sessionCatalog.listTables(namespace);
  }

  @Override
  public org.apache.spark.sql.connector.catalog.Table createTable(Identifier ident, StructType schema,
      Transform[] partitions, Map<String, String> properties)
      throws TableAlreadyExistsException, NoSuchNamespaceException {
    return sessionCatalog.createTable(ident, schema, partitions, properties);
  }

  @Override
  public org.apache.spark.sql.connector.catalog.Table alterTable(Identifier ident, TableChange... changes)
      throws NoSuchTableException {
    return sessionCatalog.alterTable(ident, changes);
  }

  @Override
  public boolean dropTable(Identifier ident) {
    return sessionCatalog.dropTable(ident);
  }

  @Override
  public void renameTable(Identifier oldIdent, Identifier newIdent)
      throws NoSuchTableException, TableAlreadyExistsException {
    sessionCatalog.renameTable(oldIdent, newIdent);
  }

  // --- SupportsNamespaces delegation ---

  @Override
  public String[][] listNamespaces() throws NoSuchNamespaceException {
    return sessionCatalog.listNamespaces();
  }

  @Override
  public String[][] listNamespaces(String[] namespace) throws NoSuchNamespaceException {
    return sessionCatalog.listNamespaces(namespace);
  }

  @Override
  public Map<String, String> loadNamespaceMetadata(String[] namespace) throws NoSuchNamespaceException {
    return sessionCatalog.loadNamespaceMetadata(namespace);
  }

  @Override
  public void createNamespace(String[] namespace, Map<String, String> metadata) throws NamespaceAlreadyExistsException {
    sessionCatalog.createNamespace(namespace, metadata);
  }

  @Override
  public void alterNamespace(String[] namespace, NamespaceChange... changes) throws NoSuchNamespaceException {
    sessionCatalog.alterNamespace(namespace, changes);
  }

  @Override
  public boolean dropNamespace(String[] namespace, boolean cascade)
      throws NoSuchNamespaceException, NonEmptyNamespaceException {
    return sessionCatalog.dropNamespace(namespace, cascade);
  }

  // --- FunctionCatalog delegation ---

  @Override
  public Identifier[] listFunctions(String[] namespace) throws NoSuchNamespaceException {
    if (sessionCatalog instanceof FunctionCatalog) {
      return ((FunctionCatalog) sessionCatalog).listFunctions(namespace);
    }
    return new Identifier[0];
  }

  @Override
  public UnboundFunction loadFunction(Identifier ident) throws NoSuchFunctionException {
    if (sessionCatalog instanceof FunctionCatalog) {
      return ((FunctionCatalog) sessionCatalog).loadFunction(ident);
    }
    throw new NoSuchFunctionException(ident);
  }
}

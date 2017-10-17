package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.ql.metadata.HiveException;


/**
 * Calcite needs different objects that are not trivial to create. This class
 * simplifies creation of objects, required by Calcite, easy. These objects
 * are created only once and shared across each call to corresponding getter.
 */
public class RelContextProvider {

  private final FrameworkConfig config;
  private final HiveSchema schema;
  private RelBuilder relBuilder;
  private CalciteCatalogReader catalogReader;
  private HiveSqlValidator sqlValidator;
  private RelOptCluster cluster;
  private SqlToRelConverter relConverter;

  /**
   * Instantiates a new Rel context provider.
   *
   * @param schema {@link HiveSchema} to use for conversion to relational algebra
   * @throws HiveException the hive exception
   */
  public RelContextProvider(HiveSchema schema) throws HiveException {
    this.schema = schema;
    SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
    schemaPlus.add(HiveSchema.ROOT_SCHEMA, schema);

    config = Frameworks.newConfigBuilder()
        .defaultSchema(schemaPlus)
        .traitDefs((List<RelTraitDef>) null)
        .programs(Programs.ofRules(Programs.RULE_SET))
        .build();
  }

  /**
   * Gets {@link FrameworkConfig} for creation of various objects
   * from Calcite object model
   *
   * @return FrameworkConfig object
   */
  FrameworkConfig getConfig() {
    return config;
  }

  /**
   * Gets {@link RelBuilder} object for generating relational algebra.
   *
   * @return the rel builder
   */
  RelBuilder getRelBuilder() {
    if (relBuilder == null) {
      relBuilder = RelBuilder.create(config);
    }
    return relBuilder;
  }

  /**
   * Gets calcite catalog reader.
   *
   * @return the calcite catalog reader
   */
  CalciteCatalogReader getCalciteCatalogReader() {
    if (catalogReader == null) {
      catalogReader =
          new CalciteCatalogReader(config.getDefaultSchema().unwrap(CalciteSchema.class),
              false,
              ImmutableList.of(HiveSchema.ROOT_SCHEMA, HiveSchema.DEFAULT_DB),
              getRelBuilder().getTypeFactory());
    }
    return catalogReader;
  }

  /**
   * Gets hive sql validator.
   *
   * @return the hive sql validator
   */
  HiveSqlValidator getHiveSqlValidator() {
    if (sqlValidator == null) {
      sqlValidator = new HiveSqlValidator(SqlStdOperatorTable.instance(),
          getCalciteCatalogReader(),
          ((JavaTypeFactory) relBuilder.getTypeFactory()),
          SqlConformanceEnum.PRAGMATIC_2003);
    }
    return sqlValidator;
  }

  /**
   * Gets rel opt cluster.
   *
   * @return the rel opt cluster
   */
  RelOptCluster getRelOptCluster() {
    if (cluster == null) {
      cluster = RelOptCluster.create(new VolcanoPlanner(), getRelBuilder().getRexBuilder());
    }
    return cluster;
  }

  HiveViewExpander getViewExpander() {
    // we don't need to cache this...Okay to re-create each time
    return new HiveViewExpander(this);
  }

  /**
   * Gets sql to rel converter.
   *
   * @return the sql to rel converter
   */
  public SqlToRelConverter getSqlToRelConverter() {
    if (relConverter == null) {
      relConverter = new HiveSqlToRelConverter(getViewExpander(),
          getHiveSqlValidator(),
          getCalciteCatalogReader(),
          getRelOptCluster(),
          StandardConvertletTable.INSTANCE,
          SqlToRelConverter.configBuilder().build());
    }
    return relConverter;
  }
}

/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.planner.BeamCodeGenerator;
import com.linkedin.beam.excution.BeamAPIUtil;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import com.linkedin.beam.planner.CalciteBeamConfig;
import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.planner.BeamPlanner;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.type.SqlTypeName;

import static com.linkedin.beam.utils.MethodNames.*;


public final class BeamTableScan extends TableScan implements BeamNode {
  private static final String SCAN_VARIABLE_NAME = "stream";
  private int beamNodeId;
  private CalciteBeamConfig.StreamConfig streamConfig = null;

  private BeamTableScan(RelOptCluster cluster, RelTraitSet traitSet, RelOptTable table) {
    super(cluster, traitSet, table);
  }

  public static BeamTableScan create(RelOptCluster cluster, final RelOptTable relOptTable) {
    final RelTraitSet traitSet = cluster.traitSetOf(BeamConvention.INSTANCE);
    return new BeamTableScan(cluster, traitSet, relOptTable);
  }

  @Override
  public String getVariableName() {
    return SCAN_VARIABLE_NAME + getBeamNodeId();
  }

  @Override
  public int getBeamNodeId() {
    return beamNodeId;
  }

  @Override
  public void setBeamNodeId(int beamNodeId) {
    this.beamNodeId = beamNodeId;
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    final Expression readGenericRecord = Expressions.call(KafkaIOGenericRecord.class, READ);
    Expression withTopic =
        Expressions.call(readGenericRecord, WITH_TOPIC, Expressions.constant(getInputStreamName(), String.class));

    final List<String> timeField = getStreamConfig().timeField;
    if (timeField != null && !timeField.isEmpty()) {
      validateTimeSchema(timeField);
      final List<Expression> timeColumns = new ArrayList<>();
      for (String column : timeField) {
        timeColumns.add(Expressions.constant(column, String.class));
      }
      final Expression timeColumnList = Expressions.call(ImmutableList.class, OF, timeColumns);
      final Expression timeFn = Expressions.call(BeamAPIUtil.class, WITH_TIME_FUNCTION, timeColumnList);
      withTopic = Expressions.call(withTopic, WITH_TIMESTAMP_FN, timeFn);
    }

    final Expression withoutMetadata = Expressions.call(withTopic, WITHOUT_METADATA);

    final Method pipelineApplyMethod = Types.lookupMethod(Pipeline.class, APPLY, String.class, PTransform.class);
    final Expression tableScanExpr = Expressions.call(BeamCodeGenerator.PIPELINE, pipelineApplyMethod, ImmutableList.of(
        Expressions.constant("Read " + getInputStreamName(), String.class), withoutMetadata));
    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this), tableScanExpr));
  }

  private void validateTimeSchema(List<String> timeField) {
    RelDataType recordType = getRowType();
    for (int i = 0; i < timeField.size(); i++) {
      final RelDataTypeField field = recordType.getField(timeField.get(i), true, false);
      if (field == null) {
        fail(timeField);
      }
      if (i < timeField.size() - 1) {
        if (!field.getType().isStruct()) {
          fail(timeField);
        }
        recordType = field.getType();
        continue;
      }

      // Final time field should be bigint
      if (field.getType().getSqlTypeName() != SqlTypeName.BIGINT) {
        throw new IllegalArgumentException("Invalid type for time column '" + field.getName() + "'. Expect BIGINT (long) type,"
            + " actual type: " + field.getType().getSqlTypeName());
      }
    }
  }

  private void fail(List<String> timeField) {
    throw new IllegalArgumentException("Invalid schema for time field. Time field: " + timeField
        + ". Record schema: " + getRowType());
  }


  private CalciteBeamConfig.StreamConfig getStreamConfig() {
    if (streamConfig == null) {
      streamConfig = getCalciteBeamConfig().getInputStream(getTable().getQualifiedName());
    }
    return streamConfig;
  }

  private CalciteBeamConfig getCalciteBeamConfig() {
    return ((BeamPlanner) getCluster().getPlanner())._calciteBeamConfig;
  }

  public String getInputStreamName() {
    return getStreamConfig().topic;
  }
}

/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.utils.RelDataTypeToAvro;
import com.linkedin.beam.utils.RexBeamUtils;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.calcite.linq4j.tree.ConstantExpression;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.rel.RelNode;

import static com.linkedin.beam.utils.MethodNames.*;
import static com.linkedin.beam.utils.Methods.*;


public interface BeamNode {
  Type KV_TYPE = Types.of(KV.class, String.class, GenericRecord.class);
  Type VITER_TYPE = Types.of(Iterable.class, GenericRecord.class);
  Type PCOLLECTION_KV_TYPE = Types.of(PCollection.class, KV_TYPE);
  Type COLLECTION_KV_TYPE = Types.of(Collection.class, KV_TYPE);

  Expression STRING_ENCODER = Expressions.call(StringUtf8Coder.class, OF);

  String getVariableName();

  /**
   * Converts the Beam node into Java Beam API statements.
   * @param statements List to contain all result statements
   */
  void toBeamStatement(List<Statement> statements);

  int getBeamNodeId();

  void setBeamNodeId(int beamNodeId);

  static String getNodeName(RelNode relNode) {
    return relNode.getRelTypeName().toLowerCase().replace("beam", "") + ((BeamNode) relNode).getBeamNodeId();
  }

  static ParameterExpression getBeamNodeVar(BeamNode node) {
    return Expressions.parameter(PCOLLECTION_KV_TYPE, node.getVariableName());
  }

  static ParameterExpression getNodeSchemaVar(BeamNode node) {
    return Expressions.parameter(Schema.class, RexBeamUtils.getSchemaName(node.getVariableName()));
  }

  static ParameterExpression getRecordVar(BeamNode beamNode) {
    return Expressions.parameter(GenericRecord.class, beamNode.getVariableName() + "Record");
  }

  static ParameterExpression getBeamKVVar(BeamNode beamNode) {
    return Expressions.parameter(KV_TYPE, beamNode.getVariableName() + "KV");
  }

  static ConstantExpression getAvroName(String relFieldName) {
    return Expressions.constant(RelDataTypeToAvro.toAvroQualifedName(relFieldName), String.class);
  }

  static Statement getRecordFromKV(BeamNode beamNode) {
    final ParameterExpression recordVar = getRecordVar(beamNode);
    final ParameterExpression kvVar = getBeamKVVar(beamNode);
    return Expressions.declare(Modifier.FINAL, recordVar, Expressions.call(kvVar, GET_VALUE, ImmutableList.of()));
  }

  static Expression getSetSchemaCoderExpr(BeamNode beamNode, Expression receiver) {
    final Expression schemaCoder = Expressions.call(AvroCoder.class, OF, getNodeSchemaVar(beamNode));
    final Expression kvEncoder = Expressions.call(KvCoder.class, OF, STRING_ENCODER, schemaCoder);
    return Expressions.call(receiver, P_COLLECTION_SET_CODER, kvEncoder);
  }
}

/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableFunctionScan;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.logical.LogicalExchange;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalIntersect;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalMatch;
import org.apache.calcite.rel.logical.LogicalMinus;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalSort;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.spark.containers.SparkRelInfo;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.utils.RelDataTypeToHiveTypeStringConverter;


/**
 * This class applies series of transformations to make a IR RelNode compatible with Spark.
 *
 * It uses Calcite's RelShuttle and RexShuttle to traverse the RelNode Plan.
 * During traversal it identifies and transforms UDFs.
 *      1) Identify UDF if it is defined in [[TransportableUDFMap]] and adds it to a List<SparkUDFInfo>
 *      2) Rewrites UDF name in the RelNode plan
 *        for example: com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds -> epochToEpochMilliseconds
 *
 * Use `transform` to get an instance of [[SparkRelInfo]] which contains the Spark RelNode and SparkUDFInfoList.
 */
class IRRelToSparkRelTransformer {

  private IRRelToSparkRelTransformer() {
  }

  /**
   * This API is used to transforms IR RelNode to make it compatible with spark.
   *
   * @return [[SparkRelInfo]] containing the Spark RelNode and list of standard UDFs.
   *
   */
  static SparkRelInfo transform(RelNode calciteNode) {
    Set<SparkUDFInfo> sparkUDFInfos = new HashSet<>();
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(LogicalProject project) {
        return super.visit(project).accept(getSparkRexConverter(project));
      }

      @Override
      public RelNode visit(LogicalFilter inputFilter) {
        return super.visit(inputFilter).accept(getSparkRexConverter(inputFilter));
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        return super.visit(aggregate).accept(getSparkRexConverter(aggregate));
      }

      @Override
      public RelNode visit(LogicalMatch match) {
        return super.visit(match).accept(getSparkRexConverter(match));
      }

      @Override
      public RelNode visit(TableScan scan) {
        return super.visit(scan).accept(getSparkRexConverter(scan));
      }

      @Override
      public RelNode visit(TableFunctionScan scan) {
        return super.visit(scan).accept(getSparkRexConverter(scan));
      }

      @Override
      public RelNode visit(LogicalValues values) {
        return super.visit(values).accept(getSparkRexConverter(values));
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        return super.visit(join).accept(getSparkRexConverter(join));
      }

      @Override
      public RelNode visit(LogicalCorrelate correlate) {
        return super.visit(correlate).accept(getSparkRexConverter(correlate));
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        return super.visit(union).accept(getSparkRexConverter(union));
      }

      @Override
      public RelNode visit(LogicalIntersect intersect) {
        return super.visit(intersect).accept(getSparkRexConverter(intersect));
      }

      @Override
      public RelNode visit(LogicalMinus minus) {
        return super.visit(minus).accept(getSparkRexConverter(minus));
      }

      @Override
      public RelNode visit(LogicalSort sort) {
        return super.visit(sort).accept(getSparkRexConverter(sort));
      }

      @Override
      public RelNode visit(LogicalExchange exchange) {
        return super.visit(exchange).accept(getSparkRexConverter(exchange));
      }

      @Override
      public RelNode visit(RelNode other) {
        return super.visit(other).accept(getSparkRexConverter(other));
      }

      private SparkRexConverter getSparkRexConverter(RelNode node) {
        return new SparkRexConverter(node.getCluster().getRexBuilder(), sparkUDFInfos);
      }
    };
    return new SparkRelInfo(calciteNode.accept(converter), sparkUDFInfos);
  }

  /**
   * For replacing a UDF SQL operator with a new SQL operator with different name.
   *
   * Consults [[TransportableUDFMap]] to get the new name.
   *
   * for example: com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds -> epochToEpochMilliseconds
   */
  private static class SparkRexConverter extends RexShuttle {
    private final RexBuilder rexBuilder;
    private final Set<SparkUDFInfo> sparkUDFInfos;

    SparkRexConverter(RexBuilder rexBuilder, Set<SparkUDFInfo> sparkUDFInfos) {
      this.sparkUDFInfos = sparkUDFInfos;
      this.rexBuilder = rexBuilder;
    }

    /**
     * This method traverses the list of RexCall nodes.  During traversal, this method performs the necessary
     * conversion from Calcite terms to Spark terms.  For example, Calcite has a built-in function "CARDINALITY",
     * which corresponds to "SIZE" in Spark.
     *
     * In order to convert to Spark terms correctly, we need to traverse RexCall expression in post-order.
     * This is because a built-in function name may appear as parameter of a user function.
     * For example, user_function1( CARDINALITY(fieldName) )
     */
    @Override
    public RexNode visitCall(RexCall call) {
      if (call == null) {
        return null;
      }

      RexCall updatedCall = (RexCall) super.visitCall(call);

      RexNode convertToNewNode =
          convertToZeroBasedArrayIndex(updatedCall).orElseGet(() -> convertFuzzyUnionGenericProject(updatedCall)
              .orElseGet(() -> removeCastToEnsureCorrectNullability(updatedCall).orElse(updatedCall)));

      return convertToNewNode;
    }

    // Coral RelNode Stores array indexes as +1, this fixes the behavior on spark side
    private Optional<RexNode> convertToZeroBasedArrayIndex(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.ITEM)) {
        RexNode columnRef = call.getOperands().get(0);
        RexNode itemRef = call.getOperands().get(1);
        if (columnRef.getType() instanceof ArraySqlType) {
          if (itemRef.isA(SqlKind.LITERAL) && itemRef.getType().getSqlTypeName().equals(SqlTypeName.INTEGER)) {
            Integer val = ((RexLiteral) itemRef).getValueAs(Integer.class);
            RexLiteral newItemRef = rexBuilder.makeExactLiteral(new BigDecimal(val - 1), itemRef.getType());
            return Optional.of(rexBuilder.makeCall(call.op, columnRef, newItemRef));
          } else {
            RexNode zeroBasedIndex =
                rexBuilder.makeCall(SqlStdOperatorTable.MINUS, itemRef, rexBuilder.makeExactLiteral(BigDecimal.ONE));
            return Optional.of(rexBuilder.makeCall(call.op, columnRef, zeroBasedIndex));
          }
        }
      }
      return Optional.empty();
    }

    /**
     * Add the schema to GenericProject in Fuzzy Union
     * @param call a given RexCall
     * @return RexCall that resolves FuzzyUnion if its operator is GenericProject; otherwise, return empty
     */
    private Optional<RexNode> convertFuzzyUnionGenericProject(RexCall call) {
      if (call.getOperator() instanceof GenericProjectFunction) {
        // Register generic_project UDF
        sparkUDFInfos.add(new SparkUDFInfo("com.linkedin.genericprojectudf.GenericProject", "generic_project",
            ImmutableList.of(URI.create("ivy://com.linkedin.GenericProject:GenericProject-impl:+")),
            SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));
        RelDataType expectedRelDataType = call.getType();
        String expectedRelDataTypeString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(expectedRelDataType);

        List<RexNode> newOperands = new ArrayList<>();
        newOperands.add(call.getOperands().get(0));
        newOperands.add(rexBuilder.makeLiteral(expectedRelDataTypeString));

        return Optional
            .of(rexBuilder.makeCall(expectedRelDataType, new GenericProjectFunction(expectedRelDataType), newOperands));
      }
      return Optional.empty();
    }

    /**
     *  Calcite entails the nullability of an expression by casting it to the correct nullable type.
     *  However, for complex types like ARRAY<STRING NOT NULL> (element non-nullable, but top-level nullable),
     *  the translated SQL will be `CAST(XXX AS ARRAY<STRING>)`, which strip the nullable information.
     *  Since Spark treats a cast target sql type name as always nullable (both inner and outer),
     *  it will treat above cast call as type ARRAY<STRING:nullable>:nullable, this deviates
     *  from the nullability represented in RelNode/Coral-Schema ARRAY<STRING:non-nullable>:nullable,
     *  see {@link com.linkedin.coral.schema.avro.ViewToAvroSchemaConverterTests#testCaseCallWithNullBranchAndComplexDataTypeBranch()
     *  testCastCallNullablility}
     *
     *  To make this work, we remove all the CAST expressions induced by nullability differences, and let Spark's
     *  SQL analyzer derive the nullability for the SQL itself, and as long as Coral-Schema can be an equal or looser
     *  with regard to the Spark analyzer schema, it should make Coral compatible with Spark.
     */
    private Optional<RexNode> removeCastToEnsureCorrectNullability(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.CAST)) {
        if (RexUtil.isNullLiteral(call, true)) {
          return Optional.of(rexBuilder.makeNullLiteral(call.getType()));
        }
        RelDataType castType = call.getType();
        RelDataType originalType = call.getOperands().get(0).getType();
        if (castType.isNullable() && !originalType.isNullable()
            && rexBuilder.getTypeFactory().createTypeWithNullability(originalType, true).equals(castType)) {
          return Optional.of(rexBuilder.copy(call.getOperands().get(0)));
        }
      }
      return Optional.empty();
    }
  }
}

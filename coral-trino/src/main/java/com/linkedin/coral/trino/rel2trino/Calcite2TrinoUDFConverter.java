/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

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
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.type.SqlTypeFamily;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.com.google.common.collect.Multimap;
import com.linkedin.coral.hive.hive2rel.functions.GenericProjectFunction;
import com.linkedin.coral.trino.rel2trino.functions.GenericProjectToTrinoConverter;


public class Calcite2TrinoUDFConverter {
  private Calcite2TrinoUDFConverter() {
  }

  /**
   * Replaces Calcite SQL operators with Trino UDF to obtain the Trino-compatible Calcite plan.
   *
   * @param calciteNode Original Calcite plan
   * @return Trino-compatible Calcite plan
   */
  public static RelNode convertRel(RelNode calciteNode) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(LogicalProject project) {
        return super.visit(project).accept(getTrinoRexConverter(project));
      }

      @Override
      public RelNode visit(LogicalFilter inputFilter) {
        return super.visit(inputFilter).accept(getTrinoRexConverter(inputFilter));
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        return super.visit(aggregate).accept(getTrinoRexConverter(aggregate));
      }

      @Override
      public RelNode visit(LogicalMatch match) {
        return super.visit(match).accept(getTrinoRexConverter(match));
      }

      @Override
      public RelNode visit(TableScan scan) {
        return super.visit(scan).accept(getTrinoRexConverter(scan));
      }

      @Override
      public RelNode visit(TableFunctionScan scan) {
        return super.visit(scan).accept(getTrinoRexConverter(scan));
      }

      @Override
      public RelNode visit(LogicalValues values) {
        return super.visit(values).accept(getTrinoRexConverter(values));
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        return super.visit(join).accept(getTrinoRexConverter(join));
      }

      @Override
      public RelNode visit(LogicalCorrelate correlate) {
        return super.visit(correlate).accept(getTrinoRexConverter(correlate));
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        return super.visit(union).accept(getTrinoRexConverter(union));
      }

      @Override
      public RelNode visit(LogicalIntersect intersect) {
        return super.visit(intersect).accept(getTrinoRexConverter(intersect));
      }

      @Override
      public RelNode visit(LogicalMinus minus) {
        return super.visit(minus).accept(getTrinoRexConverter(minus));
      }

      @Override
      public RelNode visit(LogicalSort sort) {
        return super.visit(sort).accept(getTrinoRexConverter(sort));
      }

      @Override
      public RelNode visit(LogicalExchange exchange) {
        return super.visit(exchange).accept(getTrinoRexConverter(exchange));
      }

      @Override
      public RelNode visit(RelNode other) {
        return super.visit(other).accept(getTrinoRexConverter(other));
      }

      private TrinoRexConverter getTrinoRexConverter(RelNode node) {
        return new TrinoRexConverter(node.getCluster().getRexBuilder());
      }
    };
    return calciteNode.accept(converter);
  }

  /**
   * For replacing a certain Calcite SQL operator with Trino UDFs in a relational expression
   */
  public static class TrinoRexConverter extends RexShuttle {
    private final RexBuilder rexBuilder;

    // SUPPORTED_TYPE_CAST_MAP is a static mapping that maps a SqlTypeFamily key to its set of
    // type-castable SqlTypeFamilies.
    private static final Multimap<SqlTypeFamily, SqlTypeFamily> SUPPORTED_TYPE_CAST_MAP;
    static {
      SUPPORTED_TYPE_CAST_MAP = ImmutableMultimap.<SqlTypeFamily, SqlTypeFamily> builder()
          .putAll(SqlTypeFamily.CHARACTER, SqlTypeFamily.NUMERIC, SqlTypeFamily.BOOLEAN).build();
    }

    public TrinoRexConverter(RexBuilder rexBuilder) {
      this.rexBuilder = rexBuilder;
    }

    @Override
    public RexNode visitCall(RexCall call) {
      // GenericProject requires a nontrivial function rewrite because of the following:
      //   - makes use of Trino built-in UDFs transform_values for map objects and transform for array objects
      //     which has lambda functions as parameters
      //     - syntax is difficult for Calcite to parse
      //   - the return type varies based on a desired schema to be projected
      if (call.getOperator() instanceof GenericProjectFunction) {
        return GenericProjectToTrinoConverter.convertGenericProject(rexBuilder, call);
      }

      final UDFTransformer transformer =
          CalciteTrinoUDFMap.getUDFTransformer(call.getOperator().getName(), call.operands.size());
      if (transformer != null) {
        return super.visitCall((RexCall) transformer.transformCall(rexBuilder, call.getOperands()));
      }
      RexCall modifiedCall = adjustInconsistentTypesToEqualityOperator(call);
      return super.visitCall(modifiedCall);
    }

    // [CORAL-18] Hive is inconsistent and allows everything to be string so we make
    // this adjustment for common case. Ideally, we should use Rel collation to
    // check if this is HiveCollation before applying such rules but we don't use
    // Collation here.
    private RexCall adjustInconsistentTypesToEqualityOperator(RexCall call) {
      final SqlOperator op = call.getOperator();
      if (op.getKind() != SqlKind.EQUALS) {
        return call;
      }

      RexNode leftOperand = call.getOperands().get(0);
      final RexNode rightOperand = call.getOperands().get(1);

      if (leftOperand.getKind() == SqlKind.CAST) {
        leftOperand = ((RexCall) leftOperand).getOperands().get(0);
      }

      if (SUPPORTED_TYPE_CAST_MAP.containsEntry(leftOperand.getType().getSqlTypeName().getFamily(),
          rightOperand.getType().getSqlTypeName().getFamily())) {
        final RexNode tryCastNode =
            rexBuilder.makeCall(rightOperand.getType(), TrinoTryCastFunction.INSTANCE, ImmutableList.of(leftOperand));
        return (RexCall) rexBuilder.makeCall(op, tryCastNode, rightOperand);
      }
      return call;
    }
  }
}

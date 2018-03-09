package com.linkedin.coral.presto.rel2presto;

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


public class Calcite2PrestoUDFConverter {
  private Calcite2PrestoUDFConverter() {
  }

  /**
   * Replaces Calcite SQL operators with Presto UDF to obtain the PrestoSQL-compatible Calcite plan.
   *
   * @param calciteNode Original Calcite plan
   * @return PrestoSQL-compatible Calcite plan
   */
  public static RelNode convertRel(RelNode calciteNode) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(LogicalProject project) {
        return super.visit(project).accept(getPrestoRexConverter(project));
      }

      @Override
      public RelNode visit(LogicalFilter inputFilter) {
        return super.visit(inputFilter).accept(getPrestoRexConverter(inputFilter));
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        return super.visit(aggregate).accept(getPrestoRexConverter(aggregate));
      }

      @Override
      public RelNode visit(LogicalMatch match) {
        return super.visit(match).accept(getPrestoRexConverter(match));
      }

      @Override
      public RelNode visit(TableScan scan) {
        return super.visit(scan).accept(getPrestoRexConverter(scan));
      }

      @Override
      public RelNode visit(TableFunctionScan scan) {
        return super.visit(scan).accept(getPrestoRexConverter(scan));
      }

      @Override
      public RelNode visit(LogicalValues values) {
        return super.visit(values).accept(getPrestoRexConverter(values));
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        return super.visit(join).accept(getPrestoRexConverter(join));
      }

      @Override
      public RelNode visit(LogicalCorrelate correlate) {
        return super.visit(correlate).accept(getPrestoRexConverter(correlate));
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        return super.visit(union).accept(getPrestoRexConverter(union));
      }

      @Override
      public RelNode visit(LogicalIntersect intersect) {
        return super.visit(intersect).accept(getPrestoRexConverter(intersect));
      }

      @Override
      public RelNode visit(LogicalMinus minus) {
        return super.visit(minus).accept(getPrestoRexConverter(minus));
      }

      @Override
      public RelNode visit(LogicalSort sort) {
        return super.visit(sort).accept(getPrestoRexConverter(sort));
      }

      @Override
      public RelNode visit(LogicalExchange exchange) {
        return super.visit(exchange).accept(getPrestoRexConverter(exchange));
      }

      @Override
      public RelNode visit(RelNode other) {
        return super.visit(other).accept(getPrestoRexConverter(other));
      }

      private PrestoRexConverter getPrestoRexConverter(RelNode node) {
        return new PrestoRexConverter(node.getCluster().getRexBuilder());
      }
    };
    return calciteNode.accept(converter);
  }

  /**
   * For replacing a certain Calcite SQL operator with Presto UDFs in a relational expression
   */
  public static class PrestoRexConverter extends RexShuttle {
    private final RexBuilder rexBuilder;

    public PrestoRexConverter(RexBuilder rexBuilder) {
      this.rexBuilder = rexBuilder;
    }

    @Override
    public RexNode visitCall(RexCall call) {
      final UDFTransformer transformer = CalcitePrestoUDFMap.getUDFTransformer(call.getOperator().getName(), call.operands.size());
      if (transformer != null) {
        return transformer.transformCall(rexBuilder, call.getOperands());
      }
      return super.visitCall(call);
    }
  }

}

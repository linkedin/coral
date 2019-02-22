package com.linkedin.coral.presto.rel2presto;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.com.google.common.collect.Multimap;
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

    // SUPPORTED_TYPE_CAST_MAP is a static mapping that maps a SqlTypeFamily key to its set of
    // type-castable SqlTypeFamilies.
    private static final Multimap<SqlTypeFamily, SqlTypeFamily> SUPPORTED_TYPE_CAST_MAP;
    static {
      SUPPORTED_TYPE_CAST_MAP = ImmutableMultimap.<SqlTypeFamily, SqlTypeFamily>builder()
          .putAll(SqlTypeFamily.CHARACTER, SqlTypeFamily.NUMERIC, SqlTypeFamily.BOOLEAN)
          .build();
    }

    public PrestoRexConverter(RexBuilder rexBuilder) {
      this.rexBuilder = rexBuilder;
    }

    @Override
    public RexNode visitCall(RexCall call) {
      final UDFTransformer transformer =
          CalcitePrestoUDFMap.getUDFTransformer(call.getOperator().getName(), call.operands.size());
      if (transformer != null) {
        return transformer.transformCall(rexBuilder, call.getOperands());
      }
      RexCall modifiedCall = adjustInconsistentTypesToEqualityOperator(call);
      return super.visitCall(modifiedCall);
    }

    // [CORAL-18] Hive is inconsistent and allows everything to be string so we make
    // this adjustment for common case. Ideally, we should use Rel collation to
    // check if this is HiveCollation before applying such rules but we don't use
    // Collation here.
    private RexCall adjustInconsistentTypesToEqualityOperator(RexCall call) {
      SqlOperator op = call.getOperator();
      if (op.getKind() != SqlKind.EQUALS) {
        return call;
      }

      RexNode leftOperand = call.getOperands().get(0);
      RexNode rightOperand = call.getOperands().get(1);
      if (SUPPORTED_TYPE_CAST_MAP.containsEntry(leftOperand.getType().getSqlTypeName().getFamily(),
          rightOperand.getType().getSqlTypeName().getFamily())) {
        RexNode tryCastNode =
            rexBuilder.makeCall(rightOperand.getType(), PrestoTryCastFunction.INSTANCE, ImmutableList.of(leftOperand));
        return (RexCall) rexBuilder.makeCall(op, tryCastNode, rightOperand);
      }
      return call;
    }
  }
}

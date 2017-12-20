package com.linkedin.coral.presto.rel2presto;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.logical.LogicalProject;
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
      public RelNode visit(RelNode relNode) {
        final RelNode newRel = super.visit(relNode);
        if (newRel instanceof Project) {
          Project oldProject = (Project) newRel;
          final List<RexNode> newProjections = new ArrayList<>();
          for (RexNode rexNode : oldProject.getProjects()) {
            newProjections.add(new PrestoRexConverter(oldProject.getCluster().getRexBuilder()).apply(rexNode));
          }
          return LogicalProject.create(oldProject.getInput(), newProjections, oldProject.getRowType().getFieldNames());
        }
        return newRel;
      }
    };
    return converter.visit(calciteNode);
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

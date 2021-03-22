/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.Project;
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
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * RelShuttle to convert Hive relnode to standardized representation.
 * What is "standard" is vague right now.
 * TODO: Define "standard".
 */
public class HiveRelConverter extends RelShuttleImpl {

  public RelNode convert(RelNode relNode) {
    return relNode.accept(this);
  }

  @Override
  public RelNode visit(LogicalAggregate aggregate) {
    return super.visit(aggregate).accept(getRexTransformer(aggregate));
  }

  @Override
  public RelNode visit(LogicalMatch match) {
    return super.visit(match).accept(getRexTransformer(match));
  }

  @Override
  public RelNode visit(TableScan scan) {
    return super.visit(scan).accept(getRexTransformer(scan));
  }

  @Override
  public RelNode visit(TableFunctionScan scan) {
    return super.visit(scan).accept(getRexTransformer(scan));
  }

  @Override
  public RelNode visit(LogicalValues values) {
    return super.visit(values).accept(getRexTransformer(values));
  }

  @Override
  public RelNode visit(LogicalFilter filter) {
    return super.visit(filter).accept(getRexTransformer(filter));
  }

  @Override
  public RelNode visit(LogicalJoin join) {
    return super.visit(join).accept(getRexTransformer(join));
  }

  @Override
  public RelNode visit(LogicalCorrelate correlate) {
    return super.visit(correlate).accept(getRexTransformer(correlate));
  }

  @Override
  public RelNode visit(LogicalUnion union) {
    return super.visit(union).accept(getRexTransformer(union));
  }

  @Override
  public RelNode visit(LogicalIntersect intersect) {
    return super.visit(intersect).accept(getRexTransformer(intersect));
  }

  @Override
  public RelNode visit(LogicalMinus minus) {
    return super.visit(minus).accept(getRexTransformer(minus));
  }

  @Override
  public RelNode visit(LogicalSort sort) {
    return super.visit(sort).accept(getRexTransformer(sort));
  }

  @Override
  public RelNode visit(LogicalExchange exchange) {
    return super.visit(exchange).accept(getRexTransformer(exchange));
  }

  @Override
  public RelNode visit(RelNode other) {
    return super.visit(other).accept(getRexTransformer(other));
  }

  private HiveRexConverter getRexTransformer(RelNode rel) {
    return new HiveRexConverter(rel.getCluster().getRexBuilder());
  }

  @Override
  public RelNode visit(LogicalProject project) {
    Project oldProject = (Project) super.visit(project);
    HiveRexConverter rexTransformer = new HiveRexConverter(project.getCluster().getRexBuilder());
    List<RexNode> newProjects = project.getProjects().stream().map(rexTransformer::apply).collect(Collectors.toList());
    return LogicalProject.create(oldProject.getInput(), newProjects, oldProject.getRowType().getFieldNames());
  }

  static class HiveRexConverter extends RexShuttle {

    private final RexBuilder rexBuilder;

    HiveRexConverter(RexBuilder rexBuilder) {
      this.rexBuilder = rexBuilder;
    }

    @Override
    public RexNode visitCall(RexCall inputCall) {
      RexNode call = super.visitCall(inputCall);
      if (call instanceof RexCall) {
        call = convertToOneBasedArrayIndex((RexCall) call);
      }
      return call;
    }

    // convert array[i] to array[i+1] to ensure array indexes start at 1.
    private RexNode convertToOneBasedArrayIndex(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.ITEM)) {
        RexNode columnRef = call.getOperands().get(0);
        RexNode itemRef = call.getOperands().get(1);
        if (columnRef.getType() instanceof ArraySqlType) {
          if (itemRef.isA(SqlKind.LITERAL) && itemRef.getType().getSqlTypeName().equals(SqlTypeName.INTEGER)) {
            Integer val = ((RexLiteral) itemRef).getValueAs(Integer.class);
            RexLiteral newItemRef = rexBuilder.makeExactLiteral(new BigDecimal(val + 1), itemRef.getType());
            return rexBuilder.makeCall(call.op, columnRef, newItemRef);
          } else {
            RexNode oneBasedIndex =
                rexBuilder.makeCall(SqlStdOperatorTable.PLUS, itemRef, rexBuilder.makeExactLiteral(BigDecimal.ONE));
            return rexBuilder.makeCall(call.op, columnRef, oneBasedIndex);
          }
        }
      }
      return call;
    }
  }
}

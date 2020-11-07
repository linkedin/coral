/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamArrayFlatten;
import com.linkedin.beam.planner.BeamConvention;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;


public class BeamArrayFlattenRule extends ConverterRule {

  public BeamArrayFlattenRule() {
    super(LogicalCorrelate.class, Convention.NONE, BeamConvention.INSTANCE, "BeamArrayFlattenRule");
  }

  /**
   * That rule only applies for Pig DataBag flatten, which is translated as correlate operator
   * in Calcite logical plan.
   */
  @Override
  public RelNode convert(RelNode rel) {
    final RelDataTypeFactory typeFactory = rel.getCluster().getTypeFactory();
    final LogicalCorrelate correlate = (LogicalCorrelate) rel;
    final RelNode left = getLeft(correlate);
    if (!((left instanceof Project || left instanceof TableScan) && getRight(correlate) instanceof Uncollect)) {
      unsupported();
    }

    final RelDataType leftType = getLeft(correlate).getRowType();
    final List<RelDataType> resultTypes = new ArrayList<>();
    final List<String> resultNames = new ArrayList<>();
    for (RelDataTypeField field : leftType.getFieldList()) {
      resultTypes.add(field.getType());
      resultNames.add(field.getName());
    }

    final Uncollect right = (Uncollect) getRight(correlate);
    if (!(getInput(right) instanceof Project)) {
      unsupported();
    }
    final Project rightInput = (Project) getInput(right);
    final List<String> flattenCols = new ArrayList<>();
    for (RexNode rex : rightInput.getChildExps()) {
      if (rex.getKind() != SqlKind.FIELD_ACCESS) {
        unsupported();
      }
      final RexFieldAccess fieldAccess = (RexFieldAccess) rex;
      final RelDataTypeField leftField = leftType.getField(fieldAccess.getField().getName(), true, false);
      if (fieldAccess.getReferenceExpr().getKind() != SqlKind.CORREL_VARIABLE
          || leftField == null
          || leftField.getType().getComponentType() == null) {
        unsupported();
      }
      flattenCols.add(leftField.getName());
      final RelDataType componentType = leftField.getType().getComponentType();
      if (componentType.isStruct()) {
        for (RelDataTypeField field : componentType.getFieldList()) {
          resultTypes.add(field.getType());
          resultNames.add(field.getName());
        }
      } else {
        resultTypes.add(componentType);
        resultNames.add(correlate.getRowType().getFieldNames().get(resultTypes.size() - 1));
      }
    }

    return BeamArrayFlatten.create(
        convert(correlate.getLeft(), correlate.getLeft().getTraitSet().replace(BeamConvention.INSTANCE)),
        flattenCols,
        typeFactory.createStructType(resultTypes, resultNames));
  }

  private RelNode getLeft(Correlate correlate) {
    if (correlate.getLeft() instanceof RelSubset) {
      return ((RelSubset) correlate.getLeft()).getOriginal();
    }
    return correlate.getLeft();
  }

  private RelNode getRight(Correlate correlate) {
    if (correlate.getRight() instanceof RelSubset) {
      return ((RelSubset) correlate.getRight()).getOriginal();
    }
    return correlate.getRight();
  }

  private RelNode getInput(Uncollect uncollect) {
    if (uncollect.getInput() instanceof RelSubset) {
      return ((RelSubset) uncollect.getInput()).getOriginal();
    }
    return uncollect.getInput();
  }

  private void unsupported() {
    throw new UnsupportedOperationException("Not supporting correlates other than Array Flatten");
  }
}

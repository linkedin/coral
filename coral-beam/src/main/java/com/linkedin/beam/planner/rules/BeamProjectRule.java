/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamProject;
import com.linkedin.beam.planner.BeamConvention;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalProject;

public class BeamProjectRule extends ConverterRule {
  public BeamProjectRule() {
    super(LogicalProject.class, RelOptUtil.PROJECT_PREDICATE, Convention.NONE,
        BeamConvention.INSTANCE, "ScanProjectRule");
  }

  @Override
  public RelNode convert(RelNode rel) {
    final LogicalProject project = (LogicalProject) rel;
    return BeamProject.create(
        convert(project.getInput(),
            project.getInput().getTraitSet()
                .replace(BeamConvention.INSTANCE)),
        project.getProjects(),
        project.getRowType());
  }
}

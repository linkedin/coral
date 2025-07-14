/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.ArrayList;
import java.util.List;

import com.linkedin.relocated.org.apache.calcite.linq4j.Ord;
import com.linkedin.relocated.org.apache.calcite.plan.Convention;
import com.linkedin.relocated.org.apache.calcite.plan.RelOptCluster;
import com.linkedin.relocated.org.apache.calcite.plan.RelOptTable;
import com.linkedin.relocated.org.apache.calcite.plan.RelOptUtil;
import com.linkedin.relocated.org.apache.calcite.prepare.Prepare;
import com.linkedin.relocated.org.apache.calcite.rel.RelCollation;
import com.linkedin.relocated.org.apache.calcite.rel.RelCollations;
import com.linkedin.relocated.org.apache.calcite.rel.RelNode;
import com.linkedin.relocated.org.apache.calcite.rel.RelRoot;
import com.linkedin.relocated.org.apache.calcite.rel.core.Uncollect;
import com.linkedin.relocated.org.apache.calcite.rel.logical.LogicalValues;
import com.linkedin.relocated.org.apache.calcite.rel.metadata.JaninoRelMetadataProvider;
import com.linkedin.relocated.org.apache.calcite.rel.metadata.RelMetadataQuery;
import com.linkedin.relocated.org.apache.calcite.rel.type.RelDataType;
import com.linkedin.relocated.org.apache.calcite.rex.RexNode;
import com.linkedin.relocated.org.apache.calcite.sql.SqlCall;
import com.linkedin.relocated.org.apache.calcite.sql.SqlExplainFormat;
import com.linkedin.relocated.org.apache.calcite.sql.SqlExplainLevel;
import com.linkedin.relocated.org.apache.calcite.sql.SqlKind;
import com.linkedin.relocated.org.apache.calcite.sql.SqlNode;
import com.linkedin.relocated.org.apache.calcite.sql.SqlUnnestOperator;
import com.linkedin.relocated.org.apache.calcite.sql.validate.SqlValidator;
import com.linkedin.relocated.org.apache.calcite.sql2rel.SqlRexConvertletTable;
import com.linkedin.relocated.org.apache.calcite.sql2rel.SqlToRelConverter;

import com.linkedin.coral.common.HiveUncollect;
import com.linkedin.coral.hive.hive2rel.functions.HiveExplodeOperator;


/**
 * Class to convert Trino SQL to Calcite RelNode. This class
 * specializes the functionality provided by {@link SqlToRelConverter}.
 */
class TrinoSqlToRelConverter extends SqlToRelConverter {

  TrinoSqlToRelConverter(RelOptTable.ViewExpander viewExpander, SqlValidator validator,
      Prepare.CatalogReader catalogReader, RelOptCluster cluster, SqlRexConvertletTable convertletTable,
      Config config) {
    super(viewExpander, validator, catalogReader, cluster, convertletTable, config);
  }

  // This differs from base class in two ways:
  // 1. This does not validate the type of converted rel rowType with that of validated node. This is because
  //    hive is lax in enforcing view schemas.
  // 2. This skips calling some methods because (1) those are private, and (2) not required for our usecase
  public RelRoot convertQuery(SqlNode query, final boolean needsValidation, final boolean top) {
    if (needsValidation) {
      query = validator.validate(query);
    }

    RelMetadataQuery.THREAD_PROVIDERS.set(JaninoRelMetadataProvider.of(cluster.getMetadataProvider()));
    RelNode result = convertQueryRecursive(query, top, null).rel;
    RelCollation collation = RelCollations.EMPTY;

    if (SQL2REL_LOGGER.isDebugEnabled()) {
      SQL2REL_LOGGER.debug(RelOptUtil.dumpPlan("Plan after converting SqlNode to RelNode", result,
          SqlExplainFormat.TEXT, SqlExplainLevel.EXPPLAN_ATTRIBUTES));
    }

    final RelDataType validatedRowType = validator.getValidatedNodeType(query);
    return RelRoot.of(result, validatedRowType, query.getKind()).withCollation(collation);
  }

  @Override
  protected void convertFrom(Blackboard bb, SqlNode from) {
    if (from == null) {
      super.convertFrom(bb, from);
      return;
    }
    switch (from.getKind()) {
      case UNNEST:
        convertUnnestFrom(bb, from);
        break;
      default:
        super.convertFrom(bb, from);
        break;
    }
  }

  private void convertUnnestFrom(Blackboard bb, SqlNode from) {
    final SqlCall call;
    call = (SqlCall) from;
    final List<SqlNode> nodes = call.getOperandList();
    final SqlUnnestOperator operator = (SqlUnnestOperator) call.getOperator();
    // FIXME: base class calls 'replaceSubqueries for operands here but that's a private
    // method. This is not an issue for our usecases with hive but we may need handling in future
    final List<RexNode> exprs = new ArrayList<>();
    final List<String> fieldNames = new ArrayList<>();
    for (Ord<SqlNode> node : Ord.zip(nodes)) {
      exprs.add(bb.convertExpression(node.e));
      // In Hive, "LATERAL VIEW EXPLODE(arr) t" is equivalent to "LATERAL VIEW EXPLODE(arr) t AS col".
      // Use the default column name "col" if not specified.
      fieldNames.add(node.e.getKind() == SqlKind.AS ? validator.deriveAlias(node.e, node.i)
          : HiveExplodeOperator.ARRAY_ELEMENT_COLUMN_NAME);
    }
    final RelNode input = RelOptUtil.createProject((null != bb.root) ? bb.root : LogicalValues.createOneRow(cluster),
        exprs, fieldNames, true);
    Uncollect uncollect =
        new HiveUncollect(cluster, cluster.traitSetOf(Convention.NONE), input, operator.withOrdinality);
    bb.setRoot(uncollect, true);
  }
}

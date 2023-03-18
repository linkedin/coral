/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.differential;

import java.util.List;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.spark.dialect.SparkSqlDialect;


public class RelToDiffSqlConverter extends RelToSqlConverter {

  /**
   * Creates a RelToDiffSqlConvert with dialect set to Spark.
   */
  public RelToDiffSqlConverter() {
    super(SparkSqlDialect.INSTANCE);
  }

  public String convert(RelNode relNode) {
    RelNode modifiedRelNode = RelDifferentialTransformer.convertRelDifferential(relNode);
    SqlNode sqlNode = visitChild(0, modifiedRelNode).asStatement();
    return sqlNode.toSqlString(SparkSqlDialect.INSTANCE).getSql();
  }

  @Override
  public Result visit(TableScan e) {
    List<String> qualifiedName = e.getTable().getQualifiedName();
    if (qualifiedName.size() > 2) {
      qualifiedName = qualifiedName.subList(qualifiedName.size() - 2, qualifiedName.size()); // take last two entries
    }
    final SqlIdentifier identifier = new SqlIdentifier(qualifiedName, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

}

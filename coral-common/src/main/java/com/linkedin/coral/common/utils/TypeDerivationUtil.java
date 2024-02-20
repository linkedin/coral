/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlValidator;


/**
 * This is a utility class which helps derive the RelDataType of a given SqlNode.
 */
public class TypeDerivationUtil {
  private final SqlValidator sqlValidator;
  private final List<SqlSelect> topSelectNodes = new ArrayList<>();

  public TypeDerivationUtil(SqlValidator sqlValidator, SqlNode topSqlNode) {
    this.sqlValidator = sqlValidator;
    topSqlNode.accept(new SqlNodePreprocessorForTypeDerivation());
  }

  /**
   * This method derives the RelDataType for a given SqlNode.
   * It constructs a dummy SqlSelect object with the passed SqlNode as a field and iterates over each of the top-level SELECT statements.
   * If the SqlNode passes validation with the SqlValidator instance, then the method returns the validated node's RelDataType.
   * If the method is unable to derive the RelDataType for the given SqlNode, it throws a RuntimeException.
   *
   * The method uses a list named 'topSelectNodes', to store all the top-level SqlSelects from the original "topSqlNode".
   * It does this to handle data type derivation in SQL statements that use a UNION ALL operator.
   * Since a UNION ALL operator cannot be represented as a unified SQL Select statement, it is necessary to use a list
   * to store both SELECT statements separately. Example SQL:
   *
   * SELECT *
   * FROM `test`.`tablea`
   * UNION ALL
   * SELECT *
   * FROM `test`.`tableb`
   * WHERE `concat`(CURRENT_DATE, `tableb`.`a`) = 'some string'
   *
   * @param sqlNode The SqlNode for which the RelDataType needs to be derived.
   * @return The RelDataType derived for the given SqlNode.
   * @throws RuntimeException if the RelDataType cannot be derived for the given SqlNode.
   */
  public RelDataType getRelDataType(SqlNode sqlNode) {
    if (sqlValidator == null) {
      throw new RuntimeException("SqlValidator does not exist to derive the RelDataType for SqlNode: " + sqlNode);
    }

    RelDataType fromNodeDataType = sqlValidator.getValidatedNodeTypeIfKnown(sqlNode);
    if (fromNodeDataType != null) {
      return fromNodeDataType;
    }

    for (SqlSelect topSqlSelectNode : topSelectNodes) {
      final SqlSelect dummySqlSelect = new SqlSelect(topSqlSelectNode.getParserPosition(), null,
          SqlNodeList.of(sqlNode), topSqlSelectNode.getFrom(), topSqlSelectNode.getWhere(), topSqlSelectNode.getGroup(),
          topSqlSelectNode.getHaving(), topSqlSelectNode.getWindowList(), topSqlSelectNode.getOrderList(),
          topSqlSelectNode.getOffset(), topSqlSelectNode.getFetch());

      try {
        sqlValidator.validate(dummySqlSelect);
        return sqlValidator.getValidatedNodeType(dummySqlSelect).getFieldList().get(0).getType();
      } catch (Throwable ignored) {
      }
    }

    // Additional attempt to derive RelDataType of the input sqlNode by validating the topSelectNode.
    // This is particularly useful when the input is defined as an alias in topSelectNode's selectList.
    // For example, when the topSelectNode is: `SELECT a AS tmp FROM foo WHERE tmp > 5`.
    // Previous attempts would try validating dummySqlNode: `SELECT tmp FROM foo WHERE tmp > 5`, which would fail RelDataType derivation.
    try {
      final SqlSelect dummySqlSelect = new SqlSelect(topSelectNodes.get(0).getParserPosition(), null,
          SqlNodeList.of(sqlNode), topSelectNodes.get(0), null, null, null, null, null, null, null);
      sqlValidator.validate(dummySqlSelect);
      return sqlValidator.getValidatedNodeType(sqlNode);
    } catch (Throwable ignored) {
    }

    throw new RuntimeException(String.format("Failed to derive the RelDataType for SqlNode: %s with topSqlNode: %s",
        sqlNode, topSelectNodes.get(0)));
  }

  public RelDataType leastRestrictive(List<RelDataType> types) {
    return sqlValidator.getTypeFactory().leastRestrictive(types);
  }

  private class SqlNodePreprocessorForTypeDerivation extends SqlShuttle {
    @Override
    public SqlNode visit(SqlCall sqlCall) {
      if (sqlCall instanceof SqlSelect) {
        // Updates selectList of a SqlSelect node
        // to correctly handle t.* type nodes for accurate data type derivation
        if (((SqlSelect) sqlCall).getSelectList() == null) {
          List<String> names = new ArrayList<>();
          names.add("*");
          List<SqlParserPos> sqlParserPos = Collections.nCopies(names.size(), SqlParserPos.ZERO);
          SqlNode star = SqlIdentifier.star(names, SqlParserPos.ZERO, sqlParserPos);
          ((SqlSelect) sqlCall).setSelectList(SqlNodeList.of(star));
        }
        topSelectNodes.add((SqlSelect) sqlCall);
      }
      return super.visit(sqlCall);
    }
  }
}

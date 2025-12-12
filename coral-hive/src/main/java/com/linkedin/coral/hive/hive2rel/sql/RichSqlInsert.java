/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.sql;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlInsertKeyword;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;


/** A {@link SqlInsert} that have some extension functions like partition, overwrite. * */
public class RichSqlInsert extends SqlInsert {
  private SqlNodeList partitions;

  private SqlNodeList extendedKeywords;

  public RichSqlInsert(SqlParserPos pos, SqlNodeList keywords, SqlNode targetTable, SqlNode source,
      SqlNodeList columnList, SqlNodeList extendedKeywords, SqlNodeList partitions) {
    super(pos, keywords, targetTable, source, columnList);
    this.extendedKeywords = extendedKeywords;
    this.partitions = partitions;
  }

  /**
   * @return the list of partition key-value pairs, returns empty if there is no partition
   *     specifications.
   */
  public SqlNodeList getStaticPartitions() {
    return partitions;
  }

  /**
   * Get static partition key value pair.
   *
   *
   * @return the mapping of column names to values of partition specifications, returns an empty
   *     map if there is no partition specifications.
   */
  public LinkedHashMap<SqlIdentifier, SqlLiteral> getStaticPartitionKVs() {
    LinkedHashMap<SqlIdentifier, SqlLiteral> ret = new LinkedHashMap<>();
    if (this.partitions.size() == 0) {
      return ret;
    }
    for (SqlNode node : this.partitions.getList()) {
      if (node instanceof SqlIdentifier) {
        continue;
      }
      SqlCall call = (SqlCall) node;
      assert call.getOperator() == SqlStdOperatorTable.EQUALS;
      assert call.getOperandList().size() == 2;
      SqlIdentifier key = (SqlIdentifier) call.getOperandList().get(0);
      SqlLiteral value = (SqlLiteral) call.getOperandList().get(1);
      ret.put(key, value);
    }
    return ret;
  }

  @Override
  public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    writer.startList(SqlWriter.FrameTypeEnum.SELECT);
    String insertKeyword = "INSERT INTO";
    if (isUpsert()) {
      insertKeyword = "UPSERT INTO";
    } else if (isOverwrite()) {
      insertKeyword = "INSERT OVERWRITE";
    }
    writer.sep(insertKeyword);
    final int opLeft = getOperator().getLeftPrec();
    final int opRight = getOperator().getRightPrec();
    getTargetTable().unparse(writer, opLeft, opRight);
    if (partitions != null && partitions.size() > 0) {
      writer.keyword("PARTITION");
      partitions.unparse(writer, opLeft, opRight);
      writer.newlineAndIndent();
    }
    if (getTargetColumnList() != null) {
      getTargetColumnList().unparse(writer, opLeft, opRight);
    }
    writer.newlineAndIndent();
    getSource().unparse(writer, 0, 0);
  }

  @Override
  public void setOperand(int i, SqlNode operand) {
    if (i < 4) {
      super.setOperand(i, operand);
      return;
    }
    switch (i) {
      case 4:
        this.extendedKeywords = (SqlNodeList) operand;
        break;
      case 5:
        this.partitions = (SqlNodeList) operand;
        break;
      default:
        throw new AssertionError(i);
    }

  }

  // ~ Tools ------------------------------------------------------------------

  public static boolean isUpsert(List<SqlLiteral> keywords) {
    for (SqlNode keyword : keywords) {
      SqlInsertKeyword keyword2 = ((SqlLiteral) keyword).symbolValue(SqlInsertKeyword.class);
      if (keyword2 == SqlInsertKeyword.UPSERT) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns whether the insert mode is overwrite (for whole table or for specific partitions).
   *
   * @return true if this is overwrite mode
   */
  public boolean isOverwrite() {
    return getModifierNode(RichSqlInsertKeyword.OVERWRITE) != null;
  }

  private SqlNode getModifierNode(RichSqlInsertKeyword modifier) {
    for (SqlNode keyword : extendedKeywords) {
      RichSqlInsertKeyword keyword2 = ((SqlLiteral) keyword).symbolValue(RichSqlInsertKeyword.class);
      if (keyword2 == modifier) {
        return keyword;
      }
    }
    return null;
  }
}

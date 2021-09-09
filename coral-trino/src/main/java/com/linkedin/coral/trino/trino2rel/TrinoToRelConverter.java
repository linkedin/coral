/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import com.linkned.coral.common.HiveMetastoreClient;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.lang3.StringUtils;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;

import io.trino.sql.parser.ParsingOptions;
import io.trino.sql.parser.SqlParser;
import io.trino.sql.tree.Node;
import io.trino.sql.tree.Statement;


public class TrinoToRelConverter {
  public static void main(String[] args) {
    Statement statement = new SqlParser().createStatement("SELECT 1", new ParsingOptions());
    printTrinoTree(statement, 0);
  }

  public static TrinoToRelConverter create(HiveMetastoreClient mscClient) {
    HiveToRelConverter hiveToRelConverter = HiveToRelConverter.create(mscClient);
    SqlNode sqlNode =
        new TrinoAstVisitor().process(new SqlParser().createStatement("SELECT 1", new ParsingOptions()), null);
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    RelNode relNode = hiveToRelConverter.toRel(sqlNode);
    System.out.print(relToTrinoConverter.convert(relNode));
    return null;
  }

  static void printTrinoTree(Node node, int indent) {
    System.out.println(StringUtils.repeat(' ', indent) + node.toString() + "(" + node.getClass().getSimpleName() + ")");
    for (Node child : node.getChildren()) {
      printTrinoTree(child, indent + 1);
    }
  }

}

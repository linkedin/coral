/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;


public class TrinoSqlDialect extends SqlDialect {
  private static final String IDENTIFIER_QUOTE_STRING = "\"";

  public static final TrinoSqlDialect INSTANCE =
      new TrinoSqlDialect(emptyContext().withDatabaseProduct(DatabaseProduct.UNKNOWN).withDatabaseProductName("Trino")
          .withIdentifierQuoteString(IDENTIFIER_QUOTE_STRING).withNullCollation(NullCollation.HIGH));

  private TrinoSqlDialect(Context context) {
    super(context);
  }

  /**
   * Override this method so that so that table alias is prepended to all field references (e.g., "table.column"
   * or "table.struct.filed" instead of "column" or "struct.field"), which is necessary for
   * data type derivation on struct fields.
   *
   * For the following input SQL:
   * CREATE TABLE db.tbl(s struct(name:string, age:int));
   * SELECT split(tbl.s.name, ' ') name_array
   * FROM db.tbl
   * WHERE tbl.s.age = 25;
   *
   * The input RelNode is:
   * LogicalProject(name_array=[split($0.name, ' ')])
   *   LogicalFilter(condition=[=($0.age, 25)])
   *     LogicalTableScan(table=[[hive, db, tbl]])
   *
   * With this override, the generated SqlNode is:
   * SELECT `split`(`tbl`.`s`.`name`, ' ') AS `name_array`
   * FROM `db`.`tbl` AS `tbl`
   * WHERE `tbl`.`s`.`age` = 25
   *
   * Without this override, the generated SqlNode is:
   * SELECT `split`(`s`.`name`, ' ') AS `name_array`
   * FROM `db`.`tbl`
   * WHERE `s`.`age` = 25
   *
   * Without this override, if we want to get the data type of a struct field like `s`.`name`, validation will fail
   * because Calcite uses {@link org.apache.calcite.rel.type.StructKind#FULLY_QUALIFIED} for standard SQL and it
   * expects each field inside the struct to be referenced explicitly with table alias in the method
   * {@link org.apache.calcite.sql.validate.DelegatingScope#fullyQualify(SqlIdentifier)}.
   * Therefore, we need to generate `complex`.`s`.`name` with this override, which will also affect other non-struct
   * fields and add table alias in `FROM` clause, but it won't affect the SQL correctness.
   */
  @Override
  public boolean hasImplicitTableAlias() {
    return false;
  }

  public void unparseOffsetFetch(SqlWriter writer, SqlNode offset, SqlNode fetch) {
    unparseFetchUsingLimit(writer, offset, fetch);
  }

  @Override
  public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    switch (call.getOperator().kind) {
      case MAP_VALUE_CONSTRUCTOR:
        unparseMapValueConstructor(writer, call, leftPrec, rightPrec);
        break;
      default:
        super.unparseCall(writer, call, leftPrec, rightPrec);
    }
  }

  @Override
  public String quoteIdentifier(String name) {
    // Assume that quote string is not allowed in Trino SQL identifiers
    if (name.contains(IDENTIFIER_QUOTE_STRING)) {
      // This mean the identifiers within the name were quoted before.
      return name;
    }
    return IDENTIFIER_QUOTE_STRING + name + IDENTIFIER_QUOTE_STRING;
  }

  @Override
  public void unparseIdentifier(SqlWriter writer, SqlIdentifier identifier) {
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.IDENTIFIER);
    for (int i = 0; i < identifier.names.size(); i++) {
      writer.sep(".");
      final String name = identifier.names.get(i);
      final SqlParserPos pos = identifier.getComponentParserPosition(i);
      if (name.equals("")) {
        writer.print("*");
      } else {
        writer.identifier(name, pos.isQuoted());
      }
    }
    writer.endList(frame);
  }

  public boolean requireCastOnString() {
    return true;
  }

  private void unparseMapValueConstructor(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    writer.keyword(call.getOperator().getName()); // "MAP"
    final SqlWriter.Frame frame = writer.startList("(", ")"); // not "[" and "]"
    for (int i = 0; i < call.operandCount(); i++) {
      writer.sep(",");
      call.operand(i).unparse(writer, leftPrec, rightPrec);
    }
    writer.endList(frame);
  }
}

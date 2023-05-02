/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.RelShuttleImpl;
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
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.trino.rel2trino.functions.GenericProjectToTrinoConverter;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;
import static org.apache.calcite.sql.fun.SqlStdOperatorTable.MULTIPLY;
import static org.apache.calcite.sql.type.ReturnTypes.explicit;
import static org.apache.calcite.sql.type.SqlTypeName.*;


public class Calcite2TrinoUDFConverter {
  private Calcite2TrinoUDFConverter() {
  }

  /**
   * Replaces Calcite SQL operators with Trino UDF to obtain the Trino-compatible Calcite plan.
   *
   * @param calciteNode Original Calcite plan
   * @return Trino-compatible Calcite plan
   */
  public static RelNode convertRel(RelNode calciteNode, Map<String, Boolean> configs) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(LogicalProject project) {
        return super.visit(project).accept(getTrinoRexConverter(project));
      }

      @Override
      public RelNode visit(LogicalFilter inputFilter) {
        return super.visit(inputFilter).accept(getTrinoRexConverter(inputFilter));
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        return super.visit(aggregate).accept(getTrinoRexConverter(aggregate));
      }

      @Override
      public RelNode visit(LogicalMatch match) {
        return super.visit(match).accept(getTrinoRexConverter(match));
      }

      @Override
      public RelNode visit(TableScan scan) {
        return super.visit(scan).accept(getTrinoRexConverter(scan));
      }

      @Override
      public RelNode visit(TableFunctionScan scan) {
        return super.visit(scan).accept(getTrinoRexConverter(scan));
      }

      @Override
      public RelNode visit(LogicalValues values) {
        return super.visit(values).accept(getTrinoRexConverter(values));
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        return super.visit(join).accept(getTrinoRexConverter(join));
      }

      @Override
      public RelNode visit(LogicalCorrelate correlate) {
        return super.visit(correlate).accept(getTrinoRexConverter(correlate));
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        return super.visit(union).accept(getTrinoRexConverter(union));
      }

      @Override
      public RelNode visit(LogicalIntersect intersect) {
        return super.visit(intersect).accept(getTrinoRexConverter(intersect));
      }

      @Override
      public RelNode visit(LogicalMinus minus) {
        return super.visit(minus).accept(getTrinoRexConverter(minus));
      }

      @Override
      public RelNode visit(LogicalSort sort) {
        return super.visit(sort).accept(getTrinoRexConverter(sort));
      }

      @Override
      public RelNode visit(LogicalExchange exchange) {
        return super.visit(exchange).accept(getTrinoRexConverter(exchange));
      }

      @Override
      public RelNode visit(RelNode other) {
        return super.visit(other).accept(getTrinoRexConverter(other));
      }

      private TrinoRexConverter getTrinoRexConverter(RelNode node) {
        return new TrinoRexConverter(node, configs);
      }
    };
    return calciteNode.accept(converter);
  }

  /**
   * For replacing a certain Calcite SQL operator with Trino UDFs in a relational expression
   */
  public static class TrinoRexConverter extends RexShuttle {
    private final RexBuilder rexBuilder;
    private final RelDataTypeFactory typeFactory;
    private final RelNode node;
    private final Map<String, Boolean> configs;

    public TrinoRexConverter(RelNode node, Map<String, Boolean> configs) {
      this.rexBuilder = node.getCluster().getRexBuilder();
      this.typeFactory = node.getCluster().getTypeFactory();
      this.configs = configs;
      this.node = node;
    }

    @Override
    public RexNode visitCall(RexCall call) {
      // GenericProject requires a nontrivial function rewrite because of the following:
      //   - makes use of Trino built-in UDFs transform_values for map objects and transform for array objects
      //     which has lambda functions as parameters
      //     - syntax is difficult for Calcite to parse
      //   - the return type varies based on a desired schema to be projected
      if (call.getOperator() instanceof GenericProjectFunction) {
        return GenericProjectToTrinoConverter.convertGenericProject(rexBuilder, call, node);
      }

      final String operatorName = call.getOperator().getName();

      if (operatorName.equalsIgnoreCase("from_utc_timestamp")) {
        Optional<RexNode> modifiedCall = visitFromUtcTimestampCall(call);
        if (modifiedCall.isPresent()) {
          return modifiedCall.get();
        }
      }

      if (operatorName.equalsIgnoreCase("from_unixtime")) {
        Optional<RexNode> modifiedCall = visitFromUnixtime(call);
        if (modifiedCall.isPresent()) {
          return modifiedCall.get();
        }
      }

      if (operatorName.equalsIgnoreCase("cast")) {
        Optional<RexNode> modifiedCall = visitCast(call);
        if (modifiedCall.isPresent()) {
          return modifiedCall.get();
        }
      }

      if (operatorName.equalsIgnoreCase("substr")) {
        Optional<RexNode> modifiedCall = visitSubstring(call);
        if (modifiedCall.isPresent()) {
          return modifiedCall.get();
        }
      }

      if (operatorName.equalsIgnoreCase("concat")) {
        Optional<RexNode> modifiedCall = visitConcat(call);
        if (modifiedCall.isPresent()) {
          return modifiedCall.get();
        }
      }

      return super.visitCall(call);
    }

    private Optional<RexNode> visitConcat(RexCall call) {
      // Hive supports operations like CONCAT(date, varchar) while Trino only supports CONCAT(varchar, varchar)
      // So we need to cast the unsupported types to varchar
      final SqlOperator op = call.getOperator();
      List<RexNode> convertedOperands = visitList(call.getOperands(), (boolean[]) null);
      List<RexNode> castOperands = new ArrayList<>();

      for (RexNode inputOperand : convertedOperands) {
        if (inputOperand.getType().getSqlTypeName() != VARCHAR && inputOperand.getType().getSqlTypeName() != CHAR) {
          final RexNode castOperand = rexBuilder.makeCast(typeFactory.createSqlType(VARCHAR), inputOperand);
          castOperands.add(castOperand);
        } else {
          castOperands.add(inputOperand);
        }
      }
      return Optional.of(rexBuilder.makeCall(op, castOperands));
    }

    private Optional<RexNode> visitFromUnixtime(RexCall call) {
      List<RexNode> convertedOperands = visitList(call.getOperands(), (boolean[]) null);
      SqlOperator formatDatetime = createSqlOperatorOfFunction("format_datetime", FunctionReturnTypes.STRING);
      SqlOperator fromUnixtime = createSqlOperatorOfFunction("from_unixtime", explicit(TIMESTAMP));
      if (convertedOperands.size() == 1) {
        return Optional
            .of(rexBuilder.makeCall(formatDatetime, rexBuilder.makeCall(fromUnixtime, call.getOperands().get(0)),
                rexBuilder.makeLiteral("yyyy-MM-dd HH:mm:ss")));
      } else if (convertedOperands.size() == 2) {
        return Optional.of(rexBuilder.makeCall(formatDatetime,
            rexBuilder.makeCall(fromUnixtime, call.getOperands().get(0)), call.getOperands().get(1)));
      }
      return Optional.empty();
    }

    private Optional<RexNode> visitFromUtcTimestampCall(RexCall call) {
      RelDataType inputType = call.getOperands().get(0).getType();
      // TODO(trinodb/trino#6295) support high-precision timestamp
      RelDataType targetType = typeFactory.createSqlType(TIMESTAMP, 3);

      List<RexNode> convertedOperands = visitList(call.getOperands(), (boolean[]) null);
      RexNode sourceValue = convertedOperands.get(0);
      RexNode timezone = convertedOperands.get(1);

      // In below definitions we should use `TIMESTATMP WITH TIME ZONE`. As calcite is lacking
      // this type we use `TIMESTAMP` instead. It does not have any practical implications as result syntax tree
      // is not type-checked, and only used for generating output SQL for a view query.
      SqlOperator trinoAtTimeZone =
          createSqlOperatorOfFunction("at_timezone", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
      SqlOperator trinoWithTimeZone =
          createSqlOperatorOfFunction("with_timezone", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
      SqlOperator trinoToUnixTime = createSqlOperatorOfFunction("to_unixtime", explicit(DOUBLE));
      SqlOperator trinoFromUnixtimeNanos =
          createSqlOperatorOfFunction("from_unixtime_nanos", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
      SqlOperator trinoFromUnixTime =
          createSqlOperatorOfFunction("from_unixtime", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
      SqlOperator trinoCanonicalizeHiveTimezoneId =
          createSqlOperatorOfFunction("$canonicalize_hive_timezone_id", explicit(VARCHAR));

      RelDataType bigintType = typeFactory.createSqlType(BIGINT);
      RelDataType doubleType = typeFactory.createSqlType(DOUBLE);

      if (inputType.getSqlTypeName() == BIGINT || inputType.getSqlTypeName() == INTEGER
          || inputType.getSqlTypeName() == SMALLINT || inputType.getSqlTypeName() == TINYINT) {

        return Optional.of(rexBuilder.makeCast(targetType,
            rexBuilder.makeCall(trinoAtTimeZone,
                rexBuilder.makeCall(trinoFromUnixtimeNanos,
                    rexBuilder.makeCall(MULTIPLY, rexBuilder.makeCast(bigintType, sourceValue),
                        rexBuilder.makeBigintLiteral(BigDecimal.valueOf(1000000)))),
                rexBuilder.makeCall(trinoCanonicalizeHiveTimezoneId, timezone))));
      }

      if (inputType.getSqlTypeName() == DOUBLE || inputType.getSqlTypeName() == FLOAT
          || inputType.getSqlTypeName() == DECIMAL) {

        return Optional.of(rexBuilder.makeCast(targetType,
            rexBuilder.makeCall(trinoAtTimeZone,
                rexBuilder.makeCall(trinoFromUnixTime, rexBuilder.makeCast(doubleType, sourceValue)),
                rexBuilder.makeCall(trinoCanonicalizeHiveTimezoneId, timezone))));
      }

      if (inputType.getSqlTypeName() == TIMESTAMP || inputType.getSqlTypeName() == DATE) {
        return Optional.of(rexBuilder.makeCast(targetType,
            rexBuilder.makeCall(trinoAtTimeZone,
                rexBuilder.makeCall(trinoFromUnixTime,
                    rexBuilder.makeCall(trinoToUnixTime,
                        rexBuilder.makeCall(trinoWithTimeZone, sourceValue, rexBuilder.makeLiteral("UTC")))),
                rexBuilder.makeCall(trinoCanonicalizeHiveTimezoneId, timezone))));
      }

      return Optional.empty();
    }

    // Hive allows passing in a byte array or String to substr/substring, so we can make an effort to emulate the
    // behavior by casting non-String input to String
    // https://cwiki.apache.org/confluence/display/hive/languagemanual+udf
    private Optional<RexNode> visitSubstring(RexCall call) {
      final SqlOperator op = call.getOperator();
      List<RexNode> convertedOperands = visitList(call.getOperands(), (boolean[]) null);
      RexNode inputOperand = convertedOperands.get(0);

      if (inputOperand.getType().getSqlTypeName() != VARCHAR && inputOperand.getType().getSqlTypeName() != CHAR) {
        List<RexNode> operands = new ImmutableList.Builder<RexNode>()
            .add(rexBuilder.makeCast(typeFactory.createSqlType(VARCHAR), inputOperand))
            .addAll(convertedOperands.subList(1, convertedOperands.size())).build();
        return Optional.of(rexBuilder.makeCall(op, operands));
      }

      return Optional.empty();
    }

    private Optional<RexNode> visitCast(RexCall call) {
      final SqlOperator op = call.getOperator();
      if (op.getKind() != SqlKind.CAST) {
        return Optional.empty();
      }

      List<RexNode> convertedOperands = visitList(call.getOperands(), (boolean[]) null);
      RexNode leftOperand = convertedOperands.get(0);

      // Hive allows for casting of TIMESTAMP to DECIMAL, which converts it to unix time if the decimal format is valid
      // Example: "SELECT cast(current_timestamp() AS decimal(10,0));" -> 1633112585
      // Trino does not allow for such conversion, but we can achieve the same behavior by first calling "to_unixtime"
      // on the TIMESTAMP and then casting it to DECIMAL after.
      if (call.getType().getSqlTypeName() == DECIMAL && leftOperand.getType().getSqlTypeName() == TIMESTAMP) {
        SqlOperator trinoToUnixTime = createSqlOperatorOfFunction("to_unixtime", explicit(DOUBLE));
        SqlOperator trinoWithTimeZone =
            createSqlOperatorOfFunction("with_timezone", explicit(TIMESTAMP /* should be WITH TIME ZONE */));
        return Optional.of(rexBuilder.makeCast(call.getType(), rexBuilder.makeCall(trinoToUnixTime,
            rexBuilder.makeCall(trinoWithTimeZone, leftOperand, rexBuilder.makeLiteral("UTC")))));
      }

      // Trino doesn't allow casting varbinary/binary to varchar/char, we need to use the built-in function `from_utf8`
      // to replace the cast, i.e. CAST(binary AS VARCHAR) -> from_utf8(binary)
      if ((call.getType().getSqlTypeName() == VARCHAR || call.getType().getSqlTypeName() == CHAR)
          && (leftOperand.getType().getSqlTypeName() == VARBINARY
              || leftOperand.getType().getSqlTypeName() == BINARY)) {
        SqlOperator fromUTF8 = createSqlOperatorOfFunction("from_utf8", explicit(VARCHAR));
        return Optional.of(rexBuilder.makeCall(fromUTF8, leftOperand));
      }

      return Optional.empty();
    }
  }

  private static SqlOperator createSqlOperatorOfFunction(String functionName, SqlReturnTypeInference typeInference) {
    SqlIdentifier sqlIdentifier =
        new SqlIdentifier(com.google.common.collect.ImmutableList.of(functionName), SqlParserPos.ZERO);
    return new SqlUserDefinedFunction(sqlIdentifier, typeInference, null, null, null, null);
  }
}

package com.linkedin.coral.spark;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.functions.HiveNamedStructFunction;
import com.linkedin.coral.spark.containers.SparkRelInfo;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;


/**
 * This class applies series of transformations to make a IR RelNode compatible with Spark.
 *
 * It uses Calcite's RelShuttle and RexShuttle to traverse the RelNode Plan.
 * During traversal it identifies and transforms UDFs.
 *      1) Identify UDF if it is defined in [[TansportableUDFMap]] and adds it to a List<SparkUDFInfo>
 *      2) Rewrites UDF name in the RelNode plan
 *        for example: com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds -> epochToEpochMilliseconds
 *
 * Use `transform` to get an instance of [[SparkRelInfo]] which contains the Spark RelNode and SparkUDFInfoList.
 */
class IRRelToSparkRelTransformer {

  private IRRelToSparkRelTransformer() {
  }

  /**
   * This API is used to transforms IR RelNode to make it compatible with spark.
   *
   * @return [[SparkRelInfo]] containing the Spark RelNode and list of standard UDFs.
   *
   */
  static SparkRelInfo transform(RelNode calciteNode) {
    List<SparkUDFInfo> sparkUDFInfos = new ArrayList<>();
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(LogicalProject project) {
        return super.visit(project).accept(getSparkRexConverter(project));
      }

      @Override
      public RelNode visit(LogicalFilter inputFilter) {
        return super.visit(inputFilter).accept(getSparkRexConverter(inputFilter));
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        return super.visit(aggregate).accept(getSparkRexConverter(aggregate));
      }

      @Override
      public RelNode visit(LogicalMatch match) {
        return super.visit(match).accept(getSparkRexConverter(match));
      }

      @Override
      public RelNode visit(TableScan scan) {
        return super.visit(scan).accept(getSparkRexConverter(scan));
      }

      @Override
      public RelNode visit(TableFunctionScan scan) {
        return super.visit(scan).accept(getSparkRexConverter(scan));
      }

      @Override
      public RelNode visit(LogicalValues values) {
        return super.visit(values).accept(getSparkRexConverter(values));
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        return super.visit(join).accept(getSparkRexConverter(join));
      }

      @Override
      public RelNode visit(LogicalCorrelate correlate) {
        return super.visit(correlate).accept(getSparkRexConverter(correlate));
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        return super.visit(union).accept(getSparkRexConverter(union));
      }

      @Override
      public RelNode visit(LogicalIntersect intersect) {
        return super.visit(intersect).accept(getSparkRexConverter(intersect));
      }

      @Override
      public RelNode visit(LogicalMinus minus) {
        return super.visit(minus).accept(getSparkRexConverter(minus));
      }

      @Override
      public RelNode visit(LogicalSort sort) {
        return super.visit(sort).accept(getSparkRexConverter(sort));
      }

      @Override
      public RelNode visit(LogicalExchange exchange) {
        return super.visit(exchange).accept(getSparkRexConverter(exchange));
      }

      @Override
      public RelNode visit(RelNode other) {
        return super.visit(other).accept(getSparkRexConverter(other));
      }

      private SparkRexConverter getSparkRexConverter(RelNode node) {
        return new SparkRexConverter(node.getCluster().getRexBuilder(), sparkUDFInfos);
      }
    };
    return new SparkRelInfo(calciteNode.accept(converter), sparkUDFInfos);
  }


  /**
   * For replacing a UDF SQL operator with a new SQL operator with different name.
   *
   * Consults [[TansportableUDFMap]] to get the new name.
   *
   * for example: com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds -> epochToEpochMilliseconds
   */
  private static class SparkRexConverter extends RexShuttle {
    private final RexBuilder rexBuilder;
    private List<SparkUDFInfo> sparkUDFInfos;

    SparkRexConverter(RexBuilder rexBuilder, List<SparkUDFInfo> sparkUDFInfos) {
      this.sparkUDFInfos = sparkUDFInfos;
      this.rexBuilder = rexBuilder;
    }

    @Override
    public RexNode visitCall(RexCall call) {
      return convertToZeroBasedArrayIndex(call)
          .orElseGet(() -> convertToNamedStruct(call)
          .orElseGet(() -> convertDaliUDF(call)
          .orElseGet(() -> convertBuiltInUDF(call)
          .orElseGet(() -> super.visitCall(call)))));
    }

    private Optional<RexNode> convertDaliUDF(RexCall call) {
      Optional<SparkUDFInfo> sparkUDFInfo = TansportableUDFMap.lookup(call.getOperator().getName());
      sparkUDFInfo.ifPresent(sparkUDFInfos::add);
      return sparkUDFInfo
          .map(sparkUDFInfo1 ->
              rexBuilder.makeCall(
                  createUDF(sparkUDFInfo1.getFunctionName(), call.getOperator().getReturnTypeInference()),
                  call.getOperands())
          );
    }

    private Optional<RexNode> convertBuiltInUDF(RexCall call) {
      return BuiltinUDFMap
          .lookup(call.getOperator().getName())
          .map(name ->
              rexBuilder.makeCall(
                  createUDF(name, call.getOperator().getReturnTypeInference()),
                  call.getOperands())
          );
    }

    // Coral RelNode Stores array indexes as +1, this fixes the behavior on spark side
    private Optional<RexNode> convertToZeroBasedArrayIndex(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.ITEM)) {
        RexNode columnRef = call.getOperands().get(0);
        RexNode itemRef = call.getOperands().get(1);
        if (columnRef.getType() instanceof ArraySqlType
            && itemRef.isA(SqlKind.LITERAL)
            && itemRef.getType().getSqlTypeName().equals(SqlTypeName.INTEGER)) {
          Integer val = ((RexLiteral) itemRef).getValueAs(Integer.class);
          RexLiteral newItemRef = rexBuilder.makeExactLiteral(new BigDecimal(val - 1), itemRef.getType());
          return Optional.of(rexBuilder.makeCall(call.op, columnRef, newItemRef));
        }
      }
      return Optional.empty();
    }

    // Convert CAST(ROW: RECORD_TYPE) to named_struct
    private Optional<RexNode> convertToNamedStruct(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.CAST)) {
        RexNode operand = call.getOperands().get(0);
        if (operand instanceof RexCall && ((RexCall) operand).getOperator().equals(SqlStdOperatorTable.ROW)) {
          RelRecordType recordType = (RelRecordType) call.getType();
          List<RexNode> rowOperands = ((RexCall) operand).getOperands();
          List<RexNode> newOperands = new ArrayList<>(recordType.getFieldCount() * 2);
          for (int i = 0; i < recordType.getFieldCount(); i += 1) {
            RelDataTypeField dataTypeField = recordType.getFieldList().get(i);
            newOperands.add(rexBuilder.makeLiteral(dataTypeField.getKey()));
            newOperands.add(rexBuilder.makeCast(dataTypeField.getType(), rowOperands.get(i)));
          }
          return Optional.of(rexBuilder.makeCall(call.getType(), new HiveNamedStructFunction(), newOperands));
        }
      }
      return Optional.empty();
    }

    private static SqlOperator createUDF(String udfName, SqlReturnTypeInference typeInference) {
      return new SqlUserDefinedFunction(new SqlIdentifier(ImmutableList.of(udfName), SqlParserPos.ZERO),
          typeInference, null, null, null, null);
    }
  }
}

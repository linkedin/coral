/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.Lists;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.functions.CoalesceStructUtility;
import com.linkedin.coral.hive.hive2rel.functions.HiveNamedStructFunction;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.spark.containers.SparkRelInfo;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.exceptions.UnsupportedUDFException;
import com.linkedin.coral.spark.utils.RelDataTypeToHiveTypeStringConverter;


/**
 * This class applies series of transformations to make a IR RelNode compatible with Spark.
 *
 * It uses Calcite's RelShuttle and RexShuttle to traverse the RelNode Plan.
 * During traversal it identifies and transforms UDFs.
 *      1) Identify UDF if it is defined in [[TransportableUDFMap]] and adds it to a List<SparkUDFInfo>
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
    Set<SparkUDFInfo> sparkUDFInfos = new HashSet<>();
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
    return new SparkRelInfo(calciteNode.accept(converter), new ArrayList<>(sparkUDFInfos));
  }

  /**
   * For replacing a UDF SQL operator with a new SQL operator with different name.
   *
   * Consults [[TransportableUDFMap]] to get the new name.
   *
   * for example: com.linkedin.dali.udf.date.hive.EpochToEpochMilliseconds -> epochToEpochMilliseconds
   */
  private static class SparkRexConverter extends RexShuttle {
    private final RexBuilder rexBuilder;
    private final Set<SparkUDFInfo> sparkUDFInfos;
    private static final Logger LOG = LoggerFactory.getLogger(SparkRexConverter.class);

    SparkRexConverter(RexBuilder rexBuilder, Set<SparkUDFInfo> sparkUDFInfos) {
      this.sparkUDFInfos = sparkUDFInfos;
      this.rexBuilder = rexBuilder;
    }

    /**
     * This method traverses the list of RexCall nodes.  During traversal, this method performs the necessary
     * conversion from Calcite terms to Spark terms.  For example, Calcite has a built-in function "CARDINALITY",
     * which corresponds to "SIZE" in Spark.
     *
     * In order to convert to Spark terms correctly, we need to traverse RexCall expression in post-order.
     * This is because a built-in function name may appear as parameter of a user function.
     * For example, user_function1( CARDINALITY(fieldName) )
     */
    @Override
    public RexNode visitCall(RexCall call) {
      if (call == null) {
        return null;
      }

      RexCall updatedCall = (RexCall) super.visitCall(call);

      RexNode convertToNewNode = convertToZeroBasedArrayIndex(updatedCall).orElseGet(
          () -> convertToNamedStruct(updatedCall).orElseGet(() -> convertFuzzyUnionGenericProject(updatedCall)
              .orElseGet(() -> convertDaliUDF(updatedCall).orElseGet(() -> convertBuiltInUDF(updatedCall)
                  .orElseGet(() -> fallbackToHiveUdf(updatedCall).orElseGet(() -> swapExtractUnionFunction(updatedCall)
                      .orElseGet(() -> removeCastToEnsureCorrectNullability(updatedCall).orElse(updatedCall))))))));

      return convertToNewNode;
    }

    private Optional<RexNode> convertDaliUDF(RexCall call) {
      Optional<SparkUDFInfo> sparkUDFInfo = TransportableUDFMap.lookup(call.getOperator().getName());
      if (sparkUDFInfo.isPresent()) {
        // Function name from the map lookup is always null since it must be retrieved from the enclosing SqlOperator
        String functionName = ((VersionedSqlUserDefinedFunction) call.getOperator()).getViewDependentFunctionName();
        SparkUDFInfo value = sparkUDFInfo.get();
        sparkUDFInfos
            .add(new SparkUDFInfo(value.getClassName(), functionName, value.getArtifactoryUrls(), value.getUdfType()));
        return Optional.of(rexBuilder.makeCall(createUDF(functionName, call.getOperator().getReturnTypeInference()),
            call.getOperands()));
      } else {
        return Optional.empty();
      }
    }

    private Optional<RexNode> convertBuiltInUDF(RexCall call) {
      return BuiltinUDFMap.lookup(call.getOperator().getName()).map(name -> rexBuilder
          .makeCall(createUDF(name, call.getOperator().getReturnTypeInference()), call.getOperands()));
    }

    /**
     * After failing to find the function name in BuiltinUDFMap and TransportableUDFMap,
     * we call this function to fall back to the original Hive UDF defined in HiveFunctionRegistry.
     * This is reasonable since Spark understands and has ability to run Hive UDF.
     */
    private Optional<RexNode> fallbackToHiveUdf(RexCall call) {
      SqlOperator sqlOp = call.getOperator();
      String functionClassName = sqlOp.getName();

      Optional<SparkUDFInfo> sparkUDFInfo = Optional.empty();
      if (functionClassName.indexOf('.') >= 0) {
        if (UnsupportedHiveUDFsInSpark.contains(functionClassName)) {
          throw new UnsupportedUDFException(functionClassName);
        } else {
          // We get SparkUDFInfo object for Dali UDF only which has '.' in the function class name.
          // We do not need to handle the keyword built-in functions.
          VersionedSqlUserDefinedFunction daliUdf = (VersionedSqlUserDefinedFunction) sqlOp;
          String expandedFuncName = daliUdf.getViewDependentFunctionName();
          // need to provide UDF dependency with ivy coordinates
          List<String> dependencies = daliUdf.getIvyDependencies();
          List<URI> listOfUris = dependencies.stream().map(URI::create).collect(Collectors.toList());
          sparkUDFInfo = Optional.of(
              new SparkUDFInfo(functionClassName, expandedFuncName, listOfUris, SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));
          LOG.info("Function: {} is not a Builtin UDF or Transportable UDF.  We fall back to its Hive "
              + "function with ivy dependency: {}", functionClassName, String.join(",", dependencies));
        }
      }

      sparkUDFInfo.ifPresent(sparkUDFInfos::add);

      return sparkUDFInfo.map(sparkUDFInfo1 -> rexBuilder.makeCall(
          createUDF(sparkUDFInfo1.getFunctionName(), call.getOperator().getReturnTypeInference()), call.getOperands()));
    }

    // Coral RelNode Stores array indexes as +1, this fixes the behavior on spark side
    private Optional<RexNode> convertToZeroBasedArrayIndex(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.ITEM)) {
        RexNode columnRef = call.getOperands().get(0);
        RexNode itemRef = call.getOperands().get(1);
        if (columnRef.getType() instanceof ArraySqlType) {
          if (itemRef.isA(SqlKind.LITERAL) && itemRef.getType().getSqlTypeName().equals(SqlTypeName.INTEGER)) {
            Integer val = ((RexLiteral) itemRef).getValueAs(Integer.class);
            RexLiteral newItemRef = rexBuilder.makeExactLiteral(new BigDecimal(val - 1), itemRef.getType());
            return Optional.of(rexBuilder.makeCall(call.op, columnRef, newItemRef));
          } else {
            RexNode zeroBasedIndex =
                rexBuilder.makeCall(SqlStdOperatorTable.MINUS, itemRef, rexBuilder.makeExactLiteral(BigDecimal.ONE));
            return Optional.of(rexBuilder.makeCall(call.op, columnRef, zeroBasedIndex));
          }
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

    /**
     * Add the schema to GenericProject in Fuzzy Union
     * @param call a given RexCall
     * @return RexCall that resolves FuzzyUnion if its operator is GenericProject; otherwise, return empty
     */
    private Optional<RexNode> convertFuzzyUnionGenericProject(RexCall call) {
      if (call.getOperator() instanceof GenericProjectFunction) {
        // Register generic_project UDF
        sparkUDFInfos.add(new SparkUDFInfo("com.linkedin.genericprojectudf.GenericProject", "generic_project",
            ImmutableList.of(URI.create("ivy://com.linkedin.GenericProject:GenericProject-impl:+")),
            SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));
        RelDataType expectedRelDataType = call.getType();
        String expectedRelDataTypeString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(expectedRelDataType);

        List<RexNode> newOperands = new ArrayList<>();
        newOperands.add(call.getOperands().get(0));
        newOperands.add(rexBuilder.makeLiteral(expectedRelDataTypeString));

        return Optional
            .of(rexBuilder.makeCall(expectedRelDataType, new GenericProjectFunction(expectedRelDataType), newOperands));
      }
      return Optional.empty();
    }

    /**
     * Instead of leaving extract_union visible to (Hive)Spark, since we adopted the new exploded struct schema(
     * a.k.a struct_tr) that is different from extract_union's output (a.k.a struct_ex) to interpret union in Coral IR,
     * we need to swap the reference of "extract_union" to a new UDF that is coalescing the difference between
     * struct_tr and struct_ex.
     *
     * See com.linkedin.coral.common.functions.FunctionReturnTypes#COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY
     * and its comments for more details.
     *
     * @param call the original extract_union function call.
     * @return A new {@link RexNode} replacing the original extract_union call.
     */
    private Optional<RexNode> swapExtractUnionFunction(RexCall call) {
      if (call.getOperator().getName().equalsIgnoreCase("extract_union")) {
        // Only when there's a necessity to register coalesce_struct UDF
        sparkUDFInfos.add(new SparkUDFInfo("com.linkedin.coalescestruct.GenericUDFCoalesceStruct", "coalesce_struct",
            ImmutableList.of(URI.create("ivy://com.linkedin.coalesce-struct:coalesce-struct-impl:+")),
            SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF));

        // one arg case: extract_union(field_name)
        if (call.getOperands().size() == 1) {
          return Optional.of(rexBuilder.makeCall(
              createUDF("coalesce_struct", CoalesceStructUtility.COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY),
              call.getOperands()));
        }
        // two arg case: extract_union(field_name, ordinal)
        else if (call.getOperands().size() == 2) {
          int ordinal = ((RexLiteral) call.getOperands().get(1)).getValueAs(Integer.class) + 1;
          List<RexNode> operandsCopy = Lists.newArrayList(call.getOperands());
          operandsCopy.set(1, rexBuilder.makeExactLiteral(new BigDecimal(ordinal)));
          return Optional.of(rexBuilder.makeCall(
              createUDF("coalesce_struct", CoalesceStructUtility.COALESCE_STRUCT_FUNCTION_RETURN_STRATEGY),
              operandsCopy));
        }
      }
      return Optional.empty();
    }

    /**
     *  Calcite entails the nullability of an expression by casting it to the correct nullable type.
     *  However, for complex types like ARRAY<STRING NOT NULL> (element non-nullable, but top-level nullable),
     *  the translated SQL will be `CAST(XXX AS ARRAY<STRING>)`, which strip the nullable information.
     *  Since Spark treats a cast target sql type name as always nullable (both inner and outer),
     *  it will treat above cast call as type ARRAY<STRING:nullable>:nullable, this deviates
     *  from the nullability represented in RelNode/Coral-Schema ARRAY<STRING:non-nullable>:nullable,
     *  see {@link com.linkedin.coral.schema.avro.ViewToAvroSchemaConverterTests#testCaseCallWithNullBranchAndComplexDataTypeBranch()
     *  testCastCallNullablility}
     *
     *  To make this work, we remove all the CAST expressions induced by nullability differences, and let Spark's
     *  SQL analyzer derive the nullability for the SQL itself, and as long as Coral-Schema can be an equal or looser
     *  with regard to the Spark analyzer schema, it should make Coral compatible with Spark.
     */
    private Optional<RexNode> removeCastToEnsureCorrectNullability(RexCall call) {
      if (call.getOperator().equals(SqlStdOperatorTable.CAST)) {
        if (RexUtil.isNullLiteral(call, true)) {
          return Optional.of(rexBuilder.makeNullLiteral(call.getType()));
        }
        RelDataType castType = call.getType();
        RelDataType originalType = call.getOperands().get(0).getType();
        if (castType.isNullable() && !originalType.isNullable()
            && rexBuilder.getTypeFactory().createTypeWithNullability(originalType, true).equals(castType)) {
          return Optional.of(rexBuilder.copy(call.getOperands().get(0)));
        }
      }
      return Optional.empty();
    }

    private static SqlOperator createUDF(String udfName, SqlReturnTypeInference typeInference) {
      return new SqlUserDefinedFunction(new SqlIdentifier(ImmutableList.of(udfName), SqlParserPos.ZERO), typeInference,
          null, null, null, null);
    }
  }
}

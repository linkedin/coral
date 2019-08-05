package com.linkedin.coral.schema.avro;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.rel.HiveUncollect;
import com.linkedin.coral.schema.avro.exceptions.SchemaNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.AggregateCall;
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
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexDynamicParam;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexLocalRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexOver;
import org.apache.calcite.rex.RexPatternFieldRef;
import org.apache.calcite.rex.RexRangeRef;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.rex.RexSubQuery;
import org.apache.calcite.rex.RexTableInputRef;
import org.apache.hadoop.hive.metastore.api.Table;


/**
 * This class generates an avro schema for calcite IR RelNode
 *
 * It utilizes RelShuttle to traverse the calcite IR RelNode in a bottom-up manner
 * and generate the avro schema layer by layer based on base table avro schema
 *
 * Case Sensitivity:
 * Case Sensitivity is addressed by extracting field name directly from base table avro schema based on index mapping
 * and pass it through AST tree in a bottom-up manner if there is no renaming. If there is renaming, then
 * the user-specified new name is used instead
 *
 * Nullability:
 * Currently, nullability is addressed in a similar way as case sensitivity. However, this approach can
 * cause information loss.
 * TODO: implement the following rules for nullability
 * 1. If a column is unmodified by Rel operator, it retains nullability property
 * 2. If the column is an input to UDF, always expect a nullable return value from UDF. Since nullability property
 *    for parameters is not correctly encoded for UDFs so calcite will not provide this information directly.
 * 3. For projecting an expression consider various types of expressions like arithmetic, logical and unary expressions.
 *    Operator semantics will determine nullable type. By and large, for all operators,
 *    1) if one of the inputs is null then the return value will also be null.
 *    2) If all inputs are non-null, operator will return non null
 *
 * Enum:
 * Enum is handled in a similar way as case sensitivity. Since sql does not have an enum type, enum type can only be
 * preserved through a pass-through logical operator (e.g. LogicalFilter). If any transformation (e.g. UDF) applies
 * to enum field, we use the output type of the transformation to replace enum type.
 *
 * For example
 * LogicalProject(Id_View_Col=[$0], Map_View_Col=[$1], Struct_Count=[$2], EXPR$3=[100])
 *   LogicalAggregate(group=[{0, 1}], Struct_Count=[COUNT()])
 *     LogicalProject(Id_View_Col=[$0], Map_View_Col=[$2], struct_col=[$3])
 *       LogicalFilter(condition=[AND(>($0, 0), IS NOT NULL($2), IS NOT NULL($3))])
 *         LogicalTableScan(table=[[hive, default, basecomplex]])
 *
 *  1) LogicalTableScan reads base table schema from metastore
 *  2) LogicalFilter passes through the output schema from LogicalTableScan
 *  3) LogicalProject(Id_View_Col=[$0], Map_View_Col=[$2], struct_col=[$3]) keeps
 *    [Id_View_Col, Map_View_Col, struct_col] fields from the output schema of LogicalFilter
 *  4) LogicalAggregate keeps [Id_View_Col, Map_View_Col] field and append a new field [Struct_Count]
 *    with type inferred from COUNT() operation
 *  5) LogicalProject(Id_View_Col=[$0], Map_View_Col=[$1], Struct_Count=[$2], EXPR$3=[100]) keeps field
 *     [Id_View_Col, Map_View_Col, Struct_Count] and append a new field EXPR$3 into the avro schema. The output schema
 *     of top level LogicalProject is the Dali view avro schema
 *
 */
public class RelToAvroSchemaConverter {
  private final HiveMetastoreClient hiveMetastoreClient;

  public RelToAvroSchemaConverter(HiveMetastoreClient hiveMetastoreClient) {
    this.hiveMetastoreClient = hiveMetastoreClient;
  }

  /**
   * This method generates a corresponding avro schema for calcite IR RelNode
   *
   * @param relNode
   * @return avro schema for calicite IR RelNode
   * @throws RuntimeException if cannot find table in Hive metastore
   * @throws RuntimeException if cannot determine avro schema for tableScan
   */
  public Schema convert(@Nonnull RelNode relNode) {
    Preconditions.checkNotNull(relNode, "RelNode to convert cannot be null");

    Map<RelNode, Schema> schemaMap = new HashMap<>();
    relNode.accept(new SchemaRelShuttle(hiveMetastoreClient, schemaMap));
    Schema viewSchema = schemaMap.get(relNode);

    return viewSchema;
  }

  /**
   * This class extends RelShuttleImp. It's used to generate avro schema while traversing
   * the calcite IR RelNode in a bottom-up manner
   *
   * RelShuttle is used to traverse the IR RelNode without modifying it.
   * For each logical operator type, it's implemented in the following manner:
   *
   *     @Override
   *     public RelNode visit(LogicalOperator logicalOperator) {
   *       RelNode relNode = super.visit(logicalOperator);
   *
   *       // begin of building avro schema
   *       code to build avro schema in this level
   *       // end of building avro schema
   *
   *       return relNode;
   *     }
   *
   * relNode returned is the same instance of input logicalOperator. The reason of being implemented in
   * this way is that we want to build avro schema in a bottom-up manner
   *
   */
  private class SchemaRelShuttle extends RelShuttleImpl {
    private Map<RelNode, Schema> schemaMap;

    private final HiveMetastoreClient hiveMetastoreClient;

    public SchemaRelShuttle(HiveMetastoreClient hiveMetastoreClient,
                            Map<RelNode, Schema> schemaMap) {
      this.hiveMetastoreClient = hiveMetastoreClient;
      this.schemaMap = schemaMap;
    }

    @Override
    public RelNode visit(TableScan tableScan) {
      RelNode relNode = super.visit(tableScan);
      Schema tableScanSchema = getTableScanSchema(tableScan);
      schemaMap.put(tableScan, tableScanSchema);

      return relNode;
    }

    @Override
    public RelNode visit(TableFunctionScan tableFunctionScan) {
      // TODO: implement this method
      return super.visit(tableFunctionScan);
    }

    @Override
    public RelNode visit(LogicalValues logicalValues) {
      // TODO: implement this method
      return super.visit(logicalValues);
    }

    @Override
    public RelNode visit(LogicalFilter logicalFilter) {
      RelNode relNode = super.visit(logicalFilter);
      Schema inputSchema = schemaMap.get(logicalFilter.getInput());
      schemaMap.put(logicalFilter, inputSchema);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalProject logicalProject) {
      RelNode relNode = super.visit(logicalProject);
      Schema inputSchema = schemaMap.get(logicalProject.getInput());

      SchemaBuilder.FieldAssembler<Schema> logicalProjectFieldAssembler = SchemaBuilder.record(inputSchema.getName())
                                                                         .namespace(inputSchema.getNamespace())
                                                                         .fields();
      logicalProject.accept(new SchemaRexShuttle(inputSchema, logicalProjectFieldAssembler));

      // handle column renaming
      List<String> columnNames = new ArrayList<>();
      for (RelDataTypeField field : logicalProject.getRowType().getFieldList()) {
        columnNames.add(field.getName());
      }

      Schema schemaWithRenamedColumns = SchemaUtilities.renameColumns(
          columnNames,
          logicalProjectFieldAssembler.endRecord());

      schemaMap.put(logicalProject, schemaWithRenamedColumns);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalJoin logicalJoin) {
      // TODO: implement this method
      return super.visit(logicalJoin);
    }

    @Override
    public RelNode visit(LogicalCorrelate logicalCorrelate) {
      RelNode relNode = super.visit(logicalCorrelate);

      Schema leftSchema = schemaMap.get(logicalCorrelate.getLeft());
      Schema rightSchema = schemaMap.get(logicalCorrelate.getRight());
      Schema logicalCorrelateSchema = SchemaUtilities.joinSchemas(leftSchema, rightSchema);
      schemaMap.put(logicalCorrelate, logicalCorrelateSchema);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalUnion logicalUnion) {
      RelNode relNode = super.visit(logicalUnion);
      Schema inputSchema1 = schemaMap.get(logicalUnion.getInput(0));
      Schema inputSchema2 = schemaMap.get(logicalUnion.getInput(1));

      // TODO: instead of throwing exception when schemas do not match, handle this case using fuzzy union semantics
      if (!inputSchema1.toString(true).equals(inputSchema2.toString(true))) {
        throw new RuntimeException("Input schemas of LogicalUnion operator do not match. "
            + "inputSchema1 is: " + inputSchema1.toString(true) + ", "
            + "inputSchema2 is: " + inputSchema2.toString(true));
      }

      schemaMap.put(logicalUnion, inputSchema1);

      return relNode;
    }

    @Override
    public RelNode visit(LogicalIntersect logicalIntersect) {
      // TODO: implement this method
      return super.visit(logicalIntersect);
    }

    @Override
    public RelNode visit(LogicalMinus logicalMinus) {
      // TODO: implement this method
      return super.visit(logicalMinus);
    }

    @Override
    public RelNode visit(LogicalAggregate logicalAggregate) {
      // TODO: Potential need RexShuttle
      RelNode relNode = super.visit(logicalAggregate);
      Schema inputSchema = schemaMap.get(logicalAggregate.getInput());
      SchemaBuilder.FieldAssembler<Schema> logicalAggregateFieldAssembler = SchemaBuilder.record(inputSchema.getName())
          .namespace(inputSchema.getNamespace())
          .fields();

      List<Schema.Field> inputSchemaFields = inputSchema.getFields();
      for (int i = 0; i < inputSchemaFields.size(); i++) {
        if (logicalAggregate.getGroupSet().get(i)) {
          SchemaUtilities.appendField(inputSchemaFields.get(i), logicalAggregateFieldAssembler);
        }
      }

      // Handle aggCalls
      for (AggregateCall aggCall : logicalAggregate.getAggCallList()) {
        // TODO: give the field a proper name
        String fieldName = SchemaUtilities.toAvroQualifiedName(aggCall.getName());
        RelDataType fieldType = aggCall.getType();
        SchemaUtilities.appendField(fieldName, fieldType, logicalAggregateFieldAssembler);
      }

      schemaMap.put(logicalAggregate, logicalAggregateFieldAssembler.endRecord());

      return relNode;
    }

    @Override
    public RelNode visit(LogicalMatch logicalMatch) {
      // TODO: implement this method
      return super.visit(logicalMatch);
    }

    @Override
    public RelNode visit(LogicalSort logicalSort) {
      // TODO: implement this method
      return super.visit(logicalSort);
    }

    @Override
    public RelNode visit(LogicalExchange logicalExchange) {
      // TODO: implement this method
      return super.visit(logicalExchange);
    }

    @Override
    public RelNode visit(RelNode relNode) {
      // Handles lateral views here
      if (relNode instanceof HiveUncollect) {
        SchemaBuilder.FieldAssembler<Schema> hiveUncollectFieldAssembler = SchemaBuilder.record("LateralViews")
            .namespace("LateralViews")
            .fields();

        for (RelDataTypeField field : relNode.getRowType().getFieldList()) {
          SchemaUtilities.appendField(field.getName(), field.getType(), hiveUncollectFieldAssembler);
        }

        schemaMap.put(relNode, hiveUncollectFieldAssembler.endRecord());

        return relNode;
      } else {
        return super.visit(relNode);
      }
    }

    /**
     * This method retrieves avro schema for tableScan
     *
     * @param tableScan
     * @return avro schema for tableScan
     * @throws RuntimeException if cannot find table in Hive metastore
     * @throws RuntimeException if cannot determine avro schema for tableScan
     */
    private Schema getTableScanSchema(TableScan tableScan) {
      List<String> qualifiedName = tableScan.getTable().getQualifiedName();
      String dbName = qualifiedName.get(1);
      String tableName = qualifiedName.get(2);
      Table baseTable = hiveMetastoreClient.getTable(dbName, tableName);
      if (baseTable == null) {
        throw new RuntimeException("Cannot find table " + dbName + "." + tableName + " in Hive metastore");
      }

      Schema tableSchema = SchemaUtilities.getCasePreservedSchemaFromTblProperties(baseTable);
      if (tableSchema == null) {
        throw new SchemaNotFoundException("Cannot determine avro schema for table " + dbName + "." + tableName);
      }

      return tableSchema;
    }
  }

  /**
   * This class extends RexShuttle. It's used to generate avro schema while traversing RexNode.
   */
  private class SchemaRexShuttle extends RexShuttle {
    private Schema inputSchema;
    private SchemaBuilder.FieldAssembler<Schema> fieldAssembler;

    public SchemaRexShuttle(Schema inputSchema,
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler) {
      this.inputSchema = inputSchema;
      this.fieldAssembler = fieldAssembler;
    }

    @Override
    public RexNode visitInputRef(RexInputRef rexInputRef) {
      RexNode rexNode = super.visitInputRef(rexInputRef);

      List<Schema.Field> inputSchemaFields = inputSchema.getFields();
      SchemaUtilities.appendField(inputSchemaFields.get(rexInputRef.getIndex()), fieldAssembler);

      return rexNode;
    }

    @Override
    public RexNode visitLocalRef(RexLocalRef rexLocalRef) {
      // TODO: implement this method
      return super.visitLocalRef(rexLocalRef);
    }

    @Override
    public RexNode visitLiteral(RexLiteral rexLiteral) {
      RexNode rexNode = super.visitLiteral(rexLiteral);
      // TODO: handle deduplication of literal names (SELECT 1, 1, 1 etc)
      String fieldName = "literal" + rexLiteral.getValue().toString();
      RelDataType fieldType = rexLiteral.getType();
      SchemaUtilities.appendField(fieldName, fieldType, fieldAssembler);

      return rexNode;
    }

    @Override
    public RexNode visitCall(RexCall rexCall) {
      /**
       * For GenericProject RexCall, no need to handle it recursively and only the inputRef in the 1st operands
       * is relevant to semantic schema generation
       */
      if (rexCall.getOperator() instanceof GenericProjectFunction) {
        List<RexNode> rexNodes = rexCall.getOperands();
        rexNodes.get(0).accept(this);

        return rexCall;
      } else {
        return super.visitCall(rexCall);
      }
    }

    @Override
    public RexNode visitOver(RexOver rexOver) {
      // TODO: implement this method
      return super.visitOver(rexOver);
    }

    @Override
    public RexNode visitCorrelVariable(RexCorrelVariable rexCorrelVariable) {
      // TODO: implement this method
      return super.visitCorrelVariable(rexCorrelVariable);
    }

    @Override
    public RexNode visitDynamicParam(RexDynamicParam rexDynamicParam) {
      // TODO: implement this method
      return super.visitDynamicParam(rexDynamicParam);
    }

    @Override
    public RexNode visitRangeRef(RexRangeRef rexRangeRef) {
      // TODO: implement this method
      return super.visitRangeRef(rexRangeRef);
    }

    @Override
    public RexNode visitFieldAccess(RexFieldAccess rexFieldAccess) {
      // TODO: implement this method
      return super.visitFieldAccess(rexFieldAccess);
    }

    @Override
    public RexNode visitSubQuery(RexSubQuery rexSubQuery) {
      // TODO: implement this method
      return super.visitSubQuery(rexSubQuery);
    }

    @Override
    public RexNode visitTableInputRef(RexTableInputRef rexTableInputRef) {
      // TODO: implement this method
      return super.visitTableInputRef(rexTableInputRef);
    }

    @Override
    public RexNode visitPatternFieldRef(RexPatternFieldRef rexPatternFieldRef) {
      // TODO: implement this method
      return super.visitPatternFieldRef(rexPatternFieldRef);
    }
  }

}

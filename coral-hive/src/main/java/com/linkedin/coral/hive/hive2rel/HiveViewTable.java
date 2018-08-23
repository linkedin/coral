package com.linkedin.coral.hive.hive2rel;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.base.Throwables;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexUtil;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.hadoop.hive.metastore.api.Table;

import static org.apache.calcite.sql.type.SqlTypeName.*;


/**
 * A TranslatableTable (ViewTable) version of HiveTable that supports
 * recursive expansion of view definitions
 */
public class HiveViewTable extends HiveTable implements TranslatableTable {
  private final List<String> schemaPath;

  /**
   * Constructor to create bridge from hive table to calcite table

   * @param hiveTable Hive table
   */
  public HiveViewTable(Table hiveTable, List<String> schemaPath) {
    super(hiveTable);
    this.schemaPath = schemaPath;
  }

  @Override
  public RelNode toRel(RelOptTable.ToRelContext relContext, RelOptTable relOptTable) {
    try {
      RelRoot root = relContext.expandView(relOptTable.getRowType(), hiveTable.getViewExpandedText(), schemaPath,
          ImmutableList.of(hiveTable.getTableName()));
      root = root.withRel(
          createCastRel(root.rel, relOptTable.getRowType(), RelFactories.DEFAULT_PROJECT_FACTORY));
      //root = root.withRel(RelOptUtil.createCastRel(root.rel, relOptTable.getRowType()));
      return root.rel;
    } catch (Exception e) {
      Throwables.propagateIfInstanceOf(e, RuntimeException.class);
      throw new RuntimeException("Error while parsing view definition", e);
    }
  }

  public static RelNode createCastRel(
      final RelNode rel,
      RelDataType castRowType,
      RelFactories.ProjectFactory projectFactory) {
    Preconditions.checkNotNull(projectFactory);

    RelDataType rowType = rel.getRowType();
    if (isRowCastRequired(rowType, castRowType)) {
      final RexBuilder rexBuilder = rel.getCluster().getRexBuilder();
      final List<RexNode> castExps = RexUtil.generateCastExpressions(rexBuilder, castRowType, rowType);
      return projectFactory.createProject(rel, castExps, rowType.getFieldNames());
    } else {
      return rel;
    }
  }

  // [LIHADOOP-34428]: Dali allow extra fields on struct type columns to flow through. We
  // try to match that behavior. Dali does not allow top level columns to flow through
  // Returns true if an explicit cast is required from rowType to castRowType, false otherwise
  private static boolean isRowCastRequired(RelDataType rowType, RelDataType castRowType) {
    if (rowType == castRowType) {
      return false;
    }
    List<RelDataTypeField> relFields = rowType.getFieldList();
    List<RelDataTypeField> castFields = castRowType.getFieldList();
    if (relFields.size() != castFields.size()) {
      return true;
    }
    return !isRelStructAllowed(relFields, castFields);
  }

  // Returns true if an explicit cast is required from relDataType to castDataType, false otherwise
  private static boolean isFieldCastRequired(RelDataType relDataType, RelDataType castDataType) {
    if (relDataType == castDataType) {
      return false;
    }
    if (relDataType.getSqlTypeName() == ANY || castDataType.getSqlTypeName() == ANY) {
      return false;
    }
    // Hive has string type which is varchar(65k). This results in lot of redundant
    // cast operations due to different precision(length) of varchar.
    // Also, all projections of string literals are of type CHAR but the hive schema
    // may mark that column as string. This again results in unnecessary cast operations.
    // We avoid all these casts
    if ((relDataType.getSqlTypeName() == CHAR || relDataType.getSqlTypeName() == VARCHAR)
        && castDataType.getSqlTypeName() == VARCHAR) {
      return false;
    }

    // Make sure both source and target has same root SQL Type
    if (relDataType.getSqlTypeName() != castDataType.getSqlTypeName()) {
      return true;
    }

    switch (relDataType.getSqlTypeName()) {
      case ARRAY:
        return isFieldCastRequired(relDataType.getComponentType(), castDataType.getComponentType());
      case MAP:
        return isFieldCastRequired(relDataType.getKeyType(), castDataType.getKeyType())
            || isFieldCastRequired(relDataType.getValueType(), relDataType.getValueType());
      case ROW:
        return !isRelStructAllowed(relDataType.getFieldList(), castDataType.getFieldList());
      default:
        return !relDataType.equals(castDataType);
    }
  }

  private static boolean isRelStructAllowed(List<RelDataTypeField> relFields, List<RelDataTypeField> castFields) {
    if (relFields.size() < castFields.size()) {
      return false;
    }

    for (int i = 0; i < castFields.size(); i++) {
      RelDataTypeField relField = relFields.get(i);
      RelDataTypeField castField = castFields.get(i);
      if (isFieldCastRequired(relField.getType(), castField.getType())) {
        return false;
      }
    }
    return true;
  }
}


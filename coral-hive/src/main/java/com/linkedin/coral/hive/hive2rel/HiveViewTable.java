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
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.metastore.api.Table;


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
      // nothing to do
      return rel;
    }
    final RexBuilder rexBuilder = rel.getCluster().getRexBuilder();
    final List<RexNode> castExps =
        RexUtil.generateCastExpressions(rexBuilder, castRowType, rowType);
      return projectFactory.createProject(rel, castExps,
          rowType.getFieldNames());
  }

  // [LIHADOOP-34428]: Dali allow extra fields on struct type columns to flow through. We
  // try to match that behavior. Dali does not allow top level columns to flow through
  private static boolean isRowCastRequired(RelDataType rowType, RelDataType castRowType) {
    if (rowType == castRowType) {
      return true;
    }
    List<RelDataTypeField> relFields = rowType.getFieldList();
    List<RelDataTypeField> castFields = castRowType.getFieldList();
    if (relFields.size() != castFields.size()) {
      return false;
    }
    return isRelStructAllowed(relFields, castFields);
  }

  private static boolean isFieldCastRequired(RelDataTypeField relField, RelDataTypeField castField) {
    if (relField.getType().getSqlTypeName() == SqlTypeName.ANY
        || castField.getType().getSqlTypeName() == SqlTypeName.ANY) {
      return true;
    }
    if (relField.getType().isStruct()) {
      if (relField.getType().isStruct() != castField.getType().isStruct()) {
        return false;
      }
      if (!isRelStructAllowed(relField.getType().getFieldList(), castField.getType().getFieldList())) {
        return false;
      }
    } else {
      // Hive has string type which is varchar(65k). This results in lot of redundant
      // cast operations due to different precision(length) of varchar.
      // Also, all projections of string literals are of type CHAR but the hive schema
      // may mark that column as string. This again results in unnecessary cast operations.
      // We avoid all these casts
      if ((relField.getType().getSqlTypeName() == SqlTypeName.CHAR
          || relField.getType().getSqlTypeName() == SqlTypeName.VARCHAR)
          && castField.getType().getSqlTypeName() == SqlTypeName.VARCHAR) {
        return true;
      }
      if (!relField.equals(castField)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isRelStructAllowed(List<RelDataTypeField> relFields, List<RelDataTypeField> castFields) {
    if (relFields.size() < castFields.size()) {
      return false;
    }
    for (int i = 0; i < castFields.size(); i++) {
      RelDataTypeField relField = relFields.get(i);
      RelDataTypeField castField = castFields.get(i);
      if (!isFieldCastRequired(relField, castField)) {
        return false;
      }
    }
    return true;
  }
}


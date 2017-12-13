package com.linkedin.coral.hive.hive2rel;

import com.google.common.base.Preconditions;
import com.linkedin.coral.hive.hive2rel.parsetree.ParseTreeBuilder;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlNode;


/**
 * Class that implements {@link org.apache.calcite.plan.RelOptTable.ViewExpander}
 * interface to support expansion of Hive Views to relational algebra.
 */
public class HiveViewExpander implements RelOptTable.ViewExpander {

  private final RelContextProvider relContextProvider;

  /**
   * Instantiates a new Hive view expander.
   *
   * @param relContextProvider Rel context provider instance
   */
  public HiveViewExpander(@Nonnull RelContextProvider relContextProvider) {
    Preconditions.checkNotNull(relContextProvider);
    this.relContextProvider = relContextProvider;
  }

  @Override
  public RelRoot expandView(RelDataType rowType, String queryString, List<String> schemaPath, List<String> viewPath) {
    ParseTreeBuilder treeBuilder = new ParseTreeBuilder(relContextProvider.getParseTreeBuilderConfig());
    SqlNode viewNode = treeBuilder.process(queryString);
    return relContextProvider.getSqlToRelConverter().convertQuery(viewNode, true, true);
  }
}

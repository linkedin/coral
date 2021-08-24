package com.linkedin.coral.hive.hive2rel;

import com.google.common.base.Preconditions;
import org.apache.calcite.plan.Context;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.Pair;
import com.linkedin.coral.hive.hive2rel.rel.HiveUncollect;

import java.util.ArrayList;
import java.util.List;

import static org.apache.calcite.rel.core.RelFactories.DEFAULT_AGGREGATE_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_EXCHANGE_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_FILTER_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_JOIN_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_MATCH_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_PROJECT_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_REPEAT_UNION_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SET_OP_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SNAPSHOT_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SORT_EXCHANGE_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SORT_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_SPOOL_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_TABLE_SCAN_FACTORY;
import static org.apache.calcite.rel.core.RelFactories.DEFAULT_VALUES_FACTORY;

public class HiveRelBuilder extends RelBuilder {

    private HiveRelBuilder(Context context, RelOptCluster cluster, RelOptSchema relOptSchema) {
        super(context, cluster, relOptSchema);
    }

    /**
     * Creates a RelBuilder.
     */
    public static RelBuilder create(FrameworkConfig config) {
        return Frameworks.withPrepare(config,
                (cluster, relOptSchema, rootSchema, statement) ->
                        new HiveRelBuilder(config.getContext(), cluster, relOptSchema));
    }

    /** Creates a {@link RelBuilderFactory}, a partially-created RelBuilder.
     * Just add a {@link RelOptCluster} and a {@link RelOptSchema} */
    public static RelBuilderFactory proto(final Context context) {
        return (cluster, schema) -> new HiveRelBuilder(context, cluster, schema);
    }

    public static final RelBuilderFactory LOGICAL_BUILDER =
            HiveRelBuilder.proto(
                    Contexts.of(DEFAULT_PROJECT_FACTORY,
                            DEFAULT_FILTER_FACTORY,
                            DEFAULT_JOIN_FACTORY,
                            DEFAULT_SORT_FACTORY,
                            DEFAULT_EXCHANGE_FACTORY,
                            DEFAULT_SORT_EXCHANGE_FACTORY,
                            DEFAULT_AGGREGATE_FACTORY,
                            DEFAULT_MATCH_FACTORY,
                            DEFAULT_SET_OP_FACTORY,
                            DEFAULT_VALUES_FACTORY,
                            DEFAULT_TABLE_SCAN_FACTORY,
                            DEFAULT_SNAPSHOT_FACTORY,
                            DEFAULT_SPOOL_FACTORY,
                            DEFAULT_REPEAT_UNION_FACTORY));

    /** Ensures that the field names match those given.
     *
     * <p>If all fields have the same name, adds nothing;
     * if any fields do not have the same name, adds a {@link Project}.
     *
     * <p>Note that the names can be short-lived. Other {@code RelBuilder}
     * operations make no guarantees about the field names of the rows they
     * produce.
     *
     * @param fieldNames List of desired field names; may contain null values or
     * have fewer fields than the current row type
     */
    @Override
    public RelBuilder rename(List<String> fieldNames) {
        final List<String> oldFieldNames = peek().getRowType().getFieldNames();
        Preconditions.checkArgument(fieldNames.size() <= oldFieldNames.size(),
                "More names than fields");
        final List<String> newFieldNames = new ArrayList<>(oldFieldNames);
        for (int i = 0; i < fieldNames.size(); i++) {
            final String s = fieldNames.get(i);
            if (s != null) {
                newFieldNames.set(i, s);
            }
        }
        if (oldFieldNames.equals(newFieldNames)) {
            return this;
        }
        if (peek() instanceof Values) {
            // Special treatment for VALUES. Re-build it rather than add a project.
            final Values v = (Values) build();
            final RelDataTypeFactory.Builder b = getTypeFactory().builder();
            for (Pair<String, RelDataTypeField> p
                    : Pair.zip(newFieldNames, v.getRowType().getFieldList())) {
                b.add(p.left, p.right.getType());
            }
            return values(v.tuples, b.build());
        }
        if (peek() instanceof HiveUncollect) {
            // Special treatment for HiveUncollect. Re-build it rather than add a project.
            final HiveUncollect v = (HiveUncollect) build();
            final RelDataTypeFactory.Builder b = getTypeFactory().builder();
            for (Pair<String, RelDataTypeField> p
                    : Pair.zip(newFieldNames, v.getRowType().getFieldList())) {
                b.add(p.left, p.right.getType());
            }
            push(v.copy(b.build()));
            return this;
        }

        return project(fields(), newFieldNames, true);
    }

}

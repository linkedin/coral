package com.linkedin.coral.common;

import org.apache.calcite.plan.Context;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;

public class HiveCommonRelBuilder extends RelBuilder {
    private HiveCommonRelBuilder(Context context, RelOptCluster cluster, RelOptSchema relOptSchema) {
        super(context, cluster, relOptSchema);
    }

    public static RelBuilder create(FrameworkConfig config) {
        return Frameworks.withPrepare(config, (cluster, relOptSchema, rootSchema,
                statement) -> { cluster = RelOptCluster.create(cluster.getPlanner(),
                new RexBuilder(new CoralJavaTypeFactoryImpl(cluster.getTypeFactory().getTypeSystem())));
                return new HiveCommonRelBuilder(config.getContext(), cluster, relOptSchema);});
    }
}

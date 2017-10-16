package com.linkedin.coral.hive.hive2rel.parsetree;

import com.google.common.collect.ImmutableSet;
import com.linkedin.coral.hive.hive2rel.HiveSchema;
import com.linkedin.coral.hive.hive2rel.TestUtils;
import java.util.List;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.TestUtils.*;


public class ParseTreeBuilderTest {

  private static TestHive hive;

  @BeforeClass
  public static void beforeClass() {
    hive = TestUtils.setupDefaultHive();
  }

  @Test
  public void testBasicSql() throws ValidationException, RelConversionException {
    /*{
      String sql = "SELECT a, -b, c+d, (c>10), (c > 10 AND D <25) from myTable where c > 10 AND d < 15 OR a = 'abc'";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
    }
    {
      String sql = "SELECT * from foo.myTable order by a desc, b asc";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
    }
    {
      String sql = "SELECT a, sum(c), count(*) from myTable group by a, b";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
    }
    {
      String sql = "SELECT distinct c[0] from myTable";
      SqlNode converted = convert(sql);
      System.out.println(converted.toString());
    }
    {
      String sql = "SELECT a, b from (select c, d from foo) f";
      SqlNode n = convert(sql);
      System.out.println(n);
    }*/
    {
      String sql = "SELECT a, b from foo union all select c, d from bar";
      SqlNode n = convert(sql);
      RelNode r = toRel(config(), n);
      System.out.println(n);
    }
    {
      String sql = "SELECT a, b from foo where a in  (select c from bar where d < 10)";
      SqlNode n = convert(sql);
      System.out.println(n);
    }
  }

  private static SqlNode convert(String sql) {
    ASTNode root = toAST(sql);
    ParseTreeBuilder builder = new ParseTreeBuilder();
    return builder.visit(root, new ParseTreeBuilder.ParseContext());
  }

  static RelNode toRel(FrameworkConfig config, SqlNode node) throws ValidationException, RelConversionException {
    Planner planner = Frameworks.getPlanner(config);
    SqlNode validate = planner.validate(node);
    return planner.rel(validate).rel;
  }

  static FrameworkConfig config() {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);
    HiveSchema schema = HiveSchema.create(hive.context.getHive());
    rootSchema.add("hive", schema);

    SqlParser.Config parserConfig =
        SqlParser.configBuilder().setCaseSensitive(false).setConformance(SqlConformanceEnum.STRICT_2003)
            .build();
    ImmutableSet<RelOptRule> programs =
        ImmutableSet.<RelOptRule>builder().addAll(Programs.RULE_SET).build();

    return Frameworks.newConfigBuilder()
        .parserConfig(parserConfig)
        .defaultSchema(rootSchema)
        .traitDefs((List<RelTraitDef>) null)
        .programs(Programs.ofRules(programs))
        .build();
  }
}
package com.linkedin.coral.presto.rel2presto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.Programs;

import static com.linkedin.coral.presto.rel2presto.TestTable.*;


public class TestUtils {

  public static FrameworkConfig createFrameworkConfig(TestTable... tables) {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);
    Arrays.asList(tables)
        .forEach(t -> rootSchema.add(t.getTableName(), t));

    SqlParser.Config parserConfig =
        SqlParser.configBuilder()
            .setCaseSensitive(false).
            setConformance(SqlConformanceEnum.STRICT_2003)
            .build();

    return Frameworks.newConfigBuilder()
        .parserConfig(parserConfig)
        .defaultSchema(rootSchema)
        .traitDefs((List<RelTraitDef>) null)
        .programs(Programs.ofRules(Programs.RULE_SET))
        .build();
  }

  static RelNode toRel(String sql, FrameworkConfig config) {
    Planner planner = Frameworks.getPlanner(config);
    try {
      SqlNode sn = planner.parse(sql);
      SqlNode validate = planner.validate(sn);
      RelRoot rel = planner.rel(validate);
      return rel.project();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Utility to conveniently format sql string just like Calcite formatting.
   * This is used to conveniently generate expected string without having
   * to type all formatting each time.
   * WARNING: This is not generic method but utility method for common cases
   * for testing. There are bugs. Use at your own risk or, better yet, fix it
   * @param sql
   */
  public static String formatSql(String sql) {
    String s = upcaseKeywords(sql);
    s = quoteColumns(s);
    s = addLineBreaks(s);
    return s;
  }

  private static final ImmutableList<String> SQL_KEYWORDS;
  private static final Pattern KW_PATTERN;
  static {
    String kw = "SELECT FROM WHERE AS IN GROUP BY HAVING ORDER ASC DSC JOIN"
        + " INNER OUTER CROSS UNNEST LEFT RIGHT SUM COUNT MAX MIN AVG CAST IN EXCEPT IS NULL NOT OVER AND OR ON";
    SQL_KEYWORDS = ImmutableList.copyOf(kw.toLowerCase().split("\\s+"));
    KW_PATTERN = Pattern.compile("\\b" + String.join("|", SQL_KEYWORDS));
  }

  private static String addLineBreaks(String s) {
    ImmutableList<String> lineBreakKeywords = ImmutableList.copyOf(
        "SELECT FROM WHERE GROUP HAVING ORDER OVER IN EXCEPT UNION INTERSECT".split("\\s+"));
    Pattern pattern = Pattern.compile("\\s" + String.join("\\b|\\s", lineBreakKeywords) + "\\b");
    Matcher m = pattern.matcher(s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "\n" + m.group().trim());
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String quoteColumns(String sql) {
    Iterable<String> concat = Iterables.concat(TABLE_ONE.getColumnNames(), TABLE_TWO.getColumnNames(),
        ImmutableList.of(TABLE_ONE.getTableName(), TABLE_TWO.getTableName()));
    Pattern colPattern = Pattern.compile(String.join("|", concat));
    return quoteColumns(sql, colPattern);
  }

  public String quoteColumns(String sql, List<String> columns) {
    return quoteColumns(sql, Pattern.compile(String.join("|", columns)));
  }

  public static String quoteColumns(String sql, Pattern pattern) {
    String s = quoteAliases(sql);
    Matcher m = pattern.matcher(s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "\"" + m.group() + "\"");
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String quoteAliases(String input) {
    Pattern pattern = Pattern.compile("AS (\\w+)");
    Matcher m = pattern.matcher(input);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String replacement = null;
      String alias = m.group(1);
      if (alias.equalsIgnoreCase("integer") ||
          alias.equalsIgnoreCase("double") ||
          alias.equalsIgnoreCase("varchar")) {
        replacement = "AS " + alias.toUpperCase();
      } else {
        replacement = "AS \"" + m.group(1).toUpperCase() + "\"";
      }
      m.appendReplacement(sb, replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String upcaseKeywords(String sql) {
    Matcher matcher = KW_PATTERN.matcher(sql);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, matcher.group().toUpperCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
}

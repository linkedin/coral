package com.linkedin.coral.tools;

import com.facebook.presto.sql.parser.ParsingOptions;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.converters.HiveToPrestoConverter;
import com.linkedin.coral.functions.UnknownSqlFunctionException;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.tests.MetastoreProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.naming.ConfigurationException;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;

import static com.google.common.base.Preconditions.*;


/**
 * Tool cum integration test to connect to the hive metastore on grid
 * and verify translation of all hive view definitions.
 * This tool:
 *   1. Reads all databases and all tables within the database
 *   2. If the table is a 'virtual_view', this will translate the
 *      view definition to Presto SQL dialect
 *   3. Verifies that Presto SQL parser can successfully parse translated SQL.
 *   4. Tracks stats about failures and some of the common causes.
 *   5. Prints stats and failures
 * This is expected to continue through all kinds of translation failures.
 *
 * We have intentionally not integrated this as part of automated testing because:
 *   1. Hive metastore may have a large number of view definitions
 *   2. Running each time, as part of automated suite, can put load on metastore
 *   3. Build machine setup may not have credentials to connect to metastore
 *   4. Parallelizing translation to speed up can put more load on metastore in turn
 *
 * See {@link MetastoreProvider} for the required configuration properties
 */
@SuppressWarnings("unused")
public class ViewTranslationTest {

  public static final Pattern VIEW_NAME_PATTERN = Pattern.compile("([a-zA-Z0-9_]*)_(\\d+)_(\\d+)_(\\d+)$");

  public static void main(String[] args) throws IOException, MetaException, ConfigurationException {
    InputStream hiveConfStream = ViewTranslationTest.class.getClassLoader().getResourceAsStream("hive.properties");
    Properties props = new Properties();
    props.load(hiveConfStream);

    IMetaStoreClient metastoreClient = MetastoreProvider.getGridMetastoreClient(props);
    HiveMetastoreClient msc = new HiveMetaStoreClientAdapter(metastoreClient);
    HiveToPrestoConverter converter = HiveToPrestoConverter.create(msc);
    OutputStream ostr = System.out;
    String[] views = {
        "ads_mp.ad_consolidation_request_seat_bids",
        // "foundation_enterprise_mp_versioned.fact_leap_topics_impression_event_0_1_7",
        // "foundation_enterprise_mp.fact_leap_broadcast_action_event"
        "foundation_lms_mp_versioned.dsm_proposals_0_1_45",
        //"foundation_premium_mp.fact_oms_receipt",
        "foundation_marketing_mp.fact_resp_send_event",
        "foundation_marketing_mp.fact_resp_send_event",
        "foundation_marketing_mp.fact_resp_open_event",
        "foundation_marketing_mp.fact_resp_click_event",
        "foundation_lts_mp.fact_mcm_page_view",
        "foundation_lts_mp.fact_job_views",
        "foundation_lts_mp.fact_job_views_hourly",
        "foundation_lts_mp.fact_job_offsite_apply_click",
        "foundation_lts_mp.fact_job_offsite_apply_click_ump",
        "foundation_lts_mp.fact_job_action",
        "foundation_lts_mp.fact_job_action_ump",
        "foundation_lts_mp.fact_job_action_hourly",
        "foundation_lts_mp.fact_cap_search",
        "foundation_lts_mp.fact_cap_search_results",
        "foundation_lts_mp.fact_cap_search_maps",
        "foundation_lls_mp.fact_lynda_b2c_subscription_details",
        "foundation_core_entity_mp.fact_company_page_view",
        "foundation_lts_mp_versioned.agg_monthly_influenced_hires_0_1_110",
        "foundation_core_entity_mp_versioned.dim_lin_employees_info_1_0_102",
        //"foundation_core_tracking_mp.fact_decorated_me_notification_event",
        //"foundation_lts_mp.fact_det_cap_inmail_tmplt"
        // "foundation_core_entity_mp_versioned.dim_member_all_1_0_8"
        // "foundation_core_entity_mp.dim_lu_quarter",
        // "foundation_core_entity_mp.dim_company_products"
        // "foundation_enterprise_mp.fact_leap_broadcast_action_event",
        // "foundation_enterprise_mp.fact_leap_topics_impression_event"
        // access log failures: missing functions or fuzzy union
        // "access_log_mp_versioned.unified_access_log_event_base_0_6_1",
        // "access_log_mp_versioned.hdfs_access_log_event_base_0_6_1",
        // "access_log_mp_versioned.aws_cloudtrail_event_base_0_6_1",
        // "access_log_mp_versioned.presto_access_event_base_0_6_1",
        // Union type is not support
        // "adoptimizationevent_mp_versioned.adoptimizationevent_private_0_1_1",
        // extract_union function... probably, union type
        // "adoptimizationevent_mp_versioned.adoptimizationevent_0_1_1",
        // dali function: epochToFormat
        // "ads_mp_versioned.user_conversion_event_0_1_14",
        //ads_mp_versioned_ad_consolidation_external_bidder_request_0_1_14_is_guest_member
        //"ads_mp_versioned.ad_consolidation_external_bidder_request_0_1_14",
        // Unknown function name: ads_mp_versioned_ad_consolidation_request_bidder_selection_0_1_14_is_guest_member
        // "ads_mp_versioned.ad_consolidation_request_bidder_selection_0_1_14",
        // Unknown function name: ads_mp_versioned_video_ads_action_event_0_1_14_user_interface
        // "ads_mp_versioned.video_ads_action_event_0_1_14",
        // ads_mp_versioned_fact_sas_ad_lead_0_1_14_user_interface
        // "ads_mp_versioned.fact_sas_ad_lead_0_1_14",
        //  Unknown function name: ads_mp_versioned_fact_sas_ad_click_hourly_0_1_14_user_interface
        // "ads_mp_versioned.fact_sas_ad_click_hourly_0_1_14",
        // Unknown function name: from_utc_timestamp
        // "ads_mp_versioned.follow_event_0_1_14",
        // Unknown function name: ads_mp_versioned_fact_sas_ad_click_0_1_14_user_interface
        //"ads_mp_versioned.fact_sas_ad_click_0_1_14",
        // Unknown function name: ads_mp_versioned_fact_sas_ad_request_hourly_0_1_14_user_interface
        // "ads_mp_versioned.fact_sas_ad_request_hourly_0_1_14",
        // Unknown function name: ads_mp_versioned_fact_sas_ad_impr_hourly_0_1_14_user_interface
        // "ads_mp_versioned.fact_sas_ad_impr_hourly_0_1_14",
        // Unknown function name: ads_mp_versioned_fact_sas_ad_impr_0_1_14_user_interface
        // "ads_mp_versioned.fact_sas_ad_impr_0_1_14",
        // "automated_sourcing_mp_versioned.automated_sourcing_stream_refinement_response_event_0_0_15",
        // "automated_sourcing_mp_versioned.automated_sourcing_stream_exhausted_event_0_0_15",
        // "automated_sourcing_mp_versioned.automated_sourcing_candidate_rating_action_event_0_0_15",
        // "automated_sourcing_mp_versioned.automated_sourcing_candidate_not_fit_feedback_event_0_0_15",
        // "automated_sourcing_mp_versioned.automated_sourcing_stream_create_event_0_0_15",
        // "automated_sourcing_mp_versioned.automated_sourcing_stream_close_event_0_0_15",
        // "automated_sourcing_mp_versioned.recruiter_message_send_event_0_0_15",
        // "automated_sourcing_mp_versioned.automated_sourcing_profile_view_event_0_0_15",
        //"company_insights_mp_versioned.premium_insights_teaser_click_event_0_0_5",
        //"company_insights_mp_versioned.premium_insights_module_click_event_0_0_5",
        //"company_insights_mp_versioned.premium_insights_module_impression_event_0_0_5",
        //"company_insights_mp_versioned.premium_insights_teaser_impression_event_0_0_5",
        //"company_mp_versioned.company_view_event_career_0_1_18",
        //"company_mp_versioned.company_view_event_0_1_18",
        //"company_mp_versioned.company_action_event_other_0_1_18",
        //"company_mp_versioned.company_view_event_voyager_0_1_18",
        //"company_mp_versioned.company_view_event_organization_0_1_18",
        //"company_mp_versioned.company_action_event_0_1_18",
        //"company_mp_versioned.company_view_event_other_0_1_18",
        //"company_mp_versioned.company_action_event_organization_0_1_18",
        //"company_mp_versioned.company_view_event_showcase_0_1_18",
        //"compliance_mp_versioned.offline_compliance_request_event_ei_0_1_4",
        //"compliance_mp_versioned.offline_compliance_request_event_0_1_4",
        //"connectifier_account_mp_versioned.account_0_1_3",
        //"connectifier_account_mp_versioned.account_private_0_1_3",
        // "connectifier_contactevent_mp_versioned.contactevent_0_1_3",
        //"connectifier_contactevent_mp_versioned.contactevent_private_0_1_3",
        //"connectifier_pageviewevent_mp_versioned.pageviewevent_0_1_1",
        //"connectifier_pageviewevent_mp_versioned.pageviewevent_private_0_1_1",
        //"connectifier_profileviewevent_mp_versioned.profileviewevent_0_1_3",
        //"connectifier_profileviewevent_mp_versioned.profileviewevent_private_0_1_3",
        //"connectifier_searchevent_mp_versioned.searchevent_private_0_1_4",
        //"connectifier_searchevent_mp_versioned.searchevent_0_1_4",
        //"connectifier_user_mp_versioned.user_0_1_3",
        //"connectifier_user_mp_versioned.user_private_0_1_3",
        //"controlinteractionevent_mp.controlinteractionevent_mcm",
        //"controlinteractionevent_mp_versioned.controlinteractionevent_mcm_0_1_8",
        //"data_derived_adclickevent_mp_versioned.adclickevent_daily_private_0_0_6",
        //"data_derived_adclickevent_mp_versioned.ads_35112_adclickevent_daily_0_0_3",
        //"data_derived_adclickevent_mp_versioned.ads_35112_adclickevent_daily_private_0_0_3",
        //"data_derived_inpages_company_profiles_avro_mp_versioned.inpages_company_profiles_avro_0_1_3",
        //"data_derived_inpages_company_profiles_avro_mp_versioned.inpages_company_profiles_avro_private_0_1_3",
        //"data_derived_mentorshipservedevent_mp_versioned.mentorship_mentorshipservedevent_daily_0_0_4",
        //"data_derived_mentorshipservedevent_mp_versioned.mentorship_mentorshipservedevent_daily_private_0_0_4",
        //"data_derived_userconversionevent_mp_versioned.userconversionevent_0_0_1",
        //"data_derived_userconversionevent_mp_versioned.userconversionevent_private_0_0_1",
        //"dbchanges_identity_profile_mp_versioned.profile_0_1_13",
        //"dbchanges_identity_profile_mp_versioned.profile_private_0_1_13",
        //"dbchanges_notificationdb_notifications_mp_versioned.notifications_0_1_6",
        //"dbchanges_notificationdb_notifications_mp_versioned.notifications_private_0_1_6",
        //"dbchanges_notificationdb_notificationsettings_mp_versioned.notificationsettings_0_1_3",
        //"dbchanges_notificationdb_notificationsettings_mp_versioned.notificationsettings_private_0_1_3",
        //"derived_conversion_tracking_userconversionevent_mp.conversion_tracking_userconversionevent_daily",
        //"derived_conversion_tracking_userconversionevent_mp.conversion_tracking_userconversionevent_daily_private",
        //"derived_conversion_tracking_userconversionevent_mp_versioned.conversion_tracking_userconversionevent_daily_0_1_5",
        //"derived_conversion_tracking_userconversionevent_mp_versioned.conversion_tracking_userconversionevent_daily_private_0_1_5",
        //"foundation_lms_mp.fact_f_sas_ad_impr",
        //"foundation_lms_mp.fact_f_sas_ad_click",
        //"foundation_lms_mp.fact_f_sas_ad_request",
        // non-dali function
        //"entity_handles_mp_versioned.phone_member_handles_0_1_16",
        // non-dali function
        //"feed_mp_versioned.decorated_feed_action_event_company_old_0_2_37",
        // fuzzy union
        //"feed_mp_versioned.feed_impression_event_0_2_37",
        // non-dali function
        //"feed_mp_versioned.decorated_feed_action_event_other_old_0_2_37",
        // outer join
        // "foundation_core_entity_mp_versioned.dim_msg_open_click_dedup_0_1_48",
        //"foundation_core_entity_mp_versioned.dim_msg_email_open_dedup_0_1_48",
        // window spec
        //"foundation_enterprise_mp_versioned.dim_leap_shares_0_1_6",
        //"foundation_core_entity_mp.dim_position",
        // error parsing view definition  Column 'date_sk' not found
        //"foundation_lms_mp_versioned.fact_detail_ad_impressions_0_1_13",
        // Column 'contract_id' not found in table
        //"foundation_lss_mp_versioned.sales_navigator_seat_metrics_private_0_1_7",
        // Column 'seat_id' not found
        //"foundation_lts_mp_versioned.agg_daily_cap_activity_0_1_24",
        // fuzzy union
        //"job_mp_versioned.job_view_event_jve_0_2_18",
        // fuzzy union
        //"profile_mp_versioned.profile_view_event_deduped_0_2_2",
        // cast conversion from bigint to timestamp
        //"salesforcecore_mp_versioned.opportunitylineitem_0_1_30",
    };
    for (String view : views) {
      try {
        translateTable(view, converter);
      } catch (Throwable t) {
        System.out.println(view);
        t.printStackTrace(System.out);
      }
    }

    /*translateTable("searchinputfocusevent_mp_versioned", "search_input_focus_event_lite_deduped_0_1_7",
        msc, converter);
    */
    // translateTable("foundation_enterprise_mp.fact_leap_broadcast_action_event", converter);
    // translateTable("foundation_enterprise_mp.fact_leap_employee_feed_impression_event", converter);
    // translateTable("foundation_enterprise_mp.fact_leap_follow_action_event", converter);
    // Hive to rel error
    // translateTable("foundation_enterprise_mp.fact_leap_profile_impression_event", converter);
    //translateTable("foundation_enterprise_mp.fact_leap_seat_action_event", converter);
    // translateTable("foundation_enterprise_mp.fact_leap_share_action_event", converter);
    // translateTable("foundation_enterprise_mp.fact_leap_topics_impression_event", converter);
    // translateTable("foundation_enterprise_mp.dim_leap_broadcast_tags", converter);

    //translateTable("access_log_mp_versioned", "aws_cloudtrail_event_base_0_3_1", msc, converter);
    //translateTable("ads_mp_versioned.follow_event_0_1_14", msc, converter);
    //translateAllViews(msc, converter, ostr);
  }

  // for debugging
  private void printTableInfo(Table table) {
    for (FieldSchema fieldSchema : table.getSd().getCols()) {
      System.out.println(fieldSchema.getName() + " : " + fieldSchema.getType());
    }
    System.out.println("Partitioning Columns");
    for (FieldSchema fieldSchema : table.getPartitionKeys()) {
      System.out.println(fieldSchema.getName() + " : " + fieldSchema.getType());
    }
  }

  private static class Stats {
    int datasets;

    int views;
    int failures;

    int daliviews;
    int daliviewfailures;
    int sqlFnErrors;

    @Override
    public String toString() {
      return String.format(
          "datasets = %d, views = %d, failures = %d, successes = %d, daliviews = %d, daliFailures = %d, sqlFnErrors = %d",
          datasets, views, failures, (views - failures), daliviews, daliviewfailures, sqlFnErrors);
    }
  }

  private static void translateAllViews(HiveMetastoreClient metaStoreClient, HiveToPrestoConverter converter,
      OutputStream ostr) {
    List<String> allDatabases = metaStoreClient.getAllDatabases();
    PrintWriter writer = new PrintWriter(ostr);
    Stats stats = new Stats();
    List<String> failures = new ArrayList<>();
    Map<String, Integer> errorCategories = new HashMap<>();
    Map<String, Integer> sqlFunctions = new HashMap<>();
    Set<String> translated = new HashSet<>();

    for (String db : allDatabases) {
      List<String> tables = metaStoreClient.getAllTables(db);
      // dali datasets have names ending with mp or mp_versioned
      // We focus only on dali datasets for the time-being because
      // processing all datasets is slow process
      if (!db.endsWith("_mp") && !db.endsWith("_mp_versioned")) {
        continue;
      }

      Set<String> maxVersionTables = latestViewVersions(tables);
      ++stats.datasets;
      boolean isDaliView = false;
      Table table = null;
      for (String tableName : maxVersionTables) {
        try {
          table = metaStoreClient.getTable(db, tableName);
          if (!table.getTableType().equalsIgnoreCase("virtual_view")) {
            continue;
          }
          ++stats.views;
          isDaliView = table.getOwner().equalsIgnoreCase("daliview");
          stats.daliviews += isDaliView ? 1 : 0;
          convertToPrestoAndValidate(db, tableName, converter);
        } catch (Exception e) {
          ++stats.failures;
          failures.add(db + "." + tableName);
          stats.daliviewfailures += isDaliView ? 1 : 0;
          if (e instanceof UnknownSqlFunctionException) {
            ++stats.sqlFnErrors;
            sqlFunctions.merge(((UnknownSqlFunctionException) e).getFunctionName(), 1, (i, j) -> i + j);
          }
          if (e instanceof RuntimeException && e.getCause() != null) {
            errorCategories.merge(e.getCause().getClass().getName(), 1, (i, j) -> i + j);
          } else {
            errorCategories.merge(e.getClass().getName(), 1, (i, j) -> i + j);
          }
        } catch (Throwable t) {
          writer.println(String.format("Unexpected error translating %s.%s, text: %s", db, tableName,
              (table == null ? "null" : table.getViewOriginalText())));
          ++stats.failures;
          failures.add(db + "." + tableName);
        }
        // set this to not carry over state for next iteration or when exiting
        table = null;
      }

      if (stats.datasets % 10 == 0) {
        writer.println(stats);
        writer.flush();
      }
    }

    writer.println("Failed datasets");
    failures.forEach(writer::println);
    writer.println("Error categories");
    errorCategories.forEach((x, y) -> writer.println(x + " : " + y));
    writer.println("Unknown functions");
    sqlFunctions.entrySet()
        .stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .forEach(e -> writer.println(String.format("%s:%d", e.getKey(), e.getValue())));
    writer.println(stats);
    writer.flush();
  }

  private static class ViewName {
    final String basename;
    final Version version;
    static final Version VERSION_ZERO = new Version(0, 0, 0);

    static ViewName create(String fullViewName) {
      Matcher m = VIEW_NAME_PATTERN.matcher(fullViewName);
      if (!m.matches()) {
        return new ViewName(fullViewName, new Version(0, 0, 0));
      }

      return new ViewName(m.group(1),
          new Version(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
    }

    ViewName(String name, Version v) {
      this.basename = name;
      this.version = v;
    }

    String getBasename() {
      return basename;
    }

    Version getVersion() {
      return version;
    }

    boolean isSameView(ViewName rhs) {
      return basename.equals(rhs.basename);
    }

    @Override
    public String toString() {
      return version.equals(VERSION_ZERO) ? basename : String.join("_", basename, version.toString());
    }
  }

  static class Version implements Comparable<Version> {
    int major;
    int minor;
    int patch;

    Version(int major, int minor, int patch) {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
    }

    @Override
    public int compareTo(Version rhs) {
      if (major > rhs.major) {
        return 1;
      }
      if (major < rhs.major) {
        return -1;
      }
      // major = rhs.major
      if (minor > rhs.minor) {
        return 1;
      }
      if (minor < rhs.minor) {
        return -1;
      }
      return Integer.compare(patch, rhs.patch);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Version)) {
        return false;
      }

      Version version = (Version) o;

      if (major != version.major) {
        return false;
      }
      if (minor != version.minor) {
        return false;
      }
      return patch == version.patch;
    }

    @Override
    public int hashCode() {
      int result = major;
      result = 31 * result + minor;
      result = 31 * result + patch;
      return result;
    }

    @Override
    public String toString() {
      return String.join("_", String.valueOf(major), String.valueOf(minor), String.valueOf(patch));
    }
  }

  public static Set<String> latestViewVersions(List<String> allViews) {
    allViews.sort(String::compareTo);
    ViewName selected = new ViewName("", new Version(0, 0, 0));
    //Map<String, List<ViewName>> viewGroups =
    Map<String, Version> maxViews = allViews.stream()
        .map(ViewName::create)
        .collect(Collectors.groupingBy(ViewName::getBasename,
            Collectors.reducing(new Version(0, 0, 0), ViewName::getVersion, BinaryOperator.maxBy(Version::compareTo))));

    Set<String> views = maxViews.entrySet()
        .stream()
        .map(k -> new ViewName(k.getKey(), k.getValue()).toString())
        .collect(Collectors.toSet());
    return views;
  }

  public static void translateTable(String dbTable, HiveToPrestoConverter converter) {
    String[] dbTableParts = dbTable.split("\\.");
    Preconditions.checkState(dbTableParts.length == 2,
        String.format("<db>.<table> format is required. Provided %s", dbTable));
    convertToPrestoAndValidate(dbTableParts[0], dbTableParts[1], converter);
  }

  private static void convertToPrestoAndValidate(String db, String table, HiveToPrestoConverter converter) {
    validatePrestoSql(toPrestoSql(db, table, converter));
  }

  private static String toPrestoSql(String db, String table, HiveToPrestoConverter converter) {
    checkNotNull(table);
    return converter.toPrestoSql(db, table);
  }

  private static void validatePrestoSql(String sql) {
    System.out.println(sql);
    SqlParser parser = new SqlParser();
    Statement statement = parser.createStatement(sql, new ParsingOptions());
    if (statement == null) {
      throw new RuntimeException("Failed to parse presto sql: " + sql);
    }
  }

  static class HiveMetaStoreClientAdapter implements HiveMetastoreClient {

    final IMetaStoreClient client;

    HiveMetaStoreClientAdapter(IMetaStoreClient client) {
      this.client = client;
    }

    @Override
    public List<String> getAllDatabases() {
      try {
        return client.getAllDatabases();
      } catch (TException e) {
        return ImmutableList.of();
      }
    }

    @Override
    public Database getDatabase(String dbName) {
      try {
        return client.getDatabase(dbName);
      } catch (TException e) {
        return null;
      }
    }

    @Override
    public List<String> getAllTables(String dbName) {
      try {
        return client.getAllTables(dbName);
      } catch (TException e) {
        return ImmutableList.of();
      }
    }

    @Override
    public Table getTable(String dbName, String tableName) {
      try {
        return client.getTable(dbName, tableName);
      } catch (TException e) {
        return null;
      }
    }
  }
}

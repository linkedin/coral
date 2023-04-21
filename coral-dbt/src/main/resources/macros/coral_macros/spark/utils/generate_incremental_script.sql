/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro spark__generate_incremental_script(coral_response, table_names, output_table) -%}

--     Compiled lines of spark scala code to be executed, separated by \n delimiter
  {% set spark_scala = '' %}

--     Ex.
--       sql: SELECT * FROM db.t1 UNION SELECT * FROM db.t2
--       table_names: ['db.t1', 'db.t2']
--
--       incremental_maintenance_sql: SELECT * FROM db_t1_delta UNION SELECT * FROM db_t2_delta
--       underscore_delimited_table_names: ['db_t1', 'db_t2']
--       incremental_table_names: ['db_t1_delta', 'db_t2_delta']
  {% set incremental_maintenance_sql = coral_response['incremental_maintenance_sql'] %}
  {% set underscore_delimited_table_names = coral_response['underscore_delimited_table_names'] %}
  {% set incremental_table_names = coral_response['incremental_table_names'] %}

--     Generate Iceberg incremental table scan code for each target table
--     Namespace variable used to persist changes beyond loop scope
  {% set ns = namespace(generated_sql='') %}
  {% for source_table in table_names %}
--     Timestamps are currently hard-coded to be the latest two timestamps; this
--     logic will change to use Jasper in the future
    {% set snapshot_id_retrieval_code =
        'val snapshot_df = spark.read.format("iceberg").load("' ~ source_table ~ '.snapshots")\n'
        ~ 'snapshot_df.createOrReplaceTempView("snapshot_temp_table")\n'
        ~ 'val snap_ids = spark.sql("SELECT snapshot_id FROM snapshot_temp_table ORDER BY committed_at DESC LIMIT 2")\n'
        ~ 'val start_snapshot_id = snap_ids.collect()(1)(0).toString\n'
        ~ 'val end_snapshot_id = snap_ids.collect()(0)(0).toString\n'
    %}
--     Time travel to get original table before additions
    {% set original_df_creation_code =
        'val df = spark.read.format("iceberg").option("snapshot-id", start_snapshot_id).load("' ~ source_table ~ '")\n'
        ~ 'df.createOrReplaceTempView("' ~ underscore_delimited_table_names[loop.index0] ~ '")\n'
    %}
--     Grab additions to table
    {% set delta_df_creation_code =
        'val df = spark.read.format("iceberg").option("start-snapshot-id", start_snapshot_id).option("end-snapshot-id", end_snapshot_id).load("' ~ source_table ~ '")\n'
        ~ 'df.createOrReplaceTempView("' ~ incremental_table_names[loop.index0] ~ '")\n'
    %}
    {% set ns.generated_sql = ns.generated_sql ~ snapshot_id_retrieval_code ~ original_df_creation_code ~ delta_df_creation_code %}
  {% endfor %}
  {% set spark_scala = spark_scala ~ ns.generated_sql %}

--     Generate code to execute the incremental query based on incremental table scans generated above
  {% set incremental_to_spark_scala =
    'val query_response = spark.sql("' ~ incremental_maintenance_sql.strip() ~ '")\n'
  %}
  {% set spark_scala = spark_scala ~ incremental_to_spark_scala %}

--     Insert incremental output into table
  {% set table_update_sql =
    'query_response.write.mode("append").saveAsTable("' ~ output_table ~ '")\n'
  %}
  {% set spark_scala = spark_scala ~ table_update_sql %}

  {{ return(spark_scala) }}

{%- endmacro %}

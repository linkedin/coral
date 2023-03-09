/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% materialization differential_read, adapter = 'spark' %}

  {%- set identifier = model['alias'] -%}
  {%- set target_relation = api.Relation.create(identifier=identifier,
                                                schema=schema,
                                                database=database,
                                                type='table') -%}
  {% set output_table = target_relation.include(database=false) %}

--   Ex.
--     sql: SELECT * FROM db.t1 UNION SELECT * FROM db.t2
--     table_names: ['db.t1', 'db.t2']
--
--     differential_sql: SELECT * FROM db_t1_delta UNION SELECT * FROM db_t2_delta
--     mod_table_names: ['db_t1_delta', 'db_t2_delta']
  {% set tbl_names = config.require('table_names') %}

  {% set coral_resp = coral_dbt.get_coral_delta(sql, tbl_names) %}
  {% set differential_sql = coral_resp['mod_query'] %}
  {% set mod_tbl_names = coral_resp['mod_tbl_names'] %}

--     Separate lines by \n delimiter
  {% set spark_sql = '' %}

--     Incremental read of each target table
  {% set ns = namespace(generated_sql='') %}
  {% for tbl in tbl_names %}
    {% set get_snapshot_id_code =
        'val snapshot_df = spark.read.format("iceberg").load("' ~ tbl ~ '.snapshots")\n'
        ~ 'snapshot_df.createOrReplaceTempView("snapshot_temp_tbl")\n'
        ~ 'val snap_ids = spark.sql("SELECT snapshot_id FROM snapshot_temp_tbl ORDER BY committed_at DESC LIMIT 2")\n'
        ~ 'val start_snapshot_id = snap_ids.collect()(1)(0).toString\n'
        ~ 'val end_snapshot_id = snap_ids.collect()(0)(0).toString\n'
    %}
    {% set create_df_code =
        'val df = spark.read.format("iceberg").option("start-snapshot-id", start_snapshot_id).option("end-snapshot-id", end_snapshot_id).load("' ~ tbl ~ '")\n'
        ~ 'df.createOrReplaceTempView("' ~ mod_tbl_names[loop.index0] ~ '")\n'
    %}
    {% set ns.generated_sql = ns.generated_sql ~ get_snapshot_id_code ~ create_df_code %}
  {% endfor %}
  {% set spark_sql = spark_sql ~ ns.generated_sql %}

--     Execute differential sql on dataframes created from incremental read
  {% set diff_to_spark_sql =
    'val query_resp = spark.sql("' ~ differential_sql.strip() ~ '")\n'
  %}
  {% set spark_sql = spark_sql ~ diff_to_spark_sql %}

--     Insert differential output into table
  {% set write_to_tbl_sql =
    'query_resp.write.mode("append").saveAsTable("' ~ output_table ~ '")\n'
  %}
  {% set spark_sql = spark_sql ~ write_to_tbl_sql %}
  {{ print("[DIFF_READ_DEBUG] Insert into table:\n" ~ spark_sql) }}

--     Execute spark sql
  {% call statement('main') -%}
    $$spark$$
    {{ spark_sql }}
  {%- endcall %}

--     Will persist tables via scala and not add to cache
  {{ return({'relations': []})}}

{% endmaterialization %}

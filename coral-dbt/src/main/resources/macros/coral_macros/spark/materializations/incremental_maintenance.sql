/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% materialization incremental_maintenance, adapter = 'spark' %}

--     Grab target table to write results to
  {%- set identifier = model['alias'] -%}
  {%- set target_relation = api.Relation.create(identifier=identifier,
                                                schema=schema,
                                                database=database,
                                                type='table') -%}
  {% set output_table = target_relation.include(database=false) %}

--     Ex.
--       sql: SELECT * FROM db.t1 UNION SELECT * FROM db.t2
--       table_names: ['db.t1', 'db.t2']
--
--       coral_response['incremental_maintenance_sql']: SELECT * FROM db_t1_delta UNION SELECT * FROM db_t2_delta
--       coral_response['underscore_delimited_table_names']: ['db_t1', 'db_t2']
--       coral_response['incremental_table_names']: ['db_t1_delta', 'db_t2_delta']
  {% set table_names = config.require('table_names') %}
  {% set coral_incremental_url = coral_dbt.get_coral_url() ~ '/api/incremental/rewrite' %}
  {% set sql_dialect = 'spark' %}
  {% set coral_response = coral_dbt.get_coral_incremental_response(sql, coral_incremental_url, table_names, sql_dialect) %}

--     Generate spark scala for incremental maintenance logic
  {% set spark_scala = coral_dbt.generate_incremental_script(coral_response, table_names, output_table) %}

--     Execute spark scala
  {% call statement('main') -%}
    $$spark$$
    {{ spark_scala }}
  {%- endcall %}

--     Will persist tables via scala and not add to cache
  {{ return({'relations': []})}}

{% endmaterialization %}

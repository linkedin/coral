/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro get_coral_incremental_response(sql, url, table_names, sql_dialect) %}
  {{ return(adapter.dispatch('get_coral_incremental_response', macro_namespace = 'coral_dbt')(sql, url, table_names, sql_dialect)) }}
{% endmacro %}


{% macro default__get_coral_incremental_response(sql, url, table_names, sql_dialect) -%}
    {% set requests = modules.requests %}
    {% set request_data = {
        "query": sql,
        "tableNames": table_names,
        "language": sql_dialect,
    } %}
    {% set response = requests.post(url, json=request_data) %}

--     Endpoint response contains incremental maintenance query and incremental table names
    {% set coral_incremental_response = response.json() %}
    {{ return(coral_incremental_response) }}
{%- endmacro %}

/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro spark__get_coral_incremental_response(sql, table_names) -%}

    {% set requests = modules.requests %}
    {% set url = coral_dbt.get_coral_url() ~ '/api/incremental/rewrite' %}
    {% set request_data = {
        "query": sql,
        "tableNames": table_names,
        "language": "spark",
    } %}
    {% set response = requests.post(url, json=request_data) %}

--     Endpoint response contains incremental maintenance query and incremental table names
    {% set coral_incremental_response = response.json() %}
    {{ return(coral_incremental_response) }}

{%- endmacro %}

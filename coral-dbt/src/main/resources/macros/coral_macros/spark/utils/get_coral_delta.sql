/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro spark__get_coral_delta(sql, tbl_names) -%}

    {% set requests = modules.requests %}
    {% set url = coral_dbt.get_coral_url() ~ '/api/differential/execute' %}
    {% set request_data = {
        "query": sql,
        "tblNames": tbl_names,
    } %}
    {% set response = requests.post(url, json=request_data) %}

--     Endpoint response contains modified query and modified table names
    {% set differential_sql = response.json() %}
    {{ return(differential_sql) }}

{%- endmacro %}

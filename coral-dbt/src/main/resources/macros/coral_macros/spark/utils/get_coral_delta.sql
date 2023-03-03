/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro spark__get_coral_delta(sql) -%}

--     TODO: Get rid of conditional once endpoint created
    {% if true %}
        {{ return({
            'mod_query': 'SELECT * FROM db_t1_delta UNION SELECT * FROM db_t2_delta',
            'tbl_names': ['db.t1', 'db.t2'],
            'mod_tbl_names': ['db_t1_delta', 'db_t2_delta']
        }) }}
    {% else %}
        {% set requests = modules.requests %}
    --     This url endpoint does not exist yet
        {% set url = 'http://localhost:8080/api/differential/execute' %}
        {% set request_data = {
            "query": sql
        } %}
        {% set response = requests.post(url, json=request_data) %}
        {% set differential_sql = response.text %}
        {{ return(differential_sql) }}
    {% endif %}

{%- endmacro %}

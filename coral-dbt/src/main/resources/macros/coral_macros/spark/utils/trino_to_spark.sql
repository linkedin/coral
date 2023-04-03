/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro spark__trino_to_spark(sql) -%}

    {% set requests = modules.requests %}
    {% set url = coral_dbt.get_coral_url() ~ '/api/translations/translate' %}
    {% set request_data = {
        "fromLanguage": "trino",
        "toLanguage": "spark",
        "query": sql
    } %}
    {% set response = requests.post(url, json=request_data) %}
    {% set spark_sql = response.text.split(':')[2] %}
    {{ return(spark_sql) }}

{%- endmacro %}

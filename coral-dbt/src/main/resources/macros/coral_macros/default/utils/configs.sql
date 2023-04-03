/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro get_coral_url() %}
  {{ return(adapter.dispatch('get_coral_url', macro_namespace = 'coral_dbt')()) }}
{% endmacro %}


{% macro default__get_coral_url() -%}
    {% set default_coral_url = 'http://localhost:8080' %}
    {{ return(var("coral_url", default_coral_url)) }}
{%- endmacro %}

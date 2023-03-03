/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro get_coral_delta(sql) %}
  {{ return(adapter.dispatch('get_coral_delta', macro_namespace = 'coral_dbt')(sql)) }}
{% endmacro %}


{% macro default__get_coral_delta(sql) -%}
    {{ exceptions.raise_compiler_error("macro get_coral_delta not implemented for adapters other than Spark") }}
{%- endmacro %}

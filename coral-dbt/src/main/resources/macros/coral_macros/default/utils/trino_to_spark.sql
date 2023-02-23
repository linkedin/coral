/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro trino_to_spark(sql) %}
  {{ return(adapter.dispatch('trino_to_spark', macro_namespace = 'coral_dbt')(sql)) }}
{% endmacro %}


{% macro default__trino_to_spark(sql) -%}
    {{ exceptions.raise_compiler_error("macro trino_to_spark not implemented for adapters other than Spark") }}
{%- endmacro %}

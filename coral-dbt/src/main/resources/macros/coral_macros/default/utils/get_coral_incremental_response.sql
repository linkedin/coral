/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro get_coral_incremental_response(sql, tbl_names) %}
  {{ return(adapter.dispatch('get_coral_incremental_response', macro_namespace = 'coral_dbt')(sql, tbl_names)) }}
{% endmacro %}


{% macro default__get_coral_incremental_response(sql, tbl_names) -%}
    {{ exceptions.raise_compiler_error("macro get_coral_incremental_response not implemented for adapters other than Spark") }}
{%- endmacro %}

/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro incremental_code_generation(coral_response, table_names, output_table) %}
  {{ return(adapter.dispatch('incremental_code_generation', macro_namespace = 'coral_dbt')(coral_response, table_names, output_table)) }}
{% endmacro %}


{% macro default__incremental_code_generation(coral_response, table_names, output_table) -%}
    {{ exceptions.raise_compiler_error("macro incremental_code_generation not implemented for adapters other than Spark") }}
{%- endmacro %}

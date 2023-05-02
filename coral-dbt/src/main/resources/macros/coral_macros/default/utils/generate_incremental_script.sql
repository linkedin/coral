/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
{% macro generate_incremental_script(coral_response, table_names, output_table) %}
  {{ return(adapter.dispatch('generate_incremental_script', macro_namespace = 'coral_dbt')(coral_response, table_names, output_table)) }}
{% endmacro %}


{% macro default__generate_incremental_script(coral_response, table_names, output_table) -%}
    {{ exceptions.raise_compiler_error("macro generate_incremental_script not implemented for adapters other than Spark") }}
{%- endmacro %}

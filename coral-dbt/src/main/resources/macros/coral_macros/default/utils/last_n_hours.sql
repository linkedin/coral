{% macro last_n_hours(hours=8) %}
{%- set current_datetime_pst = in_dbt_utils.logical_date(timezone="US/Pacific") -%}
{%- set target_datetime_pst =  current_datetime_pst - modules.datetime.timedelta(hours=hours) -%}
"{{ target_datetime_pst.strftime('%Y-%m-%d-%H') }}"
{% endmacro %}
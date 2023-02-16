{% macro spark__trino_to_spark(sql) -%}

    {% set requests = modules.requests %}
    {% set url = 'http://localhost:8080/api/translations/translate' %}
    {% set request_data = {
        "fromLanguage": "trino",
        "toLanguage": "spark",
        "query": sql
    } %}
    {% set response = requests.post(url, json=request_data) %}
    {% set spark_sql = response.text.split(':')[2] %}
    {{ return(spark_sql) }}

{%- endmacro %}

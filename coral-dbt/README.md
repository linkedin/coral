# Using coral-dbt Materialization Modes

Note: This project is currently in development and is not intended to be a production ready package. Currently, only Spark adapter is supported.

## Setup
1. The materialization mode in this package requires a minor modification to the dbt-core source. Clone and setup a local dbt-core source and create/activate a Python virtual environment.
2. Make the following modifications to `core/dbt/context/base.py` in your local dbt-core source:
```
import requests

def get_requests_module_context() -> Dict[str, Any]:
    context_exports = ["get", "post"]

    return {name: getattr(requests, name) for name in context_exports}
    
def get_context_modules() -> Dict[str, Dict[str, Any]]:
    return {
        "pytz": get_pytz_module_context(),
        "datetime": get_datetime_module_context(),
        "re": get_re_module_context(),
        "itertools": get_itertools_module_context(),
        "requests": get_requests_module_context(),
    }
```

3. Add this package to your dbt project by creating or modifying `packages.yml` and run `dbt deps` to install the package.

```
packages:
  - git: "https://github.com/linkedin/coral.git"
    revision: master
    subdirectory: coral-dbt/src/main/resources
```

4. Modify your `dbt_project.yaml` by adding the following line. (The absence of anything after the colon is intentional.)
```
query-comment:
```
5. Follow the instructions in the main project README to start up Coral Service. Currently, the url will default to [http://localhost:8080](http://localhost:8080). To modify this, you can either:
   * Add `coral_url` as a variable in your `dbt_project.yaml` as follows:
   ```
    vars:
      coral_url: <your_coral_url>
    ```
   * Or, clone the package (and change `packages.yml` to point to your package version) and modify the `default_coral_url` in `default/utils/configs.sql`
   ```
    {% set default_coral_url = <your_coral_url> %}
    ```

## Additional Setup
### Incremental Maintenance
In your models, specify the names of the tables your query depends on with the `table_names` config. An example model looks as follows:
```
{{
  config(
    materialized='incremental_maintenance',
    table_names=['db.t1', 'db.t2'],
  )
}}

SELECT * FROM db.t1 UNION SELECT * FROM db.t2
```

## Tests

### Running Tests

These tests are automatically run as part of the Gradle build process (`./gradlew build`) but can be manually triggered by `cd`'ing into the `src/main/resources/tests/` directory and running:

```
python3 -m unittest -v
```

Please refer to the Python `unittest` [module documentation](https://docs.python.org/3/library/unittest.html) for more information on running tests.

### Writing Tests

It is encouraged to write tests using the `unittest` module so that they will run automatically with the build process.

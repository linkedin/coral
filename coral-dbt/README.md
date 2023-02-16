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
3. Add this package to your dbt project by creating or modifying `packages.yml`.

```
packages:
  - git: "https://github.com/linkedin/coral.git"
    revision: master
    subdirectory: coral-dbt
```

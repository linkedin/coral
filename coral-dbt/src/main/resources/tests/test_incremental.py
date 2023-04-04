#
# Copyright 2023 LinkedIn Corporation. All rights reserved.
# Licensed under the BSD-2 Clause license.
# See LICENSE in the project root for license information.
#
import subprocess
import requests
import unittest
from jinja2 import Template

SEEDS_DIR = 'seeds/'
URL = 'http://localhost:8080/api/incremental/rewrite'

class TestUtils:
    @staticmethod
    def construct_incremental_code_generation_test(name):
        output_table = name + '_output'
        expected_file = open(SEEDS_DIR + 'test_' + name + '_expected.txt', 'r')
        expected_incremental_sql = expected_file.read()
        expected_file.close()

        return({
            'output_table': output_table,
            'expected_incremental_sql': expected_incremental_sql,
        })

    @staticmethod
    def test_incremental_code_generation(coral_response, table_names, output_table):
        with open('incremental_code_generation.j2') as f:
            template = Template(f.read())

        context = {'coral_response': coral_response, 'table_names': table_names, 'output_table': output_table}
        output = template.render(context)

        return output

class TestIncremental(unittest.TestCase):
    maxDiff = None

    @classmethod
    def setUpClass(self):
        subprocess.run(["chmod", "+x", "./setup_test.sh"])
        subprocess.run(["./setup_test.sh", "../macros/coral_macros/spark/utils/incremental_code_generation.sql",
                        "incremental_code_generation.j2"])

    def test_simple_select_all(self):
        name = "simple_select_all"
        table_names = ["foo"]
        mocked_coral_response = {
            "incremental_maintenance_sql": "SELECT * FROM default.foo_delta AS foo_delta",
            "incremental_table_names": ["foo_delta"],
            "temp_view_table_names": ["foo"]
        }
        test_vars = TestUtils.construct_incremental_code_generation_test(name)
        self.assertEqual(TestUtils.test_incremental_code_generation(mocked_coral_response, table_names, test_vars['output_table']).strip(), test_vars['expected_incremental_sql'].strip())

    def test_join(self):
        name = "join"
        table_names = ["bar1", "bar2"]
        mocked_coral_response = {
            "incremental_maintenance_sql": "SELECT *\n" + "FROM (SELECT *\n" + "FROM default.bar1 AS bar1\n"
                                           + "INNER JOIN default.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
                                           + "FROM default.bar1_delta AS bar1_delta\n" + "INNER JOIN default.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t\n"
                                           + "UNION ALL\n" + "SELECT *\n" + "FROM default.bar1_delta AS bar1_delta0\n"
                                           + "INNER JOIN default.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x",
            "incremental_table_names": ["bar1_delta", "bar2_delta"],
            "temp_view_table_names": ["bar1", "bar2"]
        }
        test_vars = TestUtils.construct_incremental_code_generation_test(name)
        self.assertEqual(TestUtils.test_incremental_code_generation(mocked_coral_response, table_names, test_vars['output_table']).strip(), test_vars['expected_incremental_sql'].strip())

    @classmethod
    def tearDownClass(self):
        subprocess.run(["chmod", "+x", "./cleanup_test.sh"])
        subprocess.run(["./cleanup_test.sh", "incremental_code_generation.j2"])
        subprocess.run(["./cleanup_test.sh", "incremental_code_generation.j2-e"])

if __name__ == '__main__':
    unittest.main()

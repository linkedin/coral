from jinja2 import Template
import subprocess
import requests
import unittest

SEEDS_DIR = 'seeds/'
URL = 'http://localhost:8080/api/incremental/rewrite'

class TestUtils:
    @staticmethod
    def construct_incremental_code_generation_test(sql, table_names, name):
        output_table = name + '_output'
        simple_select_all_file = open(SEEDS_DIR + 'test_' + name + '_expected.txt', 'r')
        expected_incremental_sql = simple_select_all_file.read()
        simple_select_all_file.close()
        request_data = {
            'query': sql,
            'tableNames': table_names
        }

        return({
            'output_table': output_table,
            'expected_incremental_sql': expected_incremental_sql,
            'request_data': request_data
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
        name = 'simple_select_all'
        table_names = ['foo']
        test_vars = TestUtils.construct_incremental_code_generation_test(
            sql='SELECT * FROM foo',
            table_names=table_names,
            name=name
        )
        response = requests.post(URL, json=test_vars['request_data']).json()
        self.assertEqual(TestUtils.test_incremental_code_generation(response, table_names, test_vars['output_table']).strip(), test_vars['expected_incremental_sql'].strip())

    def test_join(self):
        name = 'join'
        table_names = ['foo', 'bar']
        test_vars = TestUtils.construct_incremental_code_generation_test(
            sql='SELECT * FROM foo INNER JOIN bar ON foo.x = bar.x',
            table_names=table_names,
            name=name
        )
        response = requests.post(URL, json=test_vars['request_data']).json()
        self.assertEqual(TestUtils.test_incremental_code_generation(response, table_names, test_vars['output_table']).strip(), test_vars['expected_incremental_sql'].strip())

    @classmethod
    def tearDownClass(self):
        subprocess.run(["chmod", "+x", "./cleanup_test.sh"])
        subprocess.run(["./cleanup_test.sh", "incremental_code_generation.j2"])
        subprocess.run(["./cleanup_test.sh", "incremental_code_generation.j2-e"])

if __name__ == '__main__':
    unittest.main()

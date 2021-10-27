-- database: presto; tables: nation, workers; groups: set_operation;
-- delimiter: |; ignoreOrder: true;
--! name: except_union_intersect
SELECT n_name FROM nation WHERE n_nationkey = 17
EXCEPT
SELECT n_name FROM nation WHERE n_regionkey = 2
UNION
(SELECT n_name FROM nation WHERE n_regionkey = 2
INTERSECT
SELECT n_name FROM nation WHERE n_nationkey > 15)

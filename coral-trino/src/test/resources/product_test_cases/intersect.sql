-- database: presto; tables: nation, workers; groups: set_operation;
-- delimiter: |; ignoreOrder: true;
--! name: intersect_and_union
SELECT n_name FROM nation WHERE n_nationkey = 17 
INTERSECT 
SELECT n_name FROM nation WHERE n_regionkey = 1 
UNION 
SELECT n_name FROM nation WHERE n_regionkey = 2
--!

-- database: presto; groups: join; tables: nation, region
SELECT n_name,
       r_name
FROM   nation
WHERE  r_regionkey != n_nationkey


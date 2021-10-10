-- database: presto; groups: tpch; tables: lineitem,supplier
SELECT
  s_suppkey,
  s_name,
  s_address,
  s_phone,
  total_revenue
FROM
  revenue
WHERE
  s_suppkey = supplier_no
  AND total_revenue = (
    SELECT max(total_revenue)
    FROM
      revenue
  )
ORDER BY
  s_suppkey

select `l_orderkey`, sum(`l_extendedprice` * (1 - `l_discount`)) as `revenue`, `o_orderdate`, `o_shippriority`
from `lineitem`
where `c_mktsegment` = 'building' and `c_custkey` = `o_custkey` and `l_orderkey` = `o_orderkey` and `o_orderdate` < date '1995-03-15' and `l_shipdate` > date '1995-03-15'
group by `l_orderkey`, `o_orderdate`, `o_shippriority`
order by `revenue` desc, `o_orderdate`
fetch next 10 rows only
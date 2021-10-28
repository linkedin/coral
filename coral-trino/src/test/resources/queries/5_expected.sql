select `n_name`, sum(`l_extendedprice` * (1 - `l_discount`)) as `revenue`
from `region`
where `c_custkey` = `o_custkey` and `l_orderkey` = `o_orderkey` and `l_suppkey` = `s_suppkey` and `c_nationkey` = `s_nationkey` and `s_nationkey` = `n_nationkey` and `n_regionkey` = `r_regionkey` and `r_name` = 'region name' and `o_orderdate` >= date '2013-03-05' and `o_orderdate` < date '2013-03-05' + interval '1' year
group by `n_name`
order by `revenue` desc
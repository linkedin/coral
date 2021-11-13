select `l_shipmode`, sum(case when `o_orderpriority` = '1-urgent' or `o_orderpriority` = '2-high' then 1 else 0 end) as `high_line_count`, sum(case when `o_orderpriority` <> '1-urgent' and `o_orderpriority` <> '2-high' then 1 else 0 end) as `low_line_count`
from `lineitem`
where `o_orderkey` = `l_orderkey` and `l_shipmode` in ('ship mode 1', 'ship mode 2') and `l_commitdate` < `l_receiptdate` and `l_shipdate` < `l_commitdate` and `l_receiptdate` >= date '2013-03-05' and `l_receiptdate` < date '2013-03-05' + interval '1' year
group by `l_shipmode`
order by `l_shipmode`
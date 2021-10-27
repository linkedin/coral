-- $ID$
-- TPC-H/TPC-R Shipping Priority Query (Q3)
-- Functional Query Definition
-- Approved February 1998


select
	l_orderkey,
	sum(l_extendedprice * (1 - l_discount)) as revenue,
	o_orderdate,
	o_shippriority
from
	lineitem
where
	c_mktsegment = 'market segment'
	and c_custkey = o_custkey
	and l_orderkey = o_orderkey
	and o_orderdate < date '2013-03-05'
	and l_shipdate > date '2013-03-05'
group by
	l_orderkey,
	o_orderdate,
	o_shippriority
order by
	revenue desc,
	o_orderdate
LIMIT 10

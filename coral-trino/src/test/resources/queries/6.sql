-- $ID$
-- TPC-H/TPC-R Forecasting Revenue Change Query (Q6)
-- Functional Query Definition
-- Approved February 1998


select
	sum(l_extendedprice * l_discount) as revenue
from
	lineitem
where
	l_shipdate >= date '2013-03-05'
	and l_shipdate < date '2013-03-05' + interval '1' year
	and l_discount between 33 - 0.01 and 33 + 0.01
	and l_quantity < 44


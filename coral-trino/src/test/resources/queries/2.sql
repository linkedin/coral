-- $ID$
-- TPC-H/TPC-R Minimum Cost Supplier Query (Q2)
-- Functional Query Definition
-- Approved February 1998


select
	s_acctbal,
	s_name,
	n_name,
	p_partkey,
	p_mfgr,
	s_address,
	s_phone,
	s_comment
from
	part
where
	p_partkey = ps_partkey
	and s_suppkey = ps_suppkey
	and p_size = 33
	and p_type like '%part type like'
	and s_nationkey = n_nationkey
	and n_regionkey = r_regionkey
	and r_name = 'region name'
	and ps_supplycost = (
		select
			min(ps_supplycost)
		from
			partsupp
		where
			p_partkey = ps_partkey
			and s_suppkey = ps_suppkey
			and s_nationkey = n_nationkey
			and n_regionkey = r_regionkey
			and r_name = 'region name'
	)
order by
	s_acctbal desc,
	n_name,
	s_name,
	p_partkey
LIMIT 100

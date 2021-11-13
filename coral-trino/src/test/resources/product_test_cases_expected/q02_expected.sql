select `s_acctbal`, `s_name`, `n_name`, `p_partkey`, `p_mfgr`, `s_address`, `s_phone`, `s_comment`
from `part`
where `p_partkey` = `ps_partkey` and `s_suppkey` = `ps_suppkey` and `p_size` = 15 and `p_type` like '%brass' and `s_nationkey` = `n_nationkey` and `n_regionkey` = `r_regionkey` and `r_name` = 'europe' and `ps_supplycost` = (select min(`ps_supplycost`)
from `region`
where `p_partkey` = `ps_partkey` and `s_suppkey` = `ps_suppkey` and `s_nationkey` = `n_nationkey` and `n_regionkey` = `r_regionkey` and `r_name` = 'europe')
order by `s_acctbal` desc, `n_name`, `s_name`, `p_partkey`
fetch next 100 rows only
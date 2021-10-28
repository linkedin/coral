select `ps_partkey`, sum(`ps_supplycost` * `ps_availqty`) as `value`
from `nation`
where `ps_suppkey` = `s_suppkey` and `s_nationkey` = `n_nationkey` and `n_name` = 'nation name'
group by `ps_partkey`
having sum(`ps_supplycost` * `ps_availqty`) > (select sum(`ps_supplycost` * `ps_availqty`) * 33
from `nation`
where `ps_suppkey` = `s_suppkey` and `s_nationkey` = `n_nationkey` and `n_name` = 'nation name')
order by `value` desc
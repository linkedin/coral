select `p_partkey`, `n_name`
from `nation`
left join `part` on `n_nationkey` = `p_partkey`
where `n_name` < `p_name`
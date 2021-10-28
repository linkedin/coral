select `p_partkey`, `n_name`
from `nation`
inner join `part` on `n_nationkey` = `p_partkey`
where `n_name` < `p_name`
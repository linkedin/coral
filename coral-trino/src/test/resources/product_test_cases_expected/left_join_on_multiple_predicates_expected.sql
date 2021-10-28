select `n_name`, `p_name`
from `nation`
left join `part` on `n_regionkey` = `p_partkey` and `n_name` = `p_name`
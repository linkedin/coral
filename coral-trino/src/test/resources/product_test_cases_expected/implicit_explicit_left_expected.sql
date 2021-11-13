select `p_partkey`, `n_name`, `r_name`
from `nation`
left join `part` on `r_regionkey` = `p_partkey`
where `n_nationkey` = `r_regionkey`
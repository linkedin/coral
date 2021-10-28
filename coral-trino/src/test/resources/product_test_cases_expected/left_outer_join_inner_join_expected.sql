select `p_partkey`, `n_name`, `r_name`
from `nation`
left join `region` on `n_nationkey` = `r_regionkey`
inner join `part` on `n_regionkey` = `p_partkey`
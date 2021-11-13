select `p_partkey`, `n_name`, `r_name`
from `part`
right join `nation` on `n_regionkey` = `p_partkey`
left join `region` on `n_nationkey` = `r_regionkey`
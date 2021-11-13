select `p_partkey`, `n_name`, `r_name`
from `part`
inner join `nation` on `n_regionkey` = `p_partkey`
right join `region` on `n_nationkey` = `r_regionkey`
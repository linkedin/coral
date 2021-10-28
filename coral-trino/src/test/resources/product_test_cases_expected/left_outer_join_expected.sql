select `n_name`, `r_name`
from `nation`
left join `region` on `n_nationkey` = `r_regionkey`
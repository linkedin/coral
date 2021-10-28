select `n_name`, `r_name`
from `region`
right join `nation` on `n_nationkey` = `r_regionkey`
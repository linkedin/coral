select `n_name`
from `nation`
left join `region` on `n_nationkey` = `r_regionkey`
where `r_name` is not null
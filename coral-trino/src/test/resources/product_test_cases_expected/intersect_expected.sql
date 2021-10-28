select `n_name`
from `nation`
where `n_nationkey` = 17
intersect
select `n_name`
from `nation`
where `n_regionkey` = 1
union
select `n_name`
from `nation`
where `n_regionkey` = 2
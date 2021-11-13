select `n_name`
from `nation`
where `n_nationkey` = 17
except
select `n_name`
from `nation`
where `n_regionkey` = 2
union
select `n_name`
from `nation`
where `n_regionkey` = 2
intersect
select `n_name`
from `nation`
where `n_nationkey` > 15
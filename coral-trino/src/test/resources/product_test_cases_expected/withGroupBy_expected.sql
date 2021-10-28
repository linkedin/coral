select distinct `n_regionkey`, count(*)
from `nation`
where `n_nationkey` > 0
group by `n_regionkey`
select `n_regionkey`, count(null)
from `nation`
where `n_nationkey` > 5
group by `n_regionkey`
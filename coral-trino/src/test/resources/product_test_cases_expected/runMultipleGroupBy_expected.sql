select count(*), `n_regionkey`, `n_nationkey`
from `nation`
where `n_regionkey` < 2
group by `n_nationkey`, `n_regionkey`
order by `n_regionkey`, `n_nationkey` desc
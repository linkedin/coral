select count(*)
from `nation`
union all
select sum(`n_nationkey`)
from `nation`
group by `n_regionkey`
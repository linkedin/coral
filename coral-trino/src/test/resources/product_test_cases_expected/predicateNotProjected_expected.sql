select `n_nationkey`
from `nation`
where `n_name` < 'india'
order by `n_nationkey`
fetch next 3 rows only
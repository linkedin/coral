with `ordered` as (select `n_nationkey` as `a`, `n_regionkey` as `b`, `n_name` as `c`
from `nation`
order by 1, 2
fetch next 10 rows only) (select *
from `ordered`
order by 1, 2
fetch next 5 rows only)
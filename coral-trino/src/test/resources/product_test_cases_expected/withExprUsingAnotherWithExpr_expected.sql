with `w1` as (select min(`n_nationkey`) as `x`, max(`n_regionkey`) as `y`
from `nation`), `w2` as (select `x`, `y`
from `w1`) (select count(*) as `count`, `n_regionkey`
from `nation`
group by `n_regionkey`
union all
select *
from `w2`)
order by `n_regionkey`, `count`
with `wregion` as (select min(`n_regionkey`)
from `nation`
where `n_name` >= 'n') (select `r_regionkey`, `r_name`
from `region`
where `r_regionkey` in (select *
from `wregion`))
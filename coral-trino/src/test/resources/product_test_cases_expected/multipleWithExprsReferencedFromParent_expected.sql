with `wnation` as (select `n_nationkey`, `n_regionkey`
from `nation`), `wregion` as (select `r_regionkey`, `r_name`
from `region`) (select `n`.`n_nationkey`, `r`.`r_regionkey`
from `wnation` as `n`
inner join `wregion` as `r` on `n`.`n_regionkey` = `r`.`r_regionkey`
where `r`.`r_name` = 'africa')
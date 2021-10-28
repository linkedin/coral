with `wnation` as (select `n_name`, `n_nationkey`, `n_regionkey`
from `nation`) (select `n1`.`n_name`, `n2`.`n_name`
from `wnation` as `n1`
inner join `wnation` as `n2` on `n1`.`n_nationkey` = `n2`.`n_regionkey`)
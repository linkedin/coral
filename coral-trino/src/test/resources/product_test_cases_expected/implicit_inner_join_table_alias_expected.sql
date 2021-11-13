select `n`.`n_name`, `r`.`r_name`
from `nation` as `n`
where `n`.`n_regionkey` = `r`.`r_regionkey`
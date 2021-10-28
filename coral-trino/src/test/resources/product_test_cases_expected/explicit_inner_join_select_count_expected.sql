select count(*)
from `nation`
inner join `region` on `nation`.`n_regionkey` = `region`.`r_regionkey`
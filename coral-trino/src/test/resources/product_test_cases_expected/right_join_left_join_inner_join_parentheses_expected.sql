select `p_partkey`, `n2`.`n_name`, `r_name`
from `part`
right join `nation` as `n1` on `n1`.`n_regionkey` = `p_partkey`
left join `region` on `n1`.`n_nationkey` = `r_regionkey`
inner join `nation` as `n2` on `n2`.`n_nationkey` = `r_regionkey`
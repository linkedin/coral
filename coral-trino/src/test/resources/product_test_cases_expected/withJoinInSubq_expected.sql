select count(*)
from (select *
from `nation` as `n1`
inner join `nation` as `n2` on `n1`.`n_regionkey` = `n2`.`n_regionkey`
order by
fetch next 5 rows only) as `foo`
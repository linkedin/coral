select `foo`.`c`, `foo`.`n_regionkey`
from (select `n_regionkey`, count(*) as `c`
from `nation`
group by `n_regionkey`
order by `n_regionkey`
fetch next 2 rows only) as `foo`
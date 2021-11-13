select count(*)
from (select *
from `nation`
order by
fetch next 0 rows only) as `foo`
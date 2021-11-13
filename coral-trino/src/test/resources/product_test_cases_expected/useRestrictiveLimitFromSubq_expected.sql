select count(*)
from (select *
from `nation`
order by
fetch next 2 rows only) as `foo`
order by
fetch next 5 rows only
select count(*)
from (select *
from `nation`
order by
fetch next 10 rows only) as `t1`
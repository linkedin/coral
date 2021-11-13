select count(*)
from (select `ps_suppkey`, count(*)
from `partsupp`
group by `ps_suppkey`
order by
fetch next 20 rows only) as `t1`
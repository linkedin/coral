select `nationkey`, `regionkey`, `name`
from (select `regionkey`, `nationkey`, `name`
from `tpch`.`tiny`.`nation`
where `nationkey` < 20
order by 2 desc
fetch next 5 rows only) as `t`
order by 2, 1
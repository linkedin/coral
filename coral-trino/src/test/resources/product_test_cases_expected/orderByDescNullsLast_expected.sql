select *
from (select cast(null as bigint)
union all
select 1) as `t`
order by 1 desc nulls last
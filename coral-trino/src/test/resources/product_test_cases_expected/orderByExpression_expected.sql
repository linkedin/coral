select `totalprice` * 1.0625, `custkey`
from `tpch`.`tiny`.`orders`
order by 1
fetch next 20 rows only
select `custkey`, `orderstatus`
from `tpch`.`tiny`.`orders`
order by `totalprice` * 1.0625
fetch next 20 rows only
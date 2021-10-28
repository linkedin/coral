select avg(`retailprice`), `mfgr`
from `tpch`.`tiny`.`part`
group by 2
order by count(*)
fetch next 20 rows only
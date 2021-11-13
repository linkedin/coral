select `orderkey`, `discount`, `extendedprice`, min(`extendedprice`) over (order by `discount` range current_row) as `min_extendedprice`, max(`extendedprice`) over (order by `discount` range current_row) as `max_extendedprice`
from `tpch`.`tiny`.`lineitem`
where `partkey` = 272
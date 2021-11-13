select `orderkey`, `suppkey`, `discount`, lead(`discount`) over (partition by `suppkey` order by `orderkey` desc) as `next_discount`, `extendedprice`, lag(`extendedprice`) over (partition by `discount` order by `extendedprice` range current_row) as `previous_extendedprice`
from `tpch`.`tiny`.`lineitem`
where `partkey` = 272
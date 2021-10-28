select `orderkey`, `suppkey`, `extendedprice`, first_value(`extendedprice`) over (partition by `suppkey` order by `extendedprice` desc rows between unbounded_preceding and unbounded_following), last_value(`extendedprice`) over (partition by `suppkey` order by `extendedprice` desc rows between unbounded_preceding and unbounded_following)
from `tpch`.`tiny`.`lineitem`
where `partkey` = 272
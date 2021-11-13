select `orderkey`, `suppkey`, `quantity`, round(sum(`quantity`) over (partition by `suppkey` order by `orderkey` rows between unbounded_preceding and `preceding`(2)), 5) as `total_quantity`
from `tpch`.`tiny`.`lineitem`
where `partkey` = 272
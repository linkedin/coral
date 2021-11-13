select `orderkey`, `suppkey`, `quantity`, round(sum(`quantity`) over (partition by `suppkey` order by `orderkey` rows unbounded_preceding), 5) as `total_quantity`
from `tpch`.`tiny`.`lineitem`
where `partkey` = 272
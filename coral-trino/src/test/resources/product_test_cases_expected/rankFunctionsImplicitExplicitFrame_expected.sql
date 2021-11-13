select `orderkey`, `discount`, dense_rank() over (order by `discount`), rank() over (order by `discount` range between unbounded_preceding and current_row)
from `tpch`.`tiny`.`lineitem`
where `partkey` = 272
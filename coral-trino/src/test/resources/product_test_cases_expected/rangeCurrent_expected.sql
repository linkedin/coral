select `nationkey`, min(`nationkey`) over (partition by `regionkey` order by `comment` range between unbounded_preceding and current_row) as `min`
from `tpch`.`tiny`.`nation`
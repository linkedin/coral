select `nationkey`, min(`nationkey`) over (partition by `regionkey` order by `nationkey` rows between `preceding`(2) and `following`(1)) as `min`
from `tpch`.`tiny`.`nation`
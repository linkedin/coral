select count(distinct `n_regionkey`), count(distinct `n_name`), min(distinct `n_nationkey`)
from `nation`
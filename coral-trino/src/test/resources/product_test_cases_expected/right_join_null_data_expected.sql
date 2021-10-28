select `n_name`, `department`, `name`, `salary`
from `nation`
right join `workers` on `n_nationkey` = `department`
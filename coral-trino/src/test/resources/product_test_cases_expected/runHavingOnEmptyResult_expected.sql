select count(`n_regionkey`)
from `nation`
where 1 = 2
having sum(`n_regionkey`) is null
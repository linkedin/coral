select `n_regionkey`
from (select `n_regionkey`, count(*) as `cnt`
from `nation`
group by `n_regionkey`) as `t`
group by `n_regionkey`
having `n_regionkey` < 3 and count(`cnt`) > 0
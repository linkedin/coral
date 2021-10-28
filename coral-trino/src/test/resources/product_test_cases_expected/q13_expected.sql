select `c_count`, count(*) as `custdist`
from (select `c_custkey`, count(`o_orderkey`)
from `customer`
left join `orders` on `c_custkey` = `o_custkey` and not `o_comment` like '%special%requests%'
group by `c_custkey`) as `c_orders` (`c_custkey`, `c_count`)
group by `c_count`
order by `custdist` desc, `c_count` desc
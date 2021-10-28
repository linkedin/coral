select `p_brand`, `p_type`, `p_size`, count(distinct `ps_suppkey`) as `supplier_cnt`
from `part`
where `p_partkey` = `ps_partkey` and `p_brand` <> 'brand#45' and not `p_type` like 'medium polished%' and `p_size` in (49, 14, 23, 45, 19, 3, 36, 9) and `ps_suppkey` not in (select `s_suppkey`
from `supplier`
where `s_comment` like '%customer%complaints%')
group by `p_brand`, `p_type`, `p_size`
order by `supplier_cnt` desc, `p_brand`, `p_type`, `p_size`
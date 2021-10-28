select `p_brand`, `p_type`, `p_size`, count(distinct `ps_suppkey`) as `supplier_cnt`
from `part`
where `p_partkey` = `ps_partkey` and `p_brand` <> 'part brand' and not `p_type` like 'part type like%' and `p_size` in (3, 4, 5, 6, 7, 8, 9, 10) and `ps_suppkey` not in (select `s_suppkey`
from `supplier`
where `s_comment` like '%customer%complaints%')
group by `p_brand`, `p_type`, `p_size`
order by `supplier_cnt` desc, `p_brand`, `p_type`, `p_size`
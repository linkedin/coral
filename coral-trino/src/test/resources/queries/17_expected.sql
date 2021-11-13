select sum(`l_extendedprice`) / 7.0 as `avg_yearly`
from `part`
where `p_partkey` = `l_partkey` and `p_brand` = 'part brand' and `p_container` = 'part container' and `l_quantity` < (select 0.2 * avg(`l_quantity`)
from `lineitem`
where `l_partkey` = `p_partkey`)
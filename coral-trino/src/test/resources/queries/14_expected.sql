select 100.00 * sum(case when `p_type` like 'promo%' then `l_extendedprice` * (1 - `l_discount`) else 0 end) / sum(`l_extendedprice` * (1 - `l_discount`)) as `promo_revenue`
from `part`
where `l_partkey` = `p_partkey` and `l_shipdate` >= date '2013-03-05' and `l_shipdate` < date '2013-03-05' + interval '1' month
select `asceding`.`rnk`, `i1`.`i_product_name` as `best_performing`, `i2`.`i_product_name` as `worst_performing`
from (select *
from (select `item_sk`, rank() over (order by `rank_col`) as `rnk`
from (select `ss_item_sk` as `item_sk`, avg(`ss_net_profit`) as `rank_col`
from `store_sales` as `ss1`
where `ss_store_sk` = 4
group by `ss_item_sk`
having avg(`ss_net_profit`) > 0.9 * (select avg(`ss_net_profit`) as `rank_col`
from `store_sales`
where `ss_store_sk` = 4 and `ss_addr_sk` is null
group by `ss_store_sk`)) as `v1`) as `v11`
where `rnk` < 11) as `asceding`
where `asceding`.`rnk` = `descending`.`rnk` and `i1`.`i_item_sk` = `asceding`.`item_sk` and `i2`.`i_item_sk` = `descending`.`item_sk`
order by `asceding`.`rnk`
fetch next 100 rows only
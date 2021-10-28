select sum(`ss_net_profit`) as `total_sum`, `s_state`, `s_county`, grouping(`s_state`) + grouping(`s_county`) as `lochierarchy`, rank() over (partition by grouping(`s_state`) + grouping(`s_county`), case when grouping(`s_county`) = 0 then `s_state` else null end order by sum(`ss_net_profit`) desc) as `rank_within_parent`
from `store_sales`
where `d1`.`d_month_seq` between asymmetric 1200 and 1200 + 11 and `d1`.`d_date_sk` = `ss_sold_date_sk` and `s_store_sk` = `ss_store_sk` and `s_state` in (select `s_state`
from (select `s_state` as `s_state`, rank() over (partition by `s_state` order by sum(`ss_net_profit`) desc) as `ranking`
from `store_sales`
where `d_month_seq` between asymmetric 1200 and 1200 + 11 and `d_date_sk` = `ss_sold_date_sk` and `s_store_sk` = `ss_store_sk`
group by `s_state`) as `tmp1`
where `ranking` <= 5)
group by rollup(`s_state`, `s_county`)
order by `lochierarchy` desc, case when `lochierarchy` = 0 then `s_state` else null end, `rank_within_parent`
fetch next 100 rows only
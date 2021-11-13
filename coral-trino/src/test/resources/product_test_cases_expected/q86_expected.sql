select sum(`ws_net_paid`) as `total_sum`, `i_category`, `i_class`, grouping(`i_category`) + grouping(`i_class`) as `lochierarchy`, rank() over (partition by grouping(`i_category`) + grouping(`i_class`), case when grouping(`i_class`) = 0 then `i_category` else null end order by sum(`ws_net_paid`) desc) as `rank_within_parent`
from `web_sales`
where `d1`.`d_month_seq` between asymmetric 1200 and 1200 + 11 and `d1`.`d_date_sk` = `ws_sold_date_sk` and `i_item_sk` = `ws_item_sk`
group by rollup(`i_category`, `i_class`)
order by `lochierarchy` desc, case when `lochierarchy` = 0 then `i_category` else null end, `rank_within_parent`
fetch next 100 rows only
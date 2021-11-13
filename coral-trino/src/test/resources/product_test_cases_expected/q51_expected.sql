with `web_v1` as (select `ws_item_sk` as `item_sk`, `d_date`, sum(sum(`ws_sales_price`)) over (partition by `ws_item_sk` order by `d_date` rows between unbounded_preceding and current_row) as `cume_sales`
from `web_sales`
where `ws_sold_date_sk` = `d_date_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11 and `ws_item_sk` is not null
group by `ws_item_sk`, `d_date`), `store_v1` as (select `ss_item_sk` as `item_sk`, `d_date`, sum(sum(`ss_sales_price`)) over (partition by `ss_item_sk` order by `d_date` rows between unbounded_preceding and current_row) as `cume_sales`
from `store_sales`
where `ss_sold_date_sk` = `d_date_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11 and `ss_item_sk` is not null
group by `ss_item_sk`, `d_date`) (select *
from (select `item_sk`, `d_date`, `web_sales`, `store_sales`, max(`web_sales`) over (partition by `item_sk` order by `d_date` rows between unbounded_preceding and current_row) as `web_cumulative`, max(`store_sales`) over (partition by `item_sk` order by `d_date` rows between unbounded_preceding and current_row) as `store_cumulative`
from (select case when `web`.`item_sk` is not null then `web`.`item_sk` else `store`.`item_sk` end as `item_sk`, case when `web`.`d_date` is not null then `web`.`d_date` else `store`.`d_date` end as `d_date`, `web`.`cume_sales` as `web_sales`, `store`.`cume_sales` as `store_sales`
from `web_v1` as `web`
full join `store_v1` as `store` on `web`.`item_sk` = `store`.`item_sk` and `web`.`d_date` = `store`.`d_date`) as `x`) as `y`
where `web_cumulative` > `store_cumulative`
order by `item_sk`, `d_date`
fetch next 100 rows only)
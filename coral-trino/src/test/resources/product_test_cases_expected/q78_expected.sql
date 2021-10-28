with `ws` as (select `d_year` as `ws_sold_year`, `ws_item_sk`, `ws_bill_customer_sk` as `ws_customer_sk`, sum(`ws_quantity`) as `ws_qty`, sum(`ws_wholesale_cost`) as `ws_wc`, sum(`ws_sales_price`) as `ws_sp`
from `web_sales`
left join `web_returns` on `wr_order_number` = `ws_order_number` and `ws_item_sk` = `wr_item_sk`
inner join `date_dim` on `ws_sold_date_sk` = `d_date_sk`
where `wr_order_number` is null
group by `d_year`, `ws_item_sk`, `ws_bill_customer_sk`), `cs` as (select `d_year` as `cs_sold_year`, `cs_item_sk`, `cs_bill_customer_sk` as `cs_customer_sk`, sum(`cs_quantity`) as `cs_qty`, sum(`cs_wholesale_cost`) as `cs_wc`, sum(`cs_sales_price`) as `cs_sp`
from `catalog_sales`
left join `catalog_returns` on `cr_order_number` = `cs_order_number` and `cs_item_sk` = `cr_item_sk`
inner join `date_dim` on `cs_sold_date_sk` = `d_date_sk`
where `cr_order_number` is null
group by `d_year`, `cs_item_sk`, `cs_bill_customer_sk`), `ss` as (select `d_year` as `ss_sold_year`, `ss_item_sk`, `ss_customer_sk`, sum(`ss_quantity`) as `ss_qty`, sum(`ss_wholesale_cost`) as `ss_wc`, sum(`ss_sales_price`) as `ss_sp`
from `store_sales`
left join `store_returns` on `sr_ticket_number` = `ss_ticket_number` and `ss_item_sk` = `sr_item_sk`
inner join `date_dim` on `ss_sold_date_sk` = `d_date_sk`
where `sr_ticket_number` is null
group by `d_year`, `ss_item_sk`, `ss_customer_sk`) (select `ss_sold_year`, `ss_item_sk`, `ss_customer_sk`, round(cast(`ss_qty` as decimal(10, 10)) / coalesce(`ws_qty` + `cs_qty`, 1), 2) as `ratio`, `ss_qty` as `store_qty`, `ss_wc` as `store_wholesale_cost`, `ss_sp` as `store_sales_price`, coalesce(`ws_qty`, 0) + coalesce(`cs_qty`, 0) as `other_chan_qty`, coalesce(`ws_wc`, 0) + coalesce(`cs_wc`, 0) as `other_chan_wholesale_cost`, coalesce(`ws_sp`, 0) + coalesce(`cs_sp`, 0) as `other_chan_sales_price`
from `ss`
left join `ws` on `ws_sold_year` = `ss_sold_year` and `ws_item_sk` = `ss_item_sk` and `ws_customer_sk` = `ss_customer_sk`
left join `cs` on `cs_sold_year` = `ss_sold_year` and `cs_item_sk` = `cs_item_sk` and `cs_customer_sk` = `ss_customer_sk`
where coalesce(`ws_qty`, 0) > 0 and coalesce(`cs_qty`, 0) > 0 and `ss_sold_year` = 2000
order by `ss_sold_year`, `ss_item_sk`, `ss_customer_sk`, `ss_qty` desc, `ss_wc` desc, `ss_sp` desc, `other_chan_qty`, `other_chan_wholesale_cost`, `other_chan_sales_price`, round(cast(`ss_qty` as decimal(10, 10)) / coalesce(`ws_qty` + `cs_qty`, 1), 2)
fetch next 100 rows only)
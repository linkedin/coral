with `ss` as (select `i_item_id`, sum(`ss_ext_sales_price`) as `total_sales`
from `store_sales`
where `i_item_id` in (select `i_item_id`
from `item`
where `i_category` in ('music')) and `ss_item_sk` = `i_item_sk` and `ss_sold_date_sk` = `d_date_sk` and `d_year` = 1998 and `d_moy` = 9 and `ss_addr_sk` = `ca_address_sk` and `ca_gmt_offset` = -5
group by `i_item_id`), `cs` as (select `i_item_id`, sum(`cs_ext_sales_price`) as `total_sales`
from `catalog_sales`
where `i_item_id` in (select `i_item_id`
from `item`
where `i_category` in ('music')) and `cs_item_sk` = `i_item_sk` and `cs_sold_date_sk` = `d_date_sk` and `d_year` = 1998 and `d_moy` = 9 and `cs_bill_addr_sk` = `ca_address_sk` and `ca_gmt_offset` = -5
group by `i_item_id`), `ws` as (select `i_item_id`, sum(`ws_ext_sales_price`) as `total_sales`
from `web_sales`
where `i_item_id` in (select `i_item_id`
from `item`
where `i_category` in ('music')) and `ws_item_sk` = `i_item_sk` and `ws_sold_date_sk` = `d_date_sk` and `d_year` = 1998 and `d_moy` = 9 and `ws_bill_addr_sk` = `ca_address_sk` and `ca_gmt_offset` = -5
group by `i_item_id`) (select `i_item_id`, sum(`total_sales`) as `total_sales`
from (select *
from `ss`
union all
select *
from `cs`
union all
select *
from `ws`) as `tmp1`
group by `i_item_id`
order by `i_item_id`, `total_sales`
fetch next 100 rows only)
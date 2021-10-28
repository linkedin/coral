select `channel`, `col_name`, `d_year`, `d_qoy`, `i_category`, count(*) as `sales_cnt`, sum(`ext_sales_price`) as `sales_amt`
from (select 'store' as `channel`, 'ss_store_sk' as `col_name`, `d_year`, `d_qoy`, `i_category`, `ss_ext_sales_price` as `ext_sales_price`
from `store_sales`
where `ss_store_sk` is null and `ss_sold_date_sk` = `d_date_sk` and `ss_item_sk` = `i_item_sk`
union all
select 'web' as `channel`, 'ws_ship_customer_sk' as `col_name`, `d_year`, `d_qoy`, `i_category`, `ws_ext_sales_price` as `ext_sales_price`
from `web_sales`
where `ws_ship_customer_sk` is null and `ws_sold_date_sk` = `d_date_sk` and `ws_item_sk` = `i_item_sk`
union all
select 'catalog' as `channel`, 'cs_ship_addr_sk' as `col_name`, `d_year`, `d_qoy`, `i_category`, `cs_ext_sales_price` as `ext_sales_price`
from `catalog_sales`
where `cs_ship_addr_sk` is null and `cs_sold_date_sk` = `d_date_sk` and `cs_item_sk` = `i_item_sk`) as `foo`
group by `channel`, `col_name`, `d_year`, `d_qoy`, `i_category`
order by `channel`, `col_name`, `d_year`, `d_qoy`, `i_category`
fetch next 100 rows only
select sum(`ws_ext_discount_amt`) as `excess discount amount`
from `web_sales`
where `i_manufact_id` = 350 and `i_item_sk` = `ws_item_sk` and `d_date` between asymmetric cast('2000-01-27' as date) and cast('2000-01-27' as date) + interval '90' day and `d_date_sk` = `ws_sold_date_sk` and `ws_ext_discount_amt` > (select 1.3 * avg(`ws_ext_discount_amt`)
from `web_sales`
where `ws_item_sk` = `i_item_sk` and `d_date` between asymmetric cast('2000-01-27' as date) and cast('2000-01-27' as date) + interval '90' day and `d_date_sk` = `ws_sold_date_sk`)
order by sum(`ws_ext_discount_amt`)
fetch next 100 rows only
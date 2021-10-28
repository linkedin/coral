with `cross_items` as (select `i_item_sk` as `ss_item_sk`
from (select `iss`.`i_brand_id` as `brand_id`, `iss`.`i_class_id` as `class_id`, `iss`.`i_category_id` as `category_id`
from `store_sales`
where `ss_item_sk` = `iss`.`i_item_sk` and `ss_sold_date_sk` = `d1`.`d_date_sk` and `d1`.`d_year` between asymmetric 1999 and 1999 + 2
intersect
select `ics`.`i_brand_id`, `ics`.`i_class_id`, `ics`.`i_category_id`
from `catalog_sales`
where `cs_item_sk` = `ics`.`i_item_sk` and `cs_sold_date_sk` = `d2`.`d_date_sk` and `d2`.`d_year` between asymmetric 1999 and 1999 + 2
intersect
select `iws`.`i_brand_id`, `iws`.`i_class_id`, `iws`.`i_category_id`
from `web_sales`
where `ws_item_sk` = `iws`.`i_item_sk` and `ws_sold_date_sk` = `d3`.`d_date_sk` and `d3`.`d_year` between asymmetric 1999 and 1999 + 2) as `x`
where `i_brand_id` = `brand_id` and `i_class_id` = `class_id` and `i_category_id` = `category_id`), `avg_sales` as (select avg(`quantity` * `list_price`) as `average_sales`
from (select `ss_quantity` as `quantity`, `ss_list_price` as `list_price`
from `store_sales`
where `ss_sold_date_sk` = `d_date_sk` and `d_year` between asymmetric 1999 and 1999 + 2
union all
select `cs_quantity` as `quantity`, `cs_list_price` as `list_price`
from `catalog_sales`
where `cs_sold_date_sk` = `d_date_sk` and `d_year` between asymmetric 1999 and 1999 + 2
union all
select `ws_quantity` as `quantity`, `ws_list_price` as `list_price`
from `web_sales`
where `ws_sold_date_sk` = `d_date_sk` and `d_year` between asymmetric 1999 and 1999 + 2)) (select *
from (select 'store' as `channel`, `i_brand_id`, `i_class_id`, `i_category_id`, sum(`ss_quantity` * `ss_list_price`) as `sales`, count(*) as `number_sales`
from `store_sales`
where `ss_item_sk` in (select `ss_item_sk`
from `cross_items`) and `ss_item_sk` = `i_item_sk` and `ss_sold_date_sk` = `d_date_sk` and `d_week_seq` = (select `d_week_seq`
from `date_dim`
where `d_year` = 1999 + 1 and `d_moy` = 12 and `d_dom` = 11)
group by `i_brand_id`, `i_class_id`, `i_category_id`
having sum(`ss_quantity` * `ss_list_price`) > (select `average_sales`
from `avg_sales`))
where `this_year`.`i_brand_id` = `last_year`.`i_brand_id` and `this_year`.`i_class_id` = `last_year`.`i_class_id` and `this_year`.`i_category_id` = `last_year`.`i_category_id`
order by `this_year`.`channel`, `this_year`.`i_brand_id`, `this_year`.`i_class_id`, `this_year`.`i_category_id`
fetch next 100 rows only)
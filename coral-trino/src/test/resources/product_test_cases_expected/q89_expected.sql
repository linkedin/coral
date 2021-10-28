select *
from (select `i_category`, `i_class`, `i_brand`, `s_store_name`, `s_company_name`, `d_moy`, sum(`ss_sales_price`) as `sum_sales`, avg(sum(`ss_sales_price`)) over (partition by `i_category`, `i_brand`, `s_store_name`, `s_company_name`) as `avg_monthly_sales`
from `item`
where `ss_item_sk` = `i_item_sk` and `ss_sold_date_sk` = `d_date_sk` and `ss_store_sk` = `s_store_sk` and `d_year` in (1999) and (`i_category` in ('books', 'electronics', 'sports') and `i_class` in ('computers', 'stereo', 'football') or `i_category` in ('men', 'jewelry', 'women') and `i_class` in ('shirts', 'birdal', 'dresses'))
group by `i_category`, `i_class`, `i_brand`, `s_store_name`, `s_company_name`, `d_moy`) as `tmp1`
where case when `avg_monthly_sales` <> 0 then abs(`sum_sales` - `avg_monthly_sales`) / `avg_monthly_sales` else null end > 0.1
order by `sum_sales` - `avg_monthly_sales`, `s_store_name`
fetch next 100 rows only
select *
from (select `i_category`, `i_class`, `i_brand`, `i_product_name`, `d_year`, `d_qoy`, `d_moy`, `s_store_id`, `sumsales`, rank() over (partition by `i_category` order by `sumsales` desc) as `rk`
from (select `i_category`, `i_class`, `i_brand`, `i_product_name`, `d_year`, `d_qoy`, `d_moy`, `s_store_id`, sum(coalesce(`ss_sales_price` * `ss_quantity`, 0)) as `sumsales`
from `store_sales`
where `ss_sold_date_sk` = `d_date_sk` and `ss_item_sk` = `i_item_sk` and `ss_store_sk` = `s_store_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11
group by rollup(`i_category`, `i_class`, `i_brand`, `i_product_name`, `d_year`, `d_qoy`, `d_moy`, `s_store_id`)) as `dw1`) as `dw2`
where `rk` <= 100
order by `i_category`, `i_class`, `i_brand`, `i_product_name`, `d_year`, `d_qoy`, `d_moy`, `s_store_id`, `sumsales`, `rk`
fetch next 100 rows only
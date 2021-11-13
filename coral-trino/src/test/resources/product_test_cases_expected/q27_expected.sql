select `i_item_id`, `s_state`, grouping(`s_state`) as `g_state`, avg(`ss_quantity`) as `agg1`, avg(`ss_list_price`) as `agg2`, avg(`ss_coupon_amt`) as `agg3`, avg(`ss_sales_price`) as `agg4`
from `store_sales`
where `ss_sold_date_sk` = `d_date_sk` and `ss_item_sk` = `i_item_sk` and `ss_store_sk` = `s_store_sk` and `ss_cdemo_sk` = `cd_demo_sk` and `cd_gender` = 'm' and `cd_marital_status` = 's' and `cd_education_status` = 'college' and `d_year` = 2002 and `s_state` in ('tn', 'tn', 'tn', 'tn', 'tn', 'tn')
group by rollup(`i_item_id`, `s_state`)
order by `i_item_id`, `s_state`
fetch next 100 rows only
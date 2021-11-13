select `i_item_id`, avg(`cs_quantity`) as `agg1`, avg(`cs_list_price`) as `agg2`, avg(`cs_coupon_amt`) as `agg3`, avg(`cs_sales_price`) as `agg4`
from `catalog_sales`
where `cs_sold_date_sk` = `d_date_sk` and `cs_item_sk` = `i_item_sk` and `cs_bill_cdemo_sk` = `cd_demo_sk` and `cs_promo_sk` = `p_promo_sk` and `cd_gender` = 'm' and `cd_marital_status` = 's' and `cd_education_status` = 'college' and (`p_channel_email` = 'n' or `p_channel_event` = 'n') and `d_year` = 2000
group by `i_item_id`
order by `i_item_id`
fetch next 100 rows only
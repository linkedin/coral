select `promotions`, `total`, cast(`promotions` as decimal(15, 15)) / cast(`total` as decimal(15, 15)) * 100
from (select sum(`ss_ext_sales_price`) as `promotions`
from `store_sales`
where `ss_sold_date_sk` = `d_date_sk` and `ss_store_sk` = `s_store_sk` and `ss_promo_sk` = `p_promo_sk` and `ss_customer_sk` = `c_customer_sk` and `ca_address_sk` = `c_current_addr_sk` and `ss_item_sk` = `i_item_sk` and `ca_gmt_offset` = -5 and `i_category` = 'jewelry' and (`p_channel_dmail` = 'y' or `p_channel_email` = 'y' or `p_channel_tv` = 'y') and `s_gmt_offset` = -5 and `d_year` = 1998 and `d_moy` = 11) as `promotional_sales`
order by `promotions`, `total`
fetch next 100 rows only
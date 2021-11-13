select `cc_call_center_id` as `call_center`, `cc_name` as `call_center_name`, `cc_manager` as `manager`, sum(`cr_net_loss`) as `returns_loss`
from `call_center`
where `cr_call_center_sk` = `cc_call_center_sk` and `cr_returned_date_sk` = `d_date_sk` and `cr_returning_customer_sk` = `c_customer_sk` and `cd_demo_sk` = `c_current_cdemo_sk` and `hd_demo_sk` = `c_current_hdemo_sk` and `ca_address_sk` = `c_current_addr_sk` and `d_year` = 1998 and `d_moy` = 11 and (`cd_marital_status` = 'm' and `cd_education_status` = 'unknown' or `cd_marital_status` = 'w' and `cd_education_status` = 'advanced degree') and `hd_buy_potential` like 'unknown' and `ca_gmt_offset` = -7
group by `cc_call_center_id`, `cc_name`, `cc_manager`, `cd_marital_status`, `cd_education_status`
order by sum(`cr_net_loss`) desc
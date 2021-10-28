select count(*)
from (select distinct `c_last_name`, `c_first_name`, `d_date`
from `store_sales`
where `store_sales`.`ss_sold_date_sk` = `date_dim`.`d_date_sk` and `store_sales`.`ss_customer_sk` = `customer`.`c_customer_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11
intersect
select distinct `c_last_name`, `c_first_name`, `d_date`
from `catalog_sales`
where `catalog_sales`.`cs_sold_date_sk` = `date_dim`.`d_date_sk` and `catalog_sales`.`cs_bill_customer_sk` = `customer`.`c_customer_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11
intersect
select distinct `c_last_name`, `c_first_name`, `d_date`
from `web_sales`
where `web_sales`.`ws_sold_date_sk` = `date_dim`.`d_date_sk` and `web_sales`.`ws_bill_customer_sk` = `customer`.`c_customer_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11) as `hot_cust`
order by
fetch next 100 rows only
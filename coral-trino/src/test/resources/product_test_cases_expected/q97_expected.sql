with `ssci` as (select `ss_customer_sk` as `customer_sk`, `ss_item_sk` as `item_sk`
from `store_sales`
where `ss_sold_date_sk` = `d_date_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11
group by `ss_customer_sk`, `ss_item_sk`), `csci` as (select `cs_bill_customer_sk` as `customer_sk`, `cs_item_sk` as `item_sk`
from `catalog_sales`
where `cs_sold_date_sk` = `d_date_sk` and `d_month_seq` between asymmetric 1200 and 1200 + 11
group by `cs_bill_customer_sk`, `cs_item_sk`) (select sum(case when `ssci`.`customer_sk` is not null and `csci`.`customer_sk` is null then 1 else 0 end) as `store_only`, sum(case when `ssci`.`customer_sk` is null and `csci`.`customer_sk` is not null then 1 else 0 end) as `catalog_only`, sum(case when `ssci`.`customer_sk` is not null and `csci`.`customer_sk` is not null then 1 else 0 end) as `store_and_catalog`
from `ssci`
full join `csci` on `ssci`.`customer_sk` = `csci`.`customer_sk` and `ssci`.`item_sk` = `csci`.`item_sk`
order by
fetch next 100 rows only)
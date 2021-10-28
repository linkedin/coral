select `ss_customer_sk`, sum(`act_sales`) as `sumsales`
from (select `ss_item_sk`, `ss_ticket_number`, `ss_customer_sk`, case when `sr_return_quantity` is not null then (`ss_quantity` - `sr_return_quantity`) * `ss_sales_price` else `ss_quantity` * `ss_sales_price` end as `act_sales`
from `store_sales`
left join `store_returns` on `sr_item_sk` = `ss_item_sk` and `sr_ticket_number` = `ss_ticket_number`
where `sr_reason_sk` = `r_reason_sk` and `r_reason_desc` = 'reason 28') as `t`
group by `ss_customer_sk`
order by `sumsales`, `ss_customer_sk`
fetch next 100 rows only
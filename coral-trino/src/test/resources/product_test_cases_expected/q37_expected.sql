select `i_item_id`, `i_item_desc`, `i_current_price`
from `item`
where `i_current_price` between asymmetric 68 and 68 + 30 and `inv_item_sk` = `i_item_sk` and `d_date_sk` = `inv_date_sk` and cast(`d_date` as date) between asymmetric cast('2000-02-01' as date) and cast('2000-02-01' as date) + interval '60' day and `i_manufact_id` in (677, 940, 694, 808) and `inv_quantity_on_hand` between asymmetric 100 and 500 and `cs_item_sk` = `i_item_sk`
group by `i_item_id`, `i_item_desc`, `i_current_price`
order by `i_item_id`
fetch next 100 rows only
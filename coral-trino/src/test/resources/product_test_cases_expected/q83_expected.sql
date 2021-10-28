with `sr_items` as (select `i_item_id` as `item_id`, sum(`sr_return_quantity`) as `sr_item_qty`
from `store_returns`
where `sr_item_sk` = `i_item_sk` and `d_date` in (select `d_date`
from `date_dim`
where `d_week_seq` in (select `d_week_seq`
from `date_dim`
where `d_date` in (cast('2000-06-30' as date), cast('2000-09-27' as date), cast('2000-11-17' as date)))) and `sr_returned_date_sk` = `d_date_sk`
group by `i_item_id`), `cr_items` as (select `i_item_id` as `item_id`, sum(`cr_return_quantity`) as `cr_item_qty`
from `catalog_returns`
where `cr_item_sk` = `i_item_sk` and `d_date` in (select `d_date`
from `date_dim`
where `d_week_seq` in (select `d_week_seq`
from `date_dim`
where `d_date` in (cast('2000-06-30' as date), cast('2000-09-27' as date), cast('2000-11-17' as date)))) and `cr_returned_date_sk` = `d_date_sk`
group by `i_item_id`), `wr_items` as (select `i_item_id` as `item_id`, sum(`wr_return_quantity`) as `wr_item_qty`
from `web_returns`
where `wr_item_sk` = `i_item_sk` and `d_date` in (select `d_date`
from `date_dim`
where `d_week_seq` in (select `d_week_seq`
from `date_dim`
where `d_date` in (cast('2000-06-30' as date), cast('2000-09-27' as date), cast('2000-11-17' as date)))) and `wr_returned_date_sk` = `d_date_sk`
group by `i_item_id`) (select `sr_items`.`item_id`, `sr_item_qty`, cast(`sr_item_qty` / (cast(`sr_item_qty` as decimal(9, 9)) + `cr_item_qty` + `wr_item_qty`) / 3.0 * 100 as decimal(7, 7)) as `sr_dev`, `cr_item_qty`, cast(`cr_item_qty` / (cast(`sr_item_qty` as decimal(9, 9)) + `cr_item_qty` + `wr_item_qty`) / 3.0 * 100 as decimal(7, 7)) as `cr_dev`, `wr_item_qty`, cast(`wr_item_qty` / (cast(`sr_item_qty` as decimal(9, 9)) + `cr_item_qty` + `wr_item_qty`) / 3.0 * 100 as decimal(7, 7)) as `wr_dev`, (`sr_item_qty` + `cr_item_qty` + `wr_item_qty`) / 3.00 as `average`
from `sr_items`
where `sr_items`.`item_id` = `cr_items`.`item_id` and `sr_items`.`item_id` = `wr_items`.`item_id`
order by `sr_items`.`item_id`, `sr_item_qty`
fetch next 100 rows only)
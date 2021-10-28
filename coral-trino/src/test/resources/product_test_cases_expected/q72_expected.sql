select `i_item_desc`, `w_warehouse_name`, `d1`.`d_week_seq`, sum(case when `p_promo_sk` is null then 1 else 0 end) as `no_promo`, sum(case when `p_promo_sk` is not null then 1 else 0 end) as `promo`, count(*) as `total_cnt`
from `catalog_sales`
inner join `inventory` on `cs_item_sk` = `inv_item_sk`
inner join `warehouse` on `w_warehouse_sk` = `inv_warehouse_sk`
inner join `item` on `i_item_sk` = `cs_item_sk`
inner join `customer_demographics` on `cs_bill_cdemo_sk` = `cd_demo_sk`
inner join `household_demographics` on `cs_bill_hdemo_sk` = `hd_demo_sk`
inner join `date_dim` as `d1` on `cs_sold_date_sk` = `d1`.`d_date_sk`
inner join `date_dim` as `d2` on `inv_date_sk` = `d2`.`d_date_sk`
inner join `date_dim` as `d3` on `cs_ship_date_sk` = `d3`.`d_date_sk`
left join `promotion` on `cs_promo_sk` = `p_promo_sk`
left join `catalog_returns` on `cr_item_sk` = `cs_item_sk` and `cr_order_number` = `cs_order_number`
where `d1`.`d_week_seq` = `d2`.`d_week_seq` and `inv_quantity_on_hand` < `cs_quantity` and `d3`.`d_date` > `d1`.`d_date` + interval '5' day and `hd_buy_potential` = '>10000' and `d1`.`d_year` = 1999 and `cd_marital_status` = 'd'
group by `i_item_desc`, `w_warehouse_name`, `d1`.`d_week_seq`
order by `total_cnt` desc, `i_item_desc`, `w_warehouse_name`, `d1`.`d_week_seq`
fetch next 100 rows only
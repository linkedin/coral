with `b0` as (select avg(`ss_list_price`) as `b1_lp`, count(`ss_list_price`) as `b1_cnt`, count(distinct `ss_list_price`) as `b1_cntd`
from `store_sales`
where `ss_quantity` between asymmetric 0 and 5 and (`ss_list_price` between asymmetric 8 and 8 + 10 or `ss_coupon_amt` between asymmetric 459 and 459 + 1000 or `ss_wholesale_cost` between asymmetric 57 and 57 + 20)), `b1` as (select avg(`ss_list_price`) as `b2_lp`, count(`ss_list_price`) as `b2_cnt`, count(distinct `ss_list_price`) as `b2_cntd`
from `store_sales`
where `ss_quantity` between asymmetric 6 and 10 and (`ss_list_price` between asymmetric 90 and 90 + 10 or `ss_coupon_amt` between asymmetric 2323 and 2323 + 1000 or `ss_wholesale_cost` between asymmetric 31 and 31 + 20)), `b2` as (select avg(`ss_list_price`) as `b3_lp`, count(`ss_list_price`) as `b3_cnt`, count(distinct `ss_list_price`) as `b3_cntd`
from `store_sales`
where `ss_quantity` between asymmetric 11 and 15 and (`ss_list_price` between asymmetric 142 and 142 + 10 or `ss_coupon_amt` between asymmetric 12214 and 12214 + 1000 or `ss_wholesale_cost` between asymmetric 79 and 79 + 20)), `b3` as (select avg(`ss_list_price`) as `b4_lp`, count(`ss_list_price`) as `b4_cnt`, count(distinct `ss_list_price`) as `b4_cntd`
from `store_sales`
where `ss_quantity` between asymmetric 16 and 20 and (`ss_list_price` between asymmetric 135 and 135 + 10 or `ss_coupon_amt` between asymmetric 6071 and 6071 + 1000 or `ss_wholesale_cost` between asymmetric 38 and 38 + 20)), `b4` as (select avg(`ss_list_price`) as `b5_lp`, count(`ss_list_price`) as `b5_cnt`, count(distinct `ss_list_price`) as `b5_cntd`
from `store_sales`
where `ss_quantity` between asymmetric 21 and 25 and (`ss_list_price` between asymmetric 122 and 122 + 10 or `ss_coupon_amt` between asymmetric 836 and 836 + 1000 or `ss_wholesale_cost` between asymmetric 17 and 17 + 20)), `b5` as (select avg(`ss_list_price`) as `b6_lp`, count(`ss_list_price`) as `b6_cnt`, count(distinct `ss_list_price`) as `b6_cntd`
from `store_sales`
where `ss_quantity` between asymmetric 26 and 30 and (`ss_list_price` between asymmetric 154 and 154 + 10 or `ss_coupon_amt` between asymmetric 7326 and 7326 + 1000 or `ss_wholesale_cost` between asymmetric 7 and 7 + 20)) (select *
from `b5`
order by
fetch next 100 rows only)
with `wss` as (select `d_week_seq`, `ss_store_sk`, sum(case when `d_day_name` = 'sunday' then `ss_sales_price` else null end) as `sun_sales`, sum(case when `d_day_name` = 'monday' then `ss_sales_price` else null end) as `mon_sales`, sum(case when `d_day_name` = 'tuesday' then `ss_sales_price` else null end) as `tue_sales`, sum(case when `d_day_name` = 'wednesday' then `ss_sales_price` else null end) as `wed_sales`, sum(case when `d_day_name` = 'thursday ' then `ss_sales_price` else null end) as `thu_sales`, sum(case when `d_day_name` = 'friday' then `ss_sales_price` else null end) as `fri_sales`, sum(case when `d_day_name` = 'saturday' then `ss_sales_price` else null end) as `sat_sales`
from `store_sales`
where `d_date_sk` = `ss_sold_date_sk`
group by `d_week_seq`, `ss_store_sk`) (select `s_store_name1`, `s_store_id1`, `d_week_seq1`, `sun_sales1` / `sun_sales2`, `mon_sales1` / `mon_sales2`, `tue_sales1` / `tue_sales2`, `wed_sales1` / `wed_sales2`, `thu_sales1` / `thu_sales2`, `fri_sales1` / `fri_sales2`, `sat_sales1` / `sat_sales2`
from (select `s_store_name` as `s_store_name1`, `wss`.`d_week_seq` as `d_week_seq1`, `s_store_id` as `s_store_id1`, `sun_sales` as `sun_sales1`, `mon_sales` as `mon_sales1`, `tue_sales` as `tue_sales1`, `wed_sales` as `wed_sales1`, `thu_sales` as `thu_sales1`, `fri_sales` as `fri_sales1`, `sat_sales` as `sat_sales1`
from `wss`
where `d`.`d_week_seq` = `wss`.`d_week_seq` and `ss_store_sk` = `s_store_sk` and `d_month_seq` between asymmetric 1212 and 1212 + 11) as `y`
where `s_store_id1` = `s_store_id2` and `d_week_seq1` = `d_week_seq2` - 52
order by `s_store_name1`, `s_store_id1`, `d_week_seq1`
fetch next 100 rows only)
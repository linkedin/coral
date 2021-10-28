select *
from (select `i_manufact_id`, sum(`ss_sales_price`) as `sum_sales`, avg(sum(`ss_sales_price`)) over (partition by `i_manufact_id`) as `avg_quarterly_sales`
from `item`
where `ss_item_sk` = `i_item_sk` and `ss_sold_date_sk` = `d_date_sk` and `ss_store_sk` = `s_store_sk` and `d_month_seq` in (1200, 1200 + 1, 1200 + 2, 1200 + 3, 1200 + 4, 1200 + 5, 1200 + 6, 1200 + 7, 1200 + 8, 1200 + 9, 1200 + 10, 1200 + 11) and (`i_category` in ('books', 'children', 'electronics') and `i_class` in ('personal', 'portable', 'reference', 'self-help') and `i_brand` in ('scholaramalgamalg #14', 'scholaramalgamalg #7', 'exportiunivamalg #9', 'scholaramalgamalg #9') or `i_category` in ('women', 'music', 'men') and `i_class` in ('accessories', 'classical', 'fragrances', 'pants') and `i_brand` in ('amalgimporto #1', 'edu packscholar #1', 'exportiimporto #1', 'importoamalg #1'))
group by `i_manufact_id`, `d_qoy`) as `tmp1`
where case when `avg_quarterly_sales` > 0 then abs(cast(`sum_sales` as decimal(38, 38)) - `avg_quarterly_sales`) / `avg_quarterly_sales` else null end > 0.1
order by `avg_quarterly_sales`, `sum_sales`, `i_manufact_id`
fetch next 100 rows only
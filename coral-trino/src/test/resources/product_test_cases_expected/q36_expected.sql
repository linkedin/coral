select sum(`ss_net_profit`) / sum(`ss_ext_sales_price`) as `gross_margin`, `i_category`, `i_class`, grouping(`i_category`) + grouping(`i_class`) as `lochierarchy`, rank() over (partition by grouping(`i_category`) + grouping(`i_class`), case when grouping(`i_class`) = 0 then `i_category` else null end order by sum(`ss_net_profit`) / sum(`ss_ext_sales_price`)) as `rank_within_parent`
from `store_sales`
where `d1`.`d_year` = 2001 and `d1`.`d_date_sk` = `ss_sold_date_sk` and `i_item_sk` = `ss_item_sk` and `s_store_sk` = `ss_store_sk` and `s_state` in ('tn', 'tn', 'tn', 'tn', 'tn', 'tn', 'tn', 'tn')
group by rollup(`i_category`, `i_class`)
order by `lochierarchy` desc, case when `lochierarchy` = 0 then `i_category` else null end, `rank_within_parent`, `i_category`, `i_class`
fetch next 100 rows only
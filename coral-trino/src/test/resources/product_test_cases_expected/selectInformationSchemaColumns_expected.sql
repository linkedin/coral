select `table_catalog`, `table_schema`, `table_name`, `column_name`, `data_type`, `is_nullable`, `column_default`, `comment`
from `system`.`information_schema`.`columns`
where `table_schema` <> 'jdbc'
order by `table_catalog`, `table_schema`, `table_name`, `ordinal_position`
with `nested` as (select *
from `nation`) (select count(*)
from (select *
from `nested`) as `a`)
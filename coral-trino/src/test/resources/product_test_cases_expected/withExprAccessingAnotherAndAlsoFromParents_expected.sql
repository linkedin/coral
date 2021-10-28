with `w1` as (select *
from `nation`), `w2` as (select *
from `w1`) (select count(*)
from `w1`)
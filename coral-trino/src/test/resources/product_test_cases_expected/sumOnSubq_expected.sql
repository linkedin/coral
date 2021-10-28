select sum(`cnt`)
from (select count(*) as `cnt`
from `empty`) as `foo`
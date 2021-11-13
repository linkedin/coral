select count(*)
from `workers`
having sum(`salary` * 2) / count(*) > 0
select count(*)
from `workers`
group by `id_department` * 2
having sum(log10(`salary` + 1)) > 0
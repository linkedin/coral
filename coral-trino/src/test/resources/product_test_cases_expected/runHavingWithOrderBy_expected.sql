select `id_department`, count(*)
from `workers`
group by `id_department`
having count(*) > 1
order by `id_department` desc
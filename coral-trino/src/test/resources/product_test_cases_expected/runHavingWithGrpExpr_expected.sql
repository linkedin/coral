select count(*)
from `workers`
group by `salary` * `id_department`
having `salary` * `id_department` is not null
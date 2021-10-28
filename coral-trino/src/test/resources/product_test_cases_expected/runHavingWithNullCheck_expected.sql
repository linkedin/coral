select `first_name`, count(*)
from `workers`
group by `first_name`
having `first_name` is null
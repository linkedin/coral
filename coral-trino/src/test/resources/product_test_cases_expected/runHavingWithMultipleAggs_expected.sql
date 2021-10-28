select `p_type`, count(*)
from `part`
group by `p_type`
having count(*) > 20 and avg(`p_retailprice`) > 1000
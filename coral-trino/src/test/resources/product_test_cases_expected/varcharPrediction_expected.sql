select `classify`(`features`(1, 2 + `random`(1)), `model`)
from (select `learn_classifier`(`labels`, `features`) as `model`
from (values row('cat', `features`(1, 2))) as `t` (`labels`, `features`)) as `t2`
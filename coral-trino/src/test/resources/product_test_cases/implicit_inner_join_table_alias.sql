-- database: presto; groups: join; tables: nation, region
select n.n_name, r.r_name from nation n where n.n_regionkey = r.r_regionkey


CREATE DATABASE IF NOT EXISTS my_database ON CLUSTER '{cluster}';

CREATE TABLE IF NOT EXISTS my_database.ontime ON CLUSTER '{cluster}'
( 
	id String,
	name String not null,
	age Int32 not null 
)
ENGINE = ReplicatedMergeTree(
    '/clickhouse/{cluster}/ontime/{shard}',
    '{replica}'
)
PRIMARY KEY (id);

CREATE TABLE my_database.ontime_distributed ON CLUSTER '{cluster}' AS my_database.ontime
ENGINE = Distributed('{cluster}', my_database, ontime, rand());

insert into my_database.ontime_distributed(id, name, age) values ('1', 'Timofey', 22);
insert into my_database.ontime_distributed(id, name, age) values ('2', 'Nikita', 22);
insert into my_database.ontime_distributed(id, name, age) values ('3', 'Timofey', 22);
insert into my_database.ontime_distributed(id, name, age) values ('4', 'Nikita', 22);

select * from my_database.ontime;
select * from my_database.ontime_distributed;
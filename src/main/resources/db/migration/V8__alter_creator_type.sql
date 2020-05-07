alter table question modify column creator bigint;
alter table comment modify column commentator bigint not null;
create table notification(
    id bigint auto_increment primary key not null,
    parent_id bigint not null,
    comment_id bigint not null,
    status int default 0,
    notifier bigint not null,
    gmt_create bigint
);

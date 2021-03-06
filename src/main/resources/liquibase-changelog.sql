--liquibase formatted sql

--changeset mwierzchowski:create-timer-tables
create sequence timer_id_seq increment by 10;
create table timer (
    id int primary key,
    description varchar(30) unique not null,
    created timestamp not null,
    updated timestamp not null,
    version int not null
);

create sequence timer_schedule_id_seq increment by 10;
create table timer_schedule (
    id int primary key,
    timer_id int not null,
    time time not null,
    days varchar(13) not null,
    created timestamp not null,
    updated timestamp not null,
    version int not null
);

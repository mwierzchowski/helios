--liquibase formatted sql

--changeset mwierzchowski:create-timer-tables
create sequence timer_id_sequence;
create table timer (
    id int primary key,
    description varchar(255) unique not null,
    created timestamp not null,
    updated timestamp not null,
    version int not null
);

create sequence timerschedule_id_sequence;
create table timer_schedule (
    id int primary key,
    timer_id int not null,
    time time not null,
    days varchar(13) not null,
    created timestamp not null,
    updated timestamp not null,
    version int not null
);

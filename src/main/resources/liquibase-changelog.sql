--liquibase formatted sql

--changeset mwierzchowski:create-timer-tables
create sequence timer_id_sequence increment by 50;

create table timer (
   id int primary key,
   description varchar(255) unique not null,
   created timestamp not null,
   updated timestamp not null,
   version int not null
);
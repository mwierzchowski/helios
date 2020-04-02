--liquibase formatted sql

--changeset mwierzchowski:test-data
create table timer (
id int primary key,
name varchar(255)
);
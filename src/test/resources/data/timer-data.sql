select nextval ('timer_id_sequence');
insert into timer (id, description, created, updated, version)
    values (1, 'test timer 1', '2020-04-05T22:33:37.829+0200', '2020-04-05T22:33:37.829+0200', 1);
insert into timer (id, description, created, updated, version)
    values (2, 'test timer 2', '2020-04-05T22:33:37.829+0200', '2020-04-05T22:33:37.829+0200', 1);

select nextval ('timerschedule_id_sequence');
insert into timer_schedule (id, timer_id, time, days, created, updated, version)
    values (1, 1, '06:30:00', '1,2,3,4,5', '2020-04-05T22:33:37.829+0200', '2020-04-05T22:33:37.829+0200', 1);
insert into timer_schedule (id, timer_id, time, days, created, updated, version)
    values (2, 1, '08:00:00', '6,7', '2020-04-05T22:33:37.829+0200', '2020-04-05T22:33:37.829+0200', 1);
insert into timer_schedule (id, timer_id, time, days, created, updated, version)
    values (3, 2, '10:00:00', '6', '2020-04-05T22:33:37.829+0200', '2020-04-05T22:33:37.829+0200', 1);
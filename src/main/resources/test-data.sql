insert into users (id, name, email) values (1,'Jom','jame@mail.ru');
insert into users (id, name, email) values (2,'Jomes','jameAwer@mail.ru');
insert into users (id, name, email) values (3,'Jomes222','jameAwer123@mail.ru');

insert into requests (id, description, requestor_id, created) values (1,'test', 1, '2023-06-27T01:27:54');
insert into items (id, name, description, is_available,owner_id,request_id) values (1, 'name', 'test', true, 2,1);
insert into items (id, name, description, is_available,owner_id,request_id) values (2, 'name2', 'test', false, 2,1);
insert into bookings(id, start_date, end_date, item_id, booker_id ,status) values (1, '2023-06-27T01:27:54',
'2023-06-29T01:27:54', 1, 1, 'APPROVED');
insert into bookings(id, start_date, end_date, item_id, booker_id ,status) values (2, '2023-05-27T01:27:54',
'2023-07-29T01:27:54', 1, 1, 'WAITING');
drop table if exists users;
drop table if exists clothes;
drop table if exists reservations;

create table users (id integer, user_name string, password string, email string);
create table clothes (id integer, name string, genre integer, gender integer, size integer, color string, price integer, period integer, rental_user_id integer, return_deadline varchar);
create table reservations (user_id integer, clothes_id string);

insert into users
values
  (1, 'ryo', 'ryo11', 'r@gmail.com'),
  (2, 'rei', 'rei22', 'rei@yahoo.co.jp'),
  (3, 'roy', 'roy33', 'royroy@keio.jp');

insert into clothes
values
(1,'Hakama',1,1,170,'Black',50000,28,3,'2018/08/6'),
(2,'Furisode',1,2,150,'Red',100000,56,3,'2018/09/4'),
(3,'Tuxedo',2,1,180,'White',20000,7,1,'2018/07/19'),
(4,'Dress',2,2,160,'Blue',30000,14,2,'2018/08/10'),
(5,'Yukata',1,2,140,'Blue',4000,14,1,'2018/09/9'),
(6,'French Maid',2,2,160,'White',5000,14,0,0),
(7,'Cosplay(Ikirito)',2,1,170,'Black',30000,21,0,0),
(8,'Cosplay(Saber)',2,2,160,'Blue',40000,14,0,0),
(9,'Sailor',2,2,150,'White',5000,21,0,0),
(10,'Ninja',1,1,175,'Black',40000,10,0,0);

insert into reservations
values
(1,1),
(2,1),
(3,4),
(1,4),
(2,3),
(2,2);

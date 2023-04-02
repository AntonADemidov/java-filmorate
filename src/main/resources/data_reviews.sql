INSERT INTO genres (genre_id, name) VALUES (1, 'Комедия');
INSERT INTO genres (genre_id, name) VALUES (2, 'Драма');
INSERT INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
INSERT INTO genres (genre_id, name) VALUES (4, 'Триллер');
INSERT INTO genres (genre_id, name) VALUES (5, 'Документальный');
INSERT INTO genres (genre_id, name) VALUES (6, 'Боевик');

INSERT INTO mpa (mpa_id, name) VALUES (1, 'G');
INSERT INTO mpa (mpa_id, name) VALUES (2, 'PG');
INSERT INTO mpa (mpa_id, name) VALUES (3, 'PG-13');
INSERT INTO mpa (mpa_id, name) VALUES (4, 'R');
INSERT INTO mpa (mpa_id, name) VALUES (5, 'NC-17');

--заполняем фильмы
INSERT INTO films (film_id,name,description,duration,release_date,mpa_id)
VALUES (1,'Titanic','Next time choose train',123,'1991-01-01',1); --genres: 1,2,3
INSERT INTO films (film_id,name,description,duration,release_date,mpa_id)
VALUES (2,'Spider-Man','in love with MJ',124,'1992-02-02',1); --4,5,7,
INSERT INTO films (film_id,name,description,duration,release_date,mpa_id)
VALUES (3,'The Shawshank Redemption','top250 #3',125,'1993-03-03',2); --8,9
INSERT INTO films (film_id,name,description,duration,release_date,mpa_id)
VALUES (4,'Forrest Gump','run, Forrest, run!',126,'1994-04-04',3); --8,4,1,3
INSERT INTO films (film_id,name,description,duration,release_date,mpa_id)
VALUES (5,'Matrix','shmatrix',127,'1995-05-05',4); --10,5

--заполняем таблицу пользователей
INSERT INTO users (user_id,login,email,birthday,name) VALUES (1,'ruby','ruby@onrails.com','1993-02-23','Rname'); --1
INSERT INTO users (user_id,login,email,birthday,name) VALUES (2,'PHP','php@webpage.com','1994-01-01','Pname'); --2
INSERT INTO users (user_id,login,email,birthday,name) VALUES (3,'SQL','sql@database.com','1974-02-02','Sname'); --3
INSERT INTO users (user_id,login,email,birthday,name) VALUES (4,'JavaScript','js@goaway.com','1996-07-18','JSname'); --4
INSERT INTO users (user_id,login,email,birthday,name) VALUES (5,'Java','java@onelove.com','1996-01-21','Jname'); --5
INSERT INTO users (user_id,login,email,birthday,name) VALUES (6,'Assembler','asse@mbler.com','1947-03-03','Aname'); --6
INSERT INTO users (user_id,login,email,birthday,name) VALUES (7,'Cpp','cpp@ppc.com','1985-04-04','Cname'); --7
INSERT INTO users (user_id,login,email,birthday,name) VALUES (8,'Python','vape@hipster.com','1989-05-05','PYname'); --8

--Заполняем таблицу с отзывами
INSERT INTO reviews (film_id, user_id, content, useful,isPositive) VALUES (1,1,'Отзыв 1', 0 , true);
INSERT INTO reviews (film_id, user_id, content, useful,isPositive) VALUES (1,2,'Отзыв 2', 0 , true);
INSERT INTO reviews (film_id, user_id, content, useful,isPositive) VALUES (1,3,'Отзыв 3', 0 , false);
INSERT INTO reviews (film_id, user_id, content, useful,isPositive) VALUES (2,4,'Отзыв 4', 0 , true);


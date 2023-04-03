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

INSERT INTO events_types (type_id, event_name) VALUES (1, 'LIKE');
INSERT INTO events_types (type_id, event_name) VALUES (2, 'FRIEND');
INSERT INTO events_types (type_id, event_name) VALUES (3, 'REVIEW');

INSERT INTO operations (operation_id, operation_name) VALUES (1, 'ADD');
INSERT INTO operations (operation_id, operation_name) VALUES (2, 'REMOVE');
INSERT INTO operations (operation_id, operation_name) VALUES (3, 'UPDATE');
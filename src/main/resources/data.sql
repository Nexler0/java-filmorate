DELETE FROM FILMS;
DELETE FROM USERS;
DELETE FROM LIKES;
DELETE FROM FILMS_GENRE;
DELETE FROM GENRE;
DELETE FROM RATE;
DELETE FROM FRIENDS;
DELETE FROM FRIENDSHIP_STATUS;

INSERT INTO PUBLIC.GENRE (GENRE_ID, NAME)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO PUBLIC.RATE (RATE_ID, NAME)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG_13'),
       (4, 'R'),
       (5, 'NC_17');

INSERT INTO PUBLIC.FRIENDSHIP_STATUS (STATUS_ID, NAME)
VALUES (1, 'CONFIRMED'),
       (2, 'UNCONFIRMED');

/*
INSERT INTO PUBLIC.FILMS (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, USER_RATE)
VALUES (1, 'Cars', 'Cartoon about car', '2008-06-17 00:00:00', 131, 1, 1),
       (2, 'Jake', 'Horror', '2009-07-13 00:00:00', 98, 4, 2),
       (3, 'New film', 'New film about friends', '1999-04-30 00:00:00', 120, 3, 4);

INSERT INTO PUBLIC.FILMS_GENRE (FILM_ID, GENRE_ID)
VALUES (1, 1),
       (1, 4),
       (2, 3),
       (3, 1);

*/

DELETE FROM FILMS LIMIT 100;
DELETE FROM USERS LIMIT 100;
DELETE FROM FILMS_GENRE LIMIT 100;
DELETE FROM GENRE LIMIT 100;
DELETE FROM RATE LIMIT 100;
DELETE FROM FRIENDS LIMIT 100;
DELETE FROM FRIENDSHIP_STATUS LIMIT 100;


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
/*
INSERT INTO PUBLIC.USERS (USER_ID, EMAIL, NAME, LOGIN, BIRTHDAY)
VALUES (1, 'jopa@sef.ru', 'jon', 'jonie', '2005-05-14'),
       (2, 'gold@af.ry', 'LOG', 'daf', '1995-08-17'),
       (3, 'ld@af.ry', 'OG', 'df', '1997-08-30'),
       (4, 'ljlkd@af.ry', 'OkjG', 'df', '1985-09-24');

INSERT INTO FRIENDS (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS)
VALUES (1, 2, 1),
       (2, 3, 1),
       (3, 1, 2),
       (3, 2, 1),
       (1, 4, 2),
       (2, 4, 1),
       (1, 3, 1);
*/
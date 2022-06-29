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

INSERT INTO FILMS
VALUES
    ( 1, 'film1', 'description1', '2020-12-12', 120, 2, 0),
    ( 2, 'film2', 'description2', '2020-12-12', 120, 2, 0),
    ( 3, 'film3', 'description3', '2020-12-12', 120, 2, 0),
    ( 4, 'film4', 'description4', '2020-12-12', 120, 2, 0),
    ( 5, 'film5', 'description5', '2020-12-12', 120, 2, 0),
    ( 6, 'film6', 'description6', '2020-12-12', 120, 2, 0),
    ( 7, 'film7', 'description7', '2020-12-12', 120, 2, 0),
    ( 8, 'film8', 'description8', '2020-12-12', 120, 2, 0),
    ( 9, 'film9', 'description9', '2020-12-12', 120, 2, 0),
    ( 10, 'film10', 'description10', '2020-12-12', 120, 2, 0);

INSERT INTO USERS
VALUES ( 1, 'gge@kj.fg', 'name1', 'login1', '2018-12-12' ),
       ( 2, 'gge@kj.fg', 'name2', 'login2', '2018-12-12' ),
       ( 3, 'gge@kj.fg', 'name3', 'login3', '2018-12-12' ),
       ( 4, 'gge@kj.fg', 'name4', 'login4', '2018-12-12' ),
       ( 5, 'gge@kj.fg', 'name5', 'login5', '2018-12-12' ),
       ( 6, 'gge@kj.fg', 'name6', 'login6', '2018-12-12' ),
       ( 7, 'gge@kj.fg', 'name7', 'login7', '2018-12-12' ),
       ( 8, 'gge@kj.fg', 'name8', 'login8', '2018-12-12' ),
       ( 9, 'gge@kj.fg', 'name9', 'login9', '2018-12-12' ),
       ( 10, 'gge@kj.fg', 'name10', 'login10', '2018-12-12' );

INSERT INTO LIKES
VALUES ( 3, 1),
       ( 4, 1),
       ( 7, 1),
       ( 8, 1),
       ( 2, 2),
       ( 6, 2),
       ( 5, 3),
       ( 9, 3),
       ( 1, 4),
       ( 3, 4),
       ( 4, 4),
       ( 7, 4),
       ( 8, 4),
       ( 10, 4),
       ( 2, 5),
       ( 4, 5),
       ( 6, 5),
       ( 7, 5),
       ( 10, 5),
       ( 1, 6),
       ( 2, 6),
       ( 4, 6),
       ( 6, 6),
       ( 3, 7),
       ( 7, 7),
       ( 8, 7),
       ( 3, 8),
       ( 5, 8),
       ( 7, 8),
       ( 8, 8),
       ( 3, 9),
       ( 4, 9),
       ( 7, 9),
       ( 8, 9),
       ( 1, 10),
       ( 2, 10),
       ( 3, 10),
       ( 4, 10),
       ( 5, 10);

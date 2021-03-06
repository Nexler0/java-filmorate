CREATE TABLE IF NOT EXISTS FRIENDSHIP_STATUS
(
    STATUS_ID INTEGER NOT NULL,
    NAME      VARCHAR,
    CONSTRAINT FRIENDSHIP_STATUS_PK PRIMARY KEY (STATUS_ID)
);
COMMENT ON TABLE FRIENDSHIP_STATUS IS 'Статус дружбы';

CREATE TABLE IF NOT EXISTS GENRE
(
    GENRE_ID INTEGER NOT NULL,
    NAME     VARCHAR NOT NULL,
    CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);
COMMENT ON TABLE GENRE IS 'Жанры фильма';

CREATE TABLE IF NOT EXISTS RATE
(
    RATE_ID INTEGER NOT NULL,
    NAME    VARCHAR NOT NULL,
    CONSTRAINT RATE_PK PRIMARY KEY (RATE_ID)
);
COMMENT ON TABLE RATE IS 'Информация рейтинга фильмов';

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID  INTEGER not null,
    EMAIL    VARCHAR not null,
    NAME     VARCHAR,
    LOGIN    VARCHAR,
    BIRTHDAY DATE,
    CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);
COMMENT ON TABLE USERS IS 'Хранилище пользователей';

CREATE TABLE IF NOT EXISTS FRIENDS
(
    USER_ID           INTEGER NOT NULL,
    FRIEND_ID         INTEGER NOT NULL,
    FRIENDSHIP_STATUS INTEGER NOT NULL DEFAULT 2
);
comment on table FRIENDS is 'Статус дружбы между пользователями';

CREATE TABLE IF NOT EXISTS FILMS_GENRE
(
    FILM_ID  INTEGER NOT NULL,
    GENRE_ID INTEGER
);
COMMENT ON TABLE FILMS_GENRE IS 'Содержит  информацию о жанрах фильмов';

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER NOT NULL,
    NAME         VARCHAR NOT NULL,
    DESCRIPTION  VARCHAR,
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER,
    USER_RATE    INTEGER,
    CONSTRAINT film_PK PRIMARY KEY (FILM_ID)--доблен первичный ключ для таблицы

);
COMMENT ON TABLE FILMS IS 'Содержит подробную информацию о фильмам';

CREATE TABLE IF NOT EXISTS LIKES
(
    FILM_ID INTEGER,
    USER_ID INTEGER
);

CREATE TABLE IF NOT EXISTS REVIEWS
(
    REVIEWS_ID  INTEGER NOT NULL,
    CONTENT     VARCHAR,
    IS_POSITIVE BOOLEAN,
    USER_ID     INTEGER NOT NULL,
    FILM_ID     INTEGER NOT NULL,
    USEFUL      INTEGER,
    CONSTRAINT REVIEWS_PK PRIMARY KEY (REVIEWS_ID)
);

CREATE TABLE IF NOT EXISTS REVIEWS_LIKES
(
    REVIEWS_ID  INT NOT NULL,
    USER_ID     INT NOT NULL,
    IS_POSITIVE BOOLEAN
);

--таблица для директора фильмов
CREATE TABLE IF NOT EXISTS DIRECTORS
(
    ID   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME varchar(40) NOT NULL
);

--таблица для связи декторов и фильма
CREATE TABLE IF NOT EXISTS FILMS_DIRECTORS
(
    FILM_ID     INTEGER REFERENCES FILMS (FILM_ID),
    DIRECTOR_ID INTEGER REFERENCES DIRECTORS (ID),
    CONSTRAINT film_direc_PK PRIMARY KEY (FILM_ID, DIRECTOR_ID)
);
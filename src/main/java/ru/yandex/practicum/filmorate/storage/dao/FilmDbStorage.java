package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcT;
    private static int filmId = 0;

    public FilmDbStorage(JdbcTemplate jdbcT) {
        this.jdbcT = jdbcT;
    }

    @Override
    public List<Film> findAllFilms() {
        List<Film> filmList = new ArrayList<>();
        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT *, G2.GENRE_ID AS GENRE_ID, " +
                        "R.RATE_ID AS RATE_ID " +
                        "FROM FILMS " +
                        "JOIN FILMS_GENRE AS FG on FG.FILM_ID = FILMS.FILM_ID " +
                        "JOIN GENRE AS G2 on G2.GENRE_ID = FG.GENRE_ID " +
                        "JOIN RATE AS R on R.RATE_ID = FILMS.RATE "
        );
        return getFilmsList(filmList, userRow);
    }

    @Override
    public Film addFilm(Film film) {
        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT COUNT(FILM_ID) AS SUM FROM FILMS"
        );
        if (userRow.next()) {
            filmId = userRow.getInt("SUM");
        }
        log.info("Последний ID:{} ", filmId);
        if (film.getId() == 0 || film.getId() < 0 || film.getId() >= filmId) {
            filmId++;
            film.setId(filmId);
        }
        return insertFilmInDb(film);
    }

    @Override
    public Film updateFilm(Film film) {
        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT * FROM FILMS WHERE FILM_ID = ?", film.getId()
        );
        if (userRow.next()) {
            return updateFilmInDb(film);
        } else {
            throw new NotFoundException("Такого фильма не существует");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> filmList = new ArrayList<>();
        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT *, G2.GENRE_ID AS GENRE_ID, " +
                        "R.RATE_ID AS RATE_ID " +
                        "FROM FILMS " +
                        "LEFT JOIN FILMS_GENRE AS FG on FG.FILM_ID = FILMS.FILM_ID " +
                        "LEFT JOIN GENRE AS G2 on G2.GENRE_ID = FG.GENRE_ID " +
                        "LEFT JOIN RATE AS R on R.RATE_ID = FILMS.RATE " +
                        "ORDER BY USER_RATE DESC LIMIT ? ", count
        );
        return getFilmsList(filmList, userRow);
    }

    @Override
    public Film getFilmById(int id) {
        Film film = getFilmFromDbById(id);
        SqlRowSet likesRow = jdbcT.queryForRowSet(
                "SELECT * FROM LIKES"
        );
        while (likesRow.next()) {
            film.addUserLike(likesRow.getInt("USER_ID"));
        }
        return film;
    }

    @Override
    public String likeTheMovie(Integer id, Integer userId) {
        Film film = getFilmFromDbById(id);
        film.addUserLike(userId);
        insertFilmInDb(film);
        return "Like добавлен";
    }

    @Override
    public String deleteTheMovieLike(Integer id, Integer userId) {
        Film film = getFilmFromDbById(id);
        film.deleteUserLike(userId);
        insertFilmInDb(film);
        return "Like удален";
    }

    private boolean checkFilmInDb(Film film) {
        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT * FROM FILMS WHERE NAME = ? AND DESCRIPTION = ? AND RELEASE_DATE = ?",
                film.getName(), film.getDescription(), film.getReleaseDate()
        );
        return userRow.next();
    }

    private Film getFilmFromDbById(int id) {
        Film film = null;

        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT *, G2.GENRE_ID AS GENRE_ID, G2.NAME AS GENRE_NAME, " +
                        "R.RATE_ID AS RATE_ID, R.NAME AS RATE_NAME, L.USER_ID AS USER_LIKE " +
                        "FROM FILMS " +
                        "LEFT JOIN FILMS_GENRE AS FG on FG.FILM_ID = FILMS.FILM_ID " +
                        "LEFT JOIN GENRE AS G2 on G2.GENRE_ID = FG.GENRE_ID " +
                        "LEFT JOIN RATE AS R on R.RATE_ID = FILMS.RATE " +
                        "LEFT JOIN LIKES AS L on FG.FILM_ID = L.FILM_ID " +
                        "WHERE FILMS.FILM_ID = ?", id
        );
        while (userRow.next()) {
            film = new Film(userRow.getString("NAME"),
                    userRow.getString("RELEASE_DATE"),
                    userRow.getString("DESCRIPTION"),
                    userRow.getInt("DURATION"),
                    userRow.getInt("USER_RATE"));
            film.setId(userRow.getInt("FILM_ID"));
            film.setMpa(new Mpa(userRow.getInt("RATE_ID")));
            film.addUserLike(userRow.getInt("USER_LIKE"));
        }
        if (film != null) {
            SqlRowSet genreRow = jdbcT.queryForRowSet(
                    "SELECT * FROM FILMS_GENRE WHERE FILM_ID = ?", id
            );
            while (genreRow.next()) {
                film.addGenre(new Genre(genreRow.getInt("GENRE_ID")));
            }
            return film;
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    private Film insertFilmInDb(Film film) {
        jdbcT.update("DELETE FROM FILMS WHERE FILM_ID = ?", film.getId());
        jdbcT.update("DELETE FROM FILMS_GENRE WHERE FILM_ID = ?", film.getId());
        jdbcT.update("DELETE FROM LIKES WHERE FILM_ID = ?", film.getId());
        jdbcT.update(
                "INSERT INTO FILMS (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, USER_RATE)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)", film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getRate()
        );
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcT.update(
                        "INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)", film.getId(),
                        genre.getId()
                );
            }
        }
        if (film.getLikesId() != null) {
            for (int like : film.getLikesId()) {
                jdbcT.update(
                        "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)", film.getId(),
                        like
                );
            }
        }

        return film;
    }

    private Film updateFilmInDb(Film film) {
        jdbcT.update(
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
                        "DURATION = ?, RATE = ?, USER_RATE= ? " +
                        "WHERE FILM_ID = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getRate(), film.getId()
        );
        jdbcT.update("DELETE FROM FILMS_GENRE WHERE FILM_ID = ?", film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcT.update(
                        "INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)",
                        film.getId(), genre.getId()
                );
            }
        }
        Film film1 = new Film(film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getRate());
        film1.setMpa(film.getMpa());
        film1.setId(film.getId());
        if (!film.getLikesId().isEmpty()) {
            for (int id : film.getLikesId()) {
                film1.addUserLike(id);
            }
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres())
                film1.addGenre(genre);
        } else if (film.getGenres() != null && film.getGenres().isEmpty()){
            film1.createGenreStorage();
        }
        return film1;
    }

    private List<Film> getFilmsList(List<Film> filmList, SqlRowSet userRow) {
        while (userRow.next()) {
            Film film = new Film(userRow.getString("NAME"),
                    userRow.getString("RELEASE_DATE"),
                    userRow.getString("DESCRIPTION"),
                    userRow.getInt("DURATION"),
                    userRow.getInt("USER_RATE"));
            film.setId(userRow.getInt("FILM_ID"));
            film.setMpa(new Mpa(userRow.getInt("RATE_ID")));
            Genre genre = null;
            try {
                genre = new Genre(userRow.getInt("GENRE_ID"));
                film.addGenre(genre);
            } catch (NotFoundException info) {
                log.info("Фильм не имеет жанра");
            }
            if (!filmList.contains(film)) {
                filmList.add(film);
            } else {
                Film updateFilm = filmList.stream().filter(film1 -> film1.equals(film)).findAny().get();
                if (updateFilm.getGenres().contains(genre)) {
                    updateFilm.addGenre(genre);
                }
            }
        }
        SqlRowSet likesRow = jdbcT.queryForRowSet(
                "SELECT * FROM LIKES"
        );
        while (likesRow.next()) {
            filmList.stream().filter(film -> film.getId() == (likesRow.getInt("FILM_ID")))
                    .findAny().get().addUserLike(likesRow.getInt("USER_ID"));
        }
        log.info("Нашлось {} в хранилище фильмов", filmList.size());
        return filmList;
    }
}
package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GetCommonFilmsErrorException;
import ru.yandex.practicum.filmorate.exceptions.GetRecommendedFilmsErrorException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.director.FilmDirector;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FilmDirectorsDao;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcT;
    private final FilmDirectorsDao filmDirectorsDao;
    private final DirectorDao directorDao;
    private static int filmId = 0;

    private static final String SQL_GET_RECOMMENDED_FILM_ID_LIST =
            "WITH films_liked(film_id) AS (" +
                    "    SELECT likes.film_id" +
                    "    FROM likes" +
                    "    WHERE likes.user_id = ?)," +
                    "" +
                    "    common_likes_users (user_id, matches) AS (" +
                    "        SELECT user_id, count(*)" +
                    "        FROM likes" +
                    "        WHERE film_id IN (SELECT * FROM films_liked)" +
                    "            AND user_id <> ?" +
                    "        GROUP BY user_id" +
                    "        ORDER BY count(*) DESC)" +
                    "" +
                    "SELECT film_id, SUM(common_likes_users.matches) AS rate " +
                    "FROM likes " +
                    "    RIGHT JOIN common_likes_users" +
                    "        ON likes.user_id = common_likes_users.user_id " +
                    "WHERE likes.user_id IN (SELECT user_id FROM common_likes_users)" +
                    "    AND film_id NOT IN (SELECT * FROM films_liked)" +
                    "GROUP BY film_id " +
                    "ORDER BY rate DESC";

    public FilmDbStorage(JdbcTemplate jdbcT, FilmDirectorsDao filmDirectorsDao, DirectorDao directorDao) {
        this.jdbcT = jdbcT;
        this.filmDirectorsDao = filmDirectorsDao;
        this.directorDao = directorDao;
    }

    @Override
    public List<Film> findAllFilms() {
        List<Film> filmList = new ArrayList<>();
        SqlRowSet userRow = jdbcT.queryForRowSet(
                "SELECT *, G2.GENRE_ID AS GENRE_ID, " +
                        "R.RATE_ID AS RATE_ID " +
                        "FROM FILMS " +
                        "LEFT JOIN FILMS_GENRE AS FG on FG.FILM_ID = FILMS.FILM_ID " +
                        "LEFT JOIN GENRE AS G2 on G2.GENRE_ID = FG.GENRE_ID " +
                        "LEFT JOIN RATE AS R on R.RATE_ID = FILMS.RATE "
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
    public String deleteTheMovie(int id) {
        if (getFilmById(id) != null) {
            jdbcT.update("DELETE FROM FILMS_GENRE WHERE FILM_ID = ?", id);
            jdbcT.update("DELETE FROM FILMS WHERE FILM_ID = ?", id);
            log.info("Фильм c id:{} удален", id);
            return String.format("Фильм c id:%s удален", id);
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public String deleteTheMovieLike(Integer id, Integer userId) {
        Film film = getFilmFromDbById(id);
        film.deleteUserLike(userId);
        insertFilmInDb(film);
        return "Like удален";
    }

    @Override
    public List<Film> getSortByParamFilms(Integer directorId, String param){
        if (param.equals("year")){
            return getSortByYearFilms(directorId);
        } else if (param.equals("likes")){
            return getSortByLikesFilms(directorId);
        }else {
            throw new ValidationException("Параметр в запросе задан не верно!");
        }
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
            //Получение списка директоров для фильма
            SqlRowSet filmRows = jdbcT.queryForRowSet( "select * from FILMS where FILM_ID = ?", id);
            if (filmRows.next()){
                if (!filmDirectorsDao.findDirectorByFilms(id).isEmpty()){
                    List<FilmDirector> listOfDirectors = filmDirectorsDao.findDirectorByFilms(id);
                    List<Director> directors = new ArrayList<>();
                    for (FilmDirector filmDirector : listOfDirectors) {
                        directors.add(Director.builder()
                                .id(filmDirector.getDirectorsId())
                                .name(directorDao.getDirById(filmDirector.getDirectorsId()).get().getName())
                                .build());
                    }
                    film.setDirectors(directors);
                } else {
                    film.setDirectors(new ArrayList<>());
                }
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
        //Добавление директора к фильму если он есть в теле
        if (film.getDirectors() != null){
            List<Director> listOfDirectors = film.getDirectors();
            if (!listOfDirectors.isEmpty()){
                for (Director director : listOfDirectors) {
                    if (directorDao.containsById(director.getId())){
                        filmDirectorsDao.addDirectorToFilm(film.getId(), director.getId());
                    }else {
                        throw new NotFoundException("Такого директора в спике нет!");
                    }
                }
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
        //Добаление директора к созданному
        film1.setDirectors(film.getDirectors());
        if (!film.getLikesId().isEmpty()) {
            for (int id : film.getLikesId()) {
                film1.addUserLike(id);
            }
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres())
                film1.addGenre(genre);
        } else if (film.getGenres() != null && film.getGenres().isEmpty()) {
            film1.createGenreStorage();
        }
        //Добаление директора к фильму через Update
        if (film1.getDirectors() != null){
            List<Director> listOfDirectors = film1.getDirectors();
            if (!listOfDirectors.isEmpty()){
                List<FilmDirector> listDir = filmDirectorsDao.findDirectorByFilms(film1.getId());
                for (FilmDirector filmDirector : listDir) {
                    filmDirectorsDao.deleteDirectorFromFilm(film1.getId(), filmDirector.getDirectorsId());
                }
                for (Director director : listOfDirectors) {
                    if (directorDao.containsById(director.getId())){
                        filmDirectorsDao.addDirectorToFilm(film1.getId(), director.getId());
                    }else {
                        throw new NotFoundException("Такого директора в спике нет!");
                    }
                }
                return getFilmById(film1.getId());
            }else {
                for (Director director : listOfDirectors) {
                    if (directorDao.containsById(director.getId())){
                        filmDirectorsDao.addDirectorToFilm(film1.getId(), director.getId());
                    }else {
                        throw new NotFoundException("Такого директора в спике нет!");
                    }
                    return getFilmById(film1.getId());
                }
            }
        }else {
            List<FilmDirector> listDir = filmDirectorsDao.findDirectorByFilms(film1.getId());
            for (FilmDirector filmDirector : listDir) {
                filmDirectorsDao.deleteDirectorFromFilm(film1.getId(), filmDirector.getDirectorsId());
            }
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
            //Получение списка директоров для фильма
            SqlRowSet filmRows = jdbcT.queryForRowSet( "select * from FILMS where FILM_ID = ?", film.getId());
            if (filmRows.next()){
                if (!filmDirectorsDao.findDirectorByFilms(film.getId()).isEmpty()){
                    List<FilmDirector> listOfDirectors = filmDirectorsDao.findDirectorByFilms(film.getId());
                    List<Director> directors = new ArrayList<>();
                    for (FilmDirector filmDirector : listOfDirectors) {
                        directors.add(Director.builder()
                                .id(filmDirector.getDirectorsId())
                                .name(directorDao.getDirById(filmDirector.getDirectorsId()).get().getName())
                                .build());
                    }
                    film.setDirectors(directors);
                } else {
                    film.setDirectors(new ArrayList<>());
                }
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

    /*
     *Возвращает список фильмов, рекомендованных к просмотру пользователю.
     * Алгоритм:
     * 1. Запрос в БД список лайков фильмов пользователем;
     * 2. Запрос в БД список пользователей с лайками фильмов совпадающих с выбором пользователя
     *с количеством совпавших лайков;
     * 3. Запрос в БД списка id рекомендованных фильмов в порядке приоритета. Список фильмов формируется из фильмов
     * для которых пользователи с совпадающими лайками поставили лайк, а пользователь нет. Приоритет выставляется в
     * соответствии с рейтингом, который формируется как сумма лайков пользователей со схожими с пользователем вкусами
     * с учетом весового коэффициента. В качестве весового коэффициента используется количество совпадений по лайкам у
     * пользователя с пользователями с похожими вкусами.
     */
    @Override
    public Collection<Film> getRecommendations(int id) {
        Collection<Film> recommendedFilms;

        try {
            recommendedFilms = jdbcT.query(SQL_GET_RECOMMENDED_FILM_ID_LIST, this::makeRecommendedFilm, id, id);
        } catch (DataAccessException exception) {
            throw new GetRecommendedFilmsErrorException(exception.getMessage(), "Ошибка при запросе в БД",
                    "Запрос списка рекомендованных к просмотру фильмов для пользователя id: " + id);
        }

        log.trace("Список рекомендованных фильмов для пользователя id={} создан", id);

        return recommendedFilms;
    }

    private Film makeRecommendedFilm(ResultSet rs, int rowNum) throws SQLException {
        return this.getFilmById(rs.getInt("film_id"));
    }

    /**
     * Метод получения фильмов директора по годам
     * Method for obtaining director's films by year
     *
     * @param directorId
     * @return
     */
    private List<Film> getSortByYearFilms(Integer directorId) {
        if (directorDao.containsById(directorId)){
            String sqlQuery = "SELECT *, G2.GENRE_ID AS GENRE_ID, R.RATE_ID AS RATE_ID " +
                    "FROM FILMS " +
                    "LEFT JOIN FILMS_GENRE AS FG on FG.FILM_ID = FILMS.FILM_ID " +
                    "LEFT JOIN GENRE AS G2 on G2.GENRE_ID = FG.GENRE_ID " +
                    "LEFT JOIN RATE AS R on R.RATE_ID = FILMS.RATE " +
                    "WHERE FILMS.FILM_ID IN (" +
                    "        SELECT FILM_ID" +
                    "        FROM FILMS_DIRECTORS " +
                    "        WHERE DIRECTOR_ID = ?" +
                    ") " +
                    "ORDER BY RELEASE_DATE";

            List<Film> filmList = new ArrayList<>();
            SqlRowSet userRow = jdbcT.queryForRowSet(sqlQuery, directorId);
            return getFilmsList(filmList, userRow);
        }else {
            throw new NotFoundException("Такого директора нет!");
        }

    }


    /**
     * Метод получения фильмов директора по лайкам
     * Method for getting director's films by likes
     *
     * @param directorId
     * @return
     */
    private List<Film> getSortByLikesFilms(Integer directorId) {
        //проверить его в БД а потом если есть выводить
        if (directorDao.containsById(directorId)){
            String sqlQuery = "SELECT *, G2.GENRE_ID AS GENRE_ID, R.RATE_ID AS RATE_ID " +
                    "FROM FILMS " +
                    "LEFT JOIN FILMS_GENRE AS FG on FG.FILM_ID = FILMS.FILM_ID " +
                    "LEFT JOIN GENRE AS G2 on G2.GENRE_ID = FG.GENRE_ID " +
                    "LEFT JOIN RATE AS R on R.RATE_ID = FILMS.RATE " +
                    "WHERE FILMS.FILM_ID IN (" +
                    "        SELECT FILM_ID" +
                    "        FROM FILMS_DIRECTORS " +
                    "        WHERE DIRECTOR_ID = ?" +
                    ") " +
                    "ORDER BY USER_RATE DESC";

            List<Film> filmList = new ArrayList<>();
            SqlRowSet userRow = jdbcT.queryForRowSet(sqlQuery, directorId);
            return getFilmsList(filmList, userRow);
        } else {
            throw new NotFoundException("Такого директора нет!");
        }
    }

    private static final String SQL_GET_COMMON_FILMS_ID_LIST =
            "WITH common_films_id AS (SELECT film_id, COUNT(*)\n" +
            "                         FROM LIKES\n" +
            "                         WHERE user_id IN (?, ?)\n" +
            "                         GROUP BY film_id\n" +
            "                         HAVING COUNT(*) = 2)\n" +
            "SELECT film_id, COUNT(*)\n" +
            "FROM likes\n" +
            "WHERE film_id IN (SELECT film_id FROM common_films_id)\n" +
            "GROUP BY film_id\n" +
            "ORDER BY COUNT(*) DESC";

    //Возвращает список фильмов, отсортированных по популярности.
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        Collection<Film> commonFilms;
        try {
           commonFilms = jdbcT.query(SQL_GET_COMMON_FILMS_ID_LIST, this::makeCommonFilm, userId, friendId);
        } catch (DataAccessException exception) {
        throw new GetCommonFilmsErrorException(exception.getMessage(), "Ошибка при запросе в БД",
                "Запрос общих фильмов отсортированных по популярности. userId: " + userId
                        + "friendId" + friendId);
    }

        log.trace("Запрос общих фильмов отсортированных по популярности создан. userId: {} " +
                "friendId: {}", userId, friendId);
        return commonFilms;
    }

    private Film makeCommonFilm(ResultSet rs, int rowNum) throws SQLException {
        return this.getFilmById(rs.getInt("film_id"));
    }
}
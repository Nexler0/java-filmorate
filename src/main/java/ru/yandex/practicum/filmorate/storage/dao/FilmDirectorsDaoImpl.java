package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.director.FilmDirector;
import ru.yandex.practicum.filmorate.storage.FilmDirectorsDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Repository
public class FilmDirectorsDaoImpl implements FilmDirectorsDao {

    private final Logger log = LoggerFactory.getLogger(FilmDirectorsDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public FilmDirectorsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addDirectorToFilm(Integer filmId, Integer directorId) {
        String insertQuery = "insert into FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?, ?)";
        int status = jdbcTemplate.update(insertQuery, filmId, directorId);
        if (status != 0) {
            log.info("Добавлен директор к фильму: ID {}", filmId);
        } else {
            log.info("Не добавлен директор к фильму: ID {}", filmId);
        }
    }

    @Override
    public void deleteDirectorFromFilm(Integer filmId, Integer directorId) {
        String sql = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
        Object[] args = new Object[]{filmId, directorId};
        jdbcTemplate.update(sql, args);
        log.info("Удален директор у фильма идентификатором {} {}", filmId, directorId);
    }

    @Override
    public boolean containsDirectorInFilmById(Integer filmId, Integer directorId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from FILMS_DIRECTORS where FILM_ID = ? AND DIRECTOR_ID = ?", filmId, directorId);
        if (userRows.next()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<FilmDirector> findDirectorByFilms(Integer filmId) {
        String sql = "SELECT * from FILMS_DIRECTORS WHERE FILM_ID = " + filmId;
        log.info("Запрос на получение всех директоров у фильма.");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmDirectors(rs));
    }

    private FilmDirector makeFilmDirectors(ResultSet rs) throws SQLException {
        return FilmDirector.builder()
                .filmId(rs.getInt("FILM_ID"))
                .directorsId(rs.getInt("DIRECTOR_ID"))
                .build();
    }

    @Override
    public List<FilmDirector> findFilmByDirector(Integer directorId) {
        String sql = "SELECT * from FILMS_DIRECTORS WHERE DIRECTOR_ID = " + directorId;
        log.info("Запрос на получение всех фильмов у директора.");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectorFilms(rs));
    }

    private FilmDirector makeDirectorFilms(ResultSet rs) throws SQLException {
        return FilmDirector.builder()
                .filmId(rs.getInt("FILM_ID"))
                .directorsId(rs.getInt("DIRECTOR_ID"))
                .build();
    }
}

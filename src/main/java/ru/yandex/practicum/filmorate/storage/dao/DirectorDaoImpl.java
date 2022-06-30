package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Repository
public class DirectorDaoImpl implements DirectorDao {

    private final Logger log = LoggerFactory.getLogger(DirectorDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Director> addDirector(Director director) {
        String insertSql = "INSERT INTO DIRECTORS (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[] { "ID" });
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        log.info("Директор добавлен: ID {}, name {}", director.getId(), director.getName());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> updateDirectors(Director director) {
        jdbcTemplate.update(
                "UPDATE DIRECTORS SET NAME = ? " +
                        "WHERE id = ? ",  director.getName(), director.getId());
        log.info("Директор добавлен: name {}",director.getName());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> getDirById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from DIRECTORS where id = ?", id);
        if (userRows.next()){
            log.info("Директор найден: {} {}", userRows.getLong("id"), userRows.getString("name"));
            Director director = Director.builder()
                    .id(userRows.getLong("id"))
                    .name(userRows.getString("name"))
                    .build();
            return Optional.of(director);
        } else {
            log.info("Директор с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public void deleteDirector(Long id) {
//        log.info("Рейтинг у фильма {} повышен.", idFilm);
//        String sql = "UPDATE films SET rate = rate + 1 WHERE id = ?";
//        Object[] args = new Object[] {idFilm};
//        jdbcTemplate.update(sql, args);

        String sql = "DELETE FROM DIRECTORS WHERE id = ?";
        Object[] args = new Object[] {id};
        log.info("Директор с идентификатором {} удален.", id);
        jdbcTemplate.update(sql, args);
    }

    @Override
    public List<Director> getAllDirector() {
        String sql = "SELECT * FROM DIRECTORS";
        log.info("Запрос на получение всех директоров.");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectors(rs));

    }
    private Director makeDirectors(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public boolean containsById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet( "select * from DIRECTORS where id = ?", id);
        if (userRows.next()){
            return true;
        } else {
            return false;
        }
    }
}

package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDaoImpl implements DirectorDao {

    private final Logger log = LoggerFactory.getLogger(DirectorDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_DIRECTORS_SQL = "INSERT INTO DIRECTORS (name) VALUES (?)";
    private static final String UPDATE_DIRECTORS_SQL = "UPDATE DIRECTORS SET NAME = ? WHERE id = ? ";
    private static final String SELECT_DIRECTOR_AT_ID_SQL = "SELECT * FROM DIRECTORS WHERE ID = ?";
    private static final String DELETE_DIRECTOR_BY_ID_SQL = "DELETE FROM DIRECTORS WHERE id = ?";
    private static final String SELECT_ALL_DIRECTORS_SQL = "SELECT * FROM DIRECTORS";

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Director> addDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_DIRECTORS_SQL, new String[]{"ID"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        log.info("Директор добавлен: ID {}, name {}", director.getId(), director.getName());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> updateDirectors(Director director) {
        jdbcTemplate.update(
                UPDATE_DIRECTORS_SQL, director.getName(), director.getId());
        log.info("Директор добавлен: name {}", director.getName());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> getDirById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_DIRECTOR_AT_ID_SQL, id);
        if (userRows.next()) {
            log.info("Директор найден: {} {}", userRows.getInt("id"), userRows.getString("name"));
            Director director = Director.builder()
                    .id(userRows.getInt("id"))
                    .name(userRows.getString("name"))
                    .build();
            return Optional.of(director);
        } else {
            log.info("Директор с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public void deleteDirector(Integer id) {
        Object[] args = new Object[]{id};
        log.info("Директор с идентификатором {} удален.", id);
        jdbcTemplate.update(DELETE_DIRECTOR_BY_ID_SQL, args);
    }

    @Override
    public List<Director> getAllDirector() {
        log.info("Запрос на получение всех директоров.");
        return jdbcTemplate.query(SELECT_ALL_DIRECTORS_SQL, (rs, rowNum) -> makeDirectors(rs));

    }

    private Director makeDirectors(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public boolean containsById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_DIRECTOR_AT_ID_SQL, id);
        return userRows.next();
    }
}

package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String GET_FEED_SQL = "SELECT E.EVENT_ID, EVENT_TIME, OPERATION_NAME, EVENT_NAME, ENTITY_ID\n" +
            "FROM EVENTS AS E\n" +
            "    JOIN EVENT_TYPE ET on E.EVENT_TYPE_ID = ET.EVENT_TYPE_ID\n" +
            "    JOIN OPERATION_TYPE OT on E.OPERATION_TYPE_ID = OT.OPERATION_TYPE_ID\n" +
            "    JOIN EVENTS_USERS EU on E.ENTITY_ID = EU.EVENT_ID\n" +
            "WHERE USER_ID = ?";

    public List<Event> getFeed(int userId) {
        SqlRowSet feedRows = jdbcTemplate.queryForRowSet(GET_FEED_SQL, userId);
        List<Event> feed = new ArrayList<>();
        while (feedRows.next()) {
            Event event = new Event(
                    feedRows.getInt("EVENT_ID"),
                    feedRows.getTimestamp("EVENT_TIME"),
                    userId,
                    EventType.valueOf(feedRows.getString("EVENT_NAME")),
                    OperationType.valueOf(feedRows.getString("OPERATION_NAME")),
                    feedRows.getInt("ENTITY_ID")
            );
            feed.add(event);
        }
        log.info("Запрошена лента новостей для пользователя {}", userId);
        return feed;
    }
}

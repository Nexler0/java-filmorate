package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
public class Event {
    private Integer eventId;
    private Timestamp timestamp;
    private Integer userId;
    private EventType eventType;
    private OperationType operationType;
    private Integer entityId;

    public Event(Timestamp timestamp, Integer userId, EventType eventType,
                 OperationType operationType, Integer entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operationType = operationType;
        this.entityId = entityId;
    }

    public Event(int id, Timestamp timestamp, Integer userId, EventType eventType,
                 OperationType operationType, Integer entityId) {
        this.eventId = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operationType = operationType;
        this.entityId = entityId;
    }

    private static final String SQL_FOR_ASSIGNMENT_ID_FOR_EVENT = "SELECT EVENT_ID FROM EVENTS WHERE EVENT_TIME = ?";

    public static void addEventIntoDataBase(int userId, int entityId, OperationType operationType, EventType eventType,
                                            JdbcTemplate jdbcTemplate) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");
        Event event = new Event(new Timestamp(System.currentTimeMillis()),
                userId,
                eventType,
                operationType,
                entityId);
        simpleJdbcInsert.execute(event.toMap());
        assignIdForEvent(event, jdbcTemplate);
        event.insertIntoUsersEvents(jdbcTemplate);
    }

    public static void assignIdForEvent(Event event, JdbcTemplate jdbcTemplate) {
        if (event.getEventId() == null) {
            SqlRowSet eventIdRows = jdbcTemplate.queryForRowSet(SQL_FOR_ASSIGNMENT_ID_FOR_EVENT, event.getTimestamp());
            if (eventIdRows.next()) {
                event.setEventId(eventIdRows.getInt("EVENT_ID"));
            }
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("EVENT_TYPE_ID", eventType.getId());
        values.put("OPERATION_TYPE_ID", operationType.getId());
        values.put("EVENT_TIME", timestamp);
        values.put("ENTITY_ID", entityId);
        return values;
    }

    public void insertIntoUsersEvents(JdbcTemplate jdbcT) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcT)
                .withTableName("EVENTS_USERS")
                .usingGeneratedKeyColumns("EVENTS_USER_ID");
        Map<String, Object> values = new HashMap<>();
        values.put("USER_ID", userId);
        values.put("EVENT_ID", eventId);
        simpleJdbcInsert.execute(values);
    }
}

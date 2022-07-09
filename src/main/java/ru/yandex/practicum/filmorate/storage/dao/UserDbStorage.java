package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcT;
    private static int userId = 0;

    private static final String GET_ALL_USERS_SQL =
            "SELECT *, f.FRIEND_ID AS FRIEND_ID FROM USERS " +
                    "LEFT JOIN FRIENDS AS F on USERS.USER_ID = F.USER_ID";
    private static final String GET_USER_IN_DB_BY_EMAIL_LOGIN_BIRTHDAY_SQL =
            "SELECT * FROM USERS WHERE EMAIL = ? AND LOGIN = ? AND BIRTHDAY = ?";
    private static final String GET_LAST_USER_ID_SQL =
            "SELECT USER_ID AS COUNT FROM USERS ORDER BY COUNT DESC";
    private static final String INSERT_USER_SQL =
            "INSERT INTO USERS (USER_ID, EMAIL, NAME, LOGIN, BIRTHDAY) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_USERS_FRIENDS_STATUS_UNCONFIRMED_SQL =
            "INSERT INTO FRIENDS  (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS) VALUES (?, ?, 2)";
    private static final String INSERT_USERS_FRIENDS_STATUS_CONFIRMED_SQL =
            "INSERT INTO FRIENDS (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS) VALUES (?, ?, 1)";
    private static final String DELETE_FRIENDS_BY_USER_ID_SQL = "DELETE FROM FRIENDS WHERE USER_ID = ?";
    private static final String UPDATE_USER_BY_ID_SQL =
            "UPDATE USERS SET EMAIL = ?, NAME = ?, LOGIN = ?, BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String DELETE_FRIENDS_BY_USER_AND_FRIEND_ID_SQL =
            "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String GET_USER_BY_ID_SQL =
            "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String GET_FRIEND_BY_USER_ID_SQL =
            "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
    private static final String DELETE_USER_BY_ID_SQL =
            "DELETE FROM USERS WHERE USER_ID = ?";
    private static final String GET_FRIENDSHIP_STATUS_BY_USER_AND_FRIEND_ID_SQL =
            "SELECT FS.NAME AS STATUS FROM FRIENDS " +
                    "JOIN FRIENDSHIP_STATUS FS on FS.STATUS_ID = FRIENDS.FRIENDSHIP_STATUS " +
                    "WHERE USER_ID = ? AND FRIEND_ID =? ";

    public UserDbStorage(JdbcTemplate jdbcT) {
        this.jdbcT = jdbcT;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRow = jdbcT.queryForRowSet(GET_ALL_USERS_SQL);
        while (userRow.next()) {
            User user = new User(
                    userRow.getString("EMAIL"),
                    userRow.getString("LOGIN"),
                    userRow.getString("NAME"),
                    userRow.getString("BIRTHDAY")
            );
            user.setId(userRow.getInt("USER_ID"));
            if (!users.contains(user)) {
                users.add(user);
            }
            users.stream().filter(user1 -> user1.equals(user)).findAny().get().
                    addFriend(userRow.getInt("FRIEND_ID"));
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        if (LocalDate.parse(user.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .isBefore(LocalDate.now())) {
            SqlRowSet userRow = jdbcT.queryForRowSet(
                    GET_USER_IN_DB_BY_EMAIL_LOGIN_BIRTHDAY_SQL,
                    user.getEmail(), user.getLogin(), user.getBirthday()
            );
            if (!userRow.next()) {
                SqlRowSet count = jdbcT.queryForRowSet(GET_LAST_USER_ID_SQL);
                if (count.first()) {
                    userId = count.getInt("USER_ID");
                }
                log.info("Последний ID:{} ", userId);
                if (user.getId() == 0 || user.getId() < 0 || user.getId() >= userId) {
                    userId++;
                    user.setId(userId);
                }
                jdbcT.update(INSERT_USER_SQL, user.getId(), user.getEmail(), user.getName(),
                        user.getLogin(), user.getBirthday());
                for (int friendId : user.getAllFriends()) {
                    jdbcT.update(INSERT_USERS_FRIENDS_STATUS_UNCONFIRMED_SQL, user.getId(), friendId);
                }
            }
            return user;
        } else {
            log.info("Такой пользователь уже существует");
            throw new ValidationException("Такой пользователь уже существует");
        }
    }

    @Override
    public User updateUser(User user) {
        User oldUser = getUserById(user.getId());
        if (oldUser != null) {
            if (!user.getAllFriends().isEmpty()) {
                jdbcT.update(DELETE_FRIENDS_BY_USER_ID_SQL, user.getId());
            }
            jdbcT.update(UPDATE_USER_BY_ID_SQL, user.getEmail(), user.getName(), user.getLogin(),
                    user.getBirthday(), user.getId());
            for (int friendId : user.getFriendsList()) {
                User friend = getUserById(friendId);
                if (friend.getAllFriends().contains(user.getId())) {
                    jdbcT.update(DELETE_FRIENDS_BY_USER_AND_FRIEND_ID_SQL, user.getId(), friendId);
                    jdbcT.update(DELETE_FRIENDS_BY_USER_AND_FRIEND_ID_SQL, friendId, user.getId());
                    jdbcT.update(INSERT_USERS_FRIENDS_STATUS_CONFIRMED_SQL, user.getId(), friendId);
                    jdbcT.update(INSERT_USERS_FRIENDS_STATUS_CONFIRMED_SQL, friendId, user.getId());
                } else {
                    jdbcT.update(INSERT_USERS_FRIENDS_STATUS_UNCONFIRMED_SQL, user.getId(), friendId);
                }
            }
            log.info("Пользователь c Id:{} обновлен", user.getId());
            return user;
        } else {
            log.info("Пользователь c Id:{} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User getUserById(int id) {
        SqlRowSet userRow = jdbcT.queryForRowSet(GET_USER_BY_ID_SQL, id);
        if (userRow.next()) {
            User user = new User(
                    userRow.getString("EMAIL"),
                    userRow.getString("LOGIN"),
                    userRow.getString("NAME"),
                    userRow.getString("BIRTHDAY")
            );
            user.setId(userRow.getInt("USER_ID"));
            log.info("Пользователь c Id:{} найден", id);
            SqlRowSet friendRow = jdbcT.queryForRowSet(GET_FRIEND_BY_USER_ID_SQL, id);
            while (friendRow.next()) {
                user.addFriend(friendRow.getInt("FRIEND_ID"));
            }
            return user;
        } else {
            log.info("Пользователь c Id:{} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<User> getAllUserFriendsById(int id) {
        List<User> users = new ArrayList<>();
        User user = null;
        try {
            user = getUserById(id);
        } catch (NotFoundException e) {
            log.info("Пользователь с id:{} не существует", id);
        }
        if (user != null) {
            if (!user.getAllFriends().isEmpty()) {
                for (int friendId : user.getAllFriends()) {
                    try {
                        users.add(getUserById(friendId));
                    } catch (NotFoundException e) {
                        log.info("Пользователь с id:{} не существует", id);
                    }
                }
                return users;
            } else {
                return new ArrayList<>();
            }
        } else {
            throw new NotFoundException(String.format("Пользователь с id:%s не существует", id));
        }
    }

    @Override
    public String deleteUserById(int id) {
        SqlRowSet userRow = jdbcT.queryForRowSet(
                GET_USER_BY_ID_SQL, id
        );
        if (userRow.next()) {
            jdbcT.update(DELETE_FRIENDS_BY_USER_AND_FRIEND_ID_SQL, id, id);
            jdbcT.update(DELETE_USER_BY_ID_SQL, id);
            log.info("Пользователь c id:{} удален", id);
            return String.format("Пользователь c id:%s удален", id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<Integer> commonId = new ArrayList<>();
        List<User> users = new ArrayList<>();
        User user = getUserById(id);
        User friend = getUserById(otherId);
        for (int u1 : user.getAllFriends()) {
            for (int u2 : friend.getAllFriends()) {
                if (u1 == u2) {
                    commonId.add(u1);
                }
            }
        }
        for (int commonFriend : commonId) {
            user = getUserById(commonFriend);
            if (!users.contains(user)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public User addFriendToUserFriendList(int userId, int friendId) {
        User user = getUserById(userId);
        if (getUserById(friendId) != null) {
            user.addFriend(friendId);
            user = updateUser(user);
            Event.addEventIntoDataBase(userId, friendId, OperationType.ADD, EventType.FRIEND, jdbcT);
        }
        return user;
    }

    @Override
    public User deleteFriendFromUserFriendList(int userId, int friendId) {
        User user = getUserById(userId);
        if (getUserById(friendId) != null) {
            user.deleteFriend(friendId);
            user = updateUser(user);
            Event.addEventIntoDataBase(userId, friendId, OperationType.REMOVE, EventType.FRIEND, jdbcT);
        }
        return user;
    }

    public String getFriendshipStatus(int userId, int friendId) {
        SqlRowSet statusRow = jdbcT.queryForRowSet(GET_FRIENDSHIP_STATUS_BY_USER_AND_FRIEND_ID_SQL, userId, friendId);
        if (statusRow.next()) {
            return statusRow.getString("STATUS");
        }
        throw new NotFoundException("Статус не найден");
    }
}

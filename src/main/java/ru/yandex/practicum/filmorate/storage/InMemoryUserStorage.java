package ru.yandex.practicum.filmorate.storage;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exceptions.EmptyUsersFriendListException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Gson gson = new Gson();
    private final Map<Integer, User> users = new HashMap<>();
    private static int userIdCount = 0;

    @Override
    public List<User> getAllUsers() {
        if (!users.isEmpty()) {
            return new ArrayList<>(users.values());
        } else {
            throw new EmptyUsersFriendListException("База пользователей пуста");
        }
    }

    @Override
    public User createUser(User user) {
        if (approveUser(user)) {
            userIdCount++;
            user.setId(userIdCount);
            users.put(user.getId(), user);
            log.info("User created {}", gson.toJson(user));
            return user;
        } else {
            throw new ValidationException("User validation error");
        }
    }

    @Override
    public User updateUser(User user) {
        if (approveUser(user) && users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
            log.info("User updated");
            return user;
        }
        throw new NotFoundException(String.format("Пользователь с id: %s не найден", user.getId()));
    }

    @Override
    public User getUserById(int id) {
        if (!users.isEmpty() && users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException(String.format("Пользователь с id: %s не найден", id));
    }

    @Override
    public List<User> getAllUserFriendsById(int id) {
        if (users.containsKey(id)) {
            List<User> friendsList = new ArrayList<>();
            for (int friendId : users.get(id).getAllFriends()) {
                friendsList.add(users.get(friendId));
            }
            return friendsList;
        } else {
            throw new NotFoundException(String.format("Пользователь с id: %s не найден", id));
        }
    }

    @Override
    public String deleteUserById(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return String.format("Пользователь c id: %s, удален", id);
        } else {
            throw new NotFoundException(String.format("Пользователь с id: %s не найден", id));
        }
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<Integer> userFriendId = users.get(id).getAllFriends();
        List<User> commonFriends = new ArrayList<>();
        if (id > 0 || otherId > 0 || users.get(id).getAllFriends().size() != 0
                && users.get(otherId).getAllFriends().size() != 0) {
            for (int friendId : users.get(otherId).getAllFriends()) {
                if (userFriendId.contains(friendId)) {
                    commonFriends.add(users.get(friendId));
                }
            }
            return commonFriends;
        } else {
            throw new EmptyUsersFriendListException("Ошибка в id пользователей");
        }
    }

    @Override
    public User addFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        return null;
    }

    private boolean approveUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().contains("@") && !user.getEmail().isEmpty() &&
                !user.getLogin().isEmpty() && !user.getLogin().isBlank() &&
                LocalDate.parse(user.getBirthday(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now()) &&
                !user.equals(users.get(user.getId()))) {
            return true;
        } else {
            throw new ValidationException("User validation error");
        }
    }
}

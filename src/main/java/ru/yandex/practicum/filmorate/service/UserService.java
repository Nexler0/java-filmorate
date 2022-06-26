package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userDbStorage) {
        this.userStorage = userDbStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User addFriendToUserFriendList(int userId, int friendId) {
        return userStorage.addFriendToUserFriendList(userId, friendId);
    }

    public User deleteFriendFromUserFriendList(int userId, int friendId) {
        return userStorage.deleteFriendFromUserFriendList(userId, friendId);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public List<User> getAllUserFriends(int id) {
        return userStorage.getAllUserFriendsById(id);
    }

    public String deleteUser(Integer id) {
        return userStorage.deleteUserById(id);
    }
}

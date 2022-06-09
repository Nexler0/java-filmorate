package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage inMemoryUserStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public String createUser(User user) {
        return inMemoryUserStorage.createUser(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public String addFriend(int userId, int friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        friend.addFriend(userId);
        return user.addFriend(friendId);
    }

    public String deleteFriend(int userId, int friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        friend.deleteFriend(userId);
        return user.deleteFriend(friendId);
    }

    public User getUserById(int id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return inMemoryUserStorage.getCommonFriends(id, otherId);
    }

    public List<User> getAllUserFriends(int id) {
        return inMemoryUserStorage.getAllUserFriendsById(id);
    }

    public String deleteUser(Integer id) {
        return inMemoryUserStorage.deleteUserById(id);
    }
}

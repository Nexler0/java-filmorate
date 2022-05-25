package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    String createUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    List<User> getAllUserFriendsById(int id);

    String deleteUserById(int id);

    List<User> getCommonFriends(Integer id, Integer otherId);
}

package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testAddFilmAndUser() {
        User user = new User("Jon@ylta.ru", "Jonie","Jon","1999-06-15");
        userStorage.createUser(user);
        User user2 = new User("Log@ylta.ru", "LOgi","Yan","2005-06-15");
        userStorage.createUser(user2);
        userStorage.getAllUsers().forEach(System.out::println);
        assertEquals(user, userStorage.getUserById(user.getId()));
        Film film = new Film("Fake", "1999-06-15", "some things", 165, 5);
        film.setMpa(new Mpa(3));
        film.addGenre(new Genre(3));
        film.addGenre(new Genre(6));
        film.addUserLike(1);
        film.addUserLike(2);
        filmStorage.addFilm(film);
        System.out.println(filmStorage.getFilmById(1));
        assertEquals(film, filmStorage.getFilmById(1));
    }

    @Test
    public void testGetPopularFilms(){
        Film film = new Film("Fake", "1999-06-15", "some things", 165, 5);
        film.setMpa(new Mpa(2));
        film.addGenre(new Genre(6));
        film.setRate(10);
        filmStorage.addFilm(film);
        Film film2 = new Film("Fake2", "2001-06-15", "some things", 165, 5);
        film2.setMpa(new Mpa(1));
        film2.addGenre(new Genre(5));
        film2.setRate(2);
        filmStorage.addFilm(film2);
        Film film3 = new Film("Fake3", "2004-06-15", "some things", 165, 5);
        film3.setMpa(new Mpa(5));
        film3.addGenre(new Genre(2));
        film3.setRate(5);
        filmStorage.addFilm(film3);
        Film film4 = new Film("Fake4", "2006-06-15", "some things", 165, 5);
        film4.setMpa(new Mpa(3));
        film4.addGenre(new Genre(4));
        film4.setRate(8);
        filmStorage.addFilm(film4);
        Film film5 = new Film("Fake5", "2008-06-15", "some things", 165, 5);
        film5.setMpa(new Mpa(4));
        film5.addGenre(new Genre(5));
        film5.setRate(7);
        filmStorage.addFilm(film5);
        filmStorage.getPopularFilms(2).forEach(System.out::println);
        assertEquals(List.of(film, film4), filmStorage.getPopularFilms(2));
    }

    @Test
    public void testUpdateFilm(){
        Film film = new Film("Fake", "1999-06-15", "some things", 165, 5);
        film.setMpa(new Mpa(2));
        film.addGenre(new Genre(6));
        film.setRate(10);
        filmStorage.addFilm(film);
        System.out.println(filmStorage.getFilmById(1));
        Film film2 = new Film("Fake2", "2001-06-15", "some things", 165, 5);
        film2.setId(1);
        film2.setMpa(new Mpa(1));
        film2.addGenre(new Genre(5));
        film2.setRate(2);
        filmStorage.updateFilm(film2);
        System.out.println(filmStorage.getFilmById(1));
        assertEquals(film2, filmStorage.getFilmById(1));
    }

    @Test
    public void testCommonFriends(){
        User user = new User("ab@ram.ru", "ab", "", "2001-05-20");
        User user2 = new User("cd@ram.ru", "cd", "", "1995-05-20");
        User user3 = new User("eg@ram.ru", "eg", "egg", "2005-05-20");
        User user4 = new User("ht@ram.ru", "ht", "ht", "1998-05-20");

        userStorage.createUser(user);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userStorage.createUser(user4);

        userStorage.addFriendToUserFriendList(1,2);
        userStorage.addFriendToUserFriendList(1, 3);
        userStorage.addFriendToUserFriendList(2,1);
        userStorage.addFriendToUserFriendList(2,3);
        userStorage.addFriendToUserFriendList(1, 4);

        userStorage.getAllUsers().forEach(System.out::println);
        userStorage.getCommonFriends(1,2).forEach(System.out::println);
        System.out.println(userStorage.getFriendshipStatus(1, 2));
    }

    @Test
    void testGetUserFriends() { User user = new User("ab@ram.ru", "ab", "", "2001-05-20");
        User user2 = new User("cd@ram.ru", "cd", "", "1995-05-20");
        User user3 = new User("eg@ram.ru", "eg", "egg", "2005-05-20");
        User user4 = new User("ht@ram.ru", "ht", "ht", "1998-05-20");

        userStorage.createUser(user);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userStorage.createUser(user4);

        userStorage.addFriendToUserFriendList(1,2);
        userStorage.addFriendToUserFriendList(1, 3);
        userStorage.addFriendToUserFriendList(1, 4);
        userStorage.getAllUserFriendsById(1).forEach(System.out::println);
        System.out.println(userStorage.deleteFriendFromUserFriendList(1, 3));
        userStorage.getAllUserFriendsById(1).forEach(System.out::println);
    }
}
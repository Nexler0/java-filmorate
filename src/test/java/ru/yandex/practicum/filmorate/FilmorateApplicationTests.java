package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

    private final URI uri = URI.create("http://localhost:8080");
    private final Gson gson = new Gson();

    @BeforeEach
    void beforeEach() {
        FilmController filmController = new FilmController();
    }

    private void accept(HttpClient client, HttpRequest get) {
        try {
            HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException | ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void acceptEquals(User user, HttpClient client, HttpRequest get) {
        try {
            HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            assertEquals(user, gson.fromJson(response.body(), user.getClass()));
        } catch (IOException | InterruptedException | ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void acceptNull(User user, HttpClient client, HttpRequest get) {
        try {
            HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            assertNull(gson.fromJson(response.body(), user.getClass()));
        } catch (IOException | InterruptedException | ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldPostUser() {
        HttpClient client = HttpClient.newHttpClient();
        User user = new User("mail@mail.ru", "Login11", "User1", "1980-10-15");
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();
        accept(client, post);
        acceptEquals(user, client, post);
    }

    @Test
    void shouldNotPostNullUser() {
        HttpClient client = HttpClient.newHttpClient();
        User user = null;
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();
        accept(client, post);
        acceptNull(user, client, post);
    }
}

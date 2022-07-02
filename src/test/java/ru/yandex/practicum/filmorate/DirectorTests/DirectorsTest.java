package ru.yandex.practicum.filmorate.DirectorTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.director.Director;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorsTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    Director director;

    @Test
    public void DirectorResponseOKTest() throws Exception {
        director = Director.builder()
                .id(1)
                .name("Test1")
                .build();
        String body = mapper.writeValueAsString(director);
        //создание первого режизера
        mockMvc.perform(post("/directors")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        director = Director.builder()
                .id(1)
                .name("Test2")
                .build();
        String body2 = mapper.writeValueAsString(director);
        //Создание второго режизера
        mockMvc.perform(post("/directors")
                .content(body2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Получение всех
        mockMvc.perform(MockMvcRequestBuilders
                .get("/directors")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        //Получение по ID
        mockMvc.perform( MockMvcRequestBuilders
                .get("/directors/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        //Удаление 2 режисера
        mockMvc.perform( MockMvcRequestBuilders
                .delete("/directors/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }
}

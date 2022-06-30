package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql({"/recommendationsTest.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class filmorateRecomendstionsTest {
    @Autowired
    private final MockMvc mockMvc;

    @Test
    void testGetRecommendedFilms() throws Exception {
        mockMvc.perform(get("/users/{id}/recommendations", 9)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpectAll(status().isOk(),
                                MockMvcResultMatchers.jsonPath("$.size()").value(5),
                                MockMvcResultMatchers.jsonPath("$[0].id").value(1),
                                MockMvcResultMatchers.jsonPath("$[1].id").value(10),
                                MockMvcResultMatchers.jsonPath("$[2].id").value(2),
                                MockMvcResultMatchers.jsonPath("$[3].id").value(5),
                                MockMvcResultMatchers.jsonPath("$[4].id").value(6));
    }
}

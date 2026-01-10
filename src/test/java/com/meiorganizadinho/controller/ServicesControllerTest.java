package com.meiorganizadinho.controller;

import com.meiorganizadinho.service.ServicesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
public class ServicesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicesService servicesService;

    @Test
    void postShouldReturn400WhenNameIsEmpty() throws Exception {
        String jsonRequest = """
                {
                  "name": "",
                  "value": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Name is required"));

    }

    @Test
    void postShouldReturn400WhenNameDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                  "ame": "Teste Not Exists",
                  "value": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Name is required"));

    }

    @Test
    void postShouldReturn400WhenNameIsNull() throws Exception {
        String jsonRequest = """
                {
                  "name": ,
                  "value": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));

    }

    @Test
    void postShouldReturn400WhenValueIsZero() throws Exception {
        String jsonRequest = """
                {
                  "name": "Service test",
                  "value": 0,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Value must be positive or greater than 0"));

    }

    @Test
    void postShouldReturn400WhenValueIsNegative() throws Exception {
        String jsonRequest = """
                {
                  "name": "Service test",
                  "value": -30,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Value must be positive or greater than 0"));

    }

    @Test
    void postShouldReturn400WhenValueDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                  "ame": "Teste Not Exists",
                  "alue": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Value is required"));

    }

    @Test
    void postShouldReturn400WhenValueIsNull() throws Exception {
        String jsonRequest = """
                {
                  "name": "Teste da Silva,
                  "value": ,
                  "duration": 30
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));

    }

    @Test
    void postShouldReturn400WhenDurationIsZero() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration": 0
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Duration must be at least 1"));
    }
}

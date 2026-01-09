package com.meiorganizadinho.controller;

import com.meiorganizadinho.config.UserAuthenticationFilter;
import com.meiorganizadinho.dto.clientdto.ClientPostPutRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.ClientMessages;
import com.meiorganizadinho.repository.UserRepository;
import com.meiorganizadinho.service.ClientService;
import com.meiorganizadinho.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private UserAuthenticationFilter userAuthenticationFilter;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void postShouldReturn400WhenNameIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Name is required"));
    }

    @Test
    @WithMockUser
    void postShouldReturn400WhenNameIsNull() throws Exception {
        String jsonRequest = """
                {
                    "name":
                }
                """;

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    @WithMockUser
    void postShouldReturn400WhenNameDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "ame": ""
                }
                """;

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    @WithMockUser
    void postShouldReturn409WhenClientAlreadyExists() throws Exception {
        String jsonRequest = """
                {
                    "name": "Duplicated da Silva"
                }
                """;

        when(clientService.create(any(ClientPostPutRequestDTO.class)))
                .thenThrow(new ConflictException(ClientMessages.getClientWithNameAlreadyExistsMessage("Duplicated da Silva")));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.details").value("Client with name Duplicated da Silva already exists"));
    }

    @Test
    @WithMockUser
    void postShouldCreateClient() throws Exception {
        String jsonRequest = """
                {
                    "name": "Teste Unitário da silva"
                }
                """;


        ClientResponseDTO mockResponse = new ClientResponseDTO(1L, "Teste Unitário da silva");

        when(clientService.create(any(ClientPostPutRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Teste Unitário da silva"));

    }

    @Test
    @WithMockUser
    void getShouldReturnValues() throws Exception {
        List<ClientResponseDTO> mockResponse = Arrays.asList(
                new ClientResponseDTO(1L, "Teste Unitario da Silva"),
                new ClientResponseDTO(2L, "Unit test de Souza")
        );

        when(clientService.getAll())
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Teste Unitario da Silva"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Unit test de Souza"));
    }

    @Test
    @WithMockUser
    void getShouldReturnValuesWithFilter() throws Exception {
        List<ClientResponseDTO> mockResponse = Arrays.asList(
                new ClientResponseDTO(1L, "Teste Unitario da Silva"),
                new ClientResponseDTO(2L, "Unit test de Souza")
        );

        when(clientService.getByName("unit"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/clients?name=unit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Teste Unitario da Silva"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Unit test de Souza"));
    }

    @Test
    @WithMockUser
    void putShouldReturn400WhenNameIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(put("/api/v1/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Name is required"));
    }

    @Test
    @WithMockUser
    void putShouldReturn400WhenNameIsNull() throws Exception {
        String jsonRequest = """
                {
                    "name":
                }
                """;

        mockMvc.perform(put("/api/v1/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    @WithMockUser
    void putShouldReturn400WhenNameDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "ame": ""
                }
                """;

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    @WithMockUser
    void putShouldReturn404WhenClientNotFound() throws Exception {
        String jsonRequest = """
                {
                    "name": "nao existe"
                }
                """;

        long notFoundId = 89L;
        when(clientService.update(eq(notFoundId), any(ClientPostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(ClientMessages.CLIENT_NOT_FOUND));

        mockMvc.perform(put("/api/v1/clients/" + notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Client not found"));
    }

    @Test
    @WithMockUser
    void putShouldReturn409WhenClientAlreadyExists() throws Exception {
        String jsonRequest = """
                {
                    "name": "Duplicated da Silva"
                }
                """;

        long idAlreadyExists = 1L;
        when(clientService.update(eq(idAlreadyExists), any(ClientPostPutRequestDTO.class)))
                .thenThrow(new ConflictException(ClientMessages.getClientWithNameAlreadyExistsMessage("Duplicated da Silva")));

        mockMvc.perform(put("/api/v1/clients/" + idAlreadyExists)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.details").value("Client with name Duplicated da Silva already exists"));
    }

    @Test
    @WithMockUser
    void putShouldUpdateClient() throws Exception {
        String jsonRequest = """
                {
                    "name": "Teste Unitário da silva atulizado"
                }
                """;

        Long clientId = 1L;
        ClientResponseDTO mockResponse = new ClientResponseDTO(clientId, "Teste Unitário da silva atulizado");

        when(clientService.update(eq(clientId), any(ClientPostPutRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/api/v1/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.name").value("Teste Unitário da silva atulizado"));

    }

    @Test
    @WithMockUser
    void deleteShouldReturn404WhenClientNotFound() throws Exception{
        long clientNotFoundId = 1L;

        doThrow(new NotFoundException(ClientMessages.CLIENT_NOT_FOUND))
                .when(clientService)
                .delete(clientNotFoundId);

        mockMvc.perform(delete("/api/v1/clients/" + clientNotFoundId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Client not found"));
    }

    @Test
    @WithMockUser
    void deleteShouldReturn400WhenClientHasAppointments() throws Exception {
        long clientIdWithAppointments = 1L;
        int qtdAppointments = 2;
        doThrow(new BusinessException(ClientMessages.getClientHasLinkWithNAppointmentsMessage(qtdAppointments)))
                .when(clientService)
                .delete(clientIdWithAppointments);

        mockMvc.perform(delete("/api/v1/clients/" + clientIdWithAppointments))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Client has link with " + qtdAppointments + " appointment(s)"));
    }
}

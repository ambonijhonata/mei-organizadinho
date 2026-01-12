package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.servicedto.ServicePostPutRequestDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.ClientMessages;
import com.meiorganizadinho.messages.ServicesMessages;
import com.meiorganizadinho.service.ServicesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

    @Test
    void postShouldReturn400WhenDurationIsNegative() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration": -60
                }
                """;

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Duration must be at least 1"));
    }

    @Test
    void postShouldReturn400WhenDurationDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "uration": -60
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
    void postShouldReturn400WhenDurationIsNull() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration":
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
    void postShouldReturn409WhenClientAlreadyExists() throws Exception {
        String duplicatedServiceName = "Duplicated Service";

        String jsonRequest = """
                {
                    "name": "%s",
                    "value": 50.5,
                    "duration": 10
                }
                """.formatted(duplicatedServiceName);

        when(servicesService.create(any(ServicePostPutRequestDTO.class)))
                .thenThrow(new ConflictException(ServicesMessages.getServiceAlreadyExistsMessage(duplicatedServiceName)));

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.details").value("Service with name %s already exists".formatted(duplicatedServiceName)));
    }

    @Test
    void postShouldCreateService() throws Exception {
        Services service = new Services(1L, "Service to create", 50.5, 10);

        String jsonRequest = String.format(Locale.US, """
                {
                    "name": "%s",
                    "value": %.2f,
                    "duration": %d                
                }
                """, service.getName(), service.getValue(), service.getDuration());

        ServiceResponseDTO mockResponse = new ServiceResponseDTO(service.getId(), service.getName(), service.getValue(), service.getDuration());

        when(servicesService.create(any(ServicePostPutRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(service.getId()))
                .andExpect(jsonPath("$.name").value(service.getName()))
                .andExpect(jsonPath("$.value").value(service.getValue()))
                .andExpect(jsonPath("$.duration").value(service.getDuration()));
    }

    @Test
    void getShouldReturnValues() throws Exception {
        List<ServiceResponseDTO> mockResponse = Arrays.asList(
                new ServiceResponseDTO(1L, "Service 1", 31.78, 8),
                new ServiceResponseDTO(2L, "Service 2", 78.87, 7)
        );

        when(servicesService.getAll())
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Service 1"))
                .andExpect(jsonPath("$[0].value").value(31.78))
                .andExpect(jsonPath("$[0].duration").value(8))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Service 2"))
                .andExpect(jsonPath("$[1].value").value(78.87))
                .andExpect(jsonPath("$[1].duration").value(7));
    }

    @Test
    void getShouldReturnValuesWithFilter() throws Exception {
        List<ServiceResponseDTO> mockResponse = Arrays.asList(
                new ServiceResponseDTO(2L, "Service 2", 78.87, 7)
        );

        when(servicesService.getByName("Service 2"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/services?name=Service 2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Service 2"))
                .andExpect(jsonPath("$[0].value").value(78.87))
                .andExpect(jsonPath("$[0].duration").value(7));
    }

    @Test
    void putShouldReturn400WhenNameIsEmpty() throws Exception {
        String jsonRequest = """
                {
                  "name": "",
                  "value": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Name is required"));
    }

    @Test
    void putShouldReturn400WhenNameDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                  "ame": "Teste Not Exists",
                  "value": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Name is required"));

    }

    @Test
    void putShouldReturn400WhenNameIsNull() throws Exception {
        String jsonRequest = """
                {
                  "name": ,
                  "value": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));

    }

    @Test
    void putShouldReturn400WhenValueIsZero() throws Exception {
        String jsonRequest = """
                {
                  "name": "Service test",
                  "value": 0,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Value must be positive or greater than 0"));

    }

    @Test
    void putShouldReturn400WhenValueIsNegative() throws Exception {
        String jsonRequest = """
                {
                  "name": "Service test",
                  "value": -30,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Value must be positive or greater than 0"));

    }

    @Test
    void putShouldReturn400WhenValueDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                  "ame": "Teste Not Exists",
                  "alue": 50.50,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Value is required"));

    }

    @Test
    void putShouldReturn400WhenValueIsNull() throws Exception {
        String jsonRequest = """
                {
                  "name": "Teste da Silva,
                  "value": ,
                  "duration": 30
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));

    }

    @Test
    void putShouldReturn400WhenDurationIsZero() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration": 0
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Duration must be at least 1"));
    }

    @Test
    void putShouldReturn400WhenDurationIsNegative() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration": -60
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Duration must be at least 1"));
    }

    @Test
    void putShouldReturn400WhenDurationDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "uration": -60
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenDurationIsNull() throws Exception {
        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration":
                }
                """;

        mockMvc.perform(put("/api/v1/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn404WhenServiceNotFound() throws Exception {
        long notFoundId = 89L;

        when(servicesService.update(eq(notFoundId), any(ServicePostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(ServicesMessages.getServiceNotFoundMessage(notFoundId)));

        String jsonRequest = """
                {
                  "name": "teste da Silva",
                  "value": 50.50,
                  "duration": 10
                }
                """;

        mockMvc.perform(put("/api/v1/services/" + notFoundId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value(ServicesMessages.getServiceNotFoundMessage(notFoundId)));
    }

    @Test
    void putShouldReturn409WhenServiceAlreadyExists() throws Exception {
        long alreadyExistsId = 89L;
        String alreadyExistsName = "Already exists";

        when(servicesService.update(eq(alreadyExistsId), any(ServicePostPutRequestDTO.class)))
                .thenThrow(new ConflictException(ServicesMessages.getServiceAlreadyExistsMessage(alreadyExistsName)));


        String jsonRequest = String.format("""
                {
                  "name": "%s",
                  "value": 50.50,
                  "duration": 10
                }
                """, alreadyExistsName);

        mockMvc.perform(put("/api/v1/services/" + alreadyExistsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.details").value(ServicesMessages.getServiceAlreadyExistsMessage(alreadyExistsName)));
    }

    @Test
    void putShouldUpdateService() throws Exception {
        long idToUpdate = 1L;
        ServicePostPutRequestDTO serviceToUpdate = new ServicePostPutRequestDTO("Service to update", 48.78, 96);
        ServiceResponseDTO mockResponse = new ServiceResponseDTO(idToUpdate, serviceToUpdate.name(), serviceToUpdate.value(), serviceToUpdate.duration());

        when(servicesService.update(eq(idToUpdate), any(ServicePostPutRequestDTO.class)))
                .thenReturn(mockResponse);

        String jsonRequest = String.format("""
                {
                  "name": "%s",
                  "value": 50.50,
                  "duration": 10
                }
                
                """, serviceToUpdate.name());

        mockMvc.perform(put("/api/v1/services/" + idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(serviceToUpdate.name()));
    }

    @Test
    void deleteShouldReturn404WhenServicesNotFound() throws Exception{
        long serviceNotFoundId = 1L;

        doThrow(new NotFoundException(ServicesMessages.getServiceNotFoundMessage(serviceNotFoundId)))
                .when(servicesService)
                .delete(serviceNotFoundId);

        mockMvc.perform(delete("/api/v1/services/" + serviceNotFoundId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value(ServicesMessages.getServiceNotFoundMessage(serviceNotFoundId)));
    }

    @Test
    void deleteShouldReturn400WhenServicesHasAppointments() throws Exception{
        long serviceWithAppointmentId = 1L;

        int qtdAppointments = 2;
        doThrow(new BusinessException(ClientMessages.getClientHasLinkWithNAppointmentsMessage(qtdAppointments)))
                .when(servicesService)
                .delete(serviceWithAppointmentId);

        mockMvc.perform(delete("/api/v1/services/" + serviceWithAppointmentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value(ClientMessages.getClientHasLinkWithNAppointmentsMessage(qtdAppointments)));
    }
}

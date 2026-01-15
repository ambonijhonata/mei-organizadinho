package com.meiorganizadinho.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.AppointmentMessages;
import com.meiorganizadinho.messages.ClientMessages;
import com.meiorganizadinho.messages.ServicesMessages;
import com.meiorganizadinho.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void postShouldReturn400WhenClientIdIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": ,
                    "servicesId": [15],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void postShouldReturn400WhenClientIdDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "lientId": ,
                    "servicesId": [15],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void postShouldReturn400WhenServicesIdIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("At least one service must be specified"));
    }

    @Test
    void postShouldReturn400WhenServicesIdIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": ,
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void postShouldReturn400WhenServicesIdDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesI": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("servicesId is required"));
    }

    @Test
    void postShouldReturn400WhenDateIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("date is required"));
    }

    @Test
    void postShouldReturn400WhenDateIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": ,
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void postShouldReturn400WhenDateDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "ate": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("date is required"));
    }

    @Test
    void postShouldReturn400WhenStartTimeIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("startTime is required"));
    }

    @Test
    void postShouldReturn400WhenStartTimeIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": ,
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void postShouldReturn400WhenStartTimeDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "tartTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("startTime is required"));
    }

    @Test
    void postShouldReturn400WhenEndTimeIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": ""
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endTime is required"));
    }

    @Test
    void postShouldReturn400WhenEndTimeIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": 
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void postShouldReturn400WhenEndTimeDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "ndTime": "16:30:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endTime is required"));
    }

    @Test
    void postShouldReturn400WhenStartTimeIsAfterEndTime() throws Exception {
        when(appointmentService.create(any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new BusinessException(AppointmentMessages.START_TIME_CANNOT_BE_AFTER_END_TIME));

        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "16:00:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Start time cannot be after end time"));
    }

    @Test
    void postShouldReturn409WhenAreConflicts() throws Exception {
        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(
                2L,
                List.of(10L),
                LocalDate.of(2025, 12, 30),
                LocalTime.of(14, 30, 0),
                LocalTime.of(15, 0, 0)
        );

        when(appointmentService.create(any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new ConflictException(AppointmentMessages.getConflictingAppointmentsMessage(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime())));

        String servicesIdJson = appointmentRequest.servicesId().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String jsonRequest = """
                {
                    "clientId": %d,
                    "servicesId": [%s],
                    "date": "%s",
                    "startTime": "%s",
                    "endTime": "%s"
                }
                """.formatted(
                appointmentRequest.clientId(),
                servicesIdJson,
                appointmentRequest.date(),
                appointmentRequest.startTime(),
                appointmentRequest.endTime()
        );

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.details").value("Conflicting appointments in date 2025-12-30 between 14:30 and 15:00 are found"));
    }

    @Test
    void postShouldReturn404WhenClientNotFound() throws Exception {
        when(appointmentService.create(any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(ClientMessages.CLIENT_NOT_FOUND));

        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "17:00:00"
                  }
                """;

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Client not found"));
    }

    @Test
    void postShouldReturn404WhenServiceNotFound() throws Exception {
        Long serviceId = 2L;
        when(appointmentService.create(any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(ServicesMessages.getServiceNotFoundMessage(serviceId)));

        String jsonRequest = """
                {
                    "clientId": %d,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "17:00:00"
                  }
                """.formatted(serviceId);

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Service 2 not found"));
    }

    @Test
    void postShouldReturn201WhenAppointmentIsCreated() throws Exception {
        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(2L, List.of(10L, 15L), LocalDate.of(2025, 12, 30), LocalTime.of(16, 0), LocalTime.of(16, 45));

        List<ServiceResponseDTO> servicesMockResponse = List.of(
                new ServiceResponseDTO(10L, "Corte de Cabelo", 50.0, 30),
                new ServiceResponseDTO(15L, "Barba", 30.0, 15)
        );
        AppointmentResponseDTO mockResponse = new AppointmentResponseDTO(1L, new ClientResponseDTO(2L, "Teste de Souza"), servicesMockResponse, appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());

        when(appointmentService.create(any(AppointmentPostPutRequestDTO.class)))
                .thenReturn(mockResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonRequest = objectMapper.writeValueAsString(appointmentRequest);

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.client.id").value(2))
                .andExpect(jsonPath("$.client.name").value("Teste de Souza"))
                .andExpect(jsonPath("$.services[0].id").value(10))
                .andExpect(jsonPath("$.services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$.services[0].value").value(50.0))
                .andExpect(jsonPath("$.services[0].duration").value(30))
                .andExpect(jsonPath("$.services[1].id").value(15))
                .andExpect(jsonPath("$.services[1].name").value("Barba"))
                .andExpect(jsonPath("$.services[1].value").value(30.0))
                .andExpect(jsonPath("$.services[1].duration").value(15))
                .andExpect(jsonPath("$.date").value("2025-12-30"))
                .andExpect(jsonPath("$.startTime").value("16:00:00"))
                .andExpect(jsonPath("$.endTime").value("16:45:00"));
    }

    @Test
    void getShouldReturnValuesWithFilterByDateAndStartTimeGreaterThan() throws Exception{
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO(1L, "Cliente Teste");
        List<ServiceResponseDTO> servicesMockResponse = List.of(
                new ServiceResponseDTO(2L, "Corte de Cabelo", 50.0, 30),
                new ServiceResponseDTO(3L, "Barba", 30.0, 15)
        );

        List<AppointmentResponseDTO> appointmentsMockResponse = List.of(
                new AppointmentResponseDTO(1L, clientResponseDTO, servicesMockResponse, LocalDate.of(2025, 12, 30), LocalTime.of(15, 0), LocalTime.of(15, 45)),
                new AppointmentResponseDTO(2L, clientResponseDTO, servicesMockResponse, LocalDate.of(2025, 12, 30), LocalTime.of(16, 0), LocalTime.of(16, 45))
        );

        when(appointmentService.getByDateAndStartTimeGreaterThan(LocalDate.of(2025,12,30), LocalTime.of(15,0)))
                .thenReturn(appointmentsMockResponse);

        mockMvc.perform(get("/api/v1/appointments?date=2025-12-30&startTime=15:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].client.id").value(1))
                .andExpect(jsonPath("$[0].client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$[0].services[0].id").value(2))
                .andExpect(jsonPath("$[0].services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$[0].services[0].value").value(50.0))
                .andExpect(jsonPath("$[0].services[0].duration").value(30))
                .andExpect(jsonPath("$[0].services[1].id").value(3))
                .andExpect(jsonPath("$[0].services[1].name").value("Barba"))
                .andExpect(jsonPath("$[0].services[1].value").value(30.0))
                .andExpect(jsonPath("$[0].services[1].duration").value(15))
                .andExpect(jsonPath("$[0].date").value("2025-12-30"))
                .andExpect(jsonPath("$[0].startTime").value("15:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("15:45:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].client.id").value(1))
                .andExpect(jsonPath("$[1].client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$[1].services[0].id").value(2))
                .andExpect(jsonPath("$[1].services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$[1].services[0].value").value(50.0))
                .andExpect(jsonPath("$[1].services[0].duration").value(30))
                .andExpect(jsonPath("$[1].services[1].id").value(3))
                .andExpect(jsonPath("$[1].services[1].name").value("Barba"))
                .andExpect(jsonPath("$[1].services[1].value").value(30.0))
                .andExpect(jsonPath("$[1].services[1].duration").value(15))
                .andExpect(jsonPath("$[1].date").value("2025-12-30"))
                .andExpect(jsonPath("$[1].startTime").value("16:00:00"))
                .andExpect(jsonPath("$[1].endTime").value("16:45:00"));
    }

    @Test
    void getShouldReturnValuesWithFilterByDate() throws Exception{
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO(1L, "Cliente Teste");
        List<ServiceResponseDTO> servicesMockResponse = List.of(
                new ServiceResponseDTO(2L, "Corte de Cabelo", 50.0, 30),
                new ServiceResponseDTO(3L, "Barba", 30.0, 15)
        );

        List<AppointmentResponseDTO> appointmentsMockResponse = List.of(
                new AppointmentResponseDTO(1L, clientResponseDTO, servicesMockResponse, LocalDate.of(2025, 12, 30), LocalTime.of(15, 0), LocalTime.of(15, 45)),
                new AppointmentResponseDTO(2L, clientResponseDTO, servicesMockResponse, LocalDate.of(2025, 12, 30), LocalTime.of(16, 0), LocalTime.of(16, 45))
        );

        when(appointmentService.getByDate(LocalDate.of(2025,12,30)))
                .thenReturn(appointmentsMockResponse);

        mockMvc.perform(get("/api/v1/appointments?date=2025-12-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].client.id").value(1))
                .andExpect(jsonPath("$[0].client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$[0].services[0].id").value(2))
                .andExpect(jsonPath("$[0].services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$[0].services[0].value").value(50.0))
                .andExpect(jsonPath("$[0].services[0].duration").value(30))
                .andExpect(jsonPath("$[0].services[1].id").value(3))
                .andExpect(jsonPath("$[0].services[1].name").value("Barba"))
                .andExpect(jsonPath("$[0].services[1].value").value(30.0))
                .andExpect(jsonPath("$[0].services[1].duration").value(15))
                .andExpect(jsonPath("$[0].date").value("2025-12-30"))
                .andExpect(jsonPath("$[0].startTime").value("15:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("15:45:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].client.id").value(1))
                .andExpect(jsonPath("$[1].client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$[1].services[0].id").value(2))
                .andExpect(jsonPath("$[1].services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$[1].services[0].value").value(50.0))
                .andExpect(jsonPath("$[1].services[0].duration").value(30))
                .andExpect(jsonPath("$[1].services[1].id").value(3))
                .andExpect(jsonPath("$[1].services[1].name").value("Barba"))
                .andExpect(jsonPath("$[1].services[1].value").value(30.0))
                .andExpect(jsonPath("$[1].services[1].duration").value(15))
                .andExpect(jsonPath("$[1].date").value("2025-12-30"))
                .andExpect(jsonPath("$[1].startTime").value("16:00:00"))
                .andExpect(jsonPath("$[1].endTime").value("16:45:00"));
    }

    @Test
    void getShouldReturnValues() throws Exception{
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO(1L, "Cliente Teste");
        List<ServiceResponseDTO> servicesMockResponse = List.of(
                new ServiceResponseDTO(2L, "Corte de Cabelo", 50.0, 30),
                new ServiceResponseDTO(3L, "Barba", 30.0, 15)
        );

        List<AppointmentResponseDTO> appointmentsMockResponse = List.of(
                new AppointmentResponseDTO(1L, clientResponseDTO, servicesMockResponse, LocalDate.of(2025, 12, 30), LocalTime.of(15, 0), LocalTime.of(15, 45)),
                new AppointmentResponseDTO(2L, clientResponseDTO, servicesMockResponse, LocalDate.of(2025, 12, 30), LocalTime.of(16, 0), LocalTime.of(16, 45))
        );

        when(appointmentService.get())
                .thenReturn(appointmentsMockResponse);

        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].client.id").value(1))
                .andExpect(jsonPath("$[0].client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$[0].services[0].id").value(2))
                .andExpect(jsonPath("$[0].services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$[0].services[0].value").value(50.0))
                .andExpect(jsonPath("$[0].services[0].duration").value(30))
                .andExpect(jsonPath("$[0].services[1].id").value(3))
                .andExpect(jsonPath("$[0].services[1].name").value("Barba"))
                .andExpect(jsonPath("$[0].services[1].value").value(30.0))
                .andExpect(jsonPath("$[0].services[1].duration").value(15))
                .andExpect(jsonPath("$[0].date").value("2025-12-30"))
                .andExpect(jsonPath("$[0].startTime").value("15:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("15:45:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].client.id").value(1))
                .andExpect(jsonPath("$[1].client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$[1].services[0].id").value(2))
                .andExpect(jsonPath("$[1].services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$[1].services[0].value").value(50.0))
                .andExpect(jsonPath("$[1].services[0].duration").value(30))
                .andExpect(jsonPath("$[1].services[1].id").value(3))
                .andExpect(jsonPath("$[1].services[1].name").value("Barba"))
                .andExpect(jsonPath("$[1].services[1].value").value(30.0))
                .andExpect(jsonPath("$[1].services[1].duration").value(15))
                .andExpect(jsonPath("$[1].date").value("2025-12-30"))
                .andExpect(jsonPath("$[1].startTime").value("16:00:00"))
                .andExpect(jsonPath("$[1].endTime").value("16:45:00"));
    }

    @Test
    void putShouldReturn400WhenClientIdIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": ,
                    "servicesId": [15],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenClientIdDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "lientId": ,
                    "servicesId": [15],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenServicesIdIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("At least one service must be specified"));
    }

    @Test
    void putShouldReturn400WhenServicesIdIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": ,
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenServicesIdDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesI": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("servicesId is required"));
    }

    @Test
    void putShouldReturn400WhenDateIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("date is required"));
    }

    @Test
    void putShouldReturn400WhenDateIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": ,
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenDateDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "ate": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("date is required"));
    }

    @Test
    void putShouldReturn400WhenStartTimeIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("startTime is required"));
    }

    @Test
    void putShouldReturn400WhenStartTimeIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": ,
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenStartTimeDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "tartTime": "16:00:00",
                    "endTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("startTime is required"));
    }

    @Test
    void putShouldReturn400WhenEndTimeIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": ""
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endTime is required"));
    }

    @Test
    void putShouldReturn400WhenEndTimeIsNull() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "endTime": 
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));
    }

    @Test
    void putShouldReturn400WhenEndTimeDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:00:00",
                    "ndTime": "16:30:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endTime is required"));
    }

    @Test
    void putShouldReturn400WhenStartTimeIsAfterEndTime() throws Exception {
        when(appointmentService.update(eq(1L), any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new BusinessException(AppointmentMessages.START_TIME_CANNOT_BE_AFTER_END_TIME));

        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "16:00:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Start time cannot be after end time"));
    }

    @Test
    void putShouldReturn409WhenAreConflicts() throws Exception {
        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(
                2L,
                List.of(10L),
                LocalDate.of(2025, 12, 30),
                LocalTime.of(14, 30, 0),
                LocalTime.of(15, 0, 0)
        );

        when(appointmentService.update(eq(1L), any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new ConflictException(AppointmentMessages.getConflictingAppointmentsMessage(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime())));

        String servicesIdJson = appointmentRequest.servicesId().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String jsonRequest = """
                {
                    "clientId": %d,
                    "servicesId": [%s],
                    "date": "%s",
                    "startTime": "%s",
                    "endTime": "%s"
                }
                """.formatted(
                appointmentRequest.clientId(),
                servicesIdJson,
                appointmentRequest.date(),
                appointmentRequest.startTime(),
                appointmentRequest.endTime()
        );

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.details").value("Conflicting appointments in date 2025-12-30 between 14:30 and 15:00 are found"));
    }

    @Test
    void putShouldReturn404WhenClientNotFound() throws Exception {
        when(appointmentService.update(eq(1L), any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(ClientMessages.CLIENT_NOT_FOUND));

        String jsonRequest = """
                {
                    "clientId": 2,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "17:00:00"
                  }
                """;

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Client not found"));
    }

    @Test
    void putShouldReturn404WhenServiceNotFound() throws Exception {
        Long serviceId = 2L;
        when(appointmentService.update(eq(1L), any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(ServicesMessages.getServiceNotFoundMessage(serviceId)));

        String jsonRequest = """
                {
                    "clientId": %d,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "17:00:00"
                  }
                """.formatted(serviceId);

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Service 2 not found"));
    }

    @Test
    void putShouldReturn404WhenAppointmentNotFound() throws Exception {
        Long serviceId = 2L;
        when(appointmentService.update(eq(1L), any(AppointmentPostPutRequestDTO.class)))
                .thenThrow(new NotFoundException(AppointmentMessages.APPOINTMENT_NOT_FOUND));

        String jsonRequest = """
                {
                    "clientId": %d,
                    "servicesId": [10],
                    "date": "2025-12-30",
                    "startTime": "16:30:00",
                    "endTime": "17:00:00"
                  }
                """.formatted(serviceId);

        mockMvc.perform(put("/api/v1/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Appointment not found"));
    }

    @Test
    void putShouldUpdate() throws Exception {
        AppointmentPostPutRequestDTO appointmentRequestDTO = new AppointmentPostPutRequestDTO(
                2L,
                List.of(3L, 1L),
                LocalDate.parse("2025-11-20"),
                LocalTime.parse("17:00:00"),
                LocalTime.parse("17:30:00")
        );

       ClientResponseDTO clientMockResponse = new ClientResponseDTO(2L, "Cliente Teste");
       List<ServiceResponseDTO> servicesMockResponse = List.of(
              new ServiceResponseDTO(3L, "Corte de Cabelo", 50.0, 30),
              new ServiceResponseDTO(1L, "Barba", 30.0, 15)
       );

       AppointmentResponseDTO appointmentMockResponse = new AppointmentResponseDTO(
               1L,
               clientMockResponse,
               servicesMockResponse,
               appointmentRequestDTO.date(),
               appointmentRequestDTO.startTime(),
               appointmentRequestDTO.endTime()
         );
        when(appointmentService.update(eq(1L), any(AppointmentPostPutRequestDTO.class)))
                .thenReturn(appointmentMockResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonRequest = objectMapper.writeValueAsString(appointmentRequestDTO);

        mockMvc.perform(put("/api/v1/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.client.id").value(2))
                .andExpect(jsonPath("$.client.name").value("Cliente Teste"))
                .andExpect(jsonPath("$.services[0].id").value(3))
                .andExpect(jsonPath("$.services[0].name").value("Corte de Cabelo"))
                .andExpect(jsonPath("$.services[0].value").value(50.0))
                .andExpect(jsonPath("$.services[0].duration").value(30))
                .andExpect(jsonPath("$.services[1].id").value(1))
                .andExpect(jsonPath("$.services[1].name").value("Barba"))
                .andExpect(jsonPath("$.services[1].value").value(30.0))
                .andExpect(jsonPath("$.services[1].duration").value(15))
                .andExpect(jsonPath("$.date").value("2025-11-20"))
                .andExpect(jsonPath("$.startTime").value("17:00:00"))
                .andExpect(jsonPath("$.endTime").value("17:30:00"));


    }

    @Test
    void deleteShouldReturn404WhenAppointmentNotFound() throws Exception {
        Long appointmentId = 1L;
        doThrow(new NotFoundException(AppointmentMessages.APPOINTMENT_NOT_FOUND))
                .when(appointmentService)
                .delete(appointmentId);
        String endpoint = "/api/v1/appointments/" + appointmentId;
        mockMvc.perform(delete("/api/v1/appointments/{id}", appointmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.details").value("Appointment not found"));
    }

    @Test
    void deleteShouldDeleteAppointment() throws Exception {
        Long appointmentId = 1L;
        mockMvc.perform(delete("/api/v1/appointments/{id}", appointmentId))
                .andExpect(status().isNoContent());
    }
}

package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void postShouldReturn400WhenAreConflicts() throws Exception {
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
}

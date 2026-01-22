package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.repository.AppointmentRepository;
import com.meiorganizadinho.repository.ClientRepository;
import com.meiorganizadinho.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createShouldCreateAppointment() {
        Client client = new Client(1L, "Rampage Jackson");
        Services service1 = new Services(1L, "Corte de barba", 25.99, 30);
        Services service2 = new Services(2L, "Corte de cabelo", 55.99, 30);
        List<Services> services = List.of(service1, service2);

        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(
                client.getId(),
                List.of(services.get(0).getId(), services.get(1).getId()),
                date,
                startTime,
                endTime
        );

        Appointment savedAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime())).thenReturn(List.of());
        when(clientRepository.findById(appointmentRequest.clientId())).thenReturn(Optional.of(client));
        when(serviceRepository.findById(appointmentRequest.servicesId().get(0))).thenReturn(Optional.ofNullable(services.get(0)));
        when(serviceRepository.findById(appointmentRequest.servicesId().get(1))).thenReturn(Optional.ofNullable(services.get(0)));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponseDTO response = appointmentService.create(appointmentRequest);

        assertNotNull(response);
        assertEquals(savedAppointment.getId(), response.id());
        assertEquals(savedAppointment.getDate(), response.date());
        assertEquals(savedAppointment.getStartTime(), response.startTime());
        assertEquals(savedAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(client.getId(), response.client().id());
        assertEquals(client.getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        ServiceResponseDTO responseService1 = response.services().get(0);
        assertEquals(service1.getId(), responseService1.id());
        assertEquals(service1.getName(), responseService1.name());
        assertEquals(service1.getValue(), responseService1.value(), 0.001);
        assertEquals(service1.getDuration(), responseService1.duration());

        ServiceResponseDTO responseService2 = response.services().get(1);
        assertEquals(service2.getId(), responseService2.id());
        assertEquals(service2.getName(), responseService2.name());
        assertEquals(service2.getValue(), responseService2.value(), 0.001);
        assertEquals(service2.getDuration(), responseService2.duration());

        verify(appointmentRepository).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository).findById(appointmentRequest.clientId());
        verify(serviceRepository, times(2)).findById(any(Long.class));
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createShouldThrowBusinessExceptionWhenStartTimeIsAfterEndTime() {
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(18, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(
                client.getId(),
                List.of(services.get(0).getId(), services.get(1).getId()),
                date,
                startTime,
                endTime
        );

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            appointmentService.create(appointmentRequest);
        });

        verify(appointmentRepository, never()).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository, never()).findById(appointmentRequest.clientId());
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));

        String expectedMessage = "Start time cannot be after end time";
        assertEquals(expectedMessage, businessException.getMessage());

    }

}

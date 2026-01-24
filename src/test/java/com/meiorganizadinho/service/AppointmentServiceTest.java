package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.repository.AppointmentRepository;
import com.meiorganizadinho.repository.ClientRepository;
import com.meiorganizadinho.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

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

    @Test
    void createShouldThrowConflictExceptionWhenThereIsAConflict() {
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

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

        List<Appointment> conflictingAppointments = List.of(
                new Appointment(2L, client, services, date, startTime, endTime)
        );

        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(conflictingAppointments);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            appointmentService.create(appointmentRequest);
        });

        verify(appointmentRepository).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository, never()).findById(any(Long.class));
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));

        String expectedMessage = "Conflicting appointments in date 2025-11-20 between 17:00 and 17:30 are found";
        assertEquals(expectedMessage, conflictException.getMessage());
    }

    @Test
    void createShouldThrownNotFoundExceptionWhenClientDoesNotExist() {
        Long nonExistentClientId = 999L;
        List<Long> servicesId = List.of(1L, 2L);
        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(
                nonExistentClientId,
                servicesId,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(List.of());
        when(clientRepository.findById(nonExistentClientId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            appointmentService.create(appointmentRequest);
        });

        verify(appointmentRepository).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository).findById(nonExistentClientId);
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));

        String expectedMessage = "Client not found";
        assertEquals(expectedMessage, notFoundException.getMessage());
    }

    @Test
    void createShouldThrownNotFoundExceptionWhenServiceDoesNotExist() {
        Client client = new Client(1L, "Rampage Jackson");
        Long nonExistentServiceId = 999L;
        List<Long> servicesId = List.of(nonExistentServiceId);
        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        AppointmentPostPutRequestDTO appointmentRequest = new AppointmentPostPutRequestDTO(
                client.getId(),
                servicesId,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(List.of());
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(serviceRepository.findById(nonExistentServiceId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            appointmentService.create(appointmentRequest);
        });

        verify(appointmentRepository).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository).findById(client.getId());
        verify(serviceRepository).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
        String expectedMessage = "Service 999 not found";
        assertEquals(expectedMessage, notFoundException.getMessage());
    }

    @Test
    void getShouldReturnEmptyListWhenNoAppointmentsExist() {
        Sort sort = Sort.by(
                Sort.Order.asc("date"),
                Sort.Order.asc("startTime")
        );
        when(appointmentRepository.findAll(sort)).thenReturn(List.of());

        List<AppointmentResponseDTO> appointments = appointmentService.get();

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());

        verify(appointmentRepository).findAll(sort);
    }

    @Test
    void getShouldReturnListOfAppointments() {
        Client client = new Client(1L, "Rampage Jackson");
        Services service1 = new Services(1L, "Corte de barba", 25.99, 30);
        Services service2 = new Services(2L, "Corte de cabelo", 55.99, 30);
        List<Services> services = List.of(service1, service2);

        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        Appointment firstAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        Appointment secondAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        Sort sort = Sort.by(
                Sort.Order.asc("date"),
                Sort.Order.asc("startTime")
        );
        when(appointmentRepository.findAll(sort)).thenReturn(List.of(firstAppointment, secondAppointment));

        List<AppointmentResponseDTO> appointmentsResponse = appointmentService.get();

        assertNotNull(appointmentsResponse);
        assertEquals(2, appointmentsResponse.size());

        //FIRST APPOINTMENT
        AppointmentResponseDTO response = appointmentsResponse.getFirst();
        assertEquals(firstAppointment.getId(), response.id());
        assertEquals(firstAppointment.getDate(), response.date());
        assertEquals(firstAppointment.getStartTime(), response.startTime());
        assertEquals(firstAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(firstAppointment.getClient().getId(), response.client().id());
        assertEquals(firstAppointment.getClient().getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        //first service
        ServiceResponseDTO responseService = response.services().getFirst();
        Services service = firstAppointment.getServices().getFirst();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //second service
        responseService = response.services().get(1);
        service = firstAppointment.getServices().getLast();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //Second appointment
        response = appointmentsResponse.getLast();
        assertEquals(secondAppointment.getId(), response.id());
        assertEquals(secondAppointment.getDate(), response.date());
        assertEquals(secondAppointment.getStartTime(), response.startTime());
        assertEquals(secondAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(secondAppointment.getClient().getId(), response.client().id());
        assertEquals(secondAppointment.getClient().getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        //first service
        responseService = response.services().getFirst();
        service = secondAppointment.getServices().getFirst();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //second service
        responseService = response.services().get(1);
        service = secondAppointment.getServices().getLast();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        verify(appointmentRepository).findAll(sort);
    }

    @Test
    void getByDateShouldReturnEmptyListWhenNoAppointmentsExist() {
        LocalDate date = LocalDate.of(2025, 11, 20);
        when(appointmentRepository.findByDateOrderByDateAscStartTimeAsc(date)).thenReturn(List.of());

        List<AppointmentResponseDTO> appointments = appointmentService.getByDate(date);

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());

        verify(appointmentRepository).findByDateOrderByDateAscStartTimeAsc(date);
    }

    @Test
    void getByDateShouldReturnListOfAppointments() {
        Client client = new Client(1L, "Rampage Jackson");
        Services service1 = new Services(1L, "Corte de barba", 25.99, 30);
        Services service2 = new Services(2L, "Corte de cabelo", 55.99, 30);
        List<Services> services = List.of(service1, service2);

        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        Appointment firstAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        Appointment secondAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findByDateOrderByDateAscStartTimeAsc(date)).thenReturn(List.of(firstAppointment, secondAppointment));

        List<AppointmentResponseDTO> appointmentsResponse = appointmentService.getByDate(date);

        assertNotNull(appointmentsResponse);
        assertEquals(2, appointmentsResponse.size());

        //FIRST APPOINTMENT
        AppointmentResponseDTO response = appointmentsResponse.getFirst();
        assertEquals(firstAppointment.getId(), response.id());
        assertEquals(firstAppointment.getDate(), response.date());
        assertEquals(firstAppointment.getStartTime(), response.startTime());
        assertEquals(firstAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(firstAppointment.getClient().getId(), response.client().id());
        assertEquals(firstAppointment.getClient().getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        //first service
        ServiceResponseDTO responseService = response.services().getFirst();
        Services service = firstAppointment.getServices().getFirst();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //second service
        responseService = response.services().get(1);
        service = firstAppointment.getServices().getLast();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //Second appointment
        response = appointmentsResponse.getLast();
        assertEquals(secondAppointment.getId(), response.id());
        assertEquals(secondAppointment.getDate(), response.date());
        assertEquals(secondAppointment.getStartTime(), response.startTime());
        assertEquals(secondAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(secondAppointment.getClient().getId(), response.client().id());
        assertEquals(secondAppointment.getClient().getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        //first service
        responseService = response.services().getFirst();
        service = secondAppointment.getServices().getFirst();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //second service
        responseService = response.services().get(1);
        service = secondAppointment.getServices().getLast();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        verify(appointmentRepository).findByDateOrderByDateAscStartTimeAsc(date);
    }

    @Test
    void getByDateAndStartTimeGreaterThanShouldReturnEmptyListWhenNoAppointmentsExist() {
        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        when(appointmentRepository.findByDateAndStartTimeGreaterThanEqualOrderByDateAscStartTimeAsc(date, startTime)).thenReturn(List.of());

        List<AppointmentResponseDTO> appointments = appointmentService.getByDateAndStartTimeGreaterThan(date, startTime);

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());

        verify(appointmentRepository).findByDateAndStartTimeGreaterThanEqualOrderByDateAscStartTimeAsc(date, startTime);
    }

    @Test
    void getByDateAndStartTimeGreaterThanShouldReturnListOfAppointments() {
        Client client = new Client(1L, "Rampage Jackson");
        Services service1 = new Services(1L, "Corte de barba", 25.99, 30);
        Services service2 = new Services(2L, "Corte de cabelo", 55.99, 30);
        List<Services> services = List.of(service1, service2);

        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        Appointment firstAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        Appointment secondAppointment = new Appointment(
                1L,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findByDateAndStartTimeGreaterThanEqualOrderByDateAscStartTimeAsc(date, startTime)).thenReturn(List.of(firstAppointment, secondAppointment));

        List<AppointmentResponseDTO> appointmentsResponse = appointmentService.getByDateAndStartTimeGreaterThan(date, startTime);

        assertNotNull(appointmentsResponse);
        assertEquals(2, appointmentsResponse.size());

        //FIRST APPOINTMENT
        AppointmentResponseDTO response = appointmentsResponse.getFirst();
        assertEquals(firstAppointment.getId(), response.id());
        assertEquals(firstAppointment.getDate(), response.date());
        assertEquals(firstAppointment.getStartTime(), response.startTime());
        assertEquals(firstAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(firstAppointment.getClient().getId(), response.client().id());
        assertEquals(firstAppointment.getClient().getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        //first service
        ServiceResponseDTO responseService = response.services().getFirst();
        Services service = firstAppointment.getServices().getFirst();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //second service
        responseService = response.services().get(1);
        service = firstAppointment.getServices().getLast();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //Second appointment
        response = appointmentsResponse.getLast();
        assertEquals(secondAppointment.getId(), response.id());
        assertEquals(secondAppointment.getDate(), response.date());
        assertEquals(secondAppointment.getStartTime(), response.startTime());
        assertEquals(secondAppointment.getEndTime(), response.endTime());

        assertNotNull(response.client());
        assertEquals(secondAppointment.getClient().getId(), response.client().id());
        assertEquals(secondAppointment.getClient().getName(), response.client().name());

        assertNotNull(response.services());
        assertEquals(2, response.services().size());

        //first service
        responseService = response.services().getFirst();
        service = secondAppointment.getServices().getFirst();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        //second service
        responseService = response.services().get(1);
        service = secondAppointment.getServices().getLast();
        assertEquals(service.getId(), responseService.id());
        assertEquals(service.getName(), responseService.name());
        assertEquals(service.getValue(), responseService.value());
        assertEquals(service.getDuration(), responseService.duration());

        verify(appointmentRepository).findByDateAndStartTimeGreaterThanEqualOrderByDateAscStartTimeAsc(date, startTime);
    }

    @Test
    void updateShouldUpdateAppointment() {
        Long appointmentToUpdateId = 1L;

        Client client = new Client(1L, "Rampage Jackson");
        Services service1 = new Services(1L, "Corte de barba", 25.99, 30);
        Services service2 = new Services(2L, "Corte de cabelo", 55.99, 30);
        List<Services> services = new ArrayList<>();
        services.add(service1);
        services.add(service2);

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

        Appointment updatedAppointment = new Appointment(
                appointmentToUpdateId,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findById(appointmentToUpdateId)).thenReturn(Optional.of(updatedAppointment));
        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(List.of());
        when(clientRepository.findById(appointmentRequest.clientId())).thenReturn(Optional.of(client));
        when(serviceRepository.findById(service1.getId())).thenReturn(Optional.of(service1));
        when(serviceRepository.findById(service2.getId())).thenReturn(Optional.of(service2));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);

        AppointmentResponseDTO response = appointmentService.update(appointmentToUpdateId, appointmentRequest);
        assertNotNull(response);
        assertEquals(updatedAppointment.getId(), response.id());
        assertEquals(updatedAppointment.getDate(), response.date());
        assertEquals(updatedAppointment.getStartTime(), response.startTime());
        assertEquals(updatedAppointment.getEndTime(), response.endTime());

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
    void updateShouldThrowBusinessExceptionWhenStartTimeIsAfterEndTime() {
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
            appointmentService.update(1L, appointmentRequest);
        });

        verify(appointmentRepository, never()).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository, never()).findById(appointmentRequest.clientId());
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));

        String expectedMessage = "Start time cannot be after end time";
        assertEquals(expectedMessage, businessException.getMessage());

    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenAppointmentDoesNotExists() {
        Long appointmentToUpdateId = 1L;
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

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

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            appointmentService.update(appointmentToUpdateId, appointmentRequest);
        });

        verify(appointmentRepository).findById(appointmentToUpdateId);
        verify(appointmentRepository, never()).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository, never()).findById(any(Long.class));
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));

        String expectedMessage = "Appointment not found";
        assertEquals(expectedMessage, notFoundException.getMessage());
    }

    @Test
    void updateShouldThrownConflictExceptionWhenThereIsAConflict() {
        Long appointmentToUpdateId = 1L;
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

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

        Appointment updatedAppointment = new Appointment(
                appointmentToUpdateId,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findById(appointmentToUpdateId)).thenReturn(Optional.of(updatedAppointment));
        List<Appointment> conflictingAppointments = List.of(
                new Appointment(2L, client, services, date, startTime, endTime)
        );

        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(conflictingAppointments);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            appointmentService.update(appointmentToUpdateId, appointmentRequest);
        });

        String expectedMessage = "Conflicting appointments in date 2025-11-20 between 17:00 and 17:30 are found";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(clientRepository, never()).findById(any(Long.class));
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateShouldThrownNotFoundExceptionWhenClientDoesNotExist() {
        Long appointmentToUpdateId = 1L;
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

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

        Appointment updatedAppointment = new Appointment(
                appointmentToUpdateId,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findById(appointmentToUpdateId)).thenReturn(Optional.of(updatedAppointment));
        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(List.of());
        when(clientRepository.findById(client.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            appointmentService.update(appointmentToUpdateId, appointmentRequest);
        });

        String expectedMessage = "Client not found";
        assertEquals(expectedMessage, notFoundException.getMessage());

        verify(appointmentRepository).findById(appointmentToUpdateId);
        verify(appointmentRepository).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository).findById(client.getId());
        verify(serviceRepository, never()).findById(any(Long.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateShouldThrownNotFoundExceptionWhenServiceDoesNotExist() {
        Long appointmentToUpdateId = 1L;
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

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

        Appointment updatedAppointment = new Appointment(
                appointmentToUpdateId,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findById(appointmentToUpdateId)).thenReturn(Optional.of(updatedAppointment));
        when(appointmentRepository.findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime()))
                .thenReturn(List.of());
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(serviceRepository.findById(services.get(0).getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            appointmentService.update(appointmentToUpdateId, appointmentRequest);
        });

        String expectedMessage = "Service 1 not found";
        assertEquals(expectedMessage, notFoundException.getMessage());

        verify(appointmentRepository).findById(appointmentToUpdateId);
        verify(appointmentRepository).findConflictingAppointments(appointmentRequest.date(), appointmentRequest.startTime(), appointmentRequest.endTime());
        verify(clientRepository).findById(client.getId());
        verify(serviceRepository).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void deleteShouldDeleteAppointment() {
        Long appointmentToDeleteId = 1L;
        Client client = new Client(1L, "Rampage Jackson");
        List<Services> services = new ArrayList<>();
        services.add(new Services(1L, "Corte de barba", 25.99, 30));
        services.add(new Services(2L, "Corte de cabelo", 55.99, 30));

        LocalDate date = LocalDate.of(2025, 11, 20);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(17, 30);

        Appointment appointmentToDelete = new Appointment(
                appointmentToDeleteId,
                client,
                services,
                date,
                startTime,
                endTime
        );

        when(appointmentRepository.findById(appointmentToDeleteId)).thenReturn(Optional.of(appointmentToDelete));

        appointmentService.delete(appointmentToDeleteId);

        verify(appointmentRepository).findById(appointmentToDeleteId);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionWhenAppointmentDoesNotExist() {
        Long appointmentToDeleteId = 1L;

        when(appointmentRepository.findById(appointmentToDeleteId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            appointmentService.delete(appointmentToDeleteId);
        });

        verify(appointmentRepository).findById(appointmentToDeleteId);
        verify(appointmentRepository, never()).delete(any(Appointment.class));
    }
}

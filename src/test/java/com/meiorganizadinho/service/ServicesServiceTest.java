package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.servicedto.ServicePostPutRequestDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.ServicesMessages;
import com.meiorganizadinho.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicesServiceTest {
    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServicesService servicesService;

    @Test
    void createShouldCreateService() {
        ServicePostPutRequestDTO serviceRequestDTO = new ServicePostPutRequestDTO("Barba", 30.85, 45);

        when(serviceRepository.existsByNameIgnoreCase(serviceRequestDTO.name())).thenReturn(false);
        when(serviceRepository.save(any(Services.class))).thenReturn(new Services(1L, serviceRequestDTO.name(), serviceRequestDTO.value(), serviceRequestDTO.duration()));

        ServiceResponseDTO createdService = servicesService.create(serviceRequestDTO);

        verify(serviceRepository).existsByNameIgnoreCase(serviceRequestDTO.name());
        verify(serviceRepository).save(any(Services.class));

        assertEquals(1L, createdService.id());
        assertEquals(serviceRequestDTO.name(), createdService.name());
        assertEquals(serviceRequestDTO.value(), createdService.value());
        assertEquals(serviceRequestDTO.duration(), createdService.duration());
    }

    @Test
    void createShouldThrowConflictExceptionWhenServiceAlreadyExists() {
        ServicePostPutRequestDTO serviceRequestDTO = new ServicePostPutRequestDTO("Barba", 30.85, 45);

        when(serviceRepository.existsByNameIgnoreCase(serviceRequestDTO.name())).thenReturn(true);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            servicesService.create(serviceRequestDTO);
        });

        String expectedMessage = "Service with name " + serviceRequestDTO.name() + " already exists";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(serviceRepository).existsByNameIgnoreCase(serviceRequestDTO.name());
        verify(serviceRepository, never()).save(any(Services.class));
    }

    @Test
    void getShouldReturnEmptyListWhenNoServicesExist() {
        when(serviceRepository.findAllByOrderByNameAsc()).thenReturn(emptyList());

        List<ServiceResponseDTO> services = servicesService.getAll();

        verify(serviceRepository).findAllByOrderByNameAsc();
        assertEquals(0, services.size());
    }

    @Test
    void getShouldReturnServicesListOrderedByName() {
        Services service1 = new Services(1L, "Corte de Cabelo", 50.0, 30);
        Services service2 = new Services(2L, "Barba", 30.0, 20);

        when(serviceRepository.findAllByOrderByNameAsc()).thenReturn(List.of(service1, service2));

        List<ServiceResponseDTO> services = servicesService.getAll();

        verify(serviceRepository).findAllByOrderByNameAsc();
        assertEquals(2, services.size());

        ServiceResponseDTO responseDTO = services.getFirst();
        assertEquals(service1.getId(), responseDTO.id());
        assertEquals(service1.getName(), responseDTO.name());
        assertEquals(service1.getValue(), responseDTO.value());
        assertEquals(service1.getDuration(), responseDTO.duration());

        responseDTO = services.getLast();
        assertEquals(service2.getId(), responseDTO.id());
        assertEquals(service2.getName(), responseDTO.name());
        assertEquals(service2.getValue(), responseDTO.value());
        assertEquals(service2.getDuration(), responseDTO.duration());
    }

    @Test
    void getByNameShouldReturnEmptyListWhenNoServicesExist() {
        when(serviceRepository.findByNameContainingIgnoreCaseOrderByNameAsc("Empty")).thenReturn(emptyList());

        List<ServiceResponseDTO> services = servicesService.getByName("Empty");

        verify(serviceRepository).findByNameContainingIgnoreCaseOrderByNameAsc("Empty");
        assertEquals(0, services.size());
    }

    @Test
    void getByNameShouldReturnServicesListOrderedByName() {
        Services service1 = new Services(1L, "Corte de Cabelo", 50.0, 30);
        Services service2 = new Services(2L, "Corte de Barba", 30.0, 20);

        String filter = "corte";
        when(serviceRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filter)).thenReturn(List.of(service1, service2));

        List<ServiceResponseDTO> services = servicesService.getByName(filter);

        verify(serviceRepository).findByNameContainingIgnoreCaseOrderByNameAsc(filter);
        assertEquals(2, services.size());

        ServiceResponseDTO responseDTO = services.getFirst();
        assertEquals(service1.getId(), responseDTO.id());
        assertEquals(service1.getName(), responseDTO.name());
        assertEquals(service1.getValue(), responseDTO.value());
        assertEquals(service1.getDuration(), responseDTO.duration());

        responseDTO = services.getLast();
        assertEquals(service2.getId(), responseDTO.id());
        assertEquals(service2.getName(), responseDTO.name());
        assertEquals(service2.getValue(), responseDTO.value());
        assertEquals(service2.getDuration(), responseDTO.duration());
    }

    @Test
    void updateShouldUpdateService() {
        Services serviceToUpdate = new Services(1L, "Corte de Cabelo", 50.0, 30);
        when(serviceRepository.findById(serviceToUpdate.getId())).thenReturn(Optional.of(serviceToUpdate));

        when(serviceRepository.existsByNameIgnoreCase("Corte de barba")).thenReturn(false);

        when(serviceRepository.save(any(Services.class))).thenReturn(new Services(1L, "Corte de barba", 40.0, 25));

        ServicePostPutRequestDTO serviceRequestDTO = new ServicePostPutRequestDTO("Corte de barba", 40.0, 25);
        ServiceResponseDTO updatedService = servicesService.update(serviceToUpdate.getId(), serviceRequestDTO);

        verify(serviceRepository).findById(serviceToUpdate.getId());
        verify(serviceRepository).existsByNameIgnoreCase("Corte de barba");
        verify(serviceRepository).save(any(Services.class));

        assertEquals(1L, updatedService.id());
        assertEquals(serviceRequestDTO.name(), updatedService.name());
        assertEquals(serviceRequestDTO.value(), updatedService.value());
        assertEquals(serviceRequestDTO.duration(), updatedService.duration());
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenServiceNotFound() {
        Long serviceId = 1L;
        when(serviceRepository.findById(serviceId)).thenThrow(new NotFoundException(ServicesMessages.getServiceNotFoundMessage(serviceId)));

        ServicePostPutRequestDTO serviceRequestDTO = new ServicePostPutRequestDTO("Corte de barba", 40.0, 25);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            servicesService.update(serviceId, serviceRequestDTO);
        });

        String expectedMessage = "Service " + serviceId + " not found";
        assertEquals(expectedMessage, notFoundException.getMessage());

        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository, never()).existsByNameIgnoreCase(anyString());
        verify(serviceRepository, never()).save(any(Services.class));
    }

    @Test
    void updateShouldThrowConflictExceptionWhenServiceNameToUpdateIsEqualActualServiceNameStoredInDataBase() {
        Long serviceId = 2L;

        Services existingService = new Services(2L, "Corte de Cabelo", 50.0, 30);
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));

        ServicePostPutRequestDTO serviceRequestDTO = new ServicePostPutRequestDTO("Corte de Cabelo", 30.0, 20);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            servicesService.update(serviceId, serviceRequestDTO);
        });

        String expectedMessage = "Service with name " + serviceRequestDTO.name() + " already exists";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository, never()).existsByNameIgnoreCase("Barba");
        verify(serviceRepository, never()).save(any(Services.class));
    }

    @Test
    void updateShouldThrowConflictExceptionWhenServiceNameToUpdateIsEqualAnotherServiceNameStoredInDataBase() {
        Long serviceId = 2L;

        Services existingService = new Services(2L, "Corte de Cabelo", 50.0, 30);
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));

        ServicePostPutRequestDTO serviceRequestDTO = new ServicePostPutRequestDTO("Corte de barba", 30.0, 20);
        when(serviceRepository.existsByNameIgnoreCase(serviceRequestDTO.name())).thenReturn(true);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            servicesService.update(serviceId, serviceRequestDTO);
        });

        String expectedMessage = "Service with name " + serviceRequestDTO.name() + " already exists";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository).existsByNameIgnoreCase("Corte de barba");
        verify(serviceRepository, never()).save(any(Services.class));
    }

    @Test
    void deleteShouldDeleteService() {
        Long serviceId = 1L;

        Services serviceToDelete = new Services(serviceId, "Corte de Cabelo", 50.0, 30);
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceToDelete));

        servicesService.delete(serviceId);

        verify(serviceRepository).delete(serviceToDelete);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionWhenServiceNotFound() {
        Long serviceId = 1L;

        when(serviceRepository.findById(serviceId)).thenThrow(new NotFoundException(ServicesMessages.getServiceNotFoundMessage(serviceId)));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            servicesService.delete(serviceId);
        });

        String expectedMessage = "Service " + serviceId + " not found";
        assertEquals(expectedMessage, notFoundException.getMessage());

        verify(serviceRepository, never()).delete(any(Services.class));
    }

    @Test
    void deleteShouldThrowBusinessExceptionWhenServiceHasAppointments() {
        Long serviceId = 1L;
        Services serviceToDelete = new Services(serviceId, "Corte de Cabelo", 50.0, 30);

        serviceToDelete.getAppointments().add(new Appointment());

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceToDelete));

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            servicesService.delete(serviceId);
        });

        String expectedMessage = "Service has link with 1 appointment(s)";
        assertEquals(expectedMessage, businessException.getMessage());

        verify(serviceRepository, never()).delete(any(Services.class));
    }
}
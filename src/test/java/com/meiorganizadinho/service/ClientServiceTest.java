package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.clientdto.ClientPostPutRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.ClientMessages;
import com.meiorganizadinho.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void createShouldCreateClient() {
        Long clientId = 1L;
        String clientName = "John Doe";

        when(clientRepository.existsByNameIgnoreCase(clientName)).thenReturn(false);

        Client createdClient = new Client(clientId, clientName);
        when(clientRepository.save(any(Client.class))).thenReturn(createdClient);

        ClientPostPutRequestDTO clientRequest = new ClientPostPutRequestDTO(clientName);
        ClientResponseDTO clientResponseDTO = clientService.create(clientRequest);

        verify(clientRepository).existsByNameIgnoreCase(clientName);
        verify(clientRepository).save(any(Client.class));

        assertEquals(clientId, clientResponseDTO.id());
        assertEquals(clientName, clientResponseDTO.name());
    }

    @Test
    void createShouldThrowExceptionWhenClientAlreadyExists() {
        String clientName = "John Doe";

        when(clientRepository.existsByNameIgnoreCase(clientName)).thenReturn(true);

        ClientPostPutRequestDTO clientRequest = new ClientPostPutRequestDTO(clientName);
        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            clientService.create(clientRequest);
        });

        String expectedMessage = "Client with name " + clientName + " already exists";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(clientRepository).existsByNameIgnoreCase(clientName);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void getShouldReturnEmptyListWhenNoClientsExist() {
        when(clientRepository.findAllByOrderByNameAsc()).thenReturn(emptyList());

        List<ClientResponseDTO> clients = clientService.getAll();

        verify(clientRepository).findAllByOrderByNameAsc();
        assertEquals(0, clients.size());
    }

    @Test
    void getShouldReturnListOfClientsOrderedByNameAsc() {

        List<Client> clientsReturn = List.of(
                new Client(2L, "Alice"),
                new Client(1L, "Bob"),
                new Client(3L, "Charlie")
        );

        when(clientRepository.findAllByOrderByNameAsc()).thenReturn(clientsReturn);

        List<ClientResponseDTO> clients = clientService.getAll();

        verify(clientRepository).findAllByOrderByNameAsc();
        assertEquals(3, clients.size());
    }

    @Test
    void getByNameShouldReturnEmptyListWhenNoClientsExist() {
        String name = "";
        when(clientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name)).thenReturn(emptyList());

        List<ClientResponseDTO> clients = clientService.getByName(name);

        verify(clientRepository).findByNameContainingIgnoreCaseOrderByNameAsc(name);
        assertEquals(0, clients.size());
    }

    @Test
    void getByNameShouldReturnListOfClientsOrderedByNameAsc() {
        String filterName = "Ali";

        List<Client> clientsReturn = List.of(
                new Client(2L, "Alice"),
                new Client(4L, "Alicia")
        );

        when(clientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filterName)).thenReturn(clientsReturn);

        List<ClientResponseDTO> clients = clientService.getByName(filterName);

        verify(clientRepository).findByNameContainingIgnoreCaseOrderByNameAsc(filterName);
        assertEquals(2, clients.size());
    }

    @Test
    void updateShouldUpdateClient() {
        Long clientId = 1L;
        String clientUpdateNewName = "John Pebes";

        Client clientToUpdate = new Client(clientId, "John Doe");
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientToUpdate));

        when(clientRepository.existsByNameIgnoreCase(clientUpdateNewName)).thenReturn(false);

        when(clientRepository.save(any(Client.class))).thenReturn(new Client(clientId, clientUpdateNewName));

        ClientResponseDTO updatedClient = clientService.update(clientId, new ClientPostPutRequestDTO(clientUpdateNewName));

        verify(clientRepository).findById(clientId);
        verify(clientRepository).existsByNameIgnoreCase(clientUpdateNewName);
        verify(clientRepository).save(any(Client.class));

        assertEquals(clientId, updatedClient.id());
        assertEquals(clientUpdateNewName, updatedClient.name());
    }

    @Test
    void updateShouldThrowNotFoundExceptionExceptionWhenClientNotFound() {
        Long clientId = 1L;
        String clientToUpdateNewName = "John Pebes";

        when(clientRepository.findById(clientId)).thenThrow(new NotFoundException(ClientMessages.CLIENT_NOT_FOUND));

        NotFoundException conflictException = assertThrows(NotFoundException.class, () -> {
            clientService.update(clientId, new ClientPostPutRequestDTO(clientToUpdateNewName));
        });

        String expectedMessage = "Client not found";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(clientRepository, never()).existsByNameIgnoreCase(clientToUpdateNewName);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void updateShouldThrowConflictExceptionWhenClientNameToUpdateIsEqualActualClientNameStoredInDataBase() {
        Long clientId = 2L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client( clientId, "John Pebes")));

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            clientService.update(clientId, new ClientPostPutRequestDTO("John Pebes"));
        });

        String expectedMessage = "Client with name John Pebes already exists";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void updateShouldThrowConflictExceptionWhenClientNameToUpdateIsEqualAnotherClientNameStoredInDataBase() {
        Long clientId = 2L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client( clientId, "John Pebes")));
        when(clientRepository.existsByNameIgnoreCase("John Pibers")).thenReturn(true);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            clientService.update(clientId, new ClientPostPutRequestDTO("John Pibers"));
        });

        String expectedMessage = "Client with name John Pibers already exists";
        assertEquals(expectedMessage, conflictException.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void deleteShouldDeleteClient() {
        Long clientId = 1L;

        Client clientToDelete = new Client(clientId, "John Doe");
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientToDelete));

        clientService.delete(clientId);

        verify(clientRepository).delete(clientToDelete);
    }

    @Test
    void deleteShloudThrowNotFoundExceptionWhenClientNotFound() {
        Long clientId = 1L;

        when(clientRepository.findById(clientId)).thenThrow(new NotFoundException(ClientMessages.CLIENT_NOT_FOUND));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            clientService.delete(clientId);
        });

        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void deleteShouldThrowBusinessExceptionWhenClientHasAppointments() {
        Long clientId = 1L;
        Client clientToDelete = new Client(clientId, "clientName");

        Services service1 = new Services("Corte de Cabelo", 50.0, 30);
        Services service2 = new Services("Barba", 30.0, 20);
        List<Services> services = List.of(service1, service2);

        Appointment appointment = new Appointment(
                1L,
                clientToDelete,
                services,
                LocalDate.now(),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30)
        );

        clientToDelete.getAppointments().add(appointment);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientToDelete));

        BusinessException notFoundException = assertThrows(BusinessException.class, () -> {
            clientService.delete(clientId);
        });

        verify(clientRepository, never()).delete(any(Client.class));
    }
}

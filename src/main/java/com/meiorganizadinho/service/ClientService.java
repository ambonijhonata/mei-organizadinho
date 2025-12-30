package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.clientdto.ClientPostPutRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.ClientMessages;
import com.meiorganizadinho.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponseDTO create(ClientPostPutRequestDTO clientPostPutRequestDTO) {
        String clientName = clientPostPutRequestDTO.name();

        boolean isAlreadyClientExists = clientRepository.existsByNameIgnoreCase(clientName);
        if (isAlreadyClientExists) {
            throw new ConflictException(ClientMessages.getClientWithNameAlreadyExistsMessage(clientName));
        }

        Client client = new Client(clientPostPutRequestDTO.name());
        client = clientRepository.save(client);
        return new ClientResponseDTO(client.getId(), client.getName());
    }

    public List<ClientResponseDTO> getAll() {
        List<Client> clients = clientRepository.findAllByOrderByNameAsc();
        List<ClientResponseDTO> clientResponseDTO = new ArrayList<>();

        for(Client clientResponse : clients) {
            clientResponseDTO.add(new ClientResponseDTO(clientResponse.getId(), clientResponse.getName()));
        }

        return clientResponseDTO;
    }

    public List<ClientResponseDTO> getByName(String name) {
        List<Client> clients = clientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
        List<ClientResponseDTO> clientResponseDTO = new ArrayList<>();

        for(Client clientResponse : clients) {
            clientResponseDTO.add(new ClientResponseDTO(clientResponse.getId(), clientResponse.getName()));
        }

        return clientResponseDTO;
    }

    public ClientResponseDTO update(Long id, ClientPostPutRequestDTO clientRequest) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(((ClientMessages.CLIENT_NOT_FOUND))));

        if(clientRequest.name().equalsIgnoreCase(client.getName()) || clientRepository.existsByNameIgnoreCase(clientRequest.name())) {
            throw new ConflictException(ClientMessages.getClientWithNameAlreadyExistsMessage(clientRequest.name()));
        }

        client.setName(clientRequest.name());

        client = clientRepository.save(client);

        return new ClientResponseDTO(client.getId(), client.getName());
    }

    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(((ClientMessages.CLIENT_NOT_FOUND))));

        int qtdAppointments = client.getAppointments().size();
        if(qtdAppointments > 0) {
            throw new BusinessException(ClientMessages.getClientHasLinkWithNAppointmentsMessage(qtdAppointments));
        }
        clientRepository.delete(client);
    }
}

package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.clientdto.ClientPostRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponseDTO create(ClientPostRequestDTO clientPostRequestDTO) {
        Client client = new Client(clientPostRequestDTO.name());

        client =  clientRepository.save(client);

        return new ClientResponseDTO(client.getId(), client.getName());
    }
}

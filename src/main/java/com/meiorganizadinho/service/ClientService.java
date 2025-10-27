package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.clientdto.ClientPostPutRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ClientNotFoundException;
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

        boolean isAlreadyClientExists = clientRepository.existsByName(clientName);
        if (isAlreadyClientExists) {
            throw new BusinessException("Já existe um cliente com o nome: " + clientName);
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

    public ClientResponseDTO update(int id, ClientPostPutRequestDTO clientRequest) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException((("Cliente não encontrado"))));

        client.setName(clientRequest.name());

        client = clientRepository.save(client);

        return new ClientResponseDTO(client.getId(), client.getName());
    }

    public void delete(int id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException((("Cliente não encontrado"))));

        clientRepository.delete(client);
    }
}

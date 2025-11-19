package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.clientdto.ClientPostPutRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.NotFoundException;
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
            throw new BusinessException("Client already exists: " + clientName);
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
                .orElseThrow(() -> new NotFoundException((("Cliente não encontrado"))));

        client.setName(clientRequest.name());

        client = clientRepository.save(client);

        return new ClientResponseDTO(client.getId(), client.getName());
    }

    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException((("Cliente não encontrado"))));

        int qtdAppointments = client.getAppointments().size();
        if(qtdAppointments > 0) {
            throw new BusinessException("Cliente possui vínculos com " + qtdAppointments + " appointment(s)");
        }
        clientRepository.delete(client);
    }
}

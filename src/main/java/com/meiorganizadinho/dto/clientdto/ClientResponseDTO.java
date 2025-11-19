package com.meiorganizadinho.dto.clientdto;

import com.meiorganizadinho.entity.Client;

public record ClientResponseDTO(
        Long id,
        String name
) {
    public static ClientResponseDTO fromEntity(Client client) {
        return new ClientResponseDTO(client.getId(), client.getName());
    }
}

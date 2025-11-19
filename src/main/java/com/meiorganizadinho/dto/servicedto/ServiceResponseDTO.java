package com.meiorganizadinho.dto.servicedto;

import com.meiorganizadinho.entity.Services;

public record ServiceResponseDTO(
        Long id,
        String name,
        double value,
        int duration
) {
    public static ServiceResponseDTO fromEntity(Services service) {
        return new ServiceResponseDTO(service.getId(), service.getName(), service.getValue(), service.getDuration());
    }
}

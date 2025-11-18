package com.meiorganizadinho.dto.servicedto;

public record ServiceResponseDTO(
        Long id,
        String name,
        double value,
        int duration
) {
}

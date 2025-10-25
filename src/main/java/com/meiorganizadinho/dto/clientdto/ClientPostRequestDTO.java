package com.meiorganizadinho.dto.clientdto;

import jakarta.validation.constraints.NotBlank;

public record ClientPostRequestDTO(
        @NotBlank(message = "Name is required")
        String name) {
}

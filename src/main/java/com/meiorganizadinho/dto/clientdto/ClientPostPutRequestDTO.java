    package com.meiorganizadinho.dto.clientdto;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.validation.constraints.NotBlank;

    public record ClientPostPutRequestDTO(
            @NotBlank(message = "Name is required")
            @JsonProperty(required = true)
            String name) {
    }

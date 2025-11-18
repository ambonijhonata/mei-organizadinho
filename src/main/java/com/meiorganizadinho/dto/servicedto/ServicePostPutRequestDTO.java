package com.meiorganizadinho.dto.servicedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServicePostPutRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Value is required")
        @Positive(message = "Value must be positive")
        Double value,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1")
        @JsonProperty(required = true)
        int duration
) {
}

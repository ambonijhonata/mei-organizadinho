package com.meiorganizadinho.dto.appointmentdto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AppointmentPostPutRequestDTO(
        @NotNull(message = "clientId is required")
        Long clientId,
        @NotNull(message = "servicesId is required")
        @Size(min = 1, message = "At least one service must be specified")
        List<Long> servicesId,
        @NotNull(message = "date is required")
        LocalDate date,
        @NotNull(message = "startTime is required")
        LocalTime startTime,
        @NotNull(message = "endTime is required")
        LocalTime endTime
) {
}

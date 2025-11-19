package com.meiorganizadinho.dto.reportdto;

import com.meiorganizadinho.exception.BusinessException;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReportRequestDTO(
        @NotNull(message = "startDate is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @NotNull(message = "endDate is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {
    public ReportRequestDTO {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("endDate cannot be before startDate");
        }
    }
}
